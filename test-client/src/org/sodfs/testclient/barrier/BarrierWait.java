package org.sodfs.testclient.barrier;

import java.rmi.registry.Registry;
import org.sodfs.testclient.commons.Constants;

/**
 *
 * @author Roman Kierzkowski
 */
public class BarrierWait {

    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                String address = args[0];                
                String name = args[1];
                Registry reg = BarrierUtil.getRemoteRegistry(address, Constants.RMI_PORT);
                if (reg != null) {
                    BarrierUtil.wait(reg, name);
                    System.out.print("Barrier passed.");
                } else {
                    System.err.println("Unable to find registry.");
                }  
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("usage: sodfswait <barrier_address> <barrier_name>");
        }
    }
}
