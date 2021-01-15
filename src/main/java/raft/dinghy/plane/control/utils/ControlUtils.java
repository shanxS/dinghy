package raft.dinghy.plane.control.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import raft.dinghy.plane.control.PersonaManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


public class ControlUtils {
    private static Logger logger = LogManager.getLogger(ControlUtils.class.getName());

    public static void stopExecutor(String name, ExecutorService exec) {
        if(exec == null) return;

        try {
            exec.shutdown();
            // Wait a while for existing tasks to terminate
            if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
                exec.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
                    logger.log(Level.ERROR, name + ": unable to shutdown executorService");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            exec.shutdownNow();
            // Preserve interrupt status
            // https://stackoverflow.com/a/36426266
            Thread.currentThread().interrupt();
        }
    }
}
