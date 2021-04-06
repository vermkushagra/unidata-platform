package com.unidata.mdm.backend.service.classifier.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;

/**
 * The Class ClsfPOToDTOConverter.
 */
public class ClsfPOToDTOConverter {

    /**
     * Instantiates a new clsf PO to DTO converter.
     */
    private ClsfPOToDTOConverter() {
        super();
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf DTO
     */
    public static ClsfDTO convert(ClsfPO source) {
        if (source == null) {
            return null;
        }
        ClsfDTO target = new ClsfDTO();
        target.setCodePattern(source.getCodePattern());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setRootNode(ClsfNodePOToDTOConverter.convert(source.getRootNode()));
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setValidateCodeByLevel(source.isValidateCodeByLevel());
        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<ClsfDTO> convert(List<ClsfPO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfDTO> target = new ArrayList<>();
        for (ClsfPO element : source) {
            target.add(convert(element));
        }
        return target;
    }
}
