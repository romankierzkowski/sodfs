package org.sodfs.storage.replication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class CoinList {    
    private ArrayList<Coin> list = new ArrayList<Coin>();
    private Random rand = new Random();    
    
    private CoinBuffor coinBuffor = new CoinBuffor();
    private boolean stoped = false;
    private CountDownLatch stopFinished = new CountDownLatch(1);
    
    private static final String TIMER_NAME = "CoinListTimer";
    private Timer sheduler = new Timer(TIMER_NAME, true);
    
    private Cleaner cleaner = new Cleaner();
    
    private static final Logger logger = Logger.getLogger(ReplicaPlacementManager.class.getName());
    
    public void start() {
        coinBuffor.start();
        sheduler.scheduleAtFixedRate(cleaner, 0, 5 * Clock.SECOND);
    }
    
    public void stop() {
        stoped = true;
        try {
            stopFinished.await();
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "CoinList finished abnormally.", ex);
        }
    }
    
    public void addCoin(Coin coin) {
        coin.incrementHopCounter();
        coinBuffor.addCoin(coin);
    }
    
    public Coin getRandomCoin() {
        Coin result = null;
        do {            
            synchronized(list) {                
                int range = list.size();
                if (range != 0) {
                    int pos = rand.nextInt(range);
                    result = list.get(pos);
                    list.remove(pos);  
                }                
            }            
        } while (result != null && result.isExpired());        
        return result;
    } 
    
    public ArrayList<Coin> getSnapShot() {
        synchronized(list) {
            ArrayList<Coin> result = new ArrayList<Coin>(list.size());
            for (Iterator<Coin> it = list.iterator(); it.hasNext();) {
                Coin coin = it.next();                             
                if (!coin.isExpired()) result.add(coin);
            }
            return result;
        }
    }
    
    /* COIN BUFFOR */
    
    private class CoinBuffor implements Runnable {
        protected LinkedBlockingQueue<Coin> buffor = new LinkedBlockingQueue<Coin>();
        private Thread thread;

        public void run() {
            while (!stoped) {
                try {
                    Coin coin = buffor.take();
                    synchronized (list) {
                        list.add(coin);
                    }
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, "CoinList iterrupted abnormally.", ex);
                }
            }
        }

        public void start() {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        public void addCoin(Coin coin) {
            buffor.add(coin);            
        } 
    }
    
    /* CLEANER */
    
    private class Cleaner extends TimerTask {

        @Override
        public void run() {
            synchronized(list) {
                for (int i = 0; i < list.size(); i++) {
                    Coin coin = list.get(i);
                    if (coin.isExpired()) list.remove(i);
                }
                System.out.println("COIN LIST COUNT: " + list.size());
            }            
        }
        
    }
}
