package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.common.service.ClusterService;

public class DataRecordDeleteMatchingExecutor implements DataRecordAfterExecutor<DeleteRequestContext> {

    @Autowired
    private ClusterService clusterService;

    @Override
    public boolean execute(DeleteRequestContext deleteRequestContext) {

        RecordKeys keys = deleteRequestContext.keys();

        String etalonId = keys.getEtalonKey().getId();
        String entityName = keys.getEntityName();
        clusterService.excludeFromClusters(entityName, Collections.singleton(etalonId));
        return true;
    }
}
