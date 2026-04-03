INSERT INTO REGIONS (id, parent_region_id, name, code, depth, full_name, sort_order, is_active)
VALUES
    (1, NULL, '대한민국', 'KR', 1, '대한민국', 1, TRUE),
    (2, 1, '서울', 'KR-11', 2, '대한민국 서울', 1, TRUE),
    (3, 2, '강남구', 'KR-11-11680', 3, '대한민국 서울 강남구', 1, TRUE),
    (4, 2, '중구', 'KR-11-11140', 3, '대한민국 서울 중구', 2, TRUE),
    (5, 2, '마포구', 'KR-11-11440', 3, '대한민국 서울 마포구', 3, TRUE),
    (6, 2, '송파구', 'KR-11-11710', 3, '대한민국 서울 송파구', 4, TRUE),
    (7, 2, '영등포구', 'KR-11-11560', 3, '대한민국 서울 영등포구', 5, TRUE),
    (8, 1, '제주특별자치도', 'KR-49', 2, '대한민국 제주특별자치도', 2, TRUE),
    (9, 8, '제주시', 'KR-49-50110', 3, '대한민국 제주특별자치도 제주시', 1, TRUE),
    (10, 8, '서귀포시', 'KR-49-50130', 3, '대한민국 제주특별자치도 서귀포시', 2, TRUE),
    (11, 9, '애월읍', 'KR-49-50110-01', 4, '대한민국 제주특별자치도 제주시 애월읍', 1, TRUE),
    (12, 9, '조천읍', 'KR-49-50110-02', 4, '대한민국 제주특별자치도 제주시 조천읍', 2, TRUE),
    (13, 9, '한림읍', 'KR-49-50110-03', 4, '대한민국 제주특별자치도 제주시 한림읍', 3, TRUE),
    (14, 10, '성산읍', 'KR-49-50130-01', 4, '대한민국 제주특별자치도 서귀포시 성산읍', 1, TRUE),
    (15, 10, '안덕면', 'KR-49-50130-02', 4, '대한민국 제주특별자치도 서귀포시 안덕면', 2, TRUE),
    (16, 10, '중문동', 'KR-49-50130-03', 4, '대한민국 제주특별자치도 서귀포시 중문동', 3, TRUE);

-- guest1234!
INSERT INTO USERS (id, email, code, password_hash, name, phone_number, status, access_token, refresh_token, refresh_token_expired_at, last_login_at)
VALUES
    (1, 'guest1@ota.local', 'USER-0001', '$2a$10$bZc7A77MS2kiypJ0Y.1PJesVYXaW6DvKcRctdP1Hibdaq0EFY9z2i', '이방문', '010-1111-2222', 'ACTIVE', NULL, NULL, NULL, '2026-04-01 10:00:00'),
    (2, 'guest2@ota.local', 'USER-0002', '$2a$10$bZc7A77MS2kiypJ0Y.1PJesVYXaW6DvKcRctdP1Hibdaq0EFY9z2i', '김단골', '010-3333-4444', 'ACTIVE', NULL, NULL, NULL, '2026-04-01 11:00:00');

INSERT INTO EXTRANETS (id, email, code, password_hash, name, phone_number, status)
VALUES
    (1, 'guest1@ota.local', 'EXTRANET-0001', '$2a$10$bZc7A77MS2kiypJ0Y.1PJesVYXaW6DvKcRctdP1Hibdaq0EFY9z2i', '강남호텔 운영사', '02-555-1234', 'ACTIVE');

INSERT INTO ACCOMMODATIONS (
    id, code, source_type, extranet_id, supplier_product_id, region_type, accommodation_type, region_id, name, address,
    latitude, longitude, thumbnail_image, business_status, check_in_time, check_out_time
)
VALUES
    (
        1, 'ACC-000001', 'EXTRANET', 1, NULL, 'DOMESTIC', 'HOTEL_RESORT', 3, '강남 시티 호텔',
        '서울특별시 강남구 테헤란로 100',
        37.4980950, 127.0276100, 'https://cdn.ota.local/accommodations/1/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        2, 'ACC-000002', 'SUPPLIER', NULL, 'SUP-PRD-2001', 'DOMESTIC', 'PENSION_POOL_VILLA', 11, '제주 오션 풀빌라',
        '제주특별자치도 제주시 애월해안로 200',
        33.4857000, 126.3902000, 'https://cdn.ota.local/accommodations/2/thumb.jpg', 'OPEN', '16:00:00', '11:00:00'
    );

