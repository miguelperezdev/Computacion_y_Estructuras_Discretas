"""
Follow-Up 5: Predicción de propinas altas (NYC Yellow Taxi)
Noviembre y Diciembre 2025 - Versión para VS Code (muestra 0.5%)
Modelos: DNN, RNN, LSTM, Transformer (corregido definitivo)
"""

import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import time
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.impute import SimpleImputer
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, cohen_kappa_score, classification_report
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, SimpleRNN, LSTM, Input, MultiHeadAttention, LayerNormalization, GlobalAveragePooling1D, Reshape, Flatten
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping
from tensorflow.keras import Model
from tensorflow.keras import layers
import warnings
warnings.filterwarnings('ignore')

print("Librerías importadas")

# ============================================================================
# 1. Cargar datos (noviembre y diciembre)
# ============================================================================
data_path = './data/'
try:
    df_nov = pd.read_parquet(data_path + 'November.parquet')
    df_dec = pd.read_parquet(data_path + 'December.parquet')
    df = pd.concat([df_nov, df_dec], ignore_index=True)
    print(f"Datos cargados: {len(df):,} filas")
except FileNotFoundError:
    print(" No se encontraron los archivos en la carpeta './data/'")
    exit(1)

# ============================================================================
# 2. Limpieza y creación de high_tip
# ============================================================================
df = df[(df['fare_amount'] > 0) & (df['tip_amount'] >= 0)].copy()
df['high_tip'] = (df['tip_amount'] >= 0.20 * df['fare_amount']).astype(int)
print(f"Después limpieza: {len(df):,} filas")
print("Distribución original:\n", df['high_tip'].value_counts())

# ============================================================================
# 3. Muestreo muy pequeño (0.5%)
# ============================================================================
frac = 0.005
df = df.sample(frac=frac, random_state=42)
print(f"Muestra del {frac*100:.1f}%: {len(df):,} filas")
print("Distribución en muestra:\n", df['high_tip'].value_counts())

# ============================================================================
# 4. Selección de features y preprocesamiento
# ============================================================================
num_cols = ['fare_amount', 'tip_amount', 'trip_distance', 'passenger_count',
            'extra', 'mta_tax', 'tolls_amount', 'improvement_surcharge', 'congestion_surcharge']
num_cols = [c for c in num_cols if c in df.columns]

if 'tpep_pickup_datetime' in df.columns:
    df['pickup_dt'] = pd.to_datetime(df['tpep_pickup_datetime'])
    df['hour'] = df['pickup_dt'].dt.hour
    df['dow'] = df['pickup_dt'].dt.dayofweek
    df['month'] = df['pickup_dt'].dt.month
    feature_cols = num_cols + ['hour', 'dow', 'month']
else:
    feature_cols = num_cols

X = df[feature_cols].copy()
y = df['high_tip'].copy()

imputer = SimpleImputer(strategy='median')
X = imputer.fit_transform(X)
scaler = StandardScaler()
X = scaler.fit_transform(X)
X = np.nan_to_num(X, nan=0.0, posinf=10.0, neginf=-10.0)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
print(f"Train shape: {X_train.shape}, Test shape: {X_test.shape}")

# Para RNN y LSTM: (n_samples, timesteps=features, channels=1)
X_train_rnn = X_train.reshape((X_train.shape[0], X_train.shape[1], 1))
X_test_rnn = X_test.reshape((X_test.shape[0], X_test.shape[1], 1))

# Para Transformer: (n_samples, timesteps=1, features=n_features)
X_train_trans = X_train.reshape((X_train.shape[0], 1, X_train.shape[1]))
X_test_trans = X_test.reshape((X_test.shape[0], 1, X_test.shape[1]))

# ============================================================================
# 5. Función de entrenamiento para DNN, RNN, LSTM
# ============================================================================
def train_eval(model, X_tr, y_tr, X_te, y_te, name, is_rnn=False):
    print(f"\n=== Entrenando {name} ===")
    early_stop = EarlyStopping(monitor='val_loss', patience=2, restore_best_weights=True)
    start = time.time()
    history = model.fit(
        X_tr if not is_rnn else X_train_rnn, y_tr,
        validation_split=0.1, epochs=5, batch_size=128, callbacks=[early_stop], verbose=1
    )
    y_pred = (model.predict(X_te if not is_rnn else X_test_rnn) > 0.5).astype(int).flatten()
    elapsed = time.time() - start
    print(f"Tiempo: {elapsed:.1f} s")
    acc = accuracy_score(y_te, y_pred)
    prec = precision_score(y_te, y_pred)
    rec = recall_score(y_te, y_pred)
    f1 = f1_score(y_te, y_pred)
    kappa = cohen_kappa_score(y_te, y_pred)
    print(f"Accuracy: {acc:.4f} | Precision: {prec:.4f} | Recall: {rec:.4f} | F1: {f1:.4f} | Kappa: {kappa:.4f}")
    return [name, acc, prec, rec, f1, kappa]

results = []

# ============================================================================
# 6. DNN
# ============================================================================
model_dnn = Sequential([
    Dense(64, activation='relu', input_shape=(X_train.shape[1],)),
    Dropout(0.3),
    Dense(32, activation='relu'),
    Dropout(0.3),
    Dense(1, activation='sigmoid')
])
model_dnn.compile(optimizer=Adam(0.001), loss='binary_crossentropy', metrics=['accuracy'])
results.append(train_eval(model_dnn, X_train, y_train, X_test, y_test, "DNN", is_rnn=False))

