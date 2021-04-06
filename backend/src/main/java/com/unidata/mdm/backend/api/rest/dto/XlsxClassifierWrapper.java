package com.unidata.mdm.backend.api.rest.dto;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;

/**
 * Wrapper for classifier, for converting to xlsx file
 */
public class XlsxClassifierWrapper {

    /**
     * classifier
     */
    private final ClsfDTO classifierPresentation;

    /**
     * @param classifierPresentation - wrapped classifier
     */
    public XlsxClassifierWrapper(ClsfDTO classifierPresentation) {
        this.classifierPresentation = classifierPresentation;
    }

    public ClsfDTO getClassifierPresentation() {
        return classifierPresentation;
    }
}
