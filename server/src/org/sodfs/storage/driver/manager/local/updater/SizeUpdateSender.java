package org.sodfs.storage.driver.manager.local.updater;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;

/**
 *
 * @author Roman Kierzkowski
 */
public class SizeUpdateSender implements Runnable {
    private LinkedBlockingQueue<SizeUpdate> messages = new LinkedBlockingQueue<SizeUpdate>();
    private Thread thread;    
    private static Logger logger = Logger.getLogger(SizeUpdateSender.class.getName());

    private MetaDataServiceInterface mds;
    

    public void setMetaDataService(MetaDataServiceInterface mds) {
        this.mds = mds;
    }    
    
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                send(messages.take());
                
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "SizeUpdateSender was stopped.", ex);
            }
        }        
    }

    public void start() {
        thread = new Thread(this, SizeUpdateSender.class.getName());        
        thread.setDaemon(true);
        thread.start();
    }
    
    public void stop() {
        thread.interrupt();        
    }

    private void send(SizeUpdate msg) {
        try {
            mds.updateSize(msg.getFileId(), msg.getNewSize());
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void updateSize(int fileId, long newSize) {
        try {
            messages.put(new SizeUpdate(fileId, newSize));
        } catch (InterruptedException ex) {            
        }
    }
    private class SizeUpdate {
        private int fileId;
        private long newSize;

        public SizeUpdate(int fileId, long newSize) {
            this.fileId = fileId;
            this.newSize = newSize;
        }
        
        public int getFileId() {
            return fileId;
        }

        public long getNewSize() {
            return newSize;
        }

    }
}
