package com.unidata.mdm.backend.common.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Classifier data records set.
 */
public interface ClassifiersDTO<T extends ClassifierDTO> {
    /**
     * Classifier records, grouped by classifier name.
     * @return map
     */
    Map<String, List<T>> getClassifiers();
}
