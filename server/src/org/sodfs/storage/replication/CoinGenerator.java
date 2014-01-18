package org.sodfs.storage.replication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Random;
import org.sodfs.storage.driver.manager.MovableFileInterface;
import org.sodfs.storage.driver.manager.MovableFileInterface;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class CoinGenerator implements MovableFileInterface {
    private MovableFileInterface component;
    private CoinList coinList;
    private CoinExchanger coinExchanger;

    private int storageId;
    private int fileId;        

    private double pc;
    private double k;
    private long TTL;    
    
    private Random rand = new Random();

    public CoinGenerator(MovableFileInterface component, CoinList coinList, CoinExchanger coinExchanger, int storageId, int fileId, double pc, double k, long TTL) {
        this.component = component;
        this.coinList = coinList;
        this.coinExchanger = coinExchanger;
        this.storageId = storageId;
        this.fileId = fileId;
        this.pc = pc;
        this.k = k;
        this.TTL = TTL;
    }   
    
    public void openFile(boolean createFlag) throws RemoteException, IOException, MovableFileException {
        component.openFile(createFlag);            
    }

    public byte[] readFile(int len, long fileOff) throws RemoteException, IOException, MovableFileException {
        byte[] result = component.readFile(len, fileOff);
        generateCoin(OperationType.READ, result.length);
        return result;
    }

    public int writeFile(byte[] buf, long fileOff) throws RemoteException, IOException, MovableFileException {
        int result = component.writeFile(buf, fileOff);
        generateCoin(OperationType.WRITE, result);
        return result;
    }

    public long seekFile(long pos, int typ) throws RemoteException, IOException, MovableFileException {
        long result = component.seekFile(pos, typ);
        return result;
    }

    public void flushFile() throws RemoteException, IOException, MovableFileException {
        component.flushFile();
    }

    public void truncateFile(long siz) throws RemoteException, IOException, MovableFileException {
        component.truncateFile(siz);
    }

    public void closeFile() throws RemoteException, IOException, MovableFileException {
        component.closeFile();
    }

    public int getStorageId() throws RemoteException, MovableFileException {
        return component.getStorageId();
    }

    private void generateCoin(OperationType operationType, int dataAmount) throws RemoteException, MovableFileException {
        if (pc > rand.nextDouble()) {
            Coin coin = new Coin(fileId, component.getStorageId(), storageId, operationType, dataAmount, TTL, Clock.getInstance());                       
            coinList.addCoin(coin);
            if (k > rand.nextDouble()) {
                coinExchanger.exchange();
            }
        }
    }
}
