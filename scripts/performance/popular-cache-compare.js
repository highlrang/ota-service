import http from "k6/http";
import { check, sleep } from "k6";
import { Trend } from "k6/metrics";

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";
const accommodationType = __ENV.ACCOMMODATION_TYPE || "HOTEL_RESORT";

const coldDuration = new Trend("popular_cold_duration", true);
const warmDuration = new Trend("popular_warm_duration", true);

export const options = {
    scenarios: {
        cold_cache: {
            executor: "shared-iterations",
            exec: "cold_cache",
            vus: 1,
            iterations: 1,
            maxDuration: "30s",
        },
        warm_cache: {
            executor: "constant-vus",
            exec: "warm_cache",
            startTime: "3s",
            vus: Number(__ENV.WARM_VUS || 10),
            duration: __ENV.WARM_DURATION || "30s",
            gracefulStop: "30s",
        },
    },
    thresholds: {
        http_req_failed: ["rate<0.01"],
        popular_cold_duration: ["max<2000"],
        popular_warm_duration: ["p(95)<1000"],
    },
};

function popularUrl() {
    return `${baseUrl}/api/customer/accommodations/popular?accommodationType=${accommodationType}`;
}

export function cold_cache() {
    const response = http.get(popularUrl());
    coldDuration.add(response.timings.duration);

    check(response, {
        "cold popular api returns 200": (res) => res.status === 200,
        "cold popular api returns success": (res) => {
            try {
                const body = JSON.parse(res.body);
                return body.success === true;
            } catch (error) {
                return false;
            }
        },
    });
}

export function warm_cache() {
    const response = http.get(popularUrl());
    warmDuration.add(response.timings.duration);

    check(response, {
        "warm popular api returns 200": (res) => res.status === 200,
        "warm popular api returns success": (res) => {
            try {
                const body = JSON.parse(res.body);
                return body.success === true;
            } catch (error) {
                return false;
            }
        },
    });

    sleep(0.2);
}
