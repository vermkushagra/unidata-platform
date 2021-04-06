/**
 *
 */
package com.unidata.mdm.backend.conf.impl;

import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.exits.DeleteListener;
import com.unidata.mdm.backend.common.integration.exits.DeleteRelationListener;
import com.unidata.mdm.conf.Delete;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteImpl extends Delete {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -8343557084870357948L;

    /**
     * Before delete.
     */
    private final Map<String, DeleteListener> beforeEtalonDeactivationInstances = new HashMap<>();

    /**
     * After delete.
     */
    private final Map<String, DeleteListener> afterEtalonDeactivationInstances = new HashMap<>();
    /**
     * Before relation delete.
     */
    private final Map<String, DeleteRelationListener> beforeRelationDeactivationInstances = new HashMap<>();
    /**
     * After relation delete.
     */
    private final Map<String, DeleteRelationListener> afterRelationDeactivationInstances = new HashMap<>();

    /**
     * Constructor.
     */
    public DeleteImpl() {
        super();
    }

    /**
     * @return the beforeEtalonDeactivationListeners
     */
    public Map<String, DeleteListener> getBeforeEtalonDeactivationInstances() {
        return beforeEtalonDeactivationInstances;
    }


    /**
     * @return the afterEtalonDeactivationListeners
     */
    public Map<String, DeleteListener> getAfterEtalonDeactivationInstances() {
        return afterEtalonDeactivationInstances;
    }


    public Map<String, DeleteRelationListener> getBeforeRelationDeactivationInstances() {
        return beforeRelationDeactivationInstances;
    }


    public Map<String, DeleteRelationListener> getAfterRelationDeactivationInstances() {
        return afterRelationDeactivationInstances;
    }
}
