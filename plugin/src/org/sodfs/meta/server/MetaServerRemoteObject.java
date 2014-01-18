package org.sodfs.meta.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.sodfs.meta.api.MetaDataManipulatorInterface;
import org.sodfs.meta.api.MetaServerInterface;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class MetaServerRemoteObject  implements MetaServerInterface{
    private EntityManagerFactory emf;
    private String persistanceUnit;
    private SizeActualisator sa;
    private Clock clock = Clock.getInstance();
    
    public MetaServerRemoteObject(String persistanceUnit) {
        this.persistanceUnit = persistanceUnit;
        emf = Persistence.createEntityManagerFactory(persistanceUnit);
        sa = new SizeActualisator(emf);
        sa.start();
    }

    public MetaDataManipulatorInterface getMetaDataManipulator() throws RemoteException {
        MetaDataManipulator mdm = new MetaDataManipulator(emf, sa);
        MetaDataManipulatorInterface result = (MetaDataManipulatorInterface) UnicastRemoteObject.exportObject(mdm, 0);        
        return result;
    }

    public long getTime() throws RemoteException {
        long time = clock.getCurrentTime();
        return time;
    }
}
