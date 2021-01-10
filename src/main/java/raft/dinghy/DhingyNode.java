package raft.dinghy;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import raft.dinghy.plane.control.*;
import raft.dinghy.plane.control.utils.ControlUtils;

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
        logger.log(Level.INFO, "creating node with " + id + " " + otherNodes);
        port = Integer.parseInt(id);
        persona = (new PersonaManager.Builder())
                .withIdentity(id)
                .withOtherNodes(otherNodes)
                .build();
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(this)
                .build()
                .start();
        logger.log(Level.INFO, "starting node listening at " + port);
        persona.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    DhingyNode.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server == null) return;

        // gRPC server shutdown semantics are similar to
        // ExecutorService's semantics, so using same pattern
        // https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
        // https://grpc.github.io/grpc-java/javadoc/io/grpc/Server.html
        try {
            persona.stop();
            server.shutdown();
            // Wait a while for existing tasks to terminate
            if (!server.awaitTermination(60, TimeUnit.SECONDS)) {
                server.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!server.awaitTermination(60, TimeUnit.SECONDS)) {
                    logger.log(Level.SEVERE, "unable to shutdown server");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            server.shutdownNow();
            // Preserve interrupt status
            // https://stackoverflow.com/a/36426266
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     * Use this when you want parent thread to go to background and just wait until JVM
     * is shutting down
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
