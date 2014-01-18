package org.sodfs.storage.driver;

import java.util.logging.Logger;
import org.sodfs.storage.driver.manager.ReplicaLocator;
import org.sodfs.storage.driver.manager.MovableFileInterface;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.logging.Level;
import org.alfresco.jlan.server.filesys.FileOfflineException;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.sodfs.utils.Clock;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.driver.manager.exceptions.NoReplicaAvilableException;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.FileEntity;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSNetworkFile extends NetworkFile {
    
    public static final int MAX_OPEN_FAILURE = 3;
    public static final int MAX_READ_FAILURE = 3;
    public static final int MAX_WRITE_FAILURE = 3;
    public static final int MAX_SEEK_FAILURE = 3;
    public static final int MAX_FLUSH_FAILURE = 3;
    public static final int MAX_TRUNCATE_FAILURE = 3;    
    
    public static final long RENEWAL_PERIOD = 15 * Clock.SECOND; //Clock.MINUTE;
    
    private MovableFileInterface currentReplica;
    private ReplicaLocator replicaLocator; 
    private int fileId;    
    private boolean createFlag = false;
    private long lastOperationTimestamp;
    
    private static Clock clock = Clock.getInstance();
    private static Logger logger = Logger.getLogger(SoDFSNetworkFile.class.getName());
    
    private int written;
    
    public SoDFSNetworkFile(int fileId, ReplicaLocator replicaLocator) {
        super("");
        this.fileId = fileId;
        this.replicaLocator = replicaLocator;        
    }
    
    public void setMetaData(FileEntity node, String path) {                 
        setFileId(node.getNodeId());        
        // name and path
        setName(node.getName());
        setFullName(path);

        // times
        setCreationDate(node.getCreated().getTime());
        setModifyDate(node.getModified().getTime());        

        // size
        setFileSize(node.getNominalSize());

        // attributes        
        setAttributes(node.getAttribute());
        
        // user and group
        setGrantedAccess(node.getMode());
    }
    
    

    @Override
    public void openFile(boolean createFlag) throws IOException {
        this.createFlag = createFlag;
        currentReplica = openNewReplica(createFlag);
        lastOperationTimestamp = clock.getCurrentTime();
    }

    @Override
    public int readFile(byte[] buf, int len, int pos, long fileOff) throws IOException {
        checkForBetterReplica();
        byte[] result = null;
        boolean failure = false;
        int failureCount = 0;
        do {
            if (failure) currentReplica = openNewReplica(createFlag);
            failure = false;
            try {
                result = currentReplica.readFile(len, fileOff);
            } catch (MovableFileException ex) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " state exception during read.", ex);
                failure = true;
            } catch (RemoteException re) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " comunication exception during read.", re);
                failure = true;
            } catch (IOException ioe) {
                try {
                    logger.log(Level.FINEST, "The replica of file " + fileId + " input/output exception during read.", ioe);
                    currentReplica.closeFile(); 
                } catch (Exception cioe) {
                    logger.log(Level.FINEST, "The replica of file " + fileId + " close exception during read.", cioe);
                }                              
                failure = true;                
            } 
            if (failure) failureCount++;            
        } while (failure && failureCount < MAX_READ_FAILURE);
        if (failureCount == MAX_READ_FAILURE) throw new IOException("Fail to read file. Tried " + failureCount + " times.");
        for (int i = 0; i < result.length; i++) {
            buf[pos + i] = result[i];
        }        
        return result.length;
    }

    @Override
    public void writeFile(byte[] buf, int len, int pos, long fileOff) throws IOException {
        checkForBetterReplica();
        boolean failure = false;
        int failureCount = 0;
        do {
            if (failure) currentReplica = openNewReplica(createFlag);
            failure = false;
            try {
                byte[] toWrite = Arrays.copyOfRange(buf , pos, pos+len);
                written = currentReplica.writeFile(toWrite, fileOff);
            } catch (MovableFileException ex) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " state exception during write.", ex);
                failure = true;
            } catch (RemoteException re) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " comunication exception during write.", re);
                failure = true;
            } catch (IOException ioe) {
                try {
                    logger.log(Level.SEVERE, "The replica of file " + fileId + " input/output exception during write.", ioe);
                    currentReplica.closeFile(); 
                } catch (Exception cioe) {
                    logger.log(Level.FINEST, "The replica of file " + fileId + " close exception during write.", cioe);
                }
                throw ioe;                             
            } 
            if (failure) failureCount++;            
        } while (failure && failureCount < MAX_WRITE_FAILURE);
        if (failureCount == MAX_WRITE_FAILURE) throw new IOException("Fail to write a file. Tried " + failureCount + " times.");
    }
    
    public int getSizeOfWrittenData() {
        return written;
    }

    @Override
    public long seekFile(long pos, int typ) throws IOException {
        checkForBetterReplica();
        long result = 0;
        boolean failure = false;
        int failureCount = 0;
        do {
            if (failure) currentReplica = openNewReplica(createFlag);
            failure = false;
            try {
                result = currentReplica.seekFile(pos, typ);
            } catch (MovableFileException ex) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " state exception during seek.", ex);
                failure = true;
            } catch (RemoteException re) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " comunication exception during seek.", re);
                failure = true;
            } catch (IOException ioe) {
                try {
                    logger.log(Level.FINEST, "The replica of file " + fileId + " input/output exception during seek.", ioe);
                    currentReplica.closeFile(); 
                } catch (Exception cioe) {
                    logger.log(Level.FINEST, "The replica of file " + fileId + " close input/output exception during seek.", cioe);
                }                              
                failure = true;                
            } 
            if (failure) failureCount++;            
        } while (failure && failureCount < MAX_SEEK_FAILURE);
        if (failureCount == MAX_SEEK_FAILURE) throw new IOException("Fail to seek a file. Tried " + failureCount + " times."); 
        return result;
    }

    @Override
    public void flushFile() throws IOException {
        checkForBetterReplica();
        boolean failure = false;
        int failureCount = 0;
        do {
            if (failure) currentReplica = openNewReplica(createFlag);
            failure = false;
            try {                
                currentReplica.flushFile();
            } catch (MovableFileException ex) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " state exception during flush.", ex);
                failure = true;
            } catch (RemoteException re) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " comunication exception during flush.", re);
                failure = true;
            } catch (IOException ioe) {
                try {
                    logger.log(Level.SEVERE, "The replica of file " + fileId + " input/output exception during flush.", ioe);
                    currentReplica.closeFile(); 
                } catch (Exception cioe) {
                    logger.log(Level.FINEST, "The replica of file " + fileId + " close exception during flush.", cioe);
                }
                throw ioe;                             
            } 
            if (failure) failureCount++;            
        } while (failure && failureCount < MAX_FLUSH_FAILURE);
        if (failureCount == MAX_FLUSH_FAILURE) throw new IOException("Fail to the flush a file. Tried " + failureCount + " times.");    
    }

    @Override
    public void truncateFile(long siz) throws IOException {
        checkForBetterReplica();
        boolean failure = false;
        int failureCount = 0;
        do {
            if (failure) currentReplica = openNewReplica(createFlag);
            failure = false;
            try {                
                currentReplica.truncateFile(siz);
            } catch (MovableFileException ex) {                
                logger.log(Level.FINEST, "The replica of file " + fileId + " state exception during turncate.", ex);
                failure = true;
            } catch (RemoteException re) {
                logger.log(Level.FINEST, "The replica of file " + fileId + " comunication exception during turncate.", re);
                failure = true;
            } catch (IOException ioe) {
                try {
                    logger.log(Level.SEVERE, "The replica of file " + fileId + " input/output exception during turncate.", ioe);
                    currentReplica.closeFile(); 
                } catch (Exception cioe) {
                    logger.log(Level.FINEST, "The replica of file " + fileId + " close exception during turncate.", cioe);
                }
                throw ioe;                             
            } 
            if (failure) failureCount++;            
        } while (failure && failureCount < MAX_TRUNCATE_FAILURE);
        if (failureCount == MAX_TRUNCATE_FAILURE) throw new IOException("Fail to turncate a file. Tried " + failureCount + " times.");
    }

    @Override
    public void closeFile() throws IOException {
        try {
            if(currentReplica != null) currentReplica.closeFile();
        } catch (MovableFileException ex) {
            logger.log(Level.FINEST, "The replica of file " + fileId + " state exception during close.", ex);            
        } catch (RemoteException re) {
            logger.log(Level.FINEST, "The replica of file " + fileId + " comunication exception during close.", re);            
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "The replica of file " + fileId + " input/output exception during close.", ioe);                            
        }       
    }

    private MovableFileInterface openNewReplica(boolean createFlag) throws IOException {
        MovableFileInterface result = null;
        boolean failure;
        int failureCount = 0;
        do {
            failure = false;
            try {
                result = replicaLocator.getReplica(fileId);
                try {
                    result.openFile(createFlag);
                } catch (MovableFileException ex) {                    
                    logger.log(Level.FINE, null, ex);
                } catch (RemoteException e) {
                    failure = true;
                    failureCount++;
                }
            } catch (MetaDataServiceNotAvilableException ex) {
                throw new IOException("The meta-data service is unavilable.", ex);
            } catch (NoReplicaAvilableException ex) {
                failure = true;
                failureCount++;
                if (failureCount == MAX_OPEN_FAILURE) throw new FileOfflineException("No replica is currently avilable.");
            }
        } while (failure && failureCount < MAX_OPEN_FAILURE);
        if (failureCount == MAX_OPEN_FAILURE) throw new FileOfflineException("A connection refused by " + failureCount + " replcias.");
        return result;
    }
    
    private void checkForBetterReplica() throws IOException {
        if (lastOperationTimestamp + RENEWAL_PERIOD < clock.getCurrentTime()) {
            try {
                if (currentReplica != null) currentReplica.closeFile();                
            } catch (RemoteException ex) {
                logger.log(Level.WARNING, "Connection to replica lost during replica swithching.", ex);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "The replica threw IOException during replica switching.", ex);
            } catch (MovableFileException ex) {
                logger.log(Level.FINE, "The replica was moved during replica switching.", ex);                
            } 
            currentReplica = openNewReplica(createFlag);            
            lastOperationTimestamp = clock.getCurrentTime();
        }        
    }
}
