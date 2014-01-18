package org.sodfs.testclient.executor.tasks;

import java.io.InputStream;
import java.io.OutputStream;
import jcifs.smb.SmbFile;
import org.sodfs.testclient.executor.Task;

/**
 *
 * @author Roman Kierzkowski
 */
public class CreateFileTask extends Task {
    
    private String url;
    private int size;

    public CreateFileTask(String url, int size) {
        this.url = url;
        this.size = size;
    }
    
    @Override
    public void run() {
        try {
            SmbFile smbFile = new SmbFile(url);            
            smbFile.createNewFile();
            byte[] tab = new byte[size];
            for (int i = 0; i < tab.length; i++) {
                tab[i] = (byte) ((i % 50) + 30);
            }
            OutputStream out = smbFile.getOutputStream();
            try {                
                out.write(tab);
            } finally {
                out.close();
            }             
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

}
