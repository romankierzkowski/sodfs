package org.sodfs.storage.driver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.alfresco.config.ConfigElement;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.DeviceContextException;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.SearchContext;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.sodfs.storage.driver.config.MetaServerAddress;
import org.sodfs.storage.driver.config.SORPAConfig;
import org.sodfs.storage.driver.config.SoDFSConfigurationManager;
import org.sodfs.storage.driver.config.StorageServerConfig;
import org.sodfs.storage.driver.manager.FileManager;
import org.sodfs.storage.meta.api.FileEntity;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.NodeEntity;
import org.sodfs.storage.replication.ReplicaPlacementManager;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSStorageServer  implements DiskInterface {
    private static final String MDS_NOT_AVILABLE_MSG = "Meta-data service not avilable.";

    private Logger logger = Logger.getLogger(SoDFSStorageServer.class.getName()); 
    
    /* DRIVER OPERATIONS */

    public DeviceContext createContext(String shareName, ConfigElement config) throws DeviceContextException {
        try {
            configureLoging();
            logger.log(Level.FINEST, "createContext(" + shareName + "," + config + ")");            
            SoDFSConfigurationManager cm = new SoDFSConfigurationManager(config);
            SoDFSDeviceContexBuilder builder = new SoDFSDeviceContexBuilder();
            MetaServerAddress msa = cm.getMetaServerAddress();
            StorageServerConfig ssc = cm.getStorageServerConfig();
            SORPAConfig sorpa = cm.getSorpaConfig();
            SoDFSDeviceContext dc = builder.getContext(msa, ssc, sorpa, shareName);
            dc.start();
            return dc;
//        } catch (RemoteException ex) {
//            logger.log(Level.SEVERE, null, ex);
//            throw new DeviceContextException("Unable to initialize device context because of: " + ex.getMessage());
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } catch (MetaDataServiceNotAvilableException ex) {
//            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);            
//            throw new DeviceContextException(MDS_NOT_AVILABLE_MSG);
//        } catch (DeviceContextException ex) {
//            ex.printStackTrace();
//            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DeviceContextException(ex.getMessage());
        }
        //return null;
    }    
    
    private void configureLoging() throws IOException, SecurityException {
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.SEVERE);
        Logger global = Logger.getLogger("org.sodfs");
        FileHandler fh = new FileHandler("log.txt");
        fh.setLevel(Level.SEVERE);
        fh.setFormatter(new SimpleFormatter());
        global.addHandler(fh);
        global.addHandler(ch);
        global.setLevel(Level.SEVERE);
    }

    public void treeOpened(SrvSession sess, TreeConnection tree) {
        ClientInfo ci = sess.getClientInformation();        
        logger.log(Level.FINE, "User " + ci.toString() + "logged to the system.");
    }

    public void treeClosed(SrvSession sess, TreeConnection tree) {
        ClientInfo ci = sess.getClientInformation(); 
        logger.log(Level.FINE, "User " + ci.toString() + "logged out from the system.");
    }
    
    /* NAMESPACE OPERATIONS */
    
    public void createDirectory(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException {
        logger.log(Level.FINEST, "createDirectory(" + params.getFullPath() + ")");
        try
        {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            int userId = params.getUid();
            int groupId = params.getGid();
            int mode = params.getMode();
            int attr = params.getAttributes();
            SoDFSPath path = new SoDFSPath(params.getFullPath());
            mdm.createDirectory(path, userId, groupId, mode, attr);
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
    }

    public void deleteDirectory(SrvSession sess, TreeConnection tree, String dir) throws IOException {
        logger.log(Level.FINEST, "deleteDirectory(" + dir + ")");
        try {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            mdm.deleteDirectory(new SoDFSPath(dir));
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
    }

    public NetworkFile createFile(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException {
        logger.log(Level.FINEST, "createFile(" + params.getFullPath() + ")");        
        SoDFSNetworkFile result = null;
        try {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            ReplicaPlacementManager rpm = ctx.getReplicaPlacementManager();
            int userId = params.getUid();
            int groupId = params.getGid();
            int mode = params.getMode();
            int attr = params.getAttributes();
            long pinTime = rpm.getDefaultPinTime();
            long coinTTL = rpm.getDefaultCoinTTL();
            int minNOR = rpm.getDefaultMinNOR();            
            SoDFSPath path = new SoDFSPath(params.getFullPath());
            FileEntity fe = mdm.createFile(path, userId, groupId, mode, attr, pinTime, coinTTL, minNOR);
            FileManager fm = ctx.getFileManager();
            fm.create(fe.getNodeId(), minNOR, pinTime);
            result = fm.getNetworkFile(fe.getNodeId());
            result.openFile(false);
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        } 
        return result;
    }

    public void deleteFile(SrvSession sess, TreeConnection tree, String file) throws IOException {
        logger.log(Level.FINEST, "deleteFile(" + file + ")");
        try {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            mdm.deleteFile(new SoDFSPath(file));
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
    }

    public void renameFile(SrvSession sess, TreeConnection tree, String oldName, String newName) throws IOException {
        logger.log(Level.FINEST, "renameFile(" + oldName + ", " + newName + ")");
        try {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            mdm.renameFile(new SoDFSPath(oldName), new SoDFSPath(newName));
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
    }

    public int fileExists(SrvSession sess, TreeConnection tree, String node) {
        logger.log(Level.FINEST, "fileExists(" + node + ") [" + (node == null)+ "," + "".equals(node)+ "]");
        int result = FileStatus.NotExist;
        try {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            result = mdm.fileExist(new SoDFSPath(node));
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
        return result;
    }

    public FileInfo getFileInformation(SrvSession sess, TreeConnection tree, String node) throws IOException {
        logger.log(Level.FINEST, "getFileInformation(" + node + ") [" + (node == null)+ "," + "".equals(node)+ "]");
        SoDFSFileInfo result = null;
        try {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            NodeEntity ne = mdm.getFileInformation(new SoDFSPath(node));
            result = new SoDFSFileInfo(node, ne);
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
        return result;
    }
    
    public void setFileInformation(SrvSession sess, TreeConnection tree, String node, FileInfo info) throws IOException {
        logger.log(Level.FINEST, "setFileInformation(" + info.getFileId() + " " + info.getFileName() + " " + info.getPath() + " " + info.hasDeleteOnClose() + ")");
        try {            
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            SoDFSPath path = new SoDFSPath(node);
            NodeEntity ne = mdm.getFileInformation(path);
            if (info.hasSetFlag(FileInfo.SetFileSize)) {
               ne.setNominalSize(info.getSize()); 
            }
            if (info.hasSetFlag(FileInfo.SetAllocationSize)) {
               ne.setAllocationSize(info.getAllocationSize());
            }           
            if (info.hasSetFlag(FileInfo.SetAttributes)) {
               int attrs = info.getFileAttributes();
               if (ne.isDirectory()) attrs = attrs | FileAttribute.Directory;
               ne.setAttribute(attrs);               
            }           
            if (info.hasSetFlag(FileInfo.SetModifyDate)) {
               ne.setModified(new Date(info.getModifyDateTime()));
            }           
            if (info.hasSetFlag(FileInfo.SetCreationDate)) {
               ne.setCreated(new Date(info.getCreationDateTime()));
            }           
            if (info.hasSetFlag(FileInfo.SetAccessDate)) {
               ne.setAccessed(new Date(info.getAccessDateTime()));
            }           
            if (info.hasSetFlag(FileInfo.SetChangeDate) && !ne.isDirectory()) {
               FileEntity file = (FileEntity) ne;
               file.setChanged(new Date(info.getChangeDateTime()));
            }           
            if (info.hasSetFlag(FileInfo.SetGid)) {
               ne.setGroupId(info.getGid());
            }           
            if (info.hasSetFlag(FileInfo.SetUid)) {
               ne.setUserId(info.getUid());
            }           
            if (info.hasSetFlag(FileInfo.SetMode)) {
               ne.setMode(info.getMode());
            }
            mdm.setFileInformation(path, ne);
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
    }

    public SearchContext startSearch(SrvSession sess, TreeConnection tree, String searchPath, int attrib) throws FileNotFoundException {
        logger.log(Level.FINEST, "startSearch(" + searchPath + ")");
        SearchContext result = null;
        try {
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            SoDFSPath path = new SoDFSPath(searchPath);
            List<NodeEntity> nel = mdm.search(path, attrib);
            result = new SoDFSSearchContext(nel, path.getParent());
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }
        return result;
    }

    public boolean isReadOnly(SrvSession sess, DeviceContext ctx) throws IOException {
        return false;
    }
    
    /* FILE OPERATIONS */

    public NetworkFile openFile(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException {
        logger.log(Level.FINEST, "openFile(" + params.getFullPath() + " " + params.isDeleteOnClose() + ")");
        NetworkFile result = null;        
        try {            
            SoDFSDeviceContext ctx = (SoDFSDeviceContext) tree.getContext();
            MetaDataServiceInterface mdm = ctx.getMetaDataService();
            FileManager fm = ctx.getFileManager();
            SoDFSPath path = new SoDFSPath(params.getFullPath());
            NodeEntity ne = mdm.getFileInformation(path);
            if (ne.isDirectory()) {
                result = new SoDFSDirectory(path.getPath(), ne);
            } else {                 
                SoDFSNetworkFile snf = fm.getNetworkFile(ne.getNodeId());
                snf.setMetaData((FileEntity) ne, params.getFullPath());
                snf.openFile(false);
                result = snf;
            }
        } catch (MetaDataServiceNotAvilableException ex) {
            logger.log(Level.SEVERE, MDS_NOT_AVILABLE_MSG, ex);
        }        
        return result;
    }

    public long seekFile(SrvSession sess, TreeConnection tree, NetworkFile file, long pos, int typ) throws IOException {
        logger.log(Level.FINEST, "seekFile(" + file.getFullName() + ")");
        return file.seekFile(pos, typ);
    }

    public int readFile(SrvSession sess, TreeConnection tree, NetworkFile file, byte[] buf, int bufPos, int siz, long filePos) throws IOException {
        logger.log(Level.FINEST, "readFile(" + file.getFullName() + ")");
        return file.readFile(buf, siz, bufPos, filePos);
    }

    public void truncateFile(SrvSession sess, TreeConnection tree, NetworkFile file, long siz) throws IOException {
        logger.log(Level.FINEST, "truncateFile(" + file.getFullName() + ")");
        file.truncateFile(siz);
    }

    public void flushFile(SrvSession sess, TreeConnection tree, NetworkFile file) throws IOException {
        logger.log(Level.FINEST, "flushFile(" + file.getFullName() + ")");
        file.flushFile();
    }

    public int writeFile(SrvSession sess, TreeConnection tree, NetworkFile file, byte[] buf, int bufoff, int siz, long fileoff) throws IOException {
        logger.log(Level.FINEST, "writeFile(" + file.getFullName() + ")");
        SoDFSNetworkFile nf = (SoDFSNetworkFile) file;
        nf.writeFile(buf, siz, bufoff, fileoff);
        return nf.getSizeOfWrittenData();
    }
    
    public void closeFile(SrvSession sess, TreeConnection tree, NetworkFile file) throws IOException {        
        logger.log(Level.FINEST, "closeFile(" + file.getFullName() + " " + file.hasDeleteOnClose() + ")");
        file.closeFile();
        if (file.hasDeleteOnClose()) deleteFile(sess, tree, file.getFullName());
    }

}
