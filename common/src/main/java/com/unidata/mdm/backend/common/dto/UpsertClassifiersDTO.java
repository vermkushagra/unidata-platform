package com.unidata.mdm.backend.common.dto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UpsertClassifiersDTO implements ClassifiersDTO<UpsertClassifierDTO> {

    /**
     * Classifiers data, if requested.
     */
    private final Map<String, List<UpsertClassifierDTO>> classifiers;
    /**
     * Constructor.
     */
    public UpsertClassifiersDTO(Map<String, List<UpsertClassifierDTO>> classifiers) {
        super();
        this.classifiers = classifiers;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<UpsertClassifierDTO>> getClassifiers() {
        return classifiers == null ? Collections.emptyMap() : classifiers;
    }
}
