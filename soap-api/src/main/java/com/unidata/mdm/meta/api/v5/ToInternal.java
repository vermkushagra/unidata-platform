package com.unidata.mdm.meta.api.v5;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.unidata.mdm.backend.common.types.impl.ConstantValueDefImpl;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.AttributeGroupDef;
import com.unidata.mdm.meta.AttributeMeasurementSettingsDef;
import com.unidata.mdm.meta.BVRMergeTypeDef;
import com.unidata.mdm.meta.BVTMergeTypeDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.ConstantValueType;
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
import com.unidata.mdm.meta.DQRuleRunType;
import com.unidata.mdm.meta.DQRuleType;
import com.unidata.mdm.meta.DefaultClassifier;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.EnumerationValue;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.MeasurementUnitDef;
import com.unidata.mdm.meta.MeasurementValueDef;
import com.unidata.mdm.meta.MeasurementValues;
import com.unidata.mdm.meta.MergeAttributeDef;
import com.unidata.mdm.meta.MergeSettingsDef;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.PeriodBoundaryDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SeverityType;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;
import com.unidata.mdm.meta.SourceSystemDef;
import com.unidata.mdm.meta.v5.EntityDataQualityDef;
import com.unidata.mdm.meta.v5.ListOfEnumerations;
import com.unidata.mdm.meta.v5.ListOfLookupEntities;
import com.unidata.mdm.meta.v5.ListOfNestedEntities;
import com.unidata.mdm.meta.v5.ListOfRelations;


/**
 * The Class ToInternal.
 */
