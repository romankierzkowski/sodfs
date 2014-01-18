/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.rmi.RemoteException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sodfs.meta.server.MetaDataManipulator;
import org.sodfs.meta.server.SizeActualisator;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.meta.MetaDataManager;
import static org.junit.Assert.*;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.replication.ReplicationOrder;

/**
 *
 * @author Roman
 */
public class Try {

    public Try() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void sendReplicationOrder() throws MetaDataServiceNotAvilableException, RemoteException {        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoDFSPU");
        SizeActualisator sur = new SizeActualisator(emf);
        MetaDataManipulator instance = new MetaDataManipulator(emf, sur);
        MetaDataManager mdm = new MetaDataManager(instance);
        InternodeCommunicator comm = new InternodeCommunicator("foo","foo", 1);
        comm.setMetaDataService(mdm);
        int from = 40;
        int to = 41;
        int fileId = 85;
        StorageServerInterface interf = comm.getRemoteStorageServerInterface(to);
        assertNotNull(interf);
        interf.sendOrder(new ReplicationOrder(from, to, fileId, true));        
    }
}