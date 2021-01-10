package raft.dinghy;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        List<Integer> list = Arrays.asList(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        DhingyNode node = new DhingyNode(args[0], list);
        node.start();
        node.blockUntilShutdown(); // become daemon
    }
}