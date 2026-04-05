# Customer API

고객 API는 비회원, 회원을 모두 지원하며   
고객 인증이 필요한 경우 `Authorization: Bearer {accessToken}` 헤더를 사용한다.

## 1. 고객 로그인

- 설명: 고객 계정으로 로그인하고 access token, refresh token 을 발급받는다.
- 경로: `POST /api/customer/auth/login`

request example
```json
{
  "email": "guest1@ota.local",
  "password": "guest1234!"
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "email": "guest1@ota.local",
    "code": "USER-0001",
    "name": "이방문",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

## 2. 숙소 검색

- 설명: 지역, 날짜, 인원, 가격, 타입 조건으로 숙소를 페이징 조회한다.
- 경로: `GET /api/customer/accommodations/search`

query params

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| regionCode | String | N | 지역 코드 정확 일치 검색 |
| accommodationType | String | N | `HOTEL_RESORT` 등 숙소 타입 |
| bedType | String | N | `DOUBLE`, `TWIN` 등 침대 타입 |
| stayStartDate | LocalDate | Y | 체크인 날짜 |
| stayEndDate | LocalDate | Y | 체크아웃 날짜 |
| guestCount | Integer | Y | 투숙 인원 |
| minTotalAmount | BigDecimal | N | 최소 총액 |
| maxTotalAmount | BigDecimal | N | 최대 총액 |
| excludeSoldOut | Boolean | N | `true`면 매진 제외 |
| page | int | N | 기본값 `0` |
| size | int | N | 기본값 `20` |

request example
```text
GET /api/customer/accommodations/search?regionCode=KR-11-11680&stayStartDate=2026-04-10&stayEndDate=2026-04-12&guestCount=2&excludeSoldOut=true&page=0&size=20
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "content": [
      {
        "accommodationCode": "ACC-000001",
        "name": "강남 시티 호텔",
        "accommodationType": "HOTEL_RESORT",
        "regionCode": "KR-11-11680",
        "regionName": "대한민국 서울 강남구",
        "address": "서울특별시 강남구 테헤란로 100",
        "thumbnailImage": "https://cdn.ota.local/accommodations/1/thumb.jpg",
        "checkInTime": "15:00:00",
        "checkOutTime": "11:00:00",
        "stayStartDate": "2026-04-10",
        "stayEndDate": "2026-04-12",
        "minTotalAmount": 270000.00,
        "currency": "KRW",
        "availableRoomCount": 1,
        "maxGuestCount": 2,
        "soldOut": false
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

## 3. 인기 숙소 목록 조회

- 설명: 예약 이력 기준 인기 숙소 목록을 조회한다.
- 경로: `GET /api/customer/accommodations/popular`

query params

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| accommodationType | String | N | 미입력 시 전체 숙소 타입 대상 |

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": [
    {
      "accommodationCode": "ACC-000001",
      "name": "강남 시티 호텔",
      "accommodationType": "HOTEL_RESORT",
      "regionCode": "KR-11",
      "regionName": "대한민국 서울",
      "address": "서울특별시 강남구 테헤란로 100",
      "thumbnailImage": "https://cdn.ota.local/accommodations/1/thumb.jpg",
      "displayPrice": 135000.00,
      "currency": "KRW"
    }
  ]
}
```

## 4. 숙소 상세 조회

- 설명: 고객이 선택한 숙소의 상세 정보와 예약 가능한 객실 목록을 조회한다.
- 경로: `GET /api/customer/accommodations/{accommodationCode}`

query params

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| stayStartDate | LocalDate | Y | 체크인 날짜 |
| stayEndDate | LocalDate | Y | 체크아웃 날짜 |
| guestCount | Integer | Y | 투숙 인원 |

request example
```text
GET /api/customer/accommodations/ACC-000001?stayStartDate=2026-04-10&stayEndDate=2026-04-12&guestCount=2
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "accommodationCode": "ACC-000001",
    "name": "강남 시티 호텔",
    "regionType": "DOMESTIC",
    "accommodationType": "HOTEL_RESORT",
    "regionCode": "KR-11-11680",
    "regionName": "대한민국 서울 강남구",
    "address": "서울특별시 강남구 테헤란로 100",
    "thumbnailImage": "https://cdn.ota.local/accommodations/1/thumb.jpg",
    "checkInTime": "15:00",
    "checkOutTime": "11:00",
    "description": "강남 중심에 위치한 비즈니스 호텔",
    "extraInfo": "조식 유료 제공, 지하 주차장 이용 가능",
    "stayStartDate": "2026-04-10",
    "stayEndDate": "2026-04-12",
    "guestCount": 2,
    "images": [],
    "rooms": []
  }
}
```

## 5. 예약 생성

- 설명: 숙소/객실/투숙일/결제 정보를 받아 예약과 결제를 생성한다.
- 경로: `POST /api/customer/reservations`
- 인증: 로그인 고객은 토큰 기반으로, 비회원은 토큰 없이도 요청 가능하다.

request example
```json
{
  "guestName": "김여행",
  "guestPhoneNumber": "010-1234-5678",
  "accommodationCode": "ACC-000001",
  "roomCode": "ROOM-SEOUL-0001",
  "checkInAt": "2026-04-10 15:00",
  "checkOutAt": "2026-04-12 11:00",
  "paymentMethod": "CARD",
  "paymentAmount": 270000.00,
  "adultCount": 2,
  "childCount": 0
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "reservationNo": "RSV-20260404-0004",
    "reservationStatus": "CONFIRMED",
    "accommodationCode": "ACC-000001",
    "roomCode": "ROOM-SEOUL-0001",
    "checkInAt": "2026-04-10 15:00",
    "checkOutAt": "2026-04-12 11:00",
    "paymentNo": "PAY-20260404-0004",
    "paymentStatus": "PAID",
    "paymentMethod": "CARD",
    "paymentAmount": 270000.00,
    "currency": "KRW",
    "reservedAt": "2026-04-04T15:30:00"
  }
}
```

## 6. 예약 목록 조회

- 설명: 회원은 로그인 정보로, 비회원은 투숙객명/휴대폰 번호로 예약 목록을 조회한다.
- 경로: `GET /api/customer/reservations`

query params

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| type | String | Y | `BEFORE_USE`, `AFTER_USE`, `CANCELLED` |
| guestName | String | 조건부 | 비회원 조회 시 필수 |
| guestPhoneNumber | String | 조건부 | 비회원 조회 시 필수 |
| page | int | N | 기본값 `0` |
| size | int | N | 기본값 `20` |

request example
```text
GET /api/customer/reservations?type=BEFORE_USE&page=0&size=20
Authorization: Bearer {accessToken}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "content": [
      {
        "reservationNo": "RSV-260405-0001",
        "accommodationName": "강남 시티 호텔",
        "accommodationType": "HOTEL_RESORT",
        "reservationStatus": "CONFIRMED",
        "checkInAt": "2026-04-10 15:00",
        "checkOutAt": "2026-04-12 11:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

## 7. 예약 상세 조회

- 설명: 회원은 로그인 정보로, 비회원은 투숙객명/휴대폰 번호로 예약 상세와 결제 정보를 조회한다.
- 경로: `GET /api/customer/reservations/{reservationNo}`

query params

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| guestName | String | 조건부 | 비회원 조회 시 필수 |
| guestPhoneNumber | String | 조건부 | 비회원 조회 시 필수 |

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "reservationNo": "RSV-260405-0001",
    "reservationStatus": "CONFIRMED",
    "accommodationCode": "ACC-000001",
    "accommodationName": "강남 시티 호텔",
    "accommodationType": "HOTEL_RESORT",
    "checkInAt": "2026-04-10 15:00",
    "checkOutAt": "2026-04-12 11:00",
    "guestName": "김여행",
    "guestPhoneNumber": "010-1234-5678",
    "adultCount": 2,
    "childCount": 0,
    "totalAmount": 270000.00,
    "confirmedAt": "2026-04-05 13:20:11",
    "payment": {
      "paymentNo": "PAY-260405-0001",
      "paymentStatus": "PAID",
      "paymentMethod": "CARD",
      "paymentAmount": 270000.00,
      "currency": "KRW",
      "approvedAt": "2026-04-05 13:20:11"
    }
  }
}
```

## 8. 예약 취소

- 설명: 회원은 로그인 정보로, 비회원은 투숙객명/휴대폰 번호로 예약을 취소하고 환불 정보를 생성한다.
- 경로: `POST /api/customer/reservations/{reservationNo}/cancel`

query params

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| guestName | String | 조건부 | 비회원 조회 시 필수 |
| guestPhoneNumber | String | 조건부 | 비회원 조회 시 필수 |

request example
```json
{
  "reason": "사용자 요청 취소"
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "reservationNo": "RSV-20260401-0001",
    "reservationStatus": "CANCELLED",
    "paymentNo": "PAY-20260401-0001",
    "paymentStatus": "REFUNDED",
    "refundNo": "RFD-20260402-0001",
    "refundAmount": 270000.00,
    "cancelledAt": "2026-04-02 09:15:00"
  }
}
```