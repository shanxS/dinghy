package plane.control;


final public class Leader implements PersonaType {

    private static final String type = "Leader";
    private PersonaManager persona;

    public Leader(PersonaManager p) {
        persona = p;
    }

    @Override
    public void run() {

    }

    @Override
    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        throw new RuntimeException("Leader appendEntries not impl");
    }

    @Override
    public RequestVoteOutput requestVote(RequestVoteInput request) {
        throw new RuntimeException("Leader requestVote not impl");
    }

    public String getType() {
        return type;
    }
}
