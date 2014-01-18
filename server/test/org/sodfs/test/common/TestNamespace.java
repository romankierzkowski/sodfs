package org.sodfs.test.common;

import org.sodfs.storage.meta.api.NamespaceConstants;

/**
 *
 * @author Roman Kierzkowski
 */
public class TestNamespace {
    public static final int ROOT_ID = NamespaceConstants.ROOT_ID;
    
    public static final String FILE_TO_CREATE = "tocreate.txt";
    public static final String DIR_TO_CREATE = "dirtocreate";
    public static final String FILE_TO_DELETE = "todelete.txt";
    public static final String DIR_TO_DELETE = "dirtodelete";
    public static final String FILE_TO_RENAME = "torename.txt";
    public static final String DIR_TO_RENAME = "dirtorename";
    public static final String NEW_FILE_NAME = "renamedfile.txt";
    public static final String NEW_DIR_NAME = "renameddir";
    public static final String FILE_NAME = "file.txt";
    public static final String DIR_NAME = "dir";
    public static final String SUB_NAME = "sub";    
    public static final String NOT_EXISTING_DIRECTORY_NAME = "notexistdir";
    public static final String NOT_EXISTING_FILE_NAME = "notexist.txt";
    public static final String ROOT = NamespaceConstants.ROOT;
    public static final String SEPARATOR = NamespaceConstants.SEPARATOR;
    public static final String NOT_EXISTING_FILE = ROOT + NOT_EXISTING_FILE_NAME;
    public static final String NOT_EXISTING_DIRECTORY = ROOT + NOT_EXISTING_DIRECTORY_NAME;
    public static final String EXISTING_FILE = ROOT + FILE_NAME;
    public static final String EXISTING_DIRECTORY = ROOT + DIR_NAME;
    public static final String EXISTING_SUBDIRECTORY = ROOT + DIR_NAME + SEPARATOR + SUB_NAME;
    public static final String NOT_EXISTING_SUBDIRECTORY = ROOT + DIR_NAME + SEPARATOR +NOT_EXISTING_DIRECTORY_NAME;
    public static final String EXISTING_FILE_IN_DIR = EXISTING_DIRECTORY + SEPARATOR + FILE_NAME;
    public static final String EXISTING_FILE_IN_SUBDIR = EXISTING_SUBDIRECTORY + SEPARATOR + FILE_NAME;
    public static final String NOT_EXISTING_FILE_IN_SUB_DIR = ROOT + DIR_NAME + SEPARATOR + SUB_NAME + SEPARATOR + NOT_EXISTING_FILE_NAME;
    public static final String DIR_WITH_FILE = ROOT + "dirwithfile";
    public static final String DIR_WITH_SUBDIR = ROOT + "dirwithsubdir";
    
    public static final int EXISTING_FILE_ID = 10;
}
