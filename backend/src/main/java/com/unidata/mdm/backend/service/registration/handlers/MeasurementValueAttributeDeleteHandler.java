package com.unidata.mdm.backend.service.registration.handlers;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.service.registration.keys.AttributeRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.MeasurementValueRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

@Component
public class MeasurementValueAttributeDeleteHandler extends ForbidAbstractDeleteHandler<MeasurementValueRegistryKey, AttributeRegistryKey> {

    @Override
    public UniqueRegistryKey.Type getRemovedEntityType() {
        return UniqueRegistryKey.Type.MEASUREMENT_VALUE;
    }

    @Override
    public UniqueRegistryKey.Type getLinkedEntityType() {
        return UniqueRegistryKey.Type.ATTRIBUTE;
    }
}
