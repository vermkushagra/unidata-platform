package com.unidata.mdm.meta.api.v5;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.unidata.mdm.meta.v5.ArrayAttributeDef;
import com.unidata.mdm.meta.v5.ArrayValueType;
import com.unidata.mdm.meta.v5.AttributeGroupDef;
import com.unidata.mdm.meta.v5.AttributeMeasurementSettingsDef;
import com.unidata.mdm.meta.v5.BVRMergeTypeDef;
import com.unidata.mdm.meta.v5.BVTMergeTypeDef;
import com.unidata.mdm.meta.v5.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.v5.CodeAttributeDef;
import com.unidata.mdm.meta.v5.ComplexAttributeDef;
import com.unidata.mdm.meta.v5.ConstantValueDef;
import com.unidata.mdm.meta.v5.ConstantValueType;
import com.unidata.mdm.meta.v5.CustomPropertyDef;
import com.unidata.mdm.meta.v5.DQApplicableType;
import com.unidata.mdm.meta.v5.DQRActionType;
import com.unidata.mdm.meta.v5.DQREnrichDef;
import com.unidata.mdm.meta.v5.DQRMappingDef;
import com.unidata.mdm.meta.v5.DQROriginsDef;
import com.unidata.mdm.meta.v5.DQRPhaseType;
import com.unidata.mdm.meta.v5.DQRRaiseDef;
import com.unidata.mdm.meta.v5.DQRSourceSystemRef;
import com.unidata.mdm.meta.v5.DQRuleClass;
import com.unidata.mdm.meta.v5.DQRuleDef;
import com.unidata.mdm.meta.v5.DQRuleType;
import com.unidata.mdm.meta.v5.DefaultClassifier;
import com.unidata.mdm.meta.v5.EntitiesGroupDef;
import com.unidata.mdm.meta.v5.EntityDataQualityDef;
import com.unidata.mdm.meta.v5.EntityDef;
import com.unidata.mdm.meta.v5.EnumerationDataType;
import com.unidata.mdm.meta.v5.GetModelResponse;
import com.unidata.mdm.meta.v5.ListOfCleanseFunctions;
import com.unidata.mdm.meta.v5.ListOfDefaultClassifier;
import com.unidata.mdm.meta.v5.ListOfEntities;
import com.unidata.mdm.meta.v5.ListOfEnumerations;
import com.unidata.mdm.meta.v5.ListOfLookupEntities;
import com.unidata.mdm.meta.v5.ListOfNestedEntities;
import com.unidata.mdm.meta.v5.ListOfRelations;
import com.unidata.mdm.meta.v5.ListOfSourceSystems;
import com.unidata.mdm.meta.v5.LookupEntityDef;
import com.unidata.mdm.meta.v5.MeasurementUnitDef;
import com.unidata.mdm.meta.v5.MeasurementValueDef;
import com.unidata.mdm.meta.v5.MeasurementValues;
import com.unidata.mdm.meta.v5.MergeAttributeDef;
import com.unidata.mdm.meta.v5.MergeSettingsDef;
import com.unidata.mdm.meta.v5.Model;
import com.unidata.mdm.meta.v5.NestedEntityDef;
import com.unidata.mdm.meta.v5.PeriodBoundaryDef;
import com.unidata.mdm.meta.v5.RelType;
import com.unidata.mdm.meta.v5.RelationDef;
import com.unidata.mdm.meta.v5.SeverityType;
import com.unidata.mdm.meta.v5.SimpleAttributeDef;
import com.unidata.mdm.meta.v5.SimpleDataType;
import com.unidata.mdm.meta.v5.SourceSystemDef;



/**
 * The Class ToSOAP.
 */
public class ToSOAP {

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the entity def
	 */
	public static EntityDef convert(com.unidata.mdm.meta.EntityDef source) {
		if (source == null) {
			return null;
		}
		EntityDef target = new EntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
				.withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
				.withClassifiers(fromClassifiers(source.getClassifiers()))
				.withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
				.withCustomProperties(fromCustomProperties(source.getCustomProperties()))
				.withDashboardVisible(source.isDashboardVisible())
				.withDataQuality(fromDataQualities(source.getDataQualities())).withDescription(source.getDescription())
				.withDisplayName(source.getDisplayName()).withGroupName(source.getGroupName())
				.withMergeSettings(fromMergeSettings(source.getMergeSettings())).withName(source.getName())
				.withSimpleAttribute(fromSimpleAttributes(source.getSimpleAttribute()))
				.withValidityPeriod(fromValidityPeriod(source.getValidityPeriod())).withVersion(source.getVersion());
		return target;
	}
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the nested entity def
	 */
	public static NestedEntityDef convert(com.unidata.mdm.meta.NestedEntityDef source) {
		if (source == null) {
			return null;
		}
		NestedEntityDef target = new NestedEntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
				.withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
				.withClassifiers(fromClassifiers(source.getClassifiers()))
				.withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
				.withCustomProperties(fromCustomProperties(source.getCustomProperties()))
				.withDataQuality(fromDataQualities(source.getDataQualities())).withDescription(source.getDescription())
				.withDisplayName(source.getDisplayName())
				.withMergeSettings(fromMergeSettings(source.getMergeSettings())).withName(source.getName())
				.withSimpleAttribute(fromSimpleAttributes(source.getSimpleAttribute()))
				.withValidityPeriod(fromValidityPeriod(source.getValidityPeriod())).withVersion(source.getVersion());
		return target;
	}
	/**
	 * From validity period.
	 *
	 * @param source the source
	 * @return the period boundary def
	 */
	private static PeriodBoundaryDef fromValidityPeriod(com.unidata.mdm.meta.PeriodBoundaryDef source) {
		if (source == null) {
			return null;
		}
		PeriodBoundaryDef target = new PeriodBoundaryDef();
		target.setStart(source.getStart());
		target.setEnd(source.getEnd());
		return target;
	}

