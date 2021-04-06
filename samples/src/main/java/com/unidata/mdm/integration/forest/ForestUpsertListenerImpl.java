package com.unidata.mdm.integration.forest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.unidata.mdm.backend.common.integration.exits.ExecutionContext;
import com.unidata.mdm.backend.common.integration.exits.ExitConstants;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Forest test listener.
 */
public class ForestUpsertListenerImpl implements UpsertListener {

    /**
     * Test attribute name.
     */
    private static final String ATTR_NAME = "test_user_exit";

    /* (non-Javadoc)
     * @see com.unidata.mdm.integration.exits.UpsertListener#beforeOriginUpdate(com.unidata.mdm.data.OriginRecord, com.unidata.mdm.integration.exits.ExecutionContext)
     */
    @Override
    public boolean beforeOriginUpdate(OriginRecord origin, ExecutionContext ctx) {
        // NOP
        return true;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.integration.exits.UpsertListener#beforeOriginInsert(com.unidata.mdm.data.OriginRecord, com.unidata.mdm.integration.exits.ExecutionContext)
     */
    @Override
    public boolean beforeOriginInsert(OriginRecord origin, ExecutionContext ctx) {
        // NOP
        return true;
    }

    /**
     * Modifyes record upon insert/update.
     * @param origin record
     * @param ctx the context
     */
    private void modifyRecord(OriginRecord origin, ExecutionContext ctx) {

        String attrValue = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        SimpleAttribute<?> attribute = origin.getSimpleAttribute(ATTR_NAME);

        if (attribute == null) {
            attribute = new StringSimpleAttributeImpl(ATTR_NAME, attrValue);
            origin.addAttribute(attribute);
        } else {
            attribute.castValue(attrValue);
        }

        ctx.putToUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_IS_MODIFIED.name(), Boolean.TRUE);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.integration.exits.UpsertListener#afterOriginUpdate(com.unidata.mdm.data.OriginRecord, com.unidata.mdm.integration.exits.ExecutionContext)
     */
    @Override
    public void afterOriginUpdate(OriginRecord origin, ExecutionContext ctx) {
        modifyRecord(origin, ctx);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.integration.exits.UpsertListener#afterOriginInsert(com.unidata.mdm.data.OriginRecord, com.unidata.mdm.integration.exits.ExecutionContext)
     */
    @Override
    public void afterOriginInsert(OriginRecord origin, ExecutionContext ctx) {
        modifyRecord(origin, ctx);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.integration.exits.UpsertListener#afterUpdateEtalonComposition(com.unidata.mdm.data.EtalonRecord, com.unidata.mdm.integration.exits.ExecutionContext)
     */
    @Override
    public void afterUpdateEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.integration.exits.UpsertListener#afterInsertEtalonComposition(com.unidata.mdm.data.EtalonRecord, com.unidata.mdm.integration.exits.ExecutionContext)
     */
    @Override
    public void afterInsertEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        // NOP
    }

}
