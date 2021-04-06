package com.unidata.mdm.backend.service.registration.handlers;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.service.registration.keys.AttributeRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.MatchingRuleRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

@Component
public class AttributeMatchingRuleDeleteHandler extends ForbidAbstractDeleteHandler<AttributeRegistryKey, MatchingRuleRegistryKey> {
    @Override
    public UniqueRegistryKey.Type getRemovedEntityType() {
        return UniqueRegistryKey.Type.ATTRIBUTE;
    }

    @Override
    public UniqueRegistryKey.Type getLinkedEntityType() {
        return UniqueRegistryKey.Type.MATCHING_RULE;
    }
}
