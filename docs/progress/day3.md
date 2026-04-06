# Day3
## 구현
### 작업내용
Extranet과 Supplier 도메인에 대한 API를 개발한다. 
1. JPA Entity, Repository 생성
2. API 기본 구조 생성
   - Authentication
   - Logging
   - ExceptionHandling
   - Swagger
3. Extranet API 개발
   - 숙소 및 등록/수정/조회
3. Supplier API 연동
   - 재고조회 및 예약
   - 상품 동기화

### 의사결정
#### 공통
1. JPA 연관관계 제거
   - Entity 복잡도를 낮추고 조회 성능을 개선하기 위해 엔티티 간 연관관계를 제거하고 FK id 컬럼으로 대체한다

2. 주요 도메인 식별 코드 적용
   - 숙소의 경우 통합 DB / Extranet용 DB / Supplier용 DB로 분리하여 DB를 관리하기 때문에 각 DB의 PK로 로직을 구현하면 복잡도가 증가한다
   - 주요 도메인(유저, 숙소, 예약 등)에는 코드 기반의 식별자를 사용하여 보안과 확장성을 강화하고 서버 내부적으로는 PK를 활용한다

3. Soft Delete 적용
   - 변경 이력 관리를 위해 수정/삭제의 경우에는 활성 여부 컬럼을 활용한다

#### Extranet
1. Extranet 숙소 관리
   - 숙소 지역 정보 매핑
     - 외부 API에서 제공하는 지역 정보를 내부 지역 DB와 매핑 
     - 추후 지역 정보를 통한 숙소 검색 시  
   - 숙소 정보 등록 시 '승인 대기' 상태로 초기화
     - 관리자 승인을 통해 숙소가 노출되도록 하여 품질 관리 강화
   - Extranet 화면 단위를 고려하여 조회 API를 숙소 목록 > 숙소 상세 및 객실 목록 > 객실 상세 단위로 분리
   - 숙소 및 객실 수정의 경우 데이터별(숙소 / 객실 / 이미지 / 재고 / 요금 등) 수정 단위가 다르기 때문에 API를 세분화하여 구현

#### Supplier
1. Supplier 상품 연동
   - Supplier가 제공하는 Webhook 방식으로 상품 DB를 동기화한다
     - Webhook Event Type
       - event_type이 contents_updated면 Onda API 호출을 통해 최신 데이터를 반영한다
       - event_type이 status_updated, inventory_updated면 수신된 payload 기반으로 반영한다
       
     - 통합 DB 동기화 및 데이터 매핑 기준
       - Supplier를 통해 들어온 DB는 최신 데이터를 기준으로 통합 Accommodation DB도 갱신하고, 별도의 Supplier DB에 별도 원본 데이터를 관리한다
       - Webhook으로 수신된 Supplier DB가 Extranet에서 먼저 등록된 상품일 경우 통합 DB에 Insert가 아닌 FK 매핑만 한다
         - Extranet 서비스 일관성을 위해 Extranet으로 먼저 등록된 DB는 보존하기 위함이다
         - 상품 DB 중복은 name + address로 판단한다
       
2. TODO 
   - Supplier가 제공하는 상품 관련 API를 주기적으로 호출하여 데이터 동기화 정확도를 높인다
