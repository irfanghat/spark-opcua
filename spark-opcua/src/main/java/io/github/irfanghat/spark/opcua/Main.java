package io.github.irfanghat.spark.opcua;

import static java.util.Objects.requireNonNullElse;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

public class Main {

    private static final String DISCOVERY_ENDPOINT = "opc.tcp://localhost:4840/milo/discovery";

    public static void main(String[] args) throws Exception {

        OpcUaClient client = OpcUaClient.create(DISCOVERY_ENDPOINT);

        client.connect();

        System.out.println("\nConnected!");
        System.out.println("Browsing OPC UA server...\n");

        browseNode("", client, NodeIds.RootFolder);

        client.disconnect();
    }

    private static void browseNode(String indent, OpcUaClient client, NodeId browseRoot) {

        BrowseDescription browse = new BrowseDescription(
                browseRoot,
                BrowseDirection.Forward,
                NodeIds.References,
                true,
                uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue()));

        try {
            BrowseResult result = client.browse(browse);

            ReferenceDescription[] references = requireNonNullElse(result.getReferences(), new ReferenceDescription[0]);

            for (ReferenceDescription rd : references) {
                System.out.println(indent + rd.getBrowseName().name());

                rd.getNodeId()
                        .toNodeId(client.getNamespaceTable())
                        .ifPresent(nodeId -> browseNode(indent + "  ", client, nodeId));
            }

        } catch (UaException e) {
            System.err.println("Failed to browse " + browseRoot + ": " + e.getMessage());
        }
    }
}