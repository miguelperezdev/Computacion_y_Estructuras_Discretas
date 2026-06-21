# Module Design Document

## Chomsky — Code Repository Security Analyzer

**Formal design of all modules: function signatures, inputs, outputs, and inter-module data flow.**

---

## System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Chomsky Pipeline                          │
│                                                             │
│  Source Code / Config File (string)                         │
│         │                                                   │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │  Module 1   │  detector.py                             │
│  │  DETECTION  │  Regular Expressions → List[Detection]   │
│  └──────┬──────┘                                           │
│         │ token stream (List[str] of labels)               │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │  Module 2   │  classifier.py                           │
│  │ CLASSIFY    │  DFA → ClassificationReport              │
│  └──────┬──────┘                                           │
│         │ (parallel to Stage 2)                            │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │  Module 3   │  transducer.py                           │
│  │  TRANSFORM  │  FST → TransducerOutput                  │
│  └──────┬──────┘                                           │
│         │ (config files only)                              │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │  Module 4   │  validator.py                            │
│  │  VALIDATE   │  CFG → ValidationReport                  │
│  └─────────────┘                                           │
│                                                             │
│  pipeline.py: orchestrates all four stages                  │
└─────────────────────────────────────────────────────────────┘
```

---

## Module 1 — Detection (`src/detector.py`)

**Formal model:** Regular Languages (Regular Expressions)

### Public Functions

#### `detect(source: str) → List[Detection]`

| Parameter | Type | Description |
|---|---|---|
| `source` | `str` | Full source code or config file contents |

**Returns:** `List[Detection]` — sorted by line number, then column.

Each `Detection` has:

| Field | Type | Description |
|---|---|---|
| `label` | `str` | Pattern name (e.g. `"HARDCODED_PASSWORD"`) |
| `match` | `str` | Matched text (truncated to 40 chars for safety) |
| `line` | `int` | 1-based line number |
| `column` | `int` | 1-based column number |
| `severity` | `str` | `"HIGH"` / `"MEDIUM"` / `"LOW"` |

**Example:**

```python
src = 'password = "admin123"\nprint(password)'
detections = detect(src)
# → [Detection(label="HARDCODED_PASSWORD", match='password = "admin123"', line=1, column=1, severity="HIGH"),
#    Detection(label="PRINT_CALL", match="print(password)", line=2, column=1, severity="MEDIUM")]
```

---

#### `get_labels(detections: List[Detection]) → List[str]`

| Parameter | Type | Description |
|---|---|---|
| `detections` | `List[Detection]` | Output of `detect()` |

**Returns:** `List[str]` — ordered sequence of label strings.

This is the **token stream** fed directly into the DFA classifier (Module 2).

**Example:**

```python
labels = get_labels(detections)
# → ["HARDCODED_PASSWORD", "PRINT_CALL"]
```

---

#### `get_summary(detections: List[Detection]) → dict`

Returns a dict grouping labels by severity: `{"HIGH": [...], "MEDIUM": [...], "LOW": [...]}`.

---

### Defined Patterns

| Pattern Label | Regex | Severity |
|---|---|---|
| `AWS_API_KEY` | `AKIA[0-9A-Z]{16}` | HIGH |
| `GITHUB_TOKEN` | `ghp_[A-Za-z0-9_]{36}` | HIGH |
| `GITHUB_FINE_TOKEN` | `github_pat_...` | HIGH |
| `HARDCODED_PASSWORD` | `(?i)(password\|passwd\|pwd)\s*=\s*["'][^"']{2,}["']` | HIGH |
| `HARDCODED_SECRET` | `(?i)(secret\|api_key\|...)\s*=\s*["'][^"']{4,}["']` | HIGH |
| `DB_CONNECTION_STRING` | `(mongodb\|postgres\|...)://...:...@` | HIGH |
| `STRIPE_KEY` | `sk_live_[A-Za-z0-9]{24,}` | HIGH |
| `HIGH_ENTROPY_STRING` | `(key\|secret\|...)\s*=\s*["'][A-Za-z0-9...]{32,}["']` | HIGH |
| `EVAL_CALL` | `\beval\s*\(` | HIGH |
| `EXEC_CALL` | `\bexec\s*\(` | HIGH |
| `PRINT_CALL` | `\bprint\s*\([^)]*\)` | MEDIUM |
| `CONSOLE_LOG` | `\bconsole\.log\s*\([^)]*\)` | MEDIUM |
| `LOGGING_CALL` | `\blogging\.(debug\|info\|...)\s*\(` | MEDIUM |
| `INSECURE_URL` | `\bhttp://[^\s"']+` | MEDIUM |
| `PLAIN_CONFIG_VALUE` | `^(db_password\|...)\s*=\s*(?!\$\{)...` | HIGH |
| `TODO_CREDENTIAL` | `#\s*(TODO\|FIXME).*password` | LOW |
| `IPV4_ADDRESS` | `\b(?:\d{1,3}\.){3}\d{1,3}\b` | LOW |

