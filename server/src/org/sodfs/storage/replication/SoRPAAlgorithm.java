package org.sodfs.storage.replication;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.driver.manager.FileManager;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoRPAAlgorithm {

    private int storageId;
    private FileManager fileManager;
    private InternodeCommunicator internodeCommunicator;
    private float RF;
    private float DRF;
    private float AF;
    private float MF;
    private static final Logger logger = Logger.getLogger(ReplicaPlacementManager.class.getName());

    public SoRPAAlgorithm(int storageId, FileManager fileManager, InternodeCommunicator internodeCommunicator, float RF, float DRF, float AF, float MF) {
        this.storageId = storageId;

        this.fileManager = fileManager;
        this.internodeCommunicator = internodeCommunicator;

        this.RF = RF;
        this.DRF = DRF;
        this.AF = AF;
        this.MF = MF;
    }

    public void execute(ArrayList<Coin> coins, ReplicationRequestCollection requests) {
        Set<Integer> localFiles = fileManager.getLocalReplicasList();
        EvaluationRecordCollection erc = coinListAnalyse(coins, localFiles);
        EvaluationRecord[] evaluations = erc.getEvaluations();
        System.out.println("EVALUATION RECORD: " + evaluations.length);
        int toReplication = (int) Math.ceil(evaluations.length * RF);
        int toDereplication = (int) Math.floor(evaluations.length * DRF);
        if (toReplication + toDereplication > evaluations.length) {
            toDereplication = evaluations.length - toReplication;
        }
        sendReplicationRequests(localFiles, evaluations, toReplication);
        ordersGeneration(localFiles, erc, requests);
        performDereplication(localFiles, evaluations, toDereplication);
    }

    private EvaluationRecordCollection coinListAnalyse(ArrayList<Coin> coins, Set<Integer> localFiles) {
        EvaluationRecordCollection result = new EvaluationRecordCollection();
        for (Integer fileId : localFiles) {
            result.create(fileId);
        }
        for (Coin coin : coins) {
            EvaluationRecord er = result.getOrCreate(coin.getFileId());
            er.evaluate(coin);
        }        
        return result;
    }

    private void ordersGeneration(Set<Integer> localFiles, EvaluationRecordCollection erc,
      ReplicationRequestCollection requests) {
        Set<Integer> requestedFiles = requests.getRequestedFiles();
        for (Integer fileId : requestedFiles) {
            if (localFiles.contains(fileId)) {
                ArrayList<ReplicationRequest> requestForFile = requests.getRequestsList(fileId);
                boolean move = false;
                for (ReplicationRequest candidate : requestForFile) {                    
                    EvaluationRecord local = erc.getEvaluation(fileId);
                    if (candidate.getEvaluation() > local.getEvaluation() * AF) {                        
                        if (!move && candidate.getEvaluation() > local.getEvaluation() * MF) {
                            fileManager.move(fileId, candidate.getMessageOrgin());
                            System.out.println("REPLICA MOVE: " + fileId + " TO " + candidate.getMessageOrgin());
                            local.setMoved(true);
                            move = true;
                        }
                        ReplicationOrder ro = new ReplicationOrder(storageId, candidate.getMessageOrgin(), fileId, move);
                        try {
                            StorageServerInterface ssi = internodeCommunicator.getRemoteStorageServerInterface(candidate.getMessageOrgin());
                            if (ssi != null) {
                                ssi.sendOrder(ro);
                                System.out.println("REPLICATION ORDER: " + fileId + " TO " + candidate.getMessageOrgin());
                            }
                        } catch (RemoteException ex) {
                            logger.log(Level.SEVERE, "Unable to send a replication order.", ex);
                        } catch (MetaDataServiceNotAvilableException ex) {
                            logger.log(Level.SEVERE, "Unable to send a replication order.", ex);
                        }
                    }
                }
            }
        }
    }

    private void sendReplicationRequests(Set<Integer> localFiles, EvaluationRecord[] evaluations, int toReplication) {
        for (int i = 0; i < toReplication; i++) {
            int fileId = evaluations[i].getFileId();
            if (!localFiles.contains(fileId)) {
                int dest = evaluations[i].getDestignation();
                if (dest != -1) {
                    float eval = evaluations[i].getEvaluation();
                    ReplicationRequest msg =
                      new ReplicationRequest(storageId, dest, fileId, eval);
                    try {
                        StorageServerInterface ssi = internodeCommunicator.getRemoteStorageServerInterface(msg.getMessageDestignation());
                        if (ssi != null) {
                            ssi.sendRequest(msg);
                            System.out.println("REPLICATION REQUEST: " + fileId + " FROM " + dest);
                        }
                    } catch (RemoteException ex) {
                        logger.log(Level.SEVERE, "Unable to send a replication request.", ex);
                    } catch (MetaDataServiceNotAvilableException ex) {
                        logger.log(Level.SEVERE, "Unable to send a replication request.", ex);
                    }
                }

            }
        }
    }

    private void performDereplication(Set<Integer> localFiles, EvaluationRecord[] evaluations, int toDereplication) {
        for (int i = 0; i < toDereplication; i++) {
            EvaluationRecord eval = evaluations[evaluations.length - i - 1];
            int fileId = eval.getFileId();
            if (localFiles.contains(fileId) && !eval.isMoved()) {
                System.out.println("DEREPLICATION: " + fileId);
                fileManager.dereplicate(fileId);
            }
        }
    }
}
