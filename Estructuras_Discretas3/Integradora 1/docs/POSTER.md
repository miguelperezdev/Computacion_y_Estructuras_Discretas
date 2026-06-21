# 🛡️ Chomsky  
**Code Hazard Observation via Modeling of Syntax and KeY-patterns**  
*A Formal Language Approach to Repository Security Analysis*

**Miguel Pérez¹, Estefany Villamarin¹**  
¹Department of CSI, Computation and Discrete Structures III – Universidad ICESI, 2026-1

---

## 1. Problem

> **10M+ secrets exposed yearly in public repositories** – GitGuardian 2024

Modern code repositories suffer from **credential leakage**:
- Hardcoded passwords, API keys, tokens, database strings
- Accidental exposure via `print()` or logging
- Insecure configs (`DB_PASSWORD=admin123`)
- Nested, unbalanced, or malformed configuration files

**Consequence:** Unauthorized access, data breaches, infrastructure compromise.

**Key insight:** These vulnerabilities are **textual patterns** – they can be detected, classified, transformed, and validated using **formal language theory** (Chomsky hierarchy).

---

## 2. Solution – Chomsky Pipeline

A **4‑stage security analyzer** that processes source code and config files as formal language objects.

| Stage | Model | Question answered |
|-------|-------|-------------------|
| 1. Detection | Regular Expressions | *Does the file contain insecure patterns?* |
| 2. Classification | DFA (Deterministic Finite Automaton) | *Does the sequence of events constitute a violation?* |
| 3. Transformation | FST (Finite State Transducer) | *How to rewrite insecure code safely?* |
| 4. Validation | CFG (Context‑Free Grammar) | *Is the config file structurally secure?* |

---

## 3. Formal Models

### 3.1 Detection – Regular Languages
Pattern example: `AKIA[0-9A-Z]{16}` (AWS key)  
**Regular language** – recognized by DFA, implemented with Python `re`.

### 3.2 Classification – DFA (5‑tuple)

**M = (Q, Σ, δ, q₀, F)**

- **Q** = {q0_start, q1_cred, q2_print, q3_violation, q4_review}  
- **Σ** = {CREDENTIAL, LEAK, HIGH_RISK, LOW_RISK, OTHER}  
- **q₀** = q0_start  
- **F** = all states (classification depends on final state)

| State | CREDENTIAL | LEAK | HIGH_RISK |
|-------|------------|------|-----------|
| q0_start | q1_cred | q2_print | q4_review |
| q1_cred | q1_cred | **q3_violation** | **q3_violation** |
| q2_print | **q3_violation** | q2_print | q4_review |
| q3_violation | q3_violation | q3_violation | q3_violation |
| q4_review | **q3_violation** | q4_review | q4_review |

**Classification mapping:**  
- q0_start → **Safe**  
- q1_cred / q2_print / q4_review → **Needs Review**  
- q3_violation → **Security Violation**

### 3.3 Transformation – FST (7‑tuple)

**T = (Q, Σ, Δ, δ, λ, q₀, F)**  
Processes line by line, applies first matching rule:

| Rule | Input → Output |
|------|----------------|
| `REPLACE_PASSWORD` | `password = "x"` → `password = os.getenv("APP_PASSWORD")` |
| `REMOVE_PRINT` | `print(secret)` → `# [CHOMSKY] Sensitive output removed` |
| `UPGRADE_HTTP` | `http://...` → `https://...` |
### 3.4 Validation – CFG (EBNF)

Config       ::= Section* Entry*  
Section      ::= ID '{' Entry* Section* '}'   (* recursive nesting *)  
Entry        ::= Key '=' Value ';'  
SensitiveKey ::= 'password' | 'api_key' | 'db_pass' | ...  
Value        ::= EnvReference | QuotedString | Number  
EnvReference ::= '${' ID '}'  

Security constraint: Sensitive key ⇒ value MUST be ${VAR}.  
Why not regular? Recursive section nesting requires a pushdown automaton (Pumping Lemma).

