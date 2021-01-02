package plane.control.utils;

import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Timer {
    Logger logger = Logger.getLogger(plane.control.utils.Timer.class.getName());

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random random = new Random();
    private final String name;
    private final int min, max;
    private Future<Boolean> task;

    public Timer(String n, int min, int max) {
        name = "[" + n + "] ";
        this.min = min;
        this.max = max;
    }

    public void start() {
        logger.log(Level.INFO, name + "started by thread " + Thread.currentThread().getId());
        task = scheduler.schedule(() -> {
            return true;
        }, random.ints(min, max).findFirst().getAsInt(), TimeUnit.MILLISECONDS);
    }

    public void cancel() {
        logger.log(Level.INFO, name + "cancelled by thread " + Thread.currentThread().getId());
        if(task == null) {
            logger.log(Level.SEVERE, name + "timer task is null, wtf?");
            throw new RuntimeException(name + "timer task is null, wtf?");
        }
        task.cancel(true);
    }

    // there is no need to return a value since this is just
    // a timer and not computing anything.
    public void get() throws ExecutionException, InterruptedException {
        logger.log(Level.INFO, name + "get called by thread " + Thread.currentThread().getId());
        if(task == null) {
            logger.log(Level.SEVERE, name + "timer task null, when get() was called, wtf?");
            throw new RuntimeException(name + "timer task is null, when get() was called, wtf?");
        }
        task.get();
    }
}
