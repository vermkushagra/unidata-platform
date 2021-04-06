package com.unidata.mdm.backend.service.job.duplicates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.GetRequestContext.GetRequestContextBuilder;

/**
 * This processor avoid problem of hash conflict and transform input data to merge ctx
 */
@Component("duplicateProcessor")
@Scope(value = "step")
public class DuplicateItemProcessor implements ItemProcessor<Collection<Collection<String>>, List<MergeRequestContext>> {

    private String operationId;

    private String entityName;

    private Long auditLevel;

    private boolean skipNotifications;

    @Override
    public List<MergeRequestContext> process(Collection<Collection<String>> clusters) throws Exception {
        if(CollectionUtils.isEmpty(clusters)){
            return null;
        }
        List<MergeRequestContext> result = new ArrayList<>();
        for(Collection<String> clusterIds : clusters){
            if (clusterIds.size() < 2) {
                continue;
            }

            List<RecordIdentityContext> filteredDuplicates = clusterIds.stream()
                    .map(id -> new GetRequestContextBuilder().etalonKey(id).build())
                    .collect(Collectors.toList());
            MergeRequestContext context = new MergeRequestContext
                    .MergeRequestContextBuilder()
                    .duplicates(filteredDuplicates)
                    .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                    .entityName(entityName)
                    .batchUpsert(true)
                    .build();

            if (skipNotifications) {
                context.skipNotification();
            }

            context.setOperationId(operationId);
            result.add(context);
        }
        return result;
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setAuditLevel(Long auditLevel) {
        this.auditLevel = auditLevel;
    }

    @Required
    public void setSkipNotifications(boolean skipNotifications) {
        this.skipNotifications = skipNotifications;
    }
}
