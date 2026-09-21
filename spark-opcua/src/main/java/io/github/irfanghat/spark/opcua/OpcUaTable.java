package io.github.irfanghat.spark.opcua;

import org.apache.spark.sql.connector.catalog.SupportsRead;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableCapability;
import org.apache.spark.sql.connector.read.ScanBuilder;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

import java.util.Set;

import static org.apache.spark.sql.connector.catalog.TableCapability.BATCH_READ;

public class OpcUaTable implements Table, SupportsRead {

    private final StructType schema;

    public OpcUaTable(
            StructType schema,
            CaseInsensitiveStringMap options) {

        this.schema = schema;
    }

    @Override
    public String name() {
        return "OPC UA";
    }

    @Override
    public StructType schema() {
        return schema;
    }

    @Override
    public ScanBuilder newScanBuilder(
            CaseInsensitiveStringMap options) {

        return new OpcUaScanBuilder(options);
    }

    @Override
    public Set<TableCapability> capabilities() {
        return Set.of(BATCH_READ);
    }
}