package org.sodfs.storage.driver;

import org.sodfs.storage.driver.SharedDeviceMock;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.alfresco.config.ConfigElement;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.filesys.AccessDeniedException;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileExistsException;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.SearchContext;
import org.alfresco.jlan.server.filesys.TreeConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sodfs.meta.api.MetaServerInterface;
import org.sodfs.meta.server.MetaDataManipulator;
import org.sodfs.meta.server.SizeActualisator;
import org.sodfs.storage.meta.MetaDataManager;
import static org.junit.Assert.*;
import static org.sodfs.test.common.TestNamespace.*;
import static org.sodfs.storage.driver.config.SoDFSConfigurationManager.*;
import static org.sodfs.storage.driver.SoDFSPath.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSStorageServerTest {
    
    private MetaServerInterface msi = null;
    private SrvSession sess = null;
    private TreeConnection conn = null;
    private SoDFSDeviceContext ctx = null;
    private SoDFSStorageServer storage; 
    

    public SoDFSStorageServerTest() {
        ctx = new SoDFSDeviceContext();        
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoDFSPU");
            SizeActualisator sur = new SizeActualisator(emf);
            MetaDataManipulator instance = new MetaDataManipulator(emf, sur);
            MetaDataManager mdm = new MetaDataManager(instance);
            ctx.setMetaDataService(mdm);
            SharedDevice sd = new SharedDeviceMock(ctx);
            conn = new TreeConnection(sd);
            storage = new SoDFSStorageServer();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {     
    }
    
    /**
     * Test of createContext method, of class SoDFSStorageServer.
     */
    @Test
    public void createContext() throws Exception {
//        ConfigElement config = new ConfigElement("driver","");
//        
//        ConfigElement storageServerConfig = new ConfigElement(STORAGE_SERVER_CONFIG_ELEMENT, "");
//        ConfigElement name = new ConfigElement(STORAGE_SERVER_NAME_ELEMENT, "second");
//        ConfigElement host = new ConfigElement(STORAGE_SERVER_HOST_ELEMENT, "localhost");
//        ConfigElement port = new ConfigElement(STORAGE_SERVER_PORT_ELEMENT, "3345");
//        ConfigElement mAdd = new ConfigElement(STORAGE_SERVER_MULTICAST_ADDRESS_ELEMENT, "localhost");
//        ConfigElement mPort = new ConfigElement(STORAGE_SERVER_MULTICAST_PORT_ELEMENT, "3346");
//        ConfigElement sPath = new ConfigElement(STORAGE_SERVER_STORAGE_PATH_ELEMENT, "C:");
//        storageServerConfig.addChild(name);
//        storageServerConfig.addChild(host);
//        storageServerConfig.addChild(port);
//        storageServerConfig.addChild(mAdd);
//        storageServerConfig.addChild(mPort);
//        storageServerConfig.addChild(sPath);
//        config.addChild(storageServerConfig);
//        
//        ConfigElement metaServer = new ConfigElement(META_SERVER_CONFIG_ELEMENT,"");
//        metaServer.addAttribute(META_SERVER_HOST_ATTRIBUTE, "127.0.0.1");
//        metaServer.addAttribute(META_SERVER_PORT_ATTRIBUTE,"1099");
//        metaServer.addAttribute(META_SERVER_NAME_ATTRIBUTE,"metaServer");        
//        config.addChild(metaServer);
//        
//        ConfigElement sorpa = new ConfigElement(SORPA_CONFIG_ELEMENT,"");
//        sorpa.addAttribute(SORPA_CONFIG_PC_ATTRIBUTE, "0.3");
//        sorpa.addAttribute(SORPA_CONFIG_K_ATTRIBUTE, "0.2");
//        sorpa.addAttribute(SORPA_CONFIG_TTL_ATTRIBUTE, "10000");
//        sorpa.addAttribute(SORPA_CONFIG_PIN_ATTRIBUTE, "10000");
//        sorpa.addAttribute(SORPA_CONFIG_MIN_NOR_ATTRIBUTE,"3");
//        sorpa.addAttribute(SORPA_CONFIG_RF_ATTRIBUTE,"0.5");
//        sorpa.addAttribute(SORPA_CONFIG_DRF_ATTRIBUTE,"0.1");
//        config.addChild(sorpa);
//
//        DeviceContext context = storage.createContext("test", config);
//        assertNotNull(context);
//        assertTrue(context instanceof SoDFSDeviceContext);
        //SoDFSDeviceContext sodfsContext = (SoDFSDeviceContext) context; 
    }
    
    /**
     * Test of createDirectory method, of class SoDFSStorageServer.
     */
    @Test
    public void createDirectory() throws Exception {
        // create a not existing directory in the root
        createAndDeleteDirectory(ROOT + DIR_TO_CREATE);        
        // create a not existing directory in a directory
        createAndDeleteDirectory(EXISTING_DIRECTORY + SEPARATOR + DIR_TO_CREATE);          
        // create a not existing directory in a subdirectory
        createAndDeleteDirectory(EXISTING_SUBDIRECTORY + SEPARATOR + DIR_TO_CREATE);        
        // try to create an existing directory in a root       
        tryToCreateDirectory(EXISTING_DIRECTORY, FileExistsException.class);        
        // try to create an existing subdirectory in a directory
        tryToCreateDirectory(EXISTING_SUBDIRECTORY, FileExistsException.class);         
        // try to create a directory in a not existing directory
        tryToCreateDirectory(NOT_EXISTING_DIRECTORY + SEPARATOR + DIR_TO_CREATE, AccessDeniedException.class);        
        // try to create a directory in a not existing subdirectory        
        tryToCreateDirectory(NOT_EXISTING_SUBDIRECTORY + SEPARATOR + DIR_TO_CREATE, AccessDeniedException.class);
    }
    
    private void createAndDeleteDirectory(String path) throws IOException {
        FileOpenParams toCreate = new FileOpenParams(path, 0, 0, FileAttribute.Directory);
        storage.createDirectory(sess, conn, toCreate);
        storage.deleteDirectory(sess, conn, path);
    }


    
    private void tryToCreateDirectory(String path, Class exception) {
        boolean exceptionThrown = false;
        try {
            FileOpenParams toCreate = new FileOpenParams(path, 0, 0, FileAttribute.Directory);
            storage.createDirectory(sess, conn, toCreate);    
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Test of createFile method, of class SoDFSStorageServer.
     */
    @Test
    public void createFile() throws Exception {        
        // create a not existing file in the root
        createAndDeleteFile(ROOT + FILE_TO_CREATE);        
        // create a not existing file in a directory
        createAndDeleteFile(EXISTING_DIRECTORY + SEPARATOR + FILE_TO_CREATE);          
        // create a not existing file in a subdirectory
        createAndDeleteFile(EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_CREATE);        
        // try to create an existing file in a root       
        tryToCreateFile(EXISTING_FILE, FileExistsException.class);        
        // try to create an existing file in a directory
        tryToCreateFile(EXISTING_FILE_IN_DIR, FileExistsException.class);        
        // try to create an existing file in a subdirectory
        tryToCreateFile(EXISTING_FILE_IN_SUBDIR, FileExistsException.class);        
        // try to create a file in a not existing directory
        tryToCreateFile(NOT_EXISTING_DIRECTORY + SEPARATOR + FILE_TO_CREATE, AccessDeniedException.class);        
        // try to create a file in a not existing subdirectory        
        tryToCreateFile(NOT_EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_CREATE, AccessDeniedException.class);    
    }
    
    private void createAndDeleteFile(String path) throws IOException {
        FileOpenParams toCreate = new FileOpenParams(path, 0, 0, FileAttribute.Normal);
        NetworkFile nf = storage.createFile(sess, conn, toCreate);
        assertNotNull(nf);
        storage.deleteFile(sess, conn, path);
    }
    
    private void tryToCreateFile(String path, Class exception) {
        boolean exceptionThrown = false;
        try {
            FileOpenParams toCreate = new FileOpenParams(path, 0, 0, FileAttribute.Normal);
            storage.createFile(sess, conn, toCreate);    
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Test of deleteDirectory method, of class SoDFSStorageServer.
     */
    @Test
    public void deleteDirectory() throws Exception {
        // delete a directory in the root
        deleteDirectoryAndCheck(ROOT + DIR_TO_DELETE);
        // delete a directory in a directory
        deleteDirectoryAndCheck(EXISTING_DIRECTORY + SEPARATOR + DIR_TO_DELETE);
        // delete a directory in a sudirectory
        deleteDirectoryAndCheck(EXISTING_SUBDIRECTORY + SEPARATOR + DIR_TO_DELETE);
        // try to delete a not existing directory in the root
        tryDeleteDirectory(NOT_EXISTING_DIRECTORY, AccessDeniedException.class);    
        // try to delete a not existing subdirectory
        tryDeleteDirectory(NOT_EXISTING_SUBDIRECTORY, AccessDeniedException.class);
        // try to delete a directory containing a file
        tryDeleteDirectory(DIR_WITH_FILE, AccessDeniedException.class); 
        // try to delete a directory containging another directory
        tryDeleteDirectory(DIR_WITH_SUBDIR, AccessDeniedException.class); 
    }
    
    private void deleteDirectoryAndCheck(String path) throws IOException {
        assertEquals(FileStatus.DirectoryExists, storage.fileExists(sess, conn, path));
        storage.deleteDirectory(sess, conn, path);
        assertEquals(FileStatus.NotExist, storage.fileExists(sess, conn, path));
    }
    
    private void tryDeleteDirectory(String path, Class exception) {
        boolean exceptionThrown = false;
        try {
            storage.deleteDirectory(sess, conn, path);
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }
    
    /**
     * Test of deleteFile method, of class SoDFSStorageServer.
     */
    @Test
    public void deleteFile() throws Exception {
        // delete an existing file in the root
        deleteFileAndCheck(ROOT + FILE_TO_DELETE);
        // delete an existing file in a directory
        deleteFileAndCheck(EXISTING_DIRECTORY + SEPARATOR + FILE_TO_DELETE);
        // delete an existing file in a subdirectory
        deleteFileAndCheck(EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_DELETE);
        // try to delete a not existing file
        tryDeleteFile(NOT_EXISTING_FILE, FileNotFoundException.class);
        // try to delete a file from a not existing directory
        tryDeleteFile(NOT_EXISTING_DIRECTORY + SEPARATOR + FILE_TO_DELETE, FileNotFoundException.class);
        // try to delete a file from a not existing subdirectory
        tryDeleteFile(NOT_EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_DELETE, FileNotFoundException.class);
    }
    
    private void deleteFileAndCheck(String path) throws IOException {
        assertEquals(FileStatus.FileExists, storage.fileExists(sess, conn, path));
        storage.deleteFile(sess, conn, path);
        assertEquals(FileStatus.NotExist, storage.fileExists(sess, conn, path));
    }
    
    private void tryDeleteFile(String path, Class exception) {
        boolean exceptionThrown = false;
        try {
            storage.deleteFile(sess, conn, path);
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }
    
    /**
     * Test of fileExists method, of class SoDFSStorageServer.
     */
    @Test
    public void fileExists() {
        int result;
        result = storage.fileExists(sess, conn, ROOT);
        assertEquals(FileStatus.DirectoryExists, result);
        result = storage.fileExists(sess, conn, NOT_EXISTING_FILE);
        assertEquals(FileStatus.NotExist, result);
        result = storage.fileExists(sess, conn, NOT_EXISTING_DIRECTORY);
        assertEquals(FileStatus.NotExist, result);
        result = storage.fileExists(sess, conn, EXISTING_FILE);
        assertEquals(FileStatus.FileExists, result);
        result = storage.fileExists(sess, conn, EXISTING_DIRECTORY);
        assertEquals(FileStatus.DirectoryExists, result);
        result = storage.fileExists(sess, conn, EXISTING_SUBDIRECTORY);
        assertEquals(FileStatus.DirectoryExists, result);
        result = storage.fileExists(sess, conn, NOT_EXISTING_SUBDIRECTORY);
        assertEquals(FileStatus.NotExist, result);
        result = storage.fileExists(sess, conn, EXISTING_FILE_IN_DIR);
        assertEquals(FileStatus.FileExists, result);
        result = storage.fileExists(sess, conn, EXISTING_FILE_IN_SUBDIR);
        assertEquals(FileStatus.FileExists, result);
        result = storage.fileExists(sess, conn, NOT_EXISTING_FILE_IN_SUB_DIR);
        assertEquals(FileStatus.NotExist, result);
    }

    /**
     * Test of getFileInformation method, of class SoDFSStorageServer.
     */
    @Test
    public void getFileInformation() throws Exception {
        FileInfo result;
        result = storage.getFileInformation(sess, conn, ROOT);
        assertEquals(result.getFileId(), ROOT_ID);
        assertEquals(result.getFileName(), ROOT);
        assertEquals(result.getShortName(), ROOT);
        assertEquals(result.getPath(), ROOT);
        assertTrue(result.hasAttribute(FileAttribute.Directory));

        result = storage.getFileInformation(sess, conn, EXISTING_DIRECTORY);
        assertEquals(result.getFileName(), DIR_NAME);
        assertEquals(result.getShortName(), DIR_NAME);
        assertEquals(result.getPath(), EXISTING_DIRECTORY);
        assertTrue(result.hasAttribute(FileAttribute.Directory));

        result = storage.getFileInformation(sess, conn, EXISTING_SUBDIRECTORY);
        assertEquals(result.getFileName(), SUB_NAME);
        assertEquals(result.getShortName(), SUB_NAME);
        assertEquals(result.getPath(), EXISTING_SUBDIRECTORY);
        assertTrue(result.hasAttribute(FileAttribute.Directory));

        result = storage.getFileInformation(sess, conn, EXISTING_FILE);
        assertEquals(result.getFileName(), FILE_NAME);
        assertEquals(result.getShortName(), FILE_NAME);
        assertEquals(result.getPath(), EXISTING_FILE);
        assertFalse(result.hasAttribute(FileAttribute.Directory));
        
        tryGetFileInfo(NOT_EXISTING_DIRECTORY, FileNotFoundException.class);
        tryGetFileInfo(NOT_EXISTING_SUBDIRECTORY, FileNotFoundException.class);
        tryGetFileInfo(NOT_EXISTING_FILE, FileNotFoundException.class);
    }
    
    private void tryGetFileInfo(String path, Class exception) {
        boolean exceptionThrown = false;
        try {
            storage.getFileInformation(sess, conn, path);
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }
    
    /**
     * Test of setFileInformation method, of class SoDFSStorageServer.
     */
    @Test
    public void setFileInformation() throws Exception {
    
    }
    
    /**
     * Test of isReadOnly method, of class SoDFSStorageServer.
     */
    @Test
    public void isReadOnly() throws Exception {
//        assertFalse(storage.isReadOnly(sess, ctx));
    }  
    
    /**
    * Test of renameFile method, of class SoDFSStorageServer.
    */
    @Test
    public void renameFile() throws Exception {
        // rename a file to the same directory        
        renameFileAndCheck(ROOT + FILE_TO_RENAME, ROOT + NEW_FILE_NAME);
        renameFileAndCheck(EXISTING_DIRECTORY + SEPARATOR + FILE_TO_RENAME, 
          EXISTING_DIRECTORY + SEPARATOR + NEW_FILE_NAME);
        renameFileAndCheck(EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_RENAME,
          EXISTING_SUBDIRECTORY + SEPARATOR + NEW_FILE_NAME);
        
        // rename a file to another directory
        renameFileAndCheck(ROOT + NEW_FILE_NAME, 
          EXISTING_DIRECTORY + SEPARATOR + FILE_TO_RENAME);
        renameFileAndCheck(EXISTING_DIRECTORY + SEPARATOR + NEW_FILE_NAME, 
          EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_RENAME);
        renameFileAndCheck(EXISTING_SUBDIRECTORY + SEPARATOR + NEW_FILE_NAME,
          ROOT + FILE_TO_RENAME);
        
        // try to rename a file to existing name
        tryRenameFile(ROOT + FILE_TO_RENAME, EXISTING_FILE, FileExistsException.class);
        tryRenameFile(EXISTING_DIRECTORY + SEPARATOR + FILE_TO_RENAME, 
          EXISTING_FILE_IN_DIR, FileExistsException.class);
        tryRenameFile(EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_RENAME,
          EXISTING_FILE_IN_SUBDIR, FileExistsException.class);
        
        // try to rename a file to not existing directory        
        tryRenameFile(EXISTING_DIRECTORY+ SEPARATOR + FILE_TO_RENAME , 
          NOT_EXISTING_DIRECTORY + SEPARATOR + NEW_FILE_NAME, FileNotFoundException.class);
        tryRenameFile(EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_RENAME,
          NOT_EXISTING_SUBDIRECTORY + SEPARATOR + NEW_FILE_NAME, FileNotFoundException.class);
        
        // try to rename a not existing file
        tryRenameFile(NOT_EXISTING_FILE, ROOT + NEW_FILE_NAME, FileNotFoundException.class);
        tryRenameFile(NOT_EXISTING_FILE_IN_SUB_DIR, ROOT + NEW_FILE_NAME, FileNotFoundException.class);
        
        // try to rename a file from not existing directory
        tryRenameFile(NOT_EXISTING_DIRECTORY + SEPARATOR  + FILE_TO_RENAME, 
          EXISTING_DIRECTORY + SEPARATOR + NEW_FILE_NAME, FileNotFoundException.class);
        tryRenameFile(NOT_EXISTING_SUBDIRECTORY + SEPARATOR + FILE_TO_RENAME,
          EXISTING_SUBDIRECTORY + SEPARATOR + NEW_FILE_NAME, FileNotFoundException.class);
        
        // try to rename a directory to existing name
        tryRenameDir(ROOT + DIR_TO_RENAME, EXISTING_DIRECTORY, FileExistsException.class);
        tryRenameDir(EXISTING_DIRECTORY + SEPARATOR + DIR_TO_RENAME, 
          EXISTING_FILE_IN_DIR, FileExistsException.class);
        tryRenameDir(EXISTING_SUBDIRECTORY + SEPARATOR + DIR_TO_RENAME,
          EXISTING_FILE_IN_SUBDIR, FileExistsException.class);
        
        // rename a directory to different name
        renameDirAndCheck(ROOT + DIR_TO_RENAME, ROOT + NEW_DIR_NAME);
        renameDirAndCheck(EXISTING_DIRECTORY + SEPARATOR + DIR_TO_RENAME, 
          EXISTING_DIRECTORY + SEPARATOR + NEW_DIR_NAME);
        renameDirAndCheck(EXISTING_SUBDIRECTORY + SEPARATOR + DIR_TO_RENAME,
          EXISTING_SUBDIRECTORY + SEPARATOR + NEW_DIR_NAME);        
        
        // try to rename a not existing directory
        tryRenameDir(NOT_EXISTING_DIRECTORY, ROOT + NEW_DIR_NAME, FileNotFoundException.class);
        tryRenameDir(NOT_EXISTING_SUBDIRECTORY, EXISTING_SUBDIRECTORY 
          + SEPARATOR + NEW_DIR_NAME, FileNotFoundException.class);
        
    }
    
        
    private void renameDirAndCheck(String oldPath, String newPath) throws IOException {
        assertEquals(FileStatus.DirectoryExists, storage.fileExists(sess, conn, oldPath));
        assertEquals(FileStatus.NotExist, storage.fileExists(sess, conn, newPath));
        storage.renameFile(sess, conn, oldPath, newPath);
        assertEquals(FileStatus.NotExist, storage.fileExists(sess, conn, oldPath));
        assertEquals(FileStatus.DirectoryExists, storage.fileExists(sess, conn, newPath));
    }
    
        
    private void tryRenameDir(String oldPath, String newPath, Class exception) {
        boolean exceptionThrown = false;
        try {
            storage.renameFile(sess, conn, oldPath, newPath);
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }
    
    private void renameFileAndCheck(String oldPath, String newPath) throws IOException {
        assertEquals(FileStatus.FileExists, storage.fileExists(sess, conn, oldPath));
        assertEquals(FileStatus.NotExist, storage.fileExists(sess, conn, newPath));
        storage.renameFile(sess, conn, oldPath, newPath);
        assertEquals(FileStatus.NotExist, storage.fileExists(sess, conn, oldPath));
        assertEquals(FileStatus.FileExists, storage.fileExists(sess, conn, newPath));
    }
    
        
    private void tryRenameFile(String oldPath, String newPath, Class exception) {
        boolean exceptionThrown = false;
        try {
            storage.renameFile(sess, conn, oldPath, newPath);
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Test of startSearch method, of class SoDFSStorageServer.
     */
    @Test
    public void startSearch() throws Exception {
        // search a directory        
        startSearchAndCheck(ROOT + STAR_WILDCARD,
          FileAttribute.Normal + FileAttribute.Directory);
        startSearchAndCheck(EXISTING_DIRECTORY + SEPARATOR + STAR_WILDCARD,
          FileAttribute.Normal + FileAttribute.Directory);
        startSearchAndCheck(EXISTING_SUBDIRECTORY + SEPARATOR + STAR_WILDCARD,
          FileAttribute.Normal + FileAttribute.Directory);
        
        // search for an existing file
        startSearchAndCheck(EXISTING_FILE,
          FileAttribute.Normal + FileAttribute.Directory);
        startSearchAndCheck(EXISTING_FILE_IN_DIR,
          FileAttribute.Normal + FileAttribute.Directory);
        startSearchAndCheck(EXISTING_FILE_IN_SUBDIR,
          FileAttribute.Normal + FileAttribute.Directory);
        
        // search for an existing directory
        startSearchAndCheck(ROOT,
          FileAttribute.Normal + FileAttribute.Directory);
        startSearchAndCheck(EXISTING_DIRECTORY,
          FileAttribute.Normal + FileAttribute.Directory);
        startSearchAndCheck(EXISTING_SUBDIRECTORY,
          FileAttribute.Normal + FileAttribute.Directory);
        
        // try to search for an not existing directory          
        tryStartSearch(NOT_EXISTING_DIRECTORY + SEPARATOR + STAR_WILDCARD,
          FileAttribute.Normal + FileAttribute.Directory, FileNotFoundException.class);
        tryStartSearch(NOT_EXISTING_SUBDIRECTORY + SEPARATOR + STAR_WILDCARD,
          FileAttribute.Normal + FileAttribute.Directory, FileNotFoundException.class);
        tryStartSearch(NOT_EXISTING_DIRECTORY, FileAttribute.Normal + FileAttribute.Directory,
          FileNotFoundException.class);
        tryStartSearch(NOT_EXISTING_SUBDIRECTORY, FileAttribute.Normal + FileAttribute.Directory,
          FileNotFoundException.class);
        
        // try to search for an not existing file
        tryStartSearch(NOT_EXISTING_FILE, FileAttribute.Normal + FileAttribute.Directory,
          FileNotFoundException.class);
        tryStartSearch(NOT_EXISTING_FILE_IN_SUB_DIR, FileAttribute.Normal + FileAttribute.Directory,
          FileNotFoundException.class);
    }  
    
    private void startSearchAndCheck(String path, int attr) throws FileNotFoundException {
        SearchContext context = storage.startSearch(sess, conn, path, attr);
        assertTrue(context instanceof SoDFSSearchContext);
        assertTrue(context.hasMoreFiles());
    }
    
    private void tryStartSearch(String path, int attr, Class exception) {
        boolean exceptionThrown = false;
        try {
            storage.startSearch(sess, conn, path, attr);
        } catch (Exception e) {
            if (e.getClass().equals(exception)) exceptionThrown = true;
            else fail("Expected " + exception.getCanonicalName() + ", but " + e.getClass().getCanonicalName() + " was thrown.");
        }
        assertTrue(exceptionThrown);
    }
//
//    /**
//     * Test of openFile method, of class SoDFSStorageServer.
//     */
//    @Test
//    public void openFile() throws Exception {
//    }
//
//    /**
//     * Test of readFile method, of class SoDFSStorageServer.
//     */
//    @Test
//    public void readFile() throws Exception {
//    }
//
//    /**
//     * Test of seekFile method, of class SoDFSStorageServer.
//     */
//    @Test
//    public void seekFile() throws Exception {
//    }
//
//    /**
//     * Test of truncateFile method, of class SoDFSStorageServer.
//     */
//    @Test
//    public void truncateFile() throws Exception {
//    }
//
//    /**
//     * Test of writeFile method, of class SoDFSStorageServer.
//     */
//    @Test
//    public void writeFile() throws Exception {
//    }
//
//    /**
//     * Test of closeFile method, of class SoDFSStorageServer.
//     */
//    @Test
//    public void closeFile() throws Exception {
//    }
//
//    /**
//     * Test of flushFile method, of class SoDFSStorageServer.
//     */
//    @Test
//    public void flushFile() throws Exception {
//    }    
}
