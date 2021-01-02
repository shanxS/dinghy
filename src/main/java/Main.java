import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        int nodeId = Integer.parseInt(args[0]);
        List<Integer> list = Arrays.asList(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        NodeWrapper node = new NodeWrapper();
        node.start(nodeId, list);
        node.blockUntilShutdown();
    }
}

class NodeWrapper {
    Logger logger = Logger.getLogger(NodeWrapper.class.getName());
    private Server server;

    public void start(int port, List<Integer> list) throws IOException {
        DinghyNode node = new DinghyNode(""+port, list);
        server = ServerBuilder.forPort(port)
                .addService(node)
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        node.init();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    NodeWrapper.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
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
}
