package org.sodfs.storage.communication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.StorageServerEntity;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Roman Kierzkowski
 */
public class GroupCommunicator {

    private String multicastAddress;
    private int multicastPort;
    private String transportSingletonName;
    
    private MetaDataServiceInterface mds;
    
    //private final File CONTROL_GROUPS_CONFIG  = new File("control_gr.xml");
    private final File CONTROL_GROUPS_CONFIG  = new File("control_gr.xml");
    private final File DATA_GROUPS_CONFIG  = new File("data_gr.xml");

    public void setMetaDataService(MetaDataServiceInterface mds) {
        this.mds = mds;
    }

    public GroupCommunicator(String multicastAddress, int multicastPort, String transportSingletonName) {
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.transportSingletonName = transportSingletonName;
        System.setProperty("jgroups.bind_addr", multicastAddress);
    }
    
    public JChannel getControlChannel(int fileId) throws ChannelException {
        try {
//            List<StorageServerEntity> initialHosts = mds.getStorageServersHoldingFileReplica(fileId);
//            Element root = createConfig(CONTROL_GROUPS_CONFIG, initialHosts);
            Element root = createConfig(CONTROL_GROUPS_CONFIG, null);
            JChannel result = new JChannel(root);
            result.setOpt(JChannel.BLOCK, true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ChannelException("Unable to create a control channel.", ex);
        } 
    }

    public JChannel getDataChannel(int fileId) throws ChannelException {
        try {
//            List<StorageServerEntity> initialHosts = mds.getStorageServersHoldingFileReplica(fileId);
//            Element root = createConfig(DATA_GROUPS_CONFIG, initialHosts);
            Element root = createConfig(DATA_GROUPS_CONFIG, null);
            JChannel result = new JChannel(root);
            result.setOpt(JChannel.BLOCK, true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ChannelException("Unable to create an update channel.", ex);
        } 
    }
    
    private Element createConfig(File file, List<StorageServerEntity> storageServers) throws IOException, ParserConfigurationException, SAXException, DOMException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        Element root = (Element) doc.getElementsByTagName("config").item(0);
        Element tcp = (Element) doc.getElementsByTagName("TCP").item(0);
        tcp.setAttribute("start_port", Integer.toString(multicastPort));
        tcp.setAttribute("singleton_name", transportSingletonName);
//        Element tcpPing = (Element) root.getElementsByTagName("TCPPING").item(0);
//        if (storageServers != null) {
//            StringBuilder initialHosts = new StringBuilder();
//            for (Iterator<StorageServerEntity> it = storageServers.iterator(); it.hasNext();) {
//                StorageServerEntity sse = it.next();
//                initialHosts.append(sse.getMulticastAddress());
//                initialHosts.append("[");
//                initialHosts.append(sse.getMulticastPort());
//                initialHosts.append("]");
//                if (it.hasNext()) {
//                    initialHosts.append(",");
//                }
//            }            
//            tcpPing.setAttribute("initial_hosts", initialHosts.toString());
//            tcpPing.setAttribute("num_initial_members", Integer.toString(storageServers.size()));            
//        }
        return root;
    }
}
