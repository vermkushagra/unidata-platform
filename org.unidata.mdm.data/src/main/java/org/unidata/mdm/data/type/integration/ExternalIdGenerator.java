package org.unidata.mdm.data.type.integration;

import org.unidata.mdm.data.context.ExternalIdResettingContext;

/**
 * External id generator interface for implements custom logic
 * @author Dmitry Kopin on 11.10.2018.
 */
public interface ExternalIdGenerator {

    /**
     * Generate external id by ctx
     * @param ctx ctx
     * @return return external id
     */
    Object generateExternalId(ExternalIdResettingContext ctx);
}