INSERT INTO ACCOMMODATION_DETAILS (accommodation_id, description, extra_info)
VALUES
    (1, '강남 중심에 위치한 비즈니스 호텔', '조식 유료 제공, 지하 주차장 이용 가능'),
    (2, '오션뷰와 개별 수영장을 제공하는 풀빌라', '조식 포함, 전용 주차장 이용 가능');

INSERT INTO EXTRANET_ACCOMMODATIONS (id, extranet_id, accommodation_id, code)
VALUES
    (1, 1, 1, 'ACC-000001');

INSERT INTO ACCOMMODATION_IMAGES (id, accommodation_id, image_url, image_type, sort_order)
VALUES
    (1, 1, 'https://cdn.ota.local/accommodations/1/main.jpg', 'PRIMARY', 1),
    (2, 1, 'https://cdn.ota.local/accommodations/1/lobby.jpg', 'GENERAL', 2),
    (3, 2, 'https://cdn.ota.local/accommodations/2/main.jpg', 'PRIMARY', 1),
    (4, 2, 'https://cdn.ota.local/accommodations/2/pool.jpg', 'GENERAL', 2);

INSERT INTO ROOMS (
    id, accommodation_id, room_code, name, description, standard_occupancy, max_occupancy, bed_type, extra_info,
    min_stay_nights, max_stay_nights
)
VALUES
    (1, 1, 'ROOM-SEOUL-0001', '스탠다드 더블', '도심 전망의 더블 객실', 2, 2, 'DOUBLE', '조식 별도', 1, 7),
    (2, 1, 'ROOM-SEOUL-0002', '디럭스 트윈', '욕조가 포함된 트윈 객실', 2, 3, 'TWIN', '엑스트라 베드 가능', 1, 5),
    (3, 2, 'ROOM-JEJU-0001', '오션 풀 스위트', '개별 수영장과 오션뷰 제공', 2, 4, 'KING', '바비큐 이용 가능', 2, 7);

INSERT INTO ROOM_IMAGES (id, room_id, image_url, image_type, sort_order)
VALUES
    (1, 1, 'https://cdn.ota.local/rooms/1/main.jpg', 'PRIMARY', 1),
    (2, 1, 'https://cdn.ota.local/rooms/1/bathroom.jpg', 'GENERAL', 2),
    (3, 2, 'https://cdn.ota.local/rooms/2/main.jpg', 'PRIMARY', 1),
    (4, 3, 'https://cdn.ota.local/rooms/3/main.jpg', 'PRIMARY', 1);

INSERT INTO ROOM_RATES (
    id, room_id, rate_name, base_price, currency, sale_price, refundable_yn, rate_date
)
VALUES
    (1, 1, '스탠다드 요금', 150000.00, 'KRW', 135000.00, TRUE, '2026-04-10'),
    (2, 1, '스탠다드 요금', 150000.00, 'KRW', 135000.00, TRUE, '2026-04-11'),
    (3, 2, '디럭스 특가', 220000.00, 'KRW', 198000.00, FALSE, '2026-04-10'),
    (4, 2, '디럭스 특가', 220000.00, 'KRW', 198000.00, FALSE, '2026-04-11'),
    (5, 3, '오션 패키지', 450000.00, 'KRW', 420000.00, TRUE, '2026-04-20'),
    (6, 3, '오션 패키지', 450000.00, 'KRW', 420000.00, TRUE, '2026-04-21');

INSERT INTO EXTRANET_ROOM_RATES (
    id, room_id, room_rate_id, rate_name, base_price, currency, sale_price, refundable_yn, rate_date, active_yn, deleted_at
)
VALUES
    (1, 1, 1, '스탠다드 요금', 150000.00, 'KRW', 135000.00, TRUE, '2026-04-10', TRUE, NULL),
    (2, 1, 2, '스탠다드 요금', 150000.00, 'KRW', 135000.00, TRUE, '2026-04-11', TRUE, NULL),
    (3, 2, 3, '디럭스 특가', 220000.00, 'KRW', 198000.00, FALSE, '2026-04-10', TRUE, NULL),
    (4, 2, 4, '디럭스 특가', 220000.00, 'KRW', 198000.00, FALSE, '2026-04-11', TRUE, NULL);

