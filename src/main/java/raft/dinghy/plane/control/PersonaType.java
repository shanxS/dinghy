package raft.dinghy.plane.control;

public abstract class PersonaType implements Runnable {
    public enum Type {
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


    protected volatile boolean shutDown = false;
    protected abstract RequestVoteOutput requestVote(RequestVoteInput request);
    protected abstract AppendEntriesOutput appendEntries(AppendEntriesInput request);
    protected abstract Type getType();

    public void stop() {
        // atomic write
        shutDown = true;
    }
}
