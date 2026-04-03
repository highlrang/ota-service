# Extranet Extranet API

Extranet Extranet API를 Postman 또는 Swagger에서 테스트하는 순서 기준으로 정리했다.  
모든 판매자 전용 API는 `Authorization: Bearer {accessToken}` 헤더가 필요하다.

## 1. 판매자 로그인

- 설명: 판매자 계정으로 로그인하고 access token을 발급받는다.
- 경로: `POST /api/extranet/auth/login`

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
    "code": "EXTRANET-0001",
    "name": "강남호텔 운영사",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

## 2. 숙소 등록

- 설명: 판매자 숙소를 등록한다.
- 경로: `POST /api/extranet/accommodations`

request example
```json
{
  "name": "강남 시티 호텔",
  "accommodationType": "HOTEL_RESORT",
  "addressInfo": {
    "countryCode": "KR",
    "province": "서울특별시",
    "city": "강남구",
    "district": "역삼동",
    "addressLine1": "테헤란로 100",
    "addressLine2": "10층",
    "zipCode": "06123",
    "lat": 37.4980950,
    "lng": 127.0276100
  },
  "thumbnailImage": "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg",
  "checkInTime": "15:00",
  "checkOutTime": "11:00",
  "description": "강남 중심에 위치한 비즈니스 호텔",
  "extraInfo": "조식 유료 제공, 지하 주차장 이용 가능"
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "code": "ACC-000010",
    "name": "강남 시티 호텔",
    "regionType": "DOMESTIC",
    "accommodationType": "HOTEL_RESORT",
    "address": "서울특별시 강남구 테헤란로 100 10층",
    "latitude": 37.4980950,
    "longitude": 127.0276100,
    "thumbnailImage": "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg",
    "businessStatus": "PENDING_APPROVAL",
    "checkInTime": "15:00",
    "checkOutTime": "11:00",
    "description": "강남 중심에 위치한 비즈니스 호텔",
    "extraInfo": "조식 유료 제공, 지하 주차장 이용 가능"
  }
}
```

## 3. 내 등록 숙소 목록 조회

- 설명: 내가 등록한 숙소 목록을 조회한다.
- 경로: `GET /api/extranet/accommodations`

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": [
    {
      "code": "ACC-000001",
      "name": "강남 시티 호텔",
      "address": "서울특별시 강남구 테헤란로 100",
      "thumbnailImage": "/uploads/accommodations/thumb-1.jpg",
      "businessStatus": "OPEN"
    }
  ]
}
```

## 4. 내 등록 숙소 상세 조회

- 설명: 숙소 code 기준으로 숙소 기본 정보와 객실 요약 목록을 조회한다.
- 경로: `GET /api/extranet/accommodations/{accommodationCode}`

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "code": "ACC-000001",
    "name": "강남 시티 호텔",
    "regionType": "DOMESTIC",
    "accommodationType": "HOTEL_RESORT",
    "address": "서울특별시 강남구 테헤란로 100",
    "thumbnailImage": "/uploads/accommodations/thumb-1.jpg",
    "businessStatus": "OPEN",
    "checkInTime": "15:00",
    "checkOutTime": "11:00",
    "description": "강남 중심에 위치한 비즈니스 호텔",
    "extraInfo": "조식 유료 제공, 지하 주차장 이용 가능",
    "rooms": [
      {
        "roomCode": "ROOM-SEOUL-0001",
        "name": "스탠다드 더블",
        "bedType": "DOUBLE",
        "standardOccupancy": 2,
        "maxOccupancy": 2,
        "basePrice": 150000.00,
        "salePrice": 135000.00,
        "defaultStock": 10
      },
      {
        "roomCode": "ROOM-SEOUL-0002",
        "name": "디럭스 트윈",
        "bedType": "TWIN",
        "standardOccupancy": 2,
        "maxOccupancy": 3,
        "basePrice": 220000.00,
        "salePrice": 198000.00,
        "defaultStock": 5
      }
    ],
    "createdAt": "2026-04-03 10:15:30"
  }
}
```

## 5. 객실 이미지 업로드

- 설명: 객실 이미지 파일을 업로드하고 imageUrl을 반환한다. 반환된 imageUrl은 숙소 썸네일 또는 객실 이미지 등록에 사용할 수 있다.
- 경로: `POST /api/extranet/rooms/images/upload`

request example
```text
multipart/form-data
file: room-main.jpg
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "fileName": "0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg",
    "imageUrl": "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg"
  }
}
```

## 6. 객실 등록

