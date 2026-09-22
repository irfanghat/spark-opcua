package io.github.irfanghat.spark.opcua;

import static java.util.Objects.requireNonNullElse;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static final int DEFAULT_MAX_NODES = 10;

    public List<NodeId> browse(
            OpcUaClient client,
            NodeId root) {

        return browse(client, root, DEFAULT_MAX_NODES);
    }

    public List<NodeId> browse(
            OpcUaClient client,
            NodeId root,
            int maxNodes) {

        if (maxNodes <= 0) {
            throw new IllegalArgumentException(
                    "maxNodes must be greater than 0");
        }

        List<NodeId> variables = new ArrayList<>();
        Set<NodeId> visited = new HashSet<>();
        ArrayDeque<NodeId> queue = new ArrayDeque<>();

        visited.add(root);
        queue.add(root);

        while (!queue.isEmpty()
                && variables.size() < maxNodes) {

            NodeId current = queue.removeFirst();

            browseNode(
                    client,
                    current,
                    variables,
                    visited,
                    queue,
                    maxNodes);
        }

        return variables;
    }

    private void browseNode(
            OpcUaClient client,
            NodeId browseRoot,
            List<NodeId> variables,
            Set<NodeId> visited,
            ArrayDeque<NodeId> queue,
            int maxNodes) {

        BrowseDescription browse = new BrowseDescription(
                browseRoot,
                BrowseDirection.Forward,
                NodeIds.HierarchicalReferences,
                true,
                uint(
                        NodeClass.Object.getValue()
                                | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue()));

        try {
            BrowseResult result = client.browse(browse);

            ReferenceDescription[] references =
                    requireNonNullElse(
                            result.getReferences(),
                            new ReferenceDescription[0]);

            for (ReferenceDescription rd : references) {

                if (variables.size() >= maxNodes) {
                    return;
                }

                rd.getNodeId()
                        .toNodeId(client.getNamespaceTable())
                        .ifPresent(nodeId -> {

                            if (!visited.add(nodeId)) {
                                return;
                            }

                            NodeClass nodeClass =
                                    rd.getNodeClass();

                            if (nodeClass == NodeClass.Variable) {

                                variables.add(nodeId);

                            } else if (nodeClass == NodeClass.Object) {

                                queue.addLast(nodeId);
                            }
                        });
            }

        } catch (UaException e) {
            throw new RuntimeException(
                    "Failed to browse OPC UA node: "
                            + browseRoot,
                    e);
        }
    }
}