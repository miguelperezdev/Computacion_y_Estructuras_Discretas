# samples/insecure_example.py
# This file intentionally contains insecure patterns for Chomsky to detect.

import requests

# Hardcoded credentials — should be replaced with env vars
password = "admin123"
api_key = "AKIA1234567890ABCDE"
db_url = "postgres://admin:hunter2@192.168.1.50/production"
secret_token = "s3cr3t_t0k3n_abc123"

def fetch_user_data(user_id):
    # Leaking secret through print — policy violation
    print(password)
    print(api_key)

    # Insecure HTTP endpoint
    response = requests.get("http://internal.api.corp.com/users/" + str(user_id))
    return response.json()

def debug_info():
    # TODO: remove password from logs before deploying
    print(f"Connecting with password={password}")
    print(secret_token)

# eval usage — dangerous
user_input = input("Enter expression: ")
result = eval(user_input)
