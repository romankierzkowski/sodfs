package org.sodfs.testclient.executor.tasks;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbRandomAccessFile;
import org.sodfs.testclient.executor.Task;

/**
 *
 * @author Roman Kierzkowski
 */
public class InifniteReadTask extends Task {
    
    private String fileName;
    private String path;

    public InifniteReadTask(String fileName, String address) {
        this.fileName = fileName;
        this.path = address;
    }
    
    @Override
    public void run() {
//                try {
//            SmbRandomAccessFile raf =
//              new SmbRandomAccessFile(path + "/" + fileName,
//              "rw", SmbFile.FILE_SHARE_READ);            
//            byte b[] = new byte[1024];
//            while (true) {
//                try {   
//                    long start = System.currentTimeMillis();                    
//                    //raf.read(b, rand.nextInt(15000), b.length);
//                    raf.read(b);                    
//                    //System.out.println("Result" + b[0] + b[1] + b[2]);
//                    raf.write(b);
//                    raf.seek(0);
//                    long stop = System.currentTimeMillis();
//                    System.out.println("Time(" + args[2] + "):" + (stop - start) + " (" + errorCount + ")");                    
//                    
//                } catch (SmbException ex) {
//                    errorCount++;
//                    Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//                }
//            }
//        } catch (SmbException ex) {
//            Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnknownHostException ex) {
//            Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
