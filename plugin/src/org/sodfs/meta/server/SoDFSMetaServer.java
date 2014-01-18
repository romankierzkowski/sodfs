package org.sodfs.meta.server;

import org.sodfs.meta.api.MetaServerInterface;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.jgroups.JChannel;
import org.sodfs.utils.RegistryUtil;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSMetaServer {
    
    public static final String META_SERVER_REMOTE_OBJECT_NAME = "metaServer";
    public static final String DEFAULT_PERSISTNACE_UNIT ="SoDFSPU";
    public static int DEFAULT_REGISTRY_PORT = 1099;
    
    public static MetaServerRemoteObject remoteObject;
    public static JChannel initialHost; 
    
    public static void main(String args[]) {
        setUpSecurityManager();
        String persitanceUnit = (args.length > 0)?args[0]:DEFAULT_PERSISTNACE_UNIT;
        
        Registry rmiRegistry = RegistryUtil.getRMIRegistry(DEFAULT_REGISTRY_PORT);        
        remoteObject = new MetaServerRemoteObject(persitanceUnit);
        if (rmiRegistry != null) {
            try {
                MetaServerInterface stub = (MetaServerInterface) UnicastRemoteObject.exportObject(remoteObject,0);
                rmiRegistry.rebind(META_SERVER_REMOTE_OBJECT_NAME, stub);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void setUpSecurityManager() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
    }
}
