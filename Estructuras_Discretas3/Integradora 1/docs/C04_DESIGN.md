# Design Document

## Chomsky — Code Repository Security Analyzer

**Full formal specification: 5-tuple DFA, 7-tuple FST, EBNF CFG, and transition diagrams.**

---

## 1. System Architecture

```
Source Code / Config File (string over ASCII alphabet)
         │
         ▼
  ┌─────────────────────────────────────────────┐
  │          Stage 1 — DETECTION                │
  │          Formal model: Regular Languages     │
  │          Tool: Python re module             │
  │          Input:  str                        │
  │          Output: List[Detection] + labels   │
  └──────────────────┬──────────────────────────┘
                     │ token stream (List[str])
                     ▼
  ┌─────────────────────────────────────────────┐
  │          Stage 2 — CLASSIFICATION           │
  │          Formal model: DFA                  │
  │          Tool: hand-coded + pyformlang      │
  │          Input:  List[str] (labels)         │
  │          Output: ClassificationReport       │
  └──────────────────┬──────────────────────────┘
                     │ (source also passed directly)
                     ▼
  ┌─────────────────────────────────────────────┐
  │          Stage 3 — TRANSFORMATION           │
  │          Formal model: FST                  │
  │          Tool: Python re (rule-based)       │
  │          Input:  str (original source)      │
  │          Output: TransducerOutput           │
  └──────────────────┬──────────────────────────┘
                     │ (config files only)
                     ▼
  ┌─────────────────────────────────────────────┐
  │          Stage 4 — VALIDATION               │
  │          Formal model: CFG                  │
  │          Tool: textX / hand-written parser  │
  │          Input:  str (config file)          │
  │          Output: ValidationReport           │
  └─────────────────────────────────────────────┘
```

---

## 2. Module 1 — Detection (Regular Expressions)

### Objective

Extract and label insecure patterns from source code at the **lexical level**. The output is a sequence of abstract symbols (token labels) that serves as the input alphabet for the DFA in Stage 2.

### Formal Language Definition

**Alphabet:** Σ = printable ASCII characters (U+0020 to U+007E plus `\n`, `\t`)

Each pattern defines a **regular language** `L ⊆ Σ*`. The detector computes the union of all pattern matches:

```
L_detect = L_AWS ∪ L_PASSWORD ∪ L_GITHUB ∪ L_PRINT ∪ ... ⊆ Σ*
```

Since regular languages are closed under union, `L_detect` is also regular.

### Key Pattern Definitions

| Pattern | Regular Expression | Formal Description |
|---|---|---|
| `AWS_API_KEY` | `AKIA[0-9A-Z]{16}` | `{AKIA} · ([A-Z0-9])^{16}` — finite, trivially regular |
| `HARDCODED_PASSWORD` | `(?i)(password\|passwd\|pwd)\s*=\s*["'][^"']{2,}["']` | Assignment of 2+ char quoted string to a password keyword |
| `GITHUB_TOKEN` | `ghp_[A-Za-z0-9_]{36}` | `{ghp_} · ([A-Za-z0-9_])^{36}` |
| `PRINT_CALL` | `\bprint\s*\([^)]*\)` | `print` followed by balanced-free argument list |
| `IPV4_ADDRESS` | `\b(?:\d{1,3}\.){3}\d{1,3}\b` | Dotted-decimal format — `(\d{1,3}\.){3}\d{1,3}` |
| `INSECURE_URL` | `\bhttp://[^\s"']+` | HTTP (not HTTPS) URL |

### Input / Output

- **Input:** Source code string (str ∈ Σ*)
- **Output:** `List[Detection]` — each item carries: `label`, `match`, `line`, `column`, `severity`
- **Token stream for Stage 2:** `List[str]` of labels, derived by `get_labels(detections)`

---

## 3. Module 2 — Classification (Finite Automaton / DFA)

### Objective

Classify the sequence of pattern labels produced by Stage 1. The DFA reads the abstract token stream and transitions between states. Its final state determines the security classification.

### Formal Definition — DFA 5-tuple

```
M = (Q, Σ, δ, q₀, F)
```

**Q — Set of states (5 states):**
```
Q = { q0_start,
      q1_has_credential,
      q2_has_print_only,
      q3_violation,
      q4_needs_review }
```

**Σ — Input alphabet (5 symbols):**
```
Σ = { CREDENTIAL, LEAK, HIGH_RISK, LOW_RISK, OTHER }
```

Each detector label is abstracted to a symbol in Σ:
- `CREDENTIAL` ← HARDCODED_PASSWORD, AWS_API_KEY, GITHUB_TOKEN, DB_CONNECTION_STRING, ...
- `LEAK`       ← PRINT_CALL, CONSOLE_LOG, LOGGING_CALL
- `HIGH_RISK`  ← EVAL_CALL, EXEC_CALL
- `LOW_RISK`   ← IPV4_ADDRESS, INSECURE_URL, TODO_CREDENTIAL
- `OTHER`      ← anything else

