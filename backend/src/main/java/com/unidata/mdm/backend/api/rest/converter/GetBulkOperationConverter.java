package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.bulk.GetBulkOperationRO;
import com.unidata.mdm.backend.common.dto.GetBulkOperationDTO;

/**
 * @author Mikhail Mikhailov
 * Get bulk operation converter.
 */
public class GetBulkOperationConverter {

    /**
     * Constructor.
     */
    private GetBulkOperationConverter() {
        super();
    }

    /**
     * From internal 'to'.
     * @param source the source
     * @return RO
     */
    public static GetBulkOperationRO to(GetBulkOperationDTO source) {

        if (source == null) {
            return null;
        }

        GetBulkOperationRO target = new GetBulkOperationRO();
        target.setType(source.getType().name());
        target.setDescription(source.getDescription());

        return target;
    }

    /**
     * From internal 'to' list.
     * @param source the source list
     * @return RO list
     */
    public static List<GetBulkOperationRO> to(List<GetBulkOperationDTO> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<GetBulkOperationRO> target = new ArrayList<>(source.size());
        for (GetBulkOperationDTO dto : source) {
            target.add(to(dto));
        }

        return target;
    }
}
