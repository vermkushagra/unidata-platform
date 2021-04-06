package com.unidata.mdm.backend.conf.impl;

import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.exits.AfterJoinListener;
import com.unidata.mdm.backend.common.integration.exits.BeforeJoinListener;
import com.unidata.mdm.conf.Join;

public class JoinImpl extends Join {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -7108107634917811459L;

    /**
     * Before delete.
     */
    private final Map<String, BeforeJoinListener> beforeJoinInstances = new HashMap<>();

    /**
     * After delete.
     */
    private final Map<String, AfterJoinListener> afterJoinInstances = new HashMap<>();

    /**
     * @return the beforeJoinInstances
     */
    public Map<String, BeforeJoinListener> getBeforeJoinInstances() {
        return beforeJoinInstances;
    }


    /**
     * @return the afterJoinInstances
     */
    public Map<String, AfterJoinListener> getAfterJoinInstances() {
        return afterJoinInstances;
    }
}
