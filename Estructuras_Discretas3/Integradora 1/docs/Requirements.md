# Chomsky — Python Dependencies
# Install with: pip install -r requirements.txt

# Web interface
flask>=2.3.0,<4.0.0

# Formal automata library (Module 2 — DFA formal verification)
# Used to build and verify the DFA with pyformlang's algorithms
# Gracefully falls back to hand-coded implementation if not installed
pyformlang>=0.1.19

# Context-free grammar parser (Module 4 — CFG validation)
# Used to define and parse the secure configuration language via EBNF
# Gracefully falls back to hand-written recursive-descent parser if not installed
textx>=3.1.0

# Testing
pytest>=7.4.0
pytest-cov>=4.1.0

# Optional: colored output in pytest
pytest-html>=3.2.0