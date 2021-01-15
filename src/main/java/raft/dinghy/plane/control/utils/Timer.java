package raft.dinghy.plane.control.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.*;
import org.apache.logging.log4j.Level;

public class Timer implements AutoCloseable {
    private Logger logger = LogManager.getLogger(raft.dinghy.plane.control.utils.Timer.class.getName());

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
            logger.log(Level.ERROR, name + "timer task is null, wtf?");
            throw new RuntimeException(name + "timer task is null, wtf?");
        }
        task.cancel(true);
    }

    private void stop() {
        if(task != null) task.cancel(true);
        ControlUtils.stopExecutor(raft.dinghy.plane.control.utils.Timer.class.getName(), scheduler);
    }

    // there is no need to return a value since this is just
    // a timer and not computing anything.
    public void get() throws ExecutionException, InterruptedException {
        logger.log(Level.INFO, name + "get called by thread " + Thread.currentThread().getId());
        if(task == null) {
            logger.log(Level.ERROR, name + "timer task null, when get() was called, wtf?");
            throw new RuntimeException(name + "timer task is null, when get() was called, wtf?");
        }
        task.get();
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
