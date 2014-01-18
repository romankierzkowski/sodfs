package org.sodfs.storage.meta.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.sodfs.storage.driver.SoDFSPath;

/**
 *
 * @author Roman Kierzkowski
 */
public interface MetaDataServiceInterface {

    /* NAMESPACE OPERATIONS */
    
    public FileEntity createFile(SoDFSPath path, int userId, int groupId, int mode, int attr, long pinTime, long coinTTL, int minNOR) throws IOException, MetaDataServiceNotAvilableException;

    public void deleteDirectory(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException;
    
    public void createDirectory(SoDFSPath path, int userId, int groupId, int mode, int attr) throws IOException, MetaDataServiceNotAvilableException;

    public void deleteFile(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException;

    public List<StorageServerEntity> getStorageServers() throws MetaDataServiceNotAvilableException;

    public void renameFile(SoDFSPath oldPath, SoDFSPath newPath) throws IOException, MetaDataServiceNotAvilableException;

    public NodeEntity getFileInformation(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException;
    
    public void setFileInformation(SoDFSPath path, NodeEntity file) throws IOException, MetaDataServiceNotAvilableException;

    public int fileExist(SoDFSPath path) throws MetaDataServiceNotAvilableException;
    
    public List<NodeEntity> search(SoDFSPath path, int attr) throws FileNotFoundException, MetaDataServiceNotAvilableException;

    public boolean updateSize(int fileId, long newSize) throws MetaDataServiceNotAvilableException;
    
    /* REPLICAS LOCATION OPERATIONS */
    
    public boolean registerReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException;
    
    public boolean activateReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException;

    public boolean markReplicaMoving(int storageId, int fileId, int destignationStorage) throws MetaDataServiceNotAvilableException;
    
    public void removeReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException;
    
    public int[] getStorageServersHoldingFileReplica(int fileId, int localStorageId) throws MetaDataServiceNotAvilableException;

    public List<StorageServerEntity> getStorageServersHoldingFileReplica(int fileId) throws MetaDataServiceNotAvilableException;

    public ReplicaEntity[] getValidReplicas(int storageId) throws MetaDataServiceNotAvilableException;

    public ReplicaEntity[] getMovedReplicas(int storageId) throws MetaDataServiceNotAvilableException;

    /* STORAGE SERVERS LOCATION OPERATIONS */
    
    public StorageServerEntity registerStorageServer(String name, String address, 
      int port, String multicastAddress, int multicastPort) throws MetaDataServiceNotAvilableException;
    
    public StorageServerEntity getStorageServerEntity(int storageId) throws MetaDataServiceNotAvilableException;

}
