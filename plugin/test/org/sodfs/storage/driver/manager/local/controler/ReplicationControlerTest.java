package org.sodfs.storage.driver.manager.local.controler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.ExtendedReceiver;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sodfs.meta.persistance.StorageServer;
import org.sodfs.storage.communication.GroupCommunicator;
import org.sodfs.storage.driver.manager.MovableFileInterface;
import org.sodfs.storage.driver.manager.local.LocalFileManager;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.StorageServerEntity;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicationControlerTest {

    public ReplicationControlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void test1() throws InterruptedException, ChannelException, FileNotFoundException, MetaDataServiceNotAvilableException {
//        JChannel cc = new JChannel("test/tcp-nio.xml");  
//        ReplicationControler rc = new ReplicationControler(10, 10, cc);
//        rc.initiate(10);
//        FileOutputStream fo = new FileOutputStream("test/teststate.bin");
//        rc.getState(fo);
//        FileInputStream fi = new FileInputStream("test/teststate.bin");
//        rc.setState(fi);
//        rc.close();
    }
        /**
     * Test of join method, of class ReplicationControler.
     */
    @Test
    public void groups() throws Exception {
//        String address = "192.168.1.64";
//        GroupCommunicator gcom1 = new GroupCommunicator(address, 7803, "1");
//        MetaDataServiceInterface mds1 = getMDSMock(1,1, address);
//        gcom1.setMetaDataService(mds1);
//        
//        GroupCommunicator gcom2 = new GroupCommunicator(address, 7804, "2");
//        MetaDataServiceInterface mds2 = getMDSMock(2,1, address, 7803);
//        gcom2.setMetaDataService(mds2);
//        
//        GroupCommunicator gcom3 = new GroupCommunicator(address, 7805, "3");
//        MetaDataServiceInterface mds3 = getMDSMock(3,1, address, 7804);
//        gcom3.setMetaDataService(mds3); 
//        
//        JChannel c1 = gcom1.getControlChannel(1);
//        JChannel c2 = gcom2.getControlChannel(1);
//        JChannel c3 = gcom3.getControlChannel(1);
        
//        ReplicationControler rc1 = new ReplicationControler(1, 1, c1);
//        ReplicationControler rc2 = new ReplicationControler(1, 2, c2);
//        ReplicationControler rc3 = new ReplicationControler(1, 3, c3);
        
//        rc1.initiate(1);
//        rc2.join();
//        rc3.join();
        
//        assertTrue(rc3.tryLeave());
//        rc3.close();
//        
//        c3 = gcom3.getControlChannel(1);
//        rc3 = new ReplicationControler(1, 3, c3);
//        rc3.join();
//        
//        assertTrue(rc1.tryLeave());
//        rc1.close();
//        
//        c1 = gcom1.getControlChannel(1);
//        rc1 = new ReplicationControler(1, 1, c1);
//        rc1.join();
//        
//        assertTrue(rc2.tryLeave());
//        rc2.close();
//        
//        c2 = gcom2.getControlChannel(1);
//        rc2 = new ReplicationControler(1, 2, c2);
//        rc2.join();
//        
//        assertTrue(rc1.tryLeave());
//        rc1.close();
//        
//        c1 = gcom1.getControlChannel(1);
//        rc1 = new ReplicationControler(1, 1, c1);
//        rc1.join();
//        
//        assertTrue(rc2.tryLeave());
//        rc2.close();
//        
//        c2 = gcom2.getControlChannel(1);
//        rc2 = new ReplicationControler(1, 2, c2);
//        rc2.join();
        
        
//        registerReceiver(new TreeSet<Integer>(),1, c1);
//        registerReceiver(null, 2, c2);
//        registerReceiver(null, 3, c3);
//
//        c1.connect("con" + 1, null, null, 0);        
//        c1.send(new Message());
//        
//        c2.connect("con" + 1, null, null, 0);
//        c2.send(new Message());
//        
//        c3.connect("con" + 1, null, null, 0);
//        c3.send(new Message());  
//
//        c3.disconnect();
//        c3.close();        
//        
//        for (int i = 0; i < 10; i++) {
//            
//            c1.disconnect();
//            c1.close();
//
//            c3 = gcom3.getControlChannel(1);
//            registerReceiver(null, 3, c3);
//            c3.connect("con" + 1, null, null, 0);
//            c3.send(new Message());
//
//            c2.disconnect();
//            c2.close();
//
//            c1 = gcom1.getControlChannel(1);
//            registerReceiver(null, 3, c1);
//            c1.connect("con" + 1, null, null, 0);
//            c1.send(new Message());
//
//            c2 = gcom2.getControlChannel(1);
//            registerReceiver(null, 3, c2);
//            c2.connect("con" + 1, null, null, 0);
//            c2.send(new Message());
//
//            c3.disconnect();
//            c3.close();
//
//        }


    }
    
    

    
    /**
     * Test of join method, of class ReplicationControler.
     */
    @Test
    public void join() throws Exception {
        String address = "192.168.1.64";
        GroupCommunicator gcom1 = new GroupCommunicator(address, 7803, "1");
        MetaDataServiceInterface mds1 = getMDSMock(1,1, address);
        gcom1.setMetaDataService(mds1);
        LocalFileManager lfm1 = new LocalFileManager(1, "./test/1/");
        lfm1.setMetaDataService(mds1);
        lfm1.setGroupCommunicator(gcom1);
        
        GroupCommunicator gcom2 = new GroupCommunicator(address, 7804, "2");
        MetaDataServiceInterface mds2 = getMDSMock(2,1, address, 7803);
        gcom2.setMetaDataService(mds2);
        LocalFileManager lfm2 = new LocalFileManager(2, "./test/2/");
        lfm2.setMetaDataService(mds2);
        lfm2.setGroupCommunicator(gcom2);
        
        GroupCommunicator gcom3 = new GroupCommunicator(address, 7805, "3");
        MetaDataServiceInterface mds3 = getMDSMock(3,1, address, 7804);
        gcom3.setMetaDataService(mds3); 
        LocalFileManager lfm3 = new LocalFileManager(3, "./test/3/");
        lfm3.setMetaDataService(mds3);
        lfm3.setGroupCommunicator(gcom3);
        
        lfm1.create(1,1,0);
        MovableFileInterface rep = lfm1.getLocalReplica(1);
        rep.openFile(false);
        byte[] a = {54,43,34};
        rep.writeFile(a, 0);
        rep.closeFile();
        
        int i = 0;
        while(true) {
//            lfm2.replicate(1,0);             
//            lfm3.replicate(1, 0);
//            if (i % 2 == 0) lfm1.dereplicate(1);
//            lfm2.dereplicate(1);
//            lfm3.dereplicate(1);             
//            if (i % 2 == 0) lfm1.replicate(1, 0);
//            i++;
        }

        
        
        
//        verify(mds1);
//        verify(mds2);
//        verify(mds3);
//        lfm3.getLocalReplica(1);
        
        //System.exit(0);
//        JChannel cc1 = gcom1.getControlChannel(1);
//        JChannel cc2 = gcom2.getControlChannel(1);        
//        JChannel cc3 = gcom3.getControlChannel(1);
//        
//        ReplicationControler instance1 = new ReplicationControler(1, 1, cc1);        
//        ReplicationControler instance2 = new ReplicationControler(1, 2, cc2);        
//        ReplicationControler instance3 = new ReplicationControler(1, 3, cc3);
//        instance1.initiate(2);   
//        assertFalse(instance1.tryLeave());        
// 
//        instance2.join();
//        assertFalse(instance1.tryLeave());  
//        assertFalse(instance2.tryLeave());
//
//        instance3.join();
//        assertTrue(instance1.tryLeave());  
//        assertFalse(instance2.tryLeave());
//        assertFalse(instance3.tryLeave());
//        instance1.close();
//        instance2.close();
//        instance3.close();
    }

    private MetaDataServiceInterface getMDSMock(int storageId, int fileId, String address, int ...args) throws MetaDataServiceNotAvilableException {
        MetaDataServiceInterface mds = createNiceMock(MetaDataServiceInterface.class);
        ArrayList<StorageServerEntity> list = new ArrayList<StorageServerEntity>();
        for (int i = 0; i < args.length; i++) {
            StorageServer sse = new StorageServer();            
            sse.setMulticastAddress(address);
            sse.setMulticastPort(args[i]);
            list.add(sse);
        }
        expect(mds.registerReplica(storageId, fileId)).andStubReturn(true);
        expect(mds.getStorageServersHoldingFileReplica(fileId)).andStubReturn(list);        
        expect(mds.activateReplica(storageId, fileId)).andStubReturn(true);        
        replay(mds);
        return mds;
    }

    private void registerReceiver(TreeSet<Integer> ts, int id, JChannel c1) throws UnsupportedOperationException {
        c1.setReceiver(new ExtendedReceiverImpl(ts, id));
    }

    private class ExtendedReceiverImpl implements ExtendedReceiver {

        public ExtendedReceiverImpl(TreeSet<Integer> ts, int id) {
            this.ts = ts;
            this.id = id;
        }
        
        private TreeSet<Integer> ts;
        private int id;
        private int i = 0;

        public void receive(Message arg0) {
//            try {
                ts.add(i++);
                System.out.println(id + "receive()! " + i);
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ReplicationControlerTest.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        public byte[] getState() {
            System.out.println(id + "getState()!");
            return null;
        }

        public void setState(byte[] arg0) {
            System.out.println(id + "setState(byte[] arg0)!");
        }

        public void viewAccepted(View arg0) {
            System.out.println(id + "viewAccepted(View arg0)!");
        }

        public void suspect(Address arg0) {
            System.out.println(id + "suspect(Address arg0)!");
        }

        public void block() {
            System.out.println(id + "block()");
        }

        public byte[] getState(String arg0) {
            System.out.println(id + "getState(String arg0)!");
            return null;
        }

        public void setState(String arg0, byte[] arg1) {
            System.out.println(id + "setState(String arg0, byte[] arg1)!");
        }

        public void getState(OutputStream out) {

            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(out);
                oos.writeObject(ts);
                System.out.println(id + "getState(OutputStream arg0)!");
                //Thread.sleep(10000);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    oos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void getState(String arg0, OutputStream arg1) {
            System.out.println(id + "getState(String arg0, OutputStream arg1)!");
        }

        public void setState(InputStream in) {
            ObjectInputStream oin = null;
            try {
                System.out.println(id + "setState(InputStream arg0)!");
                oin = new ObjectInputStream(in);
                ts = (TreeSet) oin.readObject();
                System.out.println(ts);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    oin.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void setState(String arg0, InputStream arg1) {
            System.out.println(id + "setState(String arg0, InputStream arg1)!");
        }

        public void unblock() {
            System.out.println(id + "unblock()!");
        }
    }
//
//    /**
//     * Test of tryLeave method, of class ReplicationControler.
//     */
//    @Test
//    public void tryLeave() throws Exception {
//        System.out.println("tryLeave");
//        ReplicationControler instance = null;
//        boolean expResult = false;
//        boolean result = instance.tryLeave();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryReturn method, of class ReplicationControler.
//     */
//    @Test
//    public void tryReturn() throws Exception {
//        System.out.println("tryReturn");
//        boolean isActiveOrMoving = false;
//        int minNOR = 0;
//        ReplicationControler instance = null;
//        boolean expResult = false;
//        boolean result = instance.tryReturn(isActiveOrMoving, minNOR);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
}