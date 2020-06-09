import network.NodeAddress;
import plane.control.StateMachine;

public class DinghyImpl <K extends Comparable<K>, V extends Comparable<V>> implements Dinghy<K, V> {
    private StateMachine<K, V> stateMachine;

    @Override
    public NodeAddress commit(K key, V value) {
        if(stateMachine.getCurrentState().equals(StateMachine.State.LEADER)) {
            // start commit process
            return null;
        } else return stateMachine.getCurrentTerm().getLeader();
    }

    @Override
    public V read(K key) {
//        stateMachine.
        return null;
    }

}
