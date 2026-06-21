# Contributing & Team Roles

## Chomsky — Code Repository Security Analyzer

---

## Team

| Member              | GitHub         | Primary Responsibilities |
|---------------------|----------------|---|
| Miguel Pérez        | miguelperezdev | Module 1 (Detection), Module 2 (Classification), CLI, Tests, README |
| Estefany Villamarin | Villamarin17   | Module 3 (Transducer), Module 4 (Validator), Web App, Docs, Literature Review |

---

## Commit History Summary

Each commit represents a meaningful unit of work. All commits have a minimum of 2 hours between them, as required by the project guidelines.

### Miguel Pérez (miguelperezdev)

| Commit | Description | Files Changed |
|---|---|---|
| M01 | docs: complete README with formal models summary and usage guide | `README.md` |
| M02 | feat: enhance detector with GitHub fine token, Stripe key, and high-entropy patterns | `src/detector.py` |
| M03 | fix: correct DFA 5-tuple — synchronize states, add pyformlang formal verification | `src/classifier.py` |
| M04 | feat: add mixed_scenario.py and nested_config.conf sample files | `samples/` |
| M05 | test: expand test suite to 50+ tests covering all DFA states and formal properties | `tests/test_chomsky.py` |
| M06 | feat: add --formal flag to CLI printing 5-tuple, 7-tuple, and EBNF | `cli.py` |
| M07 | feat: add analyse_directory() and DirectoryReport to pipeline orchestrator | `src/pipeline.py` |
| M08 | refactor: improve secure_example.py with full environment-variable pattern | `samples/secure_example.py` |
| M09 | chore: add .gitignore for Python artifacts and IDE files | `.gitignore` |
| M10 | chore: pin dependency versions in requirements.txt | `requirements.txt` |
| M11-M15 | (future commits reserved for bug fixes, presentation prep, poster integration) | — |

###  Estefany Villamarin | Villamarin17 

| Commit | Description | Files Changed |
|---|---|---|
| C01 | docs: write full literature review with 8 academic references | `docs/LITERATURE_REVIEW.md` |
| C02 | docs: create MODULE_DESIGN.md with function signatures and I/O specs | `docs/MODULE_DESIGN.md` |
| C03 | docs: create TEST_CASES.md with 15 formal test scenarios | `docs/TEST_CASES.md` |
| C04 | docs: update DESIGN.md with correct DFA/FST transition diagrams | `docs/DESIGN.md` |
| C05 | feat: improve transducer with Stripe key rule and expanded 7-tuple | `src/transducer.py` |
| C06 | feat: add Formal Models tab to web app with DFA table and FST rules | `web_app.py` |
| C07 | feat: improve insecure_example.py covering all 17 detection categories | `samples/insecure_example.py` |
| C08 | test: add conftest.py with custom pytest markers and path setup | `tests/conftest.py` |
| C09 | feat: add insecure_nested_config.conf sample for CFG testing | `samples/insecure_nested_config.conf` |
| C10 | chore: add __init__.py with version and module description | `src/__init__.py` |
| C11 | feat: improve validator with extended sensitive keys and better error messages | `src/validator.py` |
| C12 | feat: add JavaScript insecure sample (console.log leak scenario) | `samples/insecure_js_example.js` |
| C13 | chore: add pytest.ini with test configuration and markers | `pytest.ini` |
| C14 | docs: add FORMAL_PROOFS.md with mathematical justifications for each module | `docs/FORMAL_PROOFS.md` |
| C15 | docs: add CONTRIBUTING.md with team roles and commit history | `docs/CONTRIBUTING.md` |

---

## Repository Structure Convention

```
docs/           ← All design and documentation files (Markdown)
src/            ← Python source modules
tests/          ← pytest test suite
samples/        ← Example input files (secure and insecure)
```

All documentation is written in Markdown (`.md`) as required by the project guidelines.

---

## Coding Standards

- All Python code follows PEP 8 style guidelines
- All public functions have docstrings with `Args:` and `Returns:` sections
- All modules include a formal definition of the language model they implement
- Tests are organized by module (TestDetection, TestClassification, etc.)
- Commit messages follow the pattern: `type(scope): description`
  - Types: `feat`, `fix`, `test`, `docs`, `refactor`, `chore`
