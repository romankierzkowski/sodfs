package org.sodfs.meta.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import org.sodfs.storage.driver.SoDFSPath;
import org.sodfs.storage.meta.api.FileEntity;
import org.sodfs.storage.meta.api.NodeEntity;
import org.sodfs.storage.meta.api.ReplicaEntity;
import org.sodfs.storage.meta.api.StorageServerEntity;


/**
 *
 * @author Roman Kierzkowski
 */
public interface MetaDataManipulatorInterface extends Remote {

    public FileEntity createFile(SoDFSPath path, int userId, int groupId, int mode, int attr, long pinTime, long coinTTL, int minNOR) throws IOException, RemoteException;

    public void deleteDirectory(SoDFSPath path) throws IOException, RemoteException;
    
    public void createDirectory(SoDFSPath path, int userId, int groupId, int mode, int attr) throws IOException, RemoteException;

    public void deleteFile(SoDFSPath path) throws IOException, RemoteException;

    public List<StorageServerEntity> getStorageServers() throws RemoteException;

    public void renameFile(SoDFSPath oldPath, SoDFSPath newPath) throws IOException, RemoteException;

    public NodeEntity getFileInformation(SoDFSPath path) throws IOException, RemoteException;
    
    public void setFileInformation(SoDFSPath path, NodeEntity file) throws IOException, RemoteException;

    public int fileExist(SoDFSPath path) throws RemoteException;
    
    public List<NodeEntity> search(SoDFSPath path, int attr) throws FileNotFoundException, RemoteException;

    public boolean updateSize(int fileId, long newSize) throws RemoteException;

    
    public boolean registerReplica(int storageId, int fileId) throws RemoteException;
    
    public boolean activateReplica(int storageId, int fileId) throws RemoteException;

    public boolean markReplicaMoving(int storageId, int fileId, int destignationStorage) throws RemoteException;
    
    public void removeReplica(int storageId, int fileId) throws RemoteException;
    
    public int[] getStorageServersHoldingFileReplica(int fileId, int localStorageId) throws RemoteException;

    public List<StorageServerEntity> getStorageServersHoldingFileReplica(int fileId) throws RemoteException;

    public ReplicaEntity[] getValidReplicas(int storageId) throws RemoteException;

    public ReplicaEntity[] getMovedReplicas(int storageId) throws RemoteException;

    
    public StorageServerEntity registerStorageServer(String name, String address, 
      int port, String multicastAddress, int multicastPort) throws RemoteException;
    
    public StorageServerEntity getStorageServerEntity(int storageId) throws RemoteException;
}
