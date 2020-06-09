package plane.control;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import plane.control.personality.Candidate;
import plane.control.personality.Personality;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Runs the gRPC server and dispatches calls to different personalities.
 * A personality can ask for metamorphosis to another personality by the callback
 * @param <K>
 * @param <V>
 */
@Log4j2
public class Daemon<K extends Comparable<K>, V extends Comparable<V>> {
    private StateMachine<K, V> stateMachine;
    private Personality personality;
    private AtomicBoolean shutdown;

    public Daemon(StateMachine<K,V> sm) {
        this.stateMachine = sm;
        shutdown = new AtomicBoolean();
        shutdown.set(false);
    }

    public void initShutdown() {
        shutdown.set(true);
        personality.initShutdown();
    }
}
