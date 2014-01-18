package org.sodfs.storage.replication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicationRequestCollection {

    private HashMap<Integer, ArrayList<ReplicationRequest>> requests = new HashMap<Integer, ArrayList<ReplicationRequest>>();

    public ArrayList<ReplicationRequest> getRequestsList(int fileId) {
        return requests.get(fileId);
    }

    public Set<Integer> getRequestedFiles() {
        return requests.keySet();
    }

    private ArrayList<ReplicationRequest> getOrCreateRequestsList(int fileId) {
        ArrayList<ReplicationRequest> result;
        result = requests.get(fileId);
        if (result == null) {
            result = new ArrayList<ReplicationRequest>();
            requests.put(fileId, result);
        }
        return result;
    }

    public void addReplicationRequest(ReplicationRequest msg) {
        getOrCreateRequestsList(msg.getFileId()).add(msg);
    }
}
