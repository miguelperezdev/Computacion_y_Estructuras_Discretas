# ENTRADA (inseguro)
password = "admin123"
print(password)

# CHOMSKY detecta → Security Violation
# CHOMSKY transforma →
import os
password = os.getenv("APP_PASSWORD")
# [CHOMSKY] Sensitive output removed: print(password)