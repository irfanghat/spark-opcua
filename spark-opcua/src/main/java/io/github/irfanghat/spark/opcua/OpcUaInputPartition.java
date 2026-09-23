package io.github.irfanghat.spark.opcua;

import java.util.List;

import org.apache.spark.sql.connector.read.InputPartition;

public class OpcUaInputPartition implements InputPartition {

    private final List<String> nodeIds;

    public OpcUaInputPartition(List<String> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<String> nodeIds() {
        return nodeIds;
    }
}