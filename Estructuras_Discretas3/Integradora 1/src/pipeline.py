"""
Chomsky – Main Pipeline Orchestrator
=====================================
Ties together the four formal-language stages:
  1. Detection   (Regular Expressions)
  2. Classification (DFA)
  3. Transformation (FST)
  4. Validation  (CFG)
"""

from __future__ import annotations
from dataclasses import dataclass
from typing import List, Optional
import os

from detector import detect, get_labels, Detection
from classifier import classify, ClassificationReport, ClassificationResult
from transducer import transduce, TransducerOutput
from validator import validate, ValidationReport


@dataclass
class AnalysisResult:
    filename: str
    source: str
    detections: List[Detection]
    classification: ClassificationReport
    transformation: TransducerOutput
    validation: Optional[ValidationReport]
    is_config_file: bool


CONFIG_EXTENSIONS = {".env", ".yaml", ".yml", ".toml", ".ini", ".cfg", ".conf"}


def analyse_source(source: str, filename: str = "<stdin>") -> AnalysisResult:
    """Run the full Chomsky pipeline on a source string."""
    ext = os.path.splitext(filename)[1].lower()
    is_config = ext in CONFIG_EXTENSIONS or filename.startswith(".env")

    # Stage 1 – Detection
    detections = detect(source)
    labels = get_labels(detections)

    # Stage 2 – Classification
    classification = classify(labels)

    # Stage 3 – Transformation
    transformation = transduce(source)

    # Stage 4 – Validation (only for config files)
    validation = None
    if is_config:
        validation = validate(source)

    return AnalysisResult(
        filename=filename,
        source=source,
        detections=detections,
        classification=classification,
        transformation=transformation,
        validation=validation,
        is_config_file=is_config,
    )


def analyse_file(path: str) -> AnalysisResult:
    """Read a file from disk and run the full pipeline."""
    with open(path, "r", encoding="utf-8", errors="replace") as f:
        source = f.read()
    return analyse_source(source, filename=os.path.basename(path))
