package org.sodfs.storage.replication;

import java.util.Arrays;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sodfs.utils.Clock;
import static org.junit.Assert.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class EvaluationRecordTest {

    public EvaluationRecordTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of evaluate method, of class EvaluationRecord.
     */
    @Test
    public void evaluate() {       
//        int fileId = 1;        
//        int coinOriginId = 1;        
        
        EvaluationRecord instance = new EvaluationRecord(1);
        
        assertEquals(0.0f, instance.getEvaluation());
        assertEquals(-1, instance.getDestignation());
        
//        Coin coin1 = new Coin(fileId, 1, coinOriginId, OperationType.READ, 1000, 10000, Clock.getInstance());
//        coin1.incrementHopCounter();
//        instance.evaluate(coin1);
//        assertEquals(0.3f, instance.getEvaluation());
//        assertEquals(1, instance.getDestignation());
//        
//        instance = new EvaluationRecord(1);
//        coin1.incrementHopCounter();
//        instance.evaluate(coin1);
//        assertEquals(0.3f, instance.getEvaluation());
//        assertEquals(1, instance.getDestignation());        
//        
//        Coin coin2 = new Coin(fileId, 1, coinOriginId, OperationType.WRITE, 1000, 10000, Clock.getInstance());
//        instance = new EvaluationRecord(1);
//        coin2.incrementHopCounter();
//        instance.evaluate(coin2);
//        assertEquals(0.3f, instance.getEvaluation());
//        assertEquals(instance.getDestignation(), 1);        
//        
//        instance = new EvaluationRecord(1);
//        coin2.incrementHopCounter();
//        instance.evaluate(coin2);
//        assertEquals(0.6f, instance.getEvaluation());
//        assertEquals(1, instance.getDestignation()); 
//                
//        Coin coin3 = new Coin(fileId, 1, coinOriginId, OperationType.READ, 1000, 10000, Clock.getInstance());
//        instance = new EvaluationRecord(1);
//        coin3.incrementHopCounter();
//        coin3.dispearse(3);
//        instance.evaluate(coin3);
//        System.out.println(instance.getEvaluation());
//        assertTrue(0.3f > instance.getEvaluation());
//        assertEquals(1, instance.getDestignation()); 
//        
//        coin3.incrementHopCounter();
//        coin3.dispearse(3);
//        instance.evaluate(coin3);         
//        assertTrue(0.9f > instance.getEvaluation());       
//        assertEquals(1, instance.getDestignation()); 
    }

    /**
     * Test of getDestignation method, of class EvaluationRecord.
     */
    @Test
    public void getDestignation() {        
        int fileId = 1;        
        int replicaOriginId1 = 1;        
        int replicaOriginId2 = 2;
        int coinOriginId1 = 1;        
        int coinOriginId2 = 2;
        
        EvaluationRecord instance = new EvaluationRecord(1);
        Coin coin1 = new Coin(fileId, replicaOriginId1, coinOriginId2, OperationType.READ, 1000, 10000, Clock.getInstance());
        coin1.incrementHopCounter();
        instance.evaluate(coin1);
        assertEquals(replicaOriginId1, instance.getDestignation());
        
        Coin coin2 = new Coin(fileId, replicaOriginId2, coinOriginId1, OperationType.WRITE, 1000, 10000, Clock.getInstance());        
        instance.evaluate(coin2);
        instance.evaluate(coin2);
        assertEquals(replicaOriginId2, instance.getDestignation());        
        
        instance.evaluate(coin1);
        instance.evaluate(coin1);
        assertEquals(replicaOriginId1, instance.getDestignation());   
        
    }

    /**
     * Test of compareTo method, of class EvaluationRecord.
     */
    @Test
    public void compareTo() {
        int fileId1 = 1;        
        int fileId2 = 1;        
        int coinOriginId1 = 1;        
        int coinOriginId2 = 2;
        Coin coin1 = new Coin(fileId1, 1, coinOriginId1, OperationType.READ, 1000, 10000, Clock.getInstance());
        coin1.incrementHopCounter();
        Coin coin2 = new Coin(fileId2, 1, coinOriginId2, OperationType.WRITE, 1000, 10000, Clock.getInstance());
        coin2.incrementHopCounter();
        coin2.dispearse(3);
        coin2.incrementHopCounter();
        coin2.dispearse(3);
        
        EvaluationRecord[] evalRec = {new EvaluationRecord(fileId1), new EvaluationRecord(fileId2)};
        evalRec[0].evaluate(coin1);
        evalRec[1].evaluate(coin2);
        Arrays.sort(evalRec);
        assertEquals(fileId2, evalRec[0].getFileId());
        assertEquals(fileId1, evalRec[1].getFileId());
        assertTrue(evalRec[0].getEvaluation() > evalRec[1].getEvaluation());
    }

}