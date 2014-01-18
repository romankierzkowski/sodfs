package org.sodfs.storage.meta.api;

/**
 *
 * @author Roman Kierzkowski
 */
public class MetaDataServiceNotAvilableException extends Exception {
    
    public MetaDataServiceNotAvilableException() {
        super("The meta-data service is not avilable");
    }
    
    public MetaDataServiceNotAvilableException(Throwable cause) {
        super("The meta-data service is not avilable",cause);
    }
}
