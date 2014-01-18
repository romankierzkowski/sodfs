package org.sodfs.storage.driver.manager.exceptions;

/**
 *
 * @author Roman Kierzkowski
 */
public class MovableFileException extends Exception {

    public MovableFileException(String message) {        
        super(message);        
    }
    
    public MovableFileException(String message, Throwable cause) {
        super(message, cause);        
    }
    
     
}
