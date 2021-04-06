package com.unidata.mdm.backend.service.data.listener.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.common.service.ClusterService;

public class DataRecordMergeClusterCleaningBeforeExecutor implements DataRecordBeforeExecutor<MergeRequestContext> {

    @Autowired
    private ClusterService clusterService;

    @Override
    public boolean execute(MergeRequestContext ctx) {
        Collection<String> etalonIds = new ArrayList<>();
        RecordKeys masterKey = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
        List<RecordKeys> duplicatesKeys = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS);
        etalonIds.add(masterKey.getEtalonKey().getId());
        Collection<String> duplicateIds = duplicatesKeys.stream()
                .map(key -> key.getEtalonKey().getId())
                .collect(Collectors.toCollection(() -> etalonIds));
        clusterService.excludeFromClusters(masterKey.getEntityName(), etalonIds);
        clusterService.dropFromBlockList(duplicateIds);
        return true;
    }
}
