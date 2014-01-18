package org.sodfs.utils;

import java.io.Serializable;
import org.jgroups.Message;

/**
 *
 * @author Roman Kierzkowski
 */
public class MessageUtil {
    public static Message createMessage(Serializable payload) {
        Message result = new Message();
        result.setObject(payload);
        return result;
    }
}
