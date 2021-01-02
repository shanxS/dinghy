package plane.control;

public interface PersonaType extends Runnable{
    RequestVoteOutput requestVote(RequestVoteInput request);
    AppendEntriesOutput appendEntries(AppendEntriesInput request);
}
