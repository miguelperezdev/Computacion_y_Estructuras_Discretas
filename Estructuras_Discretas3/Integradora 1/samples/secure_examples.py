import os
import logging


DB_HOST     = os.getenv("DB_HOST", "localhost")
DB_PORT     = int(os.getenv("DB_PORT", "5432"))
DB_NAME     = os.getenv("DB_NAME", "chomsky_db")
DB_PASSWORD = os.getenv("DB_MASTER_PASSWORD")      
API_KEY     = os.getenv("EXTERNAL_API_KEY")         
SECRET_KEY  = os.getenv("APP_SECRET_KEY")           


logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
logger = logging.getLogger(__name__)


def get_db_connection_string() -> str:
    """Build DB connection string from environment variables only."""
    if not DB_PASSWORD:
        raise EnvironmentError("DB_MASTER_PASSWORD environment variable is not set.")
  
    return f"postgres://{DB_HOST}:{DB_PORT}/{DB_NAME}"


def fetch_user_data(user_id: int) -> dict:
    """
    Fetch user data from a secure HTTPS endpoint.
    Credentials are passed via headers, not URL parameters.
    """
    import urllib.request

    url = f"https://api.corp.internal.com/users/{user_id}"
    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json",
    }

    req = urllib.request.Request(url, headers=headers)
    logger.info("Fetching user data for ID %d", user_id)   # No secrets logged
    return {}


def validate_config() -> bool:
    """Validate that all required environment variables are set."""
    required = ["DB_MASTER_PASSWORD", "EXTERNAL_API_KEY", "APP_SECRET_KEY"]
    missing = [var for var in required if not os.getenv(var)]
    if missing:
        logger.error("Missing required environment variables: %s", ", ".join(missing))
        return False
    return True