package org.sodfs.storage.replication;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.driver.manager.FileManager;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.utils.Clock;
import static org.junit.Assert.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoRPAAlgorithmTest {

    public SoRPAAlgorithmTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of execute method, of class SoRPAAlgorithm.
     */
    @Test
    public void execute() throws MetaDataServiceNotAvilableException, RemoteException {
        int localStorage = 1;
        int remoteStorage = 2;
        int dataAmount= 4096;
        long TTL = 1000;
        Clock clock = Clock.getInstance();
        
        InternodeCommunicator internodeCommunicator= EasyMock.createStrictMock(InternodeCommunicator.class);
        StorageServerInterface ssi = org.easymock.EasyMock.createStrictMock(StorageServerInterface.class);
        FileManager fileManager = EasyMock.createStrictMock(FileManager.class);
        Set<Integer> set = new HashSet<Integer>();        
        set.add(1); set.add(2); set.add(4); set.add(5);        
        expect(fileManager.getLocalReplicasList()).andReturn(set);     
        
        ArrayList<Coin> coins = new ArrayList<Coin>();        
        addCoin(0, 5, 4, dataAmount, clock, localStorage, TTL, coins);
        addCoin(1, 4, 3, dataAmount, clock, localStorage, TTL, coins);
        addCoin(2, 3, 1, dataAmount, clock, localStorage, TTL, coins);        
        addCoin(3, 2, 1, dataAmount, clock, localStorage, TTL, coins);        
        addCoin(4, 1, 3, dataAmount, clock, localStorage, TTL, coins);        
        
        ReplicationRequestCollection requests = new ReplicationRequestCollection();
        requests.addReplicationRequest(new ReplicationRequest(6, localStorage, 1, 0.1f));
        requests.addReplicationRequest(new ReplicationRequest(7, localStorage, 2, 0.81f));
        requests.addReplicationRequest(new ReplicationRequest(8, localStorage, 4, 14.4f));
        requests.addReplicationRequest(new ReplicationRequest(9, localStorage, 4, 14.4f));        
        
         

        expect(internodeCommunicator.getRemoteStorageServerInterface(4)).andReturn(ssi);
        ssi.sendRequest(isA(ReplicationRequest.class));
        
        expect(internodeCommunicator.getRemoteStorageServerInterface(7)).andReturn(ssi);
        ssi.sendOrder(isA(ReplicationOrder.class));
        
        expect(internodeCommunicator.getRemoteStorageServerInterface(8)).andReturn(ssi);
        ssi.sendOrder(isA(ReplicationOrder.class));
        expect(fileManager.move(4, 8)).andReturn(true);
        
        expect(internodeCommunicator.getRemoteStorageServerInterface(9)).andReturn(ssi);
        ssi.sendOrder(isA(ReplicationOrder.class));
        
        expect(fileManager.dereplicate(5)).andReturn(true); 
              
        //expectLastCall();
        replay(fileManager);
        replay(internodeCommunicator);
        replay(ssi);        
        
        SoRPAAlgorithm instance = new SoRPAAlgorithm(localStorage, fileManager, internodeCommunicator, 0.5f, 0.3f, 0.8f, 1.25f);
        instance.execute(coins, requests);
        
        verify(fileManager);
        verify(internodeCommunicator);
        verify(ssi);   
    }

    private void addCoin(int fileId, int times, int source,int dataAmount, Clock clock, int localStorage, long TTL, ArrayList<Coin> coins) {
        while (times-->0) {
            Coin coin = new Coin(fileId, source, localStorage, OperationType.READ, dataAmount, TTL, clock);
            coin.incrementHopCounter();
            coins.add(coin);
        }
    }

}