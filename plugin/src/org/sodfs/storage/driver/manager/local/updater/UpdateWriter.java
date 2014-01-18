package org.sodfs.storage.driver.manager.local.updater;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.ExtendedMessageListener;
import org.jgroups.ExtendedReceiver;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.util.RspList;
import org.sodfs.utils.MessageUtil;
import org.sodfs.storage.driver.manager.local.ReplicaState;
import org.sodfs.storage.driver.manager.local.ReplicaStateChangeListener;
import org.sodfs.storage.driver.manager.local.ReplicaStateManager;

/**
 *
 * @author Roman Kierzkowski
 */
public class UpdateWriter implements RequestHandler, ExtendedMessageListener, ReplicaStateChangeListener, MembershipListener {
    private MessageDispatcher dispatcher;
    private FileChannel fc;
    private int fileId;
    private JChannel dataChannel;
    private ReplicaStateManager replicaStateManager;
    private SizeUpdateSender sizeUpdateSender;
    
    private ReplicaState state = ReplicaState.CONSISTENT;
    private int destinationStorageId;
    
    private static Logger logger = Logger.getLogger(UpdateWriter.class.getName());
    private boolean isSizeUpdater = false;
    
    public UpdateWriter(int fileId, JChannel dataChannel, String path, ReplicaStateManager replicaStateManager, SizeUpdateSender sizeUpdateSender) throws FileNotFoundException {
        this.dataChannel = dataChannel;        
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        fc = raf.getChannel();
        this.fileId = fileId;
        this.replicaStateManager = replicaStateManager;
        this.sizeUpdateSender = sizeUpdateSender;
    }
    
    public Object handle(Message updateMessage) {
        boolean failure = false;
        Object result = null;
        long sizeBefore = 0;
        long sizeAfter = 0;
        synchronized (this) {            
            if (state == ReplicaState.CONSISTENT) {
                Object order = updateMessage.getObject();
                try {
                    if(isSizeUpdater) sizeBefore = fc.size();
                    if (order instanceof WriteOrder) {                        
                        WriteOrder wo = (WriteOrder) order;
                        ByteBuffer bb = ByteBuffer.wrap(wo.getBuffor());
                        result = fc.write(bb, wo.getFileOffset());                        
                    } else if (order instanceof TruncateOrder) {                        
                        TruncateOrder to = (TruncateOrder) order;
                        fc.truncate(to.getSize());
                        result = true;
                    } else if (order instanceof FlushOrder) {
                        fc.force(false);
                        result = true;
                    }
                    if(isSizeUpdater) sizeAfter = fc.size();
                    if (sizeAfter != sizeBefore) sizeUpdateSender.updateSize(fileId, sizeAfter);
                } catch (IOException ex) {                
                    failure = true;
                    logger.log(Level.SEVERE, "Replica " + fileId + " was detected to be corrupted during modification operation.", ex);
                }
            }
        }
        //if (failure) replicaStateManager.reportFailure();
        return result;        
    }
    
    public void start() throws ChannelException { 
        dispatcher = new MessageDispatcher(dataChannel, this, this, this);
        dataChannel.connect(Integer.toString(fileId), null, null, 0);        
        synchronized(dispatcher) {
            dispatcher.start();
        }
    }
    
    public void stop() {
        try {            
            synchronized(dispatcher) {
                dispatcher.stop();                
            }
            dataChannel.disconnect();
            dataChannel.close();
            fc.close();
        } catch (IOException ex) {            
            Logger.getLogger(UpdateWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /* ORDERS */

    public int write(byte[] buf, long off) throws IOException {
        WriteOrder wo = new WriteOrder(buf, off);
        Object returnedValue = giveOrder(wo);
        return (Integer) returnedValue;
    }
    
    public void flush() throws IOException {
        FlushOrder fo = new FlushOrder();
        giveOrder(fo);
    }

    public void truncate(long size) throws IOException {
        TruncateOrder to = new TruncateOrder(size);
        giveOrder(to);
    }
    
    private Object giveOrder(Serializable wo) throws IOException {
        Object result = null;
        Message message = MessageUtil.createMessage(wo);
        RspList returned;
        synchronized (dispatcher) {
            returned = dispatcher.castMessage(null, message, GroupRequest.GET_ALL, 0);
        }        
        Vector<Object> values = returned.getResults();
        Iterator<Object> iter = values.iterator();
        boolean notNullOccured = false;
        while (!notNullOccured && iter.hasNext()) {
            Object v = iter.next();
            if (v != null) {
                notNullOccured = true;
                result = v;
            }
        }
        if (!notNullOccured) {
            throw new IOException("No replica has been possible to be modified!");
        }
        return result;
    }
    
    /* STATE TRANSFER */

    public void getState(OutputStream state) {
        try {
            WritableByteChannel out = Channels.newChannel(state);
            fc.transferTo(0, Integer.MAX_VALUE, out);
        } catch (IOException ex) {
            Logger.getLogger(UpdateWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                state.close();
            } catch (IOException ex) {
                Logger.getLogger(UpdateWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public void setState(InputStream state) {
        System.out.println("TRANSFERING STATE...");
        boolean successful = true;
        try {            
            ReadableByteChannel in = Channels.newChannel(state);
            fc.truncate(0);            
            fc.transferFrom(in, 0, Integer.MAX_VALUE);            
            fc.force(true);            
        } catch (IOException ex) {
            successful = false;
            Logger.getLogger(UpdateWriter.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                state.close();
            } catch (IOException ex) {
                successful = false;
                Logger.getLogger(UpdateWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (successful) replicaStateManager.reportConsistance();
    }
    
    public void replicaStateChanged(ReplicaState state, int destinationStorageId) {        
        synchronized (this) {
            this.state = state;
            this.destinationStorageId = destinationStorageId;
        }
    }

    public void viewAccepted(View view) {
        Vector<Address> members = (Vector<Address>) view.getMembers().clone();
        Collections.sort(members);
        Address sizeUpdater = members.get(0);
        Address me = dataChannel.getLocalAddress();
        isSizeUpdater = (sizeUpdater.compareTo(me) == 0);
    }    
    
    /* NOT TO BE IMPLEMENTED */
    
    public byte[] getState(String stateId) {
        return null;
    }

    public void setState(String stateId, byte[] state) {
    }

    public void getState(String stateId, OutputStream state) {
    }
    
    public void setState(String stateId, InputStream state) {
    }

    public void receive(Message message) {
        System.out.println(message);
    }

    public byte[] getState() {
        return null;
    }

    public void setState(byte[] arg0) {
    }

    public void suspect(Address arg0) {        
    }

    public void block() {        
    }

    public void unblock() {
    }
}
