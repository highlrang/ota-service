# Step 2. Database Design

## 1. 개요

OTA 서비스의 핵심 도메인은 사용자, 숙소, 객실, 가격, 재고, 예약, 결제, 환불로 구성한다.
본 단계에서는 서비스 구현을 위한 스키마와 주요 Enum 을 정의한다.

## 2. 엔터티 관계

- 숙소:객실 = `1:N`
- 숙소:숙소 이미지 = `1:N`
- 판매자:숙소 = `1:N`
- 객실:요금 = `1:N`
- 객실:재고 = `1:N`
- 객실:객실 이미지 = `1:N`
- 사용자:예약 = `1:N`
- 숙소:숙소 상세 = `1:1`
- 예약:결제 = `1:N`
- 결제:환불 = `1:N`

## 3. Schema

### 3.1 사용자 `USERS`

고객 전용 계정을 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 사용자 ID |
| email | VARCHAR(255) UNIQUE | 로그인 이메일 |
| password_hash | VARCHAR(255) | 비밀번호 해시 |
| name | VARCHAR(100) | 사용자명 |
| phone_number | VARCHAR(30) | 연락처 |
| status | VARCHAR(30) | 계정 상태 |
| access_token | VARCHAR(512) | 현재 액세스 토큰 |
| refresh_token | VARCHAR(512) | 리프레시 토큰 |
| refresh_token_expired_at | DATETIME | 리프레시 토큰 만료 일시 |
| last_login_at | DATETIME | 마지막 로그인 일시 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.2 판매자 `EXTRANETS`

Extranet에서 숙소를 등록/운영하는 판매자 계정을 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 판매자 ID |
| email | VARCHAR(255) UNIQUE | 로그인 이메일 |
| password_hash | VARCHAR(255) | 비밀번호 해시 |
| name | VARCHAR(100) | 판매자명 |
| phone_number | VARCHAR(30) | 연락처 |
| status | VARCHAR(30) | 계정 상태 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.3 숙소 `ACCOMMODATIONS`

판매자가 Extranet에 직접 등록하고, Supplier로부터 공급받은 숙소 정보를 관리한다.
지역은 별도 `REGIONS` 테이블로 관리하며, 숙소는 최하위 또는 서비스 기준 지역을 `region_id`로 참조한다.
상세 설명은 별도 `ACCOMMODATION_DETAILS` 테이블로 분리하여 관리한다.
`source_type`이 `EXTRANET`이면 `extranet_id`를 사용하고, `SUPPLIER`면 외부 상품 식별자인 `supplier_product_id`를 사용한다.
외부 상품 식별자는 현재 숫자 형태여도 향후 영문 prefix, 하이픈 등 포맷 확장 가능성을 고려해 `VARCHAR`로 관리한다.

| 컬럼명             | 타입                      | 설명                  |
|-----------------|-------------------------|---------------------|
| id              | BIGINT PK               | 숙소 ID               |
| code            | VARCHAR(50) UNIQUE      | 숙소 식별 코드          |
| source_type     | ENUM(SourceType)        | Extranet / Supplier |
| extranet_id       | BIGINT FK -> EXTRANETS.id | Extranet 판매자 ID |
| supplier_product_id | VARCHAR(100)        | 외부 공급사 상품 번호 |
| region_type     | ENUM(AccommodationRegionType) | 국내 / 해외       |
| accommodation_type | ENUM(AccommodationType) | 업소 종류           |
| region_id       | BIGINT FK -> REGIONS.id | 지역 ID               |
| name            | VARCHAR(255)            | 숙소명                 |
| address         | VARCHAR(500)            | 주소                  |
| latitude        | DECIMAL(10,7)           | 위도                  |
| longitude       | DECIMAL(10,7)           | 경도                  |
| thumbnail_image | VARCHAR(255)            | 썸네일 이미지             |
| business_status | ENUM(BusinessStatus)    | 숙소 영업 상태            |
| check_in_time   | TIME                    | 체크인 시간              |
| check_out_time  | TIME                    | 체크아웃 시간             |
| created_at      | DATETIME                | 생성일시                |
| updated_at      | DATETIME                | 수정일시                |

#### 3.3.1 판매자 숙소 원본 `EXTRANET_ACCOMMODATIONS`

Extranet 판매자가 등록한 원본 숙소 데이터를 관리한다.
등록 시 통합 `ACCOMMODATIONS`와 1:1로 연결되며, 판매자 입력 원본과 통합 상품 ID를 함께 보관한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 판매자 숙소 원본 ID |
| extranet_id | BIGINT FK -> EXTRANETS.id | Extranet 판매자 ID |
| accommodation_id | BIGINT UNIQUE, FK -> ACCOMMODATIONS.id | 통합 숙소 ID |
| code | VARCHAR(50) UNIQUE | 숙소 식별 코드 |
| region_type | ENUM(AccommodationRegionType) | 국내 / 해외 |
| accommodation_type | ENUM(AccommodationType) | 업소 종류 |
| region_id | BIGINT FK -> REGIONS.id | 지역 ID |
| name | VARCHAR(255) | 숙소명 |
| address | VARCHAR(500) | 주소 |
| latitude | DECIMAL(10,7) | 위도 |
| longitude | DECIMAL(10,7) | 경도 |
| thumbnail_image | VARCHAR(255) | 썸네일 이미지 |
| business_status | ENUM(BusinessStatus) | 숙소 영업 상태 |
| check_in_time | TIME | 체크인 시간 |
| check_out_time | TIME | 체크아웃 시간 |
| description | TEXT | 숙소 설명 |
| extra_info | VARCHAR(1000) | 기타 정보 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

#### 3.3.2 숙소 상세 `ACCOMMODATION_DETAILS`

숙소 상세 조회 시 사용하는 설명 정보를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| accommodation_id | BIGINT PK, FK -> ACCOMMODATIONS.id | 숙소 ID |
| description | TEXT | 숙소 설명 |
| extra_info | VARCHAR(1000) | 조식, 주차장 등 기타 정보 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

#### 3.3.3 지역 `REGIONS`

숙소 지역 분류를 계층형으로 관리한다.
예시: 국가(depth 1) > 시/도(depth 2) > 시/군/구(depth 3)

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 지역 ID |
| parent_region_id | BIGINT FK -> REGIONS.id | 상위 지역 ID, 최상위는 NULL |
| name | VARCHAR(100) | 지역명 |
| code | VARCHAR(50) UNIQUE | 지역 코드 |
| depth | INT | 지역 depth |
| full_name | VARCHAR(255) | 전체 지역명 |
| sort_order | INT | 정렬 순서 |
| is_active | BOOLEAN | 사용 여부 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

#### 3.3.4 숙소 이미지 `ACCOMMODATION_IMAGES`

숙소 상세와 목록 노출에 사용하는 이미지를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 숙소 이미지 ID |
| accommodation_id | BIGINT FK -> ACCOMMODATIONS.id | 숙소 ID |
| image_url | VARCHAR(1000) | 이미지 URL |
| image_type | VARCHAR(50) | 대표/일반/썸네일 구분 |
| sort_order | INT | 노출 순서 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.4 객실 `ROOMS`

숙소에 속한 객실 정보를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 객실 ID |
| accommodation_id | BIGINT FK -> ACCOMMODATIONS.id | 숙소 ID |
| room_code | VARCHAR(100) UNIQUE | 객실 내부 코드 |
| name | VARCHAR(255) | 객실명 |
| description | TEXT | 객실 설명 |
| standard_occupancy | INT | 기준 인원 |
| max_occupancy | INT | 최대 인원 |
| bed_type | VARCHAR(100) | 침대 유형 |
| extra_info | VARCHAR(500) | 기타 정보 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

#### 3.4.1 객실 이미지 `ROOM_IMAGES`

객실 상세에 사용하는 이미지를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 객실 이미지 ID |
| room_id | BIGINT FK -> ROOMS.id | 객실 ID |
| image_url | VARCHAR(1000) | 이미지 URL |
| image_type | VARCHAR(50) | 대표/일반/썸네일 구분 |
| sort_order | INT | 노출 순서 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.5 요금 `ROOM_RATES`

