package org.sodfs.testclient.executor.tasks;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.sodfs.testclient.executor.*;
import org.sodfs.testclient.barrier.BarrierUtil;
import org.sodfs.testclient.commons.Constants;

/**
 *
 * @author Roman Kierzkowski
 */
public class EndTask extends Task {
    private String barrierAddress;
    private String barrierName;
    private Task component;

    public EndTask(String barrierAddress, String barrierName, Task component) {
        this.barrierAddress = barrierAddress;
        this.barrierName = barrierName;
        this.component = component;
    }
    
    @Override
    public void run() {
        try {
            component.run();
            try {
                Registry registry = LocateRegistry.getRegistry(barrierAddress, Constants.RMI_PORT);     
                if (registry != null) {
                    BarrierUtil.register(registry, barrierName);  
                } 
            } catch (Exception ex) {
                ex.printStackTrace();
            }                               
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }

    @Override
    public void setStatsGen(StatisticGenerator statsGen) {        
        super.setStatsGen(statsGen);
        component.setStatsGen(statsGen);
    }
}
