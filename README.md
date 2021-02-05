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


# 1. Saga Pattern - Event Storming
* 이벤트 스토밍 모델링 결과 (by MSAEZ) : http://www.msaez.io/#/storming/null/local/5ef4c7f90e266d3608ccbd45db9c52a7

![image](https://user-images.githubusercontent.com/5582138/106929039-3a212b00-6757-11eb-9c17-e80bbe3d1e9b.png)

* Saga pattern

![image](https://user-images.githubusercontent.com/5582138/106970785-f814db00-6790-11eb-8f39-817bca3dcace.png)


# 이벤트 도출
![image](https://user-images.githubusercontent.com/5582138/106930631-1ced5c00-6759-11eb-8a2e-928fbe706c03.png)


# 2. CQRS 포함 시나리오 구현 검증

- 티켓 구매
http POST localhost:8081/ticketing/ teamcode=KT betcredit=10000 ticketstatus=none

````
G:\>http POST  http://localhost:8081/ticketings teamcode=KT betcredit=1000000
HTTP/1.1 201
Content-Type: application/json;charset=UTF-8
Date: Fri, 05 Feb 2021 03:11:46 GMT
Location: http://localhost:8081/ticketings/1
Transfer-Encoding: chunked

{
    "_links": {
        "self": {
            "href": "http://localhost:8081/ticketings/1"
        },
        "ticketing": {
            "href": "http://localhost:8081/ticketings/1"
        }
    },
    "betCredit": 1000000,
    "teamCode": KT,
    "ticketStatus": bet
}

````

- 구매한 티켓의 상태가 betting으로 변경되었는지 확인 (pub/sub)
http localhost:8081/items/1 
image

- 티켓 취소
http DELETE localhost:8081/ticketing/ teamcode=KT betcredit=10000 ticketstatus=none
image

- 티켓 취소 확인
http POST localhost:8081/ticketing/ teamcode=KT betcredit=10000 ticketstatus=none
image

CQRS - view를 통해 티켓의 구매와 상태 변화, 집계 여부를 한번에 확인 가능함



# 4. Req/Resp

설계에서 Req/Resp 호출 모델링

Req/Res : 고객 티켓 구매 > 시스템에 집계됨
          고객 티켓 취소 > 시스템이 집계됨
          
호출 프로토콜은 앞서 작성한 REST Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 구현함


# 5. Gateway

gateway > application.yml

````

   ticketing    : 8081
   tickettotal  : 8082
   ticketcenter : 8083

````

<img src="https://user-images.githubusercontent.com/5582138/106964379-59cf4800-6785-11eb-93c3-1b325a2c3ecd.png"  width="600" height="400">
<img src="https://user-images.githubusercontent.com/5582138/106964228-21c80500-6785-11eb-8fb5-14d38de48874.png"  width="600" height="400">

````
    http POST http://localhost:8081/ticketings teamcode=AA betcredit=100
````

<img src="https://user-images.githubusercontent.com/5582138/106968915-28f31100-678d-11eb-8f7c-c421aa28bae5.png"  width="600" height="400">

````
    http http://localhost:8083/mytickets
````

<img src="https://user-images.githubusercontent.com/5582138/106968918-2a243e00-678d-11eb-8e4a-b91a31959ceb.png"  width="600" height="400">


# 6. Deploy

****** 네임스페이스 만들기

kubectl create ns sportskk
kubectl get ns
kubectl create ns kubectl get sportskk

****** 폴더 만들기, 해당폴더로 이동

mkdir sportskk
cd sportskk
mkdir sportskk

******소스 가져오기
git clone https://github.com/wonderkkid/sportskk.git

캡처1 git clone

****** 빌드하기

cd ticketing
mvn package -Dmaven.test.skip=true

<img src="https://user-images.githubusercontent.com/5582138/106985154-41beef00-67ac-11eb-80d3-3004aa462b2e.png"  width="400" height="200">

mvn package

****** 도커라이징: Azure 레지스트리에 도커 이미지 푸시하기

az acr build --registry skccuser12 --image skccuser12.azurecr.io/gateway:0.1 .

<img src="https://user-images.githubusercontent.com/5582138/106985268-7af75f00-67ac-11eb-9b57-d07b169a62e2.png"  width="400" height="200">

****** 컨테이너라이징: 디플로이 생성 확인

kubectl create deploy gateway --image=skccuser12.azurecr.io/gateway:0.1  -n sportskk

****** 컨테이너라이징: 서비스 생성 확인

kubectl expose deploy gateway --type=LoadBalancer --port=8080 -n sportskk

ticketing, ticketcenter, tickettotal 에도 반복 적용

****** 모니터링
<img src="https://user-images.githubusercontent.com/5582138/106987061-058d8d80-67b0-11eb-8887-da44a1e78989.png"  width="400" height="200">


# 7. Circuit Breaker

# 8. Autoscale(HPA)

ticketing 시스템에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:

reservation > deployment.yml 설정

<img src="https://user-images.githubusercontent.com/5582138/106990526-c3684a00-67b7-11eb-990f-c37136ad657a.png"  width="400" height="200">


<img src="https://user-images.githubusercontent.com/5582138/106991143-fe1eb200-67b8-11eb-86c7-61f5448f941e.png" >




````
   kubectl exec -it pod/siege-5c7c46b788-4rn4r -c siege -- /bin/bash
````
   
워크로드를 50초 걸어준다.
   
````
   siege -c250 -t50S -r1000 -v --content-type "application/json" 'http://localhost:8080/ticketings POST { "teamcode":KT, "betcredit":1000}'
````

<img src="https://user-images.githubusercontent.com/5582138/106993107-5788e000-67bd-11eb-906e-be5e6cc114e0.png">

오토스케일 변화 확인을 위해 모니터링을 걸어둔다:

````
   kubectl get deploy ticketing -w
````
<img src="https://user-images.githubusercontent.com/5582138/106998429-d97e0680-67c7-11eb-8add-00a40398a4de.png">

# 9. Zero-downtime deploy (Readiness Probe)



# 10. Config Map/ Persistence Volume

- application.yml 설정

* default쪽

<img src="https://user-images.githubusercontent.com/5582138/106988142-543c2700-67b2-11eb-8237-a742af97461d.png"  width="250" height="100">

* docker 쪽

<img src="https://user-images.githubusercontent.com/5582138/106988318-df1d2180-67b2-11eb-9bf8-be7871a9224f.png"  width="250" height="100">

- Deployment.yml 설정

<img src="https://user-images.githubusercontent.com/5582138/106988921-48e9fb00-67b4-11eb-8fdd-2836e443e294.png"  width="250" height="100">

- config map 생성 후 조회

```
kubectl create configmap apiurl --from-literal=url=http://localhost:8080
kubectl get configmap apiurl -o yaml
```

<img src="https://user-images.githubusercontent.com/5582138/106989375-64093a80-67b5-11eb-84df-05325429e556.png" width="500" heignt="400">


# 11. Polyglot Persistence

마이크로서비스는 각자의 DB를 가지고 있고, 다른 서비스의 DB 에 접근할 수 없음. 제공된 API 를 통해서만 접근이 가능함. 
각 서비스의 기능에 따라 적합한 데이터베이스를 선택해서 사용.

> ticketCenter 의 pom.xml 설정 : H2 DB

<img src="https://user-images.githubusercontent.com/5582138/106962285-3eaf0900-6782-11eb-9317-ac36169239bc.png"  width="400" height="200">

> ticketing 의 pom.xml 설정 : HSQLDB

<img src="https://user-images.githubusercontent.com/5582138/106962291-3f479f80-6782-11eb-8103-a3abbdeaf79c.png"  width="400" height="200">

> tickettotal 의 pom.xml 설정 : MySQLDB

<img src="https://user-images.githubusercontent.com/5582138/106962930-2be90400-6783-11eb-8d79-2897f6c38a39.png"  width="400" height="200">


# 12. Self-healing (Liveness Probe)
