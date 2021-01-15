package raft.dinghy.plane.control;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import raft.dinghy.plane.control.utils.ClientHandler;
import raft.dinghy.plane.control.utils.ControlUtils;
import raft.dinghy.plane.control.utils.State;
import raft.dinghy.plane.data.DhingyDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.apache.logging.log4j.Level;


final public class PersonaManager implements Runnable {
    private Logger logger = LogManager.getLogger(PersonaManager.class.getName());

    private final ExecutorService executor;
    private final PersonaType candidate, follower, leader;
    private final String identity;
    private final List<Integer> otherNodes;
    private final ClientHandler clientHandler;
    private final State state;
    private final DhingyDao dao;
    private PersonaType currentPersona;
    private Consumer<PersonaType.Type> personaUpdateCallback; // used for testing

    private PersonaManager(String id, List<Integer> nodes) {
        executor = Executors.newSingleThreadExecutor();
        identity = id;
        otherNodes = nodes;
        clientHandler = new ClientHandler(nodes);
        state = new State();
        dao = new DhingyDao();
        follower = new Follower(this);
        candidate = new Candidate(this);
        leader = new Leader(this);
    }

    public void stop() {
        currentPersona.stop();
        ControlUtils.stopExecutor(PersonaManager.class.getName(), executor);
    }

    public void start() {
        updatePersona(follower);
    }

    PersonaType getCandidate() { return candidate; }
    PersonaType getFollower() { return follower; }
    PersonaType getLeader() { return leader; }
    ClientHandler getClientHandler() { return clientHandler; }
    State getState() { return state; }
    DhingyDao getDao() { return dao; }
    String getIdentity() { return identity; }

    void updatePersona(PersonaType newPersona) {
        logger.log(Level.INFO, "updating persona to " + " " + newPersona.getType());
        if(personaUpdateCallback != null) personaUpdateCallback.accept(newPersona.getType());
        currentPersona = newPersona;
        executor.submit(currentPersona);
    }

    int getMajorityNumber() {
        // TODO: this should depend on number of nodes running
        return 2;
    }

    public PersonaType.Type getType() { return currentPersona.getType(); }

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

    public void setPersonaUpdateCallback(Consumer<PersonaType.Type> c) {
        personaUpdateCallback = c;
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
