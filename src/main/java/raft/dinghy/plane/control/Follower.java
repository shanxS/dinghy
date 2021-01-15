package raft.dinghy.plane.control;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import raft.dinghy.plane.control.utils.Timer;

import java.util.concurrent.*;
import org.apache.logging.log4j.Level;


final public class Follower extends PersonaType {
    private final Logger logger;
    private static final Type type = Type.FOLLOWER;

    private PersonaManager persona;
    Timer timer;

    public Follower(PersonaManager p) {
        logger = LogManager.getLogger(Follower.class.getName() + ":" + p.getIdentity());
        persona = p;
    }

    @Override
    public void run() {

        try (Timer timer = new Timer("FollowerTimer", 500, 1000)){

            // TODO: ugh, is there a better way?
            this.timer = timer;

            boolean didTimeout = false;
            while(!didTimeout && !shutDown) {

                didTimeout = true;
                timer.start();

                try {
                    timer.get();
                } catch (CancellationException e) {
                    didTimeout = false;
                } catch (InterruptedException | ExecutionException e) {
                    logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
                }
            }

        } catch (Exception e) {
            logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
        }

        if(shutDown) {
            logger.log(Level.INFO, "Shutting down");
            return;
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
            if(timer == null) {
                logger.log(Level.ERROR, "[AppendEntry] why the f is timer null?");
                throw new RuntimeException("[AppendEntry] why the f is timer null?");
            }
            timer.cancel();

            // payload will be null when leader is sending heartbeat
            if(request.getKey() != null) persona.getDao().put(request.getKey(), request.getValue());

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

    public Type getType() {
        return type;
    }

}