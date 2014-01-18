package org.sodfs.testclient.barrier;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author Roman Kierzkowski
 */
public class Barrier implements BarrierInterface {
    
    private CountDownLatch counter;    

    public Barrier(int count) {
        counter = new CountDownLatch(count);
    }
    
    public BarrierLockerInterface getLocker() throws RemoteException {
        BarierLocker bl = new BarierLocker();
        return (BarrierLockerInterface) UnicastRemoteObject.exportObject(bl, 0);
    }
    
    private class BarierLocker implements BarrierLockerInterface {

        public void register() {            
            counter.countDown();
        }

        public void await() throws InterruptedException {
            counter.await();
        }

    }
}
