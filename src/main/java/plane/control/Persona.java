package plane.control;

import lombok.extern.log4j.Log4j;
import plane.control.types.Candidate;
import plane.control.types.Follower;
import plane.control.types.Leader;
import org.apache.commons.lang3.NotImplementedException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.Level;


public class Persona implements Runnable {
    Logger logger = Logger.getLogger(Persona.class.getName());

    protected ExecutorService executor;
    protected Persona candidate, follower, leader, currentPersona;
    protected VoteMachine voteMachine;
    protected int identity;
    protected List<Integer> otherNodes;

    protected Persona() {};
    protected Persona(int id, List<Integer> nodes) {
        executor = Executors.newSingleThreadExecutor();
        follower = new Follower();
        candidate = new Candidate();
        leader = new Leader();
        voteMachine = new VoteMachine();
        identity = id;
        otherNodes = nodes;
    }

    final public void stop() {
        if(executor != null) executor.shutdown();
    }

    protected VoteMachine getVoteMachine() {
        return voteMachine;
    }

    protected ExecutorService getExecutor() {
        return executor;
    }

    protected Persona getCandidate() {
        return candidate;
    }

    protected Persona getFollower() {
        return follower;
    }

    protected Persona getLeader() {
        return leader;
    }

    protected String getType() {
        throw new NotImplementedException();
    }

    public void init() {
        updatePersona(follower);
    }

    protected void updatePersona(Persona newPersona) {
        logger.log(Level.INFO, "updating persona to " + newPersona.getType());
        currentPersona = newPersona;
        executor.submit(currentPersona);
    }

    protected int getIdentity() {
        return identity;
    }

    protected int getMajorityNumber() {
        return otherNodes.size()/2;
    }

    @Override
    public void run() {
        throw new NotImplementedException();
    }

    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        return currentPersona.appendEntries(request);
    }

    public RequestVoteOutput requestVote(RequestVoteInput request) {
        return currentPersona.requestVote(request);
    }

    public static class Builder {
        private int id;
        private List<Integer> otherNodes;

        public Builder withOtherNodes(List<Integer> list) {
            this.otherNodes = list;
            return this;
        }

        public Builder  withIdentity(int id) {
            this.id = id;
            return this;
        }

        public Persona build() {
            Persona p = new Persona(id, otherNodes);
            return p;
        }
    }
}
