package raft.dinghy.plane.control.utils;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import raft.dinghy.plane.control.*;
import raft.dinghy.plane.control.DhingyInternalGrpc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {
    Logger logger = Logger.getLogger(Follower.class.getName());
    private HashMap<Integer, DhingyInternalBlockingStub> connections;

    public ClientHandler(List<Integer> otherNodes) {
        connections = new HashMap<>();
        for(int i:otherNodes) {
            Channel channel = ManagedChannelBuilder.forAddress("localhost", i).usePlaintext().build();
            connections.put(i, DhingyInternalGrpc.newBlockingStub(channel));
        }
    }

    public List<AppendEntriesOutput> callAppendEntries(AppendEntriesInput req) {
        List<AppendEntriesOutput> res = new ArrayList<>();
        for(DhingyInternalBlockingStub stub : connections.values()) {
            // TODO: this assumes that all nodes are at best behaviour
            // that will not be the case
            res.add(stub.appendEntries(req));
        }

        return res;
    }

    public List<RequestVoteOutput> callRequestVote(RequestVoteInput req) {
        List<RequestVoteOutput> res = new ArrayList<>();
        for(DhingyInternalBlockingStub stub : connections.values()) {
            // TODO: this assumes that all nodes are at best behaviour
            // that will not be the case
            try {
                res.add(stub.requestVote(req));
            } catch (Exception e) {
                logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(e));
            }
        }

        return res;
    }
}
