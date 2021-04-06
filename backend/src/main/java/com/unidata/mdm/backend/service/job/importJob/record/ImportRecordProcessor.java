package com.unidata.mdm.backend.service.job.importJob.record;

import static com.unidata.mdm.backend.service.job.importJob.record.ImportRecordReader.DUMMY_RECORD;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.CommonSendableContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRecordSet;

/**
 * Map record to upsert context
 */
@StepScope
public class ImportRecordProcessor implements ItemProcessor<ImportRecordSet, CommonSendableContext> {

    /**
     * Source system
     */
    private String sourceSystem;

    /**
     * Entity name
     */
    private String entityName;

    /**
     * Skip cleanse
     */
    private boolean skipCleanse;

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

    /**
     * Flag responsible for applying merge with previous version logic
     */
    private Boolean mergeWithPreviousVersion;
    /**
     * Collection alias code attributes
     */
    private Collection<CodeAttributeAlias> aliasCodeAttributePointers;

    /**
     * batch size
     */
    private Integer batchSize;

    /**
     * step number
     */
    private Integer step;

    @Override
    public CommonSendableContext process(ImportRecordSet item) throws Exception {

        if (item == null || item == DUMMY_RECORD) {
            return null;
        }

        Date fromDate = getFrom(item);
        Date toDate = getTo(item);

        RecordStatus versionStatus = item.getStatus() == null ? RecordStatus.ACTIVE : item.getStatus();
        CommonSendableContext context;

        // Was actually a period delete
        if (versionStatus == RecordStatus.INACTIVE) {
            context = DeleteRequestContext.builder()
                    .originKey(item.getOriginKey())
                    .etalonKey(item.getEtalonKey())
                    .sourceSystem(sourceSystem)
                    .entityName(entityName)
                    .validFrom(fromDate)
                    .validTo(toDate)
                    .inactivatePeriod(true)
                    .build();
        } else {

            boolean mergeWithPrevious = mergeWithPreviousVersion == null ? false : mergeWithPreviousVersion.booleanValue();

            Collection<UpsertClassifierDataRequestContext> classifierContexts
                = item.getClassifiers().stream().map(this::convert).collect(Collectors.toList());

            context = UpsertRequestContext.builder()
                    .record(item.getData())
                    .originKey(item.getOriginKey())
                    .etalonKey(item.getEtalonKey())
                    .validFrom(fromDate)
                    .validTo(toDate)
                    .addClassifierUpserts(classifierContexts)
                    .sourceSystem(sourceSystem)
                    .entityName(entityName)
                    .skipCleanse(skipCleanse)
                    .mergeWithPrevVersion(mergeWithPrevious)
                    .returnEtalon(true)
                    .codeAttributeAliases(aliasCodeAttributePointers)
                    .build();
        }

        Integer numberInSet = step * batchSize + item.getImportRowNum();
        context.putToStorage(StorageId.IMPORT_ROW_NUM, numberInSet);
        context.setOperationId(operationId);
        return context;
    }

    private Date getFrom(@Nullable ImportRecordSet set) {

        if (nonNull(from)) {
            return from;
        } else if (nonNull(set)) {
            return isNull(set.getValidFrom()) ? null : set.getValidFrom();
        } else {
            return null;
        }
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    private Date getTo(@Nullable ImportRecordSet set) {
        if (nonNull(to)) {
            return to;
        } else if (nonNull(set)) {
            return isNull(set.getValidTo()) ? null : set.getValidTo();
        } else {
            return null;
        }
    }

    public void setTo(Date to) {
        this.to = to;
    }

    private UpsertClassifierDataRequestContext convert(OriginClassifier classifier) {
        return UpsertClassifierDataRequestContext.builder()
             .classifier(classifier)
             .classifierName(classifier.getInfoSection().getClassifierName())
             .classifierNodeId(classifier.getInfoSection().getNodeId())
             .status(classifier.getInfoSection().getStatus())
             .build();
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setSkipCleanse(boolean skipCleanse) {
        this.skipCleanse = skipCleanse;
    }

    public void setAliasCodeAttributePointers(Collection<CodeAttributeAlias> aliasCodeAttributePointers) {
        this.aliasCodeAttributePointers = aliasCodeAttributePointers;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public void setMergeWithPreviousVersion(Boolean mergeWithPreviousVersion) {
        this.mergeWithPreviousVersion = mergeWithPreviousVersion;
    }

    @Required
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    @Required
    public void setStep(Integer step) {
        this.step = step;
    }
}