	/**
	 * From simple attributes.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<SimpleAttributeDef> fromSimpleAttributes(List<com.unidata.mdm.meta.SimpleAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}

		List<SimpleAttributeDef> target = new ArrayList<>();
		for (com.unidata.mdm.meta.SimpleAttributeDef s : source) {
			target.add(fromSimpleAttribute(s));

		}
		return target;
	}

	/**
	 * From simple attribute.
	 *
	 * @param source the source
	 * @return the simple attribute def
	 */
	private static SimpleAttributeDef fromSimpleAttribute(com.unidata.mdm.meta.SimpleAttributeDef source) {
		if (source == null) {
			return null;
		}
		SimpleAttributeDef target = new SimpleAttributeDef();
		target.setDescription(source.getDescription());
		target.setDisplayable(source.isDisplayable());
		target.setDisplayName(source.getDisplayName());
		target.setEnumDataType(source.getEnumDataType());
		target.setHidden(source.isHidden());
		target.setLinkDataType(source.getLinkDataType());
		target.setLookupEntityCodeAttributeType(source.getLookupEntityCodeAttributeType() == null ? null
				: SimpleDataType.valueOf(source.getLookupEntityCodeAttributeType().name()));
		target.setLookupEntityType(source.getLookupEntityType());
		target.setMainDisplayable(source.isMainDisplayable());
		target.setMask(source.getMask());
		target.setMeasureSettings(fromMeasureSettings(source.getMeasureSettings()));
		target.setName(source.getName());
		target.setNullable(source.isNullable());
		target.setOrder(source.getOrder());
		target.setReadOnly(source.isReadOnly());
		target.setSearchable(source.isSearchable());
		target.setSimpleDataType(
				source.getSimpleDataType() == null ? null : SimpleDataType.valueOf(source.getSimpleDataType().name()));
		target.setUnique(source.isUnique());
		return target;
	}

	/**
	 * From measure settings.
	 *
	 * @param source the source
	 * @return the attribute measurement settings def
	 */
	private static AttributeMeasurementSettingsDef fromMeasureSettings(
			com.unidata.mdm.meta.AttributeMeasurementSettingsDef source) {
		if (source == null) {
			return null;
		}

		AttributeMeasurementSettingsDef target = new AttributeMeasurementSettingsDef();
		target.setDefaultUnitId(source.getDefaultUnitId());
		target.setValueId(source.getValueId());
		return target;
	}

	/**
	 * From merge settings.
	 *
	 * @param source the source
	 * @return the merge settings def
	 */
	private static MergeSettingsDef fromMergeSettings(com.unidata.mdm.meta.MergeSettingsDef source) {
		if (source == null) {
			return null;
		}
		MergeSettingsDef target = new MergeSettingsDef();
		target.setBvrSettings(fromBvrSettings(source.getBvrSettings()));
		target.setBvtSettings(fromBvtSettings(source.getBvtSettings()));
		return target;
	}

	/**
	 * From bvt settings.
	 *
	 * @param source the source
	 * @return the BVT merge type def
	 */
	private static BVTMergeTypeDef fromBvtSettings(com.unidata.mdm.meta.BVTMergeTypeDef source) {
		if (source == null) {
			return null;
		}
		BVTMergeTypeDef target = new BVTMergeTypeDef();
		target.withAttribute(fromMergeAttributes(source.getAttributes()));
		return target;
	}

	/**
	 * From merge attributes.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<MergeAttributeDef> fromMergeAttributes(List<com.unidata.mdm.meta.MergeAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<MergeAttributeDef> target = new ArrayList<>();
		for (com.unidata.mdm.meta.MergeAttributeDef s : source) {
			target.add(fromMergeAttribute(s));

		}
		return target;
	}

	/**
	 * From merge attribute.
	 *
	 * @param source the source
	 * @return the merge attribute def
	 */
	private static MergeAttributeDef fromMergeAttribute(com.unidata.mdm.meta.MergeAttributeDef source) {
		if (source == null) {
			return null;
		}
		MergeAttributeDef target = new MergeAttributeDef();
		target.setName(source.getName());
		target.setSourceSystemsConfig(fromSourceSystemsConfig(source.getSourceSystemsConfigs()));
		return target;
	}

	/**
	 * From source systems config.
	 *
	 * @param source the source
	 * @return the list of source systems
	 */
	private static ListOfSourceSystems fromSourceSystemsConfig(List<com.unidata.mdm.meta.SourceSystemDef> source) {
		ListOfSourceSystems target = new ListOfSourceSystems();
		if (source == null) {
			return target;
		}

		for (com.unidata.mdm.meta.SourceSystemDef s : source) {
			target.getSourceSystem().add(fromSourceSystem(s));
		}

		return target;
	}

	/**
	 * From source system.
	 *
	 * @param source the source
	 * @return the source system def
	 */
	private static SourceSystemDef fromSourceSystem(com.unidata.mdm.meta.SourceSystemDef source) {
		if (source == null) {
			return null;
		}
		SourceSystemDef target = new SourceSystemDef();
		target.setAdmin(source.isAdmin());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setVersion(source.getVersion());
		target.getCustomProperties().addAll(fromCustomProperties(source.getCustomProperties()));
		// source.getCustomProperties();
		target.setWeight(source.getWeight());
		return target;
	}

	/**
	 * From custom properties.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<CustomPropertyDef> fromCustomProperties(List<com.unidata.mdm.meta.CustomPropertyDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<CustomPropertyDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromCustomProperty(s)));
		return target;
	}

	/**
	 * From custom property.
	 *
	 * @param source the source
	 * @return the custom property def
	 */
	private static CustomPropertyDef fromCustomProperty(com.unidata.mdm.meta.CustomPropertyDef source) {
		if (source == null) {
			return null;
		}
		CustomPropertyDef target = new CustomPropertyDef();
		target.setName(source.getName());
		target.setValue(source.getValue());
		return target;
	}

	/**
	 * From bvr settings.
	 *
	 * @param source the source
	 * @return the BVR merge type def
	 */
	private static BVRMergeTypeDef fromBvrSettings(com.unidata.mdm.meta.BVRMergeTypeDef source) {
		if (source == null) {
			return null;
		}
		BVRMergeTypeDef target = new BVRMergeTypeDef();
		target.setSourceSystemsConfig(fromSourceSystemsConfig(source.getSourceSystemsConfigs()));

		return target;
	}

	/**
	 * From data qualities.
	 *
	 * @param source the source
	 * @return the entity data quality def
	 */
	private static EntityDataQualityDef fromDataQualities(List<com.unidata.mdm.meta.DQRuleDef> source) {
		EntityDataQualityDef target = new EntityDataQualityDef();
		if (source == null) {
			return target;
		}

		source.stream().forEach(s -> target.getDqRule().add(fromDataQuality(s)));
		return target;
	}

	/**
	 * From data quality.
	 *
	 * @param source the source
	 * @return the DQ rule def
	 */
	private static DQRuleDef fromDataQuality(com.unidata.mdm.meta.DQRuleDef source) {
		if (source == null) {
			return null;
		}
		DQRuleDef target = new DQRuleDef();
		target.getApplicable().addAll(fromApplicable(source.getApplicable()));
		target.setCleanseFunctionName(source.getCleanseFunctionName());
		target.setComplexAttributeName(source.getComplexAttributeName());
		target.setDescription(source.getDescription());
		target.setEnrich(fromEnrich(source.getEnrich()));
		target.getDqrMapping().addAll(fromDqrMappings(source.getDqrMapping()));
		target.setName(source.getName());
		target.setOrder(source.getOrder());
		target.setOrigins(fromOrigins(source.getOrigins()));
		target.setRaise(fromRaise(source.getRaise()));
		target.setRClass(fromRClass(source.getRClass()));
		target.setSpecial(source.isSpecial());
		target.getType().addAll(fromDQTypes(source.getType()));

		return target;
	}

	/**
	 * From DQ types.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<DQRuleType> fromDQTypes(List<com.unidata.mdm.meta.DQRuleType> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<DQRuleType> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromDQType(s)));
		return target;
	}

	/**
	 * From DQ type.
	 *
	 * @param source the source
	 * @return the DQ rule type
	 */
	private static DQRuleType fromDQType(com.unidata.mdm.meta.DQRuleType source) {
		if (source == null) {
			return null;
		}
		DQRuleType target = DQRuleType.valueOf(source.name());
		return target;
	}

