import io.grpc.stub.StreamObserver;
import plane.control.*;

import java.util.List;

public class DinghyNode extends DhingyInternalGrpc.DhingyInternalImplBase {

    private Persona persona;

    public DinghyNode(int id, List<Integer> otherNodes) {
        persona = (new Persona.Builder())
                .withIdentity(id)
                .withOtherNodes(otherNodes)
                .build();
        persona.init();
    }

    @Override
    public void appendEntries(AppendEntriesInput request, StreamObserver<AppendEntriesOutput> responseObserver) {
        responseObserver.onNext(persona.appendEntries(request));
        responseObserver.onCompleted();
    }

    @Override
    public void requestVote(RequestVoteInput request, StreamObserver<RequestVoteOutput> responseObserver) {
        responseObserver.onNext(persona.requestVote(request));
        responseObserver.onCompleted();
    }
}
