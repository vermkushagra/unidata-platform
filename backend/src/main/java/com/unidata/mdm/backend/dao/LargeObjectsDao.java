package com.unidata.mdm.backend.dao;

import java.io.IOException;

import com.unidata.mdm.backend.po.LargeObjectPO;

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
     * @param isBinary character or binary
     * @return true if successful, false otherwise
     * @throws IOException
     */
    boolean upsertLargeObject(LargeObjectPO lob, boolean isBinary) throws IOException;
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
     * @param id record id
     * @param parentId parent (golden or origin record) ID
     * @param isOrigin origin or not
     * @param isBinary whether the data is binary or not
     * @return true if successful, false otherwise
     */
    boolean ensureActive(String id, String parentId, boolean isOrigin, boolean isBinary);

    /**
     * Clean unused binary data
     * @param maxLifetime max lifetime for binary data
     * @return count of removed messages
     */
    long cleanUnusedBinaryData(long maxLifetime);
}
