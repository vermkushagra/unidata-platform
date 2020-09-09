/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.api.rest.converter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.meta.DQActionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQApplicableDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQEnrichDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRMappingDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQROriginsDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRRaiseDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRuleDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQTypeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.PhaseDefinition;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.CustomPropertyDef;
import com.unidata.mdm.meta.DQApplicableType;
import com.unidata.mdm.meta.DQRActionType;
import com.unidata.mdm.meta.DQREnrichDef;
import com.unidata.mdm.meta.DQRMappingDef;
import com.unidata.mdm.meta.DQROriginsDef;
import com.unidata.mdm.meta.DQRPhaseType;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRSourceSystemRef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleExecutionContext;
import com.unidata.mdm.meta.DQRuleRunType;
import com.unidata.mdm.meta.DQRuleType;
import com.unidata.mdm.meta.SeverityType;


/**
 * The Class DQRuleDefinitionToDQRuleDefConverter.
 *
 * @author Michael Yashin. Created on 11.06.2015.
 */
public class DQRuleDefinitionToDQRuleDefConverter {


    /**
     * Convert list.
     *
     * @param sourceList
     *            the source list
     * @param targetList
     *            the target list
     */
    public static void convertList(List<DQRuleDefinition> sourceList, List<DQRuleDef> targetList) {
        for (DQRuleDefinition source : sourceList) {
            DQRuleDef target = convert(source);
            targetList.add(target);
        }
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the DQ rule def
     */
    public static DQRuleDef convert(DQRuleDefinition source) {
        if (source == null) {
            return null;
        }

        DQRuleDef target = JaxbUtils.getMetaObjectFactory().createDQRuleDef();

        // UN-7293
        // target.setId(source.getId());
        // target.setComplexAttributeName(source.getComplexAttributeName());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setCleanseFunctionName(source.getCleanseFunctionName());

        target.setOrder(BigInteger.valueOf(source.getOrder()));
        mapInputs(source.getInputs(), target.getDqrMapping());
        mapOutputs(source.getOutputs(), target.getDqrMapping());
        target.getApplicable().addAll(convertApplicable(source.getApplicable()));
        target.setOrigins(convertDQROriginsDefinition(source.getOrigins()));
        target.setRaise(convertDQRRaiseDefinition(source.getRaise()));
        target.setEnrich(convertDQREnrichDefinition(source.getEnrich()));
		if (source.isIsEnrichment()) {
			target.getType().add(DQRuleType.ENRICH);
		}
		if (source.isIsValidation()) {
			target.getType().add(DQRuleType.VALIDATE);
		}
        //TODO:
        //target.getType().addAll(convertType(source.getDqType()));
        target.setSpecial(source.isSpecial());
        target.setRunType(source.getRunType() == null ? null : DQRuleRunType.fromValue(source.getRunType().name()));
        target.setExecutionContextPath(source.getExecutionContextPath());
        target.setExecutionContext(source.getExecutionContext() == null ? null : DQRuleExecutionContext.fromValue(source.getExecutionContext().name()));
        if(source.getCustomProperties()!=null) {
        	target.getCustomProperties().addAll(convert(source.getCustomProperties()));
        }
        return target;
    }
    
    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    private static List<CustomPropertyDef> convert(List<CustomPropertyDefinition> source) {
    	if(source==null) {
    		return null;
    	}
    	List<CustomPropertyDef> target = new ArrayList<>();
    	source.forEach(s->target.add(convert(s)));
    	return target;
    }
    
    /**
     * Convert.
     *
     * @param source the source
     * @return the custom property def
     */
    private static CustomPropertyDef convert(CustomPropertyDefinition source) {
    	if(source==null) {
    		return null;
    	}
    	CustomPropertyDef target = new CustomPropertyDef();
    	target.setName(source.getName());
    	target.setValue(source.getValue());
    	return target;
    }
    
    /**
     * Convert applicable.
     *
     * @param source the source
     * @return the list
     */
        private static List<DQApplicableType> convertApplicable(List<DQApplicableDefinition> source) {
            List<DQApplicableType> target = new ArrayList<DQApplicableType>();

            for (DQApplicableDefinition appl : source) {
                if (DQApplicableDefinition.ETALON.equals(appl)) {
                    target.add(DQApplicableType.ETALON);
                } else if (DQApplicableDefinition.ORIGIN.equals(appl)) {
                    target.add(DQApplicableType.ORIGIN);
                }
            }
            return target;
        }

    /**
     * Convert type.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<DQRuleType> convertType(List<DQTypeDefinition> source) {
        List<DQRuleType> target = new ArrayList<DQRuleType>();

        for (DQTypeDefinition dqTypeDefinition : source) {
            if (DQTypeDefinition.ENRICH.equals(dqTypeDefinition)) {
                target.add(DQRuleType.ENRICH);
            } else if (DQTypeDefinition.VALIDATE.equals(dqTypeDefinition)) {
                target.add(DQRuleType.VALIDATE);
            }
        }
        return target;
    }

    /**
     * Convert dqr enrich definition.
     *
     * @param source
     *            the source
     * @return the DQR enrich def
     */
    private static DQREnrichDef convertDQREnrichDefinition(DQEnrichDefinition source) {
        if (source == null) {
            return null;
        }
        DQREnrichDef target = new DQREnrichDef();
        target.setSourceSystem(source.getSourceSystem());
        target.setPhase(convertPhaseDefinition(source.getPhase()));
        target.setAction(convertActionDefinition(source.getAction()));
        return target;
    }

    /**
     * Convert action definition.
     *
     * @param action
     *            the action
     * @return the DQR action type
     */
    private static DQRActionType convertActionDefinition(DQActionDefinition action) {
        if (DQActionDefinition.CREATE_NEW.equals(action)) {
            return DQRActionType.CREATE_NEW;
        } else if (DQActionDefinition.UPDATE_CURRENT.equals(action)) {
            return DQRActionType.UPDATE_CURRENT;
        }
        return null;
    }

    /**
     * Convert phase definition.
     *
     * @param source
     *            the source
     * @return the DQR phase type
     */
    private static DQRPhaseType convertPhaseDefinition(PhaseDefinition source) {
        if (PhaseDefinition.AFTER_MERGE.equals(source)) {
            return DQRPhaseType.AFTER_MERGE;
        } else if (PhaseDefinition.AFTER_UPSERT.equals(source)) {
            return DQRPhaseType.AFTER_UPSERT;
        } else if (PhaseDefinition.BEFORE_UPSERT.equals(source)) {
            return DQRPhaseType.BEFORE_UPSERT;
        }
        return null;
    }

    /**
     * Map inputs.
     *
     * @param source
     *            the source
     * @param target
     *            the target
     */
    private static void mapInputs(List<DQRMappingDefinition> source, List<DQRMappingDef> target) {
        for (DQRMappingDefinition dqrMappingDef : source) {
            SimpleAttributeRO attr = dqrMappingDef.getAttributeConstantValue();
            ConstantValueDef cvd = null;
            if (attr != null && attr.getType() != null) {
                cvd = convertConstant(attr);
            }

            DQRMappingDef mappingDef = JaxbUtils.getMetaObjectFactory().createDQRMappingDef();
            mappingDef.setAttributeName(dqrMappingDef.getAttributeName());
            mappingDef.setAttributeConstantValue(cvd);
            mappingDef.setInputPort(dqrMappingDef.getFunctionPort());

            target.add(mappingDef);
        }
    }
    /**
     * Convert from simple attribute to dq constant.
     * @param source simple attribute
     * @return dq constant
     */
	public static ConstantValueDef convertConstant(SimpleAttributeRO source) {

		ConstantValueDef target = null;
		if (source != null && source.getType() != null) {
		    target = JaxbUtils.getMetaObjectFactory().createConstantValueDef();

		    switch (source.getType()) {
		    case BOOLEAN:
		        target.withBoolValue((Boolean) source.getValue());
		        break;
		    case DATE:
                target.withDateValue((LocalDate) source.getValue());
		        break;
		    case INTEGER:
		        target.withIntValue((Long) source.getValue());
		        break;
		    case NUMBER:
		        target.withNumberValue((Double) source.getValue());
		        break;
		    case STRING:
		        target.withStringValue((String) source.getValue());
		        break;
		    case TIME:
                target.withTimeValue((LocalTime)source.getValue());
		        break;
		    case TIMESTAMP:
                target.withTimestampValue((LocalDateTime)source.getValue());
		        break;
		    case BLOB:
		    case CLOB:
		    default:
		        break;
		    }
		}
		return target;
	}

    /**
     * Map outputs.
     *
     * @param source
     *            the source
     * @param target
     *            the target
     */
    private static void mapOutputs(List<DQRMappingDefinition> source, List<DQRMappingDef> target) {
        for (DQRMappingDefinition dqrMappingDef : source) {
            DQRMappingDef mappingDef = JaxbUtils.getMetaObjectFactory().createDQRMappingDef();
            mappingDef.setAttributeName(dqrMappingDef.getAttributeName());
            mappingDef.setOutputPort(dqrMappingDef.getFunctionPort());

            target.add(mappingDef);
        }
    }

    /**
     * Convert dqr origins definition.
     *
     * @param source
     *            the source
     * @return the DQR origins def
     */
    private static DQROriginsDef convertDQROriginsDefinition(DQROriginsDefinition source) {
        if (source == null) {
            return null;
        }
        DQROriginsDef target = new DQROriginsDef();

        target.setAll(source.isAll());

        for (String sourceSystem : source.getSourceSystems()) {
            target.getSourceSystem().add(new DQRSourceSystemRef().withName(sourceSystem));
        }

        return target;
    }

    /**
     * Convert dqr raise definition.
     *
     * @param source
     *            the source
     * @return the DQR raise def
     */
    private static DQRRaiseDef convertDQRRaiseDefinition(DQRRaiseDefinition source) {
        if (source == null) {
            return null;
        }
        DQRRaiseDef target = new DQRRaiseDef();
        target.setCategoryPort(source.getCategoryPort());
        target.setCategoryText(source.getCategoryText());
        target.setFunctionRaiseErrorPort(source.getFunctionRaiseErrorPort());
        target.setMessagePort(source.getMessagePort());
        target.setMessageText(source.getMessageText());
        target.setSeverityPort(source.getSeverityPort());
        target.setSeverityValue(source.getSeverityValue() == null ? SeverityType.NORMAL : SeverityType.fromValue(source.getSeverityValue()));
        target.setPathsPort(source.getPathsPort());
        target.setPhase(convertPhaseDefinition(source.getPhase()));
        return target;
    }
}
