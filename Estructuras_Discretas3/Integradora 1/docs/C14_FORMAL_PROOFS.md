# Formal Proofs and Theoretical Justifications

## Chomsky — Code Repository Security Analyzer

This document provides the mathematical foundations for the design decisions in Chomsky, connecting each module to the formal language theory studied in CyED3.

---

## 1. Regular Languages Suffice for Pattern Detection (Module 1)

**Claim:** Each security pattern defined in `detector.py` is a regular language.

**Proof:**

A language is regular if and only if it is recognized by a DFA (by Kleene's theorem: Regular Expressions ≡ NFA ≡ DFA).

Python's `re` module compiles regular expressions to finite automata internally (using Thompson's construction: Regex → NFA, then subset construction: NFA → DFA). Therefore, each compiled pattern corresponds to a DFA.

Key patterns and their regularity:

**AWS API Key:** `AKIA[0-9A-Z]{16}`

This is the concatenation of the finite string `{AKIA}` with the finite Cartesian product `([A-Z0-9])^{16}`. A finite language is trivially regular (recognized by a DFA with finitely many states — one per character position). The concatenation of regular languages is regular (closure under concatenation).

**IPv4 Address:** `\b(?:\d{1,3}\.){3}\d{1,3}\b`

This is the language: `(\d{1,3} \cdot \{\text{.}\})^3 \cdot \d{1,3}`. Since `\d{1,3}` is a finite union of finite concatenations, it is regular. The Kleene star of a regular language is regular (closure under *). The concatenation of regular languages is regular. Therefore the full pattern is regular.

**Note:** We do NOT validate that each octet is ≤ 255 — that constraint would make the language non-regular (requiring counting). We detect the structural pattern only, consistent with the lexical (not semantic) role of this stage.

**Formal claim:** `L_detect = ⋃ᵢ Lᵢ` where each `Lᵢ` is the language of pattern `i`. Since regular languages are closed under finite union, `L_detect` is regular. ∎

---

## 2. The DFA Correctly Models the Security Policy (Module 2)

**Claim:** The 5-tuple DFA `M = (Q, Σ, δ, q₀, F)` correctly accepts all and only the token sequences that represent security violations.

**Proof sketch:**

We define the policy language formally:

```
L_violation = { w ∈ Σ* | w contains CREDENTIAL followed eventually by LEAK, 
                           OR LEAK followed eventually by CREDENTIAL,
                           OR CREDENTIAL followed eventually by HIGH_RISK }
```

This language is regular (it can be expressed by the regular expression):

```
Σ* · CREDENTIAL · Σ* · LEAK · Σ*
∪ Σ* · LEAK · Σ* · CREDENTIAL · Σ*
∪ Σ* · CREDENTIAL · Σ* · HIGH_RISK · Σ*
```

The DFA we built recognizes exactly this language:
- `q0_start → q1_has_credential` on CREDENTIAL, then `q1 → q3_violation` on LEAK: captures CREDENTIAL...LEAK.
- `q0_start → q2_has_print_only` on LEAK, then `q2 → q3_violation` on CREDENTIAL: captures LEAK...CREDENTIAL.
- `q1 → q3_violation` on HIGH_RISK: captures CREDENTIAL...HIGH_RISK.

`q3_violation` is absorbing, modeling the policy that a violation persists regardless of subsequent input.

**Minimality:** The 5-state DFA cannot be reduced further. Each state corresponds to a distinct right-invariant equivalence class of strings under the Myhill-Nerode relation:
- `ε` (empty string) → `q0_start`
- `CREDENTIAL` → `q1_has_credential`
- `LEAK` → `q2_has_print_only`
- `CREDENTIAL LEAK` → `q3_violation`
- `HIGH_RISK` → `q4_needs_review`

These are 5 distinct classes (distinguished by continuations), so by the Myhill-Nerode theorem, the minimum DFA has exactly 5 states. ∎

---

## 3. The FST Implements a Well-Defined Transduction (Module 3)

**Claim:** The FST `T = (Q, Σ, Δ, δ, λ, q₀, F)` correctly implements a sequential transduction (a rational relation) from insecure code to secure code.

**Proof sketch:**

A sequential transducer is a special case of an FST where the transition function is deterministic and the output at each step depends only on the current state and current input symbol.

Our transducer processes source code **line by line**, and for each line applies the **first matching rule** (deterministic choice — no ambiguity). This makes it a sequential (deterministic) transducer.

The output function λ maps:
- `(q_start, l)` → `(q_applying_rule, transform(l))` if rule r matches l
- `(q_start, l)` → `(q_start, l)` if no rule matches (identity transduction)

Since each rule is defined by a regex (a regular expression over Σ), and the output is a function of the match (a rational function), the composition is a rational transduction — exactly what FSTs compute.

The transduction is total (defined for every input line) and functional (each input maps to exactly one output), properties of sequential transducers. ∎

---

## 4. The Configuration Language Is Context-Free but Not Regular (Module 4)

**Claim:** The language `L_config` of valid secure configurations with nested sections is context-free but not regular.

**Proof that `L_config` is context-free:**

We exhibit a CFG (provided in EBNF in `validator.py` and `DESIGN.md`). A language is context-free if and only if it is generated by a CFG (by definition). The grammar generates all valid configurations, so `L_config` is context-free. ∎

**Proof that `L_config` is not regular (by Pumping Lemma):**

Consider the sublanguage `L' = { a^n '{' b '}'^n | n ≥ 1 }` where `a^n` represents n characters of a section name, `b` is a valid entry, and `}^n` represents n closing braces (one per nested section).

More precisely, consider the language of correctly nested single-key sections:

```
L' = { S₁ '{' S₂ '{' ... Sₙ '{' key = ${VAR}; '}' ... '}' '}' | n ≥ 1 }
```

Assume for contradiction that `L'` is regular with pumping length `p`.

Choose `s = S₁ { S₂ { ... Sₚ { key = ${VAR}; } ... } }` where the string has p pairs of matched braces.

By the Pumping Lemma, `s = xyz` where `|xy| ≤ p`, `|y| ≥ 1`, and `xy^i z ∈ L'` for all i ≥ 0.

Since `|xy| ≤ p`, both x and y lie within the opening part of the string (section names and opening braces). Pumping y (i = 2) creates a string with more opening braces than closing braces — which is not in `L'` (unbalanced structure). Contradiction.

Therefore `L'` is not regular, and since `L'` is a sublanguage of `L_config`, `L_config` is not regular. A context-free grammar (and a pushdown automaton) is required. ∎

---

## 5. The Chomsky Hierarchy in Chomsky (the Application)

The four modules of the Chomsky application correspond to four levels of the **Chomsky Hierarchy**:

| Hierarchy Level | Grammar Type | Automaton | Chomsky Module |
|---|---|---|---|
| Type 3 — Regular | Regular Grammar | DFA / NFA | Module 1: Detection (regex) |
| Type 3 — Regular | Regular Grammar | DFA | Module 2: Classification (DFA) |
| Type 3 — Regular (transduction) | — | FST (rational transducer) | Module 3: Transformation |
| Type 2 — Context-Free | CFG | PDA (Pushdown Automaton) | Module 4: Validation (textX) |

The application is named "Chomsky" as an homage to Noam Chomsky, who formalized the hierarchy of formal languages and grammars that forms the theoretical backbone of this project.
