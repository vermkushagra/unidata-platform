package com.unidata.mdm.backend.dao;

import java.util.Map;

public interface ConfigurationDAO {

    Map<String, byte[]> fetchAllProperties();

    void save(Map<String, byte[]> properties);
}
