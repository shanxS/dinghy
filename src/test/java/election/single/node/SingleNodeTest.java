package election.single.node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raft.dinghy.DhingyNode;
import raft.dinghy.plane.control.PersonaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class SingleNodeTest {
    @Test
    @DisplayName("Node starts as Follower and flip flops b/n Candidate and Follower")
    public void singleNodeFlipFlops() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        DhingyNode node = new DhingyNode("1500", Arrays.asList(1501));
        TypeCallbackHandler callbackHandler = new TypeCallbackHandler();
        node.setPersonaUpdateCallback(callbackHandler);

        node.startServer();
        Thread.sleep(3000); // sleep while node is vacillating between personas
        node.stopServer();

        callbackHandler.setStopUpdate();
        List<PersonaType.Type> history = callbackHandler.getTypeHistory();
        assertTrue(history.size()>1);

        for(int i=1; i<history.size(); ++i) {
            assertTrue(history.get(i).compareTo(history.get(i-1)) != 0);
        }
    }

    class TypeCallbackHandler implements Consumer<PersonaType.Type> {
        // using volatile to sync owner thread and callback thread
        // https://docs.oracle.com/javase/tutorial/essential/concurrency/atomic.html
        private volatile boolean stopUpdate = false;

        private List<PersonaType.Type> typeHistory = new ArrayList<>();

        @Override
        public void accept(PersonaType.Type type) {
            if(!stopUpdate) { // read atomically
                typeHistory.add(type);
            }
        }

        public void setStopUpdate() {
            stopUpdate = true; // set atomically
        }

        public List<PersonaType.Type> getTypeHistory() {
            if(!stopUpdate) throw new RuntimeException("You need to stop the update before getting the history");
            return typeHistory;
        }
    }
}
