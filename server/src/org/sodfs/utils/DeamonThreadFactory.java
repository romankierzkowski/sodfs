package org.sodfs.utils;

import java.util.concurrent.ThreadFactory;

public class DeamonThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        Thread result = new Thread(r);
        result.setDaemon(true);
        return result;
    }
}
