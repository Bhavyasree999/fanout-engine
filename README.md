# 🚀 High-Throughput Fan-Out Engine

## 📌 Overview

This project implements a Distributed Data Fan-Out & Transformation Engine in Java.

The system reads records from a CSV file and distributes them concurrently to multiple downstream mock sinks while ensuring:

- Streaming ingestion (no full file load into memory)
- Backpressure handling using BlockingQueue
- Per-sink dynamic rate limiting
- Retry mechanism with Dead Letter Queue (DLQ)
- Throughput and observability metrics
- Zero data loss guarantee

This simulates a production-grade backend data pipeline used in modern distributed systems.

---

## 🏗 Architecture

### 🔹 High-Level Flow

```text
CSV File
   ↓
FileProducer (Streaming)
   ↓
BlockingQueue (Backpressure Buffer)
   ↓
FanOutOrchestrator (Thread Pool Execution)
   ↓
Parallel Distribution to Sinks
   ↓
Metrics + Dead Letter Queue
```

---

### 🔹 Supported Sinks & Transformations

| Sink | Format | Description |
|------|--------|------------|
| REST API | JSON | Simulated HTTP POST |
| gRPC | Protobuf (mocked) | Simulated gRPC client |
| Message Queue | XML | Simulated topic publish |
| Wide-Column DB | Avro-like Map | Simulated async UPSERT |

Each sink has an independent configurable rate limiter.

---

## ⚙️ Setup & Execution

### 1️⃣ Build Project

```bash
mvn clean package
```

---

### 2️⃣ Run Application

```bash
java -jar target/fanout-engine-1.0.jar
```

---

### 3️⃣ Run With Limited Heap (Streaming Proof)

```bash
java -Xmx512m -jar target/fanout-engine-1.0.jar
```

This confirms:
- The file is processed line-by-line
- The entire dataset is NOT loaded into memory
- The system runs safely with small heap

---

## 🧠 Core Design Decisions

### 🔹 Streaming & Memory Safety

- File processed using BufferedReader
- No in-memory accumulation of all records
- Suitable for very large files (100GB+ conceptually)
- Stable and predictable memory footprint

---

### 🔹 Backpressure Strategy

Implemented using:

```java
ArrayBlockingQueue<>(queueCapacity);
```

Behavior:
- Producer blocks when queue is full
- Automatically slows ingestion if sinks are slow
- Prevents OutOfMemoryError
- Ensures stable performance under load

---

### 🔹 Concurrency Model

- Uses ExecutorService with CPU-based thread pool sizing
- Each record is processed across sinks in parallel
- Thread-safe metrics using AtomicLong & ConcurrentHashMap

Benefits:
- Scalable with available CPU cores
- Controlled concurrency
- No race conditions
- Clean parallel execution

---

### 🔹 Transformation Layer (Strategy Pattern)

Each sink requires a different output format:

- REST → JSON
- gRPC → Protobuf
- MQ → XML
- DB → Avro-like Map

Implemented using:
- Transformer interface
- Concrete transformer classes
- TransformerFactory

New sinks can be added without modifying the orchestrator.

---

### 🔹 Rate Limiting

Implemented using Guava `RateLimiter`.

Each sink has configurable limits defined in `application.yaml`.

Prevents overwhelming downstream systems.

---

### 🔹 Retry & Dead Letter Queue (DLQ)

- Maximum 3 retries per record per sink
- Failed records stored in Dead Letter Queue
- No silent drops

Every record results in:

```
Success OR Failure (after retries → DLQ)
```

Zero data loss guaranteed.

---

### 🔹 Observability & Metrics

At completion, the system prints:

- Total records processed
- Throughput (records/sec)
- Success count per sink
- Failure count per sink
- Dead Letter Queue size

Example Output:

```text
================ FINAL METRICS =================
Total Records Processed : 31
Throughput              : 18.88 records/sec

MQ    -> Success: 10 | Failure: 1
GRPC  -> Success: 10 | Failure: 0
DB    -> Success: 10 | Failure: 0

Dead Letter Records     : 0
================================================
Processing Completed. Application Shutting Down.
```

---

## 📂 Project Structure

```text
fanout-engine/
├── pom.xml
├── README.md
├── .gitignore
├── src/
│   ├── main/java/com/fanout/
│   │   ├── config/
│   │   ├── ingestion/
│   │   ├── model/
│   │   ├── orchestrator/
│   │   ├── sink/
│   │   ├── transform/
│   │   └── metrics/
│   ├── main/resources/
│   │   ├── application.yaml
│   │   └── sample.csv
│   └── test/java/com/fanout/
│       ├── TransformerTest.java
│       └── OrchestratorTest.java
```

---

## 🛡 Zero Data Loss Guarantee

Every record is accounted for:

- Success  
OR  
- Failure (after retries, captured in DLQ)

No record is ignored or silently dropped.

---

## ⚡ Scalability

The system scales based on:

- CPU cores
- Thread pool size
- Queue capacity
- Sink rate limits

The architecture supports adding new sinks without changing the core orchestrator.

---

## 🧪 Testing Strategy

Includes:

- Unit tests for transformers
- Metrics validation tests
- Orchestrator behavior test

Run tests using:

```bash
mvn test
```

---

## 🔮 Future Enhancements

- Persistent Dead Letter Queue
- Real REST/gRPC integration
- Kafka-based distribution
- Prometheus metrics integration
- Docker containerization
- Horizontal scaling

---

## 🎯 Key Highlights

- Streaming architecture
- Backpressure-safe design
- Concurrent sink execution
- Retry & resilience logic
- Throughput-based observability
- Extensible modular structure

