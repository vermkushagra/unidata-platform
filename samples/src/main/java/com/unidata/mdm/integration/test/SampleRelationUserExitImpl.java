package com.unidata.mdm.integration.test;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.integration.exits.ExecutionContext;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.common.integration.exits.ExitResult.Status;
import com.unidata.mdm.backend.common.integration.exits.UpsertRelationListener;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SampleRelationUserExitImpl implements UpsertRelationListener {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleRelationUserExitImpl.class);
    /**
     * {@inheritDoc}
     */
    @Override
    public ExitResult beforeOriginRelationUpdate(OriginRelation origin, ExecutionContext ctx) {
        LOGGER.info("SAMPLE USER EXIT BEFORE ORIGIN RELATION UPDATE CALLED.");
        return new ExitResult(Status.SUCCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExitResult beforeOriginRelationInsert(OriginRelation origin, ExecutionContext ctx) {
        LOGGER.info("SAMPLE USER EXIT BEFORE ORIGIN RELATION INSERT CALLED.");
        return new ExitResult(Status.SUCCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExitResult afterOriginRelationUpdate(OriginRelation origin, ExecutionContext ctx) {

        LOGGER.info("SAMPLE USER EXIT AFTER ORIGIN RELATION UPDATE CALLED.");
        ExitResult result = new ExitResult(Status.SUCCESS);
        result.setWasModified(modifyRecord(origin));

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExitResult afterOriginRelationInsert(OriginRelation origin, ExecutionContext ctx) {

        LOGGER.info("SAMPLE USER EXIT AFTER ORIGIN RELATION INSERT CALLED.");
        ExitResult result = new ExitResult(Status.SUCCESS);
        result.setWasModified(modifyRecord(origin));

        return result;
    }

    private boolean modifyRecord(OriginRelation origin) {

        if (Objects.nonNull(origin)) {
            SimpleAttribute<?> attr = origin.getSimpleAttribute("test_attr_1");
            String val = attr != null ? attr.castValue() : null;
            if (StringUtils.isNotBlank(val)) {

                if (StringUtils.isAllLowerCase(val)) {
                    attr.castValue(val.toUpperCase());
                } else if (StringUtils.isAllUpperCase(val)) {
                    attr.castValue(val.toLowerCase());
                } else {
                    attr.castValue(val.toUpperCase());
                }

                return true;
            }
        }

        return false;
    }
}
