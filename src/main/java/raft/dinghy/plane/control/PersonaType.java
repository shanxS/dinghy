package raft.dinghy.plane.control;

public interface PersonaType extends Runnable{
    enum Type {
        CANDIDATE("Candidate"),
        FOLLOWER("Follower"),
        LEADER("Leader")
        ;

        private final String name;
        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    };


    RequestVoteOutput requestVote(RequestVoteInput request);
    AppendEntriesOutput appendEntries(AppendEntriesInput request);

    Type getType();
}
