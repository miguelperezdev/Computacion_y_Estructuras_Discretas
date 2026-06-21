# 🚕 Follow-Up 5 — Predicting High Tips in NYC Yellow Taxi Trips

This project compares four deep learning architectures — **DNN, RNN, LSTM, and Transformer** — for binary classification of NYC Yellow Taxi trips: predicting whether a trip will result in a **high tip** (tip ≥ 20% of fare amount).

---

##  Project Structure

```
follow-up-5-miguelperezdev/
├── data/
│   ├── November.parquet
│   └── December.parquet
├── taxi_analysis.py          # Main analysis script
├── resultados_comparativa.csv
├── comparativa_modelos.png
└── README.md
```

---

##  Dataset

- **Source:** NYC Yellow Taxi Trip Records (November & December 2025)
- **Raw size:** 8,486,450 rows
- **After cleaning:** 8,038,179 rows (removed trips with fare ≤ 0 or negative tip)
- **Sample used:** 0.5% → **40,191 trips**
- **Target variable:** `high_tip` — binary flag (1 if tip ≥ 20% of fare, else 0)
- **Class balance:** Nearly balanced (~50/50 split in the sample)

### Features Used (12 total)

| Feature | Description |
|---|---|
| `fare_amount` | Base fare charged |
| `tip_amount` | Tip paid |
| `trip_distance` | Distance traveled (miles) |
| `passenger_count` | Number of passengers |
| `extra` | Extra charges |
| `mta_tax` | MTA surcharge |
| `tolls_amount` | Tolls paid |
| `improvement_surcharge` | Improvement surcharge |
| `congestion_surcharge` | Congestion surcharge |
| `hour` | Pickup hour (derived) |
| `dow` | Day of week (derived) |
| `month` | Month (derived) |

---

##  Setup & Installation

```bash
# Clone the repository
git clone <repo-url>
cd follow-up-5-miguelperezdev

# Create and activate virtual environment
python -m venv taxi_env
# Windows
taxi_env\Scripts\activate
# macOS/Linux
source taxi_env/bin/activate

# Install dependencies
pip install pandas numpy scikit-learn tensorflow matplotlib seaborn pyarrow
```

---

##  Running the Analysis

```bash
python taxi_analysis.py
```

Place the Parquet files in the `./data/` directory before running. The script will:

1. Load and merge November + December data
2. Clean the dataset and create the `high_tip` column
3. Sample 0.5% of the data
4. Preprocess features (imputation + standardization)
5. Train all four models with early stopping
6. Print and save a comparison table
7. Save a bar chart of all metrics

---

##  Models

### DNN (Deep Neural Network)
- Input → Dense(64, ReLU) → Dropout(0.3) → Dense(32, ReLU) → Dropout(0.3) → Dense(1, Sigmoid)
- Optimizer: Adam (lr=0.001) | Loss: Binary Crossentropy | Batch: 128 | Epochs: 5

### RNN (Recurrent Neural Network)
- SimpleRNN(32) → Dense(16, ReLU) → Dropout(0.2) → Dense(1, Sigmoid)
- Input reshaped to `(n_samples, n_features, 1)`

### LSTM (Long Short-Term Memory)
- LSTM(32) → Dense(16, ReLU) → Dropout(0.2) → Dense(1, Sigmoid)
- Input reshaped to `(n_samples, n_features, 1)`

### Transformer
- Input reshaped to `(n_samples, 1, n_features)`
- Dense projection → MultiHeadAttention(4 heads) → Add & LayerNorm → FFN → GlobalAvgPool → Dense(1, Sigmoid)

All models use **EarlyStopping** (`patience=2`, `restore_best_weights=True`).

---

##  Results

### Comparison Table

| Model | Accuracy | Precision | Recall | F1-Score | Kappa |
|---|---|---|---|---|---|
| **DNN** | **0.9940** | **0.9945** | **0.9936** | **0.9940** | **0.9881** |
| RNN | 0.9525 | 0.9290 | 0.9802 | 0.9539 | 0.9049 |
| LSTM | 0.8114 | 0.7442 | 0.9512 | 0.8350 | 0.6225 |
| Transformer | 0.9934 | 0.9975 | 0.9893 | 0.9934 | 0.9868 |

