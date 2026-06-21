# samples/secure_example.py
# This file demonstrates secure coding practices.

import os
import requests
import logging

logger = logging.getLogger(__name__)

# All secrets loaded from environment variables
password = os.getenv("APP_PASSWORD")
api_key = os.getenv("AWS_ACCESS_KEY_ID")
secret_token = os.getenv("APP_SECRET_TOKEN")
db_url = os.getenv("DATABASE_URL")

def fetch_user_data(user_id: int) -> dict:
    """Fetch user data over HTTPS."""
    headers = {"Authorization": f"Bearer {api_key}"}
    response = requests.get(
        f"https://internal.api.corp.com/users/{user_id}",
        headers=headers,
        timeout=10
    )
    response.raise_for_status()
    return response.json()

def connect_db():
    """Database connection using env-provided URL."""
    return db_url
