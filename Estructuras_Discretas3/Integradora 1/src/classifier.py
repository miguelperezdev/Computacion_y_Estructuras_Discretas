"""
Module 2 – Classification (Behavioral Level)
=============================================
Implements a DFA that reads the token stream produced by the detector and
classifies the file into: Safe | Needs Review | Security Violation.

Formal Definition (DFA):
  Q  = { q0_start, q1_has_credential, q2_has_print, q3_violation,
          q4_needs_review, q5_safe }
  Σ  = set of pattern labels (tokens)
  δ  = transition function (see TRANSITIONS below)
  q₀ = q0_start
  F  = { q3_violation, q4_needs_review, q5_safe }

Classification logic:
  - Security Violation : credential assigned AND then printed/logged
  - Needs Review       : credential found but not yet leaked, OR eval/exec used
  - Safe               : no sensitive patterns found

The DFA is built manually and also via pyformlang for formal verification.
"""

from __future__ import annotations
from dataclasses import dataclass
from typing import List, Dict, Optional
from enum import Enum

try:
    from pyformlang.finite_automaton import (
        DeterministicFiniteAutomaton,
        State,
        Symbol,
    )
    PYFORMLANG_AVAILABLE = True
except ImportError:
    PYFORMLANG_AVAILABLE = False


# ─────────────────────────────────────────────
#  States
# ─────────────────────────────────────────────
class ClassificationResult(str, Enum):
    SAFE = "Safe"
    NEEDS_REVIEW = "Needs Review"
    SECURITY_VIOLATION = "Security Violation"


# Internal DFA state names
S_START      = "q0_start"
S_CRED       = "q1_has_credential"
S_PRINT      = "q2_has_print_only"
S_VIOLATION  = "q3_violation"
S_REVIEW     = "q4_needs_review"
S_SAFE       = "q5_safe"

# Token categories (groups of label types)
CREDENTIAL_LABELS = {
    "HARDCODED_PASSWORD", "HARDCODED_SECRET",
    "AWS_API_KEY", "GITHUB_TOKEN", "DB_CONNECTION_STRING", "PLAIN_CONFIG_VALUE",
}
LEAK_LABELS = {
    "PRINT_CALL", "CONSOLE_LOG", "LOGGING_CALL",
}
HIGH_RISK_LABELS = {
    "EVAL_CALL", "EXEC_CALL",
}
LOW_RISK_LABELS = {
    "IPV4_ADDRESS", "INSECURE_URL", "TODO_CREDENTIAL",
}


def _categorise(label: str) -> str:
    """Map a raw label to an abstract symbol for the DFA."""
    if label in CREDENTIAL_LABELS:
        return "CREDENTIAL"
    if label in LEAK_LABELS:
        return "LEAK"
    if label in HIGH_RISK_LABELS:
        return "HIGH_RISK"
    if label in LOW_RISK_LABELS:
        return "LOW_RISK"
    return "OTHER"


# ─────────────────────────────────────────────
#  Transition table  δ : Q × Σ → Q
# ─────────────────────────────────────────────
# Σ symbols: CREDENTIAL | LEAK | HIGH_RISK | LOW_RISK | OTHER
TRANSITIONS: Dict[str, Dict[str, str]] = {
    S_START: {
        "CREDENTIAL": S_CRED,
        "LEAK":       S_PRINT,
        "HIGH_RISK":  S_REVIEW,
        "LOW_RISK":   S_REVIEW,
        "OTHER":      S_START,
    },
    S_CRED: {
        "CREDENTIAL": S_CRED,
        "LEAK":       S_VIOLATION,   # credential + leak = violation
        "HIGH_RISK":  S_VIOLATION,
        "LOW_RISK":   S_CRED,
        "OTHER":      S_CRED,
    },
    S_PRINT: {
        "CREDENTIAL": S_VIOLATION,   # leak after credential (reordered) = violation
        "LEAK":       S_PRINT,
        "HIGH_RISK":  S_REVIEW,
        "LOW_RISK":   S_PRINT,
        "OTHER":      S_PRINT,
    },
    S_VIOLATION: {                   # absorbing state
        "CREDENTIAL": S_VIOLATION,
        "LEAK":       S_VIOLATION,
        "HIGH_RISK":  S_VIOLATION,
        "LOW_RISK":   S_VIOLATION,
        "OTHER":      S_VIOLATION,
    },
    S_REVIEW: {                      # absorbing state
        "CREDENTIAL": S_VIOLATION,
        "LEAK":       S_REVIEW,
        "HIGH_RISK":  S_REVIEW,
        "LOW_RISK":   S_REVIEW,
        "OTHER":      S_REVIEW,
    },
}

# Final state → classification
STATE_TO_RESULT: Dict[str, ClassificationResult] = {
    S_START:     ClassificationResult.SAFE,
    S_CRED:      ClassificationResult.NEEDS_REVIEW,
    S_PRINT:     ClassificationResult.NEEDS_REVIEW,
    S_VIOLATION: ClassificationResult.SECURITY_VIOLATION,
    S_REVIEW:    ClassificationResult.NEEDS_REVIEW,
    S_SAFE:      ClassificationResult.SAFE,
}


@dataclass
class ClassificationReport:
    result: ClassificationResult
    final_state: str
    state_trace: List[str]
    symbol_trace: List[str]


def classify(labels: List[str]) -> ClassificationReport:
    """
    Run the DFA over the token stream and return a classification report.
    """
    current = S_START
    state_trace = [current]
    symbol_trace: List[str] = []

    for label in labels:
        sym = _categorise(label)
        symbol_trace.append(sym)
        row = TRANSITIONS.get(current, {})
        current = row.get(sym, current)
        state_trace.append(current)

    return ClassificationReport(
        result=STATE_TO_RESULT.get(current, ClassificationResult.NEEDS_REVIEW),
        final_state=current,
        state_trace=state_trace,
        symbol_trace=symbol_trace,
    )


# ─────────────────────────────────────────────
#  pyformlang DFA (for formal verification)
# ─────────────────────────────────────────────

def build_pyformlang_dfa() -> Optional[object]:
    """Build and return the equivalent pyformlang DFA (if library available)."""
    if not PYFORMLANG_AVAILABLE:
        return None

    dfa = DeterministicFiniteAutomaton()
    start = State(S_START)
    dfa.add_start_state(start)

    # Add final states (all states are accepting — classification depends on which)
    for s in [S_START, S_CRED, S_PRINT, S_VIOLATION, S_REVIEW]:
        dfa.add_final_state(State(s))

    # Add transitions
    for state, symbol_map in TRANSITIONS.items():
        for symbol, target in symbol_map.items():
            dfa.add_transition(State(state), Symbol(symbol), State(target))

    return dfa


def get_dfa_5tuple() -> dict:
    """Return the formal 5-tuple representation of the DFA."""
    all_states = {S_START, S_CRED, S_PRINT, S_VIOLATION, S_REVIEW}
    alphabet = {"CREDENTIAL", "LEAK", "HIGH_RISK", "LOW_RISK", "OTHER"}
    return {
        "Q": all_states,
        "Sigma": alphabet,
        "delta": TRANSITIONS,
        "q0": S_START,
        "F": {S_VIOLATION, S_REVIEW, S_CRED, S_PRINT, S_START},
        "classification": STATE_TO_RESULT,
    }


def classify_findings():
    return None


def dfa_info():
    return None


def NEEDS_REVIEW():
    return None


def SECURITY_VIOLATION():
    return None


def SAFE():
    return None