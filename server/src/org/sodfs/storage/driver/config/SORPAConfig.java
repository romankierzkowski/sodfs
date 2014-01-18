package org.sodfs.storage.driver.config;

/**
 *
 * @author Roman Kierzkowski
 */
public class SORPAConfig {
    
    private double pc;
    private double k;
    private long TTL;
    private long pinTime;
    private int minNOR;
    private float RF;
    private float DRF;
    private float AF;
    private float MF;

    public double getPc() {
        return pc;
    }

    public void setPc(double pc) {
        this.pc = pc;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public long getTTL() {
        return TTL;
    }

    public void setTTL(long TTL) {
        this.TTL = TTL;
    }

    public long getPinTime() {
        return pinTime;
    }

    public void setPinTime(long pinTime) {
        this.pinTime = pinTime;
    }

    public int getMinNOR() {
        return minNOR;
    }

    public void setMinNOR(int minNOR) {
        this.minNOR = minNOR;
    }

    public float getRF() {
        return RF;
    }

    public void setRF(float RF) {
        this.RF = RF;
    }

    public float getDRF() {
        return DRF;
    }

    public void setDRF(float DRF) {
        this.DRF = DRF;
    }

    public float getAF() {
        return AF;
    }

    public void setAF(float AF) {
        this.AF = AF;
    }

    public float getMF() {
        return MF;
    }

    public void setMF(float MF) {
        this.MF = MF;
    }
    

}
