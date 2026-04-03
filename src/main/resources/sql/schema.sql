SET NAMES utf8mb4;

DROP TABLE IF EXISTS REFUNDS;
DROP TABLE IF EXISTS PAYMENTS;
DROP TABLE IF EXISTS RESERVATIONS;
DROP TABLE IF EXISTS ROOM_INVENTORIES;
DROP TABLE IF EXISTS EXTRANET_ROOM_RATES;
DROP TABLE IF EXISTS ROOM_RATES;
DROP TABLE IF EXISTS ROOM_IMAGES;
DROP TABLE IF EXISTS ROOMS;
DROP TABLE IF EXISTS ACCOMMODATION_IMAGES;
DROP TABLE IF EXISTS ACCOMMODATION_DETAILS;
DROP TABLE IF EXISTS EXTRANET_ACCOMMODATIONS;
DROP TABLE IF EXISTS ACCOMMODATIONS;
DROP TABLE IF EXISTS EXTRANETS;
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
    code VARCHAR(50) NOT NULL COMMENT '사용자 식별 코드',
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
    UNIQUE KEY uk_users_code (code),
    KEY idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='고객 사용자 계정 정보';

CREATE TABLE EXTRANETS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '판매자 ID',
    email VARCHAR(255) NOT NULL COMMENT '로그인 이메일',
    code VARCHAR(50) NOT NULL COMMENT '판매자 식별 코드',
    password_hash VARCHAR(255) NOT NULL COMMENT '비밀번호 해시',
    name VARCHAR(100) NOT NULL COMMENT '판매자명',
    phone_number VARCHAR(30) NOT NULL COMMENT '연락처',
    status VARCHAR(30) NOT NULL COMMENT '계정 상태',
    access_token VARCHAR(512) NULL COMMENT '현재 액세스 토큰',
    refresh_token VARCHAR(512) NULL COMMENT '리프레시 토큰',
    refresh_token_expired_at DATETIME NULL COMMENT '리프레시 토큰 만료 일시',
    last_login_at DATETIME NULL COMMENT '마지막 로그인 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_extranets_email (email),
    UNIQUE KEY uk_extranets_code (code),
    KEY idx_extranets_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Extranet 판매자 계정 정보';

