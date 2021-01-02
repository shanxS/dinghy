import io.grpc.stub.StreamObserver;
import plane.control.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DinghyNode extends DhingyInternalGrpc.DhingyInternalImplBase {

    Logger logger = Logger.getLogger(DinghyNode.class.getName());
    private PersonaManager persona;

    public DinghyNode(String id, List<Integer> otherNodes) {
        logger.log(Level.INFO, "starting node with " + id + " " + otherNodes);
        persona = (new PersonaManager.Builder())
                .withIdentity(id)
                .withOtherNodes(otherNodes)
                .build();
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

    public void init() {
        persona.init();
    }
}
