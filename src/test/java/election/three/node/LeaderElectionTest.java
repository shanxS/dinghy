package election.three.node;

import election.utils.PersonaTypeUpdateCallbackHandler;
import election.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raft.dinghy.DhingyNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LeaderElectionTest {

    @Test
    @DisplayName("Start 3 node system and 2 nodes become follower and third becomes leader")
    public void leaderGetsElectedTest() throws IOException, InterruptedException {
        Map<DhingyNode, PersonaTypeUpdateCallbackHandler> nodePersonCallbackMap = TestUtils.createNodesWithPersonaTypeUpdateCallbackHanders(Arrays.asList(1500, 1501, 1502));
        for(DhingyNode node : nodePersonCallbackMap.keySet()) {
            node.start();
        }


        for(DhingyNode node : nodePersonCallbackMap.keySet()) {
            node.stop();
        }
    }
}
