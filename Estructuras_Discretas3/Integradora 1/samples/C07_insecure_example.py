# samples/insecure_example.py
# This file intentionally contains insecure patterns for Chomsky to detect.
# It covers ALL detection categories defined in the formal language (Module 1).

import requests

# ─── Category 1: Hardcoded credentials (HARDCODED_PASSWORD, HARDCODED_SECRET) ───
password = "admin123"                       # HARDCODED_PASSWORD — HIGH
passwd   = 'letmein'                        # HARDCODED_PASSWORD — HIGH
api_key  = "abc123_secret_token_value"      # HARDCODED_SECRET — HIGH

# ─── Category 2: Cloud service keys (AWS_API_KEY, GITHUB_TOKEN) ───
aws_key      = "AKIA1234567890ABCDE"        # AWS_API_KEY — HIGH
github_token = "ghp_" + "B" * 36           # GITHUB_TOKEN — HIGH (synthetic)

# ─── Category 3: Database connection string (DB_CONNECTION_STRING) ───
db_url = "postgres://admin:hunter2@192.168.1.50/production"  # DB_CONNECTION_STRING + IPV4

# ─── Category 4: Dangerous functions (EVAL_CALL, EXEC_CALL) ───
user_expression = input("Enter expression: ")
eval_result = eval(user_expression)         # EVAL_CALL — HIGH

# ─── Functions that leak secrets through print/logging ───
def fetch_user_data(user_id):
    # Leaking credentials through print — triggers SECURITY VIOLATION in DFA
    print(password)                                          # PRINT_CALL — MEDIUM
    print(api_key)                                           # PRINT_CALL — MEDIUM

    # Insecure HTTP endpoint (INSECURE_URL)
    url = "http://internal.api.corp.com/users/" + str(user_id)
    response = requests.get(url)
    return response.json()

def debug_info():
    # TODO: remove password from logs before deploying    # TODO_CREDENTIAL — LOW
    print(f"Connecting with password={password}")          # PRINT_CALL — MEDIUM
    print(aws_key)                                         # PRINT_CALL — MEDIUM

# ─── DFA trace for this file ───
# Labels detected: HARDCODED_PASSWORD, HARDCODED_PASSWORD, HARDCODED_SECRET,
#                  AWS_API_KEY, GITHUB_TOKEN, DB_CONNECTION_STRING,
#                  IPV4_ADDRESS, EVAL_CALL, PRINT_CALL (x4), INSECURE_URL,
#                  TODO_CREDENTIAL
#
# DFA: q0_start --CREDENTIAL--> q1_has_credential --LEAK--> q3_violation
# Classification: SECURITY VIOLATION
