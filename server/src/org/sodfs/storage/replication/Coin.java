package org.sodfs.storage.replication;

import java.io.Serializable;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class Coin implements Serializable {
    private int fileId;
    private int replicaOriginId;
    private int coinOriginId;
    private OperationType operationType;
    private long dataAmount;
    private long expirationTime;
    private int hopCounter = 0;
    private float dispersionFactor = 1;

    private transient Clock clock;
    
    public Coin(int fileId, int replicaOrginId, int coinOriginId, 
      OperationType operationType, long dataAmount, long TTL, Clock clock) {
        this.fileId = fileId;
        this.replicaOriginId = replicaOrginId;
        this.coinOriginId = coinOriginId;
        this.operationType = operationType;
        this.dataAmount = dataAmount;
        this.clock = clock;
        this.expirationTime = clock.getCurrentTime() + TTL;        
    }

    void incrementHopCounter() {
        hopCounter++;          
    }

    boolean isExpired() {
        return expirationTime < clock.getCurrentTime();
    }

    public int getFileId() {
        return fileId;
    }

    public int getReplicaOrginId() {
        return replicaOriginId;
    }

    public int getCoinOriginId() {
        return coinOriginId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public long getDataAmount() {
        return dataAmount;
    }

    public int getHopCounter() {
        return hopCounter;
    }

    public float getDispersionFactor() {
        return dispersionFactor;
    }
    
    public void dispearse(int neighboursSize) {
        dispersionFactor = dispersionFactor / (float) neighboursSize; 
    }

    @Override
    public String toString() {      
        return "(" + fileId + "," + coinOriginId + ")";
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }
    
    
}
