import http from 'k6/http';
import { check, group, sleep } from 'k6';
import exec from 'k6/execution';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TOKEN = __ENV.JWT_TOKEN || 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXJiYW4iLCJpYXQiOjE3ODY1MzY0NjIsImV4cCI6MTc4NjU0NzI2Mn0.Kq0qUVICMBARvpgTrOrtBPJA8FOmRqccJsnZcphvC8Q';

export const options = {
    scenarios: {

        browse_communities: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 50 },   // Warm-up
                { duration: '1m',  target: 500 },  // Ramp-up readers
                { duration: '2m',  target: 500 },  // Sustained load
                { duration: '30s', target: 0 },    // Cool-down
            ],
            exec: 'browseJourney',
        },


        create_communities: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 10 },   // Warm-up
                { duration: '1m',  target: 90 },   // Ramp-up creators
                { duration: '2m',  target: 90 },   // Sustained load
                { duration: '30s', target: 0 },    // Cool-down
            ],
            exec: 'createJourney',
        },
    },

    thresholds: {
        http_req_failed: ['rate<0.02'],
        http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    },
};

const COMMON_PARAMS = {
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${TOKEN}`,
        'User-Agent': 'k6-load-test',
    },
};

function randomAlphaString(length) {
    const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let res = '';
    for (let i = 0; i < length; i++) {
        res += charset.charAt((Math.random() * charset.length) | 0);
    }
    return res;
}

export function browseJourney() {
    let communityId = null;


    group('GET List Subreddits', function () {
        const page = Math.floor(Math.random() * 5);
        const res = http.get(`${BASE_URL}/subreddits`, COMMON_PARAMS);

        const pass = check(res, {
            'GET list status 200': (r) => r.status === 200,
        });


        if (pass && res.json()) {
            const body = res.json();
            const items = Array.isArray(body) ? body : (body.content || []);
            if (items.length > 0) {
                communityId = items[Math.floor(Math.random() * items.length)].id;
            }
        }
    });

    sleep(Math.random() * 1.5 + 0.5);


    if (communityId) {
        group('GET Single Subreddit', function () {
            const res = http.get(`${BASE_URL}/subreddits/${communityId}`, COMMON_PARAMS);

            check(res, {
                'GET single status 200': (r) => r.status === 200,
            });
        });

        sleep(Math.random() * 2 + 1); // Think time (1s - 3s)
    }
}

export function createJourney() {
    group('POST Create Community', function () {
        const vuId = exec.vu.idInTest;
        const iter = exec.scenario.iterationInTest;
        const rand = randomAlphaString(6);
        const communityName = `Comm_v${vuId}_i${iter}_${Date.now()}_${rand}`;

        const payload = JSON.stringify({
            name: communityName,
            displayName: `Community ${communityName}`,
            description: `Test community created by VU ${vuId}`,
        });

        const res = http.post(`${BASE_URL}/subreddits`, payload, COMMON_PARAMS);

        if (res.status !== 200 && res.status !== 201) {
            console.error(`[CREATE FAILED] Status: ${res.status} | Body: ${res.body}`);
        }

        check(res, {
            'POST status 200/201': (r) => r.status === 200 || r.status === 201,
        });
    });

    sleep(Math.random() * 3 + 2); // Creators pause longer (2s - 5s)
}