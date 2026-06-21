"""
visualize.py
Visualization utilities for EDA, training curves, and model comparison.
"""

import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import confusion_matrix

try:
    plt.style.use("seaborn-v0_8-whitegrid")
except Exception:
    try:
        plt.style.use("seaborn-whitegrid")
    except Exception:
        plt.style.use("ggplot")

PALETTE = ["#2196F3", "#F44336", "#4CAF50", "#FF9800", "#9C27B0"]
FIGSIZE = (10, 5)


def save_fig(fig, path: str):
    """Save a figure, creating parent directories as needed."""
    parent = os.path.dirname(path)
    if parent:
        os.makedirs(parent, exist_ok=True)
    fig.savefig(path, bbox_inches="tight", dpi=150)
    print(f"Figure saved: {path}")
    plt.close(fig)


# ── EDA Plots ─────────────────────────────────────────────────────────────────

def plot_class_distribution(df: pd.DataFrame, save_path: str = None):
    """Bar chart of sentiment label distribution."""
    counts = df["label"].value_counts().sort_index()
    labels = ["Negative (0)", "Positive (1)"]

    fig, ax = plt.subplots(figsize=(6, 4))
    bars = ax.bar(labels, counts.values, color=PALETTE[:2], edgecolor="white", width=0.5)
    ax.set_title("Class Distribution", fontsize=14, fontweight="bold")
    ax.set_ylabel("Count")
    for bar, val in zip(bars, counts.values):
        ax.text(
            bar.get_x() + bar.get_width() / 2,
            bar.get_height() + 10,
            str(val),
            ha="center",
            fontsize=11,
        )
    ax.set_ylim(0, max(counts.values) * 1.2)
    plt.tight_layout()
    if save_path:
        save_fig(fig, save_path)
    else:
        plt.show()


def plot_sentence_length_distribution(df: pd.DataFrame, save_path: str = None):
    """Histogram of token counts per class."""
    fig, axes = plt.subplots(1, 2, figsize=FIGSIZE)
    for ax, (label, group) in zip(axes, df.groupby("label")):
        name = "Negative" if label == 0 else "Positive"
        ax.hist(group["token_count"], bins=30, color=PALETTE[label], edgecolor="white", alpha=0.85)
        ax.set_title(f"Token Count — {name}", fontsize=12)
        ax.set_xlabel("Tokens per Sentence")
        ax.set_ylabel("Frequency")
        ax.axvline(
            group["token_count"].mean(),
            color="black",
            linestyle="--",
            label=f"Mean: {group['token_count'].mean():.1f}",
        )
        ax.legend()
    plt.suptitle("Sentence Length Distribution by Class", fontsize=14, fontweight="bold")
    plt.tight_layout()
    if save_path:
        save_fig(fig, save_path)
    else:
        plt.show()


def plot_source_distribution(df: pd.DataFrame, save_path: str = None):
    """Grouped bar chart by source and sentiment."""
    pivot = df.groupby(["source", "label"]).size().unstack(fill_value=0)
    pivot.columns = ["Negative", "Positive"]

    fig, ax = plt.subplots(figsize=(7, 4))
    pivot.plot(kind="bar", ax=ax, color=PALETTE[:2], edgecolor="white", width=0.5)
    ax.set_title("Class Distribution by Source", fontsize=14, fontweight="bold")
    ax.set_xlabel("Source")
    ax.set_ylabel("Count")
    ax.set_xticklabels(pivot.index, rotation=0)
    ax.legend(title="Sentiment")
    plt.tight_layout()
    if save_path:
        save_fig(fig, save_path)
    else:
        plt.show()


