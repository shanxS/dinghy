package plane.control.personality;

import io.grpc.stub.StreamObserver;
import plane.control.AppendEntriesInput;
import plane.control.Daemon;
import plane.control.RequestVoteInput;
import plane.control.StateMachine;

public class Candidate <K extends Comparable<K>, V extends Comparable<V>> implements Personality {
    private StateMachine<K, V> stateMachine;

    public Candidate(StateMachine<K, V> stateMachine) {
        this.stateMachine = stateMachine;
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
