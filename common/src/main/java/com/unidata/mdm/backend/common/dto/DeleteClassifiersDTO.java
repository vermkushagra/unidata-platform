/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Mass delete result DTO.
 */
public class DeleteClassifiersDTO implements ClassifiersDTO<DeleteClassifierDTO> {

    /**
     * Deleted classifiers.
     */
    private final Map<String, List<DeleteClassifierDTO>> classifiers;

    /**
     * Constructor.
     */
    public DeleteClassifiersDTO(Map<String, List<DeleteClassifierDTO>> classifiers) {
        super();
        this.classifiers = classifiers;
    }

    @Override
    public Map<String, List<DeleteClassifierDTO>> getClassifiers() {
        return classifiers;
    }

}
