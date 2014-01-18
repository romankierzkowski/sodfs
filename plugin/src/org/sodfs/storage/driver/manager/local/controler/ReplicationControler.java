package org.sodfs.storage.driver.manager.local.controler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.ExtendedMessageListener;
import org.jgroups.ExtendedReceiver;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.sodfs.utils.MessageUtil;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicationControler implements ExtendedReceiver {    
    
    private static final String CONTROL_CHANNEL_PREFIX = "con";
    private int fileId;
    private int storageId;
    private JChannel controlChannel;
    
    private int minNOR;
    private TreeSet<Integer> replicaOwners;
    
    private CountDownLatch joinLatch = new CountDownLatch(1);
    private CountDownLatch tryLeaveLatch;
    private boolean leaveSuccess = false;
    private CountDownLatch tryReturnLatch = new CountDownLatch(1);    
    private boolean returnNeeded = false;
    
    private static Logger logger = Logger.getLogger(ReplicationControler.class.getName());
    
    public ReplicationControler(int fileId, int storageId, JChannel controlChannel) {        
        this.fileId = fileId;
        this.storageId = storageId;
        this.controlChannel = controlChannel;
        controlChannel.setReceiver(this);        
    }

    public void close() {
        controlChannel.disconnect();
        controlChannel.close();
    }
    
    public void initiate(int minNOR) throws InterruptedException, ChannelException {        
        replicaOwners = new TreeSet<Integer>();        
        this.minNOR = minNOR;
        registerNewReplicaHolder();
    }
    
    public void join() throws ChannelException, InterruptedException {        
        registerNewReplicaHolder();
    }
    
    private void registerNewReplicaHolder() throws ChannelException, InterruptedException {
        controlChannel.setReceiver(this);  
        controlChannel.connect(CONTROL_CHANNEL_PREFIX + Integer.toString(fileId), null, null, 0);        
        Message msg = MessageUtil.createMessage(new JoinOrder(storageId));
        controlChannel.send(msg);
        joinLatch.await();        
    }
    
    public boolean tryLeave() throws ChannelException, InterruptedException {
        tryLeaveLatch = new CountDownLatch(1);
        Message msg = MessageUtil.createMessage(new LeaveRequest(storageId));
        controlChannel.send(msg);
        tryLeaveLatch.await();
        return leaveSuccess;
    }
    
    public boolean tryReturn(boolean isActiveOrMoving, int minNOR) throws ChannelException, InterruptedException {
        controlChannel.connect(CONTROL_CHANNEL_PREFIX + fileId, null, null, 0);
        Message msg = MessageUtil.createMessage(new ReturnRequest(storageId, isActiveOrMoving, minNOR));
        controlChannel.send(msg);
        tryReturnLatch.await();
        return returnNeeded;
    }

    public void receive(Message message) {
        Object payload = message.getObject();
        if (payload instanceof JoinOrder) {
            JoinOrder jo = (JoinOrder) payload;
            synchronized(replicaOwners) {
                replicaOwners.add(jo.getStorageId());
            }            
            if (jo.getStorageId() == storageId) joinLatch.countDown();
        } else if (payload instanceof LeaveRequest) {
            LeaveRequest lr = (LeaveRequest) payload;
            if (replicaOwners.size() > minNOR) {
                synchronized(replicaOwners) {
                    replicaOwners.remove(lr.getStorageId());
                }                
                leaveSuccess = true;                
            } else {
                leaveSuccess = false;
            }
            if (lr.getStorageId() == storageId) tryLeaveLatch.countDown();
        } else if (payload instanceof ReturnRequest) {
            ReturnRequest rr = (ReturnRequest) payload;
            //synchronized(replicaOwners) {            
            returnNeeded = !(rr.isActiveOrMoving() && replicaOwners != null 
                                && !replicaOwners.contains(rr.getStorageId()));
            if (replicaOwners == null && returnNeeded) {
                replicaOwners = new TreeSet<Integer>();
                this.minNOR = rr.getMinNOR();
            }
            //}
            if (rr.getStorageId() == storageId) tryReturnLatch.countDown();
        }
    }

    public void getState(OutputStream out) {
        ObjectOutputStream oout = null;
        int minNORCopy = minNOR;
        TreeSet<Integer> replicaOwnersCopy = null;
        synchronized(replicaOwners) {
             replicaOwnersCopy = (TreeSet<Integer>) replicaOwners.clone();
        }          
        try {
            oout = new ObjectOutputStream(out);
            oout.writeInt(minNORCopy);            
            oout.writeObject(replicaOwnersCopy);
            oout.flush();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to send state.", ex);            
        } finally {
            try {
                oout.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to close state transfer stream.", ex);
            }
        }
        
    }

    public void setState(InputStream in) {
        System.out.println("Setting state!!");
        ObjectInputStream oin = null;
        try {            
            oin = new ObjectInputStream(in);
            minNOR = oin.readInt();            
            replicaOwners = (TreeSet<Integer>) oin.readObject();
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "State object was wrong type.", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to receive state.", ex);            
        } finally {
            try {
                oin.close();
                in.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to close state transfer stream after receiving.", ex);
            }
        }        
    }
    
    /* NOT TO BE IMPLEMENTED */
    
    public byte[] getState() {
        return null;
    }

    public void setState(byte[] arg0) {        
    }

    public byte[] getState(String arg0) {
        return null;
    }

    public void setState(String arg0, byte[] arg1) {
    }
    
    public void getState(String arg0, OutputStream arg1) {
    }

    public void setState(String arg0, InputStream arg1) {
    }

    public void viewAccepted(View arg0) {
    }

    public void suspect(Address arg0) {
    }

    public void block() {        
    }

    public void unblock() {
    }
}
