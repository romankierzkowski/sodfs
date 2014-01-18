package org.sodfs.storage.driver;

import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 *
 * @author Roman Kierzkowski
 */
public class SharedDeviceMock extends SharedDevice{

    public SharedDeviceMock(DeviceContext ctx) {
        super((String) null,(int) 0,ctx);
    }

}
