# 📈 xray-stock-service

### 프로젝트 개요

**xray-stock-service**는 주식 종목별 실시간 체결(Tick) 데이터를 수집하고, 이를 일정 주기로 집계한 캔들(Candle) 형태로 변환하여 제공하는 백엔드 서비스입니다.  
MongoDB TimeSeries 기반으로 데이터를 저장하며, HTTP API를 통해 다양한 조회 기능을 제공합니다.  
> 테스트, 데모, 실거래 아키텍처 구성 연습 목적이며, WebSocket/Kafka 등 실시간 처리 기술은 **향후 도입 예정**입니다.

---

### 시스템 구조 및 주요 기능

#### 1. 도메인 및 수집 설계

- **종목 상태 관리**  
  종목(`Stock`)은 종목코드, 심볼, 시장 타입(KOSPI/NASDAQ)과 함께 수집 가능 여부(`enable`) 정보를 갖고 있으며, 수집 여부를 도메인 내부에서 판단합니다.

- **Tick 데이터 구조**  
  `TradeTick` 도메인은 종목 ID, 가격, 거래량, 등락률, 체결 시각(`tickAt`) 등을 포함하여, 실제 거래 Tick 데이터를 모방합니다.

- **수집 범위 유효성 관리**  
  시간 범위를 표현하는 `TimeRange` 및 캔들 변환 기준을 담는 `CandleIntervalType`이 VO(Value Object)로 관리되어 집계 범위를 명확하게 제한합니다.

#### 2. Tick 수집 및 저장

- **스케줄 기반 자동 수집**  
  `CollectStockTradeTickScheduler`가 활성화된 종목들을 주기적으로 조회하여 외부 stock-generator API를 호출하고, 수집된 Tick 데이터를 MongoDB에 저장합니다.

- **MongoDB TimeSeries 저장소**  
  `TradeTick`은 `@TimeSeries` 애노테이션을 통해 MongoDB 타임시리즈 컬렉션에 TTL(30일)과 함께 저장되며, 저장 후 관련 이벤트(`TradeTickSavedEvent`)가 발행됩니다.

#### 3. 캔들 변환 및 제공

- **Tick → Candle 변환**  
  저장된 Tick은 `TradeTicksCandleConverter`를 통해 메모리 상에서 1분 또는 1일 단위로 변환되어 API 응답으로 제공됩니다.

- **캔들 조회 API 제공**  
  `/api/v1/stocks/{stockId}/candles` API는 주어진 기간, 간격(interval)에 따라 변환된 캔들 데이터를 반환합니다.

#### 4. CQRS 및 계층 분리

- **Command/Query 분리 구조**  
  종목 등록/수집 명령은 `StockCommandService`, 조회는 `StockQueryService`로 분리되어 있고, 각각의 UseCase 및 VO로 캡슐화되어 있습니다.

- **Persistence/외부 호출 분리**  
  외부 stock-generator 호출은 `RestStockGeneratorClient`, 저장소 연동은 `MongoStockRepository`, `MongoTradeTickRepository`에서 수행되며 Adapter/Port 구조를 따릅니다.

---

### 동작 흐름

1. **종목 저장 및 수집 시작**
   - `/api/v1/stocks/save`, `/api/v1/stocks/{stockId}/collecting` 호출
   - 종목 저장 또는 enable 설정 → 스케줄러 대상에 포함

2. **체결 Tick 수집**
   - `CollectStockTradeTickScheduler` 주기 실행
   - 외부 API에서 Tick 받아 MongoDB TimeSeries에 저장

3. **Tick 저장 후 이벤트 발행**
   - `TradeTickSavedEvent` → 핸들러에서 Tick 집계

4. **캔들 변환 및 응답**
   - 메모리 내에서 Tick 집계 → `/candles` API 응답

---

### 📦 패키지 구조

```
app.xray.stock.stock_service
├── adapter
│   ├── in/web        # REST API 컨트롤러
│   └── in/job        # 스케줄러
│   └── out/external  # 외부 stock-generator 호출
│   └── out/persistance # Mongo 저장소 어댑터
├── application
│   ├── port/in/out   # 유스케이스 및 저장소 인터페이스
│   ├── port/vo       # Command, Query VO
│   ├── service       # 비즈니스 로직
│   └── handler       # 도메인 이벤트 핸들러
├── domain
│   ├── entity        # Stock, TradeTick
│   ├── vo            # Candle, TimeRange
│   └── type/exception
├── common
│   ├── config, type, validation, event 등 공통 유틸
└── StockServiceApplication.java
```

---

### 🔧 환경 변수 설정

```yaml
spring:
  data:
    mongodb:
      host: ${MONGO_HOST:localhost}
      port: ${MONGO_PORT:27017}
      database: xray-stock
      username: ${MONGO_USER}
      password: ${MONGO_PASS}

endpoint:
  stock-generator:
    base-url: http://localhost:8080/api/v1
```

`.env` 예시:
```
MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_USER=myuser
MONGO_PASS=mypass
```

---

### 🚀 실행 방법

```bash
# 실행
./gradlew bootRun

# 테스트
./gradlew test
```

MongoDB 인스턴스가 실행 중이어야 하며, TimeSeries 컬렉션이 TTL 설정 포함되어야 합니다.

---

### 🧪 API 요약

| Method | Path                                            | 설명                       |
|--------|-------------------------------------------------|----------------------------|
| POST   | `/api/v1/stocks/save`                           | 종목 저장                  |
| POST   | `/api/v1/stocks/{stockId}/collecting`           | 종목 수집 시작             |
| DELETE | `/api/v1/stocks/{stockId}/collecting`           | 종목 수집 중지             |
| GET    | `/api/v1/stocks`                                | 종목 ROI 순 리스트         |
| GET    | `/api/v1/stocks/{stockId}/candles`              | 캔들 조회                  |

---

### 기술 스택

- Java 17+
- Spring Boot
- Spring Data MongoDB (TimeSeries)
- Lombok
- Jakarta Validation

---

### 참고

- 본 프로젝트는 백오피스 및 실시간 주식 서비스 구조 연습용으로 설계되었습니다.
- 실거래 환경에는 적절한 인증, 캐싱, 모니터링이 추가되어야 합니다.
