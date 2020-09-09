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

package com.unidata.mdm.backend.service.job.exchange.in;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.configuration.BeanNameConstants;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.module.ImportDataModuleFeature;
import com.unidata.mdm.backend.common.service.ModuleService;
import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.service.job.DefaultJobParameterProcessor;
import com.unidata.mdm.backend.service.job.JobServiceExt;
import com.unidata.mdm.backend.service.job.registry.JobTemplateParameters;

/**
 * @author Mikhail Mikhailov
 * Parameter transformer.
 */
public class ImportDataJobParameterProcessor extends DefaultJobParameterProcessor {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Default object mapper.
     */
    @Autowired
    @Qualifier(BeanNameConstants.DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    protected ObjectMapper objectMapper;

    /** The job service. */
    @Autowired
    private JobServiceExt jobServiceExt;

    @Autowired
    private ModuleService moduleService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(JobParameterDTO jobParameter, JobParametersBuilder builder) {

        if ("definitionContent".equals(jobParameter.getName())) {

            String definitionContent = jobParameter.getStringValue();
            if (Objects.nonNull(definitionContent)) {
                try {
                    ExchangeDefinition exchangeEntity = objectMapper.readValue(definitionContent, ExchangeDefinition.class);
                    builder.addString(ImportDataJobConstants.PARAM_DEFINITION, jobServiceExt.putComplexParameter(exchangeEntity));
                } catch (IOException e) {
                    final String message = "Caught a 'IOException' while unmarshalling exchange definition. {}";
                    LOGGER.warn(message, e);
                    throw new JobException(message, e,
                            ExceptionId.EX_DATA_IMPORT_UNABLE_TO_PARSE_EXCHANGE_DEFINITION,
                            e.getLocalizedMessage());
                }
            }
        } else if (ImportDataJobConstants.PARAM_DATABASE_URL.equals(jobParameter.getName())) {

            DatabaseVendor vendor = DatabaseVendor.fromUrl(jobParameter.getStringValue());
            if (Objects.nonNull(vendor)) {
                builder.addString(ImportDataJobConstants.PARAM_IMPORT_DATABASE_VENDOR, vendor.name());
            } // throw else

            super.process(jobParameter, builder);
        } else  {
            super.process(jobParameter, builder);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobTemplateParameters filter(JobTemplateParameters parameters) {

        Map<String, Object> filtered = new HashMap<>(parameters.getValueMap());
        if (!moduleService.isSupported(ImportDataModuleFeature.FEATURE_LARGE_MODE)) {
            filtered.remove(ImportDataJobConstants.PARAM_INITIAL_LOAD);
        }

        if (!moduleService.isSupported(ImportDataModuleFeature.FEATURE_LARGE_MODE)) {
            filtered.remove(ImportDataJobConstants.PARAM_DATA_SET_SIZE);
        }

        if (!moduleService.isSupported(ImportDataModuleFeature.FEATURE_CLUSTERS_CALCULATION)) {
            filtered.remove(ImportDataJobConstants.PARAM_RESOLVE_BY_MATCHING);
        }

        JobTemplateParameters newTemplateParameters = new JobTemplateParameters(parameters.getJobName(), filtered, this);
        newTemplateParameters.setValidators(parameters.getValidators());
        return newTemplateParameters;
    }
}
