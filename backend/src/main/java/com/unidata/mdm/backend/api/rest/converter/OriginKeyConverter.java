package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.data.OriginKeyRO;
import com.unidata.mdm.backend.common.keys.OriginKey;

/**
 * @author Mikhail Mikhailov
 *
 */
public class OriginKeyConverter {

    /**
     * Constructor.
     */
    private OriginKeyConverter() {
        super();
    }

    public static OriginKeyRO to(OriginKey key) {

        if (Objects.isNull(key)) {
            return null;
        }

        OriginKeyRO result = new OriginKeyRO();
        result.setEnrichment(key.isEnriched());
        result.setEntityName(key.getEntityName());
        result.setExternalId(key.getExternalId());
        result.setSourceSystem(key.getSourceSystem());
        result.setId(key.getId());

        return result;
    }

    public static List<OriginKeyRO> to(List<OriginKey> keys) {

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }

        List<OriginKeyRO> result = new ArrayList<>(keys.size());
        for (OriginKey key : keys) {
            OriginKeyRO ro = to(key);
            if (Objects.isNull(ro)) {
                continue;
            }

            result.add(ro);
        }

        return result;
    }
}
