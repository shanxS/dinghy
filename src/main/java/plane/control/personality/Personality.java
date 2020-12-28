package plane.control.personality;

import io.grpc.stub.StreamObserver;
import plane.control.*;

public interface Personality <V extends Comparable<V>, K extends Comparable<K>> {

    enum Type {FOLLOWER, LEADER, CANDIDATE};

    void initShutdown();

    void setMetamorphosisCallback(Daemon.DhingyInternalImp<K,V> daemon);

    void appendEntries(AppendEntriesInput request, StreamObserver<AppendEntriesOutput> responseObserver);

    void requestVote(RequestVoteInput request, StreamObserver<RequestVoteOutput> responseObserver);

    void start();
}
