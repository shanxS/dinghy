package plane.control.personality;

import plane.control.Daemon;
import plane.control.StateMachine;

public class Candidate <V extends Comparable<V>, K extends Comparable<K>> implements Personality {
    private StateMachine<K, V> stateMachine;

    public Candidate(StateMachine<K, V> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void initShutdown() {

    }

    @Override
    public void setMetamorphosisCallback(Daemon daemon) {

    }
}
