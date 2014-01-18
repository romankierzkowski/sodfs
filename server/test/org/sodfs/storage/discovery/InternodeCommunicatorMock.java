package org.sodfs.storage.discovery;

import org.sodfs.storage.discovery.*;
import java.util.Map;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;

/**
 *
 * @author Roman Kierzkowski
 */
public class InternodeCommunicatorMock extends InternodeCommunicator {
    private Map<Integer, Integer> delays;

    public InternodeCommunicatorMock(Map<Integer, Integer> delays) {
        super("foo","fooo", 10);
        //InternodeCommunicator i = new InternodeCommunicator(remoteObjectName, sotrageAddress, regPort)
        this.delays = delays;        
    }

    @Override
    public StorageServerInterface getRemoteStorageServerInterface(int storageId) throws MetaDataServiceNotAvilableException {
        StorageServerInterface result = null;
        if (delays.containsKey(storageId)) result = new StorageServerInterfaceMock(delays.get(storageId));
        return result;
    }    
}
