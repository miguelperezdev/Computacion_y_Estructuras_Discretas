"""
preprocessing.py
Text cleaning, tokenization, and dataset preparation utilities.
"""

import os
import re
import pickle
import numpy as np
import pandas as pd
import nltk
from nltk.corpus import stopwords
from sklearn.model_selection import train_test_split
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences

try:
    nltk.download("stopwords", quiet=True)
except Exception as e:
    print(f"Warning: could not download NLTK resources: {e}")

try:
    STOP_WORDS = set(stopwords.words("english"))
except Exception:
    STOP_WORDS = set()

MAX_VOCAB_SIZE = 5000
MAX_SEQ_LEN = 50
TEST_SIZE = 0.2
RANDOM_STATE = 42
SAMPLE_PER_CLASS = 500


def load_raw_data(data_dir: str) -> pd.DataFrame:
    """
    Load Twitter US Airline Sentiment dataset.
    Filters to positive/negative only, then samples SAMPLE_PER_CLASS per class
    with a fixed random seed for reproducibility.
    Returns a DataFrame with columns: sentence, label.
    """
    path = os.path.join(data_dir, "raw", "Tweets.csv")
    if not os.path.exists(path):
        raise FileNotFoundError(
            f"Tweets.csv not found at {path}. "
            "Download from Kaggle: crowdflower/twitter-airline-sentiment"
        )

    df = pd.read_csv(path)
    df = df[df["airline_sentiment"].isin(["positive", "negative"])].copy()

    pos = df[df["airline_sentiment"] == "positive"].sample(
        SAMPLE_PER_CLASS, random_state=RANDOM_STATE
    )
    neg = df[df["airline_sentiment"] == "negative"].sample(
        SAMPLE_PER_CLASS, random_state=RANDOM_STATE
    )
    df = pd.concat([pos, neg], ignore_index=True)

    df = df.rename(columns={"text": "sentence", "airline_sentiment": "label", "airline": "source"})
    df["label"] = (df["label"] == "positive").astype(int)
    df = df[["sentence", "label", "source"]].dropna()
    return df


NEGATION_WORDS = {
    "no", "not", "nor", "never", "neither", "nobody", "nothing", "nowhere",
    "hardly", "scarcely", "barely", "doesn", "didn", "wasn", "shouldn",
    "wouldn", "couldn", "won", "can", "cannot",
}


def clean_text(text: str, remove_stopwords: bool = False) -> str:
    """
    Clean a tweet: remove URLs, @mentions, expand hashtags, strip punctuation.
    Negation words are always preserved even when remove_stopwords=True.
    """
    text = str(text).lower()
    text = re.sub(r"http\S+|www\S+", "", text)    # remove URLs
    text = re.sub(r"@\w+", "", text)               # remove @mentions
    text = re.sub(r"#(\w+)", r"\1", text)          # #hashtag → word
    text = re.sub(r"&\w+;", " ", text)             # HTML entities (&amp; etc.)
    text = re.sub(r"[^a-z\s]", "", text)           # keep only letters
    tokens = text.split()
    if remove_stopwords:
        tokens = [t for t in tokens if t not in STOP_WORDS or t in NEGATION_WORDS]
    return " ".join(tokens)


def preprocess_dataframe(df: pd.DataFrame, remove_stopwords: bool = False) -> pd.DataFrame:
    """Apply clean_text to the 'sentence' column."""
    df = df.copy()
    df["clean_sentence"] = df["sentence"].apply(
        lambda x: clean_text(x, remove_stopwords=remove_stopwords)
    )
    df["token_count"] = df["clean_sentence"].apply(lambda x: len(x.split()))
    return df


def build_tokenizer(texts, vocab_size: int = MAX_VOCAB_SIZE):
    """Fit a Keras Tokenizer on the training texts."""
    tokenizer = Tokenizer(num_words=vocab_size, oov_token="<OOV>")
    tokenizer.fit_on_texts(texts)
    return tokenizer


def texts_to_sequences(tokenizer, texts, max_len: int = MAX_SEQ_LEN):
    """Convert texts to padded integer sequences."""
    seqs = tokenizer.texts_to_sequences(texts)
    padded = pad_sequences(seqs, maxlen=max_len, padding="post", truncating="post")
    return padded


def save_tokenizer(tokenizer, path: str):
    with open(path, "wb") as f:
        pickle.dump(tokenizer, f)


def load_tokenizer(path: str):
    with open(path, "rb") as f:
        return pickle.load(f)


def load_and_preprocess(
    data_dir: str,
    save_processed: bool = True,
    vocab_size: int = MAX_VOCAB_SIZE,
    max_seq_len: int = MAX_SEQ_LEN,
    test_size: float = TEST_SIZE,
    random_state: int = RANDOM_STATE,
    remove_stopwords: bool = False,
):
    """
    Full pipeline: load -> clean -> split -> tokenize -> pad.

    Returns:
        X_train, X_test, y_train, y_test, tokenizer, df_clean
    """
    df = load_raw_data(data_dir)
    df = preprocess_dataframe(df, remove_stopwords=remove_stopwords)

    X = df["clean_sentence"].values
    y = df["label"].values

    X_train_txt, X_test_txt, y_train, y_test = train_test_split(
        X, y, test_size=test_size, stratify=y, random_state=random_state
    )

    tokenizer = build_tokenizer(X_train_txt, vocab_size=vocab_size)

    X_train = texts_to_sequences(tokenizer, X_train_txt, max_len=max_seq_len)
    X_test = texts_to_sequences(tokenizer, X_test_txt, max_len=max_seq_len)

    if save_processed:
        proc_dir = os.path.join(data_dir, "processed")
        os.makedirs(proc_dir, exist_ok=True)
        df.to_csv(os.path.join(proc_dir, "data_clean.csv"), index=False)
        np.save(os.path.join(proc_dir, "X_train.npy"), X_train)
        np.save(os.path.join(proc_dir, "X_test.npy"), X_test)
        np.save(os.path.join(proc_dir, "y_train.npy"), y_train)
        np.save(os.path.join(proc_dir, "y_test.npy"), y_test)
        save_tokenizer(tokenizer, os.path.join(proc_dir, "tokenizer.pkl"))
        print(f"Processed data saved to {proc_dir}")

    return X_train, X_test, y_train, y_test, tokenizer, df
