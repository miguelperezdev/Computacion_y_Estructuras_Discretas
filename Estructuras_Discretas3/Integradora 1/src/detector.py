"""
Module 1 – Detection (Lexical Level)
=====================================
Uses regular expressions to detect insecure patterns in source code and config files.

Formal Language:
  Alphabet Σ = printable ASCII characters
  Each regex defines a regular language over Σ*
  Output: sequence of pattern labels (tokens) for the next stage
"""

import re
from dataclasses import dataclass
from typing import List, Tuple


# ─────────────────────────────────────────────
#  Pattern definitions  (Formal: Regular Languages)
# ─────────────────────────────────────────────

PATTERNS = {
    # AWS Access Key ID: starts with AKIA followed by 16 uppercase alphanumeric chars
    # Language: {AKIA} · ([A-Z0-9])^16
    "AWS_API_KEY": re.compile(r"AKIA[0-9A-Z]{16}"),

    # GitHub personal access token (classic): ghp_ prefix + 36 alphanumeric/underscore chars
    # Language: {ghp_} · ([A-Za-z0-9_])^36
    "GITHUB_TOKEN": re.compile(r"ghp_[A-Za-z0-9_]{36}"),

    # Hardcoded password assignment (single or double quotes)
    # Language: password\s*=\s*["'][^"']+["']
    "HARDCODED_PASSWORD": re.compile(
        r'(?i)(password|passwd|pwd)\s*=\s*["\'][^"\']{2,}["\']'
    ),

    # Hardcoded secret / token assignment
    "HARDCODED_SECRET": re.compile(
        r'(?i)(secret|api_key|access_token|auth_token)\s*=\s*["\'][^"\']{4,}["\']'
    ),

    # print() call containing a variable (potential secret leak)
    # Language: print\s*\(\s*[^)]+\)
    "PRINT_CALL": re.compile(r'\bprint\s*\([^)]*\)'),

    # console.log() call (JS)
    "CONSOLE_LOG": re.compile(r'\bconsole\.log\s*\([^)]*\)'),

    # logging.* calls that may expose secrets
    "LOGGING_CALL": re.compile(r'\blogging\.(debug|info|warning|error)\s*\([^)]*\)'),

    # IPv4 address
    # Language: (\d{1,3}\.){3}\d{1,3}
    "IPV4_ADDRESS": re.compile(r'\b(?:\d{1,3}\.){3}\d{1,3}\b'),

    # Suspicious URL (http not https)
    "INSECURE_URL": re.compile(r'\bhttp://[^\s"\']+'),

    # TODO / FIXME comment with password mention
    "TODO_CREDENTIAL": re.compile(r'#\s*(TODO|FIXME)[^\n]*(password|key|secret)', re.IGNORECASE),

    # Plain config value for sensitive keys (KEY=plaintext, no ${ } and no os.getenv)
    "PLAIN_CONFIG_VALUE": re.compile(
        r'(?i)^(db_password|db_pass|secret_key|api_key|auth_token)\s*=\s*(?!\$\{)(?!os\.getenv)[^\s#\n]+',
        re.MULTILINE
    ),

    # eval() usage (code injection risk)
    "EVAL_CALL": re.compile(r'\beval\s*\('),

    # exec() usage
    "EXEC_CALL": re.compile(r'\bexec\s*\('),

    # Hardcoded DB connection string
    "DB_CONNECTION_STRING": re.compile(
        r'(?i)(mongodb|mysql|postgres|sqlite)://[^\s"\']*:[^\s"\'@]+@'
    ),
}


@dataclass
class Detection:
    """Represents a single detected pattern match."""
    label: str          # Pattern label (e.g. HARDCODED_PASSWORD)
    match: str          # Matched string (truncated for safety)
    line: int           # Line number in source
    column: int         # Column number
    severity: str       # HIGH / MEDIUM / LOW


SEVERITY_MAP = {
    "AWS_API_KEY": "HIGH",
    "GITHUB_TOKEN": "HIGH",
    "HARDCODED_PASSWORD": "HIGH",
    "HARDCODED_SECRET": "HIGH",
    "DB_CONNECTION_STRING": "HIGH",
    "PLAIN_CONFIG_VALUE": "HIGH",
    "PRINT_CALL": "MEDIUM",
    "CONSOLE_LOG": "MEDIUM",
    "LOGGING_CALL": "MEDIUM",
    "INSECURE_URL": "MEDIUM",
    "TODO_CREDENTIAL": "LOW",
    "IPV4_ADDRESS": "LOW",
    "EVAL_CALL": "HIGH",
    "EXEC_CALL": "HIGH",
}


def detect(source: str) -> List[Detection]:
    """
    Run all regex patterns over `source` and return a list of Detection objects.
    This implements the lexical scanning phase of Chomsky.
    """
    detections: List[Detection] = []
    lines = source.splitlines()

    for label, pattern in PATTERNS.items():
        for m in pattern.finditer(source):
            # Compute line / column from match start offset
            start = m.start()
            line_no = source[:start].count('\n') + 1
            col_no = start - source[:start].rfind('\n')
            raw = m.group()
            # Truncate long matches for display
            display = raw if len(raw) <= 40 else raw[:37] + "..."
            detections.append(Detection(
                label=label,
                match=display,
                line=line_no,
                column=col_no,
                severity=SEVERITY_MAP.get(label, "LOW"),
            ))

    # Sort by line then column
    detections.sort(key=lambda d: (d.line, d.column))
    return detections


def get_labels(detections: List[Detection]) -> List[str]:
    """Return ordered list of pattern labels (token stream for DFA stage)."""
    return [d.label for d in detections]


def detect_file():
    return None


def summarize():
    return None


def token_sequence():
    return None