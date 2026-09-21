package io.github.irfanghat.spark.opcua;

import org.apache.spark.sql.connector.read.Batch;
import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

public class OpcUaScan implements Scan {

    private final CaseInsensitiveStringMap options;

    public OpcUaScan(CaseInsensitiveStringMap options) {
        this.options = options;
    }

    @Override
    public StructType readSchema() {
        return OpcUaSchema.schema();
    }

    @Override
    public Batch toBatch() {
        return new OpcUaBatch(options);
    }
}