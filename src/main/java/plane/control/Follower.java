package plane.control;

import java.util.logging.Level;
import java.util.logging.Logger;


final public class Follower implements PersonaType {
    Logger logger = Logger.getLogger(Follower.class.getName());
    private static final String type = "Follower";

    private PersonaManager persona;

    public Follower(PersonaManager p) {
        persona = p;
    }

    @Override
    public void run() {

        boolean didTimeout = false;
//        while(!didTimeout) {
//
//
//            didTimeout = true; //super.getState().getLastHeartBeat() + TIMEOUT < System.currentTimeMillis();
//        }

        // become candidate and exit
        logger.log(Level.INFO, "trying to be candidate");
        if(persona.getCandidate() == null) logger.log(Level.INFO, "candidate is null");
        else logger.log(Level.INFO, "candidate is NOT null");
        persona.updatePersona(persona.getCandidate());
    }

    @Override
    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        logger.log(Level.INFO, "[AppendEntry] " + request.toString());
        AppendEntriesOutput res = null;
        if(persona.getState().isValidRequest(request)) {
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
            return RequestVoteOutput.newBuilder()
                    .setVoteGranted(true)
                    .setTerm(persona.getState().getCurrentTerm())
                    .build();
        }

        return RequestVoteOutput.newBuilder()
                .setVoteGranted(false)
                .setTerm(persona.getState().getCurrentTerm())
                .build();
    }

    protected String getType() {
        return type;
    }

}
