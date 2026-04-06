# Day7
## 테스트
### 작업내용
Customer API를 호출하여 테스트하고 정상 및 예외 동작을 점검한다.
- Extranet
  - 상품 등록 및 수정과 관련되어 Extranet / Supplier 연동 기준/정책을 검증한다.
  - 예약 내역 조회 기능을 점검한다.

- Customer
  - 숙소 검색 / 예약 / 예약 취소 기능을 점검한다.
  - 캐시 및 동시성 제어 장치 성능을 검증한다.
    - 캐시 테스트 결과
      - [k6 스크립트](../../scripts/performance/popular-cache-compare.js)
      - 첫 조회는 108ms, 이후 반복 조회는 평균 18.63ms, 중앙값 8.25ms
    - 동시성 제어 테스트 결과
      - [k6 스크립트](../../scripts/performance/concurrent-reservation.js)
      - 동일 숙소/동일 객실/동일 날짜로 20개의 동시 요청
      - 대상 객실의 해당 날짜 가용 재고는 8이며, 테스트 결과 8건 성공 / 12건 실패
      - 가용 재고 수량까지만 예약이 확정되고, 초과 요청은 오류가 발생하여 oversell이 발생하지 않음을 확인
  
### 의사결정 및 기능 보완
- Extranet 상품이 ONDA 중복 상품과 매핑될 경우 기본 정책은 ONDA 기준으로 운영한다.
  - 이전: Extranet에서 등록된 상품이 ONDA로부터 중복되어 연동된다면 갱신하지 않았다.
  - 변경: ONDA 예약 API 연동과 재고 관련 웹훅을 동기화하는 로직의 일관성을 높이기 위해
  - `supplier_product_id`를 연결하고 숙소/객실/요금/재고를 ONDA webhook에 맞춰 동기화한다.

- 판매자가 연동을 원하지 않는 경우에는 ONDA 정보를 전부 차단한다.
  - 숙소별로 `Accommodation`의 `supplierSyncBlocked` 컬럼으로 관리한다.
  - 외부 상품 연동 차단 상태에서는 webhook의 property / roomtype / rateplan / inventory 정보를 모두 반영하지 않도록 한다.
  - 해당 상품은 판매자만 수정할 수 있다.
