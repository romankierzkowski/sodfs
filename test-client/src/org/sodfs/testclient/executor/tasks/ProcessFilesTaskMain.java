package org.sodfs.testclient.executor.tasks;

import java.rmi.registry.Registry;
import org.sodfs.testclient.barrier.BarrierUtil;
import org.sodfs.testclient.commons.Constants;
import org.sodfs.testclient.executor.TaskExecutorInterface;

/**
 *
 * @author Roman Kierzkowski
 */
public class ProcessFilesTaskMain {
    public static void main(String[] args) {
        if (args.length == 8) {
            try { 
                String address = args[0];
                String url = args[1];
                int range = Integer.parseInt(args[2]);
                int maxRepeats = Integer.parseInt(args[3]);
                float writesPercent = Float.parseFloat(args[4]);
                int dataSize = Integer.parseInt(args[5]);
                int fileSize = Integer.parseInt(args[6]);
                long time = Long.parseLong(args[7]);           
                Registry reg = BarrierUtil.getRemoteRegistry(address, Constants.RMI_PORT);
                if (reg != null) {
                    ProcessFilesTask task = new ProcessFilesTask(url, range, maxRepeats,  writesPercent,  dataSize,  fileSize,  time * 1000);
                    TaskExecutorInterface tei = (TaskExecutorInterface) reg.lookup(Constants.EXECUTOR_NAME);                    
                    tei.execute(task);
                } else {
                    System.err.println("Unable to find registry.");
                }                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("usage: sodfsprocess <exec_address> <hotst_url> <range> <max_repeats> <writesPercent> <dataSize> <fileSize> <time>");
        }
    }
}
