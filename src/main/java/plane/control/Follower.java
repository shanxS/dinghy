package plane.control;

import org.apache.commons.lang3.exception.ExceptionUtils;
import plane.control.utils.Timer;

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
        timer = new Timer("FollowerTimer", 500, 1000);
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

    public String getType() {
        return type;
    }

}