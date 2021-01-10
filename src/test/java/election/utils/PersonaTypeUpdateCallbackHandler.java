package election.utils;

import raft.dinghy.plane.control.PersonaType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PersonaTypeUpdateCallbackHandler implements Consumer<PersonaType.Type> {
    // using volatile to sync owner thread and callback thread
    // https://docs.oracle.com/javase/tutorial/essential/concurrency/atomic.html
    private volatile boolean stopUpdate = false;

    private List<PersonaType.Type> typeHistory = new ArrayList<>();

    @Override
    public void accept(PersonaType.Type type) {
        if(!stopUpdate) { // read atomically
            typeHistory.add(type);
        }
    }

    public void setStopUpdate() {
        stopUpdate = true; // set atomically
    }

    public List<PersonaType.Type> getTypeHistory() {
        if(!stopUpdate) throw new RuntimeException("You need to stop the update before getting the history");

        // Note that even if stopUpdate is true, callback thread could still be in middle of writing last
        // entry. Assumption is that this will not be of consequence since we are dealing with history
        // and looking for patterns in history. If this fails to be true, change the list to be blocking list.
        return typeHistory;
    }
}
