package plane.control;


import org.apache.commons.lang3.exception.ExceptionUtils;
import plane.control.utils.Timer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class Leader implements PersonaType {
    Logger logger = Logger.getLogger(Leader.class.getName());

    private static final String type = "Leader";

    private Timer timer;
    private PersonaManager persona;

    public Leader(PersonaManager p) {
        persona = p;
        timer = new Timer("LeaderTimer", 50, 100);
    }

    @Override
    public void run() {
        boolean stopBeingLeader = false;
        while(!stopBeingLeader) {
            try {
                timer.start();
                timer.get();
                stopBeingLeader = shouldStillLead(sendHeartBeat());
            } catch (Exception e) {
                logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(e));
            }
        }

        logger.log(Level.INFO, "lost election, stepping down");

        // TODO: if a prev leader is catching with system then
        // after heartbeat it'll know where system stands and probably
        // can figure current leader from heartbeat response instead of becoming
        // follower and passively waiting for heartbeat
        persona.updatePersona(persona.getFollower());
    }

    private boolean shouldStillLead(List<AppendEntriesOutput> heartbeatResponse) {
        int inFavor = 0;
        StringBuilder sb = new StringBuilder();
        for(AppendEntriesOutput v : heartbeatResponse) if(v.getSuccess()) {
            ++inFavor;
            sb.append(v.getNodeId() + " ");
        }

        logger.log(Level.INFO, "got " + inFavor + " votes, from " + sb.toString());

        return persona.getMajorityNumber() > inFavor;
    }

    private List<AppendEntriesOutput> sendHeartBeat() {
        AppendEntriesInput request = AppendEntriesInput.newBuilder()
                .setPrevLogIndex(persona.getState().getCurrentLeaderCommit())
                .setPrevLogTerm(persona.getState().getCurrentTerm())
                .setLeaderId(persona.getIdentity())
                .build();

        return persona.getClientHandler().callAppendEntries(request);
    }

    @Override
    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        return AppendEntriesOutput.newBuilder()
                .setTerm(persona.getState().getCurrentTerm())
                .setSuccess(false)
                .setNodeId(persona.getIdentity())
                .build();
    }

    @Override
    public RequestVoteOutput requestVote(RequestVoteInput request) {
        return RequestVoteOutput.newBuilder()
                .setTerm(persona.getState().getCurrentTerm())
                .setVoteGranted(false)
                .setVoterid(persona.getIdentity())
                .build();
    }

    public String getType() {
        return type;
    }
}
