package com.unidata.mdm.backend.conf.impl;

import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.exits.AfterSplitListener;
import com.unidata.mdm.backend.common.integration.exits.BeforeSplitListener;
import com.unidata.mdm.conf.Split;

public class SplitImpl extends Split {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -7108107634917811459L;

    /**
     * Before delete.
     */
    private final Map<String, BeforeSplitListener> beforeSplitInstances = new HashMap<>();

    /**
     * After delete.
     */
    private final Map<String, AfterSplitListener> afterSplitInstances = new HashMap<>();

    /**
     * @return the beforeSplitInstances
     */
    public Map<String, BeforeSplitListener> getBeforeSplitInstances() {
        return beforeSplitInstances;
    }


    /**
     * @return the afterSplitInstances
     */
    public Map<String, AfterSplitListener> getAfterSplitInstances() {
        return afterSplitInstances;
    }
}
