package org.unidata.mdm.meta.service;

import org.unidata.mdm.meta.context.DeleteModelRequestContext;

/**
 * Clean related records with deleted entities
 */
public interface RecordsCleaner {

    /**
     *
     * @param context
     */
    void cleanRelatedRecords(DeleteModelRequestContext context);

}
