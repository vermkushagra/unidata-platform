package com.unidata.mdm.backend.common.configuration.application;

import java.util.Collection;

import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyMetaDTO;

public interface PropertiesMetaInformationProvider {
    Collection<ConfigurationPropertyMetaDTO> properties();
}
