package org.unidata.mdm.system.type.module;

/**
 * @author Alexander Malyshev
 */
public class Dependency {
    private final String moduleId;
    private final String version;
    private final String tag;

    public Dependency(String moduleId, String version) {
        this.moduleId = moduleId;
        this.version = version;
        tag = null;
    }

    public Dependency(String moduleId, String version, String tag) {
        this.moduleId = moduleId;
        this.version = version;
        this.tag = tag;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getVersion() {
        return version;
    }

    public String getTag() {
        return tag;
    }
}
