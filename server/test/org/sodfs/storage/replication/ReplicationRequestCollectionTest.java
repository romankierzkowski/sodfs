package org.sodfs.storage.replication;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicationRequestCollectionTest {

    public ReplicationRequestCollectionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of addReplicationRequest method, of class ReplicationRequestCollection.
     */
    @Test
    public void addReplicationRequest() {
        int messageOrgin = 1;
        int messageDestignation = 2;
        int fileId1 = 1;
        int fileId2 = 2;
        int fileId3 = 3;
        float evaluation = 0.3f;
        ReplicationRequest msg1 = new ReplicationRequest(messageOrgin, messageDestignation, fileId1, evaluation);
        ReplicationRequest msg2 = new ReplicationRequest(messageOrgin, messageDestignation, fileId2, evaluation);
        ReplicationRequest msg3 = new ReplicationRequest(messageOrgin, messageDestignation, fileId2, evaluation);
        ReplicationRequest msg4 = new ReplicationRequest(messageOrgin, messageDestignation, fileId3, evaluation);
        ReplicationRequestCollection instance = new ReplicationRequestCollection();
        instance.addReplicationRequest(msg1);
        instance.addReplicationRequest(msg2);
        instance.addReplicationRequest(msg3);
        instance.addReplicationRequest(msg4);
        assertEquals(1, instance.getRequestsList(fileId1).size());
        assertTrue(instance.getRequestsList(fileId1).contains(msg1));
        assertEquals(2, instance.getRequestsList(fileId2).size());
        assertTrue(instance.getRequestsList(fileId2).contains(msg2));
        assertTrue(instance.getRequestsList(fileId2).contains(msg3));
        assertEquals(1, instance.getRequestsList(fileId3).size());
        assertTrue(instance.getRequestsList(fileId3).contains(msg4));
        assertEquals(3, instance.getRequestedFiles().size());
        assertTrue(instance.getRequestedFiles().contains(fileId1));
        assertTrue(instance.getRequestedFiles().contains(fileId2));
        assertTrue(instance.getRequestedFiles().contains(fileId3));
    }

}