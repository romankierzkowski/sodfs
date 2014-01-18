package org.sodfs.storage.excludes;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class ExcludeManager {
    private ConcurrentHashMap<ReplicaExclude, Long> replicaExcludes;
    private ConcurrentHashMap<StorageServerExclude, Long> storageServerExcludes;
        
    private Clock clock = Clock.getInstance();
    
    private static final String TIMER_NAME = "ExludeManagerTimer";
    private Timer sheduler = new Timer(TIMER_NAME, true);
    
    public static final long REPLICA_EXCL_TTL = Clock.SECOND / 2;
    public static final long STORAGE_EXCL_TTL = 15 * Clock.SECOND;
    
    public static final long CLEANING_PERIOD = 5 * Clock.SECOND;
    private Cleaner cleaner = new Cleaner();

    public ExcludeManager() {
        replicaExcludes = new ConcurrentHashMap<ReplicaExclude, Long>();
        storageServerExcludes = new ConcurrentHashMap<StorageServerExclude, Long>();        
    }

    public void start() {
        sheduler.scheduleAtFixedRate(cleaner, 0, CLEANING_PERIOD);
    }
    
    public void excludeReplica(int storageId, int fileId) {        
        ReplicaExclude re = new ReplicaExclude(storageId, fileId);
        long expirationTime = clock.getCurrentTime() + REPLICA_EXCL_TTL;        
        replicaExcludes.put(re, expirationTime);
    }
    
    public void excludeStorageServer(int storageId) {
        StorageServerExclude sse = new StorageServerExclude(storageId);
        long expirationTime = clock.getCurrentTime() + STORAGE_EXCL_TTL;
        storageServerExcludes.put(sse, expirationTime);
    }
    
    public boolean isReplicaOrStorageExcluded(int storageId, int fileId) {
        boolean result = isStorageExcluded(storageId);
        if (!result) {
            result = isReplicaExcluded(storageId, fileId);
        }
        return result;
    }

    public boolean isReplicaExcluded(int storageId, int fileId) {        
        ReplicaExclude re = new ReplicaExclude(storageId, fileId);
        Long expiration = replicaExcludes.get(re);
        return !notExistOrExpired(expiration);
    }
    
    public boolean isStorageExcluded(int storageId) {        
        StorageServerExclude sse = new StorageServerExclude(storageId);
        Long expiration = storageServerExcludes.get(sse);        
        return !notExistOrExpired(expiration);
    }

    private boolean notExistOrExpired(Long expiration) {
        boolean result = true;
        if (expiration != null) {
            long exp = expiration;
            if (exp > clock.getCurrentTime()) {
                result = false;
            }
        }
        return result;
    }
    
    private class Cleaner extends TimerTask {
        @Override
        public void run() {
            StringBuilder re = new StringBuilder();
            boolean first = false;
            re.append("REPLICA EXCLUDES [");
            Set<Entry<ReplicaExclude, Long>> repEx = replicaExcludes.entrySet();
            for (Iterator<Entry<ReplicaExclude, Long>> it = repEx.iterator(); it.hasNext();) {
                Entry<ReplicaExclude, Long> entry = it.next();
                if (entry.getValue() < clock.getCurrentTime()) {
                    it.remove();
                } else {
                    if (!first) re.append(",");
                    else first = false;
                    re.append(entry.getKey());                    
                }
            }
            re.append("]");
            System.out.println(re.toString());
            StringBuilder sse = new StringBuilder();
            first = false;
            sse.append("STORAGE EXCLUDES [");
            Set<Entry<StorageServerExclude, Long>> storEx = storageServerExcludes.entrySet();
            for (Iterator<Entry<StorageServerExclude, Long>> it = storEx.iterator(); it.hasNext();) {
                Entry<StorageServerExclude, Long> entry = it.next();
                if (entry.getValue() < clock.getCurrentTime()) {
                    it.remove();
                } else {
                    if (!first) re.append(",");
                    else first = false;
                    sse.append(entry.getKey());                    
                }
            }
            sse.append("]");
            System.out.println(sse.toString());
        }        
    }
}
