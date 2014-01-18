package org.sodfs.testclient.executor;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
 public abstract class Task implements Runnable, Serializable {
    protected StatisticGenerator statsGen;

    public void setStatsGen(StatisticGenerator statsGen) {
        this.statsGen = statsGen;
    }
          
    public abstract void run();    
}
