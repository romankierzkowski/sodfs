package org.sodfs.storage.replication;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public abstract class ReplicationMessage implements Serializable{
    private int messageOrgin;
    private int messageDestignation;
    private int fileId;

    public ReplicationMessage(int messageOrgin, int messageDestignation, int fileId) {
        this.messageOrgin = messageOrgin;
        this.messageDestignation = messageDestignation;
        this.fileId = fileId;
    }

    public int getMessageOrgin() {
        return messageOrgin;
    }

    public void setMessageOrgin(int messageOrgin) {
        this.messageOrgin = messageOrgin;
    }

    public int getMessageDestignation() {
        return messageDestignation;
    }

    public void setMessageDestignation(int messageDestignation) {
        this.messageDestignation = messageDestignation;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    
}
