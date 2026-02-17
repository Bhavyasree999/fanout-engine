# 🚀 High-Throughput Fan-Out Engine

## 📌 Overview

This project implements a Distributed Data Fan-Out & Transformation Engine in Java.

In modern data architectures, a single source of truth (such as a bulk export file) must be propagated to multiple downstream systems like REST APIs, gRPC services, message queues, and databases.

This system:

- Streams large files safely (supports very large files without loading into memory)
- Applies per-sink transformations using the Strategy Pattern
- Dispatches records concurrently
- Implements rate limiting per sink
- Supports retry logic with Dead Letter Queue (DLQ)
- Provides throughput and observability metrics
- Ensures backpressure using BlockingQueue

---

## 🏗 Architecture

### Data Flow

FileProducer (Streaming)
        ↓
BlockingQueue (Backpressure)
        ↓
FanOutOrchestrator
        ↓
 ├── REST Sink (JSON)
 ├── gRPC Sink (Protobuf - mocked)
 ├── MQ Sink (XML)
 └── DB Sink (Avro-like Map)
        ↓
Metrics + Dead Letter Queue

---

## 🧠 Technical Design

### 1️⃣ Ingestion Layer (Memory Safe Streaming)

- Uses BufferedReader
- Reads file line-by-line
- Does NOT load entire file into memory
- Safe under small heap sizes (e.g., -Xmx512m)
- Uses ArrayBlockingQueue to implement backpressure

If sinks are slow:
- Queue fills
- Producer blocks
- Memory remains stable
- No OutOfMemoryError occurs

---

### 2️⃣ Transformation Layer (Strategy Pattern)

Each sink requires a different format:

| Sink | Format |
|------|--------|
| REST | JSON |
| gRPC | Protobuf (mocked) |
| MQ | XML |
| DB | Avro-like Map |

Implemented using:
- Transformer interface
- Concrete transformer classes
- TransformerFactory

Extensibility:
Adding a new sink requires only:
- New Sink implementation
- New Transformer
No changes to the orchestrator are needed.

---

### 3️⃣ Distribution Layer (Mock Sinks)

Each sink:

- Implements Sink interface
- Uses Guava RateLimiter
- Simulates async network calls
- Randomly fails to test retry
- Returns CompletableFuture<Boolean>

---

### 4️⃣ Concurrency Model

- Uses ExecutorService (Fixed Thread Pool)
- Thread count = number of available CPU cores
- Processes sinks in parallel
- Uses AtomicLong and ConcurrentHashMap for thread safety

This ensures scalability and no race conditions.

---

### 5️⃣ Throttling (Rate Limiting)

Each sink has configurable rate limits defined in application.yaml.

Example:

restRate: 50
grpcRate: 100
mqRate: 200
dbRate: 500

Implemented using:
com.google.common.util.concurrent.RateLimiter

Prevents overwhelming downstream systems.

---

### 6️⃣ Retry & Dead Letter Queue (DLQ)

- Maximum 3 retry attempts per record per sink
- After 3 failures, record is added to Dead Letter Queue
- DLQ size is printed at completion

Ensures:
- Zero data loss
- Fault tolerance
- Failure accountability

---

### 7️⃣ Observability

The system prints:

- Total operations
- Throughput (records/sec)
- Success count per sink
- Failure count per sink
- Dead Letter Queue size

Example output:

Total: 45
Throughput (records/sec): 28.42
rest Success: 10
grpc Success: 10
mq Success: 10
db Success: 10
Dead Letter Records: 2
Processing Completed. Application Shutting Down.

---

## ⚙️ Setup Instructions

### Prerequisites

- Java 17+
- Maven 3.9+

---

### Build

mvn clean package

---

### Run

java -jar target/fanout-engine-1.0.jar

---

### Run Tests

mvn clean test

---


## 📂 Project Structure

```text
fanout-engine/
├── pom.xml
├── README.md
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/com/fanout/
│   │   │   ├── Main.java
│   │   │   ├── config/
│   │   │   ├── ingestion/
│   │   │   ├── model/
│   │   │   ├── orchestrator/
│   │   │   ├── sink/
│   │   │   ├── transform/
│   │   │   └── metrics/
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── sample.csv
│   └── test/
│       └── java/com/fanout/
│           ├── TransformerTest.java
│           └── OrchestratorTest.java
```


## 🛡 Non-Functional Improvements

Security & Reliability:
- Rate limiting prevents overload
- Thread-safe collections used
- Config-driven architecture
- Backpressure prevents memory overflow

Performance:
- Streaming ingestion
- CPU-based thread pool sizing
- Minimal memory footprint
- Concurrent collections

---

## 🧪 Testing

Includes:
- Transformer unit test
- Metrics test

Run:

mvn test

---

## 📌 Assumptions

- Input file is valid CSV
- Protobuf and Avro are mocked
- Downstream systems are simulated
- Network failures are randomized

---

## 🤖 AI Tooling Usage

This project was developed with GPT-assisted guidance for:

- Streaming implementation
- Strategy pattern design
- Retry and DLQ logic
- Throughput calculation
- Rate limiting integration
- Unit test creation
- Documentation generation

---

## 📈 Evaluation Alignment

✔ Concurrency Logic  
✔ Memory Management  
✔ Design Patterns  
✔ Resilience (Retry + DLQ)  
✔ Throttling  
✔ Observability  
✔ Config-Driven  
✔ Testing  


