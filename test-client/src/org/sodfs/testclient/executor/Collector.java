package org.sodfs.testclient.executor;

import org.sodfs.testclient.executor.CollectorInterface;
import org.sodfs.testclient.executor.StatisticGenerator;
import java.rmi.RemoteException;

/**
 *
 * @author Roman Kierzkowski
 */
public class Collector implements CollectorInterface {    
    private StatisticGenerator statGen;

    public Collector(StatisticGenerator statGen) {
        this.statGen = statGen;
    }

    public void writeFinished() throws RemoteException {
        statGen.addWrite();
    }

    public void readFinished() throws RemoteException {
        statGen.addRead();
    }

}