객실별 판매 요금 정책을 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 요금 ID |
| room_id | BIGINT FK -> ROOMS.id | 객실 ID |
| rate_name | VARCHAR(255) | 요금명 |
| base_price | DECIMAL(12,2) | 기본 요금 |
| currency | VARCHAR(10) | 통화 |
| sale_price | DECIMAL(12,2) | 판매 요금 |
| refundable_yn | BOOLEAN | 환불 가능 여부 |
| rate_date | DATE | 요금 일자 |
| active_yn | BOOLEAN | 활성 여부 |
| deleted_at | DATETIME | 삭제 일시 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.6 재고 `ROOM_INVENTORIES`

객실별 날짜 단위 판매 가능 재고를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 재고 ID |
| room_id | BIGINT FK -> ROOMS.id | 객실 ID |
| inventory_date | DATE | 재고 기준 일자 |
| total_stock | INT | 전체 재고 수량 |
| reserved_stock | INT | 예약된 수량 |
| available_stock | INT | 판매 가능 수량 |
| stop_sale_yn | BOOLEAN | 판매 중지 여부 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.7 예약 `RESERVATIONS`

사용자의 숙소 예약 정보를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 예약 ID |
| user_id | BIGINT FK -> USERS.id | 예약 사용자 ID |
| accommodation_id | BIGINT FK -> ACCOMMODATIONS.id | 숙소 ID |
| room_id | BIGINT FK -> ROOMS.id | 객실 ID |
| room_rate_id | BIGINT FK -> ROOM_RATES.id | 적용 요금 ID |
| reservation_no | VARCHAR(100) UNIQUE | 예약 번호 |
| check_in_date | DATE | 체크인 날짜 |
| check_out_date | DATE | 체크아웃 날짜 |
| guest_name | VARCHAR(100) | 투숙객명 |
| guest_phone_number | VARCHAR(30) | 투숙객 연락처 |
| adult_count | INT | 성인 수 |
| child_count | INT | 아동 수 |
| total_amount | DECIMAL(12,2) | 총 예약 금액 |
| reservation_status | ENUM(ReservationStatus) | 예약 상태 |
| requested_at | DATETIME | 예약 요청 일시 |
| confirmed_at | DATETIME | 예약 확정 일시 |
| cancelled_at | DATETIME | 예약 취소 일시 |
| failed_at | DATETIME | 예약 실패 일시 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.8 결제 `PAYMENTS`

예약에 대한 결제 정보를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 결제 ID |
| reservation_id | BIGINT FK -> RESERVATIONS.id | 예약 ID |
| payment_no | VARCHAR(100) UNIQUE | 결제 번호 |
| payment_method | VARCHAR(50) | 결제 수단 |
| payment_amount | DECIMAL(12,2) | 결제 금액 |
| currency | VARCHAR(10) | 통화 |
| payment_status | ENUM(PaymentStatus) | 결제 상태 |
| approved_at | DATETIME | 승인 일시 |
| failed_at | DATETIME | 실패 일시 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

### 3.9 환불 `REFUNDS`

결제 건에 대한 환불 정보를 관리한다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT PK | 환불 ID |
| payment_id | BIGINT FK -> PAYMENTS.id | 결제 ID |
| refund_no | VARCHAR(100) UNIQUE | 환불 번호 |
| refund_amount | DECIMAL(12,2) | 환불 금액 |
| refund_reason | VARCHAR(500) | 환불 사유 |
| refunded_at | DATETIME | 환불 완료 일시 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

## 4. Enum

### 4.1 숙소 연동 유형 `SourceType`

- EXTRANET
- SUPPLIER

### 4.2 숙소 지역 유형 `AccommodationRegionType`

- DOMESTIC
- OVERSEAS

### 4.3 숙소 유형 `AccommodationType`

- HOTEL_RESORT
- MOTEL
- PENSION_POOL_VILLA
- CAMPING_GLAMPING
- GUESTHOUSE_HANOK
- HOTEL
- HOSTEL
- GUESTHOUSE_BNB

### 4.4 숙소 영업 상태 `BusinessStatus`

- OPEN
- CLOSED
- STOP_SALE

### 4.5 예약 상태 `ReservationStatus`

- PENDING
- CONFIRMED
- COMPLETED
- CANCELLED
- FAILED

### 4.6 결제 상태 `PaymentStatus`

- READY
- PENDING
- PAID
- CANCELLED
- PARTIAL_REFUNDED
- REFUNDED
- FAILED
