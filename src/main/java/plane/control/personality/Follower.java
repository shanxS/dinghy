package plane.control.personality;

import io.grpc.stub.StreamObserver;
import plane.control.AppendEntriesInput;
import plane.control.Daemon;
import plane.control.RequestVoteInput;
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
    public void setMetamorphosisCallback(Daemon.DhingyInternalImp daemon) {

    }

    @Override
    public void start() {

    }

    @Override
    public void requestVote(RequestVoteInput request, StreamObserver responseObserver) {

    }

    @Override
    public void appendEntries(AppendEntriesInput request, StreamObserver responseObserver) {

    }

}
