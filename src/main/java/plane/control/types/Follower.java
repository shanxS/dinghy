package plane.control.types;

import lombok.Getter;
import plane.control.*;


final public class Follower extends Persona implements Runnable {

    private static final String type = "Follower";

    @Override
    public void run() {

        boolean didTimeout = true;
        while(didTimeout) {
            //
            // run timeout
            //
            didTimeout = false;
        }

        // become candidate and exit
        super.updatePersona(getCandidate());
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
