# Scala Rule Engine

A purely functional discount rule engine built in Scala that processes retail transactions, automatically applies discount rules, computes final prices, and persists results to a MySQL database.

---

## Overview

This engine reads a batch of retail orders from a CSV file, evaluates each transaction against a set of configurable discount rules, aggregates the top two applicable discounts, and writes the processed results to a MySQL database — all while logging every step to a local log file.

---

## Features

- Pure functional Scala — no `var`, no mutable state, no loops
- Functional error handling using `Try` throughout
- Parallel batch processing using Scala parallel collections
- Memory-efficient streaming of large CSV files
- Batch database inserts for high-throughput writes
- Automatic logging to `rules_engine.log`

---

## Discount Rules

| # | Rule | Condition | Discount |
|---|------|-----------|----------|
| 1 | Expiry-based | Product expires in less than 30 days | (30 - daysRemaining)% |
| 2 | Product type | Product name contains "Cheese" | 10% |
| 2 | Product type | Product name contains "Wine" | 5% |
| 3 | Special date | Transaction on March 23rd | 50% |
| 4 | Quantity | 6–9 units | 5% |
| 4 | Quantity | 10–14 units | 7% |
| 4 | Quantity | 15+ units | 10% |
| 5 | App channel | Sale made through the App | quantity rounded up to nearest multiple of 5 / 100 |
| 6 | Visa payment | Payment made with Visa card | 5% |

**Aggregation logic:**
- No rules matched → 0% discount
- One or more rules matched → top 2 discounts are averaged

---

## Project Structure

```
retail-rule-engine/
├── build.sbt                        # Project dependencies
├── project/
│   └── build.properties             # SBT version
├── src/
│   └── main/
│       └── scala/
│           ├── Main.scala           # Entry point
│           ├── Transaction.scala    # Data models
│           ├── RuleEngine.scala     # Discount logic
│           ├── DatabaseWriter.scala # MySQL writer
│           └── Logger.scala         # File logger
└── rules_engine.log                 # Auto-generated log file
```

---

## Prerequisites

- Java JDK 11+
- SBT 1.9.7
- Scala 2.13.12
- MySQL 8+
- IntelliJ IDEA with Scala plugin (recommended)

---

## Setup

**1. Clone the repository**
```bash
git clone https://github.com/mahmoudafandi/Scala-Rule-Engine-.git
cd Scala-Rule-Engine-
```

**2. Set up the MySQL database**
```sql
CREATE DATABASE retail;
USE retail;

CREATE TABLE RuleEngine (
    timestamp     DATETIME      NOT NULL,
    productName   VARCHAR(80)   NOT NULL,
    expiryDate    DATE          NOT NULL,
    quantity      INT           NOT NULL,
    unitPrice     DECIMAL(10,2) NOT NULL,
    channel       VARCHAR(30)   NOT NULL,
    paymentMethod VARCHAR(20)   NOT NULL,
    discount      DECIMAL(10,2) NOT NULL,
    finalPrice    DECIMAL(10,2) NOT NULL
);
```

**3. Update database credentials**

In `DatabaseWriter.scala`, update the following:
```scala
val url      = "jdbc:mysql://localhost:3306/retail"
val username = "your_username"
val password = "your_password"
```

**4. Add your CSV file**

Place your input file at:
```
resources/TRX1000.csv
```

The CSV must have these headers:
```
timestamp, product_name, expiry_date, quantity, unit_price, channel, payment_method
```

**5. Open in IntelliJ**

Open the project folder in IntelliJ IDEA. When prompted, import as an SBT project and wait for dependencies to download.

---

## Running the Engine

In IntelliJ, open `Main.scala` and click the green play button next to `object Main`.

Or via SBT in the terminal:
```bash
sbt run
```

---

## Dependencies

```scala
"mysql"                  %  "mysql-connector-java"        % "8.0.33"
"com.github.tototoshi"   %% "scala-csv"                   % "1.3.10"
"org.scala-lang.modules" %% "scala-parallel-collections"  % "1.0.4"
```

---

## Scalability

The engine is designed to handle large batches (10M+ orders) through:

- **Streaming** — CSV is read in batches of 10,000 rows, never fully loaded into memory
- **Parallel processing** — each batch is processed across all available CPU cores using `.par`
- **Batch DB writes** — rows are queued with `addBatch()` and flushed with `executeBatch()` to minimize database round trips

---

## Logging

Every run appends to `rules_engine.log` in the project root:

```
2024-03-23T10:00:00 INFO  Starting rule engine
2024-03-23T10:00:01 INFO  Processing batch 1 (10000 rows)
2024-03-23T10:00:02 INFO  Batch 1 written successfully
...
2024-03-23T10:05:00 INFO  Rule engine finished
```

---

## Author

Mahmoud Afandi
