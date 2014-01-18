package org.sodfs.meta.server;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import org.sodfs.meta.persistance.Directory;
import org.sodfs.meta.persistance.File;
import org.sodfs.meta.persistance.Node;
import org.sodfs.storage.meta.api.NamespaceConstants;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class SizeActualisator implements Runnable{
    private LinkedBlockingQueue<SizeUpdate> messages = new LinkedBlockingQueue<SizeUpdate>();
    private Thread thread;    
    private static Logger logger = Logger.getLogger(SizeActualisator.class.getName());
    private EntityManager em;
    
    private static final String TIMER_NAME = "SizeUpdateReciverTimer";
    private static final long SUMMING_UP_DIR_SIZE_INTERVAL = 10 * Clock.SECOND;
    private Timer sheduler = new Timer(TIMER_NAME, true);
    private DirectorySizeUpdater dirSizeUpdater;

    public SizeActualisator(EntityManagerFactory emf) {        
        em = emf.createEntityManager();
        dirSizeUpdater = new DirectorySizeUpdater(emf);
    }
    
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                SizeUpdate task = messages.take();
                EntityTransaction t = em.getTransaction();                
                t.begin();
                try {
                    File file = em.find(File.class, task.getFileId());
                    if (file != null) {
                        file.setAllocationSize(task.getNewSize());
                        file.setNominalSize(task.getNewSize());
                        //em.merge(file);
                    }
                    t.commit();            
                } catch (RollbackException e) {            
                    logger.log(Level.WARNING, "Unknow error in SizeUpdateReciver.", e);
                } 
                finally {
                    if (t.isActive()) t.rollback();
                }                
            } catch (InterruptedException ex) {
                logger.log(Level.FINE, "SizeUpdateReciver was stopped.", ex);
            }
        }        
    }

    public void start() {
        sheduler.schedule(dirSizeUpdater, 0L, SUMMING_UP_DIR_SIZE_INTERVAL);        
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
    
    public void stop() {
        thread.interrupt();        
    }

    public boolean updateSize(int fileId, long newSize) {        
        try {
            messages.put(new SizeUpdate(fileId, newSize));            
        } catch (InterruptedException ex) {            
        }
        return true;
    }
    
    private Directory getRoot(EntityManager em) {
        Directory result = em.find(Directory.class, (Integer) NamespaceConstants.ROOT_ID);                   
        return result;
    }
    
    /* SIZE UPDATE */
    
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
    
    /* DIRECTORY SIZE UPDATER */
    
    private class DirectorySizeUpdater extends TimerTask {        
        private EntityManager em;

        public DirectorySizeUpdater(EntityManagerFactory emf) {
            em = emf.createEntityManager();
        }
        
        @Override
        public void run() {
            EntityTransaction t = em.getTransaction();                
            t.begin();            
            Node current = null;
            Stack<Directory> dirStack = new Stack<Directory>();            
            Stack<LinkedList<Node>> childrenStack = new Stack<LinkedList<Node>>();
            LinkedList<Node> ll = new LinkedList<Node>();
            Node root = getRoot(em);
            em.refresh(root);
            ll.addFirst(root);            
            childrenStack.push(ll);
            LinkedList<Node> currentChildren;            
            try {
                while (!childrenStack.empty()) {                    
                    currentChildren = childrenStack.peek();                    
                    if (!currentChildren.isEmpty()) {
                        current = currentChildren.removeFirst();                        
                        if (current.isDirectory()) {
                            Directory dir = (Directory)current;
                            dir.setNominalSize(0);
                            dir.setAllocationSize(0);                                
                            dirStack.push(dir);
                            List<Node> list = dir.getChildren();
                            childrenStack.push(new LinkedList(list));                            
                        } else {                            
                            Directory dir = dirStack.peek();
                            File file = (File) current;                            
                            dir.setAllocationSize(dir.getAllocationSize() + file.getAllocationSize());
                            dir.setNominalSize(dir.getNominalSize() + file.getNominalSize());                             
                        }
                    } else {
                        childrenStack.pop();
                        if (!dirStack.isEmpty()) {
                            Directory dir = dirStack.pop();                            
                            if (!dirStack.isEmpty()) {                                
                                Directory parent = dirStack.peek();
                                parent.setAllocationSize(parent.getAllocationSize() + dir.getAllocationSize());
                                parent.setNominalSize(parent.getNominalSize() + dir.getNominalSize());                                
                            }
                        }
                    }                    
                }                
                t.commit();            
            } catch (RollbackException e) {            
                logger.log(Level.WARNING, "Unknow error in SizeUpdateReciver.", e);
            } 
            finally {
                if (t.isActive()) t.rollback();
            }  
        }
        
    }
}
