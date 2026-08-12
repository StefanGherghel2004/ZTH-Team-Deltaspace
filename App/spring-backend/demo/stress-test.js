import http from 'k6/http';
import { check, sleep } from 'k6';


const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
    scenarios: {
        browse_posts: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 300 },
                { duration: '2m', target: 600 },
                { duration: '2m', target: 900 },
            ],
            exec: 'browsePosts',
        },

        users: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 50 },
                { duration: '2m', target: 100 },
                { duration: '2m', target: 150 },
            ],
            exec: 'getUsers',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],      // Error rate under 1%
        http_req_duration: ['p(95)<1000'],   // 95% of requests under 1000ms
    },
};

export function browsePosts() {
    const res = http.get(`${BASE_URL}/posts`);

    check(res, {
        'posts status 200': (r) => r.status === 200,
    });

    sleep(Math.random() * 2 + 1); // Sleep 1-3 seconds
}

export function getUsers() {
    const res = http.get(`${BASE_URL}/users`);

    check(res, {
        'users status 200': (r) => r.status === 200,
    });

    sleep(Math.random() * 3 + 1); // Sleep 1-4 seconds
}