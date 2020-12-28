import io.prometheus.client.exporter.HTTPServer;
import plane.control.Daemon;
import plane.control.StateMachine;

import java.io.IOException;

public class Main {
    private static HTTPServer metricsServer;
    public static void main(String[] args) throws IOException, InterruptedException {
        validateArgs(args);

        int serverPort = Integer.parseInt(args[0].split("=")[1]);
        int metricsPort = Integer.parseInt(args[1].split("=")[1]);

        startMetricsServer(metricsPort);
        startDhingyServer(serverPort);
    }

    private static void validateArgs(String[] args) {
        if(args.length != 2
                && !args[0].split("=")[1].equals("--serverPort")
                && !args[1].split("=")[1].equals("--metricsPort")
        ) {
            throw new RuntimeException("Input arguments should be --serverPort=PORT --metricsPort=PORT_2");
        }
    }

    private static void startDhingyServer(int serverPort) throws IOException, InterruptedException {
        StateMachine<String, Integer> sm = new StateMachine<>();
        Daemon<String, Integer> daemon = new Daemon<>();
        daemon.start(sm, serverPort);
        daemon.blockUntilShutdown();
    }

    private static void startMetricsServer(int port) throws IOException {
        metricsServer = new HTTPServer(port);
    }
}
