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

package com.unidata.mdm.backend.service.bulk;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.dto.bulk.RemoveRelationsFromInformationDTO;
import com.unidata.mdm.backend.service.job.JobServiceExt;
import com.unidata.mdm.backend.service.job.removerelations.RemoveRelationsJobConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_BULK_OPERATION_INCORRECT_CLASS;

public class RemoveRelationsFromBulkOperation extends AbstractBulkOperation {

    /** Job service. */
    @Autowired
    private JobServiceExt jobServiceExt;

    @Override
    public boolean run(BulkOperationRequestContext ctx) {
        if (!(ctx.getConfiguration() instanceof RemoveRelationsFromConfiguration)) {
            throw new SystemRuntimeException("Modify record operation get from input incorrect class", EX_BULK_OPERATION_INCORRECT_CLASS);
        }

        final List<String> etalonIds = getEtalonIds(ctx);

        final JobDTO removeRelationJob = new JobDTO();
        removeRelationJob.setName("Remove relation job");
        removeRelationJob.setDescription("Remove relation");
        removeRelationJob.setEnabled(true);
        removeRelationJob.setJobNameReference(RemoveRelationsJobConstants.JOB_NAME);

        final String storageKeyForEtalonsIds = jobServiceExt.putComplexParameter(etalonIds);
        final JobParameterDTO etalonsIdsParam =
                new JobParameterDTO(RemoveRelationsJobConstants.ETALONS_IDS_PARAM, storageKeyForEtalonsIds);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String storageKeyForUser = jobServiceExt.putComplexParameter(authentication);
        final JobParameterDTO userParam = new JobParameterDTO("user", storageKeyForUser);

        final RemoveRelationsFromConfiguration removeRelationsFromConfiguration = (RemoveRelationsFromConfiguration) ctx.getConfiguration();
        final String storageKeyForRelationsNames =
                jobServiceExt.putComplexParameter(removeRelationsFromConfiguration.getRelationsNames());
        final JobParameterDTO relationsNamesKeyParam =
                new JobParameterDTO("relationsNamesKey", storageKeyForRelationsNames);
        final JobParameterDTO entityNameParam =
                new JobParameterDTO("entityName", ctx.getEntityName());
        final Date asOf = ctx.getApplyBySearchContext().getMainRequest().getAsOf();
        final JobParameterDTO forDateParam =
                new JobParameterDTO(
                        "forDate",
                        asOf != null ?
                                ZonedDateTime.ofInstant(asOf.toInstant(), ZoneId.systemDefault()) :
                                ZonedDateTime.now()
                );


        removeRelationJob.setParameters(
                Arrays.asList(etalonsIdsParam, relationsNamesKeyParam, entityNameParam, forDateParam, userParam)
        );

        jobServiceExt.startSystemJob(removeRelationJob);

        return true;
    }

    @Override
    public BulkOperationInformationDTO configure() {
        return new RemoveRelationsFromInformationDTO();
    }
}
