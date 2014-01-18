package org.sodfs.storage.meta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.sodfs.storage.driver.SoDFSPath;
import org.sodfs.storage.meta.api.FileEntity;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.NodeEntity;
import org.sodfs.storage.meta.api.ReplicaEntity;
import org.sodfs.storage.meta.api.StorageServerEntity;
import org.sodfs.utils.Clock;
import org.sodfs.utils.LRUMapWithTimeout;

/**
 *
 * @author Roman Kierzkowski
 */
public class MetaDataCache implements MetaDataServiceInterface  {
    
    private MetaDataManager mdm;
    
    private LRUMapWithTimeout<String, NodeEntity> fileInformationCache;
    private final int FILE_INFORMATION_CACHE_SIZE = 100;
    private final long DIRECTORY_INFORMATION_TTL = 5 * Clock.SECOND;
    private final long FILE_INFORMATION_TTL = 3 * Clock.SECOND;

    public MetaDataCache(MetaDataManager mdm) {
        this.mdm = mdm;
        this.fileInformationCache = new LRUMapWithTimeout<String, NodeEntity>(FILE_INFORMATION_CACHE_SIZE);
    }
    
    public FileEntity createFile(SoDFSPath path, int userId, int groupId, int mode, int attr, long pinTime, long coinTTL, int minNOR) throws IOException, MetaDataServiceNotAvilableException {
        return mdm.createFile(path, userId, groupId, mode, attr, pinTime, coinTTL, minNOR);
    }

    public void deleteDirectory(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException {
        mdm.deleteDirectory(path);
    }

    public void createDirectory(SoDFSPath path, int userId, int groupId, int mode, int attr) throws IOException, MetaDataServiceNotAvilableException {
        mdm.createDirectory(path, userId, groupId, mode, attr);
    }

    public void deleteFile(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException {
        mdm.deleteFile(path);
    }

    public void renameFile(SoDFSPath oldPath, SoDFSPath newPath) throws IOException, MetaDataServiceNotAvilableException {
        mdm.renameFile(oldPath, newPath);
    }

    public NodeEntity getFileInformation(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException {
        NodeEntity result = fileInformationCache.get(path.getPath());
        if (result == null) {
            result = mdm.getFileInformation(path);
            long ttl = (result.isDirectory())?DIRECTORY_INFORMATION_TTL:FILE_INFORMATION_TTL;
            fileInformationCache.put(path.getPath(), result, ttl);
        } 
        return result;
    }

    public void setFileInformation(SoDFSPath path, NodeEntity node) throws IOException, MetaDataServiceNotAvilableException {
        fileInformationCache.remove(path.getPath());
        mdm.setFileInformation(path, node);
    }

    public int fileExist(SoDFSPath path) throws MetaDataServiceNotAvilableException {
        return mdm.fileExist(path);
    }

    public List<NodeEntity> search(SoDFSPath path, int attr) throws FileNotFoundException, MetaDataServiceNotAvilableException {
        return mdm.search(path, attr);
    }

    public boolean updateSize(int fileId, long newSize) throws MetaDataServiceNotAvilableException {
        return mdm.updateSize(fileId, newSize);
    }

    public boolean registerReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException {
        return mdm.registerReplica(storageId, fileId);
    }

    public boolean activateReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException {
        return mdm.activateReplica(storageId, fileId);
    }

    public boolean markReplicaMoving(int storageId, int fileId, int destignationStorage) throws MetaDataServiceNotAvilableException {
        return mdm.markReplicaMoving(storageId, fileId, destignationStorage);
    }

    public void removeReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException {
        mdm.removeReplica(storageId, fileId);
    }

    public int[] getStorageServersHoldingFileReplica(int fileId, int localStorageId) throws MetaDataServiceNotAvilableException {
        return mdm.getStorageServersHoldingFileReplica(fileId, localStorageId);
    }

    public List<StorageServerEntity> getStorageServersHoldingFileReplica(int fileId) throws MetaDataServiceNotAvilableException {
        return mdm.getStorageServersHoldingFileReplica(fileId);
    }

    public ReplicaEntity[] getValidReplicas(int storageId) throws MetaDataServiceNotAvilableException {
        return mdm.getValidReplicas(storageId);
    }

    public ReplicaEntity[] getMovedReplicas(int storageId) throws MetaDataServiceNotAvilableException {
        return mdm.getMovedReplicas(storageId);
    }

    public StorageServerEntity registerStorageServer(String name, String address, int port, String multicastAddress, int multicastPort) throws MetaDataServiceNotAvilableException {
        return mdm.registerStorageServer(name, address, port, multicastAddress, multicastPort);
    }

    public StorageServerEntity getStorageServerEntity(int storageId) throws MetaDataServiceNotAvilableException {
        return mdm.getStorageServerEntity(storageId);
    }

    public List<StorageServerEntity> getStorageServers() throws MetaDataServiceNotAvilableException {
        return mdm.getStorageServers();
    }

}
