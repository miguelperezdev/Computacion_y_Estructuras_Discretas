# IntegrativeTask2 — Sentiment Analysis with Neural Networks

*Course:* Computación y Estructuras Discretas III — 2026-1  
*Institution:* Universidad ICESI  
*Team Members:*
- Estefany Villamarin
- Miguel Perez
- Andres Fajardo

---

## Project Overview

This project builds and compares sentiment analysis models trained on the [Twitter US Airline Sentiment Dataset](https://www.kaggle.com/datasets/crowdflower/twitter-airline-sentiment) (Kaggle). We implement and evaluate:

1. *DummyClassifier* — random baseline
2. *Dense Neural Network* — bag-of-words style via GlobalAveragePooling1D
3. *Vanilla RNN* — sequential processing with fixed hidden state
4. *LSTM* — gated memory for long-range dependencies
5. *Transformer (DistilBERT)* — pre-trained contextual embeddings with self-attention

Each architecture is connected to Turing Machine concepts: memory, sequence processing, and computability.

---

## Repository Structure


IntegrativeTask2-team/
├── README.md
├── requirements.txt
├── environment.yml
├── data/
│   ├── raw/                  # Twitter US Airline Sentiment (Tweets.csv)
│   ├── processed/            # Preprocessed/tokenized data (auto-generated)
│   └── README.md
├── notebooks/
│   ├── 01_eda.ipynb          # Exploratory Data Analysis
│   ├── 02_baseline_model.ipynb
│   ├── 03_dense_rnn_lstm.ipynb
│   ├── 04_transformer_extension.ipynb  # DistilBERT fine-tuning (mandatory)
│   └── utils.ipynb
├── src/
│   ├── __init__.py
│   ├── preprocessing.py
│   ├── models.py
│   ├── train.py
│   ├── evaluate.py
│   └── visualize.py
├── outputs/
│   ├── figures/
│   ├── metrics/
│   └── saved_models/
├── docs/
│   ├── report.pdf
│   ├── presentation.pdf
│   └── references.bib
├── prompts/
│   ├── prompt_logs.txt
│   └── AIGen_Interactions.md
└── logs/
    ├── training_logs.txt
    └── experiment_notes.md


---

## Setup & Installation

### Option A: pip

bash
pip install -r requirements.txt


### Option B: Conda

bash
conda env create -f environment.yml
conda activate sentiment-nlp


---

## How to Run

### Step 1 — Dataset

Download **Twitter US Airline Sentiment** from [Kaggle](https://www.kaggle.com/datasets/crowdflower/twitter-airline-sentiment) and place `Tweets.csv` inside `data/raw/`. The preprocessing pipeline automatically filters to positive/negative tweets and samples 500 of each class with `random_state=42`.

### Step 2 — Run notebooks in order


notebooks/01_eda.ipynb                  → EDA and class distribution
notebooks/02_baseline_model.ipynb       → DummyClassifier baseline
notebooks/03_dense_rnn_lstm.ipynb       → Dense NN, RNN, LSTM models
notebooks/04_transformer_extension.ipynb → DistilBERT fine-tuning (mandatory)


> All notebooks must be run from the notebooks/ directory, or with the repo root as the working directory. The sys.path.append('..') call in each notebook ensures src/ is importable when running from notebooks/.

### Step 3 — Use src/ modules independently

python
from src.preprocessing import load_and_preprocess
from src.models import build_lstm
from src.train import train_model
from src.evaluate import evaluate_model


---

## Results Summary

| Model           | Accuracy | Precision | Recall | F1-Score | Kappa |
|-----------------|----------|-----------|--------|----------|-------|
| DummyClassifier | 0.5000   | 0.0000    | 0.0000 | 0.0000   | 0.0000 |
| Dense NN        | 0.8400   | 0.8148    | 0.8800 | 0.8462   | 0.6800 |
| Vanilla RNN     | 0.8150   | 0.8621    | 0.7500 | 0.8021   | 0.6300 |
| LSTM            | 0.7950   | 0.7757    | 0.8300 | 0.8019   | 0.5900 |
| LSTM (tuned)    | 0.8350   | 0.8851    | 0.7700 | 0.8235   | 0.6700 |
| **DistilBERT**  | **0.9200** | **0.9286** | **0.9100** | **0.9192** | **0.8400** |

Results obtained on Twitter US Airline Sentiment dataset (500 pos + 500 neg, 80/20 stratified split).
Key preprocessing: tweet-specific cleaning (URLs, @mentions, hashtags), `remove_stopwords=False` to
preserve negation words. RNN and LSTM use `mask_zero=True` in Embedding to ignore zero-padded positions.
`MAX_SEQ_LEN=50` covers the full tweet length distribution (mean=15 tokens, max=29 tokens).

---

## Notes

- All models use `random_state=42` / `tf.random.set_seed(42)` for reproducibility.
- Dataset sampling uses `random_state=42` to guarantee a fixed balanced 500+500 subset.
- Hyperparameter tuning results are saved in `outputs/metrics/lstm_tuning_results.csv`.
- Transformer extension (DistilBERT) is implemented in `notebooks/04_transformer_extension.ipynb`.
- AI generation usage is logged in `prompts/`.
- `__pycache__/` and `.ipynb_checkpoints/` are excluded via `.gitignore`.
