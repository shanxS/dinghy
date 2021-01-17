package election.three.node;

import election.utils.PersonaTypeUpdateCallbackHandler;
import election.utils.TestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import raft.dinghy.DhingyNode;
import raft.dinghy.plane.control.PersonaType;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LeaderElectionTest {

    @RepeatedTest(5)
    @DisplayName("Start 3 node system and 2 nodes become follower and third becomes leader")
    public void leaderGetsElectedTest() throws IOException, InterruptedException {
        List<Integer> ports = Arrays.asList(1500, 1501, 1502);
        Map<DhingyNode, PersonaTypeUpdateCallbackHandler> nodePersonCallbackMap = TestUtils.createNodesWithPersonaTypeUpdateCallbackHanders(ports);
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

            if(history.get(history.size()-1).compareTo(PersonaType.Type.LEADER) == 0) {
                assertEquals(PersonaType.Type.CANDIDATE, history.get(history.size()-2));
                ++leaderCount;
            } else if(history.get(history.size()-1).compareTo(PersonaType.Type.FOLLOWER) == 0) ++followerCount;
        }

        assertEquals(1, leaderCount, "there should be exactly 1 leader");
        assertEquals(ports.size()-1, followerCount, "there should be exactly 2 follwers");
    }
}
