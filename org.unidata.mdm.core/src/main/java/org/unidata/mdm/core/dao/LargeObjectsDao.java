package org.unidata.mdm.core.dao;

import java.io.IOException;
import java.util.Collection;

import org.unidata.mdm.core.po.LargeObjectPO;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface LargeObjectsDao {
    /**
     * Fetch large object.
     * @param id the id
     * @param isBinary character or binary
     * @return object or null
     */
    LargeObjectPO fetchLargeObjectById(String id, boolean isBinary);
    /**
     * Upsert large object.
     * @param lob the object
     * @return true if successful, false otherwise
     * @throws IOException
     */
    boolean upsertLargeObject(LargeObjectPO lob) throws IOException;
    /**
     * Deletes a large object.
     * @param id the id
     * @param field the field
     * @param isBinary character or binary
     * @return true if successful, false otherwise
     */
    boolean deleteLargeObject(String id, String field, boolean isBinary);
    /**
     * Check exist large object.
     * @param id the id
     * @param isBinary character or binary
     * @return true if exist, false otherwise
     */
    boolean checkLargeObject(String id, boolean isBinary);
    /**
     * Sets a record active (submits an attachment).
     * @param spec activation spec (objects)
     * @return true if successful, false otherwise
     */
    boolean activateLargeObjects(Collection<LargeObjectPO> spec);
    /**
     * Clean unused binary data
     * @param maxLifetime max lifetime for binary data
     * @return count of removed messages
     */
    long cleanUnusedBinaryData(long maxLifetime);
}
