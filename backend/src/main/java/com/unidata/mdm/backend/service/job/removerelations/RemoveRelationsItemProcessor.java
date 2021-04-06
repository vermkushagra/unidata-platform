package com.unidata.mdm.backend.service.job.removerelations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class RemoveRelationsItemProcessor implements ItemProcessor<String, List<DeleteRelationRequestContext>> {

    /**
     * Entity name
     */
    private String entityName;

    /**
     * operation id
     */
    private String operationId;

    /**
     * for date
     */
    private Timestamp forDate;

    /**
     * operation executor
     */
    private String operationExecutor;

    private final List<String> relationsNames = new ArrayList<>();

    private RelationsServiceComponent relationsServiceComponent;

    @Autowired
    public void setRelationsServiceComponent(RelationsServiceComponent relationsServiceComponent) {
        this.relationsServiceComponent = relationsServiceComponent;
    }

    @Override
    public List<DeleteRelationRequestContext> process(final String etalonId) {
        final List<GetRelationDTO> relationDTOS = relationsServiceComponent.loadRelationsToEtalon(
                GetRelationsRequestContext.builder()
                        .etalonKey(etalonId)
                        .relationNames(relationsNames)
                        .forOperationId(operationId)
                        .forDate(Date.from(forDate.toInstant()))
                        .build()
        );

        return relationDTOS.stream()
                .map(GetRelationDTO::getRelationKeys)
                .map(k -> {
                    final DeleteRelationRequestContext deleteRelationRequestContext = DeleteRelationRequestContext.builder()
                            .relationEtalonKey(k.getEtalonId())
                            .relationName(k.getRelationName())
                            .entityName(entityName)
                            .wipe(true)
                            .auditLevel(AuditLevel.AUDIT_SUCCESS)
                            .build();
                    deleteRelationRequestContext.setOperationId(operationId);
                    return deleteRelationRequestContext;
                }).collect(Collectors.toList());
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setOperationId(final String operationId) {
        this.operationId = operationId;
    }

    @Required
    public void setForDate(Timestamp forDate) {
        this.forDate = forDate;
    }

    public String getOperationExecutor() {
        return operationExecutor;
    }

    public void setOperationExecutor(final String operationExecutor) {
        this.operationExecutor = operationExecutor;
    }

    public void setRelationsNames(final Collection<String> relationsNames) {
        this.relationsNames.clear();
        if (CollectionUtils.isNotEmpty(relationsNames)) {
            this.relationsNames.addAll(relationsNames);
        }
    }
}
