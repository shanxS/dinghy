package plane.control;

interface PersonaType extends Runnable{
    RequestVoteOutput requestVote(RequestVoteInput request);
    AppendEntriesOutput appendEntries(AppendEntriesInput request);

    String getType();
}
