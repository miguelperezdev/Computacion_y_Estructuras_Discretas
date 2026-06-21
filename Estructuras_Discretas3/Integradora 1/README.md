# Chomsky – Code Repository Security Analyzer

> **Code Hazard Observation via Modeling of Syntax and KeY-patterns**

Chomsky is a formal-language-based security analyzer for source code and configuration files. It detects hardcoded credentials, classifies security risk using finite automata, proposes automatic refactoring via transducers, and validates configuration structure with context-free grammars — all grounded in formal language theory.

---

## Team Members

| Name | ID |
|---|---|
| Miguel Pérez |miguelperezdev |
| Estefany Villamarin | Villamarin17  | 

**Course:** Computación y Estructuras Discretas 3 (CyED3)  
**Institution:** Universidad ICESI  
**Semester:** 2026-1  
**IDE Used:** VSCode

---

## Project Structure

```
chomsky/
├── cli.py                    # Command-line interface (CLI)
├── web_app.py                # Flask web interface
├── requirements.txt          # Python dependencies
├── README.md                 # This file
├── src/
│   ├── __init__.py
│   ├── detector.py           # Module 1: Regular Expressions (Lexical Level)
│   ├── classifier.py         # Module 2: Finite Automaton / DFA (Behavioral Level)
│   ├── transducer.py         # Module 3: Finite State Transducer (Rewriting Level)
│   ├── validator.py          # Module 4: Context-Free Grammar / textX (Structural Level)
│   └── pipeline.py           # Orchestrates all four stages
├── tests/
│   └── test_chomsky.py       # Unit + integration tests (pytest)
├── samples/
│   ├── insecure_example.py   # Python file with intentional vulnerabilities
│   ├── secure_example.py     # Python file with secure patterns
│   ├── insecure.env          # Config file with plaintext secrets
│   ├── secure.env            # Config file with env-variable references
│   ├── mixed_scenario.py     # Mixed scenario with multiple vulnerability types
│   └── nested_config.conf    # Config with nested sections
└── docs/
    ├── DESIGN.md             # Full formal design (5-tuples, 7-tuple, EBNF)
    ├── MODULE_DESIGN.md      # Module-level inputs, outputs, and function signatures
    ├── TEST_CASES.md         # Formal test case specifications and scenarios
    └── LITERATURE_REVIEW.md  # Academic literature review
```

---

## Setup & Installation

### Requirements
- Python 3.9 or higher
- pip

### Install dependencies

```bash
pip install -r requirements.txt
```

Or manually:

```bash
pip install flask pyformlang textx pytest
```

> **Note:** `pyformlang` and `textX` are optional. Chomsky gracefully falls back to hand-written implementations if they are not installed.

---

## Usage

### Run the Web Interface (recommended)

```bash
python web_app.py
```

Then open your browser at: **http://localhost:5000**

The web interface allows you to:
- Paste code or config files for analysis
- See detection results with severity levels
- View the DFA transition trace step-by-step
- See transformation suggestions (diff view)
- Validate configuration files against the secure grammar

### Run the CLI

```bash
# Analyze a single file
python cli.py samples/insecure_example.py

# Analyze a directory
python cli.py samples/

# Show refactored source
python cli.py samples/insecure_example.py --refactored

# JSON output (for integration)
python cli.py samples/insecure_example.py --json

# Show formal model definitions (5-tuple, 7-tuple, EBNF)
python cli.py --formal

# Run the built-in demo
python cli.py --demo
```

### Run Tests

```bash
cd tests
python -m pytest test_chomsky.py -v
```

---

## Pipeline Stages

The Chomsky pipeline processes source code through four formal language stages:

| Stage | Formal Model | Input | Output | Question Answered |
|---|---|---|---|---|
| 1. Detection | Regular Expressions | Source code (string) | `List[Detection]` with labels | Does this file contain known insecure patterns? |
| 2. Classification | Deterministic Finite Automaton (DFA) | Token label sequence | Classification + state trace | Does the event sequence constitute a security violation? |
| 3. Transformation | Finite State Transducer (FST) | Source code (string) | Refactored source + diff | How can insecure patterns be systematically rewritten? |
| 4. Validation | Context-Free Grammar (CFG) via textX | Config file (string) | Validation report | Does the config satisfy the secure grammar? |