---

## Module 2 — Classification (`src/classifier.py`)

**Formal model:** Deterministic Finite Automaton (DFA)

### Public Functions

#### `classify(labels: List[str]) → ClassificationReport`

| Parameter | Type | Description |
|---|---|---|
| `labels` | `List[str]` | Token stream from `get_labels()` (Module 1 output) |

**Returns:** `ClassificationReport`

| Field | Type | Description |
|---|---|---|
| `result` | `ClassificationResult` | `SAFE` / `NEEDS_REVIEW` / `SECURITY_VIOLATION` |
| `final_state` | `str` | Name of the DFA's final state |
| `state_trace` | `List[str]` | Full sequence of states visited |
| `symbol_trace` | `List[str]` | Abstracted symbols fed to the DFA |

**Example:**

```python
report = classify(["HARDCODED_PASSWORD", "PRINT_CALL"])
# → ClassificationReport(
#     result=ClassificationResult.SECURITY_VIOLATION,
#     final_state="q3_violation",
#     state_trace=["q0_start", "q1_has_credential", "q3_violation"],
#     symbol_trace=["CREDENTIAL", "LEAK"]
#   )
```

---

#### `build_pyformlang_dfa() → Optional[DeterministicFiniteAutomaton]`

Builds and returns the pyformlang DFA object for formal verification. Returns `None` if pyformlang is not installed.

---

#### `get_dfa_5tuple() → dict`

Returns the complete 5-tuple `(Q, Σ, δ, q₀, F)` as a Python dictionary for display and documentation.

---

### Token Abstraction: Labels → DFA Symbols

| DFA Symbol | Detector Labels |
|---|---|
| `CREDENTIAL` | `HARDCODED_PASSWORD`, `AWS_API_KEY`, `GITHUB_TOKEN`, `DB_CONNECTION_STRING`, ... |
| `LEAK` | `PRINT_CALL`, `CONSOLE_LOG`, `LOGGING_CALL` |
| `HIGH_RISK` | `EVAL_CALL`, `EXEC_CALL` |
| `LOW_RISK` | `IPV4_ADDRESS`, `INSECURE_URL`, `TODO_CREDENTIAL` |
| `OTHER` | Any unrecognized label |

---

## Module 3 — Transformation (`src/transducer.py`)

**Formal model:** Finite State Transducer (FST)

### Public Functions

#### `transduce(source: str) → TransducerOutput`

| Parameter | Type | Description |
|---|---|---|
| `source` | `str` | Original source code |

**Returns:** `TransducerOutput`

| Field | Type | Description |
|---|---|---|
| `original_source` | `str` | Unmodified input |
| `transformed_source` | `str` | Source with all rules applied |
| `transformations` | `List[TransformationResult]` | Per-line change log |
| `imports_added` | `List[str]` | Import statements prepended |
| `total_changes` | `int` | Number of lines modified |

Each `TransformationResult` has: `original_line`, `transformed_line`, `rule_applied`, `changed`.

**Example:**

