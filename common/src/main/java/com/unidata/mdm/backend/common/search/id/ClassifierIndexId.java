package com.unidata.mdm.backend.common.search.id;

import com.unidata.mdm.backend.common.search.PeriodIdUtils;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * @author Mikhail Mikhailov
 * Classifier data index id.
 */
public class ClassifierIndexId extends AbstractManagedIndexId {
    /**
     * The name of the classifier.
     */
    private String classifierName;
    /**
     * Constructor.
     */
    private ClassifierIndexId() {
        super();
    }
    /**
     * @return the classifierName
     */
    public String getClassifierName() {
        return classifierName;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SearchType getSearchType() {
        return EntitySearchType.CLASSIFIER;
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param classifierName the name of the classifier
     * @param etalonId the record etalon id
     * @param nodeId the classifier naode id
     * @return index id
     */
    public static ClassifierIndexId of(String entityName, String classifierName, String etalonId, String nodeId) {

        ClassifierIndexId id = new ClassifierIndexId();

        id.entityName = entityName;
        id.classifierName = classifierName;
        id.indexId = PeriodIdUtils.childPeriodId(etalonId, classifierName, nodeId);
        id.routing = etalonId;

        return id;
    }
}
