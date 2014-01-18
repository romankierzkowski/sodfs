package org.sodfs.storage.driver.manager.local;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicaStateManagerTest {
    public static final int FILE_ID = 1;
    public static final int STORAGE_ID = 1;

    public ReplicaStateManagerTest() {
    }

    /**
     * Test of addStateChangeListener method, of class ReplicaStateManager.
     */
    @Test
    public void addStateChangeListener() {
        ReplicaStateManager instance;
        ReplicaStateChangeListener listener1;
        ReplicaStateChangeListener listener2;
        
        listener1 = getListenerExpectingNotification(ReplicaState.FAULTY, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerExpectingNotification(ReplicaState.FAULTY, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportFailure();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerNotExpectingNotification();
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportFailure();
        verify(listener1);
        verify(listener2); 
        
        listener1 = getListenerExpectingNotification(ReplicaState.DEREPLICATED, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerExpectingNotification(ReplicaState.DEREPLICATED, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportDereplication();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerNotExpectingNotification();
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportDereplication();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerExpectingNotification(ReplicaState.CONSISTENT, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerExpectingNotification(ReplicaState.CONSISTENT, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportConsistance();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerNotExpectingNotification();
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportConsistance();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerExpectingNotification(ReplicaState.INCONSISTENT, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerExpectingNotification(ReplicaState.INCONSISTENT, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportInconsistance();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerNotExpectingNotification();
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportInconsistance();
        verify(listener1);
        verify(listener2);

        listener1 = getListenerExpectingNotification(ReplicaState.NOT_EXISTING, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerExpectingNotification(ReplicaState.NOT_EXISTING, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportRemoval();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerNotExpectingNotification();
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportInconsistance();
        verify(listener1);
        verify(listener2);
        
        
        listener1 = getListenerExpectingNotification(ReplicaState.MOVED, STORAGE_ID);
        listener2 = getListenerExpectingNotification(ReplicaState.MOVED, STORAGE_ID);        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportMove(STORAGE_ID);
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerNotExpectingNotification();
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.reportMove(STORAGE_ID);
        verify(listener1);
        verify(listener2);
    }
    
    
    private ReplicaStateChangeListener getListenerExpectingNotification(ReplicaState state, int destinationStorageId) {
        ReplicaStateChangeListener result;
        result = createMock(ReplicaStateChangeListener.class);
        result.replicaStateChanged(state, destinationStorageId);
        replay(result);
        return result;
    }
    
    private ReplicaStateChangeListener getListenerNotExpectingNotification() {
        ReplicaStateChangeListener result;
        result = createMock(ReplicaStateChangeListener.class);
        replay(result);
        return result;
    }

    /**
     * Test of removeStateChangeListener method, of class ReplicaStateManager.
     */
    @Test
    public void removeStateChangeListener() {
        ReplicaStateManager instance;
        ReplicaStateChangeListener listener1;
        ReplicaStateChangeListener listener2;
        
        listener1 = getListenerExpectingNotification(ReplicaState.FAULTY, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.removeStateChangeListener(listener2);
        instance.reportFailure();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerExpectingNotification(ReplicaState.DEREPLICATED, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.removeStateChangeListener(listener2);
        instance.reportDereplication();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerExpectingNotification(ReplicaState.CONSISTENT, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.removeStateChangeListener(listener2);
        instance.reportConsistance();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerExpectingNotification(ReplicaState.INCONSISTENT, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.removeStateChangeListener(listener2);
        instance.reportInconsistance();
        verify(listener1);
        verify(listener2);

        listener1 = getListenerExpectingNotification(ReplicaState.NOT_EXISTING, ReplicaStateManager.NOT_APPLICABLE_PARAMETER);
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.removeStateChangeListener(listener2);
        instance.reportRemoval();
        verify(listener1);
        verify(listener2);
        
        listener1 = getListenerExpectingNotification(ReplicaState.MOVED, STORAGE_ID);
        listener2 = getListenerNotExpectingNotification();
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.addStateChangeListener(listener1);
        instance.addStateChangeListener(listener2);
        instance.removeStateChangeListener(listener2);
        instance.reportMove(STORAGE_ID);
        verify(listener1);
        verify(listener2);
    }

    /**
     * Test of getDestinationStorageId method, of class ReplicaStateManager.
     */
    @Test
    public void getDestinationStorageId() {
        ReplicaStateManager instance;
        int result;
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        result = instance.getDestinationStorageId();
        assertEquals(ReplicaStateManager.NOT_APPLICABLE_PARAMETER, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        result = instance.getDestinationStorageId();
        assertEquals(ReplicaStateManager.NOT_APPLICABLE_PARAMETER, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.FAULTY);
        result = instance.getDestinationStorageId();
        assertEquals(ReplicaStateManager.NOT_APPLICABLE_PARAMETER, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        result = instance.getDestinationStorageId();
        assertEquals(ReplicaStateManager.NOT_APPLICABLE_PARAMETER, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.NOT_EXISTING);
        result = instance.getDestinationStorageId();
        assertEquals(ReplicaStateManager.NOT_APPLICABLE_PARAMETER, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED, STORAGE_ID);
        result = instance.getDestinationStorageId();
        assertEquals(STORAGE_ID, result);
    }   

    /**
     * Test of reportFailure method, of class ReplicaStateManager.
     */
    @Test
    public void reportFailure() {
        ReplicaStateManager instance;
        ReplicaState result;
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.reportFailure();
        result = instance.getState();
        assertEquals(ReplicaState.FAULTY, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.reportFailure();
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.FAULTY);
        instance.reportFailure();
        result = instance.getState();
        assertEquals(ReplicaState.FAULTY, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.reportFailure();
        result = instance.getState();
        assertEquals(ReplicaState.FAULTY, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.NOT_EXISTING);
        instance.reportFailure();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED, STORAGE_ID);
        instance.reportFailure();
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
    }

    /**
     * Test of reportInconsistance method, of class ReplicaStateManager.
     */
    @Test
    public void reportInconsistance() {
        ReplicaStateManager instance;
        ReplicaState result;
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.CONSISTENT, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.FAULTY);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.FAULTY, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.CONSISTENT, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.NOT_EXISTING);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED, STORAGE_ID);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
    }

    /**
     * Test of reportConsistance method, of class ReplicaStateManager.
     */
    @Test
    public void reportConsistance() {
        ReplicaStateManager instance;
        ReplicaState result;
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.CONSISTENT, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.FAULTY);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.FAULTY, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.CONSISTENT, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.NOT_EXISTING);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED, STORAGE_ID);
        instance.reportConsistance();
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
    }

    /**
     * Test of reportMove method, of class ReplicaStateManager.
     */
    @Test
    public void reportMove() {
        ReplicaStateManager instance;
        ReplicaState result;
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.reportMove(FILE_ID);
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.reportMove(FILE_ID);
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.FAULTY);
        instance.reportMove(FILE_ID);
        result = instance.getState();
        assertEquals(ReplicaState.FAULTY, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.reportMove(FILE_ID);
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.NOT_EXISTING);
        instance.reportMove(FILE_ID);
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED, STORAGE_ID);
        instance.reportMove(FILE_ID);
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
    }

    /**
     * Test of raportDereplication method, of class ReplicaStateManager.
     */
    @Test
    public void raportDereplication() {
        ReplicaStateManager instance;
        ReplicaState result;
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.reportDereplication();
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.reportDereplication();
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.FAULTY);
        instance.reportDereplication();
        result = instance.getState();
        assertEquals(ReplicaState.FAULTY, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.reportDereplication();
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.NOT_EXISTING);
        instance.reportDereplication();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED, STORAGE_ID);
        instance.reportDereplication();
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
    }

    /**
     * Test of reportRemoval method, of class ReplicaStateManager.
     */
    @Test
    public void reportRemoval() {
        ReplicaStateManager instance;
        ReplicaState result;
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.CONSISTENT);
        instance.reportRemoval();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.DEREPLICATED);
        instance.reportRemoval();
        result = instance.getState();
        assertEquals(ReplicaState.DEREPLICATED, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.FAULTY);
        instance.reportRemoval();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.INCONSISTENT);
        instance.reportRemoval();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.NOT_EXISTING);
        instance.reportRemoval();
        result = instance.getState();
        assertEquals(ReplicaState.NOT_EXISTING, result);
        
        instance = new ReplicaStateManager(FILE_ID, ReplicaState.MOVED, STORAGE_ID);
        instance.reportRemoval();
        result = instance.getState();
        assertEquals(ReplicaState.MOVED, result);
    }
}