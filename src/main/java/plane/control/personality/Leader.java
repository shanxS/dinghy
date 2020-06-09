package plane.control.personality;

import plane.control.Daemon;
import plane.control.StateMachine;

public class Leader<K extends Comparable<K>, V extends Comparable<V>> implements Personality {
    private StateMachine<K,V> stateMachine;

    public Leader(StateMachine<K,V> sm) {
        this.stateMachine = sm;
    }

    public Personality getNextPersonality() {
        return null;
    }

    @Override
    public void initShutdown() {

    }

    @Override
    public void setMetamorphosisCallback(Daemon daemon) {

    }
}
