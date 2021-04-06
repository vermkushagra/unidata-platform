package com.unidata.mdm.backend.service.model;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;

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
