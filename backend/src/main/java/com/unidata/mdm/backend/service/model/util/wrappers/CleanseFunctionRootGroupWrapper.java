/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import com.unidata.mdm.meta.CleanseFunctionGroupDef;

/**
 * @author Mikhail Mikhailov
 */
public class CleanseFunctionRootGroupWrapper extends ModelWrapper {

    /**
     * Cleanse function root group.
     */
    private final CleanseFunctionGroupDef rootGroup;

    /**
     * Constructor.
     */
    public CleanseFunctionRootGroupWrapper(CleanseFunctionGroupDef rootGroup) {
        super();
        this.rootGroup = rootGroup;
    }

    /**
     * @return the enumeration
     */
    public CleanseFunctionGroupDef getCleanseFunctionRootGroup() {
        return rootGroup;
    }

    @Override
    public String getUniqueIdentifier() {
        return rootGroup.getGroupName();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return rootGroup.getVersion();
    }
}
