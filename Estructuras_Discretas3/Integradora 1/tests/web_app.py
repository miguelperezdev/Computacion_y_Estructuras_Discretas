#!/usr/bin/env python3
"""
Chomsky – Web Interface (Flask)
================================
Provides a browser-based UI for analyzing code and config files.
Run with: python web_app.py
Then open: http://localhost:5000
"""

import sys, os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

from flask import Flask, request, jsonify, render_template_string
from pipeline import analyse_source
from classifier import ClassificationResult

app = Flask(__name__)

# ─────────────────────────────────────────────
#  HTML Template
# ─────────────────────────────────────────────
HTML = r"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Chomsky – Security Analyzer</title>
<style>
  @import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:wght@400;600&family=Space+Grotesk:wght@400;600;700&display=swap');

  :root {
    --bg: #0d0f14;
    --surface: #151821;
    --surface2: #1e2230;
    --border: #2a2f3f;
    --text: #e2e8f0;
    --muted: #64748b;
    --green: #22c55e;
    --yellow: #f59e0b;
    --red: #ef4444;
    --blue: #3b82f6;
    --cyan: #06b6d4;
    --mono: 'IBM Plex Mono', monospace;
    --sans: 'Space Grotesk', sans-serif;
  }

  * { box-sizing: border-box; margin: 0; padding: 0; }

  body {
    background: var(--bg);
    color: var(--text);
    font-family: var(--sans);
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  header {
    background: var(--surface);
    border-bottom: 1px solid var(--border);
    padding: 1.25rem 2rem;
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .logo {
    font-size: 1.5rem;
    font-weight: 700;
    letter-spacing: -0.03em;
    color: var(--cyan);
  }

  .logo span { color: var(--text); }

  .tagline {
    font-size: 0.75rem;
    color: var(--muted);
    font-family: var(--mono);
    letter-spacing: 0.05em;
  }

  .main {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0;
    flex: 1;
    height: calc(100vh - 65px);
  }

  .panel {
    display: flex;
    flex-direction: column;
    border-right: 1px solid var(--border);
    overflow: hidden;
  }

  .panel-header {
    background: var(--surface);
    border-bottom: 1px solid var(--border);
    padding: 0.75rem 1.25rem;
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
  }

  .panel-title {
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: var(--muted);
    font-family: var(--mono);
  }

  .controls { display: flex; gap: 0.5rem; align-items: center; }

  select {
    background: var(--surface2);
    color: var(--text);
    border: 1px solid var(--border);
    padding: 0.3rem 0.6rem;
    border-radius: 4px;
    font-size: 0.75rem;
    font-family: var(--mono);
    cursor: pointer;
  }

  textarea {
    flex: 1;
    background: var(--bg);
    color: var(--text);
    border: none;
    padding: 1.25rem;
    font-family: var(--mono);
    font-size: 0.825rem;
    line-height: 1.65;
    resize: none;
    outline: none;
    tab-size: 4;
  }

  button#analyze-btn {
    background: var(--cyan);
    color: #000;
    border: none;
    padding: 0.4rem 1.25rem;
    border-radius: 4px;
    font-weight: 700;
    font-size: 0.8rem;
    font-family: var(--sans);
    cursor: pointer;
    letter-spacing: 0.04em;
    transition: opacity 0.15s;
  }

  button#analyze-btn:hover { opacity: 0.85; }
  button#analyze-btn:disabled { opacity: 0.4; cursor: not-allowed; }

  .results-panel {
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    padding: 1.25rem;
    gap: 1.25rem;
    background: var(--bg);
  }

  .stage {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 8px;
    overflow: hidden;
  }

  .stage-header {
    padding: 0.75rem 1rem;
    background: var(--surface2);
    border-bottom: 1px solid var(--border);
    display: flex;
    align-items: center;
    gap: 0.6rem;
  }

  .stage-num {
    width: 20px; height: 20px;
    background: var(--border);
    border-radius: 50%;
    display: flex; align-items: center; justify-content: center;
    font-size: 0.65rem;
    font-weight: 700;
    font-family: var(--mono);
    color: var(--cyan);
    flex-shrink: 0;
  }

  .stage-title {
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--text);
  }

  .stage-body { padding: 0.875rem 1rem; }

  .badge {
    display: inline-flex;
    align-items: center;
    gap: 0.35rem;
    padding: 0.2rem 0.65rem;
    border-radius: 999px;
    font-size: 0.7rem;
    font-weight: 700;
    font-family: var(--mono);
    letter-spacing: 0.04em;
    margin-left: auto;
  }

  .badge-violation { background: rgba(239,68,68,0.15); color: var(--red); border: 1px solid rgba(239,68,68,0.3); }
  .badge-review    { background: rgba(245,158,11,0.15); color: var(--yellow); border: 1px solid rgba(245,158,11,0.3); }
  .badge-safe      { background: rgba(34,197,94,0.15);  color: var(--green);  border: 1px solid rgba(34,197,94,0.3); }
  .badge-high      { background: rgba(239,68,68,0.1);   color: var(--red);    border: 1px solid rgba(239,68,68,0.25); }
  .badge-medium    { background: rgba(245,158,11,0.1);  color: var(--yellow); border: 1px solid rgba(245,158,11,0.25); }
  .badge-low       { background: rgba(6,182,212,0.1);   color: var(--cyan);   border: 1px solid rgba(6,182,212,0.25); }

  .detection-row {
    display: flex;
    align-items: flex-start;
    gap: 0.75rem;
    padding: 0.4rem 0;
    border-bottom: 1px solid var(--border);
    font-size: 0.78rem;
    font-family: var(--mono);
  }
  .detection-row:last-child { border-bottom: none; }

  .det-line { color: var(--muted); min-width: 50px; }
  .det-label { color: var(--text); font-weight: 600; min-width: 200px; }
  .det-match { color: var(--muted); font-size: 0.73rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

  .trace {
    font-family: var(--mono);
    font-size: 0.72rem;
    color: var(--muted);
    line-height: 1.8;
    word-break: break-all;
  }

  .trace .state { color: var(--cyan); }
  .trace .arrow { color: var(--border); margin: 0 0.2rem; }

  .transform-item {
    padding: 0.5rem 0;
    border-bottom: 1px solid var(--border);
    font-family: var(--mono);
    font-size: 0.75rem;
  }
  .transform-item:last-child { border-bottom: none; }

  .line-old { color: var(--red); }
  .line-old::before { content: '- '; opacity: 0.7; }
  .line-new { color: var(--green); }
  .line-new::before { content: '+ '; opacity: 0.7; }
  .rule-tag { color: var(--muted); font-size: 0.68rem; margin-top: 2px; }

  .val-error {
    color: var(--red);
    font-family: var(--mono);
    font-size: 0.75rem;
    padding: 0.25rem 0;
  }

  .val-ok { color: var(--green); font-size: 0.8rem; }
  .no-issues { color: var(--green); font-size: 0.8rem; }

  .state-info {
    display: flex;
    gap: 1rem;
    margin-top: 0.5rem;
    font-family: var(--mono);
    font-size: 0.72rem;
    color: var(--muted);
  }
  .state-info span { color: var(--text); }

  .placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    gap: 0.75rem;
    color: var(--muted);
    text-align: center;
    padding: 2rem;
  }

  .placeholder-icon { font-size: 2.5rem; opacity: 0.3; }
  .placeholder-text { font-size: 0.85rem; line-height: 1.6; }

  .import-tag {
    display: inline-block;
    background: rgba(6,182,212,0.1);
    border: 1px solid rgba(6,182,212,0.25);
    color: var(--cyan);
    font-family: var(--mono);
    font-size: 0.7rem;
    padding: 0.15rem 0.5rem;
    border-radius: 4px;
    margin-top: 0.4rem;
  }

  .spinner {
    display: inline-block;
    width: 12px; height: 12px;
    border: 2px solid rgba(0,0,0,0.2);
    border-top-color: #000;
    border-radius: 50%;
    animation: spin 0.6s linear infinite;
    margin-right: 6px;
  }
  @keyframes spin { to { transform: rotate(360deg); } }
