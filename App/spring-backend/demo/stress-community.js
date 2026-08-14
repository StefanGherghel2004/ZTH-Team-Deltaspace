import http from 'k6/http';
import { check, group, sleep } from 'k6';
import exec from 'k6/execution';
//import { open } from 'k6';

// 1. INIT SCOPE: Load binary file into memory (Must be called outside VU functions)
const testImage = open('./parrots.jpg', 'b');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TOKEN = __ENV.JWT_TOKEN || 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXJiYW4iLCJpYXQiOjE3ODY2OTIyNTUsImV4cCI6MTc4NjcwMzA1NX0.C5NCFozn8wcxQQvp0MzqvY6xqVafBpcopXi3XRgMiF8';

export const options = {
    scenarios: {
        reddit_user_journey: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 5 },    // Warm-up
                { duration: '1m',  target: 20 },   // Ramp-up active users
                { duration: '1m',  target: 20 },   // Sustained load
                { duration: '30s', target: 0 },    // Cool-down
            ],
            exec: 'redditUserJourney',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.05'],            // Overall failure rate under 5%
        http_req_duration: ['p(95)<1500'],         // 95% of requests under 1.5s
    },
};

// Request Headers Configuration
const JSON_HEADERS = {
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${TOKEN}`,
        'User-Agent': 'k6-load-test',
    },
};

const MULTIPART_HEADERS = {
    headers: {
        'Authorization': `Bearer ${TOKEN}`,
        'User-Agent': 'k6-load-test',
    },
};

function randomAlphaString(length = 6) {
    const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let res = '';
    for (let i = 0; i < length; i++) {
        res += charset.charAt((Math.random() * charset.length) | 0);
    }
    return res;
}

export function redditUserJourney() {
    const vuId = exec.vu.idInTest;
    const iter = exec.scenario.iterationInTest;
    const rand = randomAlphaString();

    // Generate unique community name per iteration
    const communityName = `comm_v${vuId}_i${iter}_${Date.now()}_${rand}`;

    // -------------------------------------------------------------------------
    // STEP 1: Create Subreddit (POST /subreddits)
    // -------------------------------------------------------------------------
    group('01_POST_Create_Subreddit', function () {
        const payload = JSON.stringify({
            name: communityName,
            displayName: `Community ${communityName}`,
            description: `Test community created by VU ${vuId}`,
        });

        const res = http.post(`${BASE_URL}/subreddits`, payload, JSON_HEADERS);

        check(res, {
            'Subreddit created (200/201)': (r) => r.status === 200 || r.status === 201,
        });

        if (res.status !== 200 && res.status !== 201) {
            console.error(`[CREATE SUBREDDIT FAILED] Status: ${res.status} | Body: ${res.body}`);
        }
    });

    sleep(1); // Short pacing pause

    // -------------------------------------------------------------------------
    // STEP 2: Create Post in Subreddit (POST /posts - multipart/form-data)
    // -------------------------------------------------------------------------
    group('02_POST_Create_Post', function () {
        const postData = {
            title: `Post in ${communityName}`,
            content: 'Post content goes here...',
            subreddit: communityName,
            image: http.file(testImage, 'parrots.jpg', 'image/jpeg'),
            filter: '0', // Numeric ID stringified for multipart payload
        };

        const res = http.post(`${BASE_URL}/posts`, postData, MULTIPART_HEADERS);

        check(res, {
            'Post created (200/201)': (r) => r.status === 200 || r.status === 201,
        });

        if (res.status !== 200 && res.status !== 201) {
            console.error(`[CREATE POST FAILED] Status: ${res.status} | Body: ${res.body}`);
        }
    });

    sleep(1);

    // -------------------------------------------------------------------------
    // STEP 3: Get Subreddits (GET /subreddits)
    // -------------------------------------------------------------------------
    group('03_GET_Subreddits', function () {
        const res = http.get(`${BASE_URL}/subreddits`, JSON_HEADERS);

        check(res, {
            'GET subreddits status 200': (r) => r.status === 200,
            'GET subreddits response body present': (r) => r.body && r.body.length > 0,
        });
    });

    sleep(1);

    // -------------------------------------------------------------------------
    // STEP 4: Get Posts (GET /posts)
    // -------------------------------------------------------------------------
    group('04_GET_Posts', function () {
        // Querying all posts or posts filtered by the created subreddit
        const res = http.get(`${BASE_URL}/posts?subreddit=${communityName}`, JSON_HEADERS);

        check(res, {
            'GET posts status 200': (r) => r.status === 200,
            'GET posts response body present': (r) => r.body && r.body.length > 0,
        });
    });

    // Pacing think-time between user journeys
    sleep(Math.random() * 3 + 2);
}