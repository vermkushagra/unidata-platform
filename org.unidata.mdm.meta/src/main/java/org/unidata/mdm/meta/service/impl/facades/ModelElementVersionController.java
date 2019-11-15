package org.unidata.mdm.meta.service.impl.facades;

import org.unidata.mdm.meta.VersionedObjectDef;

/**
 * Responsible for meta model elements version controlling.
 *
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ModelElementVersionController<V extends VersionedObjectDef> {

    /**
     * Increment version or set in initial state if before it wasn't present!
     *
     * @param modelElement -  first citizen model element.(top level model element)
     */
    void updateVersion(V modelElement);

    /**
     * Set version to initial state
     *
     * @param modelElement -  first citizen model element.(top level model element)
     */
    void setInitialVersion(V modelElement);

}
