package org.sodfs.storage.replication;

import org.sodfs.utils.DeamonThreadFactory;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.discovery.NetworkDiscoverer;
import org.sodfs.storage.driver.manager.FileManager;
import org.sodfs.storage.driver.manager.FileWraperFactory;
import org.sodfs.storage.driver.manager.MovableFileInterface;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicaPlacementManager {    
    private boolean stoped = false;
    private CountDownLatch stopFinished = new CountDownLatch(4);
    
    private ReplicationOrderReceiver roReceiver = new ReplicationOrderReceiver();
    private ReplicationRequestReceiver rrReceiver = new ReplicationRequestReceiver();
    
    private CoinList coinList = new CoinList();
    private CoinExchanger coinExchanger = new CoinExchanger(coinList);
    
    private static final String TIMER_NAME = "ReplicaPlacementManagerTimer";
    private Timer sheduler = new Timer(TIMER_NAME, true);
    
    private static final long ALGORITHM_INTERVAL = 15 * Clock.SECOND;
    
    private double pc;
    private double k;
    private long coinTTL;
    
    private long pinTime;
    private int minNOR;
    
    private float RF;
    private float DRF;
    
    private float AF;
    private float MF;
    
    private NetworkDiscoverer networkDiscoverer;
    private InternodeCommunicator internodeCommunicator;
    private FileManager fileManager;
            
    private int storageId;
    
    public static final int REPLICATION_ORDER_RECEIVER_THREAD_POOL_SIZE = 1;    
    
    private static final Logger logger = Logger.getLogger(ReplicaPlacementManager.class.getName());


    public ReplicaPlacementManager(int storageId, double pc, double k, long coinTTL, long pinTime, int minNOR, float RF, float DRF, float AF, float MF) {
        this.storageId = storageId;
        this.pc = pc;
        this.k = k;
        this.coinTTL = coinTTL;        
        this.pinTime = pinTime;
        this.minNOR = minNOR;
        this.RF = RF;
        this.DRF = DRF;               
        this.AF = AF;
        this.MF = MF;
    }

    public long getDefaultCoinTTL() {
        return coinTTL;
    }

    public int getDefaultMinNOR() {
        return minNOR;
    }

    public long getDefaultPinTime() {
        return pinTime;
    }
    
    public void start() {
        roReceiver.start();
        rrReceiver.start();
        coinList.start();
        coinExchanger.start();
        SoRPARunner sorpaRunner = new SoRPARunner();
        sheduler.scheduleAtFixedRate(sorpaRunner, 0, ALGORITHM_INTERVAL);
    }
    
    public void stop() {        
        stoped = true;
        try {
            stopFinished.await();
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "ReplicaPlacementManager finished abnormally.", ex);
        }
    }
    
    public void receive(ReplicationOrder order) {
        roReceiver.addMessage(order);
    }
    
    public void receive(ReplicationRequest request) {
        rrReceiver.addMessage(request);        
    }
    
    public void receive(Coin coin) {
        coin.setClock(Clock.getInstance());
        coin.incrementHopCounter();        
        coinList.addCoin(coin);
    }

    public void setNetworkDiscoverer(NetworkDiscoverer networkDiscoverer) {
        this.networkDiscoverer = networkDiscoverer;
        coinExchanger.setNetworkDiscoverer(networkDiscoverer);
    }

    public void setInternodeCommunicator(InternodeCommunicator internodeCommunicator) {
        this.internodeCommunicator = internodeCommunicator;
        coinExchanger.setInternodeCommunicator(internodeCommunicator);
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.fileManager.addFileWraperFactory(new CoinGeneratorFactory());
    }
    
    /* SoRPA ALGORITHM */
    
    private class SoRPARunner extends TimerTask {
        
        private SoRPAAlgorithm algorithm = new SoRPAAlgorithm(storageId, fileManager, internodeCommunicator, RF, DRF, AF, MF);
        
        @Override
        public void run() {
            System.out.println("REPLICA PLACEMENT");
            ArrayList<Coin> coins = coinList.getSnapShot();
            ReplicationRequestCollection requests = rrReceiver.getReplicationRequest();            
            algorithm.execute(coins, requests);
        }
    }
    
    /* MESSAGE CONSUMER */
    
    private abstract class MessageConsumer<Message extends ReplicationMessage> implements Runnable {
        protected LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<Message>();
        private Thread thread;       
        
        public void run() {
            while (!stoped) {
                try {
                    consume(messages.take());
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, "MessageConsumer iterrupted abnormally.", ex);
                }
            }
            stopFinished.countDown();
        }
        
        public void start() {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
        
        protected abstract void consume(Message msg);
        
        public void addMessage(Message msg) {
            messages.add(msg);
        }
    }
  
    /* REPLICATION ORDER RECEIVER */
    
    private class ReplicationOrderReceiver extends MessageConsumer<ReplicationOrder> {        
        ExecutorService executor = Executors.newFixedThreadPool(REPLICATION_ORDER_RECEIVER_THREAD_POOL_SIZE, new DeamonThreadFactory());
        
        @Override
        protected void consume(ReplicationOrder msg) {            
            executor.execute(new ReplicationOrderHandler(fileManager, msg));
        }
        
        private class ReplicationOrderHandler implements Runnable {
            private FileManager fileManager;
            private ReplicationOrder replicationOrder;

            public ReplicationOrderHandler(FileManager fileManager, ReplicationOrder replicationOrder) {
                this.fileManager = fileManager;
                this.replicationOrder = replicationOrder;
            }
            
            public void run() {                
                int fileId = replicationOrder.getFileId();
                boolean move = replicationOrder.isMove();                
                fileManager.replicate(fileId, move, storageId);                
            }
            
        }
    }
    
    /* REPLICATION REQUEST RECEIVER */
    
    private class ReplicationRequestReceiver extends MessageConsumer<ReplicationRequest> {        
        private ReplicationRequestCollection requests = new ReplicationRequestCollection();          
        
        @Override
        protected void consume(ReplicationRequest msg) {
            synchronized(this) {
                requests.addReplicationRequest(msg);
            }
        }

        public ReplicationRequestCollection getReplicationRequest() {
            synchronized(this) {
                ReplicationRequestCollection result = requests;
                requests = new ReplicationRequestCollection();
                return result;
            }
        }
    }
    
    /* COIN GENERATOR FACTORY */
    
    private class CoinGeneratorFactory implements FileWraperFactory {
        
        public MovableFileInterface wrap(MovableFileInterface replica, int storageId, int fileId) {
            return new CoinGenerator(replica, coinList, coinExchanger, ReplicaPlacementManager.this.storageId, fileId, pc, k, coinTTL);
        }
    }
}
