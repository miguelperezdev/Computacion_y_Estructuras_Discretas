import requests
import subprocess

# ─── Database configuration (hardcoded credentials) ───
DB_HOST = "192.168.1.50"        # Internal IP exposed
DB_PORT = 5432
DB_NAME = "production"
DB_USER = "admin"
DB_PASSWORD = "hunter2"          # HARDCODED_PASSWORD

# ─── External API credentials ───
STRIPE_KEY = "sk_live_4eC39HqLyjWDarjtT1zdp7dc"   # STRIPE_KEY
GITHUB_TOKEN = "ghp_" + "A" * 36                   # GITHUB_TOKEN (synthetic)
AWS_KEY = "AKIA1234567890ABCDE"                     # AWS_API_KEY

# ─── Service endpoints (insecure) ───
PAYMENT_API = "http://payments.internal.corp.com/v1"   # INSECURE_URL
ANALYTICS_URL = "http://analytics.corp.com/track"       # INSECURE_URL

# ─── Dangerous functions ───
def run_user_command(cmd):
    """Execute arbitrary user input — dangerous."""
    result = eval(cmd)          # EVAL_CALL
    return result

def execute_script(script_text):
    exec(script_text)           # EXEC_CALL

# ─── Functions that leak secrets ───
def debug_db_connection():
    print(f"Connecting to {DB_HOST} with password={DB_PASSWORD}")  # PRINT_CALL leaking secret
    print(f"Using AWS key: {AWS_KEY}")                              # PRINT_CALL leaking secret

def log_api_keys():
    import logging
    logging.info(f"Stripe key in use: {STRIPE_KEY}")   # LOGGING_CALL leaking secret

# ─── TODO with credential mention ───
# TODO: remove hardcoded password before deploying to prod  # TODO_CREDENTIAL

def connect_to_db():
    conn_str = f"postgres://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}"  # DB_CONNECTION_STRING
    return conn_str

def fetch_payment_status(payment_id):
    """Fetches payment status using an insecure HTTP endpoint."""
    response = requests.get(f"{PAYMENT_API}/status/{payment_id}")
    return response.json()