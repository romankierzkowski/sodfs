package org.sodfs.storage.replication;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicationOrder extends ReplicationMessage {
    
    private boolean move;
    
    public ReplicationOrder(int messageOrgin, int messageDestignation, int fileId, boolean move) {
        super(messageOrgin, messageDestignation, fileId);
        this.move = move;
    }

    public boolean isMove() {
        return move;
    }    
    
}
