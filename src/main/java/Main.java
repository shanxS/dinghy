import plane.control.Daemon;
import plane.control.StateMachine;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        StateMachine<String, Integer> sm = new StateMachine<>();
//        Daemon<String, Integer> daemon = new Daemon<>(sm);
//        Thread thread = new Thread(daemon);
//        thread.start();
//
//        // TODO: add a shutdown listner
//        thread.join();
    }
}
