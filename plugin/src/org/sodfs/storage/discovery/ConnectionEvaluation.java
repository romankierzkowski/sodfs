package org.sodfs.storage.discovery;

/**
 *
 * @author Roman Kierzkowski
 */
public class ConnectionEvaluation implements Comparable {
    private int storageId;
    private long roundTripTime;

    public ConnectionEvaluation(int storageId, long roundTripTime) {
        this.storageId = storageId;
        this.roundTripTime = roundTripTime;
    }

    public long getRoundTripTime() {
        return roundTripTime;
    }

    public int getStorageId() {
        return storageId;
    }

    public int compareTo(Object o) {
        ConnectionEvaluation c = (ConnectionEvaluation) o;
        int result = -1;
        if (getRoundTripTime() > c.getRoundTripTime()) {
            result = 1;
        } else {
            if (getRoundTripTime() == c.getRoundTripTime()) result = 0;
        }
        return result;
    }        

    @Override
    public boolean equals(Object obj) {
        return getRoundTripTime() == ((ConnectionEvaluation) obj).getRoundTripTime();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.getRoundTripTime() ^ (this.getRoundTripTime() >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(storageId);
        sb.append("=>");
        sb.append(roundTripTime);
        sb.append(")");
        return sb.toString();
    }
    
    
}
