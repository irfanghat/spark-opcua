package io.github.irfanghat.spark.opcua;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.connector.read.InputPartition;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

public class OpcUaPartitionReaderFactory
        implements PartitionReaderFactory {

    private final CaseInsensitiveStringMap options;

    public OpcUaPartitionReaderFactory(
            CaseInsensitiveStringMap options) {

        this.options = options;
    }

    @Override
    public PartitionReader<InternalRow> createReader(
            InputPartition partition) {

        OpcUaInputPartition opcUaPartition = (OpcUaInputPartition) partition;

        return new OpcUaPartitionReader(
                options,
                opcUaPartition);
    }
}