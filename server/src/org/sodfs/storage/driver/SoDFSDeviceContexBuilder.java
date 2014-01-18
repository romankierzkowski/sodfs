package org.sodfs.storage.driver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.sodfs.meta.api.MetaDataManipulatorInterface;
import org.sodfs.meta.api.MetaServerInterface;
import org.sodfs.storage.communication.GroupCommunicator;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.discovery.NetworkDiscoverer;
import org.sodfs.storage.driver.config.MetaServerAddress;
import org.sodfs.storage.driver.config.SORPAConfig;
import org.sodfs.storage.driver.config.StorageServerConfig;
import org.sodfs.storage.driver.manager.FileManager;
import org.sodfs.storage.excludes.ExcludeManager;
import org.sodfs.storage.meta.MetaDataCache;
import org.sodfs.storage.meta.MetaDataManager;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.StorageServerEntity;
import org.sodfs.storage.replication.ReplicaPlacementManager;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSDeviceContexBuilder {
    private ReplicaPlacementManager rpm;
    private NetworkDiscoverer nd;
    private InternodeCommunicator inc;
    private GroupCommunicator gc;
    private FileManager fm;
    private ExcludeManager em;
    private MetaDataServiceInterface  mdc;
    private StorageServerEntity sse;    
    private SoDFSDeviceContext context = new SoDFSDeviceContext();
    private Clock clock = Clock.getInstance();

    SoDFSDeviceContext getContext(MetaServerAddress msa, StorageServerConfig ssc, SORPAConfig sorpa, String shareName) throws MetaDataServiceNotAvilableException, RemoteException {
        initiateMetaDataService(msa);
        registerStorageServer(ssc);
        initiateFileManager(ssc.getStoragePath(), sorpa.getPinTime());
        initiateInternodeCommunicator();
        initiateGroupCommunicator(ssc.getMulticastAddress(), ssc.getMulticastPort(), ssc.getName());
        initiateNetworkDiscoverer();
        initiateExcludeManager();
        initiateReplicaPlacementManager(sorpa);        
        setDependiences();
        context.setShareName(shareName);
        return context;
    }

    private void initiateMetaDataService(MetaServerAddress msa) throws MetaDataServiceNotAvilableException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        MetaServerInterface msi = null;
        try {
            Registry registry = LocateRegistry.getRegistry(msa.getAddress(), msa.getPort());
            msi = (MetaServerInterface) registry.lookup(msa.getName());        
            MetaDataManipulatorInterface manip = msi.getMetaDataManipulator();
            long started = clock.getCurrentTime();
            long remoteTime = msi.getTime();
            long finished = clock.getCurrentTime();
            long delta = finished - started;
            long correction = remoteTime + (delta / 2) - finished ;
            clock.setCorrection(correction);            
            MetaDataManager mdm = new MetaDataManager(manip);
            mdc = new MetaDataCache(mdm);
            context.setMetaDataService(mdc);
        } catch (Exception e) {
            throw new MetaDataServiceNotAvilableException(e);
        }
    }

    private void registerStorageServer(StorageServerConfig conf) throws MetaDataServiceNotAvilableException {
        MetaDataServiceInterface mds = context.getMetaDataService();
        sse = mds.registerStorageServer(conf.getName(), conf.getAddress(), conf.getPort(), conf.getMulticastAddress(), conf.getMulticastPort());
        context.setStorageId(sse.getStorageId());
    }

    private void initiateExcludeManager() {
        em = new ExcludeManager();
        context.setExcludeManager(em);
    }

    private void initiateFileManager(String storagePath, long pinTime) {
        fm = new FileManager(context.getStorageId(), storagePath, pinTime);
        context.setFileManager(fm);
    }

    private void initiateGroupCommunicator(String multicastAddress, int multicastPort, String storageName) {
        gc = new GroupCommunicator(multicastAddress, multicastPort, storageName);
        context.setGroupCommunicator(gc);
    }

    private void initiateInternodeCommunicator() {
        inc = new InternodeCommunicator(sse.getName(), sse.getAddress(), sse.getPort());
        context.setInternodeCommunicator(inc);
    }

    private void initiateNetworkDiscoverer() {
        nd = new NetworkDiscoverer(context.getStorageId());
        context.setNetworkDiscoverer(nd);
    }

    private void initiateReplicaPlacementManager(SORPAConfig conf) {
        rpm = new ReplicaPlacementManager(context.getStorageId(), conf.getPc(), 
                   conf.getK(), conf.getTTL(), conf.getPinTime(), conf.getMinNOR(), conf.getRF(), conf.getDRF(), conf.getAF(), conf.getMF());        
        context.setReplicaPlacementManager(rpm);
    }
    
    private void setDependiences() {
        rpm.setFileManager(fm);
        rpm.setInternodeCommunicator(inc);
        rpm.setNetworkDiscoverer(nd);
        
        nd.setInternodeCommunicator(inc);
        nd.setMetaDataServiceInterface(mdc);
        
        inc.setMetaDataService(mdc);
        inc.setFileManager(fm);
        inc.setReplicaPlacementManager(rpm);
        
        gc.setMetaDataService(mdc);
        
        fm.setGroupCommunicator(gc);
        fm.setInternodeCommuniactor(inc);
        fm.setMetaDataService(mdc);
        fm.setExcludeManager(em);
        fm.setNetworkDiscoverer(nd);        

    }
}
