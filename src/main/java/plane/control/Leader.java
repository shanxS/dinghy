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
        return persona.appendEntries(request);
    }

    @Override
    public RequestVoteOutput requestVote(RequestVoteInput request) {
        return persona.requestVote(request);
    }

    protected String getType() {
        return type;
    }
}
