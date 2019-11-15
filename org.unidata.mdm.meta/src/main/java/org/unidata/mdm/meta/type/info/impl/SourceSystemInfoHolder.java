package org.unidata.mdm.meta.type.info.impl;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.SourceSystemDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SourceSystemInfoHolder implements IdentityModelElement {

    /**
     * Source system.
     */
    private final SourceSystemDef sourceSystem;

    /**
     * Constructor.
     */
    public SourceSystemInfoHolder(SourceSystemDef sourceSystem) {
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
    public String getId() {
        return sourceSystem.getName();
    }

    @Override
    public Long getVersion() {
        return sourceSystem.getVersion();
    }
}
