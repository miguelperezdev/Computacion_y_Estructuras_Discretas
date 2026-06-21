# Chomsky – Design Document

## Overview
**Chomsky** (Code Hazard Observation via Modeling of Syntax and KeY-patterns) is a code repository security analyzer that processes source code and configuration files using formal language theory.

---

## Module 1 – Detection (Regular Expressions)

### Objective
Extract insecure patterns from source code at the **lexical level**.

### Formal Language Definition
Each pattern defines a **regular language** over the ASCII alphabet Σ.

| Pattern | Regular Expression | Language Description |
|---|---|---|
| `AWS_API_KEY` | `AKIA[0-9A-Z]{16}` | Strings starting with AKIA followed by exactly 16 uppercase alphanumeric chars |
| `HARDCODED_PASSWORD` | `(?i)(password\|passwd\|pwd)\s*=\s*["'][^"']{2,}["']` | Assignment of a non-empty quoted string to a password variable |
| `PRINT_CALL` | `\bprint\s*\([^)]*\)` | Python print() call with any arguments |
| `IPV4_ADDRESS` | `\b(?:\d{1,3}\.){3}\d{1,3}\b` | Dotted-decimal IPv4 address |
| `INSECURE_URL` | `\bhttp://[^\s"']+` | URL using plain HTTP (not HTTPS) |
| `PLAIN_CONFIG_VALUE` | `(?i)^(db_password\|...)\s*=\s*(?!\$\{)[^\s#\n]+` | Config key with non-env-reference value |

### Input / Output
- **Input:** Source code string (str over Σ)
- **Output:** `List[Detection]` — each detection has: `label`, `match`, `line`, `column`, `severity`

---

## Module 2 – Classification (Finite Automaton)

### Formal Definition (DFA 5-tuple)
```
M = (Q, Σ, δ, q₀, F)
```
- **Q** = { q0_start, q1_has_credential, q2_has_print_only, q3_violation, q4_needs_review }
- **Σ** = { CREDENTIAL, LEAK, HIGH_RISK, LOW_RISK, OTHER }  ← abstract symbol classes
- **q₀** = q0_start
- **F** = all states (classification determined by final state)
- **δ** (transition table):

| Current State | CREDENTIAL | LEAK | HIGH_RISK | LOW_RISK | OTHER |
|---|---|---|---|---|---|
| q0_start | q1_has_credential | q2_has_print_only | q4_needs_review | q4_needs_review | q0_start |
| q1_has_credential | q1_has_credential | **q3_violation** | **q3_violation** | q1_has_credential | q1_has_credential |
| q2_has_print_only | **q3_violation** | q2_has_print_only | q4_needs_review | q2_has_print_only | q2_has_print_only |
| q3_violation | q3_violation | q3_violation | q3_violation | q3_violation | q3_violation |
| q4_needs_review | **q3_violation** | q4_needs_review | q4_needs_review | q4_needs_review | q4_needs_review |

### State → Classification
| State | Classification |
|---|---|
| q0_start | **Safe** |
| q1_has_credential | **Needs Review** |
| q2_has_print_only | **Needs Review** |
| q3_violation | **Security Violation** |
| q4_needs_review | **Needs Review** |

### Transition Diagram
```
         CREDENTIAL          LEAK
[q0] ─────────────► [q1] ──────────► [q3 VIOLATION] ←──┐
  │                   │                    ▲              │
  │LEAK                │OTHER/LOW_RISK      │CREDENTIAL    │
  ▼                   │                    │              │
[q2] ◄───────────────┘              [q4 REVIEW] ─────────┘
  │ CREDENTIAL                        ▲
  └───────────────────────────────────┘ HIGH_RISK from any
```

### Input / Output
- **Input:** `List[str]` — token labels from Stage 1
- **Output:** `ClassificationReport` — result, final_state, state_trace

---

## Module 3 – Transformation (Finite State Transducer)

