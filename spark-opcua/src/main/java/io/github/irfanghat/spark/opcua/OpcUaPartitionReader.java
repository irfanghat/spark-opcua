package io.github.irfanghat.spark.opcua;

import static org.apache.spark.sql.catalyst.util.DateTimeUtils.millisToMicros;

import java.util.Iterator;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.unsafe.types.UTF8String;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

public class OpcUaPartitionReader
        implements PartitionReader<InternalRow> {

    private final OpcUaClient client;
    private final Iterator<String> nodeIds;

    private InternalRow current;

    public OpcUaPartitionReader(
            OpcUaOptions options,
            OpcUaInputPartition partition) {

        if (options.endpoint() == null || options.endpoint().isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required option: endpoint");
        }

        try {
            this.client = OpcUaClient.create(options.endpoint());
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

            DataValue dataValue = client.readValue(
                    0,
                    TimestampsToReturn.Both,
                    nodeId);

            Object rawValue = dataValue.getValue().getValue();

            String valueString = rawValue != null
                    ? rawValue.toString()
                    : null;

            Long timestamp = null;

            if (dataValue.getServerTime() != null) {
                timestamp = millisToMicros(
                        dataValue.getServerTime()
                                .getJavaDate()
                                .getTime());
            }

            current = new GenericInternalRow(new Object[] {
                    UTF8String.fromString(nodeIdString),
                    UTF8String.fromString(nodeIdString),
                    valueString != null
                            ? UTF8String.fromString(valueString)
                            : null,
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
        } catch (Exception ignored) {
        }
    }
}