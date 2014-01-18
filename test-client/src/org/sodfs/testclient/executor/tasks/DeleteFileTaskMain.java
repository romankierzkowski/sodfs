package org.sodfs.testclient.executor.tasks;

import java.rmi.registry.Registry;
import org.sodfs.testclient.barrier.BarrierUtil;
import org.sodfs.testclient.commons.Constants;
import org.sodfs.testclient.executor.TaskExecutorInterface;

/**
 *
 * @author Roman Kierzkowski
 */
public class DeleteFileTaskMain {
    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                String address = args[0];
                String url = args[1];
                Registry reg = BarrierUtil.getRemoteRegistry(address, Constants.RMI_PORT);
                if (reg != null) {
                    DeleteFileTask dft = new DeleteFileTask(url);
                    TaskExecutorInterface tei = (TaskExecutorInterface) reg.lookup(Constants.EXECUTOR_NAME);
                    tei.execute(dft);
                } else {
                    System.err.println("Unable to find registry.");
                }                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("usage: sodfsdel <address> <url>");
        }
    }
}
