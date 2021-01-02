package plane.control;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class Candidate implements PersonaType {

    Logger logger = Logger.getLogger(Candidate.class.getName());
    private static final String type = "Candidate";

    private PersonaManager persona;

    public Candidate(PersonaManager p) {
        persona = p;
    }

    @Override
    public void run() {

        logger.log(Level.INFO, "starting candidate");

        try {
            RequestVoteInput request = RequestVoteInput.newBuilder()
                .setCandidateId(persona.getIdentity())
                .setLastLogIndex(persona.getState().getCurrentLeaderCommit())
                .setLastLogTerm(persona.getState().getCurrentTerm())
                .setTerm(persona.getState().getCurrentTerm() + 1)
                .build();

            persona.getState().updateState(request);

            logger.log(Level.INFO, "requesting vote " + request);
            if (didWin(persona.getClientHandler().callRequestVote(request))) {
                logger.log(Level.INFO, "won election");
                persona.updatePersona(persona.getLeader());
            } else {
                logger.log(Level.INFO, "lost election, back to being follower");
                persona.updatePersona(persona.getFollower());
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(e));
        }

    }

    private boolean didWin(List<RequestVoteOutput> votes) {
        int inFavor = 0;
        StringBuilder sb = new StringBuilder();
        for(RequestVoteOutput v : votes) if(v.getVoteGranted()) {
            ++inFavor;
            sb.append(v.getVoterid() + " ");
        }

        logger.log(Level.INFO, "got " + inFavor + " votes, from " + sb.toString());

        return persona.getMajorityNumber() <= inFavor;
    }

    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        // don't append any data while we voting in progress
        return AppendEntriesOutput.newBuilder()
                .setTerm(persona.getState().getCurrentTerm())
                .setSuccess(false)
                .build();
    }

    public RequestVoteOutput requestVote(RequestVoteInput request) {
        // don't vote for anyone if we are candidate
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
