/**
 *
 */
package com.unidata.mdm.backend.conf.impl;

import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.exits.MergeListener;
import com.unidata.mdm.conf.Merge;


/**
 * @author Mikhail Mikhailov
 *
 */
public class MergeImpl extends Merge {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -7108107634917811459L;

    /**
     * Before delete.
     */
    private final Map<String, MergeListener> beforeMergeInstances = new HashMap<>();

    /**
     * After delete.
     */
    private final Map<String, MergeListener> afterMergeInstances = new HashMap<>();

    /**
     * Constructor.
     */
    public MergeImpl() {
        super();
    }

    /**
     * @return the beforeMergeInstances
     */
    public Map<String, MergeListener> getBeforeMergeInstances() {
        return beforeMergeInstances;
    }


    /**
     * @return the afterMergeInstances
     */
    public Map<String, MergeListener> getAfterMergeInstances() {
        return afterMergeInstances;
    }

}
