package io.github.irfanghat.spark.opcua;

import java.io.Serializable;
import java.util.List;

import org.apache.spark.sql.connector.read.InputPartition;

public class OpcUaInputPartition implements InputPartition, Serializable {

    private final List<String> nodeIds;

    public OpcUaInputPartition(List<String> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<String> nodeIds() {
        return nodeIds;
    }
}