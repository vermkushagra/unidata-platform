/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import com.unidata.mdm.meta.SourceSystemDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SourceSystemWrapper extends ModelWrapper {

    /**
     * Source system.
     */
    private final SourceSystemDef sourceSystem;

    /**
     * Constructor.
     */
    public SourceSystemWrapper(SourceSystemDef sourceSystem) {
        super();
        this.sourceSystem = sourceSystem;
    }

    /**
     * @return the sourceSystem
     */
    public SourceSystemDef getSourceSystem() {
        return sourceSystem;
    }

    @Override
    public String getUniqueIdentifier() {
        return sourceSystem.getName();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return sourceSystem.getVersion();
    }
}
