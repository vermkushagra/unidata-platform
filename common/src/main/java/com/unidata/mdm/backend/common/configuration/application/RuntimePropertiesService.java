package com.unidata.mdm.backend.common.configuration.application;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyDTO;

public interface RuntimePropertiesService {

    Collection<ConfigurationPropertyDTO> availableProperties();

    Collection<ConfigurationPropertyDTO> getPropertiesByGroup(String groupName);

    Optional<ConfigurationPropertyDTO> property(String name);

    Collection<String> updatePropertiesValuesFromExternalPlace(Map<String, String> properties);
}
