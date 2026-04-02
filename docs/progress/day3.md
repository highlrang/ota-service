# Day3
## 구현
### 작업내용
Extranet과 Supplier 도메인에 대한 API를 개발한다. 
1. JPA Entity, Repository 생성
2. Extranet API 개발
   - 판매자 계정
   - 숙소 등록/수정/조회
   - 예약 관리
3. Supplier API 연동
   - 재고조회 및 예약
   - 상품 동기화

### 의사결정
1. JPA 연관관계 제거
   - Entity 복잡도를 낮추고 조회 성능을 개선하기 위해 엔티티 간 연관관계를 제거하고 FK id 컬럼으로 대체한다