```python
out = transduce('password = "admin123"\nprint(password)')
# → TransducerOutput(
#     transformed_source='import os\npassword = os.getenv("APP_PASSWORD")\n# [CHOMSKY] Sensitive output removed: print(password)',
#     total_changes=2,
#     imports_added=["import os"]
#   )
```

---

#### `get_fst_7tuple() → dict`

Returns the formal 7-tuple `(Q, Σ, Δ, δ, λ, q₀, F)` as a Python dictionary.

---

### FST Rules

| Rule Name | Input Pattern | Output |
|---|---|---|
| `REPLACE_PASSWORD` | `password = "..."` | `password = os.getenv("APP_PASSWORD")` |
| `REPLACE_SECRET` | `api_key = "..."` | `api_key = os.getenv("APP_API_KEY")` |
| `REPLACE_AWS_KEY` | `x = "AKIA..."` | `x = os.getenv("AWS_ACCESS_KEY_ID")` |
| `REMOVE_PRINT` | `print(x)` | `# [CHOMSKY] Sensitive output removed: print(x)` |
| `REMOVE_CONSOLE_LOG` | `console.log(x)` | `// [CHOMSKY] Sensitive output removed` |
| `UPGRADE_HTTP_TO_HTTPS` | `http://url` | `https://url` |
| `SECURE_CONFIG_VALUE` | `KEY=plaintext` | `KEY=${KEY}` |

---

## Module 4 — Validation (`src/validator.py`)

**Formal model:** Context-Free Grammar (CFG) via textX

### Public Functions

#### `validate(source: str) → ValidationReport`

| Parameter | Type | Description |
|---|---|---|
| `source` | `str` | Configuration file contents (.env, YAML subset, .conf) |

**Returns:** `ValidationReport`

| Field | Type | Description |
|---|---|---|
| `is_valid` | `bool` | True if the source parses against the CFG |
| `is_secure` | `bool` | True if valid AND no sensitive keys have literal values |
| `errors` | `List[ValidationError]` | Parse or security errors |
| `warnings` | `List[str]` | Non-fatal warnings (e.g. high-entropy non-sensitive values) |
| `parsed_entries` | `List[dict]` | All key-value pairs found (for display) |

**Example (secure):**

```python
report = validate("DB_PASSWORD=${SECURE_DB_PASSWORD}\nAPP_PORT=8080\n")
# → ValidationReport(is_valid=True, is_secure=True, errors=[], warnings=[])
```

**Example (insecure):**

```python
report = validate("DB_PASSWORD=admin123\n")
# → ValidationReport(is_valid=True, is_secure=False,
#     errors=[ValidationError(line=1,
#       message="Sensitive key 'DB_PASSWORD' must use ${VAR} reference.")])
```

---

#### `get_cfg_formal() → dict`

Returns the formal grammar specification including productions, terminals, non-terminals, and the Pumping Lemma justification.

---

## Module 5 — Pipeline Orchestrator (`src/pipeline.py`)

### Public Functions

#### `analyse_source(source: str, filename: str = "<stdin>") → AnalysisResult`

Runs all four stages on a source string. Config files (`.env`, `.yaml`, etc.) also trigger Stage 4.

#### `analyse_file(path: str) → AnalysisResult`

Reads a file from disk and calls `analyse_source()`.

#### `analyse_directory(directory: str, extensions: Optional[set]) → DirectoryReport`

Recursively analyzes all matching files in a directory. Returns aggregate statistics.

---

### `AnalysisResult` Fields

| Field | Type | Description |
|---|---|---|
| `filename` | `str` | Source file name |
| `source` | `str` | Original source text |
| `detections` | `List[Detection]` | Stage 1 output |
| `classification` | `ClassificationReport` | Stage 2 output |
| `transformation` | `TransducerOutput` | Stage 3 output |
| `validation` | `Optional[ValidationReport]` | Stage 4 output (None for non-config) |
| `is_config_file` | `bool` | Whether Stage 4 was triggered |
| `risk_score` | `int` (property) | 3=Violation, 2=Review, 1=Safe |
| `high_severity_count` | `int` (property) | Count of HIGH severity detections |
