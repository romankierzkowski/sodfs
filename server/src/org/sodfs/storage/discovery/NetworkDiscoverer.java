package org.sodfs.storage.discovery;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.StorageServerEntity;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class NetworkDiscoverer {
    private InternodeCommunicator inc;
    private MetaDataServiceInterface mds;
    
    
    private int storageId;
    
    private Map<Integer, ConnectionEvaluation> evaluations;
    private long avarageEvaluation;
    private ConnectionEvaluation[] neighbours = {};
    private Random random = new Random();
    
    private Timer sheduler = new Timer();
    private Pinger pinger = new Pinger();
    private CountDownLatch initialized = new CountDownLatch(1);
    
    private Logger logger = Logger.getLogger(NetworkDiscoverer.class.getName());

    public NetworkDiscoverer(int storageId) {
        this.storageId = storageId;
        evaluations = Collections.synchronizedMap(new HashMap<Integer, ConnectionEvaluation>());
    }

    public long getLinkEvaluation(int storageId) {
        ConnectionEvaluation evaluation = evaluations.get(storageId);                    
        if (evaluation == null) evaluation = new ConnectionEvaluation(storageId, avarageEvaluation);    
        return evaluation.getRoundTripTime();
    }

    public synchronized ConnectionEvaluation[] getNeighbours() {
        return neighbours.clone();
    }
    
    public synchronized int getNeighboursCount() {
        return neighbours.length;
    }
    
    public synchronized int getRandomNeighbour() {
        if (getNeighbours().length > 0) return getNeighbours()[random.nextInt(getNeighbours().length)].getStorageId();
        else return -1;
    }

    public int[] orderFromClosestToFarest(int[] replicaHolders) {
        ConnectionEvaluation[] rh = new ConnectionEvaluation[replicaHolders.length];
        for (int i = 0; i < replicaHolders.length; i++) {
            rh[i] = evaluations.get(replicaHolders[i]);
            if (rh[i] == null) rh[i] = new ConnectionEvaluation(replicaHolders[i], avarageEvaluation);            
        }
        Arrays.sort(rh);
        for (int i = 0; i < rh.length; i++) {
            replicaHolders[i]= rh[i].getStorageId();            
        }
        return replicaHolders;
    }

    public void start() {        
        sheduler.scheduleAtFixedRate(pinger, 0, 30 * Clock.SECOND);        
        try {
            initialized.await();
        } catch (InterruptedException ex) {
           logger.log(Level.SEVERE, "Initialization of network discoverer was interrupted abnormally.", ex);
        }
    }

    public void setInternodeCommunicator(InternodeCommunicator inter) {
        this.inc = inter;
    }

    public void setMetaDataServiceInterface(MetaDataServiceInterface meta) {
        this.mds = meta;
    }
    
    private class Pinger extends TimerTask {
        
        public static final double B = 1.4;
        public static final double C = 1.5;
        public static final double MAX_DIFF = 20.0;
        
        public static final int PING_REPEATS = 3;
        
        private Clock clock = Clock.getInstance();
        
        @Override
        public void run() {
            try {
                linkEvaluation();                
                determinateClosestNeighbours();
                initialized.countDown();
            } catch (MetaDataServiceNotAvilableException ex) {
                logger.log(Level.SEVERE, "The network discovery was unable due to metadata service failure.", ex);
            }
        }
        
        private double cutOff(int k, double maxDelay, double b, double c) {            
            return maxDelay / Math.pow(b, k / c);
        }

        private void determinateClosestNeighbours() {
            LinkedList<ConnectionEvaluation> newNeighbours = new LinkedList<ConnectionEvaluation>();
            ConnectionEvaluation[] eval = new ConnectionEvaluation[evaluations.size()];
            eval = evaluations.values().toArray(eval);
            Arrays.sort(eval);
            if (eval.length > 0) {
                newNeighbours.add(eval[0]);
            }
            if (eval.length > 1) {
                long delta = 0;
                boolean stopped = false;
                for (int k = 1; !stopped && k < eval.length; k++) {
                    delta = eval[k].getRoundTripTime() - eval[k - 1].getRoundTripTime();
                    if (delta < cutOff(k, MAX_DIFF, B, C)) {                        
                        newNeighbours.add(eval[k]);
                    } else {
                        stopped = true;
                    }
                }
            }
            synchronized (NetworkDiscoverer.this) {
                neighbours = newNeighbours.toArray(getNeighbours());
                System.out.println("NEIGHBOURS: " + Arrays.toString(neighbours));                
            }
        }

        private void linkEvaluation() throws MetaDataServiceNotAvilableException {
            List<StorageServerEntity> result = mds.getStorageServers();
            ConnectionEvaluation ce = null;
            long evaluationSum = 0;
            int evaluationCount = 0;
            for (StorageServerEntity sse : result) {
                ce = evaluations.get(sse.getStorageId());
                if (ce == null) {
                    try {
                        int sid = sse.getStorageId();
                        if (sid != storageId) {
                            long roundTripTime = evaluateLink(sid);
                            ce = new ConnectionEvaluation(sid, roundTripTime);
                            evaluations.put(sid, ce);                            
                        }
                    } catch (RemoteException ex) {
                        logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
                if (ce != null) {
                    evaluationSum += ce.getRoundTripTime();
                    evaluationCount++;
                }
            }
            if (evaluationCount != 0) {
                avarageEvaluation = evaluationSum / evaluationCount;
            }
        }

        private long evaluateLink(int storageId) throws MetaDataServiceNotAvilableException, RemoteException {            
            StorageServerInterface comm = inc.getRemoteStorageServerInterface(storageId);
            if (comm == null) throw new RemoteException("The storage server " + storageId + " is not accessible.");
            long delaySum = 0;
            long start, stop;                
            for (int i = 0; i < PING_REPEATS; i++) {                    
                start = clock.getCurrentTime();
                comm.ping();
                stop = clock.getCurrentTime();
                delaySum += stop - start;
            }                
            return Math.round(delaySum / (double) PING_REPEATS);    
        }
    }
}
