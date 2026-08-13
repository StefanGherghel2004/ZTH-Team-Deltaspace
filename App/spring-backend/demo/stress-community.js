import http from 'k6/http';
import { check, group, sleep } from 'k6';
import exec from 'k6/execution';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TOKEN = __ENV.JWT_TOKEN || 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXJiYW4iLCJpYXQiOjE3ODY2MDkyMTEsImV4cCI6MTc4NjYyMDAxMX0.-Vb3f2ZCSbDEYuBFeJGnHpFcv6Xii6ipkodRSlQBsyU';

export const options = {
    scenarios: {
        create_communities: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 5 },    // Warm-up
                { duration: '1m',  target: 30 },   // Ramp-up community creators
                { duration: '1m',  target: 30 },   // Sustained load
                { duration: '30s', target: 0 },    // Cool-down
            ],
            exec: 'createCommunityJourney',
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

export function createCommunityJourney() {
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
            console.error(`[CREATE COMMUNITY FAILED] Status: ${res.status} | Body: ${res.body}`);
        }

        check(res, {
            'POST subreddit status 200/201': (r) => r.status === 200 || r.status === 201,
        });
    });

    sleep(Math.random() * 3 + 2);
}