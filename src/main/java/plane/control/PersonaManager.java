package plane.control;

import org.apache.commons.lang3.NotImplementedException;
import plane.control.utils.ClientHandler;
import plane.control.utils.State;
import plane.data.DhingyDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.Level;


public class PersonaManager implements Runnable {
    Logger logger = Logger.getLogger(PersonaManager.class.getName());

    private ExecutorService executor;
    private PersonaType currentPersona, candidate, follower, leader;
    private String identity;
    private List<Integer> otherNodes;
    private ClientHandler clientHandler;
    private State state;
    private DhingyDao dao;

    private PersonaManager(String id, List<Integer> nodes) {
        executor = Executors.newSingleThreadExecutor();
        follower = new Follower(this);
        candidate = new Candidate(this);
        leader = new Leader(this);
        identity = id;
        otherNodes = nodes;
        clientHandler = new ClientHandler(nodes);
        state = new State();
        dao = new DhingyDao();
    }

    final public void stop() {
        if(executor != null) executor.shutdown();
    }


    protected ExecutorService getExecutor() {
        return executor;
    }

    PersonaType getCandidate() { return candidate; }
    PersonaType getFollower() { return follower; }
    PersonaType getLeader() { return leader; }
    ClientHandler getClientHandler() { return clientHandler; }
    State getState() { return state; }
    DhingyDao getDao() { return dao; }

    public void init() {
        updatePersona(follower);
    }

    protected void updatePersona(PersonaType newPersona) {
        logger.log(Level.INFO, "updating persona to " + " " + newPersona.getType());
        currentPersona = newPersona;
        executor.submit(currentPersona);
    }

    protected String getIdentity() { return identity; }

    protected int getMajorityNumber() {
        // TODO: this should depend on number of nodes running
        return 2;
    }

    @Override
    public void run() {
        throw new NotImplementedException();
    }

    public AppendEntriesOutput appendEntries(AppendEntriesInput request) {
        return currentPersona.appendEntries(request);
    }

    public RequestVoteOutput requestVote(RequestVoteInput request) {
        return currentPersona.requestVote(request);
    }

    public static class Builder {
        private String id;
        private List<Integer> otherNodes;

        public Builder withOtherNodes(List<Integer> list) {
            this.otherNodes = list;
            return this;
        }

        public Builder  withIdentity(String id) {
            this.id = id;
            return this;
        }

        public PersonaManager build() {
            PersonaManager p = new PersonaManager(id, otherNodes);
            return p;
        }
    }
}
