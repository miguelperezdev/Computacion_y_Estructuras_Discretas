"""
train.py
Training routines for all neural models.
"""

import os
import numpy as np
import tensorflow as tf
from tensorflow.keras.callbacks import (
    EarlyStopping,
    ModelCheckpoint,
    ReduceLROnPlateau,
    CSVLogger,
)

tf.random.set_seed(42)
np.random.seed(42)


def get_callbacks(
    model_name: str,
    checkpoint_dir: str = "outputs/saved_models",
    log_dir: str = "logs",
    patience: int = 5,
):
    """
    Returns a list of Keras callbacks:
    - EarlyStopping
    - ModelCheckpoint (saves best weights)
    - ReduceLROnPlateau
    - CSVLogger
    """
    os.makedirs(checkpoint_dir, exist_ok=True)
    os.makedirs(log_dir, exist_ok=True)

    return [
        EarlyStopping(
            monitor="val_accuracy",
            patience=patience,
            restore_best_weights=True,
            verbose=1,
        ),
        ModelCheckpoint(
            filepath=os.path.join(checkpoint_dir, f"{model_name}_best.keras"),
            monitor="val_accuracy",
            save_best_only=True,
            verbose=0,
        ),
        ReduceLROnPlateau(
            monitor="val_loss",
            factor=0.5,
            patience=3,
            min_lr=1e-6,
            verbose=1,
        ),
        CSVLogger(
            os.path.join(log_dir, f"{model_name}_training_log.csv"),
            append=False,
        ),
    ]


def train_model(
    model,
    X_train,
    y_train,
    X_val=None,
    y_val=None,
    epochs: int = 20,
    batch_size: int = 32,
    model_name: str = "model",
    checkpoint_dir: str = "outputs/saved_models",
    log_dir: str = "logs",
    validation_split: float = 0.1,
    patience: int = 5,
):
    """
    Train a Keras model with callbacks.

    Args:
        model:           compiled Keras model
        X_train, y_train: training data
        X_val, y_val:    optional explicit validation set; if None, uses validation_split
        epochs:          maximum number of epochs
        batch_size:      mini-batch size
        model_name:      used for checkpoint filenames
        checkpoint_dir:  where to save .keras checkpoints
        log_dir:         where to save CSVLogger output
        validation_split: fraction of training data used for validation (only when X_val is None)
        patience:        EarlyStopping patience

    Returns:
        Keras History object
    """
    callbacks = get_callbacks(
        model_name=model_name,
        checkpoint_dir=checkpoint_dir,
        log_dir=log_dir,
        patience=patience,
    )

    if X_val is not None and y_val is not None:
        history = model.fit(
            X_train,
            y_train,
            validation_data=(X_val, y_val),
            epochs=epochs,
            batch_size=batch_size,
            callbacks=callbacks,
            verbose=1,
        )
    else:
        history = model.fit(
            X_train,
            y_train,
            validation_split=validation_split,
            epochs=epochs,
            batch_size=batch_size,
            callbacks=callbacks,
            verbose=1,
        )

    return history