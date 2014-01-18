package org.sodfs.utils;

import org.sodfs.utils.NumberFileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class NumberFileFilterTest {
    
    public static final String TEST_DIRECTORY = "test" + File.separator + "testDirectory";
    public static final String MULTI_FIGURE_FILENAME = TEST_DIRECTORY 
                                        + File.separator + "23123";
    public static final String NOT_NUMBER_FILENAME = TEST_DIRECTORY 
                                        + File.separator + "234a3";
    public static final String SINGLE_FIGURE_FILENAME = TEST_DIRECTORY
                                        + File.separator + "1";
    public static final String SUB_DIRECTORY_NAME = TEST_DIRECTORY 
                                        + File.separator + "1232";


    /**
     * Test of accept method, of class NumberFileFilter.
     */
    @Test
    public void accept() {
        File dir = new File(TEST_DIRECTORY);
        assertTrue(dir.isDirectory());
        File[] files = dir.listFiles(new NumberFileFilter());
        System.out.println(Arrays.toString(files));
        assertTrue(files.length == 2);
        HashSet<File> filesSet = new HashSet<File>();
        filesSet.addAll(Arrays.asList(files));
        assertTrue(filesSet.contains(new File(SINGLE_FIGURE_FILENAME)));
        assertTrue(filesSet.contains(new File(MULTI_FIGURE_FILENAME)));
        assertFalse(filesSet.contains(new File(NOT_NUMBER_FILENAME)));
        assertFalse(filesSet.contains(new File(SUB_DIRECTORY_NAME)));
    }

}