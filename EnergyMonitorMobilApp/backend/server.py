from flask import Flask, jsonify
import time
from datetime import datetime
import joblib
import os
import pandas as pd
import numpy as np

app = Flask(__name__)

# Global model variable
model = None

def load_energy_model():
    """
    Eğitilmiş Gradient Boosting modelini yükle
    Model path: ../model/best_model_gradient_boosting.pkl
    """
    global model
    try:
        # backend klasöründen bir üst klasöre (..) çıkıp model klasörüne gidiyoruz
        model_path = os.path.join(os.path.dirname(__file__), '..', 'model', 'best_model_gradient_boosting.pkl')
        model = joblib.load(model_path)
        print("✅ Model başarıyla yüklendi")
        return True
    except Exception as e:
        print(f"❌ Model yükleme hatası: {e}")
        return False

def prepare_features(timestamp, reactive_power=None, power_factor=None, load_type="Medium_Load"):
    """
    Model için feature vector hazırla
    Returns: pandas DataFrame (1 row, 17 columns)
    """
    # Zaman özellikleri
    year = timestamp.year
    month = timestamp.month
    day = timestamp.day
    hour = timestamp.hour
    day_of_week = timestamp.strftime('%A')  # Monday, Tuesday, etc.
    is_weekend = 1 if day_of_week in ['Saturday', 'Sunday'] else 0
    
    # Sensör değerleri (gerçek sistemde gerçek sensörden gelir)
    if reactive_power is None:
        # Saate göre gerçekçi değer
        reactive_power = 10 + (hour * 1.5) if 8 <= hour <= 18 else 5.0
    
    if power_factor is None:
        power_factor = 85.0 if 8 <= hour <= 18 else 90.0
    
    leading_reactive = 0.2  # Genelde düşük
    
    # Day of week one-hot encoding
    days = ['Monday', 'Saturday', 'Sunday', 'Thursday', 'Tuesday', 'Wednesday']
    day_encoding = {d: 1 if day_of_week == d else 0 for d in days}
    
    # Load type one-hot encoding
    load_types = ['Light_Load', 'Maximum_Load', 'Medium_Load']
    load_encoding = {lt: 1 if load_type == lt else 0 for lt in load_types}
    
    # Feature listesi (Modelin beklediği TAM SIRA)
    # 0: Lagging_Current_Reactive.Power_kVarh
    # 1: Leading_Current_Reactive_Power_kVarh
    # 2: Lagging_Current_Power_Factor
    # ...
    features = {
        'Lagging_Current_Reactive.Power_kVarh': reactive_power,
        'Leading_Current_Reactive_Power_kVarh': leading_reactive,
        'Lagging_Current_Power_Factor': power_factor,
        'year': year,
        'month': month,
        'day': day,
        'hour': hour,
        'WeekStatus_Weekend': is_weekend,
        'Day_of_week_Monday': day_encoding.get('Monday', 0),
        'Day_of_week_Saturday': day_encoding.get('Saturday', 0),
        'Day_of_week_Sunday': day_encoding.get('Sunday', 0),
        'Day_of_week_Thursday': day_encoding.get('Thursday', 0),
        'Day_of_week_Tuesday': day_encoding.get('Tuesday', 0),
        'Day_of_week_Wednesday': day_encoding.get('Wednesday', 0),
        'Load_Type_Maximum_Load': load_encoding.get('Maximum_Load', 0),
        'Load_Type_Medium_Load': load_encoding.get('Medium_Load', 0)
    }
    
    # DataFrame oluştur (Sözlük sırasına güvenmek yerine sütunları garantiye alalım)
    df = pd.DataFrame([features])
    
    # Sütun sırasını garanti et
    expected_order = [
        'Lagging_Current_Reactive.Power_kVarh',
        'Leading_Current_Reactive_Power_kVarh',
        'Lagging_Current_Power_Factor',
        'year',
        'month',
        'day',
        'hour',
        'WeekStatus_Weekend',
        'Day_of_week_Monday',
        'Day_of_week_Saturday',
        'Day_of_week_Sunday',
        'Day_of_week_Thursday',
        'Day_of_week_Tuesday',
        'Day_of_week_Wednesday',
        'Load_Type_Maximum_Load',
        'Load_Type_Medium_Load'
    ]
    df = df[expected_order]
    
    return df

