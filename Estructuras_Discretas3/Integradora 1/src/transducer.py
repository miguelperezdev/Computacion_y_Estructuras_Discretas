"""
Module 3 – Transformation (Rewriting Level)
============================================
Finite State Transducer (FST) that maps insecure code constructs
to safer alternatives (source-to-source rewriting).

Formal Definition (FST):
  Q  = { q_start, q_in_transform, q_done }
  Σ  = input alphabet (lines of source code, abstracted as token types)
  Δ  = output alphabet (transformed source code lines)
  δ  = transition function with output (see TransducerRule below)
  q₀ = q_start
  F  = { q_done }

Each rule defines an input pattern (regex) and its output transformation.
The FST processes the source line by line, applying matching rules.
"""

import re
from dataclasses import dataclass, field
from typing import List, Tuple, Optional


@dataclass
class TransducerRule:
    """A single FST rule: input pattern → output template."""
    name: str
    pattern: re.Pattern
    transform_fn: object   # callable(match) → str
    description: str
    needs_import: Optional[str] = None  # import statement to prepend if used


# ─────────────────────────────────────────────
#  FST Rules
# ─────────────────────────────────────────────

def _make_env_replacement(var_name: str) -> str:
    return f'os.getenv("{var_name.upper()}")'


RULES: List[TransducerRule] = [
    # Rule 1: Replace hardcoded password with env var
    TransducerRule(
        name="REPLACE_PASSWORD",
        pattern=re.compile(
            r'^(\s*)(password|passwd|pwd)\s*=\s*["\'][^"\']+["\']',
            re.IGNORECASE
        ),
        transform_fn=lambda m: (
            f'{m.group(1)}{m.group(2)} = os.getenv("APP_{m.group(2).upper()}")'
        ),
        description="Replace hardcoded password with os.getenv() call",
        needs_import="import os",
    ),

    # Rule 2: Replace hardcoded secret/token/api_key
    TransducerRule(
        name="REPLACE_SECRET",
        pattern=re.compile(
            r'^(\s*)(secret|api_key|access_token|auth_token)\s*=\s*["\'][^"\']+["\']',
            re.IGNORECASE
        ),
        transform_fn=lambda m: (
            f'{m.group(1)}{m.group(2)} = os.getenv("APP_{m.group(2).upper()}")'
        ),
        description="Replace hardcoded secret with os.getenv() call",
        needs_import="import os",
    ),

    # Rule 3: Replace AWS API key assignment
    TransducerRule(
        name="REPLACE_AWS_KEY",
        pattern=re.compile(
            r'^(\s*)(\w+)\s*=\s*["\']AKIA[0-9A-Z]{16}["\']'
        ),
        transform_fn=lambda m: (
            f'{m.group(1)}{m.group(2)} = os.getenv("AWS_ACCESS_KEY_ID")'
        ),
        description="Replace AWS API key with env var reference",
        needs_import="import os",
    ),

    # Rule 4: Comment out print() calls that may leak secrets
    TransducerRule(
        name="REMOVE_PRINT",
        pattern=re.compile(
            r'^(\s*)print\s*\(([^)]*)\)'
        ),
        transform_fn=lambda m: (
            f'{m.group(1)}# [CHOMSKY] Sensitive output removed: print({m.group(2)})'
        ),
        description="Comment out print() calls to prevent secret leakage",
    ),

    # Rule 5: Comment out console.log (JS)
    TransducerRule(
        name="REMOVE_CONSOLE_LOG",
        pattern=re.compile(
            r'^(\s*)console\.log\s*\(([^)]*)\)'
        ),
        transform_fn=lambda m: (
            f'{m.group(1)}// [CHOMSKY] Sensitive output removed: console.log({m.group(2)})'
        ),
        description="Comment out console.log() calls",
    ),

    # Rule 6: Replace insecure http:// URL with https://
    TransducerRule(
        name="UPGRADE_HTTP_TO_HTTPS",
        pattern=re.compile(r'http://([^\s"\']+)'),
        transform_fn=lambda m: f'https://{m.group(1)}',
        description="Upgrade insecure HTTP URL to HTTPS",
    ),

    # Rule 7: Replace plain config password with env reference
    TransducerRule(
        name="SECURE_CONFIG_VALUE",
        pattern=re.compile(
            r'^(db_password|db_pass|secret_key|api_key|auth_token)\s*=\s*(?!\$\{)(.+)$',
            re.MULTILINE | re.IGNORECASE
        ),
        transform_fn=lambda m: (
            f'{m.group(1)}=${{{m.group(1).upper()}}}'
        ),
        description="Replace plain config value with environment variable reference",
    ),
]


@dataclass
class TransformationResult:
    original_line: str
    transformed_line: str
    rule_applied: Optional[str]
    changed: bool


@dataclass
class TransducerOutput:
    original_source: str
    transformed_source: str
    transformations: List[TransformationResult]
    imports_added: List[str]
    total_changes: int


def transduce(source: str) -> TransducerOutput:
    """
    Apply the FST rules to the source code.
    Returns the transformed source and a record of all changes made.
    """
    lines = source.splitlines()
    result_lines: List[str] = []
    transformations: List[TransformationResult] = []
    imports_needed: set = set()

    for line in lines:
        transformed = line
        rule_name = None

        for rule in RULES:
            # Try to apply this rule to the current line
            try:
                new_line = rule.pattern.sub(rule.transform_fn, transformed)
            except Exception:
                continue

            if new_line != transformed:
                if rule.needs_import:
                    imports_needed.add(rule.needs_import)
                rule_name = rule.name
                transformed = new_line
                break   # apply first matching rule per line

        transformations.append(TransformationResult(
            original_line=line,
            transformed_line=transformed,
            rule_applied=rule_name,
            changed=(transformed != line),
        ))
        result_lines.append(transformed)

    # Prepend any needed imports at the top (after existing imports if possible)
    imports_list = sorted(imports_needed)
    final_lines = list(result_lines)
    if imports_list:
        # Find insertion point: after last existing import or at top
        insert_at = 0
        for i, l in enumerate(final_lines):
            if l.startswith("import ") or l.startswith("from "):
                insert_at = i + 1
        for imp in reversed(imports_list):
            if imp not in "\n".join(final_lines[:insert_at]):
                final_lines.insert(insert_at, imp)

    transformed_source = "\n".join(final_lines)
    total_changes = sum(1 for t in transformations if t.changed)

    return TransducerOutput(
        original_source=source,
        transformed_source=transformed_source,
        transformations=transformations,
        imports_added=imports_list,
        total_changes=total_changes,
    )


def get_fst_7tuple() -> dict:
    """Return the formal 7-tuple representation of the FST."""
    return {
        "Q": {"q_start", "q_applying_rule", "q_done"},
        "Sigma": "Lines of source code (strings over ASCII)",
        "Delta": "Transformed lines of source code",
        "delta": {r.name: r.description for r in RULES},
        "q0": "q_start",
        "F": {"q_done"},
        "lambda": "Output function: applies first matching rule to each line",
        "rules": [
            {"name": r.name, "description": r.description,
             "needs_import": r.needs_import}
            for r in RULES
        ],
    }
