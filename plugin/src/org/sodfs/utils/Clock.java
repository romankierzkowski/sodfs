package org.sodfs.utils;

/**
 *
 * @author Roman Kierzkowski
 */
public class Clock {
    public static final long SECOND = 1000L;
    public static final long MINUTE = 60L * SECOND;
    public static final long HOUR = 60L * MINUTE;
    public static final long DAY = 24L * HOUR;
    
    private static Clock INSTANCE;    
    private long correction = 0L;
    
    private Clock() {        
    }
    
    public long getCurrentTime() {
        return System.currentTimeMillis() + getCorrection();
    }
    
    public static Clock getInstance() {
        if (INSTANCE == null) INSTANCE = new Clock();
        return INSTANCE;
    }

    public long getCorrection() {
        return correction;
    }

    public void setCorrection(long correction) {
        this.correction = correction;
    }
   
}
