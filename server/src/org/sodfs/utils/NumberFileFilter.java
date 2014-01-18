/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sodfs.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Roman
 */
public class NumberFileFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        File file = new File(dir.getPath() + File.separator + name);
        return file.isFile() && name.matches("[0-9]+");
    }

}
