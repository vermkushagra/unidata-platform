package com.unidata.mdm.backend.configuration.application;


import java.util.Collection;
import java.util.Map;

import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyDTO;

public interface RuntimePropertiesService {

    Collection<ConfigurationPropertyDTO> availableProperties();

    Collection<String> updatePropertiesValuesFromExternalPlace(Map<String, String> properties);
}

