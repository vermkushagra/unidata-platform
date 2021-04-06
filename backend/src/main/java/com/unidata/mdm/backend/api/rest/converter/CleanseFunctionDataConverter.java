package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * The Class CleanseFunctionDataConverter.
 */
public class CleanseFunctionDataConverter {

    /**
     * Instantiates a new cleanse function data converter.
     */
    private CleanseFunctionDataConverter() {
    }

    /**
     * Convert rest simple to meta simple attr.
     *
     * @param source
     *            the source
     * @return the map
     */
    public static final Map<String, Object> convertRestSimpleToMetaSimpleAttr(List<SimpleAttributeRO> source) {
        Map<String, Object> target = new HashMap<String, Object>();
        for (SimpleAttributeRO simpleAttribute : source) {
            SimpleAttribute attr = SimpleAttributeConverter.from(simpleAttribute);
            target.put(simpleAttribute.getName(), attr);
        }
        return target;
    }

    /**
     * Convert meta simple attr to rest simple attr.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static final List<SimpleAttributeRO> convertMetaSimpleAttrToRestSimpleAttr(Map<String, Object> source) {
        List<SimpleAttributeRO> target = new ArrayList<SimpleAttributeRO>();
        Set<String> keySet = source.keySet();
        for (String string : keySet) {
            SimpleAttribute attr = (SimpleAttribute) source.get(string);
            SimpleAttributeRO attribute = SimpleAttributeConverter.to(attr);
            attribute.setName(string);
            if (attribute != null) {
                target.add(attribute);
            }
        }

        return target;
    }
}
