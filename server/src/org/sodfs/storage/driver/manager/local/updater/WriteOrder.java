package org.sodfs.storage.driver.manager.local.updater;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public class WriteOrder implements Serializable {
    private byte[] buffor;
    private long fileOffset;

    public WriteOrder(byte[] buffor, long fileOffset) {
        this.buffor = buffor;
        this.fileOffset = fileOffset;
    }
    
    public byte[] getBuffor() {
        return buffor;
    }

    public void setBuffor(byte[] buffor) {
        this.buffor = buffor;
    }

    public long getFileOffset() {
        return fileOffset;
    }

    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }    
}
