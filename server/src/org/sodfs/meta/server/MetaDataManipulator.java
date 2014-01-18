package org.sodfs.meta.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import org.alfresco.jlan.server.filesys.AccessDeniedException;
import org.alfresco.jlan.server.filesys.FileExistsException;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.sodfs.meta.api.MetaDataManipulatorInterface;
import org.sodfs.meta.persistance.Directory;
import org.sodfs.meta.persistance.File;
import org.sodfs.meta.persistance.Node;
import org.sodfs.meta.persistance.Replica;
import org.sodfs.meta.persistance.ReplicaPK;
import org.sodfs.meta.persistance.StorageServer;
import org.sodfs.storage.driver.SoDFSPath;
import org.sodfs.storage.driver.manager.local.LocalFileManager;
import org.sodfs.storage.meta.api.FileEntity;
import org.sodfs.storage.meta.api.NodeEntity;
import org.sodfs.storage.meta.api.ReplicaEntity;
import org.sodfs.storage.meta.api.ReplicaStatus;
import org.sodfs.storage.meta.api.StorageServerEntity;
import static org.sodfs.storage.meta.api.NamespaceConstants.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class MetaDataManipulator implements MetaDataManipulatorInterface {
    public static final int DEADLOCK_RETRIES = 3;
    private EntityManager em;
    private static Logger logger = Logger.getLogger(LocalFileManager.class.getName());
    private SizeActualisator sa;
    
    public MetaDataManipulator(EntityManagerFactory emf, SizeActualisator sur) {
        em = emf.createEntityManager();
        this.sa = sur;
    }
    
    /* NAMESPACE OPERATIONS */
    
    public synchronized FileEntity createFile(SoDFSPath path, int userId, int groupId, int mode, int attr, long pinTime, long coinTTL, int minNOR) throws IOException, RemoteException {
        EntityTransaction t = em.getTransaction();
        File result = null;
        t.begin();
        try {
            Directory parent = getDir(path, em, true);
            if (parent == null) throw new AccessDeniedException("The directory " + path.getPath() + " does not exist.");
            result = new File();
            setNodeAtributes(result, parent, path.getName(), groupId, userId, mode, attr);            
            result.setChanged(new Date());
            result.setPinTime(pinTime);
            result.setCoinTTL(coinTTL);
            result.setMinNOR(minNOR);
            em.persist(result);
            t.commit();            
        } catch (RollbackException e) {
            throw new FileExistsException("The file " + path.getPath() + " already exists.");            
        } 
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }

    public synchronized void deleteDirectory(SoDFSPath path) throws IOException, RemoteException {
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Directory dir = getDir(path, em, false);
            if (dir == null) throw new AccessDeniedException("The parent directory of " + path.getPath() + " does not exist.");
            if (dir.getNodeId() == ROOT_ID) new AccessDeniedException("The root directory is unremovable.");
            em.clear();
            dir = em.find(Directory.class, dir.getNodeId());
            em.remove(dir);
            t.commit();            
        } catch (RollbackException e) {            
            throw new AccessDeniedException("The directory " + path.getPath() + " is not empty.");
        } 
        finally {
            if (t.isActive()) {
                t.rollback(); 
            }                           
        } 
    }

    public synchronized void createDirectory(SoDFSPath path, int userId, int groupId, int mode, int attr) throws IOException, RemoteException {
        EntityTransaction t = em.getTransaction();
        Directory result = null;
        t.begin();
        try {
            Directory parent = getDir(path, em, true);
            if (parent == null) throw new AccessDeniedException("The directory " + path.getPath() + " does not exist.");
            result = new Directory();
            setNodeAtributes(result, parent, path.getName(), groupId, userId, mode, attr);            
            em.persist(result);
            t.commit();            
        } catch (RollbackException e) {            
            throw new FileExistsException("The directory " + path.getPath() + " already exists.");
        } 
        finally {
            if (t.isActive()) t.rollback();
        }        
    }

    public synchronized void deleteFile(SoDFSPath path) throws IOException, RemoteException {
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Node node = getNode(path, em);
            if (node != null && !node.isDirectory()) {                
                em.clear();                
                node = em.find(File.class, node.getNodeId());
                em.remove((File)node);                
                em.flush();
            } else {
                throw new FileNotFoundException("The file " + path.getPath() + " does not exist.");
            }           
            t.commit();            
        } catch (RollbackException e) {            
            throw new AccessDeniedException("System was unable to remove a file " + path.getPath() + " for unknown reason.");
        } 
        finally {
            if (t.isActive()) {
                t.rollback();
            }
        } 
    }

    public synchronized void renameFile(SoDFSPath oldPath, SoDFSPath newPath) throws IOException, RemoteException {
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Directory oldParent = getDir(oldPath, em, true);
            if (oldParent == null) throw new FileNotFoundException("The file or the directory " + oldPath.getPath() + " does not exist.");
            Node node = getChildByName(oldParent, oldPath.getName());            
            if (node != null) {
                Directory newParent = getDir(newPath, em, true);
                if (newParent == null) throw new FileNotFoundException("The destination directory " + newPath.getPath() + " does not exist.");
                node.setParent(newParent);
                node.setName(newPath.getName());
                em.persist(node);
            } else {
                throw new FileNotFoundException("The file or the directory " + oldPath.getPath() + " does not exist.");
            }           
            t.commit();            
        } catch (RollbackException e) {            
            throw new FileExistsException("The directory " + newPath.getPath() + " already exists.");
        } 
        finally {
            if (t.isActive()) t.rollback();
        } 
    }

    public synchronized NodeEntity getFileInformation(SoDFSPath path) throws IOException, RemoteException {
        EntityTransaction t = em.getTransaction();
        Node result = null;
        t.begin();
        try {
            result = getNode(path, em);
            if (result == null) throw new FileNotFoundException("The file or the directory " + path.getPath() + " does not exist.");            
            t.commit();            
        } catch (RollbackException e) {            
            throw new AccessDeniedException("System was unable to resolve a file or a directory " + path.getPath() + " information for unknown reason.");
        } 
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }

    public synchronized void setFileInformation(SoDFSPath path, NodeEntity file) throws IOException, RemoteException {
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            em.merge(file);            
            t.commit();            
        } catch (IllegalArgumentException e) {
            throw new FileNotFoundException("The file " + file.getName() + " has been deleted.");              
        }
        catch (RollbackException e) {            
            throw new AccessDeniedException("System was unable to resolve a file or a directory information for an unknown reason.");
        } 
        finally {
            if (t.isActive()) t.rollback();
        }
    }

    public synchronized int fileExist(SoDFSPath path) throws RemoteException {
        EntityTransaction t = em.getTransaction();
        int result = FileStatus.NotExist;
        t.begin();
        try {
            Node node = getNode(path, em);
            if (node != null) {
                result = (node.isDirectory())?FileStatus.DirectoryExists:FileStatus.FileExists;
            }   
            t.commit();            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Faild to check if a file or a directory " + path.getPath() + " exists.");
        }
        finally {            
            if (t.isActive()) t.rollback();
        }        
        return result;
    }

    public synchronized List<NodeEntity> search(SoDFSPath path, int attr) throws FileNotFoundException, RemoteException {
        EntityTransaction t = em.getTransaction();
        ArrayList<NodeEntity> result = null;
        t.begin();
        try {            
            if (path.containsWildcard()) {
                Directory parent = getDir(path, em, true);
                if (parent == null) throw new FileNotFoundException("The file or the directory " + path.getPath() + " does not exist.");
                List<Node> list = parent.getChildren();
                result = new ArrayList<NodeEntity>(list);                
            } else {                
                Node node = getNode(path, em);
                if (node == null) throw new FileNotFoundException("The file or the directory " + path.getPath() + " does not exist.");
                result = new ArrayList<NodeEntity>(1);
                result.add(node);
            }
            t.commit();            
        } 
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }

    public synchronized boolean updateSize(int fileId, long newSize) throws RemoteException {
        sa.updateSize(fileId, newSize);
        return true;
    }

    public synchronized boolean registerReplica(int storageId, int fileId) throws RemoteException {
        boolean success = true;
        EntityTransaction t = em.getTransaction();
        t.begin();        
        try {
            Replica replica  = new Replica();
            replica.setReplicaPK(new ReplicaPK(fileId, storageId));
            replica.setStatus(ReplicaStatus.NOT_ACTIVE);
            em.clear();            
            em.persist(replica);            
            t.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Faild to register replica of file " + fileId + " in storage server " + storageId + ".", e);
            success = false;
        }
        finally {
            if (t.isActive()) t.rollback();
        }
        return success;
    }

    public synchronized boolean activateReplica(int storageId, int fileId) throws RemoteException {
        boolean success = true;                 
        ArrayList<Replica> toMarkMoved = new ArrayList<Replica>();
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Replica replica  = em.find(Replica.class, new ReplicaPK(fileId, storageId));
            if (replica.getStatus() != ReplicaStatus.MOVED) {                
                replica.setStatus(ReplicaStatus.ACTIVE);
                Query q = (Query) em.createNamedQuery("Replica.getMovingReplicas");            
                q.setParameter("destination", storageId);
                q.setParameter("fileId", fileId);                    
                List replicas = q.getResultList();
                for (Iterator<Replica> it = replicas.iterator(); it.hasNext();) {
                    Replica r = it.next();
                    toMarkMoved.add(r);
                }
            }
            t.commit();
            success = true;
        } catch (Exception e) { 
            success = false;
            logger.log(Level.SEVERE, "Faild to activate replica of file " + fileId + " in storage server " + storageId + ".", e);            }
        finally {
            if (t.isActive()) t.rollback();
        }            
        if (success) {
            for (Iterator<Replica> it = toMarkMoved.iterator(); it.hasNext();) {
                Replica toMark = it.next();
                t = em.getTransaction();            
                t.begin();            
                try {
                    toMark.setStatus(ReplicaStatus.MOVED);
                    em.merge(toMark);
                    t.commit();
                    success = true;
                } catch (Exception e) { 
                    //success = false;
                    logger.log(Level.SEVERE, "Faild to mark replica of file " + fileId + " in storage server " + storageId + " moved.", e);            }
                finally {
                    if (t.isActive()) t.rollback();
                } 
            }         
        }        
        return success;
    }

    public synchronized boolean markReplicaMoving(int storageId, int fileId, int destignationStorage) throws RemoteException {
        boolean success = true;
        int failureCount = 0;
        boolean deadlock = false;
        do {
            deadlock = false;
            EntityTransaction t = em.getTransaction();
            t.begin();
            try {
                Replica replica  = em.find(Replica.class, new ReplicaPK(fileId, storageId));            
                StorageServer destination = em.find(StorageServer.class, destignationStorage);
                if (replica != null && destination != null && replica.getStatus() == ReplicaStatus.ACTIVE) {                
                    replica.setStatus(ReplicaStatus.MOVING);                
                    replica.setDestination(destination);                
                    //em.merge(replica);
                    t.commit();
                } else {
                    success = false;
                }
                
            } catch (Exception e) {                
                success = false;
                deadlock = true;
                failureCount++;
                if (failureCount == DEADLOCK_RETRIES) {
                    logger.log(Level.SEVERE, "Faild to mark replica of file " + fileId + " in storage server " + storageId + " moving.", e);
                }  
            }
            finally {
                if (t.isActive()) t.rollback();
            }
        } while (deadlock && failureCount < DEADLOCK_RETRIES);
        return success;
    }

    public synchronized void removeReplica(int storageId, int fileId) throws RemoteException {
        boolean deadlock = false;
        int failureCount = 0;
        do {
            deadlock = false;
            EntityTransaction t = em.getTransaction();
            t.begin();
            try {            
                Replica replica  = em.find(Replica.class, new ReplicaPK(fileId, storageId));
                em.remove(replica);
                t.commit();                
            } catch (Exception e) {
                deadlock = true;
                failureCount++;
                logger.log(Level.SEVERE, "Faild to remove replica of file " + fileId + " in storage server " + storageId + ".", e);
            }
            finally {
                if (t.isActive()) t.rollback();
            }  
        } while (deadlock && failureCount < DEADLOCK_RETRIES);      
    }

    public synchronized List<StorageServerEntity> getStorageServers() throws RemoteException {
        ArrayList<StorageServerEntity> result = null;
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Query q = (Query) em.createNamedQuery("StorageServer.findAll");                     
            List<StorageServerEntity> list = q.getResultList();
            result = new ArrayList<StorageServerEntity>(list);
            t.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to get list of the storage servers.", e);
        }
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;    
    }

    public synchronized int[] getStorageServersHoldingFileReplica(int fileId, int localStorageId) throws RemoteException {
        int[] result = null;
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Query q = (Query) em.createNamedQuery("Replica.getFileReplicas");            
            q.setParameter("active", ReplicaStatus.ACTIVE);
            q.setParameter("moving", ReplicaStatus.MOVING);
            q.setParameter("fileId", fileId);
            q.setParameter("localStorageId", localStorageId);
            List<ReplicaEntity> list = q.getResultList();
            Integer[] tab = new Integer[list.size()];
            tab = list.toArray(tab);
            if (tab != null) {
                result = new int[tab.length];
                for (int i = 0; i < tab.length; i++) {
                    result[i] = tab[i];
                }
            }            
            t.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Faild to get list of valid replicas for file " + fileId + ".", e);
        }
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }

    public synchronized List<StorageServerEntity> getStorageServersHoldingFileReplica(int fileId) throws RemoteException {
        ArrayList<StorageServerEntity> result = null;
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Query q = (Query) em.createNamedQuery("Replica.getFileReplicasStorageServers");            
            q.setParameter("active", ReplicaStatus.ACTIVE);
            q.setParameter("moving", ReplicaStatus.MOVING);
            q.setParameter("fileId", fileId);            
            List<StorageServerEntity> list = q.getResultList();
            result = new ArrayList<StorageServerEntity>(list);
            t.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Faild to get list of valid replicas for file " + fileId + ".", e);
        }
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }

    public synchronized ReplicaEntity[] getValidReplicas(int storageId) throws RemoteException {
        ReplicaEntity[] result = null;
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Query q = (Query) em.createNamedQuery("Replica.getValidReplicas");
            q.setParameter("storageId", storageId);            
            q.setParameter("moved", ReplicaStatus.MOVED);                        
            List<ReplicaEntity> list = q.getResultList();
            result = new ReplicaEntity[list.size()];
            result = list.toArray(result);
            t.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Faild to get list of valid replicas for storage server: " + storageId, e);
        }
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }

    public synchronized ReplicaEntity[] getMovedReplicas(int storageId) throws RemoteException {
        ReplicaEntity[] result = null;
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            Query q = (Query) em.createNamedQuery("Replica.getMovedReplicas");            
            q.setParameter("moved", ReplicaStatus.MOVED);
            q.setParameter("storageId", storageId);
            List<ReplicaEntity> list = q.getResultList();
            for (ReplicaEntity re : list) {
                em.refresh(re);
            }
            result = new ReplicaEntity[list.size()];
            result = list.toArray(result);
            t.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Faild to get list of moved replicas for storage server: " + storageId, e);
        }
        finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }

    public synchronized StorageServerEntity registerStorageServer(String name, String address, int port, String multicastAddress, int multicastPort) throws RemoteException {
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            StorageServer result = null;
            Query q = (Query) em.createNamedQuery("StorageServer.findByName");            
            q.setParameter("name", name);
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                result = new StorageServer();
                result.setName(name);
                setStorageServerFields(result, address, port, multicastAddress, multicastPort);
                em.persist(result);            
            }
            else {
                result = (StorageServer) resultList.get(0);
                setStorageServerFields(result, address, port, multicastAddress, multicastPort);
                em.merge(result);
            }
            t.commit();       
            return result;
        } finally {
            if (t.isActive()) t.rollback();
        }
    }

    private void setStorageServerFields(StorageServer result, String address, int port, String multicastAddress, int multicastPort) {
        result.setAddress(address);
        result.setPort(port);
        result.setMulticastAddress(multicastAddress);
        result.setMulticastPort(multicastPort);
    }

    public synchronized StorageServerEntity getStorageServerEntity(int storageId) throws RemoteException {
        StorageServerEntity result = null;
        EntityTransaction t = em.getTransaction();
        t.begin();
        try {
            result = em.find(StorageServer.class, storageId);            
            t.commit();
        } finally {
            if (t.isActive()) t.rollback();
        }
        return result;
    }
    
    /* AUXILIARY METHODS */
    
    private Directory getDir(SoDFSPath path, EntityManager em, boolean getParent) {
        Directory result = getRoot(em);
        String parts[] = path.getParts();
        int length = (getParent)?parts.length - 1:parts.length;
        for (int i = 0; result != null && i < length; i++) {
            em.refresh(result);
            Node node = getChildByName(result, parts[i]);
            if (node != null && node.isDirectory()) {
                result = (Directory) node;
            } else {
                result = null;
            }
        }        
        return result;
    }

    private Directory getRoot(EntityManager em) {
        Directory result = em.find(Directory.class, ROOT_ID);
        em.refresh(result);
        return result;
    }
    
    private Node getNode(SoDFSPath path, EntityManager em) {        
        Directory dir = getRoot(em);
        Node result = dir;
        String parts[] = path.getParts();
        int length = parts.length - 1;
        for (int i = 0; dir != null && i < length; i++) {
            em.refresh(dir);
            Node node = getChildByName(dir, parts[i]);            
            if (node != null && node.isDirectory()) {
                dir = (Directory) node;
            } else {
                dir = null;
                result = null;
            }
        }
        if (dir != null && parts.length > 0) {
            em.refresh(dir);
            result = getChildByName(dir, parts[parts.length - 1]);
        }
        return result;
    } 

    private Node getChildByName(Directory dir, String name) {
        List<Node> children = dir.getChildren();        
        Node node = null;
        for (Iterator<Node> it = children.iterator(); node == null && it.hasNext();) {
            Node tmp = it.next();
            if (tmp.getName().equals(name)) {
                node = tmp;
            }
        }
        return node;
    }

    private void setNodeAtributes(Node result, Directory parent, String name, int groupId,  int userId, int mode, int attr) {
        result.setName(name);
        result.setAccessed(new Date());
        result.setCreated(new Date());
        result.setModified(new Date());
        result.setNominalSize(0);
        result.setAllocationSize(0);
        result.setUserId(userId);
        result.setGroupId(groupId);
        result.setMode(mode);
        result.setAttribute(attr);
        result.setParent(parent);
    }
}
