package org.sodfs.testclient.executor.tasks;

import java.util.Random;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbRandomAccessFile;
import org.sodfs.testclient.executor.Task;

/**
 *
 * @author Roman Kierzkowski
 */
public class ProcessFilesTask extends Task {
    
    private String url;
    private int range;
    private int maxRepeats;
    private float writesPercent;    
    private int dataSize;
    private int fileSize;
    private long time;

    public ProcessFilesTask(String url, int range, int maxRepeats, float writesPercent, int dataSize, int fileSize, long time) {
        super();
        this.url = url;
        this.range = range;
        this.maxRepeats = maxRepeats;
        this.writesPercent = writesPercent;
        this.dataSize = dataSize;
        this.fileSize = fileSize;
        this.time = time;        
    }    
    
    @Override
    public void run() {
        boolean error = false;
        long workEnd = System.currentTimeMillis() + time;
        boolean finished = System.currentTimeMillis() > workEnd;
        Random rand = new Random();
        byte[] b = new byte[dataSize];
        while(!finished) {
            try {
                error = false;
                finished = System.currentTimeMillis() > workEnd;
                long repeats = rand.nextInt(maxRepeats) + 1;                
                int file = rand.nextInt(range);                
                //SmbFile smbf = new SmbFile(url + "/" + file);
                SmbRandomAccessFile raf = new SmbRandomAccessFile(url + "/" + file, "rw", SmbFile.FILE_SHARE_READ + SmbFile.FILE_SHARE_WRITE);                
                while (!finished && !error && repeats-- > 0) {
                    try {
                        finished = System.currentTimeMillis() > workEnd;
                        int pos = rand.nextInt(fileSize - b.length);
                        if (rand.nextFloat() < writesPercent) {                            
                            raf.seek(pos);
                            raf.write(b);                            
                            statsGen.addWrite();                            
                        } else {                            
                            raf.seek(pos);
                            raf.read(b);
                            statsGen.addRead();                            
                        }
                    } catch (Exception ex) {
                        error = true;
                        ex.printStackTrace();
                        statsGen.addError();
                    }
                }   
                finished = System.currentTimeMillis() > workEnd;
            } catch (Exception ex) {
                ex.printStackTrace();
                statsGen.addError();
            }
        }     
    }
}
