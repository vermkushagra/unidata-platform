package org.unidata.mdm.data.service.segments;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.data.context.ExternalIdResettingContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.type.integration.ExternalIdGenerator;
import org.unidata.mdm.meta.AbstractExternalIdGenerationStrategyDef;
import org.unidata.mdm.meta.ConcatenatedExternalIdGenerationStrategyDef;
import org.unidata.mdm.meta.CustomExternalIdGenerationStrategyDef;
import org.unidata.mdm.system.util.IdUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface IdGenerationStrategySupport {
// @Modules FIXME Add configuretion support (UN-12228).
//    @Autowired
//    private ConfigurationServiceExt configurationService;

    /**
     * Applies generated keys to a context.
     * @param record the record to as the data source
     * @param ctx ctx context
     * @param strategy the strategy to apply
     */
    default Object applyAutogenerationStrategy(ExternalIdResettingContext ctx, DataRecord record,
                                               @Nonnull AbstractExternalIdGenerationStrategyDef strategy) {

        // Caused by UN-6427
        switch (strategy.getStrategyType()) {
        case RANDOM:
            return IdUtils.v1String();
        case CONCAT:
            return applyConcatenationStrategy((ConcatenatedExternalIdGenerationStrategyDef) strategy, record, ctx.getEntityName());
        case CUSTOM:
            CustomExternalIdGenerationStrategyDef customStrategy = (CustomExternalIdGenerationStrategyDef) strategy;
            // UN-12228
            ExternalIdGenerator customGenerator = null; // configurationService.getExternalIdGenerator(customStrategy.getId());
            return customGenerator == null ? null : customGenerator.generateExternalId(ctx);
        // Not implemented yet.
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
    default String applyConcatenationStrategy(@Nonnull ConcatenatedExternalIdGenerationStrategyDef concatStrategy,
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
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_UNABLE_TO_APPLY_ID_GENERATION_STRATEGY,
                        entityName, attrName);
            }

            values[i] = val;
        }

        return StringUtils.join(values, concatStrategy.getSeparator());
    }
}
