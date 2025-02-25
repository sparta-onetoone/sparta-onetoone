# FlexiRoute

# 프로젝트 목적

- 음식점들의 배달 및 포장 주문 관리, 결제, 그리고 주문 내역 관리 기능을 제공하는 플랫폼 개발
- 모놀리식 아키텍처 구성(Layerd Architecture)
- 추후 MSA 적용을 위해, 각 도메인 간의 의존성과 연관관계를 최소화하여 독립적인 관계를 맺도록 시스템 아키텍처를 설계

<br>

# 서비스 구성 및 실행 방법

### application.yml
```yml
spring:
  profiles:
    active: prod

webclient:
  base-url: {{현재 프로젝트의 base-url}}

---
spring:
  config:
    activate:
     on-profile: prod

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://{{db-host}}/{{database}}
    username: {{username}}
    password: {{password}}

jwt:
  secret:
    key: {{JWT Secret Key}}
  access-token-expiration-time: 3600000  # 1시간 (밀리초 단위)
  refresh-token-expiration-time: 604800000  # 7일 (밀리초 단위)

gemini:
  api:
    key: {{Gemini API Key}}
  request:
    uri: https://generativelanguage.googleapis.com
    path: /v1beta/models/gemini-1.5-flash-latest:generateContent

ai:
  prompt:
    max-length-message: ", 답변을 최대한 간결하게 50자 이하로"

---
spring:
  config:
    activate:
      on-profile: test

  datasource:
    driverClassName: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=TRUE
    username: sa
    password:

gemini:
  api:
    key: testKey
  request:
    uri: https://generativelanguage.googleapis.com
    path: /v1beta/models/gemini-1.5-flash-latest:generateContent

ai:
  prompt:
    max-length-message: ", 답변을 최대한 간결하게 50자 이하로"

jwt:
  secret:
    key: testKeytestKeytestKeytestKeytestKeytestKeytestKe
  access-token-expiration-time: 3600000  # 1시간 (밀리초 단위)
  refresh-token-expiration-time: 604800000  # 7일 (밀리초 단위)

```

<br>

# 시스템 아키텍처

![](./images/image02.png)

<br>

# 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgresql-4169E1?style=flat-square&logo=postgresql&logoColor=white)

### Infra
![GCP](https://img.shields.io/badge/GCP-4285F4?style=flat-square&logo=googlecloud&logoColor=white) 
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) 
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=jenkins&logoColor=white) 

### Cooperation
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white) 
![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white) 
![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white) 

<br>

# ERD

![](./images/image03.png)

### ERD 도식화

![](./images/image01.png)

# API Docs

### [Swagger](http://34.64.236.37:8080/swagger-ui/index.html)

### [API 명세서](https://www.notion.so/teamsparta/API-1982dc3ef5148042bc1cd9e971613a71)

<br>

# 팀원 역할 분담

| 정민수 | 염금성 | 이지언 | 노현지 |
|--------|--------|--------|--------|
| <img src="https://avatars.githubusercontent.com/dbp-jack" height=150 width=150> | <img src="https://avatars.githubusercontent.com/venus-y" height=150 width=150> | <img src="https://avatars.githubusercontent.com/LeeJieon" height=150 width=150> |<img src="https://avatars.githubusercontent.com/nodajida" height=150 width=150>|
| - 팀장<br>- 가게 도메인<br>- 카테고리 도메인<br>- 리뷰 도메인 | - 상품 도메인<br>- 결제 도메인 | - 장바구니 도메인<br>- 주문 도메인<br>- CI/CD |- 회원 도메인<br>- AI 담당|
