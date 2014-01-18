package org.sodfs.testclient.executor.tasks;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class CreateFileTaskTest {

    public CreateFileTaskTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of run method, of class CreateFileTask.
     */
    @Test
    public void run() {
        System.out.println("run");
        DeleteFileTask instance = new DeleteFileTask("smb://normal:normal@192.168.1.64/host1/abecadlo2.txt");
        instance.run();        
    }

}