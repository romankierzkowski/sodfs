package org.sodfs.storage.driver;

import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileType;
import org.sodfs.storage.meta.api.FileEntity;
import org.sodfs.storage.meta.api.NodeEntity;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSFileInfo extends FileInfo {

    public SoDFSFileInfo(String path, NodeEntity node) {
        
        super(node.getName(), node.getNominalSize(), node.getAttribute());
        
        if(node.isDirectory()) {
            setDirectoryId(node.getNodeId());
            setChangeDateTime(node.getModified().getTime());
        } else {
            FileEntity file = (FileEntity) node;            
            setChangeDateTime(file.getChanged().getTime());
            setFileType(FileType.RegularFile);            
        }           
        setFileId(node.getNodeId());        
        // name and path
        setPath(path);        
        setShortName(node.getName());
        setFileName(node.getName());

        // times
        setCreationDateTime(node.getCreated().getTime());        
        setAccessDateTime(node.getAccessed().getTime());

        // size
        setSize(node.getNominalSize());
        setAllocationSize(node.getAllocationSize());

        // attributes
        setMode(node.getMode());
        setFileAttributes(node.getAttribute());
        
        // user and group
        setUid(node.getUserId());
        setGid(node.getGroupId());
    }
}
