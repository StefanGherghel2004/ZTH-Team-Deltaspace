import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
          // Normal load
        { duration: '1m',  target: 300 },  // High load
        { duration: '1m',  target: 600 },
        { duration: '1m',  target: 900 },
        { duration: '1m',  target: 1200 },// Stress threshold
        { duration: '30s', target: 0 },    // Ramp-down
    ],
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1000']// Keep failed requests under 5%
    },
};

export default function () {
    const res = http.get('http://localhost:8080/posts');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'latency < 900ms': (r) => r.timings.duration < 1000,
    });

    sleep(Math.random() * 1 + 0.5);
}