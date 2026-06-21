# Experiment Notes

<<<<<<< Updated upstream
**Course:** Computación y Estructuras Discretas III — 2026-1  
**Team:** Estefany Villamarin, Miguel Perez, Andres Fajardo

---

## Dataset

Twitter US Airline Sentiment — 500 positive + 500 negative tweets (random_state=42).  
Tweets are short (~10–25 tokens after cleaning), noisy, with @mentions, URLs, and hashtags removed.

=======
**Course:** Computación y Estructuras Discretas III — 2025-2  
**Team:** Estefany Villamarin, Miguel Perez, Juan Fajardo

---

>>>>>>> Stashed changes
## Hyperparameter Configurations Tested

### Dense NN

<<<<<<< Updated upstream
| embed_dim | hidden_units | dropout | lr | max_seq_len | Notes |
|-----------|-------------|---------|-----|-------------|-------|
| 64 | 64 | 0.3 | 1e-3 | 50 | Default config (used in final report) |
=======
| embed_dim | hidden_units | dropout | lr | Notes |
|-----------|-------------|---------|-----|-------|
| 64 | 128 | 0.3 | 1e-3 | Default config (used in final report) |
>>>>>>> Stashed changes

### Vanilla RNN

| embed_dim | rnn_units | dropout | lr | Notes |
|-----------|-----------|---------|-----|-------|
| 64 | 64 | 0.3 | 1e-3 | Default config (used in final report) |

<<<<<<< Updated upstream
### LSTM

| embed_dim | lstm_units | dropout | lr | Notes |
|-----------|------------|---------|-----|-------|
| 64 | 64 | 0.3 | 1e-3 | Default config (used in final report) |

### Transformer (DistilBERT)

| model | max_len | batch_size | epochs | Notes |
|-------|---------|------------|--------|-------|
| distilbert-base-uncased | 50 | 16 | 3 | Fine-tuned on balanced tweet dataset |
=======
### LSTM — Grid Search (notebook 03)

Explored combinations of:
- `lstm_units`: 32, 64, 128
- `dropout_rate`: 0.2, 0.4
- `learning_rate`: 1e-3, 5e-4

Best configuration found: see `outputs/metrics/lstm_tuning_results.csv`
>>>>>>> Stashed changes

---

## Decisions Made

<<<<<<< Updated upstream
1. **Vocab size = 5000** — Covers most tweet vocabulary while keeping embedding matrix manageable.
2. **Max sequence length = 50** — Tweets are short; 50 tokens covers >99% of samples.
3. **80/20 train-test split** — Standard split, stratified to preserve 50/50 class balance.
4. **Batch size = 32** — Good tradeoff between gradient stability and training speed.
5. **EarlyStopping patience = 5, monitor=val_accuracy** — Targets classification performance directly.
6. **remove_stopwords = False** — Negation words (not, never, no) are critical for tweet sentiment.
7. **mask_zero=True on Embedding** — Ensures RNN/LSTM ignore zero-padded positions correctly.
8. **Tweet-specific cleaning** — URLs, @mentions, and HTML entities removed; hashtags expanded to words.
=======
1. **Vocab size = 5000** — Covers most common words while keeping embedding matrix manageable.
2. **Max sequence length = 100** — Captures >95% of sentence lengths in the dataset.
3. **80/20 train-test split** — Standard split, stratified to preserve class balance.
4. **Batch size = 32** — Good tradeoff between gradient stability and training speed.
5. **EarlyStopping patience = 5** — Prevents overfitting while allowing enough epochs to converge.
6. **remove_stopwords = True** — Used for all models; trade-off noted (may remove negation words).
7. **DistilBERT omitted** — Optional extension; omitted to focus on required RNN/LSTM comparison.
>>>>>>> Stashed changes

---

## Observations

<<<<<<< Updated upstream
*(To be filled after training)*

- Dense NN converges fastest (fewest parameters, no sequential processing).
- Vanilla RNN processes tokens sequentially but may struggle with tweet abbreviations.
- LSTM gated memory better captures negation and contrast in short tweet sequences.
- Transformer (DistilBERT) leverages pre-trained contextual embeddings — expected to outperform all custom architectures.
=======
- Dense NN converges fastest (fewest parameters).
- Vanilla RNN shows signs of vanishing gradient on longer sequences; val_loss plateaus early.
- LSTM consistently achieves higher validation accuracy than Dense NN and Vanilla RNN.
- Early stopping typically triggers between epoch 10-20 for all models.
- Overfitting observed when dropout < 0.2 on Dense NN.
>>>>>>> Stashed changes
