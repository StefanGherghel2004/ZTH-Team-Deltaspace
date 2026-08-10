import http from 'k6/http';
import { check, group, sleep } from 'k6';
import exec from 'k6/execution';

export const options = {
    stages: [

        { duration: '1m',  target: 1000 },  // Write stress load
        { duration: '30s', target: 0 },    // Cooldown
    ],
    thresholds: {
        http_req_failed: ['rate<0.02'],     // Max 2% failure rate allowed
        http_req_duration: ['p(95)<1000'],  // 95% of requests completed in < 1000ms
    },
};
function numberToAlpha(num) {
    return num.toString().split('').map(digit => String.fromCharCode(97 + parseInt(digit))).join('');
}

// Generates a random string containing ONLY letters [a-zA-Z]
function randomAlphaString(length) {
    const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let res = '';
    for (let i = 0; i < length; i++) {
        res += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return res;
}
export default function () {
    const baseUrl = 'http://localhost:8080';
    const token='eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZXJiYW5tMSIsImlhdCI6MTc4NjAxNDQwMCwiZXhwIjoxNzg2MDI1MjAwfQ.L2fdjPRoanrPFlyKpSCPS8R3w-gyJuu5VJwE5_BmIjk';

    group('POST Create Community', function () {
            // Convert execution numbers into letters so no digits appear in communityName
            const vuAlpha = numberToAlpha(exec.vu.idInTest);
            const iterAlpha = numberToAlpha(exec.scenario.iterationInTest);
            const timeAlpha = numberToAlpha(Date.now());
            const randomAlpha = randomAlphaString(6);

        // Resulting format: "Community_vu_iter_timestamp_rand" (e.g. "Community_b_a_bchcjde_abcDEF")
        const communityName = `Community_${vuAlpha}_${iterAlpha}_${timeAlpha}_${randomAlpha}`;

        const payload = JSON.stringify({
            name: communityName,
            displayName: communityName,
            description: `Test community created by VU ${exec.vu.idInTest}`,
        });

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        };

        const res = http.post(`${baseUrl}/subreddits`, payload, params);

        // Debug log if status is not 200 or 201
        if (res.status !== 200 && res.status !== 201) {
            console.log(`[FAILED] Status: ${res.status} | Body: ${res.body}`);
        }

        check(res, {
            'POST status is 200 or 201': (r) => r.status === 200 || r.status === 201,
            'POST latency < 1000ms': (r) => r.timings.duration < 1000,
        });
    });

    sleep(Math.random() * 1 + 0.5);
}