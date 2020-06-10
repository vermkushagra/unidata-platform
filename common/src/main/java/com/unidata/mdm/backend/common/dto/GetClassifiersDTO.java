package com.unidata.mdm.backend.common.dto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Top level classifiers DTO.
 */
public class GetClassifiersDTO implements ClassifiersDTO<GetClassifierDTO> {
    /**
     * The classifiers.
     */
    private final Map<String, List<GetClassifierDTO>> classifiers;
    /**
     * Constructor.
     */
    public GetClassifiersDTO(Map<String, List<GetClassifierDTO>> classifiers) {
        super();
        this.classifiers = classifiers;
    }
    /**
     * @return the classifiers
     */
    @Override
    public Map<String, List<GetClassifierDTO>> getClassifiers() {
        return classifiers == null ? Collections.emptyMap() : classifiers;
    }
}
