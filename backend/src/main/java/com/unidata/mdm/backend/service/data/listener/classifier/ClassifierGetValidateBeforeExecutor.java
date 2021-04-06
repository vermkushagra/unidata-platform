package com.unidata.mdm.backend.service.data.listener.classifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersCommonComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;


/**
 * @author Dmitry Kopin
 *         Simple context validity checker.
 */
public class ClassifierGetValidateBeforeExecutor
        implements DataRecordBeforeExecutor<GetClassifierDataRequestContext> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierGetValidateBeforeExecutor.class);
    /**
     * Classifier meta data service.
     */
    @Autowired
    private ClsfService classifierMetaDataService;
    /**
     * Classifiers common component.
     */
    @Autowired
    private ClassifiersCommonComponent classifiersCommonComponent;

    /**
     * Constructor.
     */
    public ClassifierGetValidateBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetClassifierDataRequestContext ctx) {

        ClassifierKeys keys = classifiersCommonComponent.identify(ctx);
        if (keys == null) {
            final String message = "Classifier data record not found.";
            LOGGER.warn(message);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_GET_CLASSIFIER_RECORD_NOT_FOUND);
        }

        if (!classifierMetaDataService.isNodeExist(keys.getNodeId(), keys.getName())) {
            // UN-6227
            // Records will be garbage collected by job later.
            /*
            final String message = "Classifier data record refers an invalid node {} in {}.";
            LOGGER.warn(message, keys.getNodeId(), keys.getName());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_GET_CLASSIFIER_RECORD_INVALID_NODE,
                    keys.getNodeId(), keys.getName());
            */
            return false;
        }

        return true;
    }

}
