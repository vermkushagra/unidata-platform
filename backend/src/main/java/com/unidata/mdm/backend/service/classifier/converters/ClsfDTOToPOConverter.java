package com.unidata.mdm.backend.service.classifier.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;


/**
 * The Class ClsfDTOToPOConverter.
 */
public class ClsfDTOToPOConverter {

    /**
     * Instantiates a new clsf DTO to PO converter.
     */
    private ClsfDTOToPOConverter() {
        super();
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf PO
     */
    public static ClsfPO convert(ClsfDTO source) {
        if (source == null) {
            return null;
        }
        ClsfPO target = new ClsfPO();
        target.setCodePattern(source.getCodePattern());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setRootNode(ClsfNodeDTOToPOConverter.convert(source.getRootNode()));
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setValidateCodeByLevel(source.isValidateCodeByLevel());

        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    public static List<ClsfPO> convert(List<ClsfDTO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfPO> target = new ArrayList<>();
        for (ClsfDTO element : source) {
            target.add(convert(element));
        }
        return target;
    }

}
