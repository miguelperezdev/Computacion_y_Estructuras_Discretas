"""
models.py
Model architectures: Dense NN, Vanilla RNN, LSTM.
"""

import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import (
    Input, Embedding, GlobalAveragePooling1D, Dense, Dropout,
    SimpleRNN, LSTM,
)
from tensorflow.keras.optimizers import Adam

tf.random.set_seed(42)


def build_dense(
    vocab_size: int = 5000,
    max_seq_len: int = 100,
    embed_dim: int = 64,
    hidden_units: int = 64,
    dropout_rate: float = 0.3,
    learning_rate: float = 1e-3,
) -> Sequential:
    """
    Dense Neural Network for sentiment classification.
    Architecture: Embedding -> GlobalAveragePooling1D -> Dense -> Dropout -> Dense(sigmoid)

    GlobalAveragePooling averages token embeddings, discarding order —
    equivalent to a bag-of-words classifier with learned word vectors.
    TM analogy: finite transducer — reads all symbols simultaneously, ignores position.
    """
    model = Sequential(
        [
            Input(shape=(max_seq_len,), name="input"),
            Embedding(input_dim=vocab_size, output_dim=embed_dim, name="embedding"),
            GlobalAveragePooling1D(name="pooling"),
            Dense(hidden_units, activation="relu", name="dense_1"),
            Dropout(dropout_rate),
            Dense(1, activation="sigmoid", name="output"),
        ],
        name="DenseNN",
    )
    model.compile(
        optimizer=Adam(learning_rate=learning_rate),
        loss="binary_crossentropy",
        metrics=["accuracy"],
    )
    return model


def build_rnn(
    vocab_size: int = 5000,
    max_seq_len: int = None,  # accepted for API compatibility, not used (mask_zero handles padding)
    embed_dim: int = 64,
    rnn_units: int = 64,
    dropout_rate: float = 0.3,
    learning_rate: float = 1e-3,
) -> Sequential:
    """
    Vanilla RNN for sentiment classification.
    Architecture: Embedding (mask_zero) -> SimpleRNN -> Dropout -> Dense(sigmoid)

    mask_zero=True propagates a padding mask so the RNN ignores zero-padded positions.
    Processes tokens sequentially, maintaining a single hidden state.
    TM analogy: pushdown automaton — bounded memory, limited long-range recall.
    """
    model = Sequential(
        [
            Embedding(input_dim=vocab_size, output_dim=embed_dim, mask_zero=True, name="embedding"),
            SimpleRNN(rnn_units, return_sequences=False, name="rnn"),
            Dropout(dropout_rate),
            Dense(1, activation="sigmoid", name="output"),
        ],
        name="VanillaRNN",
    )
    model.compile(
        optimizer=Adam(learning_rate=learning_rate),
        loss="binary_crossentropy",
        metrics=["accuracy"],
    )
    return model


def build_lstm(
    vocab_size: int = 5000,
    max_seq_len: int = None,  # accepted for API compatibility, not used (mask_zero handles padding)
    embed_dim: int = 64,
    lstm_units: int = 64,
    dropout_rate: float = 0.3,
    learning_rate: float = 1e-3,
) -> Sequential:
    """
    LSTM for sentiment classification.
    Architecture: Embedding (mask_zero) -> LSTM -> Dropout -> Dense(sigmoid)

    mask_zero=True propagates a padding mask so the LSTM ignores zero-padded positions.
    Three gates (input, forget, output) selectively control information flow,
    enabling long-range dependency capture.
    TM analogy: linear-bounded automaton — gated cell state acts like a
    read/write tape, overcoming the vanishing gradient problem of SimpleRNN.
    """
    model = Sequential(
        [
            Embedding(input_dim=vocab_size, output_dim=embed_dim, mask_zero=True, name="embedding"),
            LSTM(lstm_units, return_sequences=False, name="lstm"),
            Dropout(dropout_rate),
            Dense(1, activation="sigmoid", name="output"),
        ],
        name="LSTM",
    )
    model.compile(
        optimizer=Adam(learning_rate=learning_rate),
        loss="binary_crossentropy",
        metrics=["accuracy"],
    )
    return model


def get_model_summary(model) -> str:
    """Return model summary as a string."""
    lines = []
    model.summary(print_fn=lambda x: lines.append(x))
    return "\n".join(lines)
