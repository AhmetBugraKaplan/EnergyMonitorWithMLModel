# EnergyMonitor

**Enerji Tüketimi Optimizasyonu ve İzleme Sistemi**

Bu proje, bir çelik sanayi tesisinin (veya benzeri bir işletmenin) enerji tüketimini gerçek zamanlı olarak izlemek, maliyetleri analiz etmek ve yapay zeka destekli tahminlerle anormallikleri tespit etmek için geliştirilmiş kapsamlı bir mobil uygulama ve backend çözümüdür.

## Özellikler

*   **Gerçek Zamanlı İzleme:** Anlık enerji tüketimini (kWh), güç faktörünü ve maliyeti canlı olarak takip edin.
*   **Akıllı Uyarı Sistemi:** Tüketim seviyelerine göre (Normal, Uyarı, Kritik) anında geri bildirim alın. Beklenen sınırların aşılması durumunda otomatik uyarılar.
*   **Maliyet Analizi:** Birim elektrik fiyatına göre anlık ve geçmiş maliyet hesaplamaları.
*   **Makine Öğrenmesi Desteği:** Geçmiş verilerle eğitilmiş **Gradient Boosting** modeli ile gelecek tüketim tahminleri.
*   **Manuel Simülasyon Modu:** Uygulama özelliklerini test etmek için sensör verilerini (Lag, CO2, Güç Faktörü vb.) manuel olarak değiştirme imkanı.
*   **Gelişmiş Grafikler:** Canlı tüketim akışını gösteren dinamik grafikler.

## Teknoloji Yığını

### Android Uygulaması (Client)
*   **Dil:** Kotlin
*   **UI Framework:** Jetpack Compose (Modern Android UI)
*   **Mimari:** MVVM (Model-View-ViewModel) + Clean Architecture
*   **Dependency Injection:** Hilt
*   **Networking:** Retrofit & OkHttp
*   **Asenkron İşlemler:** Coroutines & Flow

### Backend Sunucusu
*   **Dil:** Python
*   **Framework:** Flask
*   **ML Kütüphaneleri:** Scikit-learn, Pandas, Joblib

## Kurulum ve Çalıştırma

### 1. Ön Hazırlıklar
*   Android Studio (Ladybug veya daha yeni sürüm önerilir)
*   Python 3.8+ yüklü bir bilgisayar

### 2. Backend Kurulumu
Backend sunucusu, Android uygulamasının veri kaynağıdır ve ML modelini barındırır.

1.  `backend` klasörüne gidin:
    ```bash
    cd backend
    ```
2.  Gerekli Python kütüphanelerini yükleyin:
    ```bash
    pip install -r requirements.txt
    ```
    *(Not: `requirements.txt` yoksa manuel olarak: `pip install flask pandas scikit-learn joblib`)*
3.  Sunucuyu başlatın:
    ```bash
    python server.py
    ```
    Sunucu varsayılan olarak `http://0.0.0.0:5000` adresinde çalışacaktır.

### 3. Android Uygulaması Kurulumu
1.  Projeyi Android Studio ile açın.
2.  `Constants.kt` dosyasındaki (veya ilgili ağ modülündeki) `BASE_URL` ayarını kontrol edin. Emülatör için genellikle `http://10.0.2.2:5000/` kullanılır.
3.  Projenizi derleyin (Build) ve bir emülatör veya fiziksel cihazda çalıştırın.

## Yapay Zeka Modeli Detayları

Bu projede kullanılan ML modeli, enerji tüketimini etkileyen çeşitli faktörleri analiz ederek tahminleme yapar.

*   **Veri Seti:** Steel Industry Energy Consumption Dataset
*   **Kullanılan Algoritma:** Gradient Boosting Regressor
*   **Performans (R2 Skoru):** ~0.995 (Yüksek doğruluk)
*   **Girdi Öznitelikleri (Features):**
    *   `Lag_1`, `Lag_2` (Geçmiş tüketim verileri)
    *   `PF_Lead`, `PF_Lag` (Güç Faktörleri)
    *   `tCO2` (Karbondioksit emisyonu)
    *   Zaman bilgileri: `hour_sin`, `hour_cos`, `day_of_week`, `is_weekend`

## Kullanım Senaryosu (Simülasyon)

Uygulamanın tepkilerini test etmek için "Simülasyon" ekranını kullanabilirsiniz:
1.  Uygulamada **Ayarlar/Simülasyon** menüsüne gidin.
2.  "Normal", "Uyarı" veya "Kritik" senaryolarından birini seçerek "Uygula" butonuna basın.
3.  Ana Sayfa'ya döndüğünüzde, seçtiğiniz senaryoya uygun olarak ibrelerin, renklerin ve uyarı metinlerinin değiştiğini gözlemleyebilirsiniz.