### Formal Definition (FST 7-tuple)
```
T = (Q, Σ, Δ, δ, λ, q₀, F)
```
- **Q** = { q_start, q_applying_rule, q_done }
- **Σ** = input lines of source code
- **Δ** = output lines (transformed source code)
- **q₀** = q_start
- **F** = { q_done }
- **δ** = state transition (line by line, applying first matching rule)
- **λ** = output function (see rules below)

### Transducer Rules

| Rule | Input Pattern | Output |
|---|---|---|
| REPLACE_PASSWORD | `password = "..."` | `password = os.getenv("APP_PASSWORD")` |
| REPLACE_SECRET | `api_key = "..."` | `api_key = os.getenv("APP_API_KEY")` |
| REPLACE_AWS_KEY | `x = "AKIA..."` | `x = os.getenv("AWS_ACCESS_KEY_ID")` |
| REMOVE_PRINT | `print(x)` | `# [CHOMSKY] Sensitive output removed` |
| UPGRADE_HTTP | `http://...` | `https://...` |
| SECURE_CONFIG_VALUE | `KEY=plaintext` | `KEY=${KEY}` |

### Input / Output
- **Input:** Source code string
- **Output:** `TransducerOutput` — transformed source, list of changes, imports added

---

## Module 4 – Validation (Context-Free Grammar)

### Formal CFG Definition (EBNF)
```ebnf
Config       ::= Section* Entry*
Section      ::= ID '{' Entry* Section* '}'    (* recursive *)
Entry        ::= Key '=' Value ';'
Key          ::= SensitiveKey | RegularKey
SensitiveKey ::= 'password' | 'secret' | 'api_key' | 'token' | ...
RegularKey   ::= ID
Value        ::= EnvReference | QuotedString | Number | Bool | PlainID
EnvReference ::= '${' ID '}'
```

### Why Not Regular?
The production `Section → ID '{' Entry* Section* '}'` is **recursive**: sections can be arbitrarily nested. By the **Pumping Lemma for Regular Languages**, no finite automaton can track an unbounded nesting depth of matched delimiter pairs `{ }`. Therefore, this language is **strictly context-free**.

### Security Constraint
Any `Key` classified as `SensitiveKey` **must** have an `EnvReference` as its value. A literal string value for a sensitive key is a policy violation.

### Input / Output
- **Input:** Configuration file contents (str)
- **Output:** `ValidationReport` — is_valid, is_secure, errors, warnings, parsed_entries

---

## Test Cases

### Scenario 1 – Full Security Violation
```python
password = "admin123"     # Detection: HARDCODED_PASSWORD (HIGH)
print(password)           # Detection: PRINT_CALL (MEDIUM)
# DFA: q0 → q1_has_credential → q3_violation
# Classification: Security Violation
# Transformation: replaces password line + comments print
```

### Scenario 2 – Safe Code
```python
import os
password = os.getenv("APP_PASSWORD")
# No detections → DFA stays in q0_start → Safe
```

### Scenario 3 – Needs Review (credential, no leak)
```python
api_key = "AKIA1234567890ABCDE"
# DFA: q0 → q1_has_credential
# Classification: Needs Review
```

### Scenario 4 – Secure Config
```
DB_PASSWORD=${SECURE_DB_PASSWORD}   # Valid: env reference
APP_NAME=chomsky                    # Valid: non-sensitive key
```

### Scenario 5 – Insecure Config
```
DB_PASSWORD=admin123   # Invalid: sensitive key with literal value
```

---

## Module Interaction Diagram
```
Source Code / Config File
         │
         ▼
  ┌─────────────┐
  │ 1. DETECT   │  Regular Expressions → List[label]
  └──────┬──────┘
         │ token stream
         ▼
  ┌─────────────┐
  │ 2. CLASSIFY │  DFA → Safe / Needs Review / Security Violation
  └──────┬──────┘
         │ classification
         ▼
  ┌─────────────┐
  │ 3. TRANSFORM│  FST → Refactored Source Code
  └──────┬──────┘
         │
         ▼
  ┌─────────────┐
  │ 4. VALIDATE │  CFG (config files only) → Structural Validity
  └─────────────┘
```