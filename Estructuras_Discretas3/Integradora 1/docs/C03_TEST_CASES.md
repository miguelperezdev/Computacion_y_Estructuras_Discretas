# Test Cases Document

## Chomsky — Code Repository Security Analyzer

**Formal specification of all test scenarios, expected inputs, and expected outputs per module.**

---

## Test Scenarios Overview

| ID | Scenario | File Type | Expected Classification | Stage 4? |
|---|---|---|---|---|
| TC-01 | Full Security Violation (credential + print) | `.py` | Security Violation | No |
| TC-02 | Safe Code (env vars + HTTPS) | `.py` | Safe | No |
| TC-03 | Needs Review (credential, no leak) | `.py` | Needs Review | No |
| TC-04 | Needs Review (eval/exec only) | `.py` | Needs Review | No |
| TC-05 | Mixed Scenario (multiple violations) | `.py` | Security Violation | No |
| TC-06 | Secure Configuration File | `.env` | Safe | ✓ Secure |
| TC-07 | Insecure Configuration File | `.env` | Needs Review | ✓ Violation |
| TC-08 | Nested Secure Config | `.conf` | Safe | ✓ Secure |
| TC-09 | Nested Insecure Config | `.conf` | Needs Review | ✓ Violation |
| TC-10 | JavaScript with console.log leak | `.js` | Security Violation | No |
| TC-11 | AWS Key without print (Needs Review) | `.py` | Needs Review | No |
| TC-12 | DB connection string + logging leak | `.py` | Security Violation | No |
| TC-13 | Unbalanced braces in config | `.conf` | — | ✗ Invalid |
| TC-14 | Empty file | `.py` | Safe | No |
| TC-15 | Comments only | `.py` | Safe | No |

---

## Detailed Test Case Specifications

---

### TC-01 — Full Security Violation

**Description:** A Python file with a hardcoded password assigned and then printed. This is the canonical security violation.

**Input:**
```python
password = "admin123"
print(password)
```

**Stage 1 — Detection:**
| Label | Line | Severity |
|---|---|---|
| `HARDCODED_PASSWORD` | 1 | HIGH |
| `PRINT_CALL` | 2 | MEDIUM |

**Stage 2 — DFA Trace:**
```
q0_start --CREDENTIAL--> q1_has_credential --LEAK--> q3_violation
```
**Result:** `Security Violation`

**Stage 3 — Transformation:**
```diff
- password = "admin123"
+ password = os.getenv("APP_PASSWORD")
- print(password)
+ # [CHOMSKY] Sensitive output removed: print(password)
```
**Rule 1:** `REPLACE_PASSWORD` | **Rule 2:** `REMOVE_PRINT`  
**Imports added:** `import os`

**Stage 4:** Not applicable (`.py` file)

---

### TC-02 — Safe Code

**Description:** A Python file using environment variables and HTTPS. No patterns should be detected.

**Input:**
```python
import os
password = os.getenv("APP_PASSWORD")
api_key = os.getenv("AWS_ACCESS_KEY_ID")
url = "https://secure.api.corp.com/endpoint"
```

**Stage 1 — Detection:** No detections.

**Stage 2 — DFA Trace:**
```
q0_start  (no input consumed → stays at q0_start)
```
**Result:** `Safe`

**Stage 3 — Transformation:** No changes. `total_changes = 0`

**Stage 4:** Not applicable.

---

### TC-03 — Needs Review (Credential, No Leak)

**Description:** Hardcoded API key stored but never printed or logged.

**Input:**
```python
api_key = "AKIA1234567890ABCDE"
response = requests.get("https://api.corp.com/data",
    headers={"Authorization": api_key})
```

**Stage 1 — Detection:**
| Label | Line | Severity |
|---|---|---|
| `AWS_API_KEY` | 1 | HIGH |

**Stage 2 — DFA Trace:**
```
q0_start --CREDENTIAL--> q1_has_credential
```
**Result:** `Needs Review`

**Stage 3 — Transformation:**
```diff
- api_key = "AKIA1234567890ABCDE"
+ api_key = os.getenv("AWS_ACCESS_KEY_ID")
```

---

### TC-04 — Needs Review (eval/exec Only)

**Description:** Use of `eval()` without any credential — dangerous but not a credential leak.

**Input:**
```python
user_cmd = input("Enter command: ")
result = eval(user_cmd)
exec(user_cmd)
```

**Stage 1 — Detection:**
| Label | Line | Severity |
|---|---|---|
| `EVAL_CALL` | 2 | HIGH |
| `EXEC_CALL` | 3 | HIGH |

**Stage 2 — DFA Trace:**
```
q0_start --HIGH_RISK--> q4_needs_review --HIGH_RISK--> q4_needs_review
```
**Result:** `Needs Review`

---

### TC-05 — Mixed Scenario (Multiple Violations)

**Description:** File with multiple credential types, print leaks, eval, and insecure URLs.

**Input:**
```python
password = "hunter2"
api_key = "AKIA1234567890ABCDE"
stripe = "sk_live_4eC39HqLyjWDarjtT1zdp7dc"
print(password)
print(api_key)
result = eval(user_input)
url = "http://internal.corp.com/api"
```

**Stage 1 — Detection (expected):**
- `HARDCODED_PASSWORD` (HIGH)
- `AWS_API_KEY` (HIGH)
- `STRIPE_KEY` (HIGH)
- `PRINT_CALL` × 2 (MEDIUM)
- `EVAL_CALL` (HIGH)
- `INSECURE_URL` (MEDIUM)

**Stage 2 — Result:** `Security Violation`

**Stage 3:** 5+ transformations applied.

---

### TC-06 — Secure Configuration File

**Input (`secure.env`):**
```
APP_NAME=chomsky
APP_PORT=8080
DB_PASSWORD=${SECURE_DB_PASSWORD}
SECRET_KEY=${APP_SECRET_KEY}
API_KEY=${EXTERNAL_API_KEY}
```

**Stage 1 — Detection:** `PLAIN_CONFIG_VALUE` may fire on non-sensitive keys → LOW.  
**Stage 2 — Result:** `Safe` or `Needs Review` (only LOW_RISK detected)  
**Stage 4 — CFG Validation:**
- `is_valid = True`
- `is_secure = True`
- No errors

---

### TC-07 — Insecure Configuration File

**Input (`insecure.env`):**
```
DB_PASSWORD=admin123
SECRET_KEY=mysupersecretkey_do_not_share
API_KEY=abc123plaintext
```

**Stage 4 — CFG Validation:**
- `is_valid = True` (syntactically correct)
- `is_secure = False`
- `errors`: 3 errors, one per sensitive key with literal value

**Expected error messages:**
- `Sensitive key 'DB_PASSWORD' must use ${VAR} reference.`
- `Sensitive key 'SECRET_KEY' must use ${VAR} reference.`
- `Sensitive key 'API_KEY' must use ${VAR} reference.`

---

### TC-08 — Nested Secure Config

**Input (`nested_config.conf`):**
```
database {
    host = localhost;
    credentials {
        db_password = ${DB_PASS};
        auth_token = ${AUTH_TOKEN};
    }
}
```

**Stage 4 — CFG Validation:**
- `is_valid = True` (recursive Section production handles nesting)
- `is_secure = True`
- No errors

---

### TC-09 — Nested Insecure Config

**Input:**
```
api {
    auth {
        api_key = plaintext_key_here;
    }
}
```

**Stage 4 — CFG Validation:**
- `is_valid = True`
- `is_secure = False`
- Error: `Sensitive key 'api_key' must use ${VAR} reference.`

---

### TC-10 — JavaScript with console.log Leak

**Input (`script.js`):**
```javascript
const password = "admin123";
console.log(password);
```

**Stage 1:**
| Label | Line | Severity |
|---|---|---|
| `HARDCODED_PASSWORD` | 1 | HIGH |
| `CONSOLE_LOG` | 2 | MEDIUM |

**Stage 2 — DFA Trace:**
```
q0_start --CREDENTIAL--> q1_has_credential --LEAK--> q3_violation
```
**Result:** `Security Violation`

---

### TC-13 — Unbalanced Braces in Config

**Input:**
```
database {
    host = localhost;
    db_password = ${DB_PASS};
```
*(Missing closing brace)*

**Stage 4 — CFG Validation:**
- `is_valid = False`
- Error: `Unbalanced braces: 1 unclosed section(s).`

---

### TC-14 — Empty File

**Input:** `""` (empty string)

**Stage 1:** No detections.  
**Stage 2:** `q0_start` → `Safe`  
**Stage 3:** No changes.  
**Stage 4:** Skipped (not a config file if `.py` extension).

---

## DFA State Coverage Matrix

The test cases above cover all 5 DFA states:

| State Reached | Test Cases |
|---|---|
| `q0_start` (Safe) | TC-02, TC-14, TC-15 |
| `q1_has_credential` (Needs Review) | TC-03, TC-11 |
| `q2_has_print_only` (Needs Review) | (print before credential) |
| `q3_violation` (Security Violation) | TC-01, TC-05, TC-10, TC-12 |
| `q4_needs_review` (Needs Review) | TC-04 |

All 5 DFA states are reachable and tested, and the absorbing property of `q3_violation` is verified in TC-05 (multiple patterns after violation still remain in `q3_violation`).
