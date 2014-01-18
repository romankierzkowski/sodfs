package org.sodfs.storage.discovery;

import org.sodfs.storage.discovery.InternodeCommunicatorMock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.StorageServerEntity;
/**
 *
 * @author Roman
 */
public class NetworkDiscovererTest {

    public NetworkDiscovererTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of orderFromClosestToFarest method, of class NetworkDiscoverer.
     */
    @Test
    public void orderFromClosestToFarest() throws MetaDataServiceNotAvilableException {
        int[] replicaHolders = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
        NetworkDiscoverer instance = new NetworkDiscoverer(1);
        Map<Integer, Integer> delays = new HashMap<Integer, Integer>();
        delays.put(1, 1);
        delays.put(6, 1);
        delays.put(11, 1);
        delays.put(4, 5);
        delays.put(9, 5);
        delays.put(14, 5);
        delays.put(13, 12);
        delays.put(8, 12);
        delays.put(3, 12);
        delays.put(2, 100);
        delays.put(7, 100);
        delays.put(12, 100);
        delays.put(5, 300);
        delays.put(10, 300);
        delays.put(15, 300);  
        int[] expectedResult = {1,4,3,6,2,5};
        InternodeCommunicatorMock incm = new InternodeCommunicatorMock(delays);
        instance.setInternodeCommunicator(incm);
        
        MetaDataServiceInterface mds = createMock(MetaDataServiceInterface.class);
        ArrayList<StorageServerEntity> list = new ArrayList<StorageServerEntity>();
        list.add(generateSSEMock(1));
        list.add(generateSSEMock(3));
        list.add(generateSSEMock(2));
        list.add(generateSSEMock(4));
        list.add(generateSSEMock(5));
        list.add(generateSSEMock(6));
        list.add(generateSSEMock(7));
        list.add(generateSSEMock(8));
        list.add(generateSSEMock(9));
        list.add(generateSSEMock(10));
        list.add(generateSSEMock(11));
        list.add(generateSSEMock(12));
        list.add(generateSSEMock(13));
        list.add(generateSSEMock(14));
        list.add(generateSSEMock(15));
        expect(mds.getStorageServers()).andStubReturn(list);
        replay(mds);
        instance.setMetaDataServiceInterface(mds);        
        instance.start();
        System.out.println(Arrays.toString(replicaHolders));
        instance.orderFromClosestToFarest(replicaHolders);
        System.out.println(Arrays.toString(replicaHolders));
//        for (int i = 0; i < expectedResult.length; i++) {
//            assertEquals(expectedResult[i], replicaHolders[i]);
//        }
        System.out.println(Arrays.toString(instance.getNeighbours()));
    }

    private StorageServerEntity generateSSEMock(int storageId) {
        StorageServerEntity sse = createMock(StorageServerEntity.class);
        expect(sse.getStorageId()).andStubReturn(storageId);
        replay(sse);
        return sse;
    }


}