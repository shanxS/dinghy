package raft.dinghy.plane.control.utils;

import raft.dinghy.plane.control.AppendEntriesInput;
import raft.dinghy.plane.control.RequestVoteInput;

import java.util.ArrayList;
import java.util.List;

public class State {
    private int term;
    private String leader;
    private int leaderCommit;
    private List<State> history;

    public State() {
        term = -1;
        leader = null;
        leaderCommit = 0;
        history = new ArrayList<>();
    }

    private State(Integer t, String l, int s) {
        term = t;
        leader = l;
        leaderCommit = s;
        history = null;
    }

    public Integer getCurrentTerm() { return term; }
    public Integer getCurrentLeaderCommit() { return leaderCommit; }
    public List<State> getHistory() { return history; }

    public synchronized void updateState(AppendEntriesInput request) {
        history.add(new State(term, leader, leaderCommit));
        term = request.getTerm();
        leader = request.getLeaderId();
        leaderCommit = request.getLeaderCommit();
    }

    public synchronized void updateState(RequestVoteInput request) {
        history.add(new State(term, leader, leaderCommit));
        term = request.getTerm();
        leader = request.getCandidateId();
        leaderCommit = 0;
    }

    public boolean isValidRequest(AppendEntriesInput request) {
        // does not cover all cases as described in https://raft.github.io/raft.pdf
        if(leader == null) return true;
        else if(term > request.getTerm()) return false;
        else if(term < request.getTerm()) return true;
        else if(term == request.getPrevLogTerm()) return leaderCommit == request.getPrevLogIndex();
        else return false;
    }

    public boolean isValidRequest(RequestVoteInput request) {
        return leader == null ||
                (term <= request.getLastLogTerm()
                && leaderCommit <= request.getLastLogIndex()
                && request.getLastLogTerm() < request.getTerm());
    }

    @Override
    public String toString() {
        return "term: " + term + " leader:" + leader + " leaderCommit:" + leaderCommit;
    }
}
