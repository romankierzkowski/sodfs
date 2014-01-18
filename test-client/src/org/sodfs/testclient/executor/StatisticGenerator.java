package org.sodfs.testclient.executor;

/**
 *
 * @author Roman Kierzkowski
 */
public class StatisticGenerator implements Runnable {    
    private int interval;
    private long writesNum = 0;
    private long readNum = 0;
    private long error = 0;
    private long current = 0;

    public StatisticGenerator(int interval) {
        this.interval = interval;
    }
    
    public void run() {
        while(true) {
            synchronized (this) {
                writesNum = 0;
                readNum = 0;
                error = 0;
            }
            try {                
                Thread.sleep(interval * 1000);
            } catch (InterruptedException ex) {            
            }
            synchronized (this) {
                System.out.printf("%1$d;%2$d;%3$d;%4$d;%5$d\n", System.currentTimeMillis(), current, readNum, writesNum, error);
                current+=interval;
            }
        }
    }
    
    public synchronized void addWrite() {
        writesNum++;
    }
    
    public synchronized void addRead() {
        readNum++;
    }
    
    public synchronized void addError() {
        error++;
    }
}