</style>
</head>
<body>
<header>
  <div>
    <div class="logo">Chomsky<span>.</span></div>
    <div class="tagline">Code Hazard Observation via Modeling of Syntax and KeY-patterns</div>
  </div>
</header>

<div class="main">
  <!-- Left panel: code input -->
  <div class="panel">
    <div class="panel-header">
      <span class="panel-title">Source Input</span>
      <div class="controls">
        <select id="file-type">
          <option value="script.py">Python (.py)</option>
          <option value="script.js">JavaScript (.js)</option>
          <option value="config.env">.env config</option>
          <option value="config.yaml">YAML config</option>
        </select>
        <select id="example-picker" onchange="loadExample(this.value)">
          <option value="">Load example...</option>
          <option value="insecure_py">Insecure Python</option>
          <option value="safe_py">Safe Python</option>
          <option value="insecure_env">Insecure .env</option>
          <option value="secure_env">Secure .env</option>
        </select>
        <button id="analyze-btn" onclick="analyze()">▶ Analyze</button>
      </div>
    </div>
    <textarea id="source" spellcheck="false" placeholder="Paste your source code or configuration here..."></textarea>
  </div>

  <!-- Right panel: results -->
  <div class="panel" style="border-right:none;">
    <div class="panel-header">
      <span class="panel-title">Analysis Results</span>
    </div>
    <div class="results-panel" id="results">
      <div class="placeholder">
        <div class="placeholder-icon">🔍</div>
        <div class="placeholder-text">Paste code on the left and click <strong>Analyze</strong><br>to run the Chomsky security pipeline.</div>
      </div>
    </div>
  </div>
