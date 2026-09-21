package io.github.irfanghat.spark.opcua;

import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.connector.read.ScanBuilder;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

public class OpcUaScanBuilder implements ScanBuilder {

    private final CaseInsensitiveStringMap options;

    public OpcUaScanBuilder(CaseInsensitiveStringMap options) {
        this.options = options;
    }

    @Override
    public Scan build() {
        return new OpcUaScan(options);
    }
}