/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.data.LargeObjectRO;
import com.unidata.mdm.backend.common.dto.LargeObjectDTO;

/**
 * @author Mikhail Mikhailov
 *
 */
public class LargeObjectToRestLargeObjectConverter {

    /**
     * Constructor.
     */
    private LargeObjectToRestLargeObjectConverter() {
        super();
    }

    public static LargeObjectRO convert(LargeObjectDTO source) {
        LargeObjectRO target = new LargeObjectRO();

        target.setId(source.getId());
        target.setFileName(source.getFileName());
        target.setMimeType(source.getMimeType());
        target.setSize(source.getSize());

        return target;
    }
}