**q₀ — Initial state:**
```
q₀ = q0_start
```

**F — Final states:**
```
F = Q  (all states are accepting)
```
Classification is determined by **which** final state is reached, not by accept/reject.

**δ — Transition function (complete table):**

| State | CREDENTIAL | LEAK | HIGH_RISK | LOW_RISK | OTHER |
|---|---|---|---|---|---|
| q0_start | q1_has_credential | q2_has_print_only | q4_needs_review | q4_needs_review | q0_start |
| q1_has_credential | q1_has_credential | **q3_violation** | **q3_violation** | q1_has_credential | q1_has_credential |
| q2_has_print_only | **q3_violation** | q2_has_print_only | q4_needs_review | q2_has_print_only | q2_has_print_only |
| q3_violation | q3_violation | q3_violation | q3_violation | q3_violation | q3_violation |
| q4_needs_review | **q3_violation** | q4_needs_review | q4_needs_review | q4_needs_review | q4_needs_review |

**State → Classification mapping:**

| State | Classification | Meaning |
|---|---|---|
| q0_start | **Safe** | No patterns detected |
| q1_has_credential | **Needs Review** | Credential found, not yet leaked |
| q2_has_print_only | **Needs Review** | Print/log found, no credential yet |
| q3_violation | **Security Violation** | Credential + leak detected |
| q4_needs_review | **Needs Review** | High-risk construct (eval/exec) |

### Transition Diagram

```
                   CREDENTIAL                  LEAK
  ┌──────────┐ ──────────────► ┌────────────────┐ ─────────────► ┌─────────────┐
  │ q0_start │                 │q1_has_cred     │               │ q3_violation │◄─┐
  │  (Safe)  │ ──LEAK──────► ┌►├────────────────┤               │(Sec.Violat.) │  │
  └──────────┘               │ │q2_has_print    │──CREDENTIAL──►└─────────────┘  │
       │                     │ │(Needs Review)  │                      ▲          │
       │ HIGH_RISK            │ └────────────────┘                     │          │
       │ LOW_RISK             │                                        │CREDENTIAL │
       ▼                     │                                        │          │
  ┌────────────┐             │                                ┌───────────────┐  │
  │ q4_needs_  │─────────────┘                               │q4_needs_review│──┘
  │   review   │◄──────── HIGH_RISK / LOW_RISK ──────────────│(Needs Review) │
  │(Needs Rev.)│                                              └───────────────┘
  └────────────┘
  
  Note: q3_violation is ABSORBING — all symbols from q3 loop back to q3.
```

### Execution Example

```
Input labels: ["HARDCODED_PASSWORD", "PRINT_CALL"]
Abstracted:   ["CREDENTIAL",         "LEAK"]

q0_start --CREDENTIAL--> q1_has_credential --LEAK--> q3_violation

Final state: q3_violation → Classification: Security Violation
```

---

## 4. Module 3 — Transformation (Finite State Transducer / FST)

### Objective

Automatically rewrite insecure code patterns into safer alternatives. The FST maps input strings (source code lines) to output strings (refactored lines).

### Formal Definition — FST 7-tuple

```
T = (Q, Σ, Δ, δ, λ, q₀, F)
```

**Q — States:**
```
Q = { q_start, q_applying_rule, q_done }
```

**Σ — Input alphabet:**
```
Σ = Lines of source code (strings over printable ASCII)
```

**Δ — Output alphabet:**
```
Δ = Transformed lines of source code (strings over printable ASCII)
```

**q₀ — Initial state:**
```
q₀ = q_start
```

**F — Final states:**
```
F = { q_done }
```

**δ × λ — Transition and output functions:**

The FST processes source code **line by line**. For each line, it tries each rule in order:

| Rule (δ) | Input Pattern (Σ) | Output (Δ = λ) | Transition |
|---|---|---|---|
| `REPLACE_PASSWORD` | `password = "..."` | `password = os.getenv("APP_PASSWORD")` | q_start → q_applying_rule → q_start |
| `REPLACE_SECRET` | `api_key = "..."` | `api_key = os.getenv("APP_API_KEY")` | q_start → q_applying_rule → q_start |
| `REPLACE_AWS_KEY` | `x = "AKIA..."` | `x = os.getenv("AWS_ACCESS_KEY_ID")` | q_start → q_applying_rule → q_start |
| `REMOVE_PRINT` | `print(x)` | `# [CHOMSKY] Sensitive output removed` | q_start → q_applying_rule → q_start |
| `REMOVE_CONSOLE_LOG` | `console.log(x)` | `// [CHOMSKY] Sensitive output removed` | q_start → q_applying_rule → q_start |
| `UPGRADE_HTTP_TO_HTTPS` | `http://url` | `https://url` | q_start → q_applying_rule → q_start |
| `SECURE_CONFIG_VALUE` | `KEY=plaintext` | `KEY=${KEY}` | q_start → q_applying_rule → q_start |
| *(no match)* | any line | *(unchanged)* | q_start → q_start |

