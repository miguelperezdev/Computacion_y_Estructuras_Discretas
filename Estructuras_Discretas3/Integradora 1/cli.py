"""
Chomsky CLI – Command-Line Interface
======================================
Usage:
  python cli.py <file_or_directory> [--refactored] [--json] [--formal]
  python cli.py --demo
  python cli.py --formal
"""

import argparse
import json
import os
import sys
from pathlib import Path

sys.path.insert(0, os.path.join(os.path.dirname(__file__), "src"))

from pipeline import analyse_source, analyse_file, AnalysisResult
from classifier import ClassificationResult, get_dfa_5tuple
from transducer import get_fst_7tuple
from validator import get_cfg_formal

# ─────────────────────────────────────────────
#  ANSI colours
# ─────────────────────────────────────────────
RED    = "\033[91m"
YELLOW = "\033[93m"
GREEN  = "\033[92m"
BLUE   = "\033[94m"
CYAN   = "\033[96m"
BOLD   = "\033[1m"
DIM    = "\033[2m"
RESET  = "\033[0m"

SEVERITY_COLOR = {"HIGH": RED, "MEDIUM": YELLOW, "LOW": CYAN}
RESULT_COLOR = {
    ClassificationResult.SECURITY_VIOLATION: RED,
    ClassificationResult.NEEDS_REVIEW:       YELLOW,
    ClassificationResult.SAFE:               GREEN,
}


def print_banner():
    print(f"""
{BOLD}{BLUE}╔══════════════════════════════════════════════════════════╗
║         C H O M S K Y   Security Analyzer  v1.0          ║
║   Code Hazard Observation via Modeling of Syntax &        ║
║                      KeY-patterns                         ║
╚══════════════════════════════════════════════════════════╝{RESET}
""")


def print_formal_models():
    """Print the formal 5-tuple, 7-tuple, and CFG grammar definitions."""
    print(f"\n{BOLD}{CYAN}{'═'*60}{RESET}")
    print(f"{BOLD}{CYAN}  FORMAL MODEL DEFINITIONS{RESET}")
    print(f"{BOLD}{CYAN}{'═'*60}{RESET}")

    # ── DFA 5-tuple ──
    print(f"\n{BOLD}{BLUE}[MODULE 2] DFA — 5-tuple: M = (Q, Σ, δ, q₀, F){RESET}")
    t = get_dfa_5tuple()
    print(f"  {BOLD}Q  ={RESET} {{ {', '.join(sorted(t['Q']))} }}")
    print(f"  {BOLD}Σ  ={RESET} {{ {', '.join(sorted(t['Sigma']))} }}")
    print(f"  {BOLD}q₀ ={RESET} {t['q0']}")
    print(f"  {BOLD}F  ={RESET} Q  (all states accepting — classification by final state)")
    print(f"\n  {BOLD}δ — Transition Table:{RESET}")
    headers = sorted(t["Sigma"])
    col_w = 22
    header_row = f"  {'Current State':<26}" + "".join(f"{h:<{col_w}}" for h in headers)
    print(f"{DIM}{header_row}{RESET}")
    print(f"  {DIM}{'─'*90}{RESET}")
    for state in sorted(t["delta"].keys()):
        row = f"  {state:<26}"
        for sym in headers:
            target = t["delta"][state].get(sym, "—")
            row += f"{target:<{col_w}}"
        print(row)
    print(f"\n  {BOLD}State → Classification:{RESET}")
    for state, cls in t["classification"].items():
        print(f"    {state:<30} → {cls}")

    # ── FST 7-tuple ──
    print(f"\n{BOLD}{BLUE}[MODULE 3] FST — 7-tuple: T = (Q, Σ, Δ, δ, λ, q₀, F){RESET}")
    fst = get_fst_7tuple()
    print(f"  {BOLD}Q  ={RESET} {{ {', '.join(sorted(fst['Q']))} }}")
    print(f"  {BOLD}Σ  ={RESET} {fst['Sigma']}")
    print(f"  {BOLD}Δ  ={RESET} {fst['Delta']}")
    print(f"  {BOLD}q₀ ={RESET} {fst['q0']}")
    print(f"  {BOLD}F  ={RESET} {{ {', '.join(sorted(fst['F']))} }}")
    print(f"  {BOLD}λ  ={RESET} {fst['lambda']}")
    print(f"\n  {BOLD}Rules (δ × λ):{RESET}")
    for rule in fst.get("rules", []):
        print(f"    [{rule['name']}] {rule['description']}")

    # ── CFG / EBNF ──
    print(f"\n{BOLD}{BLUE}[MODULE 4] CFG — EBNF Grammar (textX){RESET}")
    cfg = get_cfg_formal()
    print(f"  {BOLD}Start symbol :{RESET} {cfg['start_symbol']}")
    print(f"  {BOLD}Non-terminals:{RESET} {', '.join(cfg['non_terminals'])}")
    print(f"\n  {BOLD}Production rules:{RESET}")
    for prod in cfg["productions"]:
        print(f"    {prod}")
    print(f"\n  {BOLD}Why not regular?{RESET}")
    # Word-wrap the justification
    words = cfg["why_not_regular"].split()
    line, out = "", []
    for w in words:
        if len(line) + len(w) + 1 > 70:
            out.append(line)
            line = w
        else:
            line = (line + " " + w).strip()
    if line:
        out.append(line)
    for l in out:
        print(f"    {l}")
    print()


