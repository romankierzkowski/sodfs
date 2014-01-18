package org.sodfs.storage.driver;

import org.junit.Test;
import org.jgroups.util.Util;
import static org.junit.Assert.*;
import static org.sodfs.test.common.TestNamespace.*;
import static org.sodfs.storage.driver.SoDFSPath.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSPathTest {

    public SoDFSPathTest() {
    }

    /**
     * Test of getName method, of class SoDFSPath.
     */
    @Test
    public void getName() {        
        SoDFSPath instance = new SoDFSPath(ROOT);
        assertEquals(ROOT, instance.getName());
        instance = new SoDFSPath(EXISTING_FILE);
        assertEquals(FILE_NAME, instance.getName());
        instance = new SoDFSPath(EXISTING_FILE_IN_DIR);
        assertEquals(FILE_NAME, instance.getName());
        instance = new SoDFSPath(EXISTING_FILE_IN_SUBDIR);
        assertEquals(FILE_NAME, instance.getName());        
    }

    /**
     * Test of getParent method, of class SoDFSPath.
     */
    @Test
    public void getParent() {
        SoDFSPath instance = new SoDFSPath(ROOT);
        assertEquals(ROOT, instance.getParent());
        instance = new SoDFSPath(EXISTING_FILE);
        assertEquals(ROOT, instance.getParent());
        instance = new SoDFSPath(EXISTING_FILE_IN_DIR);
        assertEquals(EXISTING_DIRECTORY, instance.getParent());
        instance = new SoDFSPath(EXISTING_FILE_IN_SUBDIR);
        assertEquals(EXISTING_SUBDIRECTORY, instance.getParent());  
    }
    
    /**
     * Test of getParts method, of class SoDFSPath.
     */
    @Test
    public void getParts() {
        SoDFSPath instance = new SoDFSPath(ROOT);
        assertEquals(0, instance.getParts().length);
        instance = new SoDFSPath(EXISTING_FILE);
        assertEquals(1, instance.getParts().length);
        instance = new SoDFSPath(EXISTING_FILE_IN_DIR);
        assertEquals(2, instance.getParts().length);
        instance = new SoDFSPath(EXISTING_FILE_IN_SUBDIR);
        assertEquals(3, instance.getParts().length);  
    }

    /**
     * Test of getPath method, of class SoDFSPath.
     */
    @Test
    public void getPath() throws Exception {
        SoDFSPath instance = new SoDFSPath(ROOT);
        assertEquals(ROOT, instance.getPath());
        instance = new SoDFSPath(EXISTING_FILE);
        assertEquals(EXISTING_FILE, instance.getPath());
        instance = new SoDFSPath(EXISTING_FILE_IN_DIR);
        assertEquals(EXISTING_FILE_IN_DIR, instance.getPath());
        instance = new SoDFSPath(EXISTING_FILE_IN_SUBDIR);
        assertEquals(EXISTING_FILE_IN_SUBDIR, instance.getPath()); 
        
        instance = serializeAndDeserialize(new SoDFSPath(ROOT));
        assertEquals(ROOT, instance.getPath());
        instance = serializeAndDeserialize(new SoDFSPath(EXISTING_FILE));
        assertEquals(EXISTING_FILE, instance.getPath());
        instance = serializeAndDeserialize(new SoDFSPath(EXISTING_FILE_IN_DIR));
        assertEquals(EXISTING_FILE_IN_DIR, instance.getPath());
        instance = serializeAndDeserialize(new SoDFSPath(EXISTING_FILE_IN_SUBDIR));
        assertEquals(EXISTING_FILE_IN_SUBDIR, instance.getPath()); 
    }
    
    private SoDFSPath serializeAndDeserialize(SoDFSPath s) throws Exception {
        return (SoDFSPath) Util.objectFromByteBuffer(Util.objectToByteBuffer(s));
    }
    
    /**
     * Test of containsWildcard method, of class SoDFSPath.
     */
    @Test
    public void containsWildcard() throws Exception {
        SoDFSPath instance = new SoDFSPath(ROOT);
        assertEquals(false, instance.containsWildcard());
        instance = new SoDFSPath(EXISTING_FILE);
        assertEquals(false, instance.containsWildcard());
        instance = new SoDFSPath(EXISTING_FILE_IN_DIR);
        assertEquals(false, instance.containsWildcard());
        instance = new SoDFSPath(EXISTING_FILE_IN_SUBDIR);
        assertEquals(false, instance.containsWildcard());
        
        instance = new SoDFSPath(ROOT + QUESTION_MARK_WILDCAR);
        assertEquals(true, instance.containsWildcard());
        instance = new SoDFSPath(ROOT + STAR_WILDCARD);
        assertEquals(true, instance.containsWildcard());
        instance = new SoDFSPath(EXISTING_DIRECTORY + SEPARATOR + QUESTION_MARK_WILDCAR);
        assertEquals(true, instance.containsWildcard());
        instance = new SoDFSPath(EXISTING_DIRECTORY + SEPARATOR + STAR_WILDCARD);
        assertEquals(true, instance.containsWildcard());
        instance = new SoDFSPath(EXISTING_SUBDIRECTORY + SEPARATOR + QUESTION_MARK_WILDCAR);
        assertEquals(true, instance.containsWildcard());
        instance = new SoDFSPath(EXISTING_SUBDIRECTORY + SEPARATOR + STAR_WILDCARD);
        assertEquals(true, instance.containsWildcard());
    }    
}