package io.github.irfanghat.spark.opcua;

import java.util.Map;

import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableProvider;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

public class OpcUaDataSource implements TableProvider {

    @Override
    public StructType inferSchema(CaseInsensitiveStringMap options) {
        return OpcUaSchema.schema();
    }

    @Override
    public Table getTable(
            StructType schema,
            org.apache.spark.sql.connector.expressions.Transform[] transforms,
            Map<String, String> properties) {

        return new OpcUaTable(
                schema != null ? schema : OpcUaSchema.schema(),
                new CaseInsensitiveStringMap(properties));
    }

    @Override
    public boolean supportsExternalMetadata() {
        return false;
    }
}