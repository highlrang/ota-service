import http from "k6/http";
import { check, sleep } from "k6";

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";
const accommodationCode = __ENV.ACCOMMODATION_CODE || "ACC-000001";
const roomCode = __ENV.ROOM_CODE || "ROOM-SEOUL-0001";
const checkInAt = __ENV.CHECK_IN_AT || "2026-04-10 15:00";
const checkOutAt = __ENV.CHECK_OUT_AT || "2026-04-11 11:00";
const paymentAmount = __ENV.PAYMENT_AMOUNT || "135000";
const paymentMethod = __ENV.PAYMENT_METHOD || "CARD";

export const options = {
    scenarios: {
        reservation_race: {
            executor: "shared-iterations",
            vus: Number(__ENV.VUS || 20),
            iterations: Number(__ENV.ITERATIONS || 20),
            maxDuration: "1m",
        },
    },
    thresholds: {
        http_req_failed: ["rate<0.95"],
        http_req_duration: ["p(95)<3000"],
    },
};

export default function () {
    const suffix = `${__VU}-${__ITER}-${Date.now()}`;
    const payload = JSON.stringify({
        guestName: `lock-test-${suffix}`,
        guestPhoneNumber: `010-${String(__VU).padStart(4, "0")}-${String(__ITER).padStart(4, "0")}`,
        accommodationCode,
        roomCode,
        checkInAt,
        checkOutAt,
        paymentMethod,
        paymentAmount: Number(paymentAmount),
        adultCount: 2,
        childCount: 0,
    });

    const response = http.post(`${baseUrl}/api/customer/reservations`, payload, {
        headers: {
            "Content-Type": "application/json",
        },
    });

    check(response, {
        "reservation endpoint responds": (res) => res.status === 200,
        "response is api envelope": (res) => {
            try {
                const body = JSON.parse(res.body);
                return typeof body.success === "boolean";
            } catch (error) {
                return false;
            }
        },
    });

    sleep(0.1);
}
