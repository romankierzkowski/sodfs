package org.sodfs.testclient.executor.tasks;

import org.sodfs.testclient.executor.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class PrintTask extends Task {
    private String message;

    public PrintTask(String message) {
        this.message = message;
    }
    
    @Override
    public void run() {
        System.out.println(message);
    } 
    
}
