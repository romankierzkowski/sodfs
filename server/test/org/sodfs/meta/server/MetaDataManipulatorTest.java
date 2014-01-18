package org.sodfs.meta.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.alfresco.jlan.server.filesys.AccessDeniedException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.sodfs.storage.driver.SoDFSPath;
import org.sodfs.storage.meta.api.FileEntity;
import org.sodfs.storage.meta.api.NodeEntity;
import org.sodfs.storage.meta.api.ReplicaEntity;
import org.sodfs.storage.meta.api.StorageServerEntity;
import static org.sodfs.test.common.TestNamespace.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class MetaDataManipulatorTest {
    
    private EntityManagerFactory emf;// = Persistence.createEntityManagerFactory("SoDFSPU_test");
    
    public MetaDataManipulatorTest() {
    }
    
    @Test
    public void createTestDatabase() throws Exception {
        emf = Persistence.createEntityManagerFactory("SoDFSPU_local");
        SizeActualisator sur = new SizeActualisator(emf);
        MetaDataManipulator instance = new MetaDataManipulator(emf, sur);
//        
//        instance.createDirectory(new SoDFSPath(EXISTING_DIRECTORY), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createDirectory(new SoDFSPath(EXISTING_SUBDIRECTORY), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);                
//        instance.createFile(new SoDFSPath(EXISTING_FILE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createFile(new SoDFSPath(EXISTING_FILE_IN_DIR), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createFile(new SoDFSPath(EXISTING_FILE_IN_SUBDIR), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//                
//        instance.createDirectory(new SoDFSPath(ROOT + DIR_TO_RENAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createDirectory(new SoDFSPath(EXISTING_DIRECTORY + SEPARATOR + DIR_TO_RENAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createDirectory(new SoDFSPath(EXISTING_SUBDIRECTORY + SEPARATOR + DIR_TO_RENAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);                
//        
//        instance.createDirectory(new SoDFSPath(ROOT + DIR_TO_DELETE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createDirectory(new SoDFSPath(EXISTING_DIRECTORY + SEPARATOR + DIR_TO_DELETE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createDirectory(new SoDFSPath(EXISTING_SUBDIRECTORY + SEPARATOR + DIR_TO_DELETE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);                
//        
//        instance.createFile(new SoDFSPath(ROOT + FILE_TO_RENAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createFile(new SoDFSPath(EXISTING_DIRECTORY + SEPARATOR + FILE_TO_RENAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createFile(new SoDFSPath(EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_RENAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        
//        instance.createFile(new SoDFSPath(ROOT + FILE_TO_DELETE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createFile(new SoDFSPath(EXISTING_DIRECTORY + SEPARATOR + FILE_TO_DELETE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createFile(new SoDFSPath(EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_DELETE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        
//        instance.createDirectory(new SoDFSPath(DIR_WITH_FILE), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);                
//        instance.createFile(new SoDFSPath(DIR_WITH_FILE + SEPARATOR + FILE_NAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);
//        instance.createDirectory(new SoDFSPath(DIR_WITH_SUBDIR), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);        
//        instance.createDirectory(new SoDFSPath(DIR_WITH_SUBDIR + SEPARATOR + DIR_NAME), ROOT_ID, ROOT_ID, ROOT_ID, ROOT_ID);

    }
    
    @Test
    public void testReplicaRegistration() throws RemoteException {
        emf = Persistence.createEntityManagerFactory("SoDFSPU_local");
        SizeActualisator sur = new SizeActualisator(emf);
        MetaDataManipulator instance = new MetaDataManipulator(emf, sur);        
        StorageServerEntity sse = instance.registerStorageServer("storage1", "localhost", 10, "loclahost", 11);
        int[] replicas;
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(0, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, -2);
        assertEquals(0, replicas.length);
        ReplicaEntity[] list;
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        instance.registerReplica(sse.getStorageId(), EXISTING_FILE_ID);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(0, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, -3);
        assertEquals(0, replicas.length);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(1,list.length);
        list = instance.getValidReplicas(-3);
        assertEquals(0,list.length);
        instance.activateReplica(sse.getStorageId(), EXISTING_FILE_ID);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(0, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, -2);
        assertEquals(1, replicas.length);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(1,list.length);
        instance.removeReplica(sse.getStorageId(), EXISTING_FILE_ID);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(0, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, -2);
        assertEquals(0, replicas.length);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        
        StorageServerEntity sse2 = instance.registerStorageServer("storage2", "localhost", 14, "loclahost", 12);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        instance.registerReplica(sse.getStorageId(), EXISTING_FILE_ID);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(1,list.length);
        instance.activateReplica(sse.getStorageId(), EXISTING_FILE_ID);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(0, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse2.getStorageId());
        assertEquals(1, replicas.length);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(1,list.length);
        list = instance.getMovedReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        assertTrue(instance.markReplicaMoving(sse.getStorageId(), EXISTING_FILE_ID, sse2.getStorageId()));
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(0, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse2.getStorageId());
        assertEquals(1, replicas.length);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(1,list.length);
        list = instance.getMovedReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        instance.registerReplica(sse2.getStorageId(), EXISTING_FILE_ID);        
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(0, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse2.getStorageId());
        assertEquals(1, replicas.length);
        list = instance.getMovedReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        instance.activateReplica(sse2.getStorageId(), EXISTING_FILE_ID);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse.getStorageId());
        assertEquals(1, replicas.length);
        replicas = instance.getStorageServersHoldingFileReplica(EXISTING_FILE_ID, sse2.getStorageId());
        assertEquals(0, replicas.length);
        list = instance.getMovedReplicas(sse.getStorageId());
        assertEquals(1,list.length);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(0,list.length);        
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        instance.removeReplica(sse.getStorageId(), EXISTING_FILE_ID);
        list = instance.getValidReplicas(sse.getStorageId());
        assertEquals(0,list.length);
        instance.removeReplica(sse2.getStorageId(), EXISTING_FILE_ID);
    }
    
    

}