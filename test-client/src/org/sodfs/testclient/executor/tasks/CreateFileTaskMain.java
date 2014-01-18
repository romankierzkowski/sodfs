package org.sodfs.testclient.executor.tasks;

import java.rmi.registry.Registry;
import org.sodfs.testclient.barrier.BarrierUtil;
import org.sodfs.testclient.commons.Constants;
import org.sodfs.testclient.executor.TaskExecutorInterface;

/**
 *
 * @author Roman Kierzkowski
 */
public class CreateFileTaskMain {
    public static void main(String[] args) {
        if (args.length == 5) {
            try {
                String address = args[0];
                String url = args[1];
                int size = Integer.parseInt(args[2]);
                String barrierAddress = args[3];
                String barrierName = args[4];
                Registry reg = BarrierUtil.getRemoteRegistry(address, Constants.RMI_PORT);
                if (reg != null) {
                    CreateFileTask cft = new CreateFileTask(url, size);
                    EndTask et = new EndTask(barrierAddress, barrierName, cft);
                    TaskExecutorInterface tei = (TaskExecutorInterface) reg.lookup(Constants.EXECUTOR_NAME);
                    tei.execute(et);
                } else {
                    System.err.println("Unable to find registry.");
                }                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("usage: sodfscreate <address> <url> <size> <barrier_address> <barrier_name>");
        }
    }
}