	/**
	 * From R class.
	 *
	 * @param source the source
	 * @return the DQ rule class
	 */
	private static DQRuleClass fromRClass(com.unidata.mdm.meta.DQRuleClass source) {
		if (source == null) {
			return null;
		}
		DQRuleClass target = DQRuleClass.valueOf(source.name());
		return target;
	}

	/**
	 * From raise.
	 *
	 * @param source the source
	 * @return the DQR raise def
	 */
	private static DQRRaiseDef fromRaise(com.unidata.mdm.meta.DQRRaiseDef source) {
		if (source == null) {
			return null;
		}
		DQRRaiseDef target = new DQRRaiseDef();
		target.setCategoryPort(source.getCategoryPort());
		target.setCategoryText(source.getCategoryText());
		target.setFunctionRaiseErrorPort(source.getFunctionRaiseErrorPort());
		target.setMessagePort(source.getMessagePort());
		target.setMessageText(source.getMessageText());
		target.setPhase(fromDQRPhase(source.getPhase()));
		target.setSeverityPort(source.getSeverityPort());
		target.setSeverityValue(fromSeverityType(source.getSeverityValue()));

		return target;
	}

	/**
	 * From severity type.
	 *
	 * @param source the source
	 * @return the severity type
	 */
	private static SeverityType fromSeverityType(com.unidata.mdm.meta.SeverityType source) {
		if (source == null) {
			return null;
		}
		SeverityType target = SeverityType.valueOf(source.name());
		return target;
	}

	/**
	 * From DQR phase.
	 *
	 * @param source the source
	 * @return the DQR phase type
	 */
	private static DQRPhaseType fromDQRPhase(com.unidata.mdm.meta.DQRPhaseType source) {
		if (source == null) {
			return null;
		}
		DQRPhaseType target = DQRPhaseType.valueOf(source.name());
		return target;
	}

	/**
	 * From origins.
	 *
	 * @param source the source
	 * @return the DQR origins def
	 */
	private static DQROriginsDef fromOrigins(com.unidata.mdm.meta.DQROriginsDef source) {
		if (source == null) {
			return null;
		}
		DQROriginsDef target = new DQROriginsDef();
		target.setAll(source.isAll());
		target.getSourceSystem().addAll(fromSourceSystemsDQ(source.getSourceSystem()));
		return target;
	}

	/**
	 * From source systems DQ.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<DQRSourceSystemRef> fromSourceSystemsDQ(List<com.unidata.mdm.meta.DQRSourceSystemRef> source) {

		if (source == null) {
			return Collections.emptyList();
		}
		List<DQRSourceSystemRef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromSourceSystemDQ(s)));
		return target;
	}

	/**
	 * From source system DQ.
	 *
	 * @param source the source
	 * @return the DQR source system ref
	 */
	private static DQRSourceSystemRef fromSourceSystemDQ(com.unidata.mdm.meta.DQRSourceSystemRef source) {
		if (source == null) {
			return null;
		}
		DQRSourceSystemRef target = new DQRSourceSystemRef();
		target.setName(source.getName());
		return target;
	}

	/**
	 * From dqr mappings.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<DQRMappingDef> fromDqrMappings(List<com.unidata.mdm.meta.DQRMappingDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<DQRMappingDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromDqrMapping(s)));
		return target;
	}

	/**
	 * From dqr mapping.
	 *
	 * @param source the source
	 * @return the DQR mapping def
	 */
	private static DQRMappingDef fromDqrMapping(com.unidata.mdm.meta.DQRMappingDef source) {
		if (source == null) {
			return null;
		}
		DQRMappingDef target = new DQRMappingDef();
		target.setAttributeConstantValue(fromAttributeConstantValue(source.getAttributeConstantValue()));
		target.setAttributeName(source.getAttributeName());
		target.setFilterValue(source.getFilterValue());
		target.setInputPort(source.getInputPort());
		target.setOutputPort(source.getOutputPort());
		target.getDqrMapping().addAll(fromDqrMappings(source.getDqrMapping()));

		return target;
	}

	/**
	 * From attribute constant value.
	 *
	 * @param source the source
	 * @return the constant value def
	 */
	private static ConstantValueDef fromAttributeConstantValue(com.unidata.mdm.meta.ConstantValueDef source) {
		if (source == null) {
			return null;
		}
		ConstantValueDef target = new ConstantValueDef();
		target.setBoolValue(source.isBoolValue());
		target.setDateValue(fromLocalDate(source.getDateValue()));
		target.setIntValue(source.getIntValue());
		target.setNumberValue(source.getNumberValue());
		target.setStringValue(source.getStringValue());
		target.setTimestampValue(fromTimestampValue(source.getTimestampValue()));
		target.setTimeValue(fromTimeValue(source.getTimeValue()));
		target.setType(fromConstantValueType(source.getType()));

		return target;
	}