After all lines are processed: q_start → q_done.

### FST Transition Diagram

```
                  line matches rule r                   line unchanged
  ┌─────────┐ ─────────────────────────► ┌──────────────┐ ──────────► ┌─────────┐
  │ q_start │                            │q_applying_   │             │ q_start │
  │         │◄───────────────────────────│   rule       │             │ (next   │
  └─────────┘   output: λ(r, line)       └──────────────┘             │  line)  │
       │                                                               └─────────┘
       │ (all lines processed)
       ▼
  ┌─────────┐
  │ q_done  │
  └─────────┘
```

---

## 5. Module 4 — Validation (Context-Free Grammar / CFG)

### Objective

Validate the hierarchical structure of configuration files. Unlike flat key=value matching (which is regular), configuration files may have arbitrarily nested sections — requiring a CFG.

### Formal Definition — CFG in EBNF

```ebnf
Config       ::= Section* Entry*

Section      ::= ID '{' Entry* Section* '}'   (* RECURSIVE production *)
                                               (* enables arbitrary nesting depth *)
Entry        ::= Key '=' Value ';'

Key          ::= SensitiveKey | RegularKey

SensitiveKey ::= 'password' | 'passwd' | 'secret'
               | 'api_key' | 'access_token' | 'auth_token'
               | 'db_pass' | 'db_password'

RegularKey   ::= ID

Value        ::= EnvReference | QuotedString | NumberVal | BoolVal | PlainVal

EnvReference ::= '${' ID '}'

QuotedString ::= '"' .* '"'

NumberVal    ::= [0-9]+

BoolVal      ::= 'true' | 'false'

PlainVal     ::= ID
```

**Terminals:** `{`, `}`, `=`, `;`, `${`, `}`, `"`, `true`, `false`, `password`, `secret`, ..., ID, INT, STRING

**Non-terminals:** Config, Section, Entry, Key, SensitiveKey, RegularKey, Value, EnvReference, QuotedString, NumberVal, BoolVal, PlainVal

**Start symbol:** Config

### Security Constraint

If a `Key` is classified as `SensitiveKey`, its `Value` **MUST** be an `EnvReference` (`${IDENTIFIER}`). Any other value type for a sensitive key is a security policy violation.

### Why This Language Is NOT Regular

**Claim:** The language of valid configurations with balanced nested sections is not regular.

**Proof sketch (by Pumping Lemma):**

Assume for contradiction that L is regular. Then there exists a pumping length p.

Consider the string `s = A^p { B = ${X}; }` where A^p is a section name with p characters.

By the Pumping Lemma, s = xyz where |xy| ≤ p and |y| ≥ 1. Pumping y gives strings with section names of different lengths — but more critically, consider a string with p nested sections:

```
S₁ { S₂ { ... Sₚ { key = ${VAR}; } ... } }
```

The string has p opening braces that must match p closing braces. The Pumping Lemma would require us to pump the prefix to create a string with a different number of opening braces than closing braces — which is not in L. This contradicts the assumption that L is regular.

Therefore, L is **context-free but not regular**, and a CFG is required.

### Parse Example

**Valid (secure) input:**
```
database {
    host = localhost;
    db_password = ${DB_PASS};
}
```

**Parse tree:**
```
Config
  └── Section("database")
        ├── Entry(RegularKey("host"), PlainVal("localhost"))
        └── Entry(SensitiveKey("db_password"), EnvReference("DB_PASS"))
                                                ↑ VALID: env reference
```

**Invalid (insecure) input:**
```
database {
    db_password = admin123;
}
```
**Error:** `Sensitive key 'db_password' must use ${VAR} reference. Got: admin123`

---

## 6. Module Interaction Diagram

```
Source Code / Config File (str)
         │
         ├──────────────────────────────────────────────────────┐
         │                                                      │
         ▼                                                      ▼
  [ Stage 1: detect() ]                               [ Stage 3: transduce() ]
  Regular Expressions                                 Finite State Transducer
  Output: List[Detection]                             Output: TransducerOutput
         │                                                      │
         │ get_labels(detections)                               │
         ▼                                                      │
  [ Stage 2: classify() ]                                       │
  DFA over token stream                                         │
  Output: ClassificationReport                                  │
                                                               │
  (config files only)                                          │
         │                                                      │
         ▼                                                      │
  [ Stage 4: validate() ]                                       │
  CFG / textX parser                                            │
  Output: ValidationReport                                      │
         │                                                      │
         └──────────────────── AnalysisResult ──────────────────┘
                          (all four outputs combined)
```
