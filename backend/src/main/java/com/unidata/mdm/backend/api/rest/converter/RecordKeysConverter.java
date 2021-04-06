package com.unidata.mdm.backend.api.rest.converter;

import java.util.Objects;

import com.unidata.mdm.backend.api.rest.dto.data.RecordKeysRO;
import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 *
 */
public class RecordKeysConverter {

    /**
     * Constructor.
     */
    private RecordKeysConverter() {
        super();
    }

    public static RecordKeysRO to(RecordKeys keys) {
        if (Objects.isNull(keys)) {
            return null;
        }

        RecordKeysRO result = new RecordKeysRO();
        result.setEtalonId(keys.getEtalonKey().getId());
        result.setOriginKeys(OriginKeyConverter.to(keys.getNotEnrichedSupplementaryKeys()));
        return result;
    }
}
