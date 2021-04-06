package com.unidata.mdm.backend.service.data.listener;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.meta.AbstractExternalIdGenerationStrategyDef;
import com.unidata.mdm.meta.ConcatenatedExternalIdGenerationStrategyDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class AbstractIdGenerationStrategyExecutor {
    /**
     * The logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractIdGenerationStrategyExecutor.class);
    /**
     * Constructor.
     */
    public AbstractIdGenerationStrategyExecutor() {
        super();
    }

    /**
     * Applies generated keys to a context.
     * @param record the record to as the data source
     * @param entityName the entity name
     * @param strategy the strategy to apply
     */
    protected Object applyAutogenerationStrategy(DataRecord record, String entityName,
            @Nonnull AbstractExternalIdGenerationStrategyDef strategy) {

        // Caused by UN-6427
        switch (strategy.getStrategyType()) {
        case RANDOM:
            return IdUtils.v1String();
        case CONCAT:
            return applyConcatenationStrategy((ConcatenatedExternalIdGenerationStrategyDef) strategy, record, entityName);
        // Not implemented so far.
        case SEQUENCE:
        default:
            break;
        }

        return null;
    }
    /**
     * Applies CONCAT strategy.
     * @param concatStrategy the strategy
     * @param record the data record
     * @param entityName
     * @return
     */
    private String applyConcatenationStrategy(@Nonnull ConcatenatedExternalIdGenerationStrategyDef concatStrategy,
            DataRecord record, String entityName) {

        Object[] values = new Object[concatStrategy.getAttributes().size()];
        for (int i = 0; i < concatStrategy.getAttributes().size(); i++) {

            String attrName = concatStrategy.getAttributes().get(i);

            // Only first level attrs are allowed
            Object val = null;
            Attribute attr = record != null ? record.getAttribute(attrName) : null;
            if (attr != null) {
                switch (attr.getAttributeType()) {
                case SIMPLE:
                    val = ((SimpleAttribute<?>) attr).getValue();
                    break;
                case CODE:
                    val = ((CodeAttribute<?>) attr).getValue();
                    break;
                case ARRAY:
                    val = StringUtils.join(((ArrayAttribute<?>) attr).toArray(), concatStrategy.getSeparator());
                    break;
                default:
                    break;
                }
            }

            if (val == null || StringUtils.isBlank(val.toString())) {
                final String message = "Unable to generate externalId/code attribute value, using autogeneration strategy for entity [{}]. "
                        + "Either no data was given or content for configured fields is missing or incomplete. Processing [{}].";
                LOGGER.warn(message, entityName, attrName);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_UNABLE_TO_APPLY_ID_GENERATION_STRATEGY,
                        entityName, attrName);
            }

            values[i] = val;
        }

        return StringUtils.join(values, concatStrategy.getSeparator());
    }
}