	/**
	 * From constant value type.
	 *
	 * @param source the source
	 * @return the constant value type
	 */
	private static ConstantValueType fromConstantValueType(com.unidata.mdm.meta.ConstantValueType source) {
		if (source == null) {
			return null;
		}
		ConstantValueType target = ConstantValueType.valueOf(source.name());
		return target;
	}

	/**
	 * From time value.
	 *
	 * @param source the source
	 * @return the XML gregorian calendar
	 */
	private static XMLGregorianCalendar fromTimeValue(LocalTime source) {
		if (source == null) {
			return null;
		}
		XMLGregorianCalendar target = null;
		GregorianCalendar gcal = GregorianCalendar.from(source.atDate(LocalDate.MIN).atZone(ZoneId.systemDefault()));
		try {
			target = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			// ignored
		}
		return target;
	}

	/**
	 * From timestamp value.
	 *
	 * @param source the source
	 * @return the XML gregorian calendar
	 */
	private static XMLGregorianCalendar fromTimestampValue(LocalDateTime source) {
		if (source == null) {
			return null;
		}
		XMLGregorianCalendar target = null;
		GregorianCalendar gcal = GregorianCalendar.from(source.atZone(ZoneId.systemDefault()));
		try {
			target = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			// ignored
		}
		return target;
	}

	/**
	 * From local date.
	 *
	 * @param source the source
	 * @return the XML gregorian calendar
	 */
	private static XMLGregorianCalendar fromLocalDate(LocalDate source) {
		if (source == null) {
			return null;
		}
		XMLGregorianCalendar target = null;
		GregorianCalendar gcal = GregorianCalendar.from(source.atStartOfDay(ZoneId.systemDefault()));
		try {
			target = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			// ignored
		}
		return target;
	}

	/**
	 * From enrich.
	 *
	 * @param source the source
	 * @return the DQR enrich def
	 */
	private static DQREnrichDef fromEnrich(com.unidata.mdm.meta.DQREnrichDef source) {
		if (source == null) {
			return null;
		}
		DQREnrichDef target = new DQREnrichDef();
		target.setAction(fromDQRAction(source.getAction()));
		target.setPhase(fromDQRPhase(source.getPhase()));
		target.setSourceSystem(source.getSourceSystem());

		return target;
	}

	/**
	 * From DQR action.
	 *
	 * @param source the source
	 * @return the DQR action type
	 */
	private static DQRActionType fromDQRAction(com.unidata.mdm.meta.DQRActionType source) {
		if (source == null) {
			return null;
		}
		DQRActionType target = DQRActionType.valueOf(source.name());
		return target;
	}

	/**
	 * From applicable.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<DQApplicableType> fromApplicable(List<com.unidata.mdm.meta.DQApplicableType> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<DQApplicableType> target = new ArrayList<>();
		
		source.stream().forEach(s -> target.add(fromDQApplicableType(s)));

		return target;
	}

	/**
	 * From DQ applicable type.
	 *
	 * @param source the source
	 * @return the DQ applicable type
	 */
	private static DQApplicableType fromDQApplicableType(com.unidata.mdm.meta.DQApplicableType source) {
		if (source == null) {
			return null;
		}
		DQApplicableType target = DQApplicableType.valueOf(source.name());

		return target;
	}

	/**
	 * From complex attributes.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<ComplexAttributeDef> fromComplexAttributes(
			List<com.unidata.mdm.meta.ComplexAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<ComplexAttributeDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromComplexAttributeDef(s)));
		return target;
	}

	/**
	 * From complex attribute def.
	 *
	 * @param source the source
	 * @return the complex attribute def
	 */
	private static ComplexAttributeDef fromComplexAttributeDef(com.unidata.mdm.meta.ComplexAttributeDef source) {
		if (source == null) {
			return null;
		}
		ComplexAttributeDef target = new ComplexAttributeDef();
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setHidden(source.isHidden());
		target.setMaxCount(source.getMaxCount());
		target.setMinCount(source.getMinCount());
		target.setName(source.getName());
		target.setNestedEntityName(source.getNestedEntityName());
		target.setOrder(source.getOrder());
		target.setReadOnly(source.isReadOnly());
		target.setSubEntityKeyAttribute(source.getSubEntityKeyAttribute());
		target.withCustomProperties(fromCustomProperties(source.getCustomProperties()));
		return target;
	}

	/**
	 * From classifiers.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<String> fromClassifiers(List<String> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<String> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(s));
		return target;
	}

	/**
	 * From attribute groups.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<AttributeGroupDef> fromAttributeGroups(List<com.unidata.mdm.meta.AttributeGroupDef> source) {
		if (source == null) {
			return null;
		}
		List<AttributeGroupDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromAttributeGroup(s)));
		return target;
	}

	/**
	 * From attribute group.
	 *
	 * @param source the source
	 * @return the attribute group def
	 */
	private static AttributeGroupDef fromAttributeGroup(com.unidata.mdm.meta.AttributeGroupDef source) {
		if (source == null) {
			return null;
		}
		AttributeGroupDef target = new AttributeGroupDef();
		target.setColumn(source.getColumn());
		target.setRow(source.getRow());
		target.setTitle(source.getTitle());
		return target;
	}

