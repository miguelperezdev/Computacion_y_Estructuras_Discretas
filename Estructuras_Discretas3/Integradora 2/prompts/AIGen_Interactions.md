# AIGen Interactions Log

**Course:** Computación y Estructuras Discretas III — 2026-1  
**Task:** Integrative Task 2  
<<<<<<< Updated upstream
**Team:** Estefany Villamarin, Miguel Perez, Andres Fajardo
=======
**Team:** Estefany Villamarin, Miguel Perez, Juan Fajardo
>>>>>>> Stashed changes

---

## Summary of AI-Assisted Work

| # | Tool | Topic | Student Contribution |
|---|------|-------|----------------------|
| 1 | Claude | RNN vs LSTM architecture, TM analogy | Adapted explanation to our report; rewrote code to fit project structure |
| 2 | Claude | Cohen's Kappa interpretation | Synthesized explanation; included in comparative analysis |
| 3 | Claude | Tweet-specific NLP preprocessing | Implemented pipeline; evaluated trade-off of removing stopwords; kept negation words |
| 4 | Claude | DistilBERT fine-tuning and self-attention vs RNN | Adapted conceptual explanation to notebook 04; all code written from scratch |

---

## Interaction Detail

### Interaction 1 — Architecture and TM Analogy

**Prompt provided to AI:**  
"Help me understand the difference between RNN and LSTM architectures and how they relate to Turing Machine concepts of memory and state."

**AI response (summary):**  
The AI explained that a vanilla RNN is analogous to a finite automaton: it has a single hidden state vector of fixed size, which limits its ability to remember long-term dependencies. An LSTM uses a separate cell state with input, forget, and output gates that selectively read and write — closer to a bounded Turing tape. The AI also provided a Keras code skeleton for both architectures.

**How we modified the output:**  
We discarded the bidirectional LSTM variant suggested by the AI (not required by the assignment), integrated the TM analogy into the notebook markdown cells and report in our own words, and rewrote all code from scratch in `src/models.py` to fit our project structure.

---

### Interaction 2 — Cohen's Kappa

**Prompt provided to AI:**  
"Explain what Cohen's Kappa measures and why it is more informative than accuracy for classification tasks with balanced datasets."

**How we modified the output:**  
We synthesized the explanation to two sentences appropriate for our report, and added a concrete example using our balanced 500+500 Twitter dataset to show when kappa and accuracy coincide.

---

### Interaction 3 — Tweet Preprocessing Pipeline

**Prompt provided to AI:**  
"What preprocessing steps are recommended for sentiment analysis on Twitter data with RNN/LSTM models? How should URLs, mentions, and hashtags be handled?"

**How we modified the output:**  
We implemented the pipeline from scratch in `src/preprocessing.py` following the recommendations. We implemented URL removal, @mention removal, and hashtag expansion. Critical deviation: the AI recommended removing all stopwords, but we chose to keep negation words (not, never, no, nor) with `remove_stopwords=False` as default after verifying empirically that removing them degraded model accuracy on the validation set.

---

### Interaction 4 — DistilBERT Fine-tuning and Self-Attention

**Prompt provided to AI:**  
"How does DistilBERT fine-tuning work for text classification, and how does the self-attention mechanism differ conceptually from LSTM's recurrent computation?"

**AI response (summary):**  
The AI explained that fine-tuning updates pre-trained Transformer weights on a downstream task, allowing strong performance even with small datasets due to transfer learning. It described self-attention as a mechanism where every token attends to every other token in parallel, contrasted with LSTM's sequential hidden state updates.

**How we modified the output:**  
We adapted the conceptual comparison into notebook 04 markdown cells and the TM analogy table. All implementation code (TweetDataset class, training loop, attention visualization, evaluation) was written from scratch by the team. The AI did not generate any code included in the final project.

---

## Declaration

All code was written, reviewed, and validated by team members. AI tools were used only as a reference to understand concepts, not to generate final code or text verbatim. All outputs were critically evaluated and substantially modified before inclusion in the project.
