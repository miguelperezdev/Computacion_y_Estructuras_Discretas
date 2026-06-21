# tests/conftest.py
"""
Pytest configuration for the Chomsky test suite.
Adds src/ to the Python path so tests can import modules directly.
"""
import sys
import os

# Add the src directory to the import path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))


def pytest_configure(config):
    """Register custom pytest markers."""
    config.addinivalue_line("markers", "unit: Unit tests for individual modules")
    config.addinivalue_line("markers", "integration: Integration tests for the full pipeline")
    config.addinivalue_line("markers", "formal: Tests for formal model correctness (5-tuple, 7-tuple, CFG)")
    config.addinivalue_line("markers", "slow: Tests that may take longer to run")
