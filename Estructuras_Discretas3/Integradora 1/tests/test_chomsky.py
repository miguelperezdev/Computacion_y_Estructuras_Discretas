"""
Unit and integration tests for Chomsky Security Analyzer.
Run with: pytest tests/ -v
"""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

from src.detector import detect
from src.classifier import classify
from src.transducer import transduce
from src.validator import validate
from src.pipeline import analyse_source


# ----------------------------------------------------------------------
# 1. Detection tests
# ----------------------------------------------------------------------

def test_detect_hardcoded_password():
    src = 'password = "admin123"'
    detections = detect(src)
    labels = [d.label for d in detections]
    assert "HARDCODED_PASSWORD" in labels

def test_detect_print_call():
    src = 'print(some_var)'
    detections = detect(src)
    labels = [d.label for d in detections]
    assert "PRINT_CALL" in labels

def test_detect_eval():
    src = 'eval(user_input)'
    detections = detect(src)
    labels = [d.label for d in detections]
    assert "EVAL_CALL" in labels


# ----------------------------------------------------------------------
# 2. Classification tests (DFA)
# ----------------------------------------------------------------------

def test_classify_safe():
    labels = []
    report = classify(labels)
    assert report.result.value == "Safe"

def test_classify_needs_review_credential_only():
    labels = ["HARDCODED_PASSWORD"]
    report = classify(labels)
    assert report.result.value == "Needs Review"

def test_classify_violation_credential_then_print():
    labels = ["HARDCODED_PASSWORD", "PRINT_CALL"]
    report = classify(labels)
    assert report.result.value == "Security Violation"

def test_classify_violation_print_then_credential():
    labels = ["PRINT_CALL", "HARDCODED_PASSWORD"]
    report = classify(labels)
    assert report.result.value == "Security Violation"

def test_classify_high_risk_eval():
    labels = ["EVAL_CALL"]
    report = classify(labels)
    assert report.result.value == "Needs Review"


# ----------------------------------------------------------------------
# 3. Transformation tests (FST)
# ----------------------------------------------------------------------

def test_transduce_replace_password():
    src = 'password = "admin123"'
    out = transduce(src)
    assert 'os.getenv' in out.transformed_source
    assert 'APP_PASSWORD' in out.transformed_source
    assert out.total_changes == 1

def test_transduce_remove_print():
    src = 'print(password)'
    out = transduce(src)
    assert '# [CHOMSKY] Sensitive output removed:' in out.transformed_source

def test_transduce_upgrade_http():
    src = 'url = "http://example.com"'
    out = transduce(src)
    assert 'https://example.com' in out.transformed_source

def test_transduce_adds_import():
    src = 'password = "admin123"'
    out = transduce(src)
    assert 'import os' in out.transformed_source


# ----------------------------------------------------------------------
# 4. Validation tests (CFG)
# ----------------------------------------------------------------------

def test_validate_secure_env():
    src = 'DB_PASSWORD=${SECURE_DB_PASSWORD}\nAPI_KEY=${API_KEY}'
    report = validate(src)
    assert report.is_secure is True
    assert len(report.errors) == 0

def test_validate_insecure_env():
    src = 'DB_PASSWORD=admin123'
    report = validate(src)
    assert report.is_secure is False
    assert any('Sensitive key' in e.message for e in report.errors)

def test_validate_secure_with_sections():
    src = '''
    database {
        password=${DB_PASS};
        user=admin;
    }
    '''
    report = validate(src)
    assert report.is_secure is True

def test_validate_insecure_with_sections():
    src = '''
    database {
        password=plaintext;
    }
    '''
    report = validate(src)
    assert report.is_secure is False


# ----------------------------------------------------------------------
# 5. Integration tests (full pipeline)
# ----------------------------------------------------------------------

def test_pipeline_insecure_py():
    src = '''
password = "admin123"
print(password)
'''
    result = analyse_source(src, filename="test.py")
    assert result.classification.result.value == "Security Violation"
    assert len(result.detections) >= 2
    assert result.transformation.total_changes >= 1
    assert result.validation is None  # not a config file

