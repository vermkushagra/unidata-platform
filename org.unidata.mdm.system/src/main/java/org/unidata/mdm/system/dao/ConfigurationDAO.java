package org.unidata.mdm.system.dao;

import java.util.Map;

public interface ConfigurationDAO {

    Map<String, byte[]> fetchAllProperties();

    void save(Map<String, byte[]> properties);
}