def print_result(result: AnalysisResult, show_refactor: bool = True):
    print(f"\n{BOLD}{'─'*60}{RESET}")
    print(f"{BOLD}File:{RESET} {result.filename}")
    print(f"{BOLD}{'─'*60}{RESET}")

    # ── Stage 1: Detections ──
    print(f"\n{BOLD}{BLUE}[1] DETECTION — Regular Expressions{RESET}")
    if not result.detections:
        print(f"  {GREEN}✓ No insecure patterns detected.{RESET}")
    else:
        for d in result.detections:
            col = SEVERITY_COLOR.get(d.severity, RESET)
            print(f"  {col}[{d.severity}]{RESET} Line {d.line:>3} | {d.label:<30} | {DIM}{d.match}{RESET}")

    # ── Stage 2: Classification ──
    print(f"\n{BOLD}{BLUE}[2] CLASSIFICATION — Finite Automaton (DFA){RESET}")
    res = result.classification.result
    col = RESULT_COLOR.get(res, RESET)
    print(f"  Result : {col}{BOLD}{res.value}{RESET}")
    print(f"  State  : {result.classification.final_state}")
    trace_str = " → ".join(result.classification.state_trace)
    print(f"  Trace  : {DIM}{trace_str}{RESET}")
    if result.classification.symbol_trace:
        sym_str = " → ".join(result.classification.symbol_trace)
        print(f"  Symbols: {DIM}{sym_str}{RESET}")

    # ── Stage 3: Transformation ──
    if show_refactor:
        print(f"\n{BOLD}{BLUE}[3] TRANSFORMATION — Finite State Transducer (FST){RESET}")
        t = result.transformation
        if t.total_changes == 0:
            print(f"  {GREEN}✓ No transformations needed.{RESET}")
        else:
            print(f"  {t.total_changes} transformation(s) applied:")
            for change in t.transformations:
                if change.changed:
                    print(f"  {RED}  [-] {change.original_line.strip()}{RESET}")
                    print(f"  {GREEN}  [+] {change.transformed_line.strip()}{RESET}")
                    print(f"  {DIM}      Rule: {change.rule_applied}{RESET}")
            if t.imports_added:
                print(f"\n  {CYAN}Imports added: {', '.join(t.imports_added)}{RESET}")

    # ── Stage 4: Validation ──
    if result.validation is not None:
        print(f"\n{BOLD}{BLUE}[4] VALIDATION — Context-Free Grammar (CFG){RESET}")
        v = result.validation
        if v.is_secure:
            print(f"  {GREEN}✓ Configuration is valid and secure.{RESET}")
        else:
            for err in v.errors:
                print(f"  {RED}✗ Line {err.line}: {err.message}{RESET}")
        for warn in v.warnings:
            print(f"  {YELLOW}⚠ {warn}{RESET}")
    elif result.is_config_file is False:
        print(f"\n{DIM}  [4] Validation skipped (not a config file){RESET}")


