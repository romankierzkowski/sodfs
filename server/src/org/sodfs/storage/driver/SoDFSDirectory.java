package org.sodfs.storage.driver;

import java.io.IOException;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.sodfs.storage.meta.api.NodeEntity;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSDirectory extends NetworkFile {
    private static final String NOT_SUPPORTED_FOR_DIR = "This operation is not supported for a directory.";

    public SoDFSDirectory(String path, NodeEntity node) {
        super(node.getName());
        setFullName(path);
        setName(node.getName());
        setDirectoryId(node.getNodeId());
        setModifyDate(node.getModified().getTime());      
        setFileId(node.getNodeId());
        
     
        // times
        setCreationDate(node.getCreated().getTime());        
        setAccessDate(node.getAccessed().getTime());

        // size
        setFileSize(node.getNominalSize());        

        // attributes
        setGrantedAccess(node.getMode());
        setAttributes(node.getAttribute());
    }
    
    @Override
    public void openFile(boolean createFlag) throws IOException {        
    }

    @Override
    public int readFile(byte[] buf, int len, int pos, long fileOff) throws IOException {
        throw new UnsupportedOperationException(NOT_SUPPORTED_FOR_DIR);
    }

    @Override
    public void writeFile(byte[] buf, int len, int pos, long fileOff) throws IOException {
        throw new UnsupportedOperationException(NOT_SUPPORTED_FOR_DIR);
    }

    @Override
    public long seekFile(long pos, int typ) throws IOException {
        throw new UnsupportedOperationException(NOT_SUPPORTED_FOR_DIR);
    }

    @Override
    public void flushFile() throws IOException {
        throw new UnsupportedOperationException(NOT_SUPPORTED_FOR_DIR);
    }

    @Override
    public void truncateFile(long siz) throws IOException {
        throw new UnsupportedOperationException(NOT_SUPPORTED_FOR_DIR);
    }

    @Override
    public void closeFile() throws IOException {        
    }

}
