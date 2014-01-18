/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sodfs.testclient.executor.tasks;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbRandomAccessFile;

/**
 *
 * @author Roman
 */
public class InfiniteReadFile {

    public static void main(String[] args) {
        try {

            SmbRandomAccessFile raf =
              new SmbRandomAccessFile("smb://normal:normal@127.0.0.1:" + args[0] + "/" + args[1] + "/" + args[2],
              "rw", SmbFile.FILE_SHARE_READ);
            Random rand = new Random();
            byte b[] = new byte[1024];//{'b','b','b','b'};
            for (int i = 0; i < b.length; i++) {
                byte c = (byte) rand.nextInt(255);
            }
            if (raf.length() == 0) raf.write(b);
            int errorCount = 0;
            while (true) {
                try {   
                    long start = System.currentTimeMillis();                    
                    //raf.read(b, rand.nextInt(15000), b.length);
                    raf.read(b);                    
                    //System.out.println("Result" + b[0] + b[1] + b[2]);
                    raf.write(b);
                    raf.seek(0);
                    long stop = System.currentTimeMillis();
                    System.out.println("Time(" + args[2] + "):" + (stop - start) + " (" + errorCount + ")");                    
                    
                } catch (SmbException ex) {
                    errorCount++;
                    Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        } catch (SmbException ex) {
            Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(InfiniteReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
