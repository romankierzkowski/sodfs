package org.sodfs.testclient.executor.tasks;

import org.sodfs.testclient.executor.*;
import java.rmi.registry.Registry;
import org.sodfs.testclient.barrier.BarrierUtil;
import org.sodfs.testclient.commons.Constants;

/**
 *
 * @author Roman Kierzkowski
 */
public class PrintTaskMain {
    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                String address = args[0];
                String message = args[1];
                Registry reg = BarrierUtil.getRemoteRegistry(address, Constants.RMI_PORT);
                if (reg != null) {
                    PrintTask pt = new PrintTask(message);
                    TaskExecutorInterface tei = (TaskExecutorInterface) reg.lookup(Constants.EXECUTOR_NAME);
                    tei.execute(pt);
                } else {
                    System.err.println("Unable to find registry.");
                }                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("usage: sodfsprint <address> <message>");
        }
    }
}