def print_directory_summary(results):
    """Print a multi-file summary table."""
    print(f"\n{BOLD}{'═'*60}{RESET}")
    print(f"{BOLD}  DIRECTORY SUMMARY — {len(results)} file(s) analyzed{RESET}")
    print(f"{BOLD}{'═'*60}{RESET}")

    violations = [r for r in results if r.classification.result == ClassificationResult.SECURITY_VIOLATION]
    reviews    = [r for r in results if r.classification.result == ClassificationResult.NEEDS_REVIEW]
    safe       = [r for r in results if r.classification.result == ClassificationResult.SAFE]

    print(f"\n  {RED}Security Violations : {len(violations)}{RESET}")
    for r in violations:
        print(f"    • {r.filename} ({len(r.detections)} detections)")
    print(f"  {YELLOW}Needs Review        : {len(reviews)}{RESET}")
    for r in reviews:
        print(f"    • {r.filename} ({len(r.detections)} detections)")
    print(f"  {GREEN}Safe                : {len(safe)}{RESET}")
    for r in safe:
        print(f"    • {r.filename}")
    print()


DEMO_CODE = '''import requests

password = "admin123"
api_key = "AKIA1234567890ABCDE"
db_url = "postgres://admin:hunter2@192.168.1.50/production"

def fetch_user_data(user_id):
    print(password)
    print(api_key)
    response = requests.get("http://internal.api.corp.com/users/" + str(user_id))
    return response.json()

result = eval(input("Enter expression: "))
'''


def main():
    parser = argparse.ArgumentParser(
        description="Chomsky — Code Repository Security Analyzer",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument("target", nargs="?", help="File or directory to analyze")
    parser.add_argument("--refactored", action="store_true", help="Show refactored source")
    parser.add_argument("--json", action="store_true", help="Output as JSON")
    parser.add_argument("--demo", action="store_true", help="Run built-in demo")
    parser.add_argument("--formal", action="store_true", help="Print formal model definitions (5-tuple, 7-tuple, CFG)")
    args = parser.parse_args()

    print_banner()

    if args.formal:
        print_formal_models()
        return

    if args.demo:
        result = analyse_source(DEMO_CODE, filename="demo_insecure.py")
        print_result(result)
        return

    if not args.target:
        parser.print_help()
        return

    target = Path(args.target)
    if not target.exists():
        print(f"{RED}Error: '{args.target}' not found.{RESET}")
        sys.exit(1)

    if target.is_file():
        result = analyse_file(str(target))
        if args.json:
            print(json.dumps({
                "filename": result.filename,
                "classification": result.classification.result.value,
                "detections": [
                    {"label": d.label, "line": d.line, "severity": d.severity}
                    for d in result.detections
                ],
                "total_transformations": result.transformation.total_changes,
            }, indent=2))
        else:
            print_result(result, show_refactor=True)
            if args.refactored and result.transformation.total_changes > 0:
                print(f"\n{BOLD}{CYAN}{'─'*60}{RESET}")
                print(f"{BOLD}Refactored Source:{RESET}")
                print(f"{BOLD}{CYAN}{'─'*60}{RESET}")
                print(result.transformation.transformed_source)

    elif target.is_dir():
        extensions = {".py", ".js", ".ts", ".env", ".yaml", ".yml", ".conf", ".cfg", ".toml"}
        files = [f for f in target.rglob("*") if f.suffix in extensions and f.is_file()]
        if not files:
            print(f"{YELLOW}No analyzable files found in '{args.target}'.{RESET}")
            return
        results = []
        for f in sorted(files):
            result = analyse_file(str(f))
            results.append(result)
            print_result(result, show_refactor=False)
        print_directory_summary(results)


if __name__ == "__main__":
    main()