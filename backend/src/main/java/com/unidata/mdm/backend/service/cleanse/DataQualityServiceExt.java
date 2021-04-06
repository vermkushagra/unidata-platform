package com.unidata.mdm.backend.service.cleanse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.service.DataQualityService;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.impl.BinaryLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.CharacterLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.meta.DQApplicableType;
import com.unidata.mdm.meta.DQRMappingDef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.Port;

/**
 * The Interface DataQualityService.
 * @author ilya.bykov
 */
public interface DataQualityServiceExt extends DataModifier, DataQualityService {

    /** The etalon record id. */
    public static final String $ETALON_RECORD_ID = "$ETALON_RECORD_ID";

    /** The entity. */
    public static final String $ENTITY ="$ENTITY";
    
    /** record valid from key. */
    public static final String $FROM = "$FROM";
    
    /** record valid to key. */
    public static final String $TO = "$TO";
    /** User storage. Map<String, Object> */
    public static final String $USER_STORAGE = "$USER_STORAGE";

   
    default List<DQRuleDef> filterForEtalon(List<DQRuleDef> rules) {
        List<DQRuleDef> filtered = new ArrayList<>();
        for (DQRuleDef rule : rules) {
            if(rule.getApplicable().contains(DQApplicableType.ETALON)){
                filtered.add(rule);
            }
        }
        return filtered ;
    }

    /**
     * Filter for origin.
     *
     * @param sourceSystem
     *            the source system
     * @param rules
     *            the rules
     * @return the list
     */
    default List<DQRuleDef> filterForOrigin(String sourceSystem, List<DQRuleDef> rules){
        List<DQRuleDef> filtered = new ArrayList<>();
        for (DQRuleDef rule : rules) {
            if (rule.getApplicable().contains(DQApplicableType.ORIGIN)) {
                if (rule.getOrigins().isAll() || rule.getOrigins().getSourceSystem().stream()
                        .anyMatch(ss -> StringUtils.equals(ss.getName(), sourceSystem))) {
                    filtered.add(rule);
                }
            }
        }
        return filtered ;
    }
    /**
     * Extract.
     *
     * @param rules
     *            the rules
     * @param record
     *            the record
     * @param entityName
     *            the entity name
     * @return the map
     */
    default Map<String, Attribute> extract(List<DQRuleDef> rules, DataRecord record, String entityName) {

        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyMap();
        }

        Set<String> elementsAffected = new HashSet<>();
        for (DQRuleDef r : rules) {
            List<DQRMappingDef> ms = r.getDqrMapping();
            for (DQRMappingDef m : ms) {
                if (!StringUtils.isEmpty(m.getAttributeName()) && !elementsAffected.contains(m.getAttributeName())) {
                    elementsAffected.add(m.getAttributeName());
                }
            }
        }

        return find(elementsAffected, record, entityName);
    }

    /**
     * Copy state of 'from' attr to 'to'attr
     * Note: DQ implementation reset measurement attrs because after DQ we have an executor which will set their in default.
     * UN-4225
     * @param from -  source attr
     * @param to   - destination attr
     */
    @Override
    default void set(Attribute from, Attribute to) {
        DataModifier.super.set(from, to);
        if (to.getAttributeType() != Attribute.AttributeType.SIMPLE) {
            return;
        }
        if (((SimpleAttribute<?>) to).getDataType() != SimpleAttribute.DataType.MEASURED) {
            return;
        }
        MeasuredSimpleAttributeImpl measuredSimpleAttribute = (MeasuredSimpleAttributeImpl) to;
        measuredSimpleAttribute.withValueId(null)
                               .withInitialUnitId(null)
                               .withInitialValue(null);
    }
    
    /**
     * Return value by port name.
     *
     * @param name            port name.
     * @param inputPorts the input ports
     * @param input            map with input objects.
     * @return value by port name.
     * @throws CleanseFunctionExecutionException             if some error occurs while execution of cleanse function.
     */
	default Object getValueByPort(String name,List<Port> inputPorts, Map<String, Object> input) throws CleanseFunctionExecutionException {

		for (Port port : inputPorts) {
			if (StringUtils.equals(port.getName(), name)) {
				Attribute value = (Attribute) input.get(port.getName());				
				if (value == null) {
					return null;
				} else if (value.getAttributeType() == AttributeType.SIMPLE) {
				    return getValueFromSimpleAttribute(port, (SimpleAttribute<?>) value);
				} else if (value.getAttributeType() == AttributeType.CODE) {
				    return getValueFromCodeAttribute(port, (CodeAttribute<?>) value);
				} else if (value.getAttributeType() == AttributeType.ARRAY) {
				    return getValueFromArrayAttribute(port, (ArrayAttribute<?>) value);
				}

				break;
			}
		}

		return null;
	}

	/**
	 * Gets the value from code attribute.
	 *
	 * @param port the port
	 * @param attribute the attribute
	 * @return the value from code attribute
	 */
	default Object getValueFromCodeAttribute(Port port, CodeAttribute<?> attribute) {

        switch (port.getDataType()) {
            case INTEGER:
                return attribute == null ? null : attribute.getValue();
            case STRING:
                return attribute == null || attribute.getValue() == null
                    ? StringUtils.EMPTY
                    : attribute.getValue();
            case ANY:
                return attribute;
            default:
                break;
        }

        return null;
    }

	/**
	 * Gets the value from simple attribute.
	 *
	 * @param port the port
	 * @param attribute the attribute
	 * @return the value from simple attribute
	 */
	default Object getValueFromSimpleAttribute(Port port, SimpleAttribute<?> attribute) {

	    switch (port.getDataType()) {
            case BLOB:
                return attribute == null ? null : ((BinaryLargeValueImpl) attribute.getValue()).getData();
            case CLOB:
                return attribute == null ? null : ((CharacterLargeValueImpl) attribute.getValue()).getData();
            case BOOLEAN:
                return attribute == null ? Boolean.FALSE : attribute.getValue();
            case DATE:
            case TIME:
            case TIMESTAMP:
            case INTEGER:
            case NUMBER:
                return attribute == null ? null : attribute.getValue();
            case STRING:
            	 return attribute == null ? null : attribute.getValue();
            case ANY:
                return attribute;
            default:
                break;
	    }

	    return null;
	}

	/**
	 * Gets the value from array attribute.
	 *
	 * @param port the port
	 * @param attribute the attribute
	 * @return the value from array attribute
	 */
	default Object getValueFromArrayAttribute(Port port, ArrayAttribute<?> attribute) {

        switch (port.getDataType()) {
            case DATE:
            case TIME:
            case TIMESTAMP:
            case INTEGER:
            case STRING:
            case NUMBER:
                return attribute == null
                    ? null
                    : attribute.toArray();
            case ANY:
                return attribute;
            default:
                break;
        }

        return null;
    }
}
