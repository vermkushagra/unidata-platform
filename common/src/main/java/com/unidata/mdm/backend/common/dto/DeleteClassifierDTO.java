package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.keys.ClassifierKeys;

/**
 * @author Mikhail Mikhailov
 * Delete classifier DTO.
 */
public class DeleteClassifierDTO implements ClassifierDTO {

    /**
     * Relation type.
     */
    private final ClassifierKeys keys;
    /**
     * Constructor.
     */
    public DeleteClassifierDTO(ClassifierKeys keys) {
        super();
        this.keys = keys;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeys getClassifierKeys() {
        return keys;
    }
}
