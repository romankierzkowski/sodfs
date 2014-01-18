package org.sodfs.storage.replication;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class EvaluationRecordCollectionTest {

    public EvaluationRecordCollectionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getOrCreate method, of class EvaluationRecordCollection.
     */
    @Test
    public void getOrCreate() {
        int fileId = 1;
        EvaluationRecordCollection instance = new EvaluationRecordCollection();
        EvaluationRecord result1 = instance.getOrCreate(fileId);
        EvaluationRecord result2 = instance.getOrCreate(fileId);
        assertEquals(result1, result2);
    }

    /**
     * Test of create method, of class EvaluationRecordCollection.
     */
    @Test
    public void create() {
        int fileId = 1;
        EvaluationRecordCollection instance = new EvaluationRecordCollection();
        instance.create(fileId);
        EvaluationRecord result1 = instance.getOrCreate(fileId);
        instance.create(fileId);
        EvaluationRecord result2 = instance.getOrCreate(fileId);
        assertNotSame(result1, result2);
    }

    /**
     * Test of getEvaluations method, of class EvaluationRecordCollection.
     */
    @Test
    public void getEvaluations() {
        EvaluationRecordCollection instance = new EvaluationRecordCollection();
        instance.create(1);
        instance.getOrCreate(1);
        instance.getOrCreate(2);
        instance.create(3);
        assertEquals(3, instance.getEvaluations().length);        
    }

    /**
     * Test of getEvaluation method, of class EvaluationRecordCollection.
     */
    @Test
    public void getEvaluation() {
        EvaluationRecordCollection instance = new EvaluationRecordCollection();
        instance.create(1);
        instance.getOrCreate(1);
        instance.getOrCreate(2);
        instance.create(3);
        assertNotNull(instance.getEvaluation(1));
        assertNotNull(instance.getEvaluation(2));
        assertNotNull(instance.getEvaluation(3));
        assertNull(instance.getEvaluation(4));
    }
}
