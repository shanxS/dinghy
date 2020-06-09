package plane.control.personality;

import plane.control.Daemon;
import plane.control.StateMachine;

public class Follower<K extends Comparable<K>, V extends Comparable<V>> implements Personality {
    private StateMachine<K,V> stateMachine;
    public Follower(StateMachine<K,V> sm) {
        this.stateMachine = sm;
    }


    @Override
    public void initShutdown() {

    }

    @Override
    public void setMetamorphosisCallback(Daemon daemon) {

    }
}
