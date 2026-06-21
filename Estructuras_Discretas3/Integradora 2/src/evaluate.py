import os
import json
import numpy as np
import pandas as pd
from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    cohen_kappa_score,
    confusion_matrix,
    classification_report,
)


def get_predictions(model, X, threshold: float = 0.5):
    """Get binary predictions from a Keras sigmoid model."""
    probs = model.predict(X, verbose=0).flatten()
    preds = (probs >= threshold).astype(int)
    return preds, probs


def compute_metrics(y_true, y_pred, model_name: str = "model") -> dict:
    """
    Compute accuracy, precision, recall, F1, and Cohen's kappa.

    Returns:
        dict with all metrics
    """
    return {
        "model": model_name,
        "accuracy": round(float(accuracy_score(y_true, y_pred)), 4),
        "precision": round(float(precision_score(y_true, y_pred, zero_division=0)), 4),
        "recall": round(float(recall_score(y_true, y_pred, zero_division=0)), 4),
        "f1_score": round(float(f1_score(y_true, y_pred, zero_division=0)), 4),
        "cohen_kappa": round(float(cohen_kappa_score(y_true, y_pred)), 4),
    }


def evaluate_model(model, X_test, y_test, model_name: str = "model") -> dict:
    """
    Evaluate a Keras model on the test set.

    Returns:
        dict with metrics and predictions
    """
    y_pred, y_probs = get_predictions(model, X_test)
    metrics = compute_metrics(y_test, y_pred, model_name=model_name)

    print(f"\n{'='*50}")
    print(f"  Model: {model_name}")
    print(f"{'='*50}")
    print(f"  Accuracy:     {metrics['accuracy']:.4f}")
    print(f"  Precision:    {metrics['precision']:.4f}")
    print(f"  Recall:       {metrics['recall']:.4f}")
    print(f"  F1-Score:     {metrics['f1_score']:.4f}")
    print(f"  Cohen Kappa:  {metrics['cohen_kappa']:.4f}")
    print(f"\n{classification_report(y_test, y_pred, target_names=['Negative', 'Positive'])}")

    metrics["y_pred"] = y_pred.tolist()
    metrics["y_probs"] = y_probs.tolist()
    return metrics


def compare_models(results: list, save_path: str = None) -> pd.DataFrame:
    """
    Build a comparison DataFrame from a list of metrics dicts.

    Args:
        results:   list of dicts from compute_metrics()
        save_path: optional CSV save path

    Returns:
        pd.DataFrame sorted by F1-Score descending
    """
    rows = []
    for r in results:
        rows.append(
            {
                "Model": r.get("model", "unknown"),
                "Accuracy": r.get("accuracy", 0),
                "Precision": r.get("precision", 0),
                "Recall": r.get("recall", 0),
                "F1-Score": r.get("f1_score", 0),
                "Cohen Kappa": r.get("cohen_kappa", 0),
            }
        )
    df = pd.DataFrame(rows).sort_values("F1-Score", ascending=False).reset_index(drop=True)

    if save_path:
        # Guard against empty dirname (e.g. save_path = "metrics.csv")
        parent = os.path.dirname(save_path)
        if parent:
            os.makedirs(parent, exist_ok=True)
        df.to_csv(save_path, index=False)
        print(f"Metrics saved to {save_path}")

    return df


def save_metrics(metrics: dict, path: str):
    """Save a metrics dict as JSON (strips non-serializable prediction arrays)."""
    parent = os.path.dirname(path)
    if parent:
        os.makedirs(parent, exist_ok=True)
    clean = {k: v for k, v in metrics.items() if k not in ("y_pred", "y_probs")}
    with open(path, "w") as f:
        json.dump(clean, f, indent=2)
    print(f"Metrics saved to {path}")


def get_confusion_matrix(y_true, y_pred):
    """Return confusion matrix as ndarray."""
    return confusion_matrix(y_true, y_pred)