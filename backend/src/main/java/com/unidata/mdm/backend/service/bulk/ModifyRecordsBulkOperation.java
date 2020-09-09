/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package com.unidata.mdm.backend.service.bulk;

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_BULK_OPERATION_INCORRECT_CLASS;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_BULK_OPERATION_MODIFY_RECORD_INCORRECT;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.service.job.modify.ModifyItemJobConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.dto.bulk.ModifyRecordsInformationDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.job.JobServiceExt;


/**
 * The Class ModifyRecordsBulkOperation.
 *
 * @author Mikhail Mikhailov
 *         Modify records operation.
 */
public class ModifyRecordsBulkOperation extends AbstractBulkOperation {

    /** Job service. */
    @Autowired(required = false)
    private JobServiceExt jobServiceExt;

    /**
     * Constructor.
     */
    public ModifyRecordsBulkOperation() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean run(BulkOperationRequestContext ctx) {

        if (!(ctx.getConfiguration() instanceof ModifyRecordsConfiguration)) {
            throw new SystemRuntimeException("Modify record operation get from input incorrect class", EX_BULK_OPERATION_INCORRECT_CLASS);
        }

        ModifyRecordsConfiguration configuration = (ModifyRecordsConfiguration) ctx.getConfiguration();

        checkRecord(configuration.getPartiallyFilledRecord());
        checkClassifiers(configuration.getClassifierRecords());
        checkRelations(configuration.getEtalonRelations());

        String storageKeyForRecord = jobServiceExt.putComplexParameter(configuration.getPartiallyFilledRecord());
        JobParameterDTO recordParam = new JobParameterDTO("record", storageKeyForRecord);

        String storageKeyForClassifiers = jobServiceExt.putComplexParameter(configuration.getClassifierRecords());
        JobParameterDTO classifiersParam = new JobParameterDTO("classifiers", storageKeyForClassifiers);
        
        String storageKeyForRelations = jobServiceExt.putComplexParameter(configuration.getEtalonRelations());
        JobParameterDTO relationsParam = new JobParameterDTO("relations", storageKeyForRelations);
        List<String> etalonIds = getEtalonIds(ctx);
        String idsKey = jobServiceExt.putComplexParameter(etalonIds);
        JobParameterDTO searchContext = new JobParameterDTO("idsKey", idsKey);

        String storageKeyForUser = jobServiceExt.putComplexParameter(SecurityContextHolder.getContext().getAuthentication());
        JobParameterDTO user = new JobParameterDTO("user", storageKeyForUser);

        JobParameterDTO entity = new JobParameterDTO("entityName", ctx.getEntityName());

        JobDTO job = new JobDTO();
        job.setDescription("Change simple attribute in all records");
        job.setName("Modify Job");
        job.setEnabled(true);
        job.setJobNameReference(ModifyItemJobConstants.JOB_NAME);
        job.setParameters(asList(recordParam, classifiersParam, relationsParam, entity, searchContext, user));
        JobExecution execution = jobServiceExt.startSystemJob(job);
        return execution != null;
    }

    /**
     * Check record.
     *
     * @param etalonRecord the etalon record
     */
    private void checkRecord(EtalonRecord etalonRecord) {
        if (Objects.isNull(etalonRecord)) {
            return ;
        }

        etalonRecord.getAllAttributesRecursive().stream()
                .map(Attribute::getName)
                .filter(StringUtils::isBlank)
                .findAny()
                .ifPresent(x -> {
                    throw new DataProcessingException("All record attributes should contain name", EX_BULK_OPERATION_MODIFY_RECORD_INCORRECT);
                });
    }

    /**
     * Check classifiers.
     *
     * @param classifiers the classifiers
     */
    private void checkClassifiers(List<EtalonClassifier> classifiers) {
        if (CollectionUtils.isEmpty(classifiers)) {
            return;
        }

        classifiers.forEach(cls -> cls.getAllAttributesRecursive().stream()
                .map(Attribute::getName)
                .filter(StringUtils::isBlank)
                .findAny()
                .ifPresent(x -> {
                    throw new DataProcessingException("All classifier attributes should contain name",
                            ExceptionId.EX_BULK_OPERATION_MODIFY_CLASSIFIER_INCORRECT);
                }));
    }
    
    /**
     * Check relations.
     *
     * @param relations the relations
     */
    private void checkRelations(List<EtalonRelation> relations) {
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }

        relations.forEach(rel -> rel.getAllAttributesRecursive().stream()
                .map(Attribute::getName)
                .filter(StringUtils::isBlank)
                .findAny()
                .ifPresent(x -> {
                    throw new DataProcessingException("All relation attributes should contain name",
                            ExceptionId.EX_BULK_OPERATION_MODIFY_RECORD_INCORRECT);
                }));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BulkOperationInformationDTO configure() {
        // Nothing specific so far
        return new ModifyRecordsInformationDTO();
    }

}
