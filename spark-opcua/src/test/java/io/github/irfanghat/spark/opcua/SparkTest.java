package io.github.irfanghat.spark.opcua;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ServiceLoader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.connector.catalog.TableProvider;
import org.junit.jupiter.api.Test;

class OpcUaDataSourceTest {
    @Test
    void shouldLoadOpcUaDataSource() {

        SparkSession spark = SparkSession.builder()
                .master("local[2]")
                .appName("spark-opcua-test")
                .getOrCreate();

        try {
            Dataset<Row> df = spark.read()
                    .format("opcua")
                    .option(
                            "endpoint",
                            "opc.tcp://localhost:4840/milo/discovery")
                    .load();

            assertNotNull(df);

            df.printSchema();
            df.show(false);

        } finally {
            spark.stop();
        }
    }
}