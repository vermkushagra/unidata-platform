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

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.meta.DQActionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQApplicableDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQEnrichDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRMappingDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQROriginsDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRRaiseDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRRuleExecutionContext;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRuleDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRuleRunType;
import com.unidata.mdm.backend.api.rest.dto.meta.DQTypeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.PhaseDefinition;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.service.cleanse.DQUtils;
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
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleType;


/**
 * The Class DQRuleDefToDQRuleDefinitionConverter.
 *
 * @author Michael Yashin. Created on 11.06.2015.
 */
public class DQRuleDefToDQRuleDefinitionConverter {



	/**
	 * Convert list.
	 *
	 * @param sourceList
	 *            the source list
	 * @param targetList
	 *            the target list
	 */
	public static void convertList(List<DQRuleDef> sourceList, List<DQRuleDefinition> targetList) {
		for (DQRuleDef source : sourceList) {
			DQRuleDefinition target = convert(source);
			targetList.add(target);
		}
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the DQ rule definition
	 */
	public static DQRuleDefinition convert(DQRuleDef source) {
		if (source == null) {
			return null;
		}

		DQRuleDefinition target = new DQRuleDefinition();
		// UN-7293
		// target.setId(source.getId());
		// target.setComplexAttributeName(source.getComplexAttributeName());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setCleanseFunctionName(source.getCleanseFunctionName());

		if (source.getOrder() != null) {
			target.setOrder(source.getOrder().intValue());
		}
		mapInputs(source.getDqrMapping(), target.getInputs());
		mapOutputs(source.getDqrMapping(), target.getOutputs());
		target.setOrigins(convertDQROriginsDefinition(source.getOrigins()));
		target.setRaise(convertDQRRaiseDefinition(source.getRaise(), source.getRClass() == DQRuleClass.SYSTEM));
		target.setApplicable(convertApplicable(source.getApplicable()));
		target.setEnrich(convertEnrich(source.getEnrich()));
		target.setDqType(convertDQType(source.getType()));
		target.setSpecial(source.isSpecial());
		target.setRunType(source.getRunType() == null ? null : DQRuleRunType.valueOf(source.getRunType().name()));
		target.setExecutionContextPath(source.getExecutionContextPath());
        target.setExecutionContext(source.getExecutionContext() == null ? null : DQRRuleExecutionContext.valueOf(source.getExecutionContext().name()));
        target.setCustomProperties(convert(source.getCustomProperties()));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<CustomPropertyDefinition> convert(List<CustomPropertyDef> source) {
		if(source == null) {
			return null;
		}
		List<CustomPropertyDefinition> target = new ArrayList<>();
		source.forEach(s->target.add(convert(s)));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the custom property definition
	 */
	private static CustomPropertyDefinition convert(CustomPropertyDef source) {
		if(source == null) {
			return null;
		}
		CustomPropertyDefinition target = new CustomPropertyDefinition();
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
	private static List<DQApplicableDefinition> convertApplicable(List<DQApplicableType> source) {

		List<DQApplicableDefinition> target = new ArrayList<>();

		for (DQApplicableType appl : source) {
			if (DQApplicableType.ETALON.equals(appl)) {
				target.add(DQApplicableDefinition.ETALON);
			} else if (DQApplicableType.ORIGIN.equals(appl)) {
				target.add(DQApplicableDefinition.ORIGIN);
			}
		}
		return target;
	}

	/**
	 * Convert enrich.
	 *
	 * @param source
	 *            the source
	 * @return the DQ enrich definition
	 */
	private static DQEnrichDefinition convertEnrich(DQREnrichDef source) {
		if (source == null) {
			return null;
		}
		DQEnrichDefinition target = new DQEnrichDefinition();
		target.setPhase(convertPhase(source.getPhase()));
		target.setAction(convertAction(source.getAction()));
		target.setSourceSystem(source.getSourceSystem());
		return target;
	}

	/**
	 * Convert action.
	 *
	 * @param source
	 *            the source
	 * @return the DQ action definition
	 */
	private static DQActionDefinition convertAction(DQRActionType source) {
		if (DQRActionType.CREATE_NEW.equals(source)) {
			return DQActionDefinition.CREATE_NEW;
		} else if (DQRActionType.UPDATE_CURRENT.equals(source)) {
			return DQActionDefinition.UPDATE_CURRENT;
		}
		return DQActionDefinition.UPDATE_CURRENT;
	}

	/**
	 * Convert dq type.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	private static List<DQTypeDefinition> convertDQType(List<DQRuleType> source) {
		if (source == null) {
			return null;
		}
		List<DQTypeDefinition> target = new ArrayList<DQTypeDefinition>();
		for (DQRuleType sourceType : source) {
			if (DQRuleType.ENRICH.equals(sourceType)) {
				target.add(DQTypeDefinition.ENRICH);
			} else if (DQRuleType.VALIDATE.equals(sourceType)) {
				target.add(DQTypeDefinition.VALIDATE);
			}
		}
		return target;
	}

	/**
	 * Convert phase.
	 *
	 * @param source
	 *            the phase
	 * @return the phase definition
	 */
	private static PhaseDefinition convertPhase(DQRPhaseType source) {
		if (DQRPhaseType.AFTER_MERGE.equals(source)) {
			return PhaseDefinition.AFTER_MERGE;
		} else if (DQRPhaseType.BEFORE_UPSERT.equals(source)) {
			return PhaseDefinition.BEFORE_UPSERT;
		} else if (DQRPhaseType.AFTER_UPSERT.equals(source)) {
			return PhaseDefinition.AFTER_UPSERT;
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
	private static void mapInputs(List<DQRMappingDef> source, List<DQRMappingDefinition> target) {
		for (DQRMappingDef dqrMappingDef : source) {
			if (dqrMappingDef.getInputPort() == null) {
				continue;
			}

			SimpleAttributeRO attr =  convertConstant(dqrMappingDef.getAttributeConstantValue(), dqrMappingDef.getAttributeName());

			DQRMappingDefinition mappingDef = new DQRMappingDefinition();
			mappingDef.setAttributeName(dqrMappingDef.getAttributeName());
			mappingDef.setAttributeConstantValue(attr);
			mappingDef.setFunctionPort(dqrMappingDef.getInputPort());

			target.add(mappingDef);
		}
	}
	
	/**
	 * Convert constant.
	 *
	 * @param constantValueDef the constant value def
	 * @param attributeName the attribute name
	 * @return the simple attribute RO
	 */
	public static SimpleAttributeRO convertConstant(ConstantValueDef constantValueDef, String attributeName) {
		if (constantValueDef == null || constantValueDef.getType() == null) {
			return null;
		}
		SimpleAttributeRO target = new SimpleAttributeRO();
		target.setName(attributeName);
		target.setType(SimpleDataType.fromValue(constantValueDef.getType().value().value()));
		switch (constantValueDef.getType()) {
		case BOOLEAN:
			target.setValue(constantValueDef.isBoolValue());
			break;
		case DATE:
			target.setValue(constantValueDef.getDateValue());
			break;
		case INTEGER:
			target.setValue(constantValueDef.getIntValue());
			break;
		case NUMBER:
			target.setValue(constantValueDef.getNumberValue());
			break;
		case STRING:
			target.setValue(constantValueDef.getStringValue());
			break;
		case TIME:
			target.setValue(constantValueDef.getTimeValue());
			break;
		case TIMESTAMP:
			target.setValue(constantValueDef.getTimestampValue());
			break;
		default:
			break;
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
	private static void mapOutputs(List<DQRMappingDef> source, List<DQRMappingDefinition> target) {
		for (DQRMappingDef dqrMappingDef : source) {
			if (dqrMappingDef.getOutputPort() == null) {
				continue;
			}
			DQRMappingDefinition mappingDef = new DQRMappingDefinition();
			mappingDef.setAttributeName(dqrMappingDef.getAttributeName());
			mappingDef.setFunctionPort(dqrMappingDef.getOutputPort());
			target.add(mappingDef);
		}
	}

	/**
	 * Convert dqr origins definition.
	 *
	 * @param source
	 *            the source
	 * @return the DQR origins definition
	 */
	private static DQROriginsDefinition convertDQROriginsDefinition(DQROriginsDef source) {
		if (source == null) {
			return null;
		}
		DQROriginsDefinition target = new DQROriginsDefinition();

		target.setAll(source.isAll());

		for (DQRSourceSystemRef sourceSystemRef : source.getSourceSystem()) {
			if (sourceSystemRef.getName() != null) {
				target.getSourceSystems().add(sourceSystemRef.getName());
			}
		}

		return target;
	}

	/**
	 * Convert dqr raise definition.
	 *
	 * @param source
	 *            the source
	 * @param isSystem system rule or not
	 * @return the DQR raise definition
	 */
	private static DQRRaiseDefinition convertDQRRaiseDefinition(DQRRaiseDef source, boolean isSystem) {
		if (source == null) {
			return null;
		}
		DQRRaiseDefinition target = new DQRRaiseDefinition();
		target.setCategoryPort(source.getCategoryPort());
		target.setCategoryText(source.getCategoryText());
		target.setFunctionRaiseErrorPort(source.getFunctionRaiseErrorPort());
		target.setMessagePort(source.getMessagePort());
		target.setMessageText(isSystem
		        ? DQUtils.extractSystemDQRaiseMessageText(source.getMessageText())
		        : source.getMessageText());
		target.setSeverityPort(source.getSeverityPort());
		target.setSeverityValue(source.getSeverityValue() == null
		        ? com.unidata.mdm.meta.SeverityType.CRITICAL.value()
		        : source.getSeverityValue().value());
		target.setPathsPort(source.getPathsPort());
		target.setPhase(convertPhase(source.getPhase()));
		return target;
	}
}