###  Best Model: DNN

The DNN achieves the highest F1-Score (0.9940) and Kappa (0.9881), with near-perfect balance between precision and recall. It also trains faster (~2.9 s) than the Transformer (~17 s), making it the most practical choice for this tabular dataset.

The Transformer is a very close second (F1 = 0.9934), but adds architectural complexity without meaningful gains on this non-sequential data structure.

---

##  Precision vs. Recall Trade-offs

| Model | Precision | Recall | Notes |
|---|---|---|---|
| DNN | 0.9945 | 0.9936 | Extremely balanced — best for general use |
| RNN | 0.9290 | 0.9802 | Favors recall; better when missing a high-tip is costly |
| LSTM | 0.7442 | 0.9512 | High recall but many false positives |
| Transformer | 0.9975 | 0.9893 | Highest precision; best when false alarms are expensive |

**Use case guidance:**
- **Driver reward systems** → prioritize **Recall** (don't miss true high-tippers) → RNN or DNN
- **Customer-facing predictions** → prioritize **Precision** (don't over-promise) → Transformer or DNN

---

##  Cohen's Kappa Interpretation

Cohen's Kappa measures classification agreement beyond chance:

| Range | Interpretation |
|---|---|
| < 0.20 | Insignificant |
| 0.21 – 0.40 | Acceptable |
| 0.41 – 0.60 | Moderate |
| 0.61 – 0.80 | Substantial |
| 0.81 – 1.00 | Almost perfect |

| Model | Kappa | Interpretation |
|---|---|---|
| DNN | 0.9881 | Almost perfect |
| RNN | 0.9049 | Almost perfect |
| LSTM | 0.6225 | Substantial |
| Transformer | 0.9868 | Almost perfect |

The LSTM's lower Kappa reflects that the dataset lacks meaningful sequential structure — LSTM's temporal memory provides no advantage over a simple DNN for tabular trip data.

---

##  Hyperparameters (DNN — Best Model)

> *Note: Full GridSearchCV was not performed due to time constraints. The parameters below were selected manually.*

| Parameter | Value |
|---|---|
| Hidden layers | 2 (64 → 32 neurons) |
| Activation | ReLU (hidden), Sigmoid (output) |
| Dropout | 0.3 per layer |
| Optimizer | Adam |
| Learning rate | 0.001 |
| Loss | Binary Crossentropy |
| Batch size | 128 |
| Max epochs | 5 |
| Early stopping patience | 2 |

### Suggested GridSearchCV Search Space

```python
param_grid = {
    'layers':        [[64, 32], [128, 64, 32]],
    'dropout':       [0.2, 0.3, 0.4],
    'learning_rate': [0.01, 0.001, 0.0001],
    'batch_size':    [64, 128, 256],
}
```

---

##  Key Concepts

- [`pandas.read_parquet()`](https://pandas.pydata.org/docs/reference/api/pandas.read_parquet.html) — reading Parquet files with PyArrow
- [`sklearn.metrics.cohen_kappa_score`](https://scikit-learn.org/stable/modules/generated/sklearn.metrics.cohen_kappa_score.html) — Kappa computation
- `EarlyStopping` with `restore_best_weights=True` — prevents overfitting
- `StandardScaler` + `SimpleImputer` — feature preprocessing pipeline
- `MultiHeadAttention` in Keras — Transformer block for tabular data

---

##  Future Work

- Full `GridSearchCV` or `Optuna` hyperparameter tuning
- Feature engineering: traffic density, weather data, borough zones
- Testing on full dataset (100% sample, all 12 months)
- SMOTE or class-weight adjustments for imbalanced variants
- Model deployment as a REST API for real-time tip prediction

---

##  Author

**Miguel Perez** — `miguelperezdev`  
NYC Yellow Taxi Tip Prediction | Deep Learning Classification Project