package election.utils;

import raft.dinghy.DhingyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
    public static List<DhingyNode> createNodes(List<Integer> ports) {
        List<DhingyNode> nodes = new ArrayList<>();

        for(int i=0; i<ports.size(); ++i) {
            List<Integer> otherPorts = new ArrayList<>(ports);
            otherPorts.remove(i);
            nodes.add(new DhingyNode(ports.get(i).toString(), otherPorts));
        }

        return nodes;
    }

    public static Map<DhingyNode, PersonaTypeUpdateCallbackHandler> createNodesWithPersonaTypeUpdateCallbackHanders(List<Integer> ports) {
        List<DhingyNode> nodes = createNodes(ports);
        Map<DhingyNode, PersonaTypeUpdateCallbackHandler> map = new HashMap<>();

        for(DhingyNode node : nodes) {
            PersonaTypeUpdateCallbackHandler callbackHandler = new PersonaTypeUpdateCallbackHandler();
            node.setPersonaUpdateCallback(callbackHandler);
            map.put(node, callbackHandler);
        }

        return map;
    }

}
