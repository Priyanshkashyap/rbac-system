import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 500,
    duration: '2m',
};

const BASE_URL = 'http://localhost:8080';

// Paste your JWT token here
const TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImlhdCI6MTc4Mzc3MDY3NiwiZXhwIjoxNzgzNzc0Mjc2fQ.qxFHgXZwkUJeUHdE9o3SHmKVXpbng1SjoFACitdYodQ"
const authHeaders = {
    headers: {
        Authorization: `Bearer ${TOKEN}`,
        "Content-Type": "application/json",
    },
};

export default function () {

    // Test USER endpoint
    let userRes = http.get(
        `${BASE_URL}/instance`,
        authHeaders
    );

    check(userRes, {
        "User endpoint returned 200": (r) => r.status === 200,
    });

    // Uncomment these if you want to test more endpoints

    /*
    let adminRes = http.get(
        `${BASE_URL}/api/admin`,
        authHeaders
    );

    check(adminRes, {
        "Admin endpoint returned 200": (r) => r.status === 200,
    });

    let managerRes = http.get(
        `${BASE_URL}/api/manager`,
        authHeaders
    );

    check(managerRes, {
        "Manager endpoint returned 200": (r) => r.status === 200,
    });
    */

    sleep(0.1);
}