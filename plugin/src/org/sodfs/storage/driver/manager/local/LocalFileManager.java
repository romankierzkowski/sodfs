package org.sodfs.storage.driver.manager.local;

import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.utils.NumberFileFilter;
import org.sodfs.storage.driver.manager.local.controler.ReplicationControler;
import org.sodfs.storage.driver.manager.local.updater.UpdateWriter;
import org.sodfs.storage.driver.manager.*;
import org.sodfs.storage.driver.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.sodfs.storage.meta.api.ReplicaEntity;
import org.sodfs.utils.Clock;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.communication.GroupCommunicator;
import org.sodfs.storage.driver.manager.exceptions.DereplicatedReplicaException;
import org.sodfs.storage.driver.manager.exceptions.FaultyReplicaException;
import org.sodfs.storage.driver.manager.exceptions.InconsistentReplicaException;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.driver.manager.exceptions.MovedReplicaException;
import org.sodfs.storage.driver.manager.exceptions.NotExistingReplicaException;
import org.sodfs.storage.driver.manager.local.updater.SizeUpdateSender;
import org.sodfs.storage.meta.api.FileEntity;

/**
 *
 * @author Roman Kierzkowski
 */
public class LocalFileManager {
    public static final int NOT_ACTIVE = 0;
    public static final int ACTIVE = 1;
    public static final int MOVING = 2;
    
    private String storagePath;
    private int storageId;
    
    private HashMap<Integer, ReplicaRecord> localReplicas = new HashMap<Integer, LocalFileManager.ReplicaRecord>();
    private LinkedList<LocalFileWraperFactory> wraperFactories = new LinkedList<LocalFileWraperFactory>();
      
    private GroupCommunicator groupCommunicator;
    private MetaDataServiceInterface mds;
    
    private static final String TIMER_NAME = "LocalFileManagerTimer";
    private Timer sheduler = new Timer(TIMER_NAME, true);
    
    private static final long GARBAGE_COLLECTION = Clock.MINUTE;
    private static final long MOVE_COLLECTION = 7 * Clock.SECOND;
    private GarbageFilesRemover garbageFileRemover = new GarbageFilesRemover();
    private MovedFilesRemover movedFileCollector = new MovedFilesRemover();
    private SizeUpdateSender sizeUpdateSender = new SizeUpdateSender();
    
    private static Logger logger = Logger.getLogger(LocalFileManager.class.getName());
    
    public LocalFileManager(int storageId, String storagePath) {
        this.storageId = storageId;
        this.storagePath = storagePath;
        InformantFactory inf = new InformantFactory();
        AnnouncerFactory ann = new AnnouncerFactory();        
        wraperFactories.add(inf);
        wraperFactories.add(ann);        
    }
    
    /* DEPENDIENCES */

    public void setGroupCommunicator(GroupCommunicator groupCommunicator) {
        this.groupCommunicator = groupCommunicator;
    }

    public void setMetaDataService(MetaDataServiceInterface msd) {
        this.mds = msd;
        sizeUpdateSender.setMetaDataService(mds);
    }
    
    /* WRAPER FACTORIES REGISTRATION */
    
    public void addLocalFileWraperFactory(LocalFileWraperFactory factory) {
        wraperFactories.add(factory);
    }
    
    public void removeLocalFileWraperFactory(LocalFileWraperFactory factory) {
        wraperFactories.remove(factory);
    }
    
    public void start() throws MetaDataServiceNotAvilableException {
        sizeUpdateSender.start();
        ReplicaEntity[] vr = mds.getValidReplicas(storageId);
        if (vr != null) {
            for (int i = 0; i < vr.length; i++) {
                boolean isActiveOrMoving = vr[i].getStatus() == ACTIVE || 
                                           vr[i].getStatus() == MOVING;
                FileEntity fe = vr[i].getFileEntity();                        
                load(fe.getNodeId(), fe.getMinNOR(), fe.getPinTime(), isActiveOrMoving);
            }
        }        
        sheduler.scheduleAtFixedRate(garbageFileRemover, 0, GARBAGE_COLLECTION);
        sheduler.scheduleAtFixedRate(movedFileCollector, 0, MOVE_COLLECTION);
    }
    
    public Set<Integer> getLocalReplicasList() {
        synchronized(localReplicas) {
            HashSet<Integer> result = new HashSet<Integer>(localReplicas.keySet());
            return result;
        }
    }
    
    private void load(int fileId, int minNOR, long pinTime, boolean isActiveOrMoving) throws MetaDataServiceNotAvilableException {
        try {
            synchronized(localReplicas) {
                JChannel controlChannel = groupCommunicator.getControlChannel(fileId);
                ReplicationControler rc = new ReplicationControler(fileId, storageId, controlChannel);
                if (rc.tryReturn(isActiveOrMoving, minNOR)) {
                    ReplicaStateManager rsm = new ReplicaStateManager(fileId, ReplicaState.CONSISTENT);
                    File file = new File(storagePath + File.separator + fileId);                        
                    JChannel dataChannel = groupCommunicator.getDataChannel(fileId);
                    UpdateWriter uw = new UpdateWriter(fileId, dataChannel, file.getPath(), rsm, sizeUpdateSender);
                    rsm.addStateChangeListener(uw);
                    uw.start();
                    rc.join();                    
                    ReplicaRecord rr = new ReplicaRecord(rsm, rc, uw, pinTime);
                    rr.pin();
                    localReplicas.put(fileId, rr);
                    if(!isActiveOrMoving) mds.activateReplica(storageId, fileId);                                       
                } else {
                    rc.close();
                }                
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Replica file is probably coliding with directory.", ex);
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, "Unable to connect to meta-server.", ex);
        } catch (ChannelException ex) {
            logger.log(Level.SEVERE, "Group communication failed.", ex);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Thread was unexpectedly iterrupted.", ex);
        }    
    }
    
    public void create(int fileId, int minNOR, long pinTime) {
        try {
            synchronized(localReplicas) {
                if (!localReplicas.containsKey(fileId)) {
                    if (mds.registerReplica(storageId, fileId)) {
                        ReplicaStateManager rsm = new ReplicaStateManager(fileId, ReplicaState.CONSISTENT);
                        JChannel controlChannel = groupCommunicator.getControlChannel(fileId);                        
                        ReplicationControler rc = new ReplicationControler(fileId, storageId, controlChannel);                        
                        File file = new File(storagePath + File.separator + fileId);
                        JChannel dataChannel = groupCommunicator.getDataChannel(fileId);                        
                        UpdateWriter uw = new UpdateWriter(fileId, dataChannel, file.getPath(), rsm, sizeUpdateSender);
                        rsm.addStateChangeListener(uw);
                        uw.start();
                        rc.initiate(minNOR);
                        ReplicaRecord rr = new ReplicaRecord(rsm, rc, uw, pinTime);
                        rr.pin();
                        localReplicas.put(fileId, rr);
                        mds.activateReplica(storageId, fileId);
                    }                
                }
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Replica file is probably coliding with directory.", ex);
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, "Unable to connect to meta-server.", ex);
        } catch (ChannelException ex) {
            logger.log(Level.SEVERE, "Group communication failed.", ex);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Thread was unexpectedly iterrupted.", ex);
        }  
    }
    
    public boolean replicate(int fileId, long pinTime, boolean move, int orginStorageId) {     
        boolean result = false;
        try {           
           synchronized(localReplicas) {                
                if (!localReplicas.containsKey(fileId)) {                    
                    if (mds.registerReplica(storageId, fileId)) {
                        System.out.println("REGISTERED: " + fileId);
                        ReplicaStateManager rsm = new ReplicaStateManager(fileId, ReplicaState.INCONSISTENT);
                        JChannel controlChannel = groupCommunicator.getControlChannel(fileId);
                        ReplicationControler rc = new ReplicationControler(fileId, storageId, controlChannel);                        
                        File file = new File(storagePath + File.separator + fileId);                        
                        JChannel dataChannel = groupCommunicator.getDataChannel(fileId);
                        UpdateWriter uw = new UpdateWriter(fileId, dataChannel, file.getPath(), rsm, sizeUpdateSender);
                        rsm.addStateChangeListener(uw);
                        uw.start();                        
                        ReplicaRecord rr = new ReplicaRecord(rsm, rc, uw, pinTime);
                        rr.pin();
                        localReplicas.put(fileId, rr);
                        rc.join();
                        mds.activateReplica(storageId, fileId);
                        result = true;
                    }                
                } else {
                    if (move) mds.activateReplica(orginStorageId, fileId);
                }
            }            
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Replica file is probably coliding with directory.", ex);
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, "Unable to connect to meta-server.", ex);
        } catch (ChannelException ex) {
            logger.log(Level.SEVERE, "Group communication failed.", ex);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Thread was unexpectedly iterrupted.", ex);
        }
        return result;
    }     
    
    private boolean remove(int fileId) {
        boolean successful = false;
        synchronized (localReplicas) {            
            if (localReplicas.containsKey(fileId)) {
                ReplicaRecord replica = localReplicas.get(fileId);
                ReplicationControler rc = replica.getReplicationControler();
                rc.close();
                ReplicaStateManager rsm = replica.getReplicaStateManager();
                rsm.reportRemoval();
                replica.getUpdateWriter().stop();
                File file = new File(storagePath + File.separator + fileId);
                successful = file.delete();
                localReplicas.remove(fileId);
            }
        }
        return successful;
    }
    
    public boolean move(int fileId, int destignationStorage) {
        boolean successful = false;    
        try {
            synchronized (localReplicas) {
                ReplicaRecord replica = localReplicas.get(fileId);
                if (replica != null && !replica.isPined()) {
                    successful = mds.markReplicaMoving(storageId, fileId, destignationStorage);
                }
            }

        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, "Unable to connect to meta-server.", ex);            
        }
        return successful;
    }      
      
    private boolean finishMove(int fileId, int destignationStorage) {
        boolean successful = false;
        try {        
            synchronized(localReplicas) {
                ReplicaRecord replica = localReplicas.get(fileId);
                if (replica != null) {
                    ReplicationControler rc = replica.getReplicationControler();
                    if (rc.tryLeave()) {
                        System.out.println("FINISHED MOVE: " + fileId);
                        mds.removeReplica(storageId, fileId);
                        ReplicaStateManager rsm = replica.getReplicaStateManager();
                        rsm.reportMove(destignationStorage);
                        replica.getUpdateWriter().stop();
                        rc.close();
                        File file = new File(storagePath + File.separator + fileId);
                        successful = file.delete();
                        localReplicas.remove(fileId);
                    }
                }
            }                
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, "Unable to connect to meta-server.", ex);
        } catch (ChannelException ex) {
            logger.log(Level.SEVERE, "Group communication failed.", ex);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Thread was unexpectedly iterrupted.", ex);
        }
        return successful;        
    }
    
    public boolean dereplicate(int fileId) {
        boolean successful = false;
        try {        
            synchronized(localReplicas) {
                ReplicaRecord replica = localReplicas.get(fileId);
                if (replica != null && !replica.isPined()) {
                    ReplicationControler rc = replica.getReplicationControler();
                    if (rc.tryLeave()) {
                        System.out.println("DEREPLICATE: " + fileId);
                        mds.removeReplica(storageId, fileId);
                        ReplicaStateManager rsm = replica.getReplicaStateManager();
                        rsm.reportDereplication();
                        rc.close();
                        replica.getUpdateWriter().stop();                        
                        File file = new File(storagePath + File.separator + fileId);
                        file.delete();
                        localReplicas.remove(fileId);
                        successful = true;
                    }
                }
            }                
        } catch (MetaDataServiceNotAvilableException ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ChannelException ex) {
            logger.log(Level.SEVERE, "Group communication failed.", ex);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Thread was unexpectedly iterrupted.", ex);
        }
        return successful;
    }
    
    public boolean isOwning(int fileId) {
        synchronized (localReplicas) {
            return localReplicas.containsKey(fileId); 
        }        
    }
    
    public MovableFileInterface getLocalReplica(int fileId) throws MovableFileException {
        MovableFileInterface result = null;
        ReplicaRecord fr = null;
//        System.out.println("FETCH LOCAL REPLICA - start: " + fileId);
        synchronized (localReplicas) {
//            System.out.println("FETCH LOCAL REPLICA - lock on localReplicas: " + fileId);
            fr = localReplicas.get(fileId);         
            //System.out.println("FETCH LOCAL REPLICA - get local replica: " + fileId);
            if (fr != null)  {                
                ReplicaState state = fr.getReplicaStateManager().getState();
                //System.out.println("FETCH LOCAL REPLICA - get state: " + fileId);
                checkState(fileId, state, storageId);
                //System.out.println("FETCH LOCAL REPLICA - state check: " + fileId);
                File file = new File(storagePath + File.separator + fileId);
                result = new LocalFile(file.getAbsolutePath(), fr.getUpdateWriter(), storageId);
                //System.out.println("FETCH LOCAL REPLICA - local file: " + fileId);
                result = wrap(result, fileId);
               // System.out.println("FETCH LOCAL REPLICA - wraped: " + fileId);
            } else {
                //System.out.println("FETCH LOCAL REPLICA - not existing replica: " + fileId);
                throw new NotExistingReplicaException(storageId, fileId);
            }
        }
        return result;
    }

    private MovableFileInterface wrap(MovableFileInterface replica, int fileId) {
        MovableFileInterface result = replica;
        Iterator<LocalFileWraperFactory> it = wraperFactories.iterator();
        while (it.hasNext()) {
            LocalFileWraperFactory factory = it.next();
            result = factory.wrap(result, storageId, fileId);
        }
        return result;
    }
    
    private void checkState(int fileId, ReplicaState state, int destinationStorageId) throws MovableFileException {            
        MovableFileException exception = null;
        switch (state) {
            case DEREPLICATED: 
                exception = new DereplicatedReplicaException(storageId, fileId);
            break;                
            case FAULTY:
                exception = new FaultyReplicaException(storageId, fileId);
            break;
            case INCONSISTENT:
                exception = new InconsistentReplicaException(storageId, fileId);
            break; 
            case MOVED:
                exception = new MovedReplicaException(storageId, fileId, destinationStorageId);
            break;
            case NOT_EXISTING:
                exception = new NotExistingReplicaException(storageId, fileId);
            break;
        }
        if (exception != null) throw exception;
    }
    
    /* FILE RECORD */
    
    private class ReplicaRecord {
        private ReplicaStateManager replicaStateManager;
        private ReplicationControler replicationControler;
        private UpdateWriter updateWriter;
        private long pinTime;
        private Clock clock = Clock.getInstance();
        
        private long notDereplicableTimestamp;

        public ReplicaRecord(ReplicaStateManager replicaStateManager, ReplicationControler replicationControler, UpdateWriter updateWriter, long pinTime) {
            this.replicaStateManager = replicaStateManager;
            this.replicationControler = replicationControler;
            this.updateWriter = updateWriter;
            this.pinTime = pinTime;
        }        
        
        public UpdateWriter getUpdateWriter() {
            return updateWriter;
        }

        public ReplicaStateManager getReplicaStateManager() {
            return replicaStateManager;
        }

        public ReplicationControler getReplicationControler() {
            return replicationControler;
        }
        
        public void pin() {
            notDereplicableTimestamp = clock.getCurrentTime() + pinTime;
        }
        
        public boolean isPined() {
            return notDereplicableTimestamp > clock.getCurrentTime();
        }
    }
    
    /* ANNOUNCER */
    
    private class Announcer implements MovableFileInterface, ReplicaStateChangeListener { 
        private MovableFileInterface replica;
        
        private ReplicaStateManager replicaStateManager;
        private ReplicaState state;
        private int destinationStorageId;
        
        int fileId;

        public Announcer(MovableFileInterface replica, ReplicaStateManager replicaStateManager, int fileId) {
            this.replica = replica;
            this.replicaStateManager = replicaStateManager;
            this.fileId = fileId;
            synchronized (replicaStateManager) {
                this.state = replicaStateManager.getState();
                this.destinationStorageId = replicaStateManager.getDestinationStorageId();
                replicaStateManager.addStateChangeListener(this);
            }
        }
        
        public void openFile(boolean createFlag) throws RemoteException, IOException, MovableFileException {
            synchronized(this) {
                checkState();
                replica.openFile(createFlag);
            }
        }

        public byte[] readFile(int len, long fileOff) throws RemoteException, IOException, MovableFileException {
            synchronized(this) {
                checkState();
                return replica.readFile(len, fileOff);
            }
        }

        public int writeFile(byte[] buf, long fileOff) throws RemoteException, IOException, MovableFileException {
            synchronized(this) {
                checkState();
                return replica.writeFile(buf, fileOff);
            }
        }

        public long seekFile(long pos, int typ) throws RemoteException, IOException, MovableFileException {
            synchronized(this) {
                checkState();
                return replica.seekFile(pos, typ);
            }
        }

        public void flushFile() throws RemoteException, IOException, MovableFileException {
            synchronized(this) {
                checkState();
                replica.flushFile();
            }
        }

        public void truncateFile(long siz) throws RemoteException, IOException, MovableFileException {
            synchronized(this) {
                checkState();
                replica.truncateFile(siz);
            }
        }

        public void closeFile() throws RemoteException, IOException, MovableFileException {
            synchronized(this) {                
                checkClosePossible();                
                replica.closeFile();
            }
            replicaStateManager.removeStateChangeListener(this);
        }

        public int getStorageId() throws RemoteException, MovableFileException {
            synchronized(this) {                
                checkState();                
                return replica.getStorageId();
            }            
        }

        public void replicaStateChanged(ReplicaState state, int destinationStorageId) {
            boolean unregister = false;
            synchronized(this) {                
                this.destinationStorageId = destinationStorageId;
                boolean isOpen = (this.state == ReplicaState.CONSISTENT) || 
                                 (this.state == ReplicaState.INCONSISTENT);
                boolean becomeUnavilable = state == ReplicaState.DEREPLICATED ||
                                           state == ReplicaState.FAULTY ||
                                           state == ReplicaState.MOVED ||
                                           state == ReplicaState.NOT_EXISTING;
                this.state = state;
                if (isOpen && becomeUnavilable) {
                    try {
                        replica.closeFile();
                        unregister = true;
                    } catch (Exception ex) {
                        logger.log(Level.WARNING, "Unable to close file " + fileId + " replica ", ex);                        
                    } 
                }                
            }
            //if (unregister) replicaStateManager.removeStateChangeListener(this);
        }

        private void checkClosePossible() throws MovableFileException {            
            MovableFileException exception = null;
            switch (state) {
                case DEREPLICATED: 
                    exception = new DereplicatedReplicaException(storageId, fileId);
                break;                
                case FAULTY:
                    exception = new FaultyReplicaException(storageId, fileId);
                break;
                case MOVED:
                    exception = new MovedReplicaException(storageId, fileId, destinationStorageId);
                break;
                case NOT_EXISTING:
                    exception = new NotExistingReplicaException(storageId, fileId);
                break;
            }
            if (exception != null) throw exception;
        }
        
        private void checkState() throws MovableFileException {            
            MovableFileException exception = null;
            switch (state) {
                case DEREPLICATED: 
                    exception = new DereplicatedReplicaException(storageId, fileId);
                break;                
                case FAULTY:
                    exception = new FaultyReplicaException(storageId, fileId);
                break;
                case INCONSISTENT:
                    exception = new InconsistentReplicaException(storageId, fileId);
                break; 
                case MOVED:
                    exception = new MovedReplicaException(storageId, fileId, destinationStorageId);
                break;
                case NOT_EXISTING:
                    exception = new NotExistingReplicaException(storageId, fileId);
                break;
            }
            if (exception != null) throw exception;
        }
    }
    
    /* ANNONCER FACTORY */
    
    private class AnnouncerFactory implements LocalFileWraperFactory {        
        public MovableFileInterface wrap(MovableFileInterface replica, int storageId, int fileId) {            
            ReplicaRecord fr = localReplicas.get(fileId);
            ReplicaStateManager rsm = fr.getReplicaStateManager();
            Announcer result = new Announcer(replica, rsm, fileId);
            return result;          
        }
        
    }
    
    /* INFORMANT */
    
    private class Informant implements MovableFileInterface {
        private MovableFileInterface replica;        
        private ReplicaStateManager replicaStateManager;
        private int fileId;
        
        public Informant(MovableFileInterface replica, ReplicaStateManager replicaStateManager, int fileId) {
            this.replica = replica;
            this.replicaStateManager = replicaStateManager;
            
        }

        public void openFile(boolean createFlag) throws RemoteException, IOException, MovableFileException {
            try {
                replica.openFile(createFlag);
            } catch (IOException e) {
                replicaStateManager.reportFailure();
                throw new FaultyReplicaException(storageId, fileId, e);
            }
        }

        public byte[] readFile(int len, long fileOff) throws RemoteException, IOException, MovableFileException {
            try {
                return replica.readFile(len, fileOff);
            } catch (IOException e) {
                replicaStateManager.reportFailure();
                throw new FaultyReplicaException(storageId, fileId, e);
            }               
        }

        public int writeFile(byte[] buf, long fileOff) throws RemoteException, IOException, MovableFileException {
            try {
                return replica.writeFile(buf, fileOff);
            } catch (IOException e) {
                replicaStateManager.reportFailure();
                throw new FaultyReplicaException(storageId, fileId, e);
            }
        }

        public long seekFile(long pos, int typ) throws RemoteException, IOException, MovableFileException {
            try {
                return replica.seekFile(pos, typ);
            } catch (IOException e) {
                replicaStateManager.reportFailure();
                throw new FaultyReplicaException(storageId, fileId, e);
            }
        }

        public void flushFile() throws RemoteException, IOException, MovableFileException {
            try {
                replica.flushFile();
            } catch (IOException e) {
                replicaStateManager.reportFailure();
                throw new FaultyReplicaException(storageId, fileId, e);
            }
        }

        public void truncateFile(long siz) throws RemoteException, IOException, MovableFileException {
            try {
                replica.truncateFile(siz);
            } catch (IOException e) {
                replicaStateManager.reportFailure();
                throw new FaultyReplicaException(storageId, fileId, e);
            }
        }

        public void closeFile() throws RemoteException, IOException, MovableFileException {
            try {
                replica.closeFile();
            } catch (IOException e) {
                replicaStateManager.reportFailure();
                throw new FaultyReplicaException(storageId, fileId, e);
            }
        }

        public int getStorageId() throws RemoteException, MovableFileException {
            return replica.getStorageId();
        }
    }
    
    /* INFORMANT FACTORY */
    
    private class InformantFactory implements LocalFileWraperFactory {        
        public MovableFileInterface wrap(MovableFileInterface replica, int storageId, int fileId) {            
            ReplicaRecord fr = localReplicas.get(fileId);
            ReplicaStateManager rsm = fr.getReplicaStateManager();
            Informant result = new Informant(replica, rsm, fileId);
            return result;          
        }        
    }
    
    /* GARBAGE FILES REMOVER */
    
    private class GarbageFilesRemover extends TimerTask {
        @Override
        public void run() {
            try {
                File storageDir = new File(storagePath);
                File[] files = storageDir.listFiles(new NumberFileFilter());                
                ReplicaEntity[] vr = mds.getValidReplicas(storageId);
                if (vr != null) {                    
                    Set<Integer> validReplicas = new HashSet<Integer>();
                    for (int i = 0; i < vr.length; i++) {
                        validReplicas.add(vr[i].getReplicaPKEntity().getFile());
                    }
                    for (int i = 0; i < files.length; i++) {                        
                        String name = files[i].getName();                        
                        int fileId = Integer.parseInt(name);
                        if (!validReplicas.contains(fileId)) {
                            if (isOwning(fileId)) {
                                remove(fileId);
                            } else {
                                files[i].delete();
                            }
                        }
                    }
                }
            } catch (MetaDataServiceNotAvilableException ex) {
                logger.log(Level.SEVERE, "Unable to connect to meta server.", ex);
            }             
        }        
    }
    
    /* MOVED FILES COLLETOR */
    
    private class MovedFilesRemover extends TimerTask {
        @Override
        public void run() {
            try {
                ReplicaEntity[] replicas = mds.getMovedReplicas(storageId);
                if (replicas != null) {                    
                    for (int i = 0; i < replicas.length; i++) {                        
                        ReplicaEntity r = replicas[i];
                        int fileId = r.getReplicaPKEntity().getFile();
                        int destination = r.getDestination().getStorageId();                        
                        finishMove(fileId, destination);                        
                    }
                }
            } catch (MetaDataServiceNotAvilableException ex) {
                logger.log(Level.SEVERE, "Unable to connect to meta server.", ex);
            }
        }        
    }
}
