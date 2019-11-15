package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.AbstractManagedIndexId;

/**
 * @author Mikhail Mikhailov
 * Classifier data index id.
 */
public class ModelIndexId extends AbstractManagedIndexId {
    /**
     * The name of the classifier.
     */
    private String classifierName;
    /**
     * Constructor.
     */
    private ModelIndexId() {
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
    public IndexType getSearchType() {
        return ModelIndexType.MODEL;
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param classifierName the name of the classifier
     * @param etalonId the record etalon id
     * @param nodeId the classifier naode id
     * @return index id
     */
    public static ModelIndexId of(String entityName, String classifierName, String etalonId, String nodeId) {

        ModelIndexId id = new ModelIndexId();

        id.entityName = entityName;
        id.classifierName = classifierName;
        id.indexId = PeriodIdUtils.childPeriodId(etalonId, classifierName, nodeId);
        id.routing = etalonId;

        return id;
    }
}
