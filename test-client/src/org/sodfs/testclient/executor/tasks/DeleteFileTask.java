package org.sodfs.testclient.executor.tasks;

import jcifs.smb.SmbFile;
import org.sodfs.testclient.executor.Task;

/**
 *
 * @author Roman Kierzkowski
 */
public class DeleteFileTask extends Task {
    
    private String url;

    public DeleteFileTask(String url) {
        this.url = url;
    }
    
        @Override
    public void run() {
        try {
            SmbFile smbFile = new SmbFile(url);            
            smbFile.delete();                        
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }
}