def plot_wordcloud(df: pd.DataFrame, label: int = 1, save_path: str = None):
    """WordCloud for a given sentiment label."""
    try:
        from wordcloud import WordCloud
    except ImportError:
        print("wordcloud not installed. Run: pip install wordcloud")
        return

    text = " ".join(df[df["label"] == label]["clean_sentence"].dropna())
    if not text.strip():
        print(f"Warning: no words found for class {label}.")
        return

    wc = WordCloud(width=800, height=400, background_color="white",
                   max_words=100, colormap="RdYlGn").generate(text)

    fig, ax = plt.subplots(figsize=(10, 5))
    ax.imshow(wc, interpolation="bilinear")
    ax.axis("off")
    title = "Positive Sentiment" if label == 1 else "Negative Sentiment"
    ax.set_title(f"Word Cloud — {title}", fontsize=14, fontweight="bold")
    plt.tight_layout()
    if save_path:
        save_fig(fig, save_path)
    else:
        plt.show()


# ── Training Curves ───────────────────────────────────────────────────────────

def plot_training_history(history, model_name: str = "Model", save_path: str = None):
    """Plot training and validation accuracy + loss curves."""
    fig, axes = plt.subplots(1, 2, figsize=FIGSIZE)

    axes[0].plot(history.history["accuracy"], label="Train", color=PALETTE[0])
    axes[0].plot(history.history["val_accuracy"], label="Validation", color=PALETTE[1])
    axes[0].set_title(f"{model_name} — Accuracy", fontsize=12)
    axes[0].set_xlabel("Epoch")
    axes[0].set_ylabel("Accuracy")
    axes[0].legend()

    axes[1].plot(history.history["loss"], label="Train", color=PALETTE[0])
    axes[1].plot(history.history["val_loss"], label="Validation", color=PALETTE[1])
    axes[1].set_title(f"{model_name} — Loss", fontsize=12)
    axes[1].set_xlabel("Epoch")
    axes[1].set_ylabel("Loss")
    axes[1].legend()

    plt.tight_layout()
    if save_path:
        save_fig(fig, save_path)
    else:
        plt.show()


# ── Evaluation Plots ──────────────────────────────────────────────────────────

def plot_confusion_matrix(y_true, y_pred, model_name: str = "Model", save_path: str = None):
    """Annotated heatmap confusion matrix."""
    cm = confusion_matrix(y_true, y_pred)
    fig, ax = plt.subplots(figsize=(5, 4))
    sns.heatmap(
        cm,
        annot=True,
        fmt="d",
        cmap="Blues",
        xticklabels=["Predicted Neg", "Predicted Pos"],
        yticklabels=["True Neg", "True Pos"],
        ax=ax,
    )
    ax.set_title(f"Confusion Matrix — {model_name}", fontsize=12, fontweight="bold")
    plt.tight_layout()
    if save_path:
        save_fig(fig, save_path)
    else:
        plt.show()


def plot_metrics_comparison(results_df: pd.DataFrame, save_path: str = None):
    """Grouped bar chart comparing all models across metrics."""
    metrics = ["Accuracy", "Precision", "Recall", "F1-Score", "Cohen Kappa"]
    df_melted = results_df.melt(
        id_vars="Model", value_vars=metrics, var_name="Metric", value_name="Score"
    )

    num_models = results_df["Model"].nunique()
    palette = PALETTE[:num_models] if num_models <= len(PALETTE) else sns.color_palette("muted", num_models)

    fig, ax = plt.subplots(figsize=(12, 5))
    sns.barplot(
        data=df_melted, x="Metric", y="Score", hue="Model",
        palette=palette, ax=ax, edgecolor="white",
    )
    ax.set_title("Model Performance Comparison", fontsize=14, fontweight="bold")
    ax.set_ylim(0, 1.15)
    ax.set_ylabel("Score")
    ax.legend(title="Model", bbox_to_anchor=(1, 1))
    for bar in ax.patches:
        try:
            height = bar.get_height()
            if height > 0.01:
                ax.text(
                    bar.get_x() + bar.get_width() / 2,
                    height + 0.01,
                    f"{height:.2f}",
                    ha="center",
                    fontsize=7,
                )
        except AttributeError:
            continue
    plt.tight_layout()
    if save_path:
        save_fig(fig, save_path)
    else:
        plt.show()