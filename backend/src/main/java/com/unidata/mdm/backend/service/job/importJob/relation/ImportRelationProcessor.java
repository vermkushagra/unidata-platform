package com.unidata.mdm.backend.service.job.importJob.relation;

import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.keys.ReferenceAliasKey;
import com.unidata.mdm.backend.common.types.impl.RecordKeysCache;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRelationSet;

@Component("importRelationProcessor")
@StepScope
public class ImportRelationProcessor implements ItemProcessor<ImportRelationSet, UpsertRelationsRequestContext> {

    /**
     * This run cache.
     */
    private RecordKeysCache keysCache = new RecordKeysCache();

    /**
     * From Source system
     */
    private String fromSourceSystem;
    /**
     * To Source system
     */
    private String toSourceSystem;

    /**
     * Alias attribute name, for resolving right edge.
     */
    private String toEntityAttributeName;

    /**
     * Entity name
     */
    private String entityName;

    /**
     * Default 'from' date
     */
    private Date from;

    /**
     * Default 'to' date
     */
    private Date to;

    /**
     * Operation id
     */
    private String operationId;

    @Override
    public UpsertRelationsRequestContext process(ImportRelationSet item) throws Exception {

        if (item == null) {
            return null;
        }

        Date fromDate = getFrom(item);
        Date toDate = getTo(item);

        ReferenceAliasKey referenceAliasKey = getReferenceAliasKey(item);

        UpsertRelationRequestContext context = UpsertRelationRequestContext.builder()
                .relation(item.getData())
                .sourceSystem(toSourceSystem)
                .originKey(item.getToOriginKey())
                .etalonKey(item.getToEtalonKey())
                .validFrom(fromDate)
                .validTo(toDate)
                .referenceAliasKey(referenceAliasKey)
                .build();

        UpsertRelationsRequestContext rCtx = UpsertRelationsRequestContext.builder()
                .originKey(item.getFromOriginKey())
                .etalonKey(item.getFromEtalonKey())
                .entityName(entityName) // Reset from definition
                .sourceSystem(fromSourceSystem) // Reset from definition
                .relations(Collections.singletonMap(item.getRelationName(), Collections.singletonList(context)))
                .build();

        context.setOperationId(operationId);
        rCtx.setOperationId(operationId);

        return rCtx;
    }

    private ReferenceAliasKey getReferenceAliasKey(ImportRelationSet item) {

        if (StringUtils.isNotBlank(toEntityAttributeName)
         && Objects.nonNull(item)) {
            return Objects.nonNull(item.getToOriginKey()) && Objects.nonNull(item.getToOriginKey().getExternalId())
                    ? ReferenceAliasKey.builder()
                            .value(item.getToOriginKey().getExternalId())
                            .entityAttributeName(toEntityAttributeName)
                            .build()
                    : null;
        }

        return null;
    }

    public void setFromSourceSystem(String fromSourceSystem) {
        this.fromSourceSystem = fromSourceSystem;
    }

    public void setToSourceSystem(String toSourceSystem) {
        this.toSourceSystem = toSourceSystem;
    }

    public void setKeysCache(RecordKeysCache keysCache) {
        this.keysCache = keysCache;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Depends on context return correct from date
     *
     * @param rel - relation
     * @return from date
     */
    private Date getFrom(@Nullable ImportRelationSet rel) {

        if (nonNull(from)) {
            return from;
        } else if(Objects.nonNull(rel)) {
            return rel.getValidFrom();
        }

        return null;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * Depends on context return correct to date
     *
     * @param rel - relation
     * @return to date
     */
    private Date getTo(@Nullable ImportRelationSet rel) {

        if (nonNull(to)) {
            return to;
        } else if (Objects.nonNull(rel)) {
            return rel.getValidTo();
        }

        return null;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public void setToEntityAttributeName(String toEntityAttributeName) {
        this.toEntityAttributeName = toEntityAttributeName;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}
