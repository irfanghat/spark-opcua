package io.github.irfanghat.spark.opcua;

import static java.util.Objects.requireNonNullElse;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.util.ArrayList;
import java.util.List;

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

public class OpcUaBrowser {

    public List<NodeId> browse(OpcUaClient client, NodeId root) {
        List<NodeId> nodes = new ArrayList<>();

        browseNode(client, root, nodes);

        return nodes;
    }

    private void browseNode(
            OpcUaClient client,
            NodeId browseRoot,
            List<NodeId> nodes) {

        BrowseDescription browse = new BrowseDescription(
                browseRoot,
                BrowseDirection.Forward,
                NodeIds.References,
                true,
                uint(
                        NodeClass.Object.getValue()
                                | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue()));

        try {
            BrowseResult result = client.browse(browse);

            ReferenceDescription[] references = requireNonNullElse(
                    result.getReferences(),
                    new ReferenceDescription[0]);

            for (ReferenceDescription rd : references) {

                rd.getNodeId()
                        .toNodeId(client.getNamespaceTable())
                        .ifPresent(nodeId -> {

                            nodes.add(nodeId);

                            browseNode(
                                    client,
                                    nodeId,
                                    nodes);
                        });
            }

        } catch (UaException e) {
            throw new RuntimeException(
                    "Failed to browse OPC UA node: " + browseRoot,
                    e);
        }
    }
}