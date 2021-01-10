package election.single.node;

import election.utils.PersonaTypeUpdateCallbackHandler;
import election.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raft.dinghy.DhingyNode;
import raft.dinghy.plane.control.PersonaType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class SingleNodeTest {
    @Test
    @DisplayName("Node starts as Follower and flip flops b/n Candidate and Follower")
    public void singleNodeFlipFlops() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Map<DhingyNode, PersonaTypeUpdateCallbackHandler> nodeCallbackMap = TestUtils.createNodesWithPersonaTypeUpdateCallbackHanders(Arrays.asList(1500, 1501));
        DhingyNode node = null;
        for(DhingyNode n : nodeCallbackMap.keySet()) node = n;
        PersonaTypeUpdateCallbackHandler callbackHandler = nodeCallbackMap.get(node);

        node.start();
        Thread.sleep(3000); // sleep while node is vacillating between personas
        node.stop();

        callbackHandler.setStopUpdate();
        List<PersonaType.Type> history = callbackHandler.getTypeHistory();
        assertTrue(history.size()>1);

        for(int i=1; i<history.size(); ++i) {
            assertTrue(history.get(i).compareTo(history.get(i-1)) != 0);
        }
    }
}