</div>

<script>
const EXAMPLES = {
  insecure_py: {
    type: 'script.py',
    code: `import requests

password = "admin123"
api_key = "AKIA1234567890ABCDE"
db_url = "postgres://user:hunter2@localhost/mydb"

def fetch_data():
    print(password)
    print(api_key)
    r = requests.get("http://internal.api.corp.com/data")
    return r.json()`
  },
  safe_py: {
    type: 'script.py',
    code: `import os
import requests

password = os.getenv("APP_PASSWORD")
api_key = os.getenv("AWS_ACCESS_KEY_ID")

def fetch_data():
    headers = {"Authorization": f"Bearer {api_key}"}
    r = requests.get("https://internal.api.corp.com/data", headers=headers)
    return r.json()`
  },
  insecure_env: {
    type: 'config.env',
    code: `APP_NAME=chomsky
APP_PORT=8080
DB_HOST=localhost
DB_NAME=production
DB_PASSWORD=admin123
SECRET_KEY=mysupersecretkey_do_not_share
API_KEY=abc123plaintext`
  },
  secure_env: {
    type: 'config.env',
    code: `APP_NAME=chomsky
APP_PORT=8080
DB_HOST=localhost
DB_NAME=mydb
DB_PASSWORD=\${SECURE_DB_PASSWORD}
SECRET_KEY=\${APP_SECRET_KEY}
API_KEY=\${EXTERNAL_API_KEY}`
  }
};

function loadExample(key) {
  if (!key) return;
  const ex = EXAMPLES[key];
  document.getElementById('source').value = ex.code;
  document.getElementById('file-type').value = ex.type;
  document.getElementById('example-picker').value = '';
  analyze();
}

function sev_badge(sev) {
  const cls = {HIGH:'high',MEDIUM:'medium',LOW:'low'}[sev]||'low';
  return `<span class="badge badge-${cls}">${sev}</span>`;
}

function res_badge(res) {
  const map = {
    'Security Violation':'violation',
    'Needs Review':'review',
    'Safe':'safe'
  };
  return `<span class="badge badge-${map[res]||'safe'}">${res}</span>`;
}

async function analyze() {
  const src = document.getElementById('source').value.trim();
  if (!src) return;
  const filename = document.getElementById('file-type').value;
  const btn = document.getElementById('analyze-btn');
  btn.disabled = true;
  btn.innerHTML = '<span class="spinner"></span>Analyzing...';

  try {
    const resp = await fetch('/analyze', {
      method: 'POST',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify({source: src, filename})
    });
    const data = await resp.json();
    renderResults(data);
  } catch(e) {
    document.getElementById('results').innerHTML = `<p style="color:var(--red);padding:1rem">Error: ${e.message}</p>`;
  } finally {
    btn.disabled = false;
    btn.innerHTML = '▶ Analyze';
  }
}