public class ToInternal {

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the entity def
	 */
	public static EntityDef convert(com.unidata.mdm.meta.v5.EntityDef source) {
		if (source == null) {
			return null;
		}
		EntityDef target = new EntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
				.withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
				.withClassifiers(fromClassifiers(source.getClassifiers()))
				.withCustomProperties(fromCustomProperties(source.getCustomProperties()))
				.withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
				.withDashboardVisible(source.isDashboardVisible())
				.withDataQualities(fromDataQualities(source.getDataQuality())).withDescription(source.getDescription())
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
	public static NestedEntityDef convert(com.unidata.mdm.meta.v5.NestedEntityDef source) {
		if (source == null) {
			return null;
		}
		NestedEntityDef target = new NestedEntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
				.withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
				.withCustomProperties(fromCustomProperties(source.getCustomProperties()))
				.withClassifiers(fromClassifiers(source.getClassifiers()))
				.withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
				.withDataQualities(fromDataQualities(source.getDataQuality())).withDescription(source.getDescription())
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
	public static PeriodBoundaryDef fromValidityPeriod(com.unidata.mdm.meta.v5.PeriodBoundaryDef source) {
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
	public static List<SimpleAttributeDef> fromSimpleAttributes(
			List<com.unidata.mdm.meta.v5.SimpleAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}

		List<SimpleAttributeDef> target = new ArrayList<>();
		for (com.unidata.mdm.meta.v5.SimpleAttributeDef s : source) {
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
	public static SimpleAttributeDef fromSimpleAttribute(com.unidata.mdm.meta.v5.SimpleAttributeDef source) {
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
	public static AttributeMeasurementSettingsDef fromMeasureSettings(
			com.unidata.mdm.meta.v5.AttributeMeasurementSettingsDef source) {
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
	public static MergeSettingsDef fromMergeSettings(com.unidata.mdm.meta.v5.MergeSettingsDef source) {
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
	public static BVTMergeTypeDef fromBvtSettings(com.unidata.mdm.meta.v5.BVTMergeTypeDef source) {
		if (source == null) {
			return null;
		}
		BVTMergeTypeDef target = new BVTMergeTypeDef();
		target.withAttributes(fromMergeAttributes(source.getAttribute()));
		return target;
	}

	/**
	 * From merge attributes.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<MergeAttributeDef> fromMergeAttributes(List<com.unidata.mdm.meta.v5.MergeAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<MergeAttributeDef> target = new ArrayList<>();
		for (com.unidata.mdm.meta.v5.MergeAttributeDef s : source) {
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
	public static MergeAttributeDef fromMergeAttribute(com.unidata.mdm.meta.v5.MergeAttributeDef source) {
		if (source == null) {
			return null;
		}
		MergeAttributeDef target = new MergeAttributeDef();
		target.setName(source.getName());
		target.setSourceSystemsConfigs(fromSourceSystemsConfig(source.getSourceSystemsConfig()));
		return target;
	}

	/**
	 * From source systems config.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<SourceSystemDef> fromSourceSystemsConfig(com.unidata.mdm.meta.v5.ListOfSourceSystems source) {
		List<SourceSystemDef> target = new ArrayList<>();
		if (source == null) {
			return target;
		}

		for (com.unidata.mdm.meta.v5.SourceSystemDef s : source.getSourceSystem()) {
			target.add(fromSourceSystem(s));
		}

		return target;
	}

	/**
	 * From source system.
	 *
	 * @param source the source
	 * @return the source system def
	 */
	public static SourceSystemDef fromSourceSystem(com.unidata.mdm.meta.v5.SourceSystemDef source) {
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
	public static List<CustomPropertyDef> fromCustomProperties(
			List<com.unidata.mdm.meta.v5.CustomPropertyDef> source) {
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
	public static CustomPropertyDef fromCustomProperty(com.unidata.mdm.meta.v5.CustomPropertyDef source) {
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
	public static BVRMergeTypeDef fromBvrSettings(com.unidata.mdm.meta.v5.BVRMergeTypeDef source) {
		if (source == null) {
			return null;
		}
		BVRMergeTypeDef target = new BVRMergeTypeDef();
		target.setSourceSystemsConfigs(fromSourceSystemsConfig(source.getSourceSystemsConfig()));

		return target;
	}

	/**
	 * From data qualities.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<com.unidata.mdm.meta.DQRuleDef> fromDataQualities(EntityDataQualityDef source) {
		List<com.unidata.mdm.meta.DQRuleDef> target = new ArrayList<>();
		if (source == null) {
			return target;
		}

		source.getDqRule().stream().forEach(s -> target.add(fromDataQuality(s)));
		return target;
	}

	/**
	 * From data quality.
	 *
	 * @param source the source
	 * @return the DQ rule def
	 */
	public static DQRuleDef fromDataQuality(com.unidata.mdm.meta.v5.DQRuleDef source) {
		if (source == null) {
			return null;
		}
		DQRuleDef target = new DQRuleDef();
		target.getApplicable().addAll(fromApplicable(source.getApplicable()));
		target.setCleanseFunctionName(source.getCleanseFunctionName());
		// UN-7293
		// target.setComplexAttributeName(source.getComplexAttributeName());
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
		target.setRunType(source.getRunType() == null ? null : DQRuleRunType.valueOf(source.getRunType().name()));
		target.withCustomProperties(fromCustomProperties(source.getCustomProperties()));

		return target;
	}

	/**
	 * From DQ types.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<DQRuleType> fromDQTypes(List<com.unidata.mdm.meta.v5.DQRuleType> source) {
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
	public static DQRuleType fromDQType(com.unidata.mdm.meta.v5.DQRuleType source) {
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
	public static DQRuleClass fromRClass(com.unidata.mdm.meta.v5.DQRuleClass source) {
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
	public static DQRRaiseDef fromRaise(com.unidata.mdm.meta.v5.DQRRaiseDef source) {
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
	public static SeverityType fromSeverityType(com.unidata.mdm.meta.v5.SeverityType source) {
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
	public static DQRPhaseType fromDQRPhase(com.unidata.mdm.meta.v5.DQRPhaseType source) {
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
	public static DQROriginsDef fromOrigins(com.unidata.mdm.meta.v5.DQROriginsDef source) {
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
	public static List<DQRSourceSystemRef> fromSourceSystemsDQ(
			List<com.unidata.mdm.meta.v5.DQRSourceSystemRef> source) {

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
	public static DQRSourceSystemRef fromSourceSystemDQ(com.unidata.mdm.meta.v5.DQRSourceSystemRef source) {
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
	public static List<DQRMappingDef> fromDqrMappings(List<com.unidata.mdm.meta.v5.DQRMappingDef> source) {
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
	public static DQRMappingDef fromDqrMapping(com.unidata.mdm.meta.v5.DQRMappingDef source) {
		if (source == null) {
			return null;
		}
		DQRMappingDef target = new DQRMappingDef();
		target.setAttributeConstantValue(fromAttributeConstantValue(source.getAttributeConstantValue()));
		target.setAttributeName(source.getAttributeName());
		// UN-7293
		// target.setFilterValue(source.getFilterValue());
		// target.getDqrMapping().addAll(fromDqrMappings(source.getDqrMapping()));
		target.setInputPort(source.getInputPort());
		target.setOutputPort(source.getOutputPort());

		return target;
	}

	/**
	 * From attribute constant value.
	 *
	 * @param source the source
	 * @return the constant value def
	 */
	public static ConstantValueDef fromAttributeConstantValue(com.unidata.mdm.meta.v5.ConstantValueDef source) {
		if (source == null) {
			return null;
		}
		ConstantValueDef target = new ConstantValueDefImpl();
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
	public static ConstantValueType fromConstantValueType(com.unidata.mdm.meta.v5.ConstantValueType source) {
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
	 * @return the local time
	 */
	public static LocalTime fromTimeValue(XMLGregorianCalendar source) {
		if (source == null) {
			return null;
		}
		LocalTime target = LocalTime.of(source.getHour(), source.getMinute(), source.getSecond(),
				source.getMillisecond() * 1000 * 1000);
		return target;
	}

	/**
	 * From timestamp value.
	 *
	 * @param source the source
	 * @return the local date time
	 */
	public static LocalDateTime fromTimestampValue(XMLGregorianCalendar source) {
		if (source == null) {
			return null;
		}
		LocalDateTime target = LocalDateTime
				.from(source.toGregorianCalendar().toZonedDateTime());
		return target;
	}

	/**
	 * From local date.
	 *
	 * @param source the source
	 * @return the local date
	 */
	public static LocalDate fromLocalDate(XMLGregorianCalendar source) {
		if (source == null) {
			return null;
		}
		LocalDate target = LocalDate.of(source.getYear(), source.getMonth(), source.getDay());
		return target;
	}

	/**
	 * From enrich.
	 *
	 * @param source the source
	 * @return the DQR enrich def
	 */
	public static DQREnrichDef fromEnrich(com.unidata.mdm.meta.v5.DQREnrichDef source) {
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
	public static DQRActionType fromDQRAction(com.unidata.mdm.meta.v5.DQRActionType source) {
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
	public static List<DQApplicableType> fromApplicable(List<com.unidata.mdm.meta.v5.DQApplicableType> source) {
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
	public static DQApplicableType fromDQApplicableType(com.unidata.mdm.meta.v5.DQApplicableType source) {
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
	public static List<ComplexAttributeDef> fromComplexAttributes(
			List<com.unidata.mdm.meta.v5.ComplexAttributeDef> source) {
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
	public static ComplexAttributeDef fromComplexAttributeDef(com.unidata.mdm.meta.v5.ComplexAttributeDef source) {
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
	public static List<String> fromClassifiers(List<String> source) {
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
	public static List<AttributeGroupDef> fromAttributeGroups(List<com.unidata.mdm.meta.v5.AttributeGroupDef> source) {
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
	public static AttributeGroupDef fromAttributeGroup(com.unidata.mdm.meta.v5.AttributeGroupDef source) {
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
	public static List<ArrayAttributeDef> fromArrayAttribute(List<com.unidata.mdm.meta.v5.ArrayAttributeDef> source) {
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
	public static ArrayAttributeDef fromArrayAttributeDef(com.unidata.mdm.meta.v5.ArrayAttributeDef source) {
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
	public static ArrayValueType fromArrayValueType(com.unidata.mdm.meta.v5.ArrayValueType source) {
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
	public static LookupEntityDef convert(com.unidata.mdm.meta.v5.LookupEntityDef source) {
		if (source == null) {
			return null;
		}
		LookupEntityDef target = new LookupEntityDef();
		target.setCodeAttribute(fromCodeAttributeDef(source.getCodeAttribute()));
		target.setDashboardVisible(source.isDashboardVisible());
		target.setDataQualities(fromDataQualities(source.getDataQuality()));
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
	public static List<CodeAttributeDef> fromCodeAttributesDef(List<com.unidata.mdm.meta.v5.CodeAttributeDef> source) {
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
	public static CodeAttributeDef fromCodeAttributeDef(com.unidata.mdm.meta.v5.CodeAttributeDef source) {
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
	public static SimpleDataType fromSimpleDataType(com.unidata.mdm.meta.v5.SimpleDataType source) {
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
	public static RelationDef convert(com.unidata.mdm.meta.v5.RelationDef source) {
		if (source == null) {
			return null;
		}
		RelationDef target = new RelationDef();
		target.setDataQualities(fromDataQualities(source.getDataQuality()));
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
	 * Convert.
	 *
	 * @param source the source
	 * @return the enumeration data type
	 */
	public static EnumerationDataType convert(com.unidata.mdm.meta.v5.EnumerationDataType source) {
		if (source == null) {
			return null;
		}
		EnumerationDataType target = new EnumerationDataType();
		target.withName(source.getName());
		target.withDisplayName(source.getDisplayName());
		target.withVersion(source.getVersion());
		target.withEnumVal(fromEnumerationValues(source.getEnumVal()));
		return target;
	}

	/**
	 * From rel type.
	 *
	 * @param source the source
	 * @return the rel type
	 */
	public static RelType fromRelType(com.unidata.mdm.meta.v5.RelType source) {
		if (source == null) {
			return null;
		}
		RelType target = RelType.valueOf(source.name());
		return target;
	}

	/**
	 * From model.
	 *
	 * @param source the source
	 * @return the model
	 */
	public static Model fromModel(com.unidata.mdm.meta.v5.Model source) {
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
	 * @return the list
	 */
	public static List<DefaultClassifier> fromDefaultClassifiers(
			com.unidata.mdm.meta.v5.ListOfDefaultClassifier source) {
		if (source == null) {
			return null;
		}
		List<DefaultClassifier> target = new ArrayList<>();
		source.getDefaultClassifier().forEach(s -> target.add(fromDefaultClassifier(s)));
		return target;
	}

	/**
	 * From default classifier.
	 *
	 * @param source the source
	 * @return the default classifier
	 */
	public static DefaultClassifier fromDefaultClassifier(com.unidata.mdm.meta.v5.DefaultClassifier source) {
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
	 * @return the com.unidata.mdm.meta. list of cleanse functions
	 */
	public static com.unidata.mdm.meta.ListOfCleanseFunctions fromCleanseFunctions(
			com.unidata.mdm.meta.v5.ListOfCleanseFunctions source) {
		com.unidata.mdm.meta.ListOfCleanseFunctions target = new ListOfCleanseFunctions();
		if (source == null) {
			return target;
		}
		target.setGroup(fromCleanseFunctionGroup(source.getGroup()));
		return target;
	}

	/**
	 * From cleanse function group.
	 *
	 * @param cleanseFunctionGroupDef the cleanse function group def
	 * @return the cleanse function group def
	 */
	public static CleanseFunctionGroupDef fromCleanseFunctionGroup(
			com.unidata.mdm.meta.v5.CleanseFunctionGroupDef cleanseFunctionGroupDef) {
		if (cleanseFunctionGroupDef == null) {
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
	 * @return the list
	 */
	public static List<com.unidata.mdm.meta.RelationDef> fromRelations(ListOfRelations source) {
		if (source == null) {
			return null;
		}
		List<com.unidata.mdm.meta.RelationDef> target = new ArrayList<>();
		source.getRel().stream().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * From nested entities.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<com.unidata.mdm.meta.NestedEntityDef> fromNestedEntities(ListOfNestedEntities  source) {
		if (source == null) {
			return null;
		}
		return null;
	}

	/**
	 * From measurement values.
	 *
	 * @param source the source
	 * @return the measurement values
	 */
	public static MeasurementValues fromMeasurementValues(com.unidata.mdm.meta.v5.MeasurementValues source) {
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
	public static List<MeasurementValueDef> fromMeasurementValues(
			List<com.unidata.mdm.meta.v5.MeasurementValueDef> source) {
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
	public static MeasurementValueDef fromMeasurementValue(com.unidata.mdm.meta.v5.MeasurementValueDef source) {
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
	public static List<MeasurementUnitDef> fromUnits(List<com.unidata.mdm.meta.v5.MeasurementUnitDef> source) {
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
	public static MeasurementUnitDef fromUnit(com.unidata.mdm.meta.v5.MeasurementUnitDef source) {
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
	 * @return the list
	 */
	public static List<LookupEntityDef> fromLookupEntities(ListOfLookupEntities source) {
		if (source == null) {
			return null;
		}
		List<LookupEntityDef> target = new ArrayList<>();
		source.getLookupEntity().stream().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * From enumertations.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<EnumerationDataType> fromEnumertations(ListOfEnumerations source) {
		if (source == null) {
			return null;
		}
		List<EnumerationDataType> target = new ArrayList<>();
		source.getEnumeration().stream().forEach(s -> target.add(fromEnumertation(s)));
		return target;
	}

	/**
	 * From enumertation.
	 *
	 * @param source the source
	 * @return the enumeration data type
	 */
	public static EnumerationDataType fromEnumertation(com.unidata.mdm.meta.v5.EnumerationDataType source) {
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
	public static EntitiesGroupDef fromEntitiesGroup(com.unidata.mdm.meta.v5.EntitiesGroupDef source) {
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
	public static List<EntitiesGroupDef> fromInnerGroups(List<com.unidata.mdm.meta.v5.EntitiesGroupDef> source) {
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
	 * @return the list
	 */
	public static List<EntityDef> fromEntities(com.unidata.mdm.meta.v5.ListOfEntities source) {
		if (source == null) {
			return null;
		}
		List<EntityDef>  target = new ArrayList<>();
		source.getEntity().stream().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * From enumeration values.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<EnumerationValue> fromEnumerationValues(
			List<com.unidata.mdm.meta.v5.EnumerationValue> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<EnumerationValue> target = new ArrayList<>();
		source.stream().forEach(s ->
				target.add(new EnumerationValue().withName(s.getName()).withDisplayName(s.getDisplayName())));
		return target;
	}
}
