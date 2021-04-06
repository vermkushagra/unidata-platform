package com.unidata.mdm.integration.test;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.integration.exits.ExecutionContext;
import com.unidata.mdm.backend.common.integration.exits.ExitConstants;
import com.unidata.mdm.backend.common.integration.exits.ExitException;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SampleUppercaseValuesImpl implements UpsertListener {

    /**
     * Constructor.
     */
    public SampleUppercaseValuesImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean beforeOriginUpdate(OriginRecord origin, ExecutionContext ctx) throws ExitException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean beforeOriginInsert(OriginRecord origin, ExecutionContext ctx) throws ExitException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterOriginUpdate(OriginRecord origin, ExecutionContext ctx) {

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

                ctx.putToUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_IS_MODIFIED.name(), Boolean.TRUE);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterOriginInsert(OriginRecord origin, ExecutionContext ctx) {

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

                ctx.putToUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_IS_MODIFIED.name(), Boolean.TRUE);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdateEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        // NOPE

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterInsertEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        // NOPE

    }

}
