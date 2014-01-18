package org.sodfs.testclient.executor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.sodfs.testclient.commons.Constants;

/**
 *
 * @author Roman Kierzkowski
 */
public class TaskExecutorMain {
    public static TaskExecutor executorRO;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 2) { 
            System.setProperty("java.rmi.server.hostname", args[0]);
            int interval = Integer.parseInt(args[1]);
            try {
                Registry rmiRegistry = getRMIRegistry(Constants.RMI_PORT);     
                if (rmiRegistry != null) {
                    StatisticGenerator statGen = new StatisticGenerator(interval);                    
                    executorRO = new TaskExecutor(statGen);
                    Thread statGenThr = new Thread(statGen);
                    statGenThr.setDaemon(true);
                    statGenThr.start();
                    TaskExecutorInterface stub = (TaskExecutorInterface) UnicastRemoteObject.exportObject(executorRO, 0);
                    rmiRegistry.rebind(Constants.EXECUTOR_NAME, stub);
                } 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("usage: sodfsexecutor <host> <interval>");
        }
    }
    
    public static Registry getRMIRegistry(int port) {
        Registry rmiRegistry = null;
        try {
            rmiRegistry = LocateRegistry.createRegistry(port);
        } catch (RemoteException ex) {
            try {
                rmiRegistry = LocateRegistry.getRegistry(port);
            } catch (RemoteException rex) {
                rex.printStackTrace();
            }
        }
        return rmiRegistry;
    }
}
