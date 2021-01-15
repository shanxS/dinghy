package raft.dinghy.plane.control;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import raft.dinghy.plane.control.utils.Timer;

import java.util.List;
import org.apache.logging.log4j.Level;

final public class Leader extends PersonaType {
    private final Logger logger;

    private static final Type type = Type.LEADER;

    private PersonaManager persona;

    public Leader(PersonaManager p) {
        logger = LogManager.getLogger(Leader.class.getName() + ":" + p.getIdentity());
        persona = p;
    }

    @Override
    public void run() {
        try (Timer timer = new Timer("LeaderTimer", 50, 100)) {
            boolean stopBeingLeader = false;
            while (!stopBeingLeader && !shutDown) {
                try {
                    timer.start();
                    timer.get();
                    stopBeingLeader = shouldStillLead(sendHeartBeat());
                } catch (Exception e) {
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

    public Type getType() {
        return type;
    }
}
