package org.sodfs.storage.driver;

import java.util.Iterator;
import java.util.List;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.SearchContext;
import org.sodfs.storage.meta.api.NamespaceConstants;
import org.sodfs.storage.meta.api.NodeEntity;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSSearchContext extends SearchContext {
    
    private Iterator<NodeEntity> iter;
    private String parent;
    
    SoDFSSearchContext(List<NodeEntity> nel, String parent) {
        iter = nel.iterator();
        this.parent = parent;
    }

    @Override
    public int getResumeId() {
        return 0;
    }

    @Override
    public boolean hasMoreFiles() {
        return iter.hasNext();
    }

    @Override
    public boolean nextFileInfo(FileInfo info) {
        boolean result = false;
        if (iter.hasNext()) {
            NodeEntity ne = iter.next();
            FileInfo fi = new SoDFSFileInfo((parent.equals(NamespaceConstants.ROOT)?"":parent) + NamespaceConstants.SEPARATOR + ne.getName(), ne);
            info.copyFrom(fi);
            result = true;
        }        
        return result;
    }

    @Override
    public String nextFileName() {
        String result = null;
        if (iter.hasNext()) {
            NodeEntity ne = iter.next();
            result = ne.getName();
        }        
        return result;
    }

    @Override
    public boolean restartAt(int resumeId) {
        return false;
    }

    @Override
    public boolean restartAt(FileInfo info) {
        return false;
    }

}
