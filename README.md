# 음식점 판매 서비스를 구현하여 트래픽을 처리해 보자

## 서비스 설명

- 가게에 음식이 존재하고, 이를 손님에게 판매한다.
- 음식은 판매 가능한 양이 존재한다. 이 이상은 판매할 수 없고 남은 갯수가 0인 음식은 검색 불가하다.
- 손님은 포인트가 있고 포인트 이상의 음식을 구매한 수 없다. 음식을 구매하면 그 가격만큼의 포인트가 감소한다.
- 이와 관련된 서비스를 구현해본다.

## 요구사항

- API가 아니라 테스트 케이스로 구현한다. (controller없이 테스트만 해보기)
- 동시성 이슈를 고려한다.
- 트래픽이 많은 상황을 상정하여 테스트한다.
- 데이터 일관성을 고려한다.

## 테스트 케이스 설명(중요 비즈니스 로직만)

- 특정 가게의 재고가 존재하는 모든 요리를 볼 수 있어야한다.
- 손님은 가게의 물건을 구매 가능하다.
- 많이 팔린 물건 순서대로 볼 수 있는 정렬이 있으면 좋겠다.

## 구현

### 재고가 존재하는 모든 요리 확인

- [테스트 로직](https://github.com/RyooChan/nyam-nyam-good/blob/f7f4cf32331c042b8ae3869a8e73849c9861e07e/src/test/java/com/example/nyamnyamgood/item/service/ItemServiceTest.java#L67)
- querydsl을 사용하여 remain 0 이상의 값들만 노출시킨다.
- index를 통한 검색 진행
    - `create index idx_storeid_remained on item (store_id, remained);`
    - querydsl의 [조건절](https://github.com/RyooChan/nyam-nyam-good/blob/f7f4cf32331c042b8ae3869a8e73849c9861e07e/src/main/java/com/example/nyamnyamgood/item/repository/ItemRepositoryImpl.java#L26)에 맞춰 인덱스 생성
    - [여기](https://github.com/RyooChan/TIL/blob/main/Database/Real_MySQL/008.md)를 보면 index는 그냥 읽는 것에 비해 4~5배 정도의 속도 하락이 있기 때문에 검색해야 하는 데이터가 전체 레코드의 20~25% 이하가 되도록 설정하고 진행했다. -> 시간을 좀 명확히 보기 위해 많은 더미데이터를 넣음
    - `select store_id, remained, item_id from item where 1=1 and store_id = 272 and remained >= 1;`
    - 다음의 쿼리에 대해(검색 결과는 인덱스 사용을 보여주기 위해 조작) 
    - ![image](https://github.com/RyooChan/nyam-nyam-good/assets/53744363/56b56c96-ccb8-4f97-ae96-45b1445b7d0d)
    - 이렇게 인덱스를 사용했고, 인덱스를 쓰지 않은 결과(`drop index idx_storeid_remained on item;`)에 비해 
        - 인덱스 미사용 : ![image](https://github.com/RyooChan/nyam-nyam-good/assets/53744363/21df093c-80d8-462e-9ce9-fb416cbe98d7)
        - 인덱스 사용 : ![image](https://github.com/RyooChan/nyam-nyam-good/assets/53744363/61ec2e67-99e1-4e33-a5ad-3d7d7a3cd082)
    - 성능이 늘어난 것을 확인 가능하다.
- redis를 통한 캐싱과 속도 확인
    - [캐싱을 통해 동일 로직 결과값을 저장](https://github.com/RyooChan/nyam-nyam-good/blob/f7f4cf32331c042b8ae3869a8e73849c9861e07e/src/main/java/com/example/nyamnyamgood/item/service/ItemService.java#L43)하고 보여준다.
    - [둘은 동일한 결과를 보여주고, 속도는 캐시를 통하는게 훨씬 빠르다](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/test/java/com/example/nyamnyamgood/item/service/ItemServiceTest.java#L100).
- 참고로 [새로운 음식이 등록되면 갱신](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/main/java/com/example/nyamnyamgood/item/service/ItemService.java#L25)된다. [테스트 확인 완료](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/test/java/com/example/nyamnyamgood/item/service/ItemServiceTest.java#L139)
    - 이거를 매번 등록할 때에 갱신하는 이유는, 음식의 경우 가게에서 새로운 음식을 등록하는 경우보다 고객이 찾는 경우가 훨씬 많기 때문이다.
    - 일종의 [write-through 전략](https://hello-backend.tistory.com/233) 이라고 생각하면 된다.

## 음식 구매 프로세스

1. 음식의 남은 갯수를 뺴준다.
2. 유저 포인트를 뺀다.
3. 유저가 구매한 물건 데이터를 저장한다.

요런 순서로 적용

- Transactinal를 적용하여 세가지 비즈니스 로직에서 문제가 없도록 작성
- [물건 구매 성공 테스트](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/test/java/com/example/nyamnyamgood/customer/service/CustomerServiceTest.java#L65)
- [물건 구매 실패 테스트](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/test/java/com/example/nyamnyamgood/customer/service/CustomerServiceTest.java#L76)
    - [Test할 때에 SpringBootTest의 Transactional 제거](https://hello-backend.tistory.com/294)
- Case 1 : [여러 고객이 하나의 물건을 구매](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/test/java/com/example/nyamnyamgood/customer/service/CustomerServiceTest.java#L105)하는 경우
    - redisson을 통한 redis 분산락 적용
    - [lettuce에 비해 빠르고](https://hello-backend.tistory.com/214), 여러 DB table에 걸쳐서 진행되고 실서버의 경우 분산DB가 있을 수 있다고 판단하여 [DB lock](https://hello-backend.tistory.com/213)보다는 redisson 적용.
        - 이를 통해 여러 쓰레드에서 한꺼번에 접근해도 동시성 문제 해결 완료
    - 동일 service에서의 [self-invocation](https://hello-backend.tistory.com/258) 해결을 위해 [getBean](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/main/java/com/example/nyamnyamgood/config/Utils.java#L8) 적용
    - 
- Case 2 : [하나의 고객이 여러번 동일 물건 구매](https://github.com/RyooChan/nyam-nyam-good/blob/45179de63bacd90e46a26f7f2ec920afdda8e1f6/src/test/java/com/example/nyamnyamgood/customer/service/CustomerServiceTest.java#L134)
    - 위의 케이스와 마찬가지로 redisson을 통해 동시성 문제를 해결하였다.

### 많이 팔린 순서대로 물건 정렬

- 현재는 group by를 사용중이다. (2024.03.03 기준)
    - 집계테이블 방식과의 장단점을 적기 위해 일단 이렇게 했다.
    - 이후 집계테이블 활용으로 변경 예정
