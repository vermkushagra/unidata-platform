package org.unidata.mdm.core.service;

import org.unidata.mdm.core.context.DeleteLargeObjectRequestContext;
import org.unidata.mdm.core.context.FetchLargeObjectRequestContext;
import org.unidata.mdm.core.context.SaveLargeObjectRequestContext;
import org.unidata.mdm.core.dto.LargeObjectDTO;

/**
 * @author Dmitry Kopin on 26.12.2017.
 */
public interface LargeObjectsServiceComponent {

    /**
     * Gets large object input stream according to context specification.
     * @param ctx the context
     * @return {@link LargeObjectDTO} instance
     */
    LargeObjectDTO fetchLargeObject(FetchLargeObjectRequestContext ctx);

    byte[] fetchLargeObjectByteArray(FetchLargeObjectRequestContext ctx);

    /**
     * Saves large object data.
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    LargeObjectDTO saveLargeObject(SaveLargeObjectRequestContext ctx);

    /**
     * Deletes large object data.
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    boolean deleteLargeObject(DeleteLargeObjectRequestContext ctx);

    /**
     * Check large object exist
     * @param ctx context
     * @return return true if exist, else false
     */
    boolean checkExistLargeObject(FetchLargeObjectRequestContext ctx);
}
