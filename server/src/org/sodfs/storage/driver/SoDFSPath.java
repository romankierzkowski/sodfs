package org.sodfs.storage.driver;

import java.io.Serializable;
import java.util.Arrays;
import static org.sodfs.storage.meta.api.NamespaceConstants.*;
/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSPath implements Serializable {        
    public static final String STAR_WILDCARD = "*";
    public static final String QUESTION_MARK_WILDCAR = "?";
    
    private transient String path;
    private String[] parts;
    
    public SoDFSPath(String path) {
        this.path = path;
        if ((path!= null && path.equals(""))||path == null) this.path = ROOT;        
        this.parts = this.path.split("\\" + SEPARATOR);
        if (parts.length > 0) {
            parts = Arrays.copyOfRange(parts, 1, parts.length);
        }
    }

    public String getName() {
        return (parts.length > 0)?getParts()[parts.length - 1]:ROOT;
    }

    public String getParent() {
        StringBuilder sb = new StringBuilder(ROOT);
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(parts[i]);
            if (i != parts.length - 2) sb.append(SEPARATOR);
        }
        return sb.toString();
    }

    public String[] getParts() {
        return parts.clone();
    }

    public String getPath() {
        return getOrBuildPath();
    }
    
    private String getOrBuildPath() {
        if (path == null) {
            StringBuilder sb = new StringBuilder(ROOT);
            for (int i = 0; i < parts.length; i++) {
                sb.append(parts[i]);
                if (i != parts.length - 1) sb.append(SEPARATOR);
            }
            path = sb.toString();
        }
        return path;
    }      
    
    public boolean containsWildcard() {
        String name = (parts.length > 0)?getParts()[parts.length - 1]:null;
        boolean result = false;
        if (name != null){
            result = name.contains(STAR_WILDCARD) || name.contains(QUESTION_MARK_WILDCAR);
        }
        return result;
    }
}
