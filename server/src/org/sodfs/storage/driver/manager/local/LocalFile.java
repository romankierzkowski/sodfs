package org.sodfs.storage.driver.manager.local;

import org.sodfs.storage.driver.manager.local.updater.UpdateWriter;
import org.sodfs.storage.driver.manager.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.util.Arrays;
import org.alfresco.jlan.smb.SeekType;

/**
 *
 * @author Roman Kierzkowski
 */
public class LocalFile implements MovableFileInterface {
    private String path;   
    private FileChannel fc;
    private UpdateWriter uw;
    private int storageId;

    public LocalFile(String path, UpdateWriter uw, int storageId) {
        this.path = path;
        this.uw = uw;
        this.storageId = storageId;
    }
    
    public String getPath() {
        return path;
    }
    
    public void openFile(boolean createFlag) throws RemoteException, IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "r");
        fc = raf.getChannel();        
    }

    public byte[] readFile(int len, long off) throws RemoteException, IOException {        
        ByteBuffer buff = ByteBuffer.allocate(len);        
        int result = fc.read(buff, off);
        byte[] resultBuff = buff.array();
        if (result == -1) {
            resultBuff = new byte[0];
        } else if (result < len) {
            resultBuff = Arrays.copyOf(resultBuff, result);
        } 
        return resultBuff;
    }

    public int writeFile(byte[] buf, long off) throws RemoteException, IOException {
        return uw.write(buf,off);
    }

    public long seekFile(long off, int typ) throws RemoteException, IOException {
        long absoluteOffset = 0;
        switch (typ) {
            case SeekType.EndOfFile:
                absoluteOffset = fc.size();
            break;
            case SeekType.CurrentPos:
                absoluteOffset = fc.position();
            break;
        }
        absoluteOffset += off;        
        FileChannel result = fc.position(absoluteOffset);
        return result.position();
    }

    public void flushFile() throws RemoteException, IOException {
        uw.flush();
    }

    public void truncateFile(long size) throws RemoteException, IOException {
        uw.truncate(size);
    }

    public void closeFile() throws RemoteException, IOException {
        fc.close();
    }

    public int getStorageId() {
        return storageId;
    }
}
