import joblib
import os
import pandas as pd

def inspect_model():
    try:
        model_path = os.path.join(os.path.dirname(__file__), '..', 'model', 'best_model_gradient_boosting.pkl')
        model = joblib.load(model_path)
        print("✅ Model yüklendi")
        
        # Pipeline ise asıl modele ulaşmaya çalışalım veya doğrudan pipeline özelliklerine bakalım
        if hasattr(model, 'feature_names_in_'):
            print("\n--- Modelin Beklediği Özellikler (feature_names_in_) ---")
            for i, name in enumerate(model.feature_names_in_):
                print(f"{i}: {name}")
        elif hasattr(model, 'steps'): # Pipeline
            # Genelde son adım modeldir, ama feature isimleri ilk adımda (scaler) veya direk pipeline'da olabilir
            try:
                # Pipeline'ın feature name desteği sklearn versiyonuna göre değişir
                if hasattr(model, 'feature_names_in_'):
                    names = model.feature_names_in_
                else:
                    # Model adımına bakalım
                    step_name, step_model = model.steps[-1]
                    if hasattr(step_model, 'feature_names_in_'):
                        names = step_model.feature_names_in_
                    else:
                        names = ["Bulunamadı (feature_names_in_ yok)"]
                
                print("\n--- Pipeline Beklenen Özellikler ---")
                for i, name in enumerate(names):
                    print(f"{i}: {name}")
            except Exception as e:
                print(f"Pipeline özelliği okunamadı: {e}")
        else:
            print("Model feature_names_in_ özelliğine sahip değil.")

    except Exception as e:
        print(f"Hata: {e}")

if __name__ == "__main__":
    inspect_model()