---

## Formal Models Summary

### Module 1 — Regular Languages

Each detection pattern defines a **regular language** over the ASCII alphabet Σ. Key patterns:

| Pattern | Regex | Description |
|---|---|---|
| `AWS_API_KEY` | `AKIA[0-9A-Z]{16}` | AWS access key format |
| `HARDCODED_PASSWORD` | `(?i)(password\|passwd\|pwd)\s*=\s*["'][^"']{2,}["']` | Password assigned a literal string |
| `GITHUB_TOKEN` | `ghp_[A-Za-z0-9_]{36}` | GitHub personal access token |
| `IPV4_ADDRESS` | `\b(?:\d{1,3}\.){3}\d{1,3}\b` | Dotted-decimal IPv4 address |
| `INSECURE_URL` | `\bhttp://[^\s"']+` | URL using plain HTTP |

### Module 2 — DFA (5-tuple)

```
M = (Q, Σ, δ, q₀, F)

Q  = { q0_start, q1_has_credential, q2_has_print_only, q3_violation, q4_needs_review }
Σ  = { CREDENTIAL, LEAK, HIGH_RISK, LOW_RISK, OTHER }
q₀ = q0_start
F  = Q  (all states are accepting; classification depends on which state is final)
```

Final state → classification:
- `q0_start` → **Safe**
- `q1_has_credential` → **Needs Review**
- `q2_has_print_only` → **Needs Review**
- `q3_violation` → **Security Violation**
- `q4_needs_review` → **Needs Review**

### Module 3 — FST (7-tuple)

```
T = (Q, Σ, Δ, δ, λ, q₀, F)

Q  = { q_start, q_applying_rule, q_done }
Σ  = Lines of source code (strings over ASCII)
Δ  = Transformed lines of source code
q₀ = q_start
F  = { q_done }
```

### Module 4 — Context-Free Grammar (EBNF)

```ebnf
Config       ::= Section* Entry*
Section      ::= ID '{' Entry* Section* '}'    (* recursive — not regular! *)
Entry        ::= Key '=' Value ';'
Key          ::= SensitiveKey | RegularKey
SensitiveKey ::= 'password' | 'secret' | 'api_key' | 'token' | ...
Value        ::= EnvReference | QuotedString | Number | Bool | PlainID
EnvReference ::= '${' ID '}'
```

**Why not regular?** The `Section` production is recursive, enabling arbitrarily nested braces. By the Pumping Lemma, no DFA can track unbounded nesting depth. A CFG is required.

---

## Example Output

```
╔══════════════════════════════════════════════════════════╗
║         C H O M S K Y   Security Analyzer  v1.0          ║
╚══════════════════════════════════════════════════════════╝

File: insecure_example.py

[1] DETECTION — Regular Expressions
  [HIGH]   Line   6 | HARDCODED_PASSWORD        | password = "admin123"
  [HIGH]   Line   7 | AWS_API_KEY               | AKIA1234567890ABCDE
  [HIGH]   Line   8 | DB_CONNECTION_STRING       | postgres://admin:hunter2@...
  [MEDIUM] Line  13 | PRINT_CALL                | print(password)

[2] CLASSIFICATION — Finite Automaton
  Result : Security Violation
  State  : q3_violation
  Trace  : q0_start → q1_has_credential → q3_violation

[3] TRANSFORMATION — Finite State Transducer
  2 transformation(s) applied:
  [-] password = "admin123"
  [+] password = os.getenv("APP_PASSWORD")
    Rule: REPLACE_PASSWORD
  [-] print(password)
  [+] # [CHOMSKY] Sensitive output removed: print(password)
    Rule: REMOVE_PRINT

[4] VALIDATION — (not applicable for .py files)
```

---

## License

Academic project — Universidad ICESI, CyED3, 2026-1. All rights reserved by the authors.