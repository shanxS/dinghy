package raft.dinghy.plane.control;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import org.apache.logging.log4j.Level;

final public class Candidate extends PersonaType {

    private final Logger logger;
    private static final Type type = Type.CANDIDATE;

    private PersonaManager persona;

    public Candidate(PersonaManager p) {
        logger = LogManager.getLogger(Candidate.class.getName() + ":" + p.getIdentity());
        persona = p;
    }

    // no explicit attempt to shutdown since this does not run in loop
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

            logger.log(Level.INFO, "requesting vote " + request);
            if (didWin(persona.getClientHandler().callRequestVote(request))) {
                persona.getState().updateState(request);
                logger.log(Level.INFO, "won election");
                persona.updatePersona(persona.getLeader());
            } else {
                logger.log(Level.INFO, "lost election, back to being follower");
                persona.updatePersona(persona.getFollower());
            }

        } catch (Exception e) {
            logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
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
                .setNodeId(persona.getIdentity())
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

    public Type getType() {
        return type;
    }
}
