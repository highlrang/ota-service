SET NAMES utf8mb4;

DROP TABLE IF EXISTS REFUNDS;
DROP TABLE IF EXISTS PAYMENTS;
DROP TABLE IF EXISTS RESERVATIONS;
DROP TABLE IF EXISTS ROOM_INVENTORIES;
DROP TABLE IF EXISTS ROOM_RATES;
DROP TABLE IF EXISTS ROOM_IMAGES;
DROP TABLE IF EXISTS ROOMS;
DROP TABLE IF EXISTS ACCOMMODATION_IMAGES;
DROP TABLE IF EXISTS ACCOMMODATION_DETAILS;
DROP TABLE IF EXISTS ACCOMMODATIONS;
DROP TABLE IF EXISTS SELLERS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS REGIONS;

CREATE TABLE REGIONS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '지역 ID',
    parent_region_id BIGINT NULL COMMENT '상위 지역 ID, 최상위는 NULL',
    name VARCHAR(100) NOT NULL COMMENT '지역명',
    code VARCHAR(50) NOT NULL COMMENT '지역 코드',
    depth INT NOT NULL COMMENT '지역 depth',
    full_name VARCHAR(255) NOT NULL COMMENT '전체 지역명',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '사용 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_regions_code (code),
    KEY idx_regions_parent_region_id (parent_region_id),
    KEY idx_regions_depth_sort_order (depth, sort_order),
    CONSTRAINT fk_regions_parent_region
        FOREIGN KEY (parent_region_id) REFERENCES REGIONS (id),
    CONSTRAINT chk_regions_depth CHECK (depth >= 1),
    CONSTRAINT chk_regions_sort_order CHECK (sort_order >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='숙소 지역 분류 정보';

CREATE TABLE USERS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '사용자 ID',
    email VARCHAR(255) NOT NULL COMMENT '로그인 이메일',
    password_hash VARCHAR(255) NOT NULL COMMENT '비밀번호 해시',
    name VARCHAR(100) NOT NULL COMMENT '사용자명',
    phone_number VARCHAR(30) NOT NULL COMMENT '연락처',
    status VARCHAR(30) NOT NULL COMMENT '계정 상태',
    access_token VARCHAR(512) NULL COMMENT '현재 액세스 토큰',
    refresh_token VARCHAR(512) NULL COMMENT '리프레시 토큰',
    refresh_token_expired_at DATETIME NULL COMMENT '리프레시 토큰 만료 일시',
    last_login_at DATETIME NULL COMMENT '마지막 로그인 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    KEY idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='고객 사용자 계정 정보';

CREATE TABLE SELLERS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '판매자 ID',
    email VARCHAR(255) NOT NULL COMMENT '로그인 이메일',
    password_hash VARCHAR(255) NOT NULL COMMENT '비밀번호 해시',
    name VARCHAR(100) NOT NULL COMMENT '판매자명',
    phone_number VARCHAR(30) NOT NULL COMMENT '연락처',
    status VARCHAR(30) NOT NULL COMMENT '계정 상태',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sellers_email (email),
    KEY idx_sellers_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Extranet 판매자 계정 정보';

CREATE TABLE ACCOMMODATIONS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '숙소 ID',
    source_type ENUM('EXTRANET', 'SUPPLIER') NOT NULL COMMENT 'Extranet / Supplier',
    seller_id BIGINT NULL COMMENT 'Extranet 판매자 ID',
    external_product_id VARCHAR(100) NULL COMMENT '외부 공급사 상품 번호',
    region_type ENUM('DOMESTIC', 'OVERSEAS') NOT NULL COMMENT '국내 / 해외',
    accommodation_type ENUM(
        'HOTEL_RESORT',
        'MOTEL',
        'PENSION_POOL_VILLA',
        'CAMPING_GLAMPING',
        'GUESTHOUSE_HANOK',
        'HOTEL',
        'HOSTEL',
        'GUESTHOUSE_BNB'
    ) NOT NULL COMMENT '업소 종류',
    region_id BIGINT NOT NULL COMMENT '지역 ID',
    name VARCHAR(255) NOT NULL COMMENT '숙소명',
    address VARCHAR(500) NOT NULL COMMENT '주소',
    latitude DECIMAL(10,7) NULL COMMENT '위도',
    longitude DECIMAL(10,7) NULL COMMENT '경도',
    thumbnail_image VARCHAR(255) NULL COMMENT '썸네일 이미지',
    business_status ENUM('OPEN', 'CLOSED', 'STOP_SALE') NOT NULL COMMENT '숙소 영업 상태',
    check_in_time TIME NOT NULL COMMENT '체크인 시간',
    check_out_time TIME NOT NULL COMMENT '체크아웃 시간',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY idx_accommodations_seller_id (seller_id),
    KEY idx_accommodations_external_product_id (external_product_id),
    KEY idx_accommodations_region_id (region_id),
    KEY idx_accommodations_source_type (source_type),
    KEY idx_accommodations_region_type_accommodation_type (region_type, accommodation_type),
    KEY idx_accommodations_business_status (business_status),
    CONSTRAINT fk_accommodations_seller
        FOREIGN KEY (seller_id) REFERENCES SELLERS (id),
    CONSTRAINT fk_accommodations_region
        FOREIGN KEY (region_id) REFERENCES REGIONS (id),
    CONSTRAINT chk_accommodations_latitude CHECK (latitude IS NULL OR (latitude >= -90 AND latitude <= 90)),
    CONSTRAINT chk_accommodations_longitude CHECK (longitude IS NULL OR (longitude >= -180 AND longitude <= 180)),
    CONSTRAINT chk_accommodations_source_owner CHECK (
        (source_type = 'EXTRANET' AND seller_id IS NOT NULL AND external_product_id IS NULL) OR
        (source_type = 'SUPPLIER' AND seller_id IS NULL AND external_product_id IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='숙소 기본 정보';

CREATE TABLE ACCOMMODATION_DETAILS (
    accommodation_id BIGINT NOT NULL COMMENT '숙소 ID',
    description TEXT NULL COMMENT '숙소 설명',
    extra_info VARCHAR(1000) NULL COMMENT '조식, 주차장 등 기타 정보',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (accommodation_id),
    CONSTRAINT fk_accommodation_details_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATIONS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='숙소 상세 설명 정보';

CREATE TABLE ACCOMMODATION_IMAGES (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '숙소 이미지 ID',
    accommodation_id BIGINT NOT NULL COMMENT '숙소 ID',
    image_url VARCHAR(1000) NOT NULL COMMENT '이미지 URL',
    image_type VARCHAR(50) NOT NULL COMMENT '대표/일반/썸네일 구분',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '노출 순서',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY idx_accommodation_images_accommodation_id (accommodation_id),
    KEY idx_accommodation_images_sort_order (accommodation_id, sort_order),
    CONSTRAINT fk_accommodation_images_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATIONS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='숙소 이미지 정보';

CREATE TABLE ROOMS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '객실 ID',
    accommodation_id BIGINT NOT NULL COMMENT '숙소 ID',
    room_code VARCHAR(100) NOT NULL COMMENT '객실 내부 코드',
    name VARCHAR(255) NOT NULL COMMENT '객실명',
    description TEXT NULL COMMENT '객실 설명',
    standard_occupancy INT NOT NULL COMMENT '기준 인원',
    max_occupancy INT NOT NULL COMMENT '최대 인원',
    bed_type VARCHAR(100) NULL COMMENT '침대 유형',
    extra_info VARCHAR(500) NULL COMMENT '기타 정보',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rooms_room_code (room_code),
    KEY idx_rooms_accommodation_id (accommodation_id),
    CONSTRAINT fk_rooms_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATIONS (id),
    CONSTRAINT chk_rooms_standard_occupancy CHECK (standard_occupancy > 0),
    CONSTRAINT chk_rooms_max_occupancy CHECK (max_occupancy > 0),
    CONSTRAINT chk_rooms_occupancy_order CHECK (max_occupancy >= standard_occupancy)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='객실 기본 정보';

CREATE TABLE ROOM_IMAGES (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '객실 이미지 ID',
    room_id BIGINT NOT NULL COMMENT '객실 ID',
    image_url VARCHAR(1000) NOT NULL COMMENT '이미지 URL',
    image_type VARCHAR(50) NOT NULL COMMENT '대표/일반/썸네일 구분',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '노출 순서',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY idx_room_images_room_id (room_id),
    KEY idx_room_images_room_id_sort_order (room_id, sort_order),
    CONSTRAINT fk_room_images_room
        FOREIGN KEY (room_id) REFERENCES ROOMS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='객실 이미지 정보';

CREATE TABLE ROOM_RATES (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '요금 ID',
    room_id BIGINT NOT NULL COMMENT '객실 ID',
    rate_name VARCHAR(255) NOT NULL COMMENT '요금명',
    base_price DECIMAL(12,2) NOT NULL COMMENT '기본 요금',
    currency VARCHAR(10) NOT NULL COMMENT '통화',
    sale_price DECIMAL(12,2) NOT NULL COMMENT '판매 요금',
    refundable_yn BOOLEAN NOT NULL DEFAULT FALSE COMMENT '환불 가능 여부',
    min_stay_nights INT NOT NULL DEFAULT 1 COMMENT '최소 숙박 일수',
    max_stay_nights INT NOT NULL DEFAULT 1 COMMENT '최대 숙박 일수',
    valid_from DATE NOT NULL COMMENT '적용 시작일',
    valid_to DATE NOT NULL COMMENT '적용 종료일',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY idx_room_rates_room_id (room_id),
    KEY idx_room_rates_valid_range (room_id, valid_from, valid_to),
    CONSTRAINT fk_room_rates_room
        FOREIGN KEY (room_id) REFERENCES ROOMS (id),
    CONSTRAINT chk_room_rates_base_price CHECK (base_price >= 0),
    CONSTRAINT chk_room_rates_sale_price CHECK (sale_price >= 0),
    CONSTRAINT chk_room_rates_min_stay_nights CHECK (min_stay_nights > 0),
    CONSTRAINT chk_room_rates_max_stay_nights CHECK (max_stay_nights > 0),
    CONSTRAINT chk_room_rates_stay_nights_order CHECK (max_stay_nights >= min_stay_nights),
    CONSTRAINT chk_room_rates_valid_range CHECK (valid_to >= valid_from)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='객실별 판매 요금 정책';

CREATE TABLE ROOM_INVENTORIES (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '재고 ID',
    room_id BIGINT NOT NULL COMMENT '객실 ID',
    inventory_date DATE NOT NULL COMMENT '재고 기준 일자',
    total_stock INT NOT NULL COMMENT '전체 재고 수량',
    reserved_stock INT NOT NULL DEFAULT 0 COMMENT '예약된 수량',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '판매 가능 수량',
    stop_sale_yn BOOLEAN NOT NULL DEFAULT FALSE COMMENT '판매 중지 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_inventories_room_id_inventory_date (room_id, inventory_date),
    KEY idx_room_inventories_inventory_date (inventory_date),
    CONSTRAINT fk_room_inventories_room
        FOREIGN KEY (room_id) REFERENCES ROOMS (id),
    CONSTRAINT chk_room_inventories_total_stock CHECK (total_stock >= 0),
    CONSTRAINT chk_room_inventories_reserved_stock CHECK (reserved_stock >= 0),
    CONSTRAINT chk_room_inventories_available_stock CHECK (available_stock >= 0),
    CONSTRAINT chk_room_inventories_stock_order CHECK (total_stock >= reserved_stock),
    CONSTRAINT chk_room_inventories_available_stock_limit CHECK (available_stock <= total_stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='객실 날짜별 재고 정보';

CREATE TABLE RESERVATIONS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '예약 ID',
    user_id BIGINT NOT NULL COMMENT '예약 사용자 ID',
    accommodation_id BIGINT NOT NULL COMMENT '숙소 ID',
    room_id BIGINT NOT NULL COMMENT '객실 ID',
    room_rate_id BIGINT NOT NULL COMMENT '적용 요금 ID',
    reservation_no VARCHAR(100) NOT NULL COMMENT '예약 번호',
    check_in_date DATE NOT NULL COMMENT '체크인 날짜',
    check_out_date DATE NOT NULL COMMENT '체크아웃 날짜',
    guest_name VARCHAR(100) NOT NULL COMMENT '투숙객명',
    guest_phone_number VARCHAR(30) NOT NULL COMMENT '투숙객 연락처',
    adult_count INT NOT NULL DEFAULT 1 COMMENT '성인 수',
    child_count INT NOT NULL DEFAULT 0 COMMENT '아동 수',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '총 예약 금액',
    reservation_status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'FAILED') NOT NULL COMMENT '예약 상태',
    requested_at DATETIME NULL COMMENT '예약 요청 일시',
    confirmed_at DATETIME NULL COMMENT '예약 확정 일시',
    cancelled_at DATETIME NULL COMMENT '예약 취소 일시',
    failed_at DATETIME NULL COMMENT '예약 실패 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_reservations_reservation_no (reservation_no),
    KEY idx_reservations_user_id (user_id),
    KEY idx_reservations_accommodation_id (accommodation_id),
    KEY idx_reservations_room_id (room_id),
    KEY idx_reservations_room_rate_id (room_rate_id),
    KEY idx_reservations_status (reservation_status),
    KEY idx_reservations_check_in_out (check_in_date, check_out_date),
    CONSTRAINT fk_reservations_user
        FOREIGN KEY (user_id) REFERENCES USERS (id),
    CONSTRAINT fk_reservations_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATIONS (id),
    CONSTRAINT fk_reservations_room
        FOREIGN KEY (room_id) REFERENCES ROOMS (id),
    CONSTRAINT fk_reservations_room_rate
        FOREIGN KEY (room_rate_id) REFERENCES ROOM_RATES (id),
    CONSTRAINT chk_reservations_adult_count CHECK (adult_count >= 1),
    CONSTRAINT chk_reservations_child_count CHECK (child_count >= 0),
    CONSTRAINT chk_reservations_total_amount CHECK (total_amount >= 0),
    CONSTRAINT chk_reservations_stay_range CHECK (check_out_date > check_in_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 숙소 예약 정보';

CREATE TABLE PAYMENTS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '결제 ID',
    reservation_id BIGINT NOT NULL COMMENT '예약 ID',
    payment_no VARCHAR(100) NOT NULL COMMENT '결제 번호',
    payment_method VARCHAR(50) NOT NULL COMMENT '결제 수단',
    payment_amount DECIMAL(12,2) NOT NULL COMMENT '결제 금액',
    currency VARCHAR(10) NOT NULL COMMENT '통화',
    payment_status ENUM('READY', 'PENDING', 'PAID', 'CANCELLED', 'PARTIAL_REFUNDED', 'REFUNDED', 'FAILED') NOT NULL COMMENT '결제 상태',
    approved_at DATETIME NULL COMMENT '승인 일시',
    failed_at DATETIME NULL COMMENT '실패 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_payments_payment_no (payment_no),
    KEY idx_payments_reservation_id (reservation_id),
    KEY idx_payments_payment_status (payment_status),
    CONSTRAINT fk_payments_reservation
        FOREIGN KEY (reservation_id) REFERENCES RESERVATIONS (id),
    CONSTRAINT chk_payments_payment_amount CHECK (payment_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='예약 결제 정보';

CREATE TABLE REFUNDS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '환불 ID',
    payment_id BIGINT NOT NULL COMMENT '결제 ID',
    refund_no VARCHAR(100) NOT NULL COMMENT '환불 번호',
    refund_amount DECIMAL(12,2) NOT NULL COMMENT '환불 금액',
    refund_reason VARCHAR(500) NULL COMMENT '환불 사유',
    refunded_at DATETIME NULL COMMENT '환불 완료 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_refunds_refund_no (refund_no),
    KEY idx_refunds_payment_id (payment_id),
    CONSTRAINT fk_refunds_payment
        FOREIGN KEY (payment_id) REFERENCES PAYMENTS (id),
    CONSTRAINT chk_refunds_refund_amount CHECK (refund_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='결제 환불 정보';
