"""
Module 4 – Validation (Structural Level)
=========================================
Context-Free Grammar (CFG) for secure configuration files,
implemented with textX.

Grammar (EBNF) – semicolon is optional:

  Config       ::= Section*
  Section      ::= IDENTIFIER '{' Entry* Section* '}'
  Entry        ::= Key '=' Value [';']?
  Key          ::= SensitiveKey | RegularKey
  SensitiveKey ::= 'password' | 'secret' | 'api_key' | 'token' | 'db_pass'
  RegularKey   ::= IDENTIFIER
  Value        ::= EnvReference | QuotedString | Number | Identifier
  EnvReference ::= '${' IDENTIFIER '}'
  QuotedString ::= '"' .* '"'
  Number       ::= [0-9]+
  Identifier   ::= [A-Za-z_][A-Za-z0-9_]*

Why is this NOT a Regular Language?
  The grammar requires balanced/nested braces: Section '{' ... Section '{' ... '}' ... '}'.
  Arbitrarily deep nesting means the language cannot be expressed by any finite automaton
  (by the Pumping Lemma for regular languages). A context-free grammar is required because
  it can express recursive, hierarchically nested structures.

Sensitive key constraint:
  If Key is a SensitiveKey, then Value MUST be an EnvReference (${VAR}).
  Assigning a literal string to a sensitive key constitutes a security violation.
"""

from dataclasses import dataclass, field
from typing import List, Optional, Tuple
import re

# ─────────────────────────────────────────────
#  textX grammar (as a string) – semicolon optional
# ─────────────────────────────────────────────

TEXTX_GRAMMAR = r"""
Config:
    sections*=Section
    entries*=Entry
;

Section:
    name=ID '{' entries*=Entry sections*=Section '}'
;

Entry:
    key=Key '=' value=Value [';']?
;

Key:
    SensitiveKey | RegularKey
;

SensitiveKey:
    name=/(password|passwd|secret|api_key|access_token|auth_token|db_pass|db_password)/
;

RegularKey:
    name=ID
;

Value:
    EnvReference | QuotedString | NumberVal | BoolVal | PlainVal
;

EnvReference:
    '${' name=ID '}'
;

QuotedString:
    value=STRING
;

NumberVal:
    value=INT
;

BoolVal:
    value=/true|false/
;

PlainVal:
    value=ID
;
"""


# ─────────────────────────────────────────────
#  Validation result
# ─────────────────────────────────────────────

@dataclass
class ValidationError:
    line: int
    message: str
    entry_key: str


@dataclass
class ValidationReport:
    is_valid: bool
    is_secure: bool
    errors: List[ValidationError]
    warnings: List[str]
    parsed_entries: List[dict]


# ─────────────────────────────────────────────
#  Sensitive key names
# ─────────────────────────────────────────────

SENSITIVE_KEYS = {
    "password", "passwd", "secret", "api_key",
    "access_token", "auth_token", "db_pass", "db_password",
}


# ─────────────────────────────────────────────
#  Fallback hand-written parser (when textX not installed)
#  Supports both with and without semicolon, and flat .env files (no sections)
# ─────────────────────────────────────────────

_ENTRY_RE = re.compile(
    r'^(?P<key>[A-Za-z_][A-Za-z0-9_]*)\s*=\s*(?P<value>[^\n#]+?)\s*;?\s*$',
    re.MULTILINE
)
_ENV_REF_RE = re.compile(r'^\$\{[A-Za-z_][A-Za-z0-9_]*\}$')
_SECTION_OPEN  = re.compile(r'^(?P<name>[A-Za-z_]\w*)\s*\{')
_SECTION_CLOSE = re.compile(r'^\}')


def _validate_handwritten(source: str) -> ValidationReport:
    """
    Handwritten recursive-descent validator for the secure config language.
    Supports nested sections (recursive production) and validates sensitive keys.
    Also works for flat .env files (no braces at all).
    """
    errors: List[ValidationError] = []
    warnings: List[str] = []
    parsed: List[dict] = []

    lines = source.splitlines()
    brace_depth = 0
    section_stack: List[str] = []

    for lineno, raw_line in enumerate(lines, start=1):
        line = raw_line.strip()

        # Skip blanks and comments
        if not line or line.startswith('#'):
            continue

        # Section open: name {
        m_open = _SECTION_OPEN.match(line)
        if m_open:
            brace_depth += 1
            section_stack.append(m_open.group('name'))
            continue

        # Section close: }
        if _SECTION_CLOSE.match(line):
            if brace_depth == 0:
                errors.append(ValidationError(lineno, "Unmatched closing brace '}'", ""))
            else:
                brace_depth -= 1
                section_stack.pop() if section_stack else None
            continue

        # Key = Value (optional semicolon)
        m_entry = _ENTRY_RE.match(line)
        if m_entry:
            key = m_entry.group('key').strip()
            raw_value = m_entry.group('value').strip()
            # Remove possible trailing semicolon (already handled by regex but safe)
            if raw_value.endswith(';'):
                raw_value = raw_value[:-1].strip()
            value = raw_value.strip('"\'')
            section_ctx = '.'.join(section_stack) if section_stack else 'global'

            entry = {"key": key, "value": raw_value, "section": section_ctx, "line": lineno}
            parsed.append(entry)

            if key.lower() in SENSITIVE_KEYS:
                # Sensitive key MUST use env reference  ${ VAR }
                if not _ENV_REF_RE.match(raw_value):
                    errors.append(ValidationError(
                        lineno,
                        f"Sensitive key '{key}' must use an environment variable "
                        f"reference (${{VAR}}) but got literal value '{raw_value[:30]}'.",
                        key,
                    ))
            else:
                # Non-sensitive keys: warn if value looks like a secret (long alphanumeric)
                if re.search(r'[A-Za-z0-9]{20,}', raw_value):
                    warnings.append(
                        f"Line {lineno}: Key '{key}' has a long value that may be a secret. "
                        f"Consider using an env reference."
                    )
        else:
            errors.append(ValidationError(lineno, f"Cannot parse line: '{line[:60]}'", ""))

    # Check balanced braces
    if brace_depth != 0:
        errors.append(ValidationError(
            len(lines),
            f"Unbalanced braces: {brace_depth} unclosed section(s).",
            "",
        ))

    return ValidationReport(
        is_valid=len(errors) == 0,
        is_secure=len(errors) == 0,
        errors=errors,
        warnings=warnings,
        parsed_entries=parsed,
    )


