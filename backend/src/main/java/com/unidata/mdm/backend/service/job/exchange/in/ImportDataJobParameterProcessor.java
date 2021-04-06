package com.unidata.mdm.backend.service.job.exchange.in;

import java.io.IOException;
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
import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.service.job.DefaultJobParameterProcessor;
import com.unidata.mdm.backend.service.job.JobServiceExt;

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
}
