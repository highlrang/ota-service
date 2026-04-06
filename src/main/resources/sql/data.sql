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
    id, code, source_type, extranet_id, supplier_product_id, supplier_sync_blocked, region_type, accommodation_type, region_id, name, address,
    latitude, longitude, thumbnail_image, business_status, check_in_time, check_out_time
)
VALUES
    (
        1, 'ACC-000001', 'EXTRANET', 1, NULL, FALSE, 'DOMESTIC', 'HOTEL_RESORT', 3, '강남 시티 호텔',
        '서울특별시 강남구 테헤란로 100',
        37.4980950, 127.0276100, 'https://cdn.ota.local/accommodations/1/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        2, 'ACC-000002', 'SUPPLIER', NULL, 'SUP-PRD-2001', FALSE, 'DOMESTIC', 'PENSION_POOL_VILLA', 11, '제주 오션 풀빌라',
        '제주특별자치도 제주시 애월해안로 200',
        33.4857000, 126.3902000, 'https://cdn.ota.local/accommodations/2/thumb.jpg', 'OPEN', '16:00:00', '11:00:00'
    ),
    (
        3, 'ACC-000003', 'EXTRANET', 1, 'SUP-PRD-3001', TRUE, 'DOMESTIC', 'HOTEL', 4, '명동 부티크 호텔',
        '서울특별시 중구 명동길 10',
        37.5636000, 126.9851000, 'https://cdn.ota.local/accommodations/3/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        4, 'ACC-000004', 'EXTRANET', 1, 'SUP-PRD-3002', FALSE, 'DOMESTIC', 'GUESTHOUSE_BNB', 5, '홍대 스테이',
        '서울특별시 마포구 와우산로 20',
        37.5563000, 126.9220000, 'https://cdn.ota.local/accommodations/4/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        5, 'ACC-000005', 'SUPPLIER', NULL, 'SUP-PRD-5001', FALSE, 'DOMESTIC', 'HOTEL_RESORT', 6, '잠실 레이크 호텔',
        '서울특별시 송파구 올림픽로 300',
        37.5133000, 127.1028000, 'https://cdn.ota.local/accommodations/5/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        6, 'ACC-000006', 'SUPPLIER', NULL, 'SUP-PRD-5002', FALSE, 'DOMESTIC', 'HOTEL_RESORT', 7, '여의도 비즈니스 호텔',
        '서울특별시 영등포구 국제금융로 20',
        37.5251000, 126.9253000, 'https://cdn.ota.local/accommodations/6/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        7, 'ACC-000007', 'SUPPLIER', NULL, 'SUP-PRD-5003', FALSE, 'DOMESTIC', 'HOTEL_RESORT', 9, '제주 하버 호텔',
        '제주특별자치도 제주시 서해안로 80',
        33.5179000, 126.5237000, 'https://cdn.ota.local/accommodations/7/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        8, 'ACC-000008', 'SUPPLIER', NULL, 'SUP-PRD-5004', FALSE, 'DOMESTIC', 'HOTEL_RESORT', 10, '서귀포 선셋 리조트',
        '제주특별자치도 서귀포시 태평로 15',
        33.2519000, 126.5600000, 'https://cdn.ota.local/accommodations/8/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    ),
    (
        9, 'ACC-000009', 'SUPPLIER', NULL, 'SUP-PRD-5005', FALSE, 'DOMESTIC', 'HOTEL_RESORT', 3, '강남 프리미어 스테이',
        '서울특별시 강남구 봉은사로 220',
        37.5078000, 127.0312000, 'https://cdn.ota.local/accommodations/9/thumb.jpg', 'OPEN', '15:00:00', '11:00:00'
    );

INSERT INTO ACCOMMODATION_DETAILS (accommodation_id, description, extra_info)
VALUES
    (1, '강남 중심에 위치한 비즈니스 호텔', '조식 유료 제공, 지하 주차장 이용 가능'),
    (2, '오션뷰와 개별 수영장을 제공하는 풀빌라', '조식 포함, 전용 주차장 이용 가능'),
    (3, '명동 쇼핑 거리 인근의 도심형 부티크 호텔', '공항 리무진 정류장 도보 3분'),
    (4, '홍대 메인 거리에 위치한 캐주얼 스테이', '셀프 체크인, 공용 라운지 제공'),
    (5, '석촌호수 인근 전망이 좋은 레이크뷰 호텔', '피트니스, 라운지, 유료 발렛'),
    (6, '여의도 금융가 중심의 비즈니스 특화 호텔', '회의실, 조식 뷔페, 셀프 세탁'),
    (7, '제주 공항과 가까운 하버뷰 호텔', '렌터카 제휴, 루프탑 바'),
    (8, '서귀포 바다 전망의 리조트형 호텔', '야외 수영장, 키즈존 운영'),
    (9, '강남 출장 수요에 특화된 프리미엄 스테이', '24시간 프런트, 공항버스 정류장 인접');

INSERT INTO EXTRANET_ACCOMMODATIONS (id, extranet_id, accommodation_id, code)
VALUES
    (1, 1, 1, 'ACC-000001'),
    (2, 1, 3, 'ACC-000003'),
    (3, 1, 4, 'ACC-000004');

INSERT INTO ACCOMMODATION_IMAGES (id, accommodation_id, image_url, image_type, sort_order)
VALUES
    (1, 1, 'https://cdn.ota.local/accommodations/1/main.jpg', 'PRIMARY', 1),
    (2, 1, 'https://cdn.ota.local/accommodations/1/lobby.jpg', 'GENERAL', 2),
    (3, 2, 'https://cdn.ota.local/accommodations/2/main.jpg', 'PRIMARY', 1),
    (4, 2, 'https://cdn.ota.local/accommodations/2/pool.jpg', 'GENERAL', 2),
    (5, 5, 'https://cdn.ota.local/accommodations/5/main.jpg', 'PRIMARY', 1),
    (6, 6, 'https://cdn.ota.local/accommodations/6/main.jpg', 'PRIMARY', 1),
    (7, 7, 'https://cdn.ota.local/accommodations/7/main.jpg', 'PRIMARY', 1),
    (8, 8, 'https://cdn.ota.local/accommodations/8/main.jpg', 'PRIMARY', 1),
    (9, 9, 'https://cdn.ota.local/accommodations/9/main.jpg', 'PRIMARY', 1);

INSERT INTO ROOMS (
    id, accommodation_id, room_code, name, description, standard_occupancy, max_occupancy, bed_type, extra_info,
    min_stay_nights, max_stay_nights
)
VALUES
    (1, 1, 'ROOM-SEOUL-0001', '스탠다드 더블', '도심 전망의 더블 객실', 2, 2, 'DOUBLE', '조식 별도', 1, 7),
    (2, 1, 'ROOM-SEOUL-0002', '디럭스 트윈', '욕조가 포함된 트윈 객실', 2, 3, 'TWIN', '엑스트라 베드 가능', 1, 5),
    (3, 2, 'ROOM-JEJU-0001', '오션 풀 스위트', '개별 수영장과 오션뷰 제공', 2, 4, 'KING', '바비큐 이용 가능', 2, 7),
    (4, 3, 'ROOM-MYEONGDONG-0001', '시티 싱글', '혼숙 여행객을 위한 컴팩트 객실', 1, 1, 'SINGLE', '넷플릭스 이용 가능', 1, 3),
    (5, 4, 'ROOM-HONGDAE-0001', '홍대 더블', '홍대 메인 스트리트와 가까운 더블 객실', 2, 2, 'DOUBLE', '셀프 체크인 지원', 1, 5),
    (6, 5, 'ROOM-JAMSIL-0001', '레이크 더블', '호수 전망의 더블 객실', 2, 2, 'DOUBLE', '고층 배정 가능', 1, 5),
    (7, 6, 'ROOM-YEOUIDO-0001', '비즈니스 퀸', '업무형 투숙에 적합한 퀸 객실', 1, 2, 'QUEEN', '책상, 유선랜 제공', 1, 5),
    (8, 7, 'ROOM-JEJU-0002', '하버 디럭스', '공항 인근 항구 전망 객실', 2, 3, 'DOUBLE', '렌터카 제휴 할인', 1, 5),
    (9, 8, 'ROOM-SEOGWIPO-0001', '선셋 패밀리', '노을 전망의 패밀리 객실', 3, 4, 'TWIN', '키즈 어메니티 제공', 1, 5),
    (10, 9, 'ROOM-GANGNAM-0003', '프리미어 킹', '출장 고객용 프리미엄 킹 객실', 2, 2, 'KING', '라운지 이용 가능', 1, 5);

INSERT INTO ROOM_IMAGES (id, room_id, image_url, image_type, sort_order)
VALUES
    (1, 1, 'https://cdn.ota.local/rooms/1/main.jpg', 'PRIMARY', 1),
    (2, 1, 'https://cdn.ota.local/rooms/1/bathroom.jpg', 'GENERAL', 2),
    (3, 2, 'https://cdn.ota.local/rooms/2/main.jpg', 'PRIMARY', 1),
    (4, 3, 'https://cdn.ota.local/rooms/3/main.jpg', 'PRIMARY', 1),
    (5, 4, 'https://cdn.ota.local/rooms/4/main.jpg', 'PRIMARY', 1),
    (6, 5, 'https://cdn.ota.local/rooms/5/main.jpg', 'PRIMARY', 1),
    (7, 6, 'https://cdn.ota.local/rooms/6/main.jpg', 'PRIMARY', 1),
    (8, 7, 'https://cdn.ota.local/rooms/7/main.jpg', 'PRIMARY', 1),
    (9, 8, 'https://cdn.ota.local/rooms/8/main.jpg', 'PRIMARY', 1),
    (10, 9, 'https://cdn.ota.local/rooms/9/main.jpg', 'PRIMARY', 1),
    (11, 10, 'https://cdn.ota.local/rooms/10/main.jpg', 'PRIMARY', 1);

INSERT INTO ROOM_RATES (
    id, room_id, rate_name, base_price, currency, sale_price, refundable_yn, rate_date
)
VALUES
    (1, 1, '스탠다드 요금', 150000.00, 'KRW', 135000.00, TRUE, '2026-04-10'),
    (2, 1, '스탠다드 요금', 150000.00, 'KRW', 135000.00, TRUE, '2026-04-11'),
    (3, 2, '디럭스 특가', 220000.00, 'KRW', 198000.00, FALSE, '2026-04-10'),
    (4, 2, '디럭스 특가', 220000.00, 'KRW', 198000.00, FALSE, '2026-04-11'),
    (5, 3, '오션 패키지', 450000.00, 'KRW', 420000.00, TRUE, '2026-04-20'),
    (6, 3, '오션 패키지', 450000.00, 'KRW', 420000.00, TRUE, '2026-04-21'),
    (7, 4, '명동 얼리버드', 120000.00, 'KRW', 99000.00, FALSE, '2026-04-15'),
    (8, 4, '명동 얼리버드', 120000.00, 'KRW', 99000.00, FALSE, '2026-04-16'),
    (9, 5, '홍대 스테이 특가', 170000.00, 'KRW', 149000.00, TRUE, '2026-04-15'),
    (10, 5, '홍대 스테이 특가', 170000.00, 'KRW', 149000.00, TRUE, '2026-04-16'),
    (11, 6, '잠실 베스트레이트', 210000.00, 'KRW', 189000.00, TRUE, '2026-04-15'),
    (12, 6, '잠실 베스트레이트', 210000.00, 'KRW', 189000.00, TRUE, '2026-04-16'),
    (13, 7, '여의도 비즈니스 특가', 190000.00, 'KRW', 175000.00, TRUE, '2026-04-15'),
    (14, 7, '여의도 비즈니스 특가', 190000.00, 'KRW', 175000.00, TRUE, '2026-04-16'),
    (15, 8, '제주 하버 세일', 160000.00, 'KRW', 145000.00, TRUE, '2026-04-15'),
    (16, 8, '제주 하버 세일', 160000.00, 'KRW', 145000.00, TRUE, '2026-04-16'),
    (17, 9, '선셋 패밀리 패키지', 260000.00, 'KRW', 238000.00, TRUE, '2026-04-15'),
    (18, 9, '선셋 패밀리 패키지', 260000.00, 'KRW', 238000.00, TRUE, '2026-04-16'),
    (19, 10, '강남 프리미어 특가', 230000.00, 'KRW', 209000.00, TRUE, '2026-04-15'),
    (20, 10, '강남 프리미어 특가', 230000.00, 'KRW', 209000.00, TRUE, '2026-04-16');

INSERT INTO SUPPLIER_ACCOMMODATIONS (
    id, source, supplier_property_id, accommodation_id, status, last_synced_at
)
VALUES
    (1, 'ONDA', 'SUP-PRD-2001', 2, 'enabled', '2026-04-01 09:00:00'),
    (2, 'ONDA', 'SUP-PRD-3001', 3, 'blocked', '2026-04-01 09:30:00'),
    (3, 'ONDA', 'SUP-PRD-3002', 4, 'enabled', '2026-04-01 09:40:00');

INSERT INTO SUPPLIER_ROOMS (
    id, source, supplier_roomtype_id, room_id, supplier_accommodation_id, status, last_synced_at
)
VALUES
    (1, 'ONDA', 'SUP-ROOM-3001', 3, 1, 'enabled', '2026-04-01 09:05:00'),
    (2, 'ONDA', 'SUP-ROOM-3002', 4, 2, 'blocked', '2026-04-01 09:35:00'),
    (3, 'ONDA', 'SUP-ROOM-3003', 5, 3, 'enabled', '2026-04-01 09:45:00');

INSERT INTO SUPPLIER_RATEPLANS (
    id, source, supplier_rateplan_id, room_rate_id, supplier_room_id, rate_name, base_price, currency, sale_price,
    refundable_yn, valid_from, valid_to, status, last_synced_at
)
VALUES
    (1, 'ONDA', 'SUP-RATE-4001', 5, 1, '오션 패키지', 450000.00, 'KRW', 420000.00,
     TRUE, '2026-04-01', '2026-12-31', 'enabled', '2026-04-01 09:10:00'),
    (2, 'ONDA', 'SUP-RATE-4002', 7, 2, '명동 얼리버드', 120000.00, 'KRW', 99000.00,
     FALSE, '2026-04-15', '2026-04-16', 'blocked', '2026-04-01 09:36:00'),
    (3, 'ONDA', 'SUP-RATE-4003', 9, 3, '홍대 스테이 특가', 170000.00, 'KRW', 149000.00,
     TRUE, '2026-04-15', '2026-04-16', 'enabled', '2026-04-01 09:46:00');

INSERT INTO SUPPLIER_RATEPLAN_INVENTORIES (
    id, room_rate_id, inventory_date, base_price, sale_price, extra_adult, extra_child, extra_infant, promotion_type, vacancy, stop_sale_yn
)
VALUES
    (1, 3, '2026-04-20', 450000.00, 420000.00, 30000.00, 15000.00, 0.00, NULL, 2, FALSE),
    (2, 3, '2026-04-21', 450000.00, 420000.00, 30000.00, 15000.00, 0.00, 'SPRING', 2, FALSE),
    (3, 7, '2026-04-15', 120000.00, 99000.00, 10000.00, 0.00, 0.00, NULL, 1, FALSE),
    (4, 8, '2026-04-16', 120000.00, 99000.00, 10000.00, 0.00, 0.00, NULL, 1, FALSE),
    (5, 9, '2026-04-15', 170000.00, 149000.00, 20000.00, 10000.00, 0.00, 'WEEKDAY', 2, FALSE),
    (6, 10, '2026-04-16', 170000.00, 149000.00, 20000.00, 10000.00, 0.00, 'WEEKDAY', 2, FALSE);

INSERT INTO ROOM_INVENTORIES (id, room_id, inventory_date, total_stock, reserved_stock, available_stock, stop_sale_yn)
VALUES
    (1, 1, '2026-04-10', 10, 2, 8, FALSE),
    (2, 1, '2026-04-11', 10, 2, 8, FALSE),
    (3, 2, '2026-04-10', 5, 1, 4, FALSE),
    (4, 2, '2026-04-11', 5, 1, 4, FALSE),
    (5, 3, '2026-04-20', 3, 1, 2, FALSE),
    (6, 3, '2026-04-21', 3, 1, 2, FALSE),
    (7, 4, '2026-04-15', 4, 0, 4, FALSE),
    (8, 4, '2026-04-16', 4, 0, 4, FALSE),
    (9, 5, '2026-04-15', 6, 1, 5, FALSE),
    (10, 5, '2026-04-16', 6, 1, 5, FALSE),
    (11, 6, '2026-04-15', 8, 2, 6, FALSE),
    (12, 6, '2026-04-16', 8, 2, 6, FALSE),
    (13, 7, '2026-04-15', 10, 3, 7, FALSE),
    (14, 7, '2026-04-16', 10, 3, 7, FALSE),
    (15, 8, '2026-04-15', 7, 1, 6, FALSE),
    (16, 8, '2026-04-16', 7, 1, 6, FALSE),
    (17, 9, '2026-04-15', 5, 0, 5, FALSE),
    (18, 9, '2026-04-16', 5, 0, 5, FALSE),
    (19, 10, '2026-04-15', 9, 2, 7, FALSE),
    (20, 10, '2026-04-16', 9, 2, 7, FALSE);

INSERT INTO RESERVATIONS (
    id, user_id, accommodation_id, room_id, room_rate_id, reservation_no,
    check_in_at, check_out_at, guest_name, guest_phone_number,
    adult_count, child_count, total_amount, reservation_status,
    requested_at, confirmed_at, cancelled_at, failed_at
)
VALUES
    (
        1, 1, 1, 1, 1, 'RSV-20260401-0001',
        '2026-04-10 15:00:00', '2026-04-12 11:00:00', '김여행', '010-1111-2222',
        2, 0, 270000.00, 'CONFIRMED',
        '2026-04-01 09:00:00', '2026-04-01 09:01:00', NULL, NULL
    ),
    (
        2, 2, 2, 3, 5, 'RSV-20260401-0002',
        '2026-04-20 16:00:00', '2026-04-22 11:00:00', '박숙박', '010-3333-4444',
        2, 1, 840000.00, 'CANCELLED',
        '2026-04-01 10:00:00', '2026-04-01 10:01:00', '2026-04-02 08:00:00', NULL
    ),
    (
        3, 2, 1, 2, 3, 'RSV-20260315-0003',
        '2026-03-20 15:00:00', '2026-03-22 11:00:00', '박숙박', '010-3333-4444',
        2, 0, 396000.00, 'COMPLETED',
        '2026-03-15 14:00:00', '2026-03-15 14:01:00', NULL, NULL
    ),
    (
        4, 1, 5, 6, 11, 'RSV-20260402-0004',
        '2026-04-15 15:00:00', '2026-04-17 11:00:00', '이호수', '010-1000-2000',
        2, 0, 378000.00, 'CONFIRMED',
        '2026-04-02 09:00:00', '2026-04-02 09:01:00', NULL, NULL
    ),
    (
        5, 2, 5, 6, 11, 'RSV-20260402-0005',
        '2026-04-15 15:00:00', '2026-04-17 11:00:00', '최레이크', '010-1000-2001',
        2, 1, 378000.00, 'COMPLETED',
        '2026-04-02 09:10:00', '2026-04-02 09:11:00', NULL, NULL
    ),
    (
        6, 1, 5, 6, 12, 'RSV-20260402-0006',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '김잠실', '010-1000-2002',
        2, 0, 189000.00, 'CONFIRMED',
        '2026-04-02 09:20:00', '2026-04-02 09:21:00', NULL, NULL
    ),
    (
        7, 1, 6, 7, 13, 'RSV-20260403-0007',
        '2026-04-15 15:00:00', '2026-04-16 11:00:00', '정여의도', '010-1000-2003',
        1, 0, 175000.00, 'CONFIRMED',
        '2026-04-03 08:00:00', '2026-04-03 08:01:00', NULL, NULL
    ),
    (
        8, 2, 6, 7, 14, 'RSV-20260403-0008',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '오금융', '010-1000-2004',
        1, 0, 175000.00, 'COMPLETED',
        '2026-04-03 08:10:00', '2026-04-03 08:11:00', NULL, NULL
    ),
    (
        9, 1, 6, 7, 13, 'RSV-20260403-0009',
        '2026-04-15 15:00:00', '2026-04-16 11:00:00', '신오피스', '010-1000-2005',
        1, 0, 175000.00, 'CONFIRMED',
        '2026-04-03 08:20:00', '2026-04-03 08:21:00', NULL, NULL
    ),
    (
        10, 1, 6, 7, 14, 'RSV-20260403-0010',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '강재무', '010-1000-2006',
        1, 0, 175000.00, 'CONFIRMED',
        '2026-04-03 08:30:00', '2026-04-03 08:31:00', NULL, NULL
    ),
    (
        11, 2, 7, 8, 15, 'RSV-20260404-0011',
        '2026-04-15 15:00:00', '2026-04-16 11:00:00', '한제주', '010-1000-2007',
        2, 0, 145000.00, 'CONFIRMED',
        '2026-04-04 09:00:00', '2026-04-04 09:01:00', NULL, NULL
    ),
    (
        12, 1, 7, 8, 16, 'RSV-20260404-0012',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '송하버', '010-1000-2008',
        2, 0, 145000.00, 'COMPLETED',
        '2026-04-04 09:10:00', '2026-04-04 09:11:00', NULL, NULL
    ),
    (
        13, 1, 8, 9, 17, 'RSV-20260404-0013',
        '2026-04-15 15:00:00', '2026-04-17 11:00:00', '임선셋', '010-1000-2009',
        2, 1, 476000.00, 'CONFIRMED',
        '2026-04-04 10:00:00', '2026-04-04 10:01:00', NULL, NULL
    ),
    (
        14, 2, 8, 9, 18, 'RSV-20260404-0014',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '강리조트', '010-1000-2010',
        3, 0, 238000.00, 'CONFIRMED',
        '2026-04-04 10:10:00', '2026-04-04 10:11:00', NULL, NULL
    ),
    (
        15, 1, 8, 9, 17, 'RSV-20260404-0015',
        '2026-04-15 15:00:00', '2026-04-16 11:00:00', '민서귀포', '010-1000-2011',
        3, 1, 238000.00, 'COMPLETED',
        '2026-04-04 10:20:00', '2026-04-04 10:21:00', NULL, NULL
    ),
    (
        16, 1, 9, 10, 19, 'RSV-20260405-0016',
        '2026-04-15 15:00:00', '2026-04-16 11:00:00', '서강남', '010-1000-2012',
        2, 0, 209000.00, 'CONFIRMED',
        '2026-04-05 09:00:00', '2026-04-05 09:01:00', NULL, NULL
    ),
    (
        17, 2, 9, 10, 20, 'RSV-20260405-0017',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '유프리미어', '010-1000-2013',
        2, 0, 209000.00, 'CONFIRMED',
        '2026-04-05 09:10:00', '2026-04-05 09:11:00', NULL, NULL
    ),
    (
        18, 1, 9, 10, 19, 'RSV-20260405-0018',
        '2026-04-15 15:00:00', '2026-04-16 11:00:00', '한비즈', '010-1000-2014',
        2, 0, 209000.00, 'COMPLETED',
        '2026-04-05 09:20:00', '2026-04-05 09:21:00', NULL, NULL
    ),
    (
        19, 2, 9, 10, 20, 'RSV-20260405-0019',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '도강남', '010-1000-2015',
        2, 0, 209000.00, 'CONFIRMED',
        '2026-04-05 09:30:00', '2026-04-05 09:31:00', NULL, NULL
    ),
    (
        20, 1, 5, 6, 12, 'RSV-20260405-0020',
        '2026-04-16 15:00:00', '2026-04-17 11:00:00', '장레이크', '010-1000-2016',
        2, 0, 189000.00, 'CONFIRMED',
        '2026-04-05 10:00:00', '2026-04-05 10:01:00', NULL, NULL
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
