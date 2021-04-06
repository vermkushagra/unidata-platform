package com.unidata.mdm.backend.service.job.importJob.record;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.configuration.BeanNameConstants;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.service.job.DefaultJobParameterProcessor;
import com.unidata.mdm.backend.service.job.JobServiceExt;

/**
 * @author Mikhail Mikhailov
 * Much more a parameter transformer, than a validator.
 */
public class ImportRecordParameterProcessor extends DefaultJobParameterProcessor {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportRecordParameterProcessor.class);
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
                    DbExchangeEntity exchangeEntity = objectMapper.readValue(definitionContent, DbExchangeEntity.class);
                    builder.addString("definition", jobServiceExt.putComplexParameter(exchangeEntity));
                } catch (IOException e) {
                    final String message = "Caught a 'IOException' while unmarshalling RECORD exchange definition. {}";
                    LOGGER.warn(message, e);
                    throw new JobException(message, e,
                            ExceptionId.EX_DATA_IMPORT_UNABLE_TO_PARSE_EXCHANGE_DEFINITION,
                            e.getLocalizedMessage());
                }
            }

        } else  {
            super.process(jobParameter, builder);
        }
    }
}
