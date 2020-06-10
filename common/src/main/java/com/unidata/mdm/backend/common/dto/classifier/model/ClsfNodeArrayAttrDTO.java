package com.unidata.mdm.backend.common.dto.classifier.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class ClsfNodeArrayAttrDTO extends ClsfNodeAttrDTO {

    private final List<Object> values = new ArrayList<>();

    public void setValues(final Collection<Object> values) {
        this.values.clear();
        if (CollectionUtils.isNotEmpty(values)) {
            this.values.addAll(values);
        }
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }
    @Override
    public boolean isArray() {
        return true;
    }
}
