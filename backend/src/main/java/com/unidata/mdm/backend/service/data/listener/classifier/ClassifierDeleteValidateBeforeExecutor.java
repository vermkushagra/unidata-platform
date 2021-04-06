package com.unidata.mdm.backend.service.data.listener.classifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersCommonComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;


/**
 * @author Dmitry Kopin
 *         Simple context validity checker.
 */
public class ClassifierDeleteValidateBeforeExecutor
        implements DataRecordBeforeExecutor<DeleteClassifierDataRequestContext> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierDeleteValidateBeforeExecutor.class);
    /**
     * Classifiers common component.
     */
    @Autowired
    private ClassifiersCommonComponent classifiersCommonComponent;

    /**
     * Constructor.
     */
    public ClassifierDeleteValidateBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteClassifierDataRequestContext ctx) {

        ClassifierKeys keys = classifiersCommonComponent.identify(ctx);
        if (keys == null) {
            final String message = "Classifier data record not found.";
            LOGGER.warn(message);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_DELETE_CLASSIFIER_RECORD_NOT_FOUND);
        }

        return true;
    }

}