	/**
	 * From array attribute.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<ArrayAttributeDef> fromArrayAttribute(List<com.unidata.mdm.meta.ArrayAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<ArrayAttributeDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromArrayAttributeDef(s)));
		return target;
	}

	/**
	 * From array attribute def.
	 *
	 * @param source the source
	 * @return the array attribute def
	 */
	private static ArrayAttributeDef fromArrayAttributeDef(com.unidata.mdm.meta.ArrayAttributeDef source) {
		if (source == null) {
			return null;
		}
		ArrayAttributeDef target = new ArrayAttributeDef();
		target.setArrayValueType(fromArrayValueType(source.getArrayValueType()));
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setExchangeSeparator(source.getExchangeSeparator());
		target.setHidden(source.isHidden());
		target.setLookupEntityCodeAttributeType(fromArrayValueType(source.getLookupEntityCodeAttributeType()));
		target.setLookupEntityType(source.getLookupEntityType());
		target.setMask(source.getMask());
		target.setName(source.getName());
		target.setNullable(source.isNullable());
		target.setOrder(source.getOrder());
		target.setReadOnly(source.isReadOnly());
		target.setSearchable(source.isSearchable());
		target.withCustomProperties(fromCustomProperties(source.getCustomProperties()));
		target.withLookupEntityDisplayAttributes(source.getLookupEntityDisplayAttributes());

		return target;
	}

