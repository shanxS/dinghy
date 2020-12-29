package plane.control.types;

import lombok.Getter;
import plane.control.*;


final public class Leader extends Persona implements Runnable {

    private static final String type = "Leader";

    @Override
    public void run() {

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