INSERT INTO SUPPLIER_ACCOMMODATIONS (
    id, source, supplier_property_id, accommodation_id, status, last_synced_at
)
VALUES
    (1, 'ONDA', 'SUP-PRD-2001', 2, 'enabled', '2026-04-01 09:00:00');

INSERT INTO SUPPLIER_ROOMS (
    id, source, supplier_roomtype_id, room_id, supplier_accommodation_id, status, last_synced_at
)
VALUES
    (1, 'ONDA', 'SUP-ROOM-3001', 3, 1, 'enabled', '2026-04-01 09:05:00');

INSERT INTO SUPPLIER_RATEPLANS (
    id, source, supplier_rateplan_id, room_rate_id, supplier_room_id, rate_name, base_price, currency, sale_price,
    refundable_yn, valid_from, valid_to, status, last_synced_at
)
VALUES
    (1, 'ONDA', 'SUP-RATE-4001', 5, 1, '오션 패키지', 450000.00, 'KRW', 420000.00,
     TRUE, '2026-04-01', '2026-12-31', 'enabled', '2026-04-01 09:10:00');

INSERT INTO SUPPLIER_RATEPLAN_INVENTORIES (
    id, room_rate_id, inventory_date, base_price, sale_price, extra_adult, extra_child, extra_infant, promotion_type, vacancy, stop_sale_yn
)
VALUES
    (1, 3, '2026-04-20', 450000.00, 420000.00, 30000.00, 15000.00, 0.00, NULL, 2, FALSE),
    (2, 3, '2026-04-21', 450000.00, 420000.00, 30000.00, 15000.00, 0.00, 'SPRING', 2, FALSE);

INSERT INTO ROOM_INVENTORIES (id, room_id, inventory_date, total_stock, reserved_stock, available_stock, stop_sale_yn)
VALUES
    (1, 1, '2026-04-10', 10, 2, 8, FALSE),
    (2, 1, '2026-04-11', 10, 2, 8, FALSE),
    (3, 2, '2026-04-10', 5, 1, 4, FALSE),
    (4, 2, '2026-04-11', 5, 1, 4, FALSE),
    (5, 3, '2026-04-20', 3, 1, 2, FALSE),
    (6, 3, '2026-04-21', 3, 1, 2, FALSE);

INSERT INTO RESERVATIONS (
    id, user_id, accommodation_id, room_id, room_rate_id, reservation_no,
    check_in_date, check_out_date, guest_name, guest_phone_number,
    adult_count, child_count, total_amount, reservation_status,
    requested_at, confirmed_at, cancelled_at, failed_at
)
VALUES
    (
        1, 1, 1, 1, 1, 'RSV-20260401-0001',
        '2026-04-10', '2026-04-12', '김여행', '010-1111-2222',
        2, 0, 270000.00, 'CONFIRMED',
        '2026-04-01 09:00:00', '2026-04-01 09:01:00', NULL, NULL
    ),
    (
        2, 2, 2, 3, 5, 'RSV-20260401-0002',
        '2026-04-20', '2026-04-22', '박숙박', '010-3333-4444',
        2, 1, 840000.00, 'CANCELLED',
        '2026-04-01 10:00:00', '2026-04-01 10:01:00', '2026-04-02 08:00:00', NULL
    ),
    (
        3, 2, 1, 2, 3, 'RSV-20260315-0003',
        '2026-03-20', '2026-03-22', '박숙박', '010-3333-4444',
        2, 0, 396000.00, 'COMPLETED',
        '2026-03-15 14:00:00', '2026-03-15 14:01:00', NULL, NULL
    );

INSERT INTO PAYMENTS (
    id, reservation_id, payment_no, payment_method, payment_amount, currency,
    payment_status, approved_at, failed_at
)
VALUES
    (1, 1, 'PAY-20260401-0001', 'CARD', 270000.00, 'KRW', 'PAID', '2026-04-01 09:02:00', NULL),
    (2, 2, 'PAY-20260401-0002', 'CARD', 840000.00, 'KRW', 'REFUNDED', '2026-04-01 10:02:00', NULL),
    (3, 3, 'PAY-20260315-0003', 'CARD', 396000.00, 'KRW', 'PAID', '2026-03-15 14:02:00', NULL);

INSERT INTO REFUNDS (id, payment_id, refund_no, refund_amount, refund_reason, refunded_at)
VALUES
    (1, 2, 'RFD-20260402-0001', 840000.00, '사용자 요청 취소', '2026-04-02 08:10:00');