- 설명: 숙소 code 기준으로 객실 기본정보, 이미지, 기본 재고, 날짜별 재고 override, 기간별 요금 override를 등록한다.
- 경로: `POST /api/extranet/accommodations/{accommodationCode}/rooms`

request example
```json
{
  "name": "디럭스 트윈",
  "description": "욕조가 포함된 트윈 객실",
  "standardOccupancy": 2,
  "maxOccupancy": 3,
  "bedType": "TWIN",
  "extraInfo": "엑스트라 베드 가능",
  "basePrice": 220000.00,
  "currency": "KRW",
  "salePrice": 198000.00,
  "refundable": false,
  "minStayNights": 1,
  "maxStayNights": 5,
  "defaultStock": 5,
  "images": [
    {
      "imageUrl": "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg",
      "imageType": "PRIMARY",
      "sortOrder": 1
    },
    {
      "imageUrl": "/uploads/room-images/5ec1b0d6-bf52-4b2f-a21f-ff0b0e9cb6cb.jpg",
      "imageType": "GENERAL",
      "sortOrder": 2
    }
  ],
  "inventories": [
    {
      "inventoryDate": "2026-04-20",
      "totalStock": 3
    },
    {
      "inventoryDate": "2026-04-21",
      "totalStock": 4
    }
  ],
  "rates": [
    {
      "rateName": "주말 특가",
      "basePrice": 240000.00,
      "currency": "KRW",
      "salePrice": 215000.00,
      "refundable": false,
      "validFrom": "2026-04-25",
      "validTo": "2026-04-26"
    }
  ]
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "roomCode": "ROOM-000010",
    "name": "디럭스 트윈",
    "description": "욕조가 포함된 트윈 객실",
    "standardOccupancy": 2,
    "maxOccupancy": 3,
    "bedType": "TWIN",
    "extraInfo": "엑스트라 베드 가능",
    "basePrice": 220000.00,
    "currency": "KRW",
    "salePrice": 198000.00,
    "refundable": false,
    "minStayNights": 1,
    "maxStayNights": 5,
    "defaultStock": 5,
    "images": [
      {
        "imageUrl": "/uploads/room-images/0d93ac7b-1b2d-43b8-9f17-15fbe6aa8f27.jpg",
        "imageType": "PRIMARY",
        "sortOrder": 1
      }
    ],
    "inventories": [
      {
        "inventoryDate": "2026-04-20",
        "totalStock": 3,
        "reservedStock": 0,
        "availableStock": 3,
        "stopSale": false
      }
    ],
    "rates": [
      {
        "rateName": "주말 특가",
        "basePrice": 240000.00,
        "currency": "KRW",
        "salePrice": 215000.00,
        "refundable": false,
        "validFrom": "2026-04-25",
        "validTo": "2026-04-26"
      }
    ],
    "createdAt": "2026-04-03 14:20:00"
  }
}
```

## 7. 객실 상세 조회

