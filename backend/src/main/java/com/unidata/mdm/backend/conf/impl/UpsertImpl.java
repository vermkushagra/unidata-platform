/**
 *
 */
package com.unidata.mdm.backend.conf.impl;

import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.integration.exits.UpsertRelationListener;
import com.unidata.mdm.conf.Upsert;


/**
 * @author Mikhail Mikhailov
 */
public class UpsertImpl extends Upsert {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -3023457488036107833L;
    /**
     * Before origin upsert.
     */
    private final Map<String, UpsertListener> beforeOriginUpsertInstances = new HashMap<>();
    /**
     * After origin upsert.
     */
    private final Map<String, UpsertListener> afterOriginUpsertInstances = new HashMap<>();
    /**
     * Before origin relation upsert.
     */
    private final Map<String, UpsertRelationListener> beforeOriginRelationUpsertInstances = new HashMap<>();
    /**
     * After origin relation upsert.
     */
    private final Map<String, UpsertRelationListener> afterOriginRelationUpsertInstances = new HashMap<>();
    /**
     * After etalon composition.
     */
    private final Map<String, UpsertListener> afterEtalonCompositionInstances = new HashMap<>();
    /**
     * After complete.
     */
    private final Map<String, UpsertListener> afterCompleteInstances = new HashMap<>();
    /**
     * Constructor.
     */
    public UpsertImpl() {
        super();
    }

    /**
     * @return the beforeOriginUpsertInstances
     */
    public Map<String, UpsertListener> getBeforeOriginUpsertInstances() {
        return beforeOriginUpsertInstances;
    }
    /**
     * @return the afterOriginUpsertInstances
     */
    public Map<String, UpsertListener> getAfterOriginUpsertInstances() {
        return afterOriginUpsertInstances;
    }
    /**
     * @return the afterEtalonCompositionInstances
     */
    public Map<String, UpsertListener> getAfterEtalonCompositionInstances() {
        return afterEtalonCompositionInstances;
    }
    /**
     * @return the afterCompleteInstances
     */
    public Map<String, UpsertListener> getAfterCompleteInstances() {
        return afterCompleteInstances;
    }
    /**
     * @return the beforeOriginUpsertInstances
     */
    public Map<String, UpsertRelationListener> getBeforeOriginRelationUpsertInstances() {
        return beforeOriginRelationUpsertInstances;
    }
    /**
     * @return the afterOriginUpsertInstances
     */
    public Map<String, UpsertRelationListener> getAfterOriginRelationUpsertInstances() {
        return afterOriginRelationUpsertInstances;
    }

}
