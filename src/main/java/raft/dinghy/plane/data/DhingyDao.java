package raft.dinghy.plane.data;

import java.util.HashMap;

public class DhingyDao {
    private HashMap<String, String> data;

    public DhingyDao() {
        data = new HashMap<>();
    }

    public void put(String k, String v) {
        data.put(k,v);
    }

    public String get(String k) {
        return data.get(k);
    }
}
