package org.sodfs.storage.replication;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Roman Kierzkowski
 */
public class EvaluationRecord implements Comparable {
    private int fileId;    
    private float evaluation;
    
    private boolean moved;
    
    private static final float l = 3.0f;
    
    private HashMap<Integer, Integer> destignations = new HashMap<Integer, Integer>();
        
    EvaluationRecord(int fileId) {
        this.fileId = fileId;
    }

    public int getFileId() {
        return fileId;
    }
    
    public void evaluate(Coin coin) {
        addDestignation(coin.getReplicaOrginId());
        float tmp = (coin.getOperationType() == OperationType.READ)?1:coin.getHopCounter();
        
        evaluation+= tmp/(l * coin.getDispersionFactor());
    }

    public float getEvaluation() {
        return evaluation;
    }
    
    private void addDestignation(int storageId) {        
        Integer count = destignations.get(storageId);
        if (count != null) count++;
        else count = 1;
        destignations.put(storageId, count);            
    }
    
    public int getDestignation() {
        int maxCount = 0;
        int result = -1;
        Set<Entry<Integer, Integer>> es = destignations.entrySet();
        for (Entry<Integer, Integer> entry : es) {
            if (maxCount < entry.getValue()) {
                maxCount = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }

    public int compareTo(Object o) {
        return Float.compare(((EvaluationRecord) o).evaluation, evaluation);
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean isMoved() {
        return moved;
    }
    
    
}
