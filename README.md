# OPC UA Data Source for Apache Spark
### Distributed Industrial Data Ingestion for Apache Spark

`spark-opcua` enables Apache Spark to read data **directly from OPC UA servers**. It brings **Industrial Telemetry** into Spark DataFrames allowing **Manufacturing**, **SCADA** and **IOT workloads** to leverage **Spark's SQL engine** for large-scale Analytics and downstream processing (**Java**, **Scala**, **Python**, **Go**, **Rust** and **C++** supported via **Spark Connect**).

* **OPC UA** is a standard protocol for Industrial Automation.

### Use cases
- Stream industrial telemetry directly into Spark pipelines.
- Analyze PLC and sensor data via Spark SQL.
- Feed manufacturing data into **Delta Lake** or **Iceberg**.
- Build **Predictive Maintenance** and **Anomaly Detection** workflows on top of **Spark MLlib**.
- Integrate Factory Systems with Modern Data Platforms e.g. **Databricks**.

### Quickstart

```java
SparkSession spark = SparkSession.builder()
    .master("local[*]")
    .appName("spark-opcua")
    .getOrCreate();

Dataset<Row> df = spark.read()
    .format("opcua")
    .option("endpoint", "opc.tcp://localhost:4840/milo/discovery")
    .load();

df.printSchema();

// ------------------------------------------------
// root
//  |-- node_id: string (nullable = false)
//  |-- browse_name: string (nullable = false)
//  |-- value: string (nullable = true)
//  |-- timestamp: timestamp (nullable = true)
// ------------------------------------------------


df.show(true);

// +-------+-----------+--------------------+--------------------+
// |node_id|browse_name|               value|           timestamp|
// +-------+-----------+--------------------+--------------------+
// | i=2254|     i=2254|[Ljava.lang.Strin...|2026-09-23 22:03:...|
// | i=2255|     i=2255|[Ljava.lang.Strin...|2026-09-23 22:03:...|
// |i=15004|    i=15004|                NULL|2026-09-23 22:03:...|
// | i=2256|     i=2256|ExtensionObject[b...|2026-09-23 22:03:...|
// | i=2267|     i=2267|                 255|2026-09-23 22:03:...|
// | i=2994|     i=2994|               false|2026-09-23 22:03:...|
// |i=12885|    i=12885|DateTime{date=Wed...|2026-09-23 22:03:...|
// |i=17634|    i=17634|                NULL|2026-09-23 22:03:...|
// | i=2269|     i=2269|[Ljava.lang.Strin...|2026-09-23 22:03:...|
// | i=2271|     i=2271|[Ljava.lang.Strin...|2026-09-23 22:03:...|
// +-------+-----------+--------------------+--------------------+
```

- **Runtime:** Java 17
- **Support:** Apache Spark **3.5.x**
- **License**: Apache-2.0