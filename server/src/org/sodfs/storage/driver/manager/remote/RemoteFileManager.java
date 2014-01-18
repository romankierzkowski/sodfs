package org.sodfs.storage.driver.manager.remote;

import org.sodfs.storage.excludes.ExcludeManager;
import java.io.IOException;
import org.sodfs.storage.driver.manager.*;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.discovery.NetworkDiscoverer;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.driver.manager.exceptions.NoReplicaAvilableException;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;

/**
 *
 * @author Roman Kierzkowski
 */
public class RemoteFileManager {    
    private MetaDataServiceInterface mds;
    private InternodeCommunicator inc;
    private NetworkDiscoverer networkDiscoverer;
    private ExcludeManager excludeManager;
    
    private int localStorageId;   
    
    private Logger logger = Logger.getLogger(RemoteFileManager.class.getName());
    private LinkedList<RemoteFileWraperFactory> wraperFactories = new LinkedList<RemoteFileWraperFactory>();
    
    public RemoteFileManager(int storageId) {
        localStorageId = storageId;
        wraperFactories.add(new InformantFactory());
    }
    
    /* DEPENDENCES */

    public void setMetaDataService(MetaDataServiceInterface mds) {
        this.mds = mds;
    }

    public void setInternodeCommunicator(InternodeCommunicator inc) {
        this.inc = inc;
    }

    public void setNetworkDiscoverer(NetworkDiscoverer networkDiscoverer) {
        this.networkDiscoverer = networkDiscoverer;
    }

    public void setExcludeManager(ExcludeManager excludeManager) {
        this.excludeManager = excludeManager;
    }
    
    /* WRAPER FACTORIES REGISTRATION */
    
    public void addRemoteFileWraperFactory(RemoteFileWraperFactory factory) {
        wraperFactories.add(factory);
    }
    
    public void removeLocalFileWraperFactory(RemoteFileWraperFactory factory) {
        wraperFactories.remove(factory);
    }
    
    public MovableFileInterface getRemoteReplica(int fileId) throws MetaDataServiceNotAvilableException, NoReplicaAvilableException {
        MovableFileInterface result = null;        
        int[] replicaHolders = null;        
        replicaHolders = mds.getStorageServersHoldingFileReplica(fileId, localStorageId);
        boolean successful = false;
        int from = -1;
        if (replicaHolders.length != 0) {            
            replicaHolders = networkDiscoverer.orderFromClosestToFarest(replicaHolders);            
            for (int i = 0; !successful && i < replicaHolders.length; i++) {
                from = replicaHolders[i];
                if (!excludeManager.isReplicaOrStorageExcluded(from, fileId)) {
                    try {    
                        StorageServerInterface ssi = inc.getRemoteStorageServerInterface(from);
                        if (ssi != null) {
                            result = ssi.getRemoteFile(fileId);
                            successful = true;    
                        }
                        else {
                            excludeManager.excludeStorageServer(from);
                            logger.log(Level.FINE, "The storage server " + from + " is not avilable.");
                        }
                    } catch (MovableFileException ex) {
                        //excludeManager.excludeReplica(from, fileId);
                        logger.log(Level.FINE, "The replica " + fileId + "from storage server " + from + " is not avilable.", ex);
                    } catch (RemoteException ex) {
                        excludeManager.excludeStorageServer(from);
                        logger.log(Level.FINE, "The storage server " + from + " is not avilable.", ex);
                    }
                }
            }
        } 
        if (replicaHolders.length == 0 || !successful) {
            logger.log(Level.WARNING, "No replica for file " + fileId + " is avilable.");
            throw new NoReplicaAvilableException(fileId);
        }
        result = wrap(result, localStorageId, from, fileId);
        return result;
    }

    private MovableFileInterface wrap(MovableFileInterface replica, int localStorageId, int remoteStorageId, int fileId) {
        MovableFileInterface result = replica;
        Iterator<RemoteFileWraperFactory> it = wraperFactories.iterator();
        while (it.hasNext()) {
            RemoteFileWraperFactory factory = it.next();
            result = factory.wrap(result, localStorageId, remoteStorageId, fileId);
        }
        return result;
    }
    
    /* INFORMANT */
    
    private class Informant implements MovableFileInterface {
        private MovableFileInterface replica;
        private int localStorageId;
        private int remoteStorageId;
        private int fileId;

        public Informant(MovableFileInterface component, int localStorageId, int remoteStorageId, int fileId) {
            this.replica = component;
            this.localStorageId = localStorageId;
            this.remoteStorageId = remoteStorageId;
            this.fileId = fileId;
        }
        
        public void openFile(boolean createFlag) throws RemoteException, IOException, MovableFileException {
            try {                
                replica.openFile(createFlag);
            } catch (RemoteException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (IOException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (MovableFileException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            }
        }

        public byte[] readFile(int len, long fileOff) throws RemoteException, IOException, MovableFileException {
            try {
                return replica.readFile(len, fileOff);            
            } catch (RemoteException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (IOException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (MovableFileException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            }
        }

        public int writeFile(byte[] buf, long fileOff) throws RemoteException, IOException, MovableFileException {
            try {
                return replica.writeFile(buf, fileOff);
            } catch (RemoteException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (IOException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (MovableFileException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            }
        }

        public long seekFile(long pos, int typ) throws RemoteException, IOException, MovableFileException {            
            try {
                return replica.seekFile(pos, typ);
            } catch (RemoteException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (IOException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (MovableFileException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            }
        }

        public void flushFile() throws RemoteException, IOException, MovableFileException {
            try {
                replica.flushFile();
            } catch (RemoteException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (IOException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (MovableFileException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            }            
        }

        public void truncateFile(long siz) throws RemoteException, IOException, MovableFileException {
            try {
                replica.truncateFile(siz);
            } catch (RemoteException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (IOException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (MovableFileException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            }            
        }

        public void closeFile() throws RemoteException, IOException, MovableFileException {
            try {
                replica.closeFile();
            } catch (RemoteException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (IOException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            } catch (MovableFileException ex) {
                excludeManager.excludeReplica(remoteStorageId, fileId);
                throw ex;
            }            
        }

        public int getStorageId() throws RemoteException, MovableFileException {            
            return remoteStorageId;
        }
    }
    
    /* INFORMANT FACTORY */
    
    private class InformantFactory implements RemoteFileWraperFactory {
        public MovableFileInterface wrap(MovableFileInterface replica, int localStorageId, int remoteStorageId, int fileId) {
            return new Informant(replica, localStorageId, remoteStorageId, fileId);
        }        
    }
    
}
