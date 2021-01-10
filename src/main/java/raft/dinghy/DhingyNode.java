package raft.dinghy;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import raft.dinghy.plane.control.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DhingyNode extends DhingyInternalGrpc.DhingyInternalImplBase {

    Logger logger = Logger.getLogger(DhingyNode.class.getName());
    private PersonaManager persona;
    private Server server;
    private int port;

    public DhingyNode(String id, List<Integer> otherNodes) {
        logger.log(Level.INFO, "starting node with " + id + " " + otherNodes);
        port = Integer.parseInt(id);
        persona = (new PersonaManager.Builder())
                .withIdentity(id)
                .withOtherNodes(otherNodes)
                .build();
    }

    public void startServer() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(this)
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        persona.init();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    stopServer();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stopServer() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void setPersonaUpdateCallback(Consumer<PersonaType.Type> c) { persona.setPersonaUpdateCallback(c); }

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
