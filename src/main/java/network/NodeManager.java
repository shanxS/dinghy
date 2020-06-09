package network;

import java.util.Set;

/**
 * Class to manage network level details of nodes
 */
public class NodeManager {
    private Set<NodeAddress> nodes;

    public boolean addNode(NodeAddress node) {
        return nodes.add(node);
    }

    public boolean removeNode(NodeAddress node) {
        return nodes.remove(node);
    }
}
