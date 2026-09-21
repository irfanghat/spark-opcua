package io.github.irfanghat.spark.opcua;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.connector.read.Batch;
import org.apache.spark.sql.connector.read.InputPartition;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class OpcUaBatch implements Batch {

    private final CaseInsensitiveStringMap options;

    public OpcUaBatch(CaseInsensitiveStringMap options) {
        this.options = options;
    }

    @Override
    public InputPartition[] planInputPartitions() {

        String endpoint = options.get("endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required option: endpoint");
        }

        try {
            OpcUaClient client = OpcUaClient.create(endpoint);

            client.connect();

            try {
                OpcUaBrowser browser = new OpcUaBrowser();

                List<NodeId> nodes = browser.browse(
                        client,
                        NodeIds.RootFolder);

                return createPartitions(nodes);

            } finally {
                client.disconnect();
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to discover OPC UA nodes",
                    e);
        }
    }

    private InputPartition[] createPartitions(
            List<NodeId> nodes) {

        List<InputPartition> partitions = new ArrayList<>();

        for (NodeId node : nodes) {

            partitions.add(
                    new OpcUaInputPartition(
                            List.of(node.toParseableString())));
        }

        return partitions.toArray(
                new InputPartition[0]);
    }

    @Override
    public PartitionReaderFactory createReaderFactory() {
        return new OpcUaPartitionReaderFactory(options);
    }
}