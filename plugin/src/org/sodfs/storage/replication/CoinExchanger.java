package org.sodfs.storage.replication;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.discovery.NetworkDiscoverer;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;

/**
 *
 * @author Roman Kierzkowski
 */
public class CoinExchanger implements Runnable {

    private int exchanges;
    private CoinList coinList;
    private boolean stopped = false;
    private NetworkDiscoverer networkDiscoverer;
    private InternodeCommunicator internodeCommunicator;
    private Logger logger = Logger.getLogger(CoinExchanger.class.getName());
    private Thread thread;

    public CoinExchanger(CoinList coinList) {
        this.coinList = coinList;
    }

    public void exchange() {
        synchronized (this) {
            exchanges++;
            if (exchanges == 1) {
                notifyAll();
            }
        }
    }

    public void run() {
        Coin coin = null;
        int storageId = -1;
        while (!stopped) {
            try {
                synchronized (this) {
                    try {
                        while (exchanges == 0) {
                            wait();
                        }
                        exchanges--;
                    } catch (InterruptedException ex) {
                        logger.log(Level.SEVERE, "CoinExchanger interrupted abnormally.", ex);
                    }
                }
                storageId = networkDiscoverer.getRandomNeighbour();
                if (storageId > 0) {
                    do {
                        coin = coinList.getRandomCoin();
                        if (coin == null) {                            
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                            }
                        }
                    } while (coin == null);
                    StorageServerInterface ssi = internodeCommunicator.getRemoteStorageServerInterface(storageId);
                    if (ssi != null) {
                        coin.dispearse(networkDiscoverer.getNeighboursCount());
                        ssi.sendCoin(coin);
                    }
                }
            } catch (MetaDataServiceNotAvilableException ex) {
                logger.log(Level.SEVERE, "Meta-data service not avilable.", ex);
            } catch (RemoteException ex) {
                logger.log(Level.SEVERE, "Storage server " + storageId + " is not avilable.", ex);
            }
        }
    }

    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        stopped = true;
    }

    public void setNetworkDiscoverer(NetworkDiscoverer networkDiscoverer) {
        this.networkDiscoverer = networkDiscoverer;
    }

    public void setInternodeCommunicator(InternodeCommunicator internodeCommunicator) {
        this.internodeCommunicator = internodeCommunicator;
    }
}
