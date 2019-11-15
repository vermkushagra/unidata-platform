package org.unidata.mdm.system.dao;

import java.util.Collection;
import java.util.List;

import org.unidata.mdm.system.dto.ModuleInfo;

public interface ModuleDao {
    boolean moduleInfoTableExists();
    List<ModuleInfo> fetchModulesInfo();
    void saveModulesInfo(Collection<ModuleInfo> modulesInfo);
}
