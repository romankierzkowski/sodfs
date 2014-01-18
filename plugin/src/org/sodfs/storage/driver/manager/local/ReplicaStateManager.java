package org.sodfs.storage.driver.manager.local;

import java.util.LinkedList;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicaStateManager {
    
    public static final int NOT_APPLICABLE_PARAMETER = -1;
    
    private int fileId;
    private ReplicaState state;
    private int destinationStorageId;
    
    private LinkedList<ReplicaStateChangeListener> listeners = new LinkedList<ReplicaStateChangeListener>();
    

    public ReplicaStateManager(int fileId, ReplicaState state) {
        this.fileId = fileId;
        this.state = state;
    }

    public ReplicaStateManager(int fileId, ReplicaState state, int destinationStorageId) {
        this.fileId = fileId;
        this.state = state;
        this.destinationStorageId = destinationStorageId;
    }
    
    /* GETTERS */
    
    synchronized public ReplicaState getState() {
        return state;
    }
    
    synchronized public int getDestinationStorageId() {
        if (state == ReplicaState.MOVED) return destinationStorageId;
        else return NOT_APPLICABLE_PARAMETER;
    }
    
    public int getFileId() {
        return fileId;
    }
    
    /* LISTENERS */
    
    synchronized public void addStateChangeListener(ReplicaStateChangeListener listener) {
        listeners.add(listener);
    }
    
    synchronized public void removeStateChangeListener(ReplicaStateChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(ReplicaState state, int destinationStorageId) {
        for (ReplicaStateChangeListener l : listeners) {
            l.replicaStateChanged(state, destinationStorageId);
        }
    }
    
    /* REPORTERS */
    
    synchronized public void reportFailure() {
        switch (state) {
            case CONSISTENT: case INCONSISTENT: 
                state = ReplicaState.FAULTY;               
                notifyListeners(state, NOT_APPLICABLE_PARAMETER);
            break;
        }
    }
    
    synchronized public void reportInconsistance() {
        switch (state) {
            case CONSISTENT: 
                state = ReplicaState.INCONSISTENT; 
                notifyListeners(state, NOT_APPLICABLE_PARAMETER);
            break;
        } 
    }
    
    synchronized public void reportConsistance() {
        switch (state) {
            case INCONSISTENT: 
                state = ReplicaState.CONSISTENT; 
                notifyListeners(state, NOT_APPLICABLE_PARAMETER);
            break;
        }        
    }
    
    synchronized public void reportMove(int destinationStorageId) {
        switch (state) {
            case CONSISTENT: case INCONSISTENT: 
                state = ReplicaState.MOVED; 
                this.destinationStorageId = destinationStorageId;
                notifyListeners(state, destinationStorageId);
            break;
        }
    }
    
    synchronized public void reportDereplication() {
        switch (state) {
            case CONSISTENT: case INCONSISTENT: 
                state = ReplicaState.DEREPLICATED; 
                notifyListeners(state, NOT_APPLICABLE_PARAMETER);
            break;
        }
    }
    
    synchronized public void reportRemoval() {
        switch (state) {
            case CONSISTENT: case INCONSISTENT:            
            case FAULTY:               
                state = ReplicaState.NOT_EXISTING; 
                notifyListeners(state, NOT_APPLICABLE_PARAMETER);
            break;
        }
    }
}