---

## 4. Architecture

Source Code / Config File  
         │  
         ▼  
┌─────────────────────┐  
│ 1. DETECTION (re)   │ → List[Detection] (labels + positions)  
└────────┬────────────┘  
         │ token stream: ["HARDCODED_PASSWORD", "PRINT_CALL"]  
         ▼  
┌─────────────────────┐  
│ 2. CLASSIFICATION   │ → ClassificationReport  
│    (DFA)            │   (Safe / Needs Review / Security Violation)  
└────────┬────────────┘  
         │  
         ▼  
┌─────────────────────┐  
│ 3. TRANSFORMATION   │ → TransducerOutput  
│    (FST)            │   (refactored code + changes)  
└────────┬────────────┘  
         │ (if config file)  
         ▼  
┌─────────────────────┐  
│ 4. VALIDATION (CFG) │ → ValidationReport  
└─────────────────────┘   (is_valid, is_secure, errors)  

Implementation: Python 3.10+ with re, pyformlang, textX, Flask web UI.

---

## 5. Results

### 5.1 Detection & Classification – Test Cases

| TC | Input | Detection labels | DFA final state | Classification |
|----|------|------------------|-----------------|---------------|
| TC-01 | password = "x"<br>print(password) | HARDCODED_PASSWORD, PRINT_CALL | q3_violation | Security Violation |
| TC-02 | os.getenv("PWD") | (none) | q0_start | Safe |
| TC-03 | api_key = "AKIA..." | AWS_API_KEY | q1_cred | Needs Review |
| TC-04 | eval(user_input) | EVAL_CALL | q4_review | Needs Review |
| TC-10 (JS) | const p = "admin"<br>console.log(p) | HARDCODED_PASSWORD, CONSOLE_LOG | q3_violation | Security Violation |

---

### 5.2 Transformation – Example

Input:

password = "admin123"  
print(password)  

Output (FST):

import os  
password = os.getenv("APP_PASSWORD")  
# [CHOMSKY] Sensitive output removed: print(password)  

REPLACE_PASSWORD + REMOVE_PRINT applied, import os added.

---

### 5.3 Validation – CFG

| Config file | is_valid | is_secure | Errors |
|------------|----------|-----------|--------|
| DB_PASSWORD=${DB_PASS} | True | True | none |
| DB_PASSWORD=admin123 | True | False | Sensitive key 'DB_PASSWORD' must use ${VAR} |
| database { db_pass = ${PASS}; } | True | True | none |
| database { (no closing brace) | False | False | Unbalanced braces |

---

### 5.4 Coverage

- 5 DFA states – all reachable and tested  
- 17 regex patterns – covering AWS, GitHub, Stripe, passwords, URLs, IPv4, eval/exec, print/log  
- 7 FST rules – including env-var replacement, print removal, HTTP to HTTPS  
- CFG supports nested sections – recursive production verified  

---

## 6. Conclusion

Chomsky successfully applies the Chomsky hierarchy to automate code repository security analysis:

- Regular expressions detect lexical patterns  
- DFA models policy violation sequences  
- FST performs secure source-to-source rewriting  
- CFG validates hierarchical config structure  

The system classifies files as Safe, Needs Review, or Security Violation, and provides automatic refactoring suggestions.

Limitations and future work:

- Entropy-based detection for unstructured secrets  
- Support for more languages (Java, Go, Rust)  
- Integration with pre-commit hooks and CI pipelines  

---

## 7. References (abridged)

- Sipser, M. (2013). Introduction to the Theory of Computation (3rd ed.). Cengage  
- Hopcroft, Motwani, Ullman (2006). Automata Theory, Languages, Computation. Pearson  
- GitGuardian (2024). State of Secrets Sprawl 2024  
- Meli, M. et al. (2019). How Bad Can It Git? NDSS  
- Dejanović et al. (2016). textX: A Python tool for DSLs. SoftwareX  