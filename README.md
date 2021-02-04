# S portsk K : 스포츠KK [스포츠 크크]

<img src="https://user-images.githubusercontent.com/5582138/106925754-bca7eb80-6753-11eb-9361-7d99c3c60323.png" width="200" height="200">

# Table of contents
- 스포츠 KK 
   - [서비스 시나리오](#서비스-시나리오)
   - [체크포인트](#체크포인트)
   - [분석/설계](#분석설계)
        - [AS-IS 조직 (Horizontally-Aligned)](#AS-IS-조직-Horizontally-Aligned)
        - [TO-BE 조직 (Vertically-Aligned)](#TO-BE-조직-Vertically-Aligned)
        - [Event Storming 결과](#Event-Storming-결과)
        - [헥사고날 아키텍처 다이어그램 도출 (Polyglot)](#헥사고날-아키텍처-다이어그램-도출-Polyglot)
   - [구현](#구현)
      - [DDD의 적용](#DDD의-적용)
      - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
      - [Gateway 적용](#Gateway-적용)
      - [동기식 호출과 Fallback 처리](#동기식-호출과-Fallback-처리)
      - [비동기식 호출 / 시간적 디커플링 / 장애격리](#비동기식-호출--시간적-디커플링--장애격리)
      - [CQRS 포함 시나리오 구현 검증](#CQRS-포함-시나리오-구현-검증)
   - [운영](#운영)
      - [Deploy / Pipeline](#Deploy--Pipeline)
      - [CirCuit Breaker](#CirCuit-Breaker)
      - [오토스케일 아웃](#오토스케일-아웃)
      - [무정지 재배포](#무정지-재배포)
      - [Config Map](#Config-Map)
      - [Self-healing (Liveness Probe)](#Self-healing-Liveness-Probe)


# 서비스 시나리오

기능적 요구사항
1. 구매자는 티켓을 구입할 수 있다.
2. 구매자는 티켓 구입을 취소할 수 있다.
3. 구매자는 티켓 구입시 팀과 베팅 금액을 고를 수 있다.
3. 구입된 티켓은 "Approved"로 상태가 변경된다.
4. 취소된 티켓은 "Cancelled"로 상태가 변경된다.


비기능적 요구사항
1. 트랜잭션
    1. 구입 완료된 티켓은 집계 요청으로 진행된다. (Sync 호출)
2. 장애격리
    1. 집계 시스템이 중지된 상태라도 티켓 구매는 가능하다. (Async / pub-sub)
    2. 결제시스템에 부하가 발생할 경우 잠시 결제를 중지한다. (Circuit breaker, fallback)
3. 성능
    1. 구매와 별도로 관리자는 티켓의 상태를 조회할 수 있어야 한다. (CQRS)


# 체크포인트

1. Saga
1. CQRS
1. Correlation
1. Req/Resp
1. Gateway
1. Deploy/ Pipeline
1. Circuit Breaker
1. Autoscale (HPA)
1. Zero-downtime deploy (Readiness Probe)
1. Config Map/ Persistence Volume
1. Polyglot
1. Self-healing (Liveness Probe)


# Event Storming
* 이벤트 스토밍 모델링 결과 (by MSAEZ) : http://www.msaez.io/#/storming/null/local/5ef4c7f90e266d3608ccbd45db9c52a7

![image](https://user-images.githubusercontent.com/5582138/106929039-3a212b00-6757-11eb-9c17-e80bbe3d1e9b.png)

# 이벤트 도출
![image](https://user-images.githubusercontent.com/5582138/106930631-1ced5c00-6759-11eb-8a2e-928fbe706c03.png)

# 5. Gateway

gateway > application.yml

<img src="https://user-images.githubusercontent.com/5582138/106964379-59cf4800-6785-11eb-93c3-1b325a2c3ecd.png"  width="600" height="400">
<img src="https://user-images.githubusercontent.com/5582138/106964228-21c80500-6785-11eb-8fb5-14d38de48874.png"  width="600" height="400">

``
    http POST http://localhost:8081/ticketings teamcode=AA betcredit=100
``

<img src="https://user-images.githubusercontent.com/5582138/106968915-28f31100-678d-11eb-8f7c-c421aa28bae5.png"  width="600" height="400">

``
    http http://localhost:8083/mytickets
``
<img src="https://user-images.githubusercontent.com/5582138/106968918-2a243e00-678d-11eb-8e4a-b91a31959ceb.png"  width="600" height="400">


# 11. Polyglot Persistence

마이크로서비스는 각자의 DB를 가지고 있고, 다른 서비스의 DB 에 접근할 수 없음. 제공된 API 를 통해서만 접근이 가능함. 
각 서비스의 기능에 따라 적합한 데이터베이스를 선택해서 사용.

> ticketCenter 의 pom.xml 설정 : H2 DB

<img src="https://user-images.githubusercontent.com/5582138/106962285-3eaf0900-6782-11eb-9317-ac36169239bc.png"  width="400" height="200">

> ticketing 의 pom.xml 설정 : HSQLDB

<img src="https://user-images.githubusercontent.com/5582138/106962291-3f479f80-6782-11eb-8103-a3abbdeaf79c.png"  width="400" height="200">

> tickettotal 의 pom.xml 설정 : MySQLDB

<img src="https://user-images.githubusercontent.com/5582138/106962930-2be90400-6783-11eb-8d79-2897f6c38a39.png"  width="400" height="200">


