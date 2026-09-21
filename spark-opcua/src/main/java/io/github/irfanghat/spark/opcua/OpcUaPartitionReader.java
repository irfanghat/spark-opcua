package io.github.irfanghat.spark.opcua;

import static org.apache.spark.sql.catalyst.util.DateTimeUtils.millisToMicros;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow;
import org.apache.spark.sql.catalyst.util.DateTimeUtils;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class OpcUaPartitionReader
        implements PartitionReader<InternalRow> {

    private final OpcUaClient client;
    private final Iterator<String> nodeIds;

    private InternalRow current;

    public OpcUaPartitionReader(
            CaseInsensitiveStringMap options,
            OpcUaInputPartition partition) {

        String endpoint = options.get("endpoint");

        if (endpoint == null) {
            throw new IllegalArgumentException(
                    "Missing required option: endpoint");
        }

        try {
            this.client = OpcUaClient.create(endpoint);
            this.client.connect();

            this.nodeIds = partition.nodeIds().iterator();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to connect to OPC UA server",
                    e);
        }
    }

    @Override
    public boolean next() {

        if (!nodeIds.hasNext()) {
            return false;
        }

        String nodeIdString = nodeIds.next();

        try {
            NodeId nodeId = NodeId.parse(nodeIdString);

            DataValue value = client.readValue(0, null, nodeId);

            String valueString = value.getValue().getValue().toString();

            long timestamp = millisToMicros(
                    value.getServerTime().getJavaDate().getTime());

            current = new GenericInternalRow(new Object[] {
                    nodeIdString,
                    nodeIdString,
                    valueString,
                    timestamp
            });

            return true;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to read OPC UA node: " + nodeIdString,
                    e);
        }
    }

    @Override
    public InternalRow get() {
        return current;
    }

    @Override
    public void close() {
        try {
            client.disconnect();
        } catch (Exception e) {
            // Nothing else to do during cleanup.
        }
    }
}