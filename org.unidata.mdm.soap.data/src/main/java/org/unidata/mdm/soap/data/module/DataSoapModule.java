package org.unidata.mdm.soap.data.module;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.module.Module;

/**
 * @author Alexander Malyshev
 */
public class DataSoapModule implements Module {

    private static final Set<Dependency> DEPENDENCIES = Collections.singleton(
            new Dependency("org.unidata.mdm.data", "5.2")
    );

    @Override
    public String getId() {
        return "org.unidata.mdm.soap.data";
    }

    @Override
    public String getVersion() {
        return "5.2";
    }

    @Override
    public String getName() {
        return "Data Soap Module";
    }

    @Override
    public String getDescription() {
        return "Data Soap Module";
    }

    @Override
    public Collection<Dependency> getDependencies() {
        return DEPENDENCIES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getResourceBundleBasenames() {
        return new String[]{ "soap_data_messages" };
    }
}
