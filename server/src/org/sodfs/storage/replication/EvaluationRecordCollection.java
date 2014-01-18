package org.sodfs.storage.replication;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Roman Kierzkowski
 */
public class EvaluationRecordCollection {
    private HashMap<Integer, EvaluationRecord> evaluations = new HashMap<Integer, EvaluationRecord>();
    
    public EvaluationRecord getOrCreate(int fileId) {
        EvaluationRecord result = evaluations.get(fileId);
        if (result == null) {
            result = new EvaluationRecord(fileId);
            evaluations.put(fileId, result);
        }
        return result;
    }
    
    public void create(int fileId) {
        EvaluationRecord result = new EvaluationRecord(fileId);
        evaluations.put(fileId, result);
    }
    
    public EvaluationRecord[] getEvaluations() {       
        EvaluationRecord[] result = new EvaluationRecord[0];
        Collection<EvaluationRecord> collection = evaluations.values();
        result = collection.toArray(result);
        Arrays.sort(result);
        return result;        
    }
    
    public EvaluationRecord getEvaluation(int fileId) {
        return evaluations.get(fileId);
    }
}
