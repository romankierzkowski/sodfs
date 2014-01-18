package org.sodfs.testclient.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Roman Kierzkowski
 */
public class TaskExecutor implements TaskExecutorInterface {
    
    private ExecutorService executor = Executors.newCachedThreadPool();    
    private StatisticGenerator statsGen;

    public TaskExecutor(StatisticGenerator statsGen) {
        this.statsGen = statsGen;
    }  
    
    public void execute(Task task) {
        task.setStatsGen(statsGen);
        executor.submit(task);
    }

}
