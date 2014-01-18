package org.sodfs.storage.replication;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicationRequest extends ReplicationMessage {
    private float evaluation;

    public ReplicationRequest(int messageOrgin, int messageDestignation, int fileId, 
      float evaluation) {     
        super(messageOrgin, messageDestignation, fileId);
        this.evaluation = evaluation;
    }

    public float getEvaluation() {
        return evaluation;
    }
   
}