- 설명: 특정 객실의 전체 상세 정보와 이미지, 요금, 날짜별 재고를 조회한다.
- 경로: `GET /api/extranet/rooms/{roomCode}`

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "roomCode": "ROOM-SEOUL-0001",
    "name": "스탠다드 더블",
    "description": "도심 전망의 더블 객실",
    "standardOccupancy": 2,
    "maxOccupancy": 2,
    "bedType": "DOUBLE",
    "extraInfo": "조식 별도",
    "basePrice": 150000.00,
    "currency": "KRW",
    "salePrice": 135000.00,
    "refundable": true,
    "minStayNights": 1,
    "maxStayNights": 7,
    "defaultStock": 10,
    "images": [
      {
        "imageUrl": "/uploads/room-images/6f7d9d0e-main.jpg",
        "imageType": "PRIMARY",
        "sortOrder": 1
      }
    ],
    "rates": [
      {
        "rateName": "스탠다드 요금",
        "basePrice": 150000.00,
        "currency": "KRW",
        "salePrice": 135000.00,
        "refundable": true,
        "validFrom": "2026-04-01",
        "validTo": "2026-12-31"
      }
    ],
    "inventories": [
      {
        "inventoryDate": "2026-04-10",
        "totalStock": 8,
        "reservedStock": 2,
        "availableStock": 6,
        "stopSale": false
      }
    ],
    "createdAt": "2026-04-03 14:20:00"
  }
}
```

## 8. 숙소 정보 수정

- 설명: 숙소 기본 정보를 부분 수정한다.
- 경로: `PATCH /api/extranet/accommodations/{accommodationCode}`

request example
```json
{
  "name": "강남 시티 호텔 프리미어",
  "thumbnailImage": "/uploads/room-images/new-thumb.jpg",
  "checkInTime": "16:00",
  "description": "리뉴얼된 강남 비즈니스 호텔"
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "code": "ACC-000001",
    "name": "강남 시티 호텔 프리미어",
    "regionType": "DOMESTIC",
    "accommodationType": "HOTEL_RESORT",
    "address": "서울특별시 강남구 테헤란로 100",
    "thumbnailImage": "/uploads/room-images/new-thumb.jpg",
    "businessStatus": "OPEN",
    "checkInTime": "16:00",
    "checkOutTime": "11:00",
    "description": "리뉴얼된 강남 비즈니스 호텔",
    "extraInfo": "조식 유료 제공, 지하 주차장 이용 가능",
    "rooms": [],
    "createdAt": "2026-04-03 10:15:30"
  }
}
```

## 9. 객실 정보 수정

- 설명: 객실 기본 정보를 부분 수정한다.
- 경로: `PATCH /api/extranet/rooms/{roomCode}`

request example
```json
{
  "name": "디럭스 트윈 리뉴얼",
  "salePrice": 189000.00,
  "defaultStock": 6
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "roomCode": "ROOM-000010",
    "name": "디럭스 트윈 리뉴얼",
    "description": "욕조가 포함된 트윈 객실",
    "standardOccupancy": 2,
    "maxOccupancy": 3,
    "bedType": "TWIN",
    "extraInfo": "엑스트라 베드 가능",
    "basePrice": 220000.00,
    "currency": "KRW",
    "salePrice": 189000.00,
    "refundable": false,
    "minStayNights": 1,
    "maxStayNights": 5,
    "defaultStock": 6,
    "createdAt": "2026-04-03 14:20:00"
  }
}
```

## 10. 객실 이미지 수정

- 설명: 객실 이미지 목록을 비활성 처리한 후 새로 저장한다.
- 경로: `PUT /api/extranet/rooms/{roomCode}/images`

request example
```json
{
  "images": [
    {
      "imageUrl": "/uploads/room-images/new-main.jpg",
      "imageType": "PRIMARY",
      "sortOrder": 1
    },
    {
      "imageUrl": "/uploads/room-images/new-sub.jpg",
      "imageType": "GENERAL",
      "sortOrder": 2
    }
  ]
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "roomCode": "ROOM-000010",
    "images": [
      {
        "imageUrl": "/uploads/room-images/new-main.jpg",
        "imageType": "PRIMARY",
        "sortOrder": 1
      },
      {
        "imageUrl": "/uploads/room-images/new-sub.jpg",
        "imageType": "GENERAL",
        "sortOrder": 2
      }
    ]
  }
}
```

## 11. 객실 요금 수정

- 설명: 객실 요금 비활성 처리한 후 새로 저장한다.
- 경로: `PUT /api/extranet/rooms/{roomCode}/rates`

request example
```json
{
  "rates": [
    {
      "rateName": "주말 특가",
      "basePrice": 240000.00,
      "currency": "KRW",
      "salePrice": 210000.00,
      "refundable": false,
      "validFrom": "2026-04-25",
      "validTo": "2026-04-26"
    }
  ]
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "roomCode": "ROOM-000010",
    "rates": [
      {
        "rateName": "주말 특가",
        "basePrice": 240000.00,
        "currency": "KRW",
        "salePrice": 210000.00,
        "refundable": false,
        "validFrom": "2026-04-25",
        "validTo": "2026-04-26"
      }
    ]
  }
}
```

## 12. 객실 재고 수정

- 설명: 객실 날짜별 재고를 비활성 처리한 후 새로 저장한다.
- 경로: `PUT /api/extranet/rooms/{roomCode}/inventories`

request example
```json
{
  "inventories": [
    {
      "inventoryDate": "2026-04-20",
      "totalStock": 4,
      "stopSale": false
    },
    {
      "inventoryDate": "2026-04-21",
      "totalStock": 0,
      "stopSale": true
    }
  ]
}
```

response example
```json
{
  "success": true,
  "code": 200,
  "message": null,
  "data": {
    "roomCode": "ROOM-000010",
    "inventories": [
      {
        "inventoryDate": "2026-04-20",
        "totalStock": 4,
        "reservedStock": 0,
        "availableStock": 4,
        "stopSale": false
      },
      {
        "inventoryDate": "2026-04-21",
        "totalStock": 0,
        "reservedStock": 0,
        "availableStock": 0,
        "stopSale": true
      }
    ]
  }
}
```
