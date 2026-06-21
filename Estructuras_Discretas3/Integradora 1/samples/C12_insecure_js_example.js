// samples/insecure_js_example.js
// JavaScript file with insecure patterns — tests CONSOLE_LOG and JS-specific detection.
// Expected: Security Violation (hardcoded secret + console.log leak)

const express = require('express');
const app = express();

// Hardcoded credentials (HARDCODED_SECRET)
const apiKey = "abc123_super_secret_key_value";
const password = "admin_password_123";

// AWS key exposed (AWS_API_KEY)
const awsAccessKey = "AKIA1234567890ABCDE";

// Insecure endpoint (INSECURE_URL)
const paymentEndpoint = "http://payments.internal.corp.com/v1/charge";

// Leaking secrets through console.log (CONSOLE_LOG → LEAK in DFA)
function debugAuth() {
    console.log(apiKey);       // VIOLATION: credential + leak
    console.log(password);     // VIOLATION: credential + leak
    console.log(awsAccessKey); // VIOLATION: credential + leak
}

// Insecure HTTP API call
async function chargePayment(amount) {
    const response = await fetch(paymentEndpoint, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${apiKey}` },
        body: JSON.stringify({ amount })
    });
    return response.json();
}

// DFA trace for this file:
// HARDCODED_SECRET → HARDCODED_PASSWORD → AWS_API_KEY → INSECURE_URL → CONSOLE_LOG (×3)
// q0 --CREDENTIAL--> q1 --CREDENTIAL--> q1 --CREDENTIAL--> q1 --LOW_RISK--> q1 --LEAK--> q3_violation
// Result: Security Violation
