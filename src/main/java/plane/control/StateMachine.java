package plane.control;

import lombok.Getter;
import network.NodeAddress;
import plane.data.DataLog;
import plane.data.Term;

/**
 * Deterministic state machine that computes same state
 * given a seq. of data logs
 */
public class StateMachine<K extends Comparable<K>, V extends Comparable<V>> {
    public enum State {FOLLOWER, CANDIDATE, LEADER};

    @Getter // latest term server has seen (initialized to 0 on first boot, increases monotonically)
    private Term currentTerm;
    @Getter // id of this node
    private NodeAddress selfId;
    private DataLog<K, V> dataLog;
    @Getter
    private State currentState;
}