	/**
	 * From array value type.
	 *
	 * @param source the source
	 * @return the array value type
	 */
	private static ArrayValueType fromArrayValueType(com.unidata.mdm.meta.ArrayValueType source) {
		if (source == null) {
			return null;
		}
		ArrayValueType target = ArrayValueType.valueOf(source.name());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the lookup entity def
	 */
	public static LookupEntityDef convert(com.unidata.mdm.meta.LookupEntityDef source) {
		if (source == null) {
			return null;
		}
		LookupEntityDef target = new LookupEntityDef();
		target.setCodeAttribute(fromCodeAttributeDef(source.getCodeAttribute()));
		target.setDashboardVisible(source.isDashboardVisible());
		target.setDataQuality(fromDataQualities(source.getDataQualities()));
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setGroupName(source.getGroupName());
		target.setMergeSettings(fromMergeSettings(source.getMergeSettings()));
		target.setName(source.getName());
		target.setValidityPeriod(fromValidityPeriod(source.getValidityPeriod()));
		target.withAliasCodeAttributes(fromCodeAttributesDef(source.getAliasCodeAttributes()));
		target.withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()));
		target.withClassifiers(fromClassifiers(source.getClassifiers()));
		target.withCustomProperties(fromCustomProperties(source.getCustomProperties()));
		target.withSimpleAttribute(fromSimpleAttributes(source.getSimpleAttribute()));
		return target;
	}

	/**
	 * From code attributes def.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<CodeAttributeDef> fromCodeAttributesDef(List<com.unidata.mdm.meta.CodeAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}

		List<CodeAttributeDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromCodeAttributeDef(s)));
		return target;
	}

	/**
	 * From code attribute def.
	 *
	 * @param source the source
	 * @return the code attribute def
	 */
	private static CodeAttributeDef fromCodeAttributeDef(com.unidata.mdm.meta.CodeAttributeDef source) {
		if (source == null) {
			return null;
		}
		CodeAttributeDef target = new CodeAttributeDef();
		target.setDescription(source.getDescription());
		target.setDisplayable(source.isDisplayable());
		target.setDisplayName(source.getDisplayName());
		target.setHidden(source.isHidden());
		target.setMainDisplayable(source.isMainDisplayable());
		target.setMask(source.getMask());
		target.setName(source.getName());
		target.setNullable(source.isNullable());
		target.setReadOnly(source.isReadOnly());
		target.setSearchable(source.isSearchable());
		target.setSimpleDataType(fromSimpleDataType(source.getSimpleDataType()));
		target.withCustomProperties(fromCustomProperties(source.getCustomProperties()));
		return target;
	}

	/**
	 * From simple data type.
	 *
	 * @param source the source
	 * @return the simple data type
	 */
	private static SimpleDataType fromSimpleDataType(com.unidata.mdm.meta.SimpleDataType source) {
		if (source == null) {
			return null;
		}
		SimpleDataType target = SimpleDataType.valueOf(source.name());

		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the relation def
	 */
	public static RelationDef convert(com.unidata.mdm.meta.RelationDef source) {
		if (source == null) {
			return null;
		}
		RelationDef target = new RelationDef();
		target.setDataQuality(fromDataQualities(source.getDataQualities()));
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setFromEntity(source.getFromEntity());
		target.setMergeSettings(fromMergeSettings(source.getMergeSettings()));
		target.setName(source.getName());
		target.setRelType(fromRelType(source.getRelType()));
		target.setRequired(source.isRequired());
		target.setToEntity(source.getToEntity());
		target.setValidityPeriod(fromValidityPeriod(source.getValidityPeriod()));
		target.withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()));
		target.withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()));
		target.withClassifiers(fromClassifiers(source.getClassifiers()));
		target.withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()));
		target.withCustomProperties(fromCustomProperties(source.getCustomProperties()));

		return target;
	}

	/**
	 * From rel type.
	 *
	 * @param source the source
	 * @return the rel type
	 */
	private static RelType fromRelType(com.unidata.mdm.meta.RelType source) {
		if (source == null) {
			return null;
		}
		RelType target = RelType.valueOf(source.name());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the gets the model response
	 */
	public static GetModelResponse convert(com.unidata.mdm.meta.Model source) {
		if (source == null) {
			return null;
		}
		GetModelResponse target = new GetModelResponse();
		target.setModel(fromModel(source));

		return target;
	}

	/**
	 * From model.
	 *
	 * @param source the source
	 * @return the model
	 */
	private static Model fromModel(com.unidata.mdm.meta.Model source) {
		if (source == null) {
			return null;
		}
		Model target = new Model();
		target.setCleanseFunctions(fromCleanseFunctions(source.getCleanseFunctions()));
		target.setDefaultClassifiers(fromDefaultClassifiers(source.getDefaultClassifiers()));
		target.setEntities(fromEntities(source.getEntities()));
		target.setEntitiesGroup(fromEntitiesGroup(source.getEntitiesGroup()));
		target.setEnumerations(fromEnumertations(source.getEnumerations()));
		target.setLookupEntities(fromLookupEntities(source.getLookupEntities()));
		target.setMeasurementValues(fromMeasurementValues(source.getMeasurementValues()));
		target.setNestedEntities(fromNestedEntities(source.getNestedEntities()));
		target.setRelations(fromRelations(source.getRelations()));
		target.setSourceSystems(fromSourceSystemsConfig(source.getSourceSystems()));
		target.setStorageId(source.getStorageId());

		return target;
	}

	/**
	 * From default classifiers.
	 *
	 * @param source the source
	 * @return the list of default classifier
	 */
	private static ListOfDefaultClassifier fromDefaultClassifiers(List<com.unidata.mdm.meta.DefaultClassifier> source) {
		if (source == null) {
			return null;
		}
		ListOfDefaultClassifier target = new ListOfDefaultClassifier();
		source.stream().forEach(s -> target.getDefaultClassifier().add(fromDefaultClassifier(s)));
		return target;
	}

	/**
	 * From default classifier.
	 *
	 * @param source the source
	 * @return the default classifier
	 */
	private static DefaultClassifier fromDefaultClassifier(com.unidata.mdm.meta.DefaultClassifier source) {
		if (source == null) {
			return null;
		}
		DefaultClassifier target = new DefaultClassifier();
		target.setCodePattern(source.getCodePattern());
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setName(source.getName());
		return target;
	}

	/**
	 * From cleanse functions.
	 *
	 * @param source the source
	 * @return the list of cleanse functions
	 */
	private static ListOfCleanseFunctions fromCleanseFunctions(com.unidata.mdm.meta.ListOfCleanseFunctions source) {
		if (source == null) {
			return null;
		}
		ListOfCleanseFunctions target = new ListOfCleanseFunctions();
		target.setGroup(fromCleanseFunctionGroup(source.getGroup()));
		return target;
	}

	/**
	 * From cleanse function group.
	 *
	 * @param source the source
	 * @return the cleanse function group def
	 */
	private static CleanseFunctionGroupDef fromCleanseFunctionGroup(
			com.unidata.mdm.meta.CleanseFunctionGroupDef source) {
		if (source == null) {
			return null;
		}
		// CleanseFunctionGroupDef target = new CleanseFunctionGroupDef();
		// target.setDescription(source.getDescription());
		// target.setGroupName(source.getGroupName());
		// target.setVersion(source.getVersion());
		// target
		return null;
	}

	/**
	 * From relations.
	 *
	 * @param source the source
	 * @return the list of relations
	 */
	private static ListOfRelations fromRelations(List<com.unidata.mdm.meta.RelationDef> source) {
		if (source == null) {
			return null;
		}
		ListOfRelations target = new ListOfRelations();
		source.stream().forEach(s -> target.getRel().add(convert(s)));
		return target;
	}

	/**
	 * From nested entities.
	 *
	 * @param source the source
	 * @return the list of nested entities
	 */
	private static ListOfNestedEntities fromNestedEntities(List<com.unidata.mdm.meta.NestedEntityDef> source) {
		if (source == null) {
			return null;
		}
		ListOfNestedEntities target = new ListOfNestedEntities();
		source.stream().forEach(s -> target.getNestedEntity().add(convert(s)));
		return target;
	}

	/**
	 * From measurement values.
	 *
	 * @param source the source
	 * @return the measurement values
	 */
	private static MeasurementValues fromMeasurementValues(com.unidata.mdm.meta.MeasurementValues source) {
		if (source == null) {
			return null;
		}
		MeasurementValues target = new MeasurementValues();
		target.setId(source.getId());
		target.withValue(fromMeasurementValues(source.getValue()));
		return target;
	}

	/**
	 * From measurement values.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<MeasurementValueDef> fromMeasurementValues(
			List<com.unidata.mdm.meta.MeasurementValueDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<MeasurementValueDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromMeasurementValue(s)));
		return target;
	}

	/**
	 * From measurement value.
	 *
	 * @param source the source
	 * @return the measurement value def
	 */
	private static MeasurementValueDef fromMeasurementValue(com.unidata.mdm.meta.MeasurementValueDef source) {
		if (source == null) {
			return null;
		}
		MeasurementValueDef target = new MeasurementValueDef();
		target.setDisplayName(source.getDisplayName());
		target.setId(source.getId());
		target.setShortName(source.getShortName());
		target.withUnit(fromUnits(source.getUnit()));
		return target;
	}

	/**
	 * From units.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<MeasurementUnitDef> fromUnits(List<com.unidata.mdm.meta.MeasurementUnitDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<MeasurementUnitDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromUnit(s)));
		return target;
	}

	/**
	 * From unit.
	 *
	 * @param source the source
	 * @return the measurement unit def
	 */
	private static MeasurementUnitDef fromUnit(com.unidata.mdm.meta.MeasurementUnitDef source) {
		if (source == null) {
			return null;
		}
		MeasurementUnitDef target = new MeasurementUnitDef();
		target.setBase(source.isBase());
		target.setConvectionFunction(source.getConvectionFunction());
		target.setDisplayName(source.getDisplayName());
		target.setId(source.getId());
		target.setShortName(source.getShortName());

		return target;
	}

	/**
	 * From lookup entities.
	 *
	 * @param source the source
	 * @return the list of lookup entities
	 */
	private static ListOfLookupEntities fromLookupEntities(List<com.unidata.mdm.meta.LookupEntityDef> source) {
		if (source == null) {
			return null;
		}
		ListOfLookupEntities target = new ListOfLookupEntities();
		source.stream().forEach(s -> target.getLookupEntity().add(convert(s)));
		return target;
	}

	/**
	 * From enumertations.
	 *
	 * @param source the source
	 * @return the list of enumerations
	 */
	private static ListOfEnumerations fromEnumertations(List<com.unidata.mdm.meta.EnumerationDataType> source) {
		if (source == null) {
			return null;
		}
		ListOfEnumerations target = new ListOfEnumerations();
		source.stream().forEach(s -> target.getEnumeration().add(fromEnumertation(s)));
		return target;
	}

	/**
	 * From enumertation.
	 *
	 * @param source the source
	 * @return the enumeration data type
	 */
	private static EnumerationDataType fromEnumertation(com.unidata.mdm.meta.EnumerationDataType source) {
		if (source == null) {
			return null;
		}
		EnumerationDataType target = new EnumerationDataType();
		target.setDisplayName(source.getDisplayName());
		target.setName(source.getName());
		target.setVersion(source.getVersion());
		return target;
	}

	/**
	 * From entities group.
	 *
	 * @param source the source
	 * @return the entities group def
	 */
	private static EntitiesGroupDef fromEntitiesGroup(com.unidata.mdm.meta.EntitiesGroupDef source) {
		if (source == null) {
			return null;
		}
		EntitiesGroupDef target = new EntitiesGroupDef();
		target.setGroupName(source.getGroupName());
		target.setTitle(source.getTitle());
		target.setVersion(source.getVersion());
		target.withInnerGroups(fromInnerGroups(source.getInnerGroups()));
		return target;
	}

	/**
	 * From inner groups.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<EntitiesGroupDef> fromInnerGroups(List<com.unidata.mdm.meta.EntitiesGroupDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<EntitiesGroupDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(fromEntitiesGroup(s)));
		return target;
	}

	/**
	 * From entities.
	 *
	 * @param source the source
	 * @return the list of entities
	 */
	private static ListOfEntities fromEntities(List<com.unidata.mdm.meta.EntityDef> source) {
		if (source == null) {
			return null;
		}
		ListOfEntities target = new ListOfEntities();
		source.stream().forEach(s -> target.getEntity().add(convert(s)));
		return target;
	}

}