CREATE TABLE ACCOMMODATIONS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '숙소 ID',
    code VARCHAR(50) NOT NULL COMMENT '숙소 식별 코드',
    source_type ENUM('EXTRANET', 'SUPPLIER') NOT NULL COMMENT 'Extranet / Supplier',
    extranet_id BIGINT NULL COMMENT 'Extranet 판매자 ID',
    supplier_product_id VARCHAR(100) NULL COMMENT '외부 공급사 상품 번호',
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
    business_status ENUM('PENDING_APPROVAL', 'NEEDS_REVISION', 'OPEN', 'CLOSED') NOT NULL COMMENT '숙소 검수/운영 상태',
    check_in_time TIME NOT NULL COMMENT '체크인 시간',
    check_out_time TIME NOT NULL COMMENT '체크아웃 시간',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_accommodations_code (code),
    KEY idx_accommodations_extranet_id (extranet_id),
    KEY idx_accommodations_supplier_product_id (supplier_product_id),
    KEY idx_accommodations_region_id (region_id),
    KEY idx_accommodations_source_type (source_type),
    KEY idx_accommodations_region_type_accommodation_type (region_type, accommodation_type),
    KEY idx_accommodations_business_status (business_status),
    CONSTRAINT fk_accommodations_extranet
        FOREIGN KEY (extranet_id) REFERENCES EXTRANETS (id),
    CONSTRAINT fk_accommodations_region
        FOREIGN KEY (region_id) REFERENCES REGIONS (id),
    CONSTRAINT chk_accommodations_latitude CHECK (latitude IS NULL OR (latitude >= -90 AND latitude <= 90)),
    CONSTRAINT chk_accommodations_longitude CHECK (longitude IS NULL OR (longitude >= -180 AND longitude <= 180)),
    CONSTRAINT chk_accommodations_source_owner CHECK (
        (source_type = 'EXTRANET' AND extranet_id IS NOT NULL) OR
        (source_type = 'SUPPLIER' AND extranet_id IS NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='숙소 기본 정보';

CREATE TABLE EXTRANET_ACCOMMODATIONS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '판매자 숙소 원본 ID',
    extranet_id BIGINT NOT NULL COMMENT 'Extranet 판매자 ID',
    accommodation_id BIGINT NOT NULL COMMENT '통합 숙소 ID',
    code VARCHAR(50) NOT NULL COMMENT '숙소 식별 코드',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_extranet_accommodations_accommodation_id (accommodation_id),
    UNIQUE KEY uk_extranet_accommodations_code (code),
    KEY idx_extranet_accommodations_extranet_id (extranet_id),
    CONSTRAINT fk_extranet_accommodations_extranet
        FOREIGN KEY (extranet_id) REFERENCES EXTRANETS (id),
    CONSTRAINT fk_extranet_accommodations_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATIONS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Extranet 판매자 숙소 원본 정보';

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
    base_price DECIMAL(12,2) NOT NULL COMMENT '객실 기본 기준가',
    currency VARCHAR(10) NOT NULL COMMENT '객실 기본 통화',
    sale_price DECIMAL(12,2) NOT NULL COMMENT '객실 기본 판매가',
    refundable_yn BOOLEAN NOT NULL DEFAULT FALSE COMMENT '객실 기본 환불 가능 여부',
    min_stay_nights INT NOT NULL DEFAULT 1 COMMENT '객실 기본 최소 숙박 일수',
    max_stay_nights INT NOT NULL DEFAULT 1 COMMENT '객실 기본 최대 숙박 일수',
    default_stock INT NOT NULL DEFAULT 0 COMMENT '객실 기본 재고',
    active_yn BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성 여부',
    deleted_at DATETIME NULL COMMENT '삭제 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rooms_room_code (room_code),
    KEY idx_rooms_accommodation_id (accommodation_id),
    KEY idx_rooms_active (accommodation_id, active_yn),
    CONSTRAINT fk_rooms_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATIONS (id),
    CONSTRAINT chk_rooms_base_price CHECK (base_price >= 0),
    CONSTRAINT chk_rooms_sale_price CHECK (sale_price >= 0),
    CONSTRAINT chk_rooms_min_stay_nights CHECK (min_stay_nights > 0),
    CONSTRAINT chk_rooms_max_stay_nights CHECK (max_stay_nights > 0),
    CONSTRAINT chk_rooms_default_stock CHECK (default_stock >= 0),
    CONSTRAINT chk_rooms_stay_nights_order CHECK (max_stay_nights >= min_stay_nights),
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
    active_yn BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성 여부',
    deleted_at DATETIME NULL COMMENT '삭제 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY idx_room_images_room_id (room_id),
    KEY idx_room_images_room_id_sort_order (room_id, sort_order),
    KEY idx_room_images_active (room_id, active_yn),
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
    valid_from DATE NOT NULL COMMENT '적용 시작일',
    valid_to DATE NOT NULL COMMENT '적용 종료일',
    active_yn BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성 여부',
    deleted_at DATETIME NULL COMMENT '삭제 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    KEY idx_room_rates_room_id (room_id),
    KEY idx_room_rates_valid_range (room_id, valid_from, valid_to),
    KEY idx_room_rates_active (room_id, active_yn),
    CONSTRAINT fk_room_rates_room
        FOREIGN KEY (room_id) REFERENCES ROOMS (id),
    CONSTRAINT chk_room_rates_base_price CHECK (base_price >= 0),
    CONSTRAINT chk_room_rates_sale_price CHECK (sale_price >= 0),
    CONSTRAINT chk_room_rates_valid_range CHECK (valid_to >= valid_from)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='객실별 판매 요금 정책';

CREATE TABLE EXTRANET_ROOM_RATES (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '판매자 원본 요금 ID',
    room_id BIGINT NOT NULL COMMENT '객실 ID',
    room_rate_id BIGINT NOT NULL COMMENT '통합 요금제 ID',
    rate_name VARCHAR(255) NOT NULL COMMENT '요금명',
    base_price DECIMAL(12,2) NOT NULL COMMENT '기본 요금',
    currency VARCHAR(10) NOT NULL COMMENT '통화',
    sale_price DECIMAL(12,2) NOT NULL COMMENT '판매 요금',
    refundable_yn BOOLEAN NOT NULL DEFAULT FALSE COMMENT '환불 가능 여부',
    valid_from DATE NOT NULL COMMENT '적용 시작일',
    valid_to DATE NOT NULL COMMENT '적용 종료일',
    active_yn BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성 여부',
    deleted_at DATETIME NULL COMMENT '삭제 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_extranet_room_rates_room_rate_id (room_rate_id),
    KEY idx_extranet_room_rates_room_id (room_id),
    KEY idx_extranet_room_rates_active (room_id, active_yn),
    CONSTRAINT fk_extranet_room_rates_room
        FOREIGN KEY (room_id) REFERENCES ROOMS (id),
    CONSTRAINT fk_extranet_room_rates_room_rate
        FOREIGN KEY (room_rate_id) REFERENCES ROOM_RATES (id),
    CONSTRAINT chk_extranet_room_rates_base_price CHECK (base_price >= 0),
    CONSTRAINT chk_extranet_room_rates_sale_price CHECK (sale_price >= 0),
    CONSTRAINT chk_extranet_room_rates_valid_range CHECK (valid_to >= valid_from)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Extranet 원본 객실 요금 정보';

CREATE TABLE SUPPLIER_ACCOMMODATIONS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '외부 숙소 매핑 ID',
    source VARCHAR(30) NOT NULL COMMENT '외부 공급사',
    supplier_property_id VARCHAR(100) NOT NULL COMMENT '외부 숙소 ID',
    accommodation_id BIGINT NOT NULL COMMENT '통합 숙소 ID',
    status VARCHAR(30) NULL COMMENT '외부 상태',
    last_synced_at DATETIME NULL COMMENT '마지막 동기화 시각',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_accommodations_source_property (source, supplier_property_id),
    UNIQUE KEY uk_supplier_accommodations_accommodation_id (accommodation_id),
    CONSTRAINT fk_supplier_accommodations_accommodation
        FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATIONS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 공급사 숙소 매핑 정보';

CREATE TABLE SUPPLIER_ROOMS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '외부 객실 매핑 ID',
    source VARCHAR(30) NOT NULL COMMENT '외부 공급사',
    supplier_roomtype_id VARCHAR(100) NOT NULL COMMENT '외부 객실 타입 ID',
    room_id BIGINT NOT NULL COMMENT '통합 객실 ID',
    supplier_accommodation_id BIGINT NOT NULL COMMENT '외부 숙소 매핑 ID',
    status VARCHAR(30) NULL COMMENT '외부 상태',
    last_synced_at DATETIME NULL COMMENT '마지막 동기화 시각',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_rooms_source_roomtype (source, supplier_roomtype_id),
    UNIQUE KEY uk_supplier_rooms_room_id (room_id),
    KEY idx_supplier_rooms_supplier_accommodation_id (supplier_accommodation_id),
    CONSTRAINT fk_supplier_rooms_room
        FOREIGN KEY (room_id) REFERENCES ROOMS (id),
    CONSTRAINT fk_supplier_rooms_supplier_accommodation
        FOREIGN KEY (supplier_accommodation_id) REFERENCES SUPPLIER_ACCOMMODATIONS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 공급사 객실 매핑 정보';

CREATE TABLE SUPPLIER_RATEPLANS (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '외부 요금제 매핑 ID',
    source VARCHAR(30) NOT NULL COMMENT '외부 공급사',
    supplier_rateplan_id VARCHAR(100) NOT NULL COMMENT '외부 요금제 ID',
    room_rate_id BIGINT NOT NULL COMMENT '통합 요금제 ID',
    supplier_room_id BIGINT NOT NULL COMMENT '외부 객실 매핑 ID',
    rate_name VARCHAR(255) NOT NULL COMMENT '외부 요금명',
    base_price DECIMAL(12,2) NOT NULL COMMENT '외부 기본 요금',
    currency VARCHAR(10) NOT NULL COMMENT '통화',
    sale_price DECIMAL(12,2) NOT NULL COMMENT '외부 판매 요금',
    refundable_yn BOOLEAN NOT NULL DEFAULT FALSE COMMENT '외부 환불 가능 여부',
    valid_from DATE NOT NULL COMMENT '외부 적용 시작일',
    valid_to DATE NOT NULL COMMENT '외부 적용 종료일',
    status VARCHAR(30) NULL COMMENT '외부 상태',
    last_synced_at DATETIME NULL COMMENT '마지막 동기화 시각',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_rateplans_source_rateplan (source, supplier_rateplan_id),
    UNIQUE KEY uk_supplier_rateplans_room_rate_id (room_rate_id),
    KEY idx_supplier_rateplans_supplier_room_id (supplier_room_id),
    CONSTRAINT fk_supplier_rateplans_room_rate
        FOREIGN KEY (room_rate_id) REFERENCES ROOM_RATES (id),
    CONSTRAINT fk_supplier_rateplans_supplier_room
        FOREIGN KEY (supplier_room_id) REFERENCES SUPPLIER_ROOMS (id),
    CONSTRAINT chk_supplier_rateplans_base_price CHECK (base_price >= 0),
    CONSTRAINT chk_supplier_rateplans_sale_price CHECK (sale_price >= 0),
    CONSTRAINT chk_supplier_rateplans_valid_range CHECK (valid_to >= valid_from)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 공급사 요금제 매핑 정보';

CREATE TABLE SUPPLIER_RATEPLAN_INVENTORIES (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '공급사 요금제 재고 ID',
    room_rate_id BIGINT NOT NULL COMMENT '요금제 ID',
    inventory_date DATE NOT NULL COMMENT '요금/재고 기준 일자',
    base_price DECIMAL(12,2) NOT NULL COMMENT '기본 요금',
    sale_price DECIMAL(12,2) NOT NULL COMMENT '판매 요금',
    extra_adult DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '성인 추가 요금',
    extra_child DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '아동 추가 요금',
    extra_infant DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '유아 추가 요금',
    promotion_type VARCHAR(100) NULL COMMENT '프로모션 타입',
    vacancy INT NOT NULL DEFAULT 0 COMMENT '잔여 재고 수',
    stop_sale_yn BOOLEAN NOT NULL DEFAULT FALSE COMMENT '판매 중지 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_rateplan_inventories_rate_date (room_rate_id, inventory_date),
    KEY idx_supplier_rateplan_inventories_date (inventory_date),
    CONSTRAINT fk_supplier_rateplan_inventories_room_rate
        FOREIGN KEY (room_rate_id) REFERENCES ROOM_RATES (id),
    CONSTRAINT chk_supplier_rateplan_inventories_base_price CHECK (base_price >= 0),
    CONSTRAINT chk_supplier_rateplan_inventories_sale_price CHECK (sale_price >= 0),
    CONSTRAINT chk_supplier_rateplan_inventories_extra_adult CHECK (extra_adult >= 0),
    CONSTRAINT chk_supplier_rateplan_inventories_extra_child CHECK (extra_child >= 0),
    CONSTRAINT chk_supplier_rateplan_inventories_extra_infant CHECK (extra_infant >= 0),
    CONSTRAINT chk_supplier_rateplan_inventories_vacancy CHECK (vacancy >= 0),
    CONSTRAINT chk_supplier_rateplan_inventories_price_order CHECK (sale_price <= base_price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공급사 요금제 일자별 재고/요금 정보';

CREATE TABLE ROOM_INVENTORIES (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '재고 ID',
    room_id BIGINT NOT NULL COMMENT '객실 ID',
    inventory_date DATE NOT NULL COMMENT '재고 기준 일자',
    total_stock INT NOT NULL COMMENT '전체 재고 수량',
    reserved_stock INT NOT NULL DEFAULT 0 COMMENT '예약된 수량',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '판매 가능 수량',
    stop_sale_yn BOOLEAN NOT NULL DEFAULT FALSE COMMENT '판매 중지 여부',
    active_yn BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성 여부',
    deleted_at DATETIME NULL COMMENT '삭제 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_inventories_room_id_inventory_date_active (room_id, inventory_date, active_yn),
    KEY idx_room_inventories_inventory_date (inventory_date),
    KEY idx_room_inventories_active (room_id, active_yn),
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