# ============================================================================
# 7. RNN
# ============================================================================
model_rnn = Sequential([
    SimpleRNN(32, input_shape=(X_train.shape[1], 1), dropout=0.2, recurrent_dropout=0.2),
    Dense(16, activation='relu'),
    Dropout(0.2),
    Dense(1, activation='sigmoid')
])
model_rnn.compile(optimizer=Adam(0.001), loss='binary_crossentropy', metrics=['accuracy'])
results.append(train_eval(model_rnn, X_train, y_train, X_test, y_test, "RNN", is_rnn=True))

# ============================================================================
# 8. LSTM
# ============================================================================
model_lstm = Sequential([
    LSTM(32, input_shape=(X_train.shape[1], 1), dropout=0.2, recurrent_dropout=0.2),
    Dense(16, activation='relu'),
    Dropout(0.2),
    Dense(1, activation='sigmoid')
])
model_lstm.compile(optimizer=Adam(0.001), loss='binary_crossentropy', metrics=['accuracy'])
results.append(train_eval(model_lstm, X_train, y_train, X_test, y_test, "LSTM", is_rnn=True))

# ============================================================================
# 9. TRANSFORMER (CORREGIDO DEFINITIVO)
# ============================================================================
def build_transformer(input_shape, d_model=64, num_heads=4, ff_dim=128, dropout_rate=0.1):
    """
    input_shape: (timesteps, features)
    Para datos tabulares: (1, n_features)
    """
    inputs = layers.Input(shape=input_shape)          
    x = layers.Dense(d_model)(inputs)               
    
    # Atención multi-cabeza
    attn = layers.MultiHeadAttention(num_heads=num_heads, key_dim=d_model)(x, x)
    x = layers.Add()([x, attn])
    x = layers.LayerNormalization()(x)
    
    # Feed-forward
    ff = layers.Dense(ff_dim, activation="relu")(x)
    ff = layers.Dense(d_model)(ff)
    x = layers.Add()([x, ff])
    x = layers.LayerNormalization()(x)
    
    # Reducción temporal
    x = layers.GlobalAveragePooling1D()(x)          
    x = layers.Dropout(dropout_rate)(x)
    outputs = layers.Dense(1, activation='sigmoid')(x)  
    
    model = Model(inputs, outputs)
    return model

print("\n=== Entrenando Transformer ===")
model_transformer = build_transformer(input_shape=(1, X_train.shape[1]))  
model_transformer.compile(optimizer=Adam(0.001), loss='binary_crossentropy', metrics=['accuracy'])

early_stop = EarlyStopping(monitor='val_loss', patience=2, restore_best_weights=True)
history = model_transformer.fit(
    X_train_trans, y_train,
    validation_split=0.1, epochs=5, batch_size=128, callbacks=[early_stop], verbose=1
)
y_pred_trans = (model_transformer.predict(X_test_trans) > 0.5).astype(int).flatten()
acc = accuracy_score(y_test, y_pred_trans)
prec = precision_score(y_test, y_pred_trans)
rec = recall_score(y_test, y_pred_trans)
f1 = f1_score(y_test, y_pred_trans)
kappa = cohen_kappa_score(y_test, y_pred_trans)
print(f"Accuracy: {acc:.4f} | Precision: {prec:.4f} | Recall: {rec:.4f} | F1: {f1:.4f} | Kappa: {kappa:.4f}")
results.append(["Transformer", acc, prec, rec, f1, kappa])

# ============================================================================
# 10. Tabla comparativa y gráfico
# ============================================================================
comparison_df = pd.DataFrame(results, columns=['Modelo', 'Accuracy', 'Precision', 'Recall', 'F1-Score', 'Kappa'])
print("\n" + "="*70)
print("📊 TABLA COMPARATIVA FINAL (Muestra 0.5% - Noviembre + Diciembre 2025)")
print("="*70)
print(comparison_df.to_string(index=False))

comparison_df.to_csv('resultados_comparativa.csv', index=False)
print("\n✅ Tabla guardada como 'resultados_comparativa.csv'")

# Gráfico
comparison_melt = comparison_df.melt(id_vars="Modelo", var_name="Métrica", value_name="Valor")
plt.figure(figsize=(12,5))
sns.barplot(data=comparison_melt, x="Métrica", y="Valor", hue="Modelo", palette="Set2")
plt.ylim(0,1)
plt.title("Comparación de modelos - Predicción de propina alta (0.5% sample)")
plt.legend(bbox_to_anchor=(1.05, 1))
plt.tight_layout()
plt.savefig('comparativa_modelos.png', dpi=150)
plt.show()
print("✅ Gráfico guardado como 'comparativa_modelos.png'")

# ============================================================================
# 11. Mejor modelo
# ============================================================================
best_idx = comparison_df["F1-Score"].idxmax()
best_model = comparison_df.loc[best_idx, "Modelo"]
best_f1 = comparison_df.loc[best_idx, "F1-Score"]
best_kappa = comparison_df.loc[best_idx, "Kappa"]

print(f"\n🏆 MEJOR MODELO: {best_model} (F1={best_f1:.4f}, Kappa={best_kappa:.4f})")

if best_kappa < 0.2:
    interp = "Insignificante"
elif best_kappa < 0.4:
    interp = "Aceptable"
elif best_kappa < 0.6:
    interp = "Moderado"
elif best_kappa < 0.8:
    interp = "Sustancial"
else:
    interp = "Casi perfecto"
print(f"Interpretación del Kappa: {best_kappa:.4f} → {interp}")

print("\n✅ Ejecución completada exitosamente.")