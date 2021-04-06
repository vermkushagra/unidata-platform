package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;


/**
 * The Class ClsfROToDTOConverter.
 */
public class ClsfROToDTOConverter {

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf DTO
     */
    public static ClsfDTO convert(ClsfRO source) {
        if (source == null) {
            return null;
        }
        ClsfDTO target = new ClsfDTO();
        target.setRootNode(ClsfNodeROToDTOConverter.convert(source.getChildren().stream().findFirst().orElse(null)));
        target.setCodePattern(source.getCodePattern());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setValidateCodeByLevel(source.isValidateCodeByLevel());
        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    public static List<ClsfDTO> convert(List<ClsfRO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfDTO> target = new ArrayList<>();
        for (ClsfRO element : source) {
            target.add(convert(element));
        }
        return target;
    }
}