def predict_consumption(timestamp, **kwargs):
    """
    Belirli bir zaman için enerji tüketimi tahmini
    Returns: float (Predicted kWh)
    """
    if model is None:
        raise Exception("Model yüklenmemiş!")
    
    # Feature vector hazırla
    features_df = prepare_features(timestamp, **kwargs)
    
    # Tahmin yap
    prediction = model.predict(features_df)
    return float(prediction[0])

# Uygulama başlarken modeli yükle
load_energy_model()

@app.route('/api/predict-test', methods=['POST', 'GET'])
def predict_test():
    try:
        # Şimdiki zaman için tahmin yap
        now = datetime.now()
        
        # Gelecek için tahmin testi (örn: yarın bu saat)
        # tomorrow = now + timedelta(days=1)
        
        prediction = predict_consumption(now)
        
        return jsonify({
            "timestamp": now.strftime("%Y-%m-%d %H:%M:%S"),
            "predicted_usage_kwh": round(prediction, 2),
            "status": "success",
            "model_used": "Gradient Boosting"
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/feature-test', methods=['GET'])
def feature_test():
    try:
        # Test için şu anki zamanı kullan
        now = datetime.now()
        df = prepare_features(now)
        
        return jsonify({
            "status": "success",
            "columns": list(df.columns),
            "column_count": len(df.columns),
            "sample_values": df.iloc[0].to_dict()
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/model-test', methods=['GET'])
def model_test():
    if model:
        return jsonify({
            "model_loaded": True,
            "model_type": "Pipeline" if hasattr(model, 'predict') else str(type(model)),
            "features_count": 17 # Beklenen özellik sayısı
        })
    else:
        return jsonify({
            "model_loaded": False,
            "error": "Model yüklenemedi"
        }), 500

# --- Existing Mock Endpoints (Aşamalar ilerledikçe güncellenecek) ---

def get_timestamp():
    return datetime.now().strftime("%H:%M:%S")

# --- Simulation Logic (Manual Control) ---

from flask import request

# Global Simulation State (Varsayılan: Normal)
simulation_state = {
    "reactive_power": 5.0,
    "power_factor": 98.0,
    "load_type": "Medium_Load"
}

@app.route('/api/update-simulation', methods=['POST'])
def update_simulation():
    global simulation_state
    try:
        data = request.json
        
        # Gelen verilerle state'i güncelle (Sadece gönderilenleri değiştir)
        if 'reactive_power' in data:
            simulation_state['reactive_power'] = float(data['reactive_power'])
            
        if 'power_factor' in data:
            simulation_state['power_factor'] = float(data['power_factor'])
            
        if 'load_type' in data:
            simulation_state['load_type'] = data['load_type']
            
        print(f"🔄 Simülasyon Güncellendi: {simulation_state}")
        
        return jsonify({
            "status": "success",
            "current_state": simulation_state
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 400

def get_current_simulation_data():
    """Anlık simülasyon durumunu döner"""
    return simulation_state

@app.route('/api/current-status', methods=['GET'])
def get_current_status():
    try:
        now = datetime.now()
        
        # 1. Simülasyon Verilerini Al (Artık global state'den)
        sim_data = get_current_simulation_data()
        
        # 2. Tahmin Yap (Simüle edilen verilerle)
        current_prediction = predict_consumption(
            now, 
            reactive_power=sim_data["reactive_power"],
            power_factor=sim_data["power_factor"],
            load_type=sim_data["load_type"]
        )
        
        # 3. Beklenen Değer (Dinamil Limitler)
        # Kullanıcı isteği: 75 kWh'e kadar SAFE olsun.
        # Maximum Load için limiti 75.0 yapıyoruz.
        
        base_expected = 25.0
        if sim_data["load_type"] == "Maximum_Load":
            base_expected = 75.0 # 75'e kadar safe
        elif sim_data["load_type"] == "Light_Load":
            base_expected = 12.0
            
        expected_kwh = base_expected
        
        # 4. Durum Belirleme (Kullanıcı Talebi: Aralık Mantığı)
        # 0 - 75 kWh : Safe (Normal)
        # 75 - 125 kWh : Warning
        # 125+ kWh : Critical
        
        status = "Normal"
        
        if current_prediction >= 125.0:
            status = "Critical"
        elif current_prediction >= 75.0:
            status = "Warning"
            
        # Güç Faktörü Kontrolü (Ekstra Güvenlik)
        # Eğer tüketim normalse ama PF çok düşükse Warning verelim
        if status == "Normal" and sim_data["power_factor"] < 90.0:
            status = "Warning"
            
        return jsonify({
            "current_kwh": round(current_prediction, 2),
            "predicted_kwh": round(expected_kwh, 2),
            "difference": round(current_prediction - expected_kwh, 2),
            "status": status,
            "timestamp": get_timestamp(),
            "debug_info": sim_data 
        })
    except Exception as e:
        print(f"Hata: {e}")
        return jsonify({"current_kwh": 0.0, "status": "Error", "timestamp": get_timestamp()})

@app.route('/api/alerts', methods=['GET'])
def get_alerts():
    # Uyarıları da simülasyon state'ine göre üret
    sim_data = get_current_simulation_data()
    
    alerts = []
    
    # Kural 1: Düşük Güç Faktörü (Verimlilik)
    # Kural 1: Düşük Güç Faktörü (Verimlilik)
    if sim_data["power_factor"] < 90.0:
        alerts.append({
            "priority": "Medium",
            "title": "Düşük Verimlilik",
            "message": f"Güç faktörü kritik seviyede: %{sim_data['power_factor']}",
            "recommendation": "Kompanzasyon panosunu kontrol ediniz."
        })
        
    # Kural 2: Yüksek Reaktif Güç
    if sim_data["reactive_power"] > 30.0:
        alerts.append({
            "priority": "High",
            "title": "Kritik Reaktif Yük",
            "message": f"Sistem reaktif sınırları aşıyor: {sim_data['reactive_power']} kVarh",
            "recommendation": "Endüktif yükleri acilen azaltın veya kapasitör devreye alın."
        })

    # Kural 3: Aşırı Tüketim (Yük tipine göre)
    # Maximum Load için limit 125.0 oldu.
    threshold = 125.0 if sim_data["load_type"] == "Maximum_Load" else 25.0
    
    # Tahmin için tekrar hesaplama yapalım
    now = datetime.now()
    pred = predict_consumption(now, **sim_data)
    
    # %10 tolerans ile (125 * 1.1 = ~137 kWh'i geçerse uyarı verir)
    if pred > threshold * 1.1:
        alerts.append({
            "priority": "High",
            "title": "Aşırı Tüketim Uyarısı",
            "message": f"Anlık tüket ({round(pred, 2)} kWh), beklenen limitin üzerinde.",
            "recommendation": "Gereksiz cihazları kapatın."
        })

    # Sistem Normalsa: Rastgele farklı mesajlar göster (Canlılık hissi için)
    import random
    if not alerts:
        normal_messages = [
            ("Sistem Stabil", "Tüm parametreler nominal değerlerde seyrediyor.", "Rutin kontrollere devam edebilirsiniz."),
            ("Verimlilik Yüksek", "Enerji tüketimi ve güç faktörü ideal seviyede.", "Tasarruf modundasınız."),
            ("Optimum Çalışma", "Şebeke kalitesi ve yük dengesi mükemmel.", "Herhangi bir müdahaleye gerek yok."),
            ("Güvenli Mod", "Sistem güvenli sınırlar içerisinde çalışıyor.", "Bakım planına uygun ilerleyiniz.")
        ]
        # Saate göre veya rastgele seç
        choice = random.choice(normal_messages)
        
        alerts.append({
            "priority": "Low",
            "title": choice[0],
            "message": choice[1],
            "recommendation": choice[2]
        })
        
    return jsonify(alerts)

@app.route('/api/cost-analysis', methods=['GET'])
def get_cost_analysis():
    # Maliyet de tahmine dayalı olsun
    try:
        now = datetime.now()
        sim_data = get_current_simulation_data()
        prediction = predict_consumption(now, **sim_data)
        
        # Basit hesaplama: Tahmin edilen * 24 saat
        # Kullanıcı başlangıçta 500 TL görmek istedi.
        # Ort tüketim 25 kWh * 24 = 600 kWh. 500 / 600 = ~0.85 TL/kWh
        daily_total = prediction * 24 * 0.85
        
        return jsonify({
            "total_cost": round(daily_total, 2),
            "peak_cost": round(daily_total * 0.6, 2), # %60'ı peak olsun
            "off_peak_cost": round(daily_total * 0.4, 2)
        })
    except:
        return jsonify({"total_cost": 0.0, "peak_cost": 0.0, "off_peak_cost": 0.0})

if __name__ == '__main__':
    print("Enerji İzleme Sunucusu Başlatılıyor...")
    print("Port: 5000")
    app.run(host='0.0.0.0', port=5000)
