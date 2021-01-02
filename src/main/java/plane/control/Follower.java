package plane.control;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;


final public class Follower implements PersonaType {
    Logger logger = Logger.getLogger(Follower.class.getName());
    private static final String type = "Follower";

    private PersonaManager persona;
    Timer timer;

    public Follower(PersonaManager p) {
        persona = p;
        timer = new Timer();
    }

    @Override
    public void run() {

        boolean didTimeout = false;
        while(!didTimeout) {

            didTimeout = true;
            timer.start();

            try {
                timer.get();
            } catch (CancellationException e) {
                didTimeout = false;
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(e));
            }
        }

        // become candidate and exit
        logger.log(Level.INFO, "trying to be candidate");
        persona.updatePersona(persona.getCandidate());
    }

    @Override
    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        logger.log(Level.INFO, "[AppendEntry] " + request.toString());
        AppendEntriesOutput res = null;
        if(persona.getState().isValidRequest(request)) {
            timer.cancel();
            persona.getDao().put(request.getKey(), request.getValue());
            persona.getState().updateState(request);
            res = AppendEntriesOutput.newBuilder()
                    .setTerm(persona.getState().getCurrentTerm())
                    .setSuccess(true)
                    .build();
        } else {
            res = AppendEntriesOutput.newBuilder()
                    .setTerm(persona.getState().getCurrentTerm())
                    .setSuccess(false)
                    .build();
        }

        return res;
    }

    @Override
    public synchronized RequestVoteOutput requestVote(RequestVoteInput request) {
        if(persona.getState().isValidRequest(request)) {
            timer.cancel();
            return RequestVoteOutput.newBuilder()
                    .setVoteGranted(true)
                    .setTerm(persona.getState().getCurrentTerm())
                    .setVoterid(persona.getIdentity())
                    .build();
        }

        return RequestVoteOutput.newBuilder()
                .setVoteGranted(false)
                .setTerm(persona.getState().getCurrentTerm())
                .setVoterid(persona.getIdentity())
                .build();
    }

    public String getType() {
        return type;
    }

}

class Timer {
    Logger logger = Logger.getLogger(Timer.class.getName());

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random random = new Random();
    private Future<Boolean> task;

    void start() {
        logger.log(Level.INFO, "started by thread " + Thread.currentThread().getId());
        task = scheduler.schedule(() -> {
            return true;
        }, random.ints(150, 300).findFirst().getAsInt(), TimeUnit.MILLISECONDS);
    }

    void cancel() {
        logger.log(Level.INFO, "cancelled by thread " + Thread.currentThread().getId());
        if(task == null) {
            logger.log(Level.SEVERE, "timer task is null, wtf?");
            throw new RuntimeException("timer task is null, wtf?");
        }
        task.cancel(true);
    }

    // there is no need to return a value since this is just
    // a timer and not computing anything.
    public void get() throws ExecutionException, InterruptedException {
        logger.log(Level.INFO, "get called by thread " + Thread.currentThread().getId());
        if(task == null) {
            logger.log(Level.SEVERE, "timer task null, when get() was called, wtf?");
            throw new RuntimeException("timer task is null, when get() was called, wtf?");
        }
        task.get();
    }
}