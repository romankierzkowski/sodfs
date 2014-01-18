package org.sodfs.storage.meta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import org.sodfs.meta.api.MetaDataManipulatorInterface;
import org.sodfs.storage.driver.SoDFSPath;
import org.sodfs.storage.meta.api.FileEntity;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.NodeEntity;
import org.sodfs.storage.meta.api.ReplicaEntity;
import org.sodfs.storage.meta.api.StorageServerEntity;

/**
 *
 * @author Roman Kierzkowski
 */
public class MetaDataManager implements MetaDataServiceInterface {

    private MetaDataManipulatorInterface manipulator;

    public MetaDataManager(MetaDataManipulatorInterface manipulator) {
        this.manipulator = manipulator;
    }

    public FileEntity createFile(SoDFSPath path, int userId, int groupId, int mode, int attr, long pinTime, long coinTTL, int minNOR) throws IOException, MetaDataServiceNotAvilableException {
        try {
            return manipulator.createFile(path, userId, groupId, mode, attr, pinTime, coinTTL, minNOR);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }        
        return null; // Will not be executed.
    }

    public void deleteDirectory(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException {
        try {
            manipulator.deleteDirectory(path);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
    }

    public void createDirectory(SoDFSPath path, int userId, int groupId, int mode, int attr) throws IOException, MetaDataServiceNotAvilableException {
        try {
            manipulator.createDirectory(path, userId, groupId, mode, attr);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
    }

    public void deleteFile(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException {
        try {
            manipulator.deleteFile(path);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
    }

    public void renameFile(SoDFSPath oldPath, SoDFSPath newPath) throws IOException, MetaDataServiceNotAvilableException {
        try {
            manipulator.renameFile(oldPath, newPath);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
    }

    public NodeEntity getFileInformation(SoDFSPath path) throws IOException, MetaDataServiceNotAvilableException {
        try {
            return manipulator.getFileInformation(path);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }        
        return null; // Will not be executed;
    }

    public void setFileInformation(SoDFSPath path, NodeEntity file) throws IOException, MetaDataServiceNotAvilableException {
        try {
            manipulator.setFileInformation(path, file);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
    }

    public int fileExist(SoDFSPath path) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.fileExist(path);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return 0; // Will not be executed;
    }

    public List<NodeEntity> search(SoDFSPath path, int attr) throws FileNotFoundException, MetaDataServiceNotAvilableException {
        try {
            return manipulator.search(path, attr);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    public boolean updateSize(int fileId, long newSize) throws MetaDataServiceNotAvilableException {
        try {
           return manipulator.updateSize(fileId, newSize);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return false; // Will not be executed;
    }

    public boolean registerReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.registerReplica(storageId, fileId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return false; // Will not be executed;
    }

    public boolean activateReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.activateReplica(storageId, fileId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return false; 
    }

    public boolean markReplicaMoving(int storageId, int fileId, int destignationStorage) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.markReplicaMoving(storageId, fileId, destignationStorage);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return false; 
    }

    public void removeReplica(int storageId, int fileId) throws MetaDataServiceNotAvilableException {
        try {
            manipulator.removeReplica(storageId, fileId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
    }

    public int[] getStorageServersHoldingFileReplica(int fileId, int localStorageId) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.getStorageServersHoldingFileReplica(fileId, localStorageId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    public List<StorageServerEntity> getStorageServersHoldingFileReplica(int fileId) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.getStorageServersHoldingFileReplica(fileId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    public ReplicaEntity[] getValidReplicas(int storageId) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.getValidReplicas(storageId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    public ReplicaEntity[] getMovedReplicas(int storageId) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.getMovedReplicas(storageId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    public StorageServerEntity registerStorageServer(String name, String address, int port, String multicastAddress, int multicastPort) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.registerStorageServer(name, address, port, multicastAddress, multicastPort);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    public StorageServerEntity getStorageServerEntity(int storageId) throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.getStorageServerEntity(storageId);
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    public List<StorageServerEntity> getStorageServers() throws MetaDataServiceNotAvilableException {
        try {
            return manipulator.getStorageServers();
        } catch (RemoteException remoteException) {
            throwMetaDataServiceNotAvilableException(remoteException);
        }
        return null; // Will not be executed;
    }

    private void throwMetaDataServiceNotAvilableException(RemoteException remoteException) throws MetaDataServiceNotAvilableException {
        throw new MetaDataServiceNotAvilableException(remoteException);
    }
}
