package plane.control;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import plane.control.personality.Candidate;
import plane.control.personality.Follower;
import plane.control.personality.Leader;
import plane.control.personality.Personality;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Runs the gRPC server and dispatches calls to different personalities.
 * A personality can ask for metamorphosis to another personality by the callback
 * @param <K>
 * @param <V>
 */
@Log4j2
public class Daemon<K extends Comparable<K>, V extends Comparable<V>> {
    private Server server;

    public void start(StateMachine<K,V> sm, int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new DhingyInternalImp<K,V>(sm))
                .build()
                .start();
        log.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    Daemon.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                log.info("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static class DhingyInternalImp <K extends Comparable<K>, V extends Comparable<V>> extends DhingyInternalGrpc.DhingyInternalImplBase {
        private Personality<K,V> personality;
        private HashMap<Personality.Type, Personality<K, V>> personalityCache;
        private StateMachine<K, V> stateMachine;

        public DhingyInternalImp(StateMachine<K,V> sm) {
            personalityCache = new HashMap<>();
            stateMachine = sm;
            personalityCache.put(Personality.Type.LEADER, new Leader<K,V>(stateMachine));
            personalityCache.put(Personality.Type.FOLLOWER, new Follower<K,V>(stateMachine));
            personalityCache.put(Personality.Type.CANDIDATE, new Candidate<K,V>(stateMachine));

            personality = personalityCache.get(Personality.Type.CANDIDATE);
        }

        /**
         * Called by a given personality instance to morph to another personality
         * @param type
         */
        public void personalityMetamorphosis(Personality.Type type) {
            personality = personalityCache.get(type);
        }

        @Override
        public void appendEntries(AppendEntriesInput request, StreamObserver<AppendEntriesOutput> responseObserver) {
            personality.appendEntries(request, responseObserver);
        }

        @Override
        public void requestVote(RequestVoteInput request, StreamObserver<RequestVoteOutput> responseObserver) {
            personality.requestVote(request, responseObserver);
        }
    }
}
