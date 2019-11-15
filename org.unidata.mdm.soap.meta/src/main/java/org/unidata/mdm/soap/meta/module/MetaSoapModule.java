package org.unidata.mdm.soap.meta.module;

import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.module.Module;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Alexander Malyshev
 */
public class MetaSoapModule implements Module {

    private static final Set<Dependency> DEPENDENCIES = Collections.singleton(
            new Dependency("org.unidata.mdm.core", "5.2")
    );

    @Override
    public String getId() {
        return "org.unidata.mdm.soap.meta";
    }

    @Override
    public String getVersion() {
        return "5.2";
    }

    @Override
    public String getName() {
        return "Meta Soap Module";
    }

    @Override
    public String getDescription() {
        return "Meta Soap Module";
    }

    @Override
    public Collection<Dependency> getDependencies() {
        return DEPENDENCIES;
    }
}
