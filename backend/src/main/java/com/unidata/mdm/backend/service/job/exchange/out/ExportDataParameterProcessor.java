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

package com.unidata.mdm.backend.service.job.exchange.out;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.configuration.BeanNameConstants;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.service.job.DefaultJobParameterProcessor;
import com.unidata.mdm.backend.service.job.JobServiceExt;

/**
 * @author Mikhail Mikhailov
 * Much more a parameter transformer, than a validator.
 */
public class ExportDataParameterProcessor extends DefaultJobParameterProcessor {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataConstants.EXPORT_JOB_LOGGER_NAME);
    /**
     * Default object mapper.
     */
    @Autowired
    @Qualifier(BeanNameConstants.DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    protected ObjectMapper objectMapper;

    /** The job service. */
    @Autowired
    private JobServiceExt jobServiceExt;

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
                    builder.addString("definition", jobServiceExt.putComplexParameter(exchangeEntity));
                } catch (IOException e) {
                    final String message = "Caught a 'IOException' while unmarshalling RECORD exchange definition. {}";
                    LOGGER.warn(message, e);
                    throw new JobException(message, e,
                            ExceptionId.EX_DATA_EXPORT_UNABLE_TO_PARSE_EXCHANGE_DEFINITION,
                            e.getLocalizedMessage());
                }
            }

        } else if ("asOf".equals(jobParameter.getName())) {

            if (StringUtils.isBlank(jobParameter.getStringValue())) {
                builder.addDate("asOf", null);
                return;
            }

            try {
                ZonedDateTime param = ZonedDateTime.parse(jobParameter.getStringValue());
                builder.addDate("asOf", ConvertUtils.zonedDateTime2Date(param));
            } catch (Exception e) {
                LOGGER.warn("Cannot parse 'asOf' date {}.", jobParameter.getStringValue(), e);
            }
        } else if ("updatesAfter".equals(jobParameter.getName())) {

            if (StringUtils.isBlank(jobParameter.getStringValue())) {
                builder.addDate("updatesAfter", null);
                return;
            }

            try {
                ZonedDateTime param = ZonedDateTime.parse(jobParameter.getStringValue());
                builder.addDate("updatesAfter", ConvertUtils.zonedDateTime2Date(param));
            } catch (Exception e) {
                LOGGER.warn("Cannot parse 'updatesAfter' date {}.", jobParameter.getStringValue(), e);
            }
        } else  {
            super.process(jobParameter, builder);
        }
    }
}