function renderResults(data) {
  let html = '';

  // Stage 1 – Detection
  html += `<div class="stage">
    <div class="stage-header">
      <div class="stage-num">1</div>
      <div class="stage-title">Detection — Regular Expressions</div>
    </div>
    <div class="stage-body">`;
  if (data.detections.length === 0) {
    html += `<div class="no-issues">✓ No insecure patterns detected.</div>`;
  } else {
    data.detections.forEach(d => {
      html += `<div class="detection-row">
        ${sev_badge(d.severity)}
        <span class="det-line">L${d.line}</span>
        <span class="det-label">${d.label}</span>
        <span class="det-match">${escHtml(d.match)}</span>
      </div>`;
    });
  }
  html += `</div></div>`;

  // Stage 2 – Classification
  const res = data.classification;
  html += `<div class="stage">
    <div class="stage-header">
      <div class="stage-num">2</div>
      <div class="stage-title">Classification — Finite Automaton</div>
      ${res_badge(res.result)}
    </div>
    <div class="stage-body">
      <div class="state-info">
        <div>Final state: <span>${res.final_state}</span></div>
      </div>
      <div class="trace" style="margin-top:0.6rem">
        ${res.state_trace.map(s => `<span class="state">${s}</span>`).join('<span class="arrow">→</span>')}
      </div>
    </div>
  </div>`;

  // Stage 3 – Transformation
  html += `<div class="stage">
    <div class="stage-header">
      <div class="stage-num">3</div>
      <div class="stage-title">Transformation — Finite State Transducer</div>
    </div>
    <div class="stage-body">`;
  if (data.transformation.total_changes === 0) {
    html += `<div class="no-issues">✓ No transformations needed.</div>`;
  } else {
    data.transformation.changes.forEach(c => {
      html += `<div class="transform-item">
        <div class="line-old">${escHtml(c.original.trim())}</div>
        <div class="line-new">${escHtml(c.transformed.trim())}</div>
        <div class="rule-tag">Rule: ${c.rule}</div>
      </div>`;
    });
    if (data.transformation.imports_added.length > 0) {
      data.transformation.imports_added.forEach(i => {
        html += `<span class="import-tag">+ ${i}</span> `;
      });
    }
  }
  html += `</div></div>`;

  // Stage 4 – Validation (config only)
  if (data.validation) {
    const v = data.validation;
    html += `<div class="stage">
      <div class="stage-header">
        <div class="stage-num">4</div>
        <div class="stage-title">Validation — Context-Free Grammar</div>
        ${v.is_secure ? '<span class="badge badge-safe">SECURE</span>' : '<span class="badge badge-violation">VIOLATION</span>'}
      </div>
      <div class="stage-body">`;
    if (v.is_secure) {
      html += `<div class="val-ok">✓ Configuration is valid and secure.</div>`;
    } else {
      v.errors.forEach(e => {
        html += `<div class="val-error">✗ Line ${e.line}: ${escHtml(e.message)}</div>`;
      });
    }
    v.warnings.forEach(w => {
      html += `<div style="color:var(--yellow);font-size:0.75rem;font-family:var(--mono)">⚠ ${escHtml(w)}</div>`;
    });
    html += `</div></div>`;
  }

  document.getElementById('results').innerHTML = html;
}

function escHtml(s) {
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

// Ctrl+Enter to analyze
document.addEventListener('keydown', e => {
  if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') analyze();
});
</script>
</body>
</html>"""


@app.route("/")
def index():
    return render_template_string(HTML)


@app.route("/analyze", methods=["POST"])
def analyze():
    data = request.get_json()
    source = data.get("source", "")
    filename = data.get("filename", "input.py")

    result = analyse_source(source, filename=filename)

    # Serialize detections
    detections = [
        {"label": d.label, "line": d.line, "severity": d.severity,
         "match": d.match, "column": d.column}
        for d in result.detections
    ]

    # Classification
    classification = {
        "result": result.classification.result.value,
        "final_state": result.classification.final_state,
        "state_trace": result.classification.state_trace,
        "symbol_trace": result.classification.symbol_trace,
    }

    # Transformation
    changes = [
        {"original": t.original_line, "transformed": t.transformed_line, "rule": t.rule_applied}
        for t in result.transformation.transformations if t.changed
    ]
    transformation = {
        "total_changes": result.transformation.total_changes,
        "changes": changes,
        "imports_added": result.transformation.imports_added,
        "transformed_source": result.transformation.transformed_source,
    }

    # Validation
    validation = None
    if result.validation:
        validation = {
            "is_valid": result.validation.is_valid,
            "is_secure": result.validation.is_secure,
            "errors": [
                {"line": e.line, "message": e.message, "key": e.entry_key}
                for e in result.validation.errors
            ],
            "warnings": result.validation.warnings,
        }

    return jsonify({
        "filename": filename,
        "detections": detections,
        "classification": classification,
        "transformation": transformation,
        "validation": validation,
    })


if __name__ == "__main__":
    print("\n🔍 Chomsky Web Interface")
    print("   Open: http://localhost:5000\n")
    app.run(debug=True, port=5000)