/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sodfs.storage.driver.manager.local;

/**
 *
 * @author Roman
 */
public enum ReplicaState {
    NOT_EXISTING,
    INCONSISTENT,
    CONSISTENT,
    FAULTY,
    DEREPLICATED,
    MOVED
}
