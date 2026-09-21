package io.github.irfanghat.spark.opcua;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class OpcUaSchema {

    private OpcUaSchema() {
    }

    public static StructType schema() {
        return new StructType()
                .add("node_id", DataTypes.StringType, false)
                .add("browse_name", DataTypes.StringType, false)
                .add("value", DataTypes.StringType, true)
                .add("timestamp", DataTypes.TimestampType, true);
    }
}