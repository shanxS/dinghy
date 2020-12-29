package plane.control.types;

import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;
import plane.control.*;

final public class Candidate extends Persona implements Runnable {

    private static final String type = "Candidate";

    @Override
    public void run() {
        super.getVoteMachine().vote(super.getIdentity());
        int count = requestVotes();
        if(count > super.getMajorityNumber()) super.updatePersona(super.getLeader());
        else super.updatePersona(super.getFollower());
    }

    private int requestVotes() {
        throw new NotImplementedException();
    }

    @Override
    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        return super.appendEntries(request);
    }

    @Override
    public RequestVoteOutput requestVote(RequestVoteInput request) {
        return super.requestVote(request);
    }

    @Override
    protected String getType() {
        return type;
    }
}
