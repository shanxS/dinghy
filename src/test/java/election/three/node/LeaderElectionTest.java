package election.three.node;

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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LeaderElectionTest {

    @Test
    @DisplayName("Start 3 node system and 2 nodes become follower and third becomes leader")
    public void leaderGetsElectedTest() throws IOException, InterruptedException {
        Map<DhingyNode, PersonaTypeUpdateCallbackHandler> nodePersonCallbackMap = TestUtils.createNodesWithPersonaTypeUpdateCallbackHanders(Arrays.asList(1500, 1501, 1502));
        for(DhingyNode node : nodePersonCallbackMap.keySet()) {
            node.start();
        }

        Thread.sleep(30000); // sleep while system stablises

        for(Map.Entry<DhingyNode, PersonaTypeUpdateCallbackHandler>  entry : nodePersonCallbackMap.entrySet()) {
            entry.getKey().stop();
            entry.getValue().setStopUpdate();
        }

        int leaderCount = 0;
        int followerCount = 0;
        for(PersonaTypeUpdateCallbackHandler callbackHandler : nodePersonCallbackMap.values()) {
            List<PersonaType.Type> history = callbackHandler.getTypeHistory();
            assertTrue(history.size()>0, "No persona updates found for this node");
            assertTrue(history.get(history.size()-1).compareTo(PersonaType.Type.CANDIDATE) != 0,
                    "Found a node which was candidate in end, that means system did not reach steady state");

            System.out.println(history);

            if(history.get(history.size()-1).compareTo(PersonaType.Type.LEADER) == 0) ++leaderCount;
            if(history.get(history.size()-1).compareTo(PersonaType.Type.FOLLOWER) == 0) ++followerCount;
        }

        assertEquals(1, leaderCount, "there should be exactly 1 leader");
        assertEquals(2, followerCount, "there should be exactly 2 follwers");
    }
}
