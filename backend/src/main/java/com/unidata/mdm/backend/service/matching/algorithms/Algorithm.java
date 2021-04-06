package com.unidata.mdm.backend.service.matching.algorithms;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;

/**
 * Algorithm define methods and rules, which can say is record matched or not.
 */
public interface Algorithm {
    /**
     * @return id
     */
    Integer getAlgorithmId();
    /**
     * @return algorithm
     */
    @Nonnull
    String getAlgorithmName();
    /**
     * @return algorithm description
     */
    @Nonnull
    String getAlgorithmDescription();
    /**
     * @return true if exact otherwise false
     */
    boolean isExact();
    /**
     * Gets exact algorithm type.
     * @return type
     */
    AlgorithmType getType();
    /**
     * Constraucts value, suitable for clustering/blocking off the attribute.
     * @param attr the attribute to use
     * @return value, may be null
     */
    @Nullable
    Object construct(Attribute attr);

    /**
     * Constraucts value, suitable for clustering/blocking off the attribute.
     * @param attr the attribute to use
     * @param additional information to use
     * @return value, may be null
     */
    @Nullable
    default Object construct(Attribute attr, Object additional){
        return null;
    }
    /**
     * @param attributeMap - attribute map
     * @return - null in case when it mean find all records
     */
    @Nullable
    FormFieldsGroup getFormFieldGroup(Map<Integer, SimpleAttribute<?>> attributeMap);
    /**
     * @return template for filling
     */
    @Nonnull
    MatchingAlgorithm getTemplate();
    /**
     * @return collection of fields ids, which can be used for calculation cluster id.
     */
    Collection<Integer> getFieldsForIdentify();

    default Collection<Integer> getSupplementaryFields(Integer id){
        return Collections.emptyList();
    }
}