def validate(source: str) -> ValidationReport:
    """
    Validate a configuration file against the secure CFG.
    Tries textX first; falls back to the hand-written parser.
    """
    try:
        from textx import metamodel_from_string, TextXSyntaxError

        mm = metamodel_from_string(TEXTX_GRAMMAR)
        try:
            model = mm.model_from_string(source)
        except TextXSyntaxError as e:
            return ValidationReport(
                is_valid=False,
                is_secure=False,
                errors=[ValidationError(getattr(e, 'line', 0), str(e), "")],
                warnings=[],
                parsed_entries=[],
            )

        # Post-parse security check: sensitive keys must be env references
        errors: List[ValidationError] = []
        parsed: List[dict] = []

        def check_entries(entries, section_name="global"):
            for entry in entries:
                key_obj = entry.key
                val_obj = entry.value
                key_name = getattr(key_obj, 'name', str(key_obj))
                # Get line number if available (textX may not provide line, fallback 0)
                line_no = getattr(entry, '_tx_position', 0)
                parsed.append({"key": key_name, "value": str(val_obj), "section": section_name, "line": line_no})

                if key_name.lower() in SENSITIVE_KEYS:
                    if not hasattr(val_obj, '__class__') or val_obj.__class__.__name__ != 'EnvReference':
                        errors.append(ValidationError(
                            line_no,
                            f"Sensitive key '{key_name}' must use ${{VAR}} reference.",
                            key_name,
                        ))

        def walk(model_obj, section="global"):
            if hasattr(model_obj, 'entries'):
                check_entries(model_obj.entries, section)
            if hasattr(model_obj, 'sections'):
                for sec in model_obj.sections:
                    walk(sec, getattr(sec, 'name', 'section'))

        walk(model)

        return ValidationReport(
            is_valid=True,
            is_secure=len(errors) == 0,
            errors=errors,
            warnings=[],
            parsed_entries=parsed,
        )

    except ImportError:
        # textX not available — use hand-written parser
        return _validate_handwritten(source)


def get_cfg_formal() -> dict:
    """Return the formal BNF/EBNF grammar and justification."""
    return {
        "grammar_notation": "EBNF",
        "start_symbol": "Config",
        "terminals": [
            "'{' | '}'", "'=' | ';' (optional)", "'${' | '}'",
            "STRING", "INT", "ID", "true | false",
            "password | secret | api_key | ...",
        ],
        "non_terminals": [
            "Config", "Section", "Entry", "Key",
            "SensitiveKey", "RegularKey", "Value",
            "EnvReference", "QuotedString", "NumberVal", "BoolVal", "PlainVal",
        ],
        "productions": [
            "Config       → Section* Entry*",
            "Section      → ID '{' Entry* Section* '}'   ← recursive production",
            "Entry        → Key '=' Value [';']?         ← semicolon optional",
            "Key          → SensitiveKey | RegularKey",
            "SensitiveKey → 'password' | 'secret' | 'api_key' | ...",
            "RegularKey   → ID",
            "Value        → EnvReference | QuotedString | NumberVal | BoolVal | PlainVal",
            "EnvReference → '${' ID '}'",
            "QuotedString → STRING",
            "NumberVal    → INT",
            "PlainVal     → ID",
        ],
        "why_not_regular": (
            "The grammar includes the production Section → ID '{' ... Section* ... '}', "
            "which is recursive and requires matching balanced braces at arbitrary depth. "
            "By the Pumping Lemma for regular languages, no finite automaton can track "
            "the nesting depth of an unbounded number of matched delimiter pairs. "
            "Therefore, this language is strictly context-free and cannot be expressed "
            "by any regular expression or DFA."
        ),
        "sensitive_key_constraint": (
            "If a Key is classified as SensitiveKey, its Value MUST be an EnvReference. "
            "Assigning a literal string to a sensitive key violates the security policy."
        ),
    }