package org.unidata.mdm.soap.meta.util;

import org.unidata.mdm.meta.ArrayAttributeDef;
import org.unidata.mdm.meta.ArrayValueType;
import org.unidata.mdm.meta.AttributeGroupDef;
import org.unidata.mdm.meta.AttributeMeasurementSettingsDef;
import org.unidata.mdm.meta.BVRMergeTypeDef;
import org.unidata.mdm.meta.BVTMergeTypeDef;
import org.unidata.mdm.meta.CodeAttributeDef;
import org.unidata.mdm.meta.ComplexAttributeDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.EnumerationValue;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.MeasurementUnitDef;
import org.unidata.mdm.meta.MeasurementValueDef;
import org.unidata.mdm.meta.MeasurementValues;
import org.unidata.mdm.meta.MergeAttributeDef;
import org.unidata.mdm.meta.MergeSettingsDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.PeriodBoundaryDef;
import org.unidata.mdm.meta.RelType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SimpleAttributeDef;
import org.unidata.mdm.meta.SimpleDataType;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.v1.ListOfEnumerations;
import org.unidata.mdm.meta.v1.ListOfLookupEntities;
import org.unidata.mdm.meta.v1.ListOfNestedEntities;
import org.unidata.mdm.meta.v1.ListOfRelations;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The Class ToInternal.
 */
public final class ToInternal {
	private ToInternal() { }

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the entity def
	 */
	public static EntityDef convert(org.unidata.mdm.meta.v1.EntityDef source) {
		if (source == null) {
			return null;
		}
		return new EntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
				.withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
				.withCustomProperties(fromCustomProperties(source.getCustomProperties()))
				.withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
				.withDashboardVisible(source.isDashboardVisible())
				.withDisplayName(source.getDisplayName()).withGroupName(source.getGroupName())
				.withMergeSettings(fromMergeSettings(source.getMergeSettings())).withName(source.getName())
				.withSimpleAttribute(fromSimpleAttributes(source.getSimpleAttribute()))
				.withValidityPeriod(fromValidityPeriod(source.getValidityPeriod())).withVersion(source.getVersion());
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the nested entity def
	 */
	public static NestedEntityDef convert(org.unidata.mdm.meta.v1.NestedEntityDef source) {
		if (source == null) {
			return null;
		}
		return new NestedEntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
				.withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
				.withCustomProperties(fromCustomProperties(source.getCustomProperties()))
				.withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
				.withDisplayName(source.getDisplayName())
				.withMergeSettings(fromMergeSettings(source.getMergeSettings())).withName(source.getName())
				.withSimpleAttribute(fromSimpleAttributes(source.getSimpleAttribute()))
				.withValidityPeriod(fromValidityPeriod(source.getValidityPeriod())).withVersion(source.getVersion());
	}
	/**
	 * From validity period.
	 *
	 * @param source the source
	 * @return the period boundary def
	 */
	public static PeriodBoundaryDef fromValidityPeriod(org.unidata.mdm.meta.v1.PeriodBoundaryDef source) {
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
			List<org.unidata.mdm.meta.v1.SimpleAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}

		List<SimpleAttributeDef> target = new ArrayList<>();
		for (org.unidata.mdm.meta.v1.SimpleAttributeDef s : source) {
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
	public static SimpleAttributeDef fromSimpleAttribute(org.unidata.mdm.meta.v1.SimpleAttributeDef source) {
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
			org.unidata.mdm.meta.v1.AttributeMeasurementSettingsDef source) {
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
	public static MergeSettingsDef fromMergeSettings(org.unidata.mdm.meta.v1.MergeSettingsDef source) {
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
	public static BVTMergeTypeDef fromBvtSettings(org.unidata.mdm.meta.v1.BVTMergeTypeDef source) {
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
	public static List<MergeAttributeDef> fromMergeAttributes(List<org.unidata.mdm.meta.v1.MergeAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<MergeAttributeDef> target = new ArrayList<>();
		for (org.unidata.mdm.meta.v1.MergeAttributeDef s : source) {
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
	public static MergeAttributeDef fromMergeAttribute(org.unidata.mdm.meta.v1.MergeAttributeDef source) {
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
	public static List<SourceSystemDef> fromSourceSystemsConfig(org.unidata.mdm.meta.v1.ListOfSourceSystems source) {
		List<SourceSystemDef> target = new ArrayList<>();
		if (source == null) {
			return target;
		}

		for (org.unidata.mdm.meta.v1.SourceSystemDef s : source.getSourceSystem()) {
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
	public static SourceSystemDef fromSourceSystem(org.unidata.mdm.meta.v1.SourceSystemDef source) {
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
			List<org.unidata.mdm.meta.v1.CustomPropertyDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<CustomPropertyDef> target = new ArrayList<>();
		source.forEach(s -> target.add(fromCustomProperty(s)));
		return target;
	}

	/**
	 * From custom property.
	 *
	 * @param source the source
	 * @return the custom property def
	 */
	public static CustomPropertyDef fromCustomProperty(org.unidata.mdm.meta.v1.CustomPropertyDef source) {
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
	public static BVRMergeTypeDef fromBvrSettings(org.unidata.mdm.meta.v1.BVRMergeTypeDef source) {
		if (source == null) {
			return null;
		}
		BVRMergeTypeDef target = new BVRMergeTypeDef();
		target.setSourceSystemsConfigs(fromSourceSystemsConfig(source.getSourceSystemsConfig()));

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
		return LocalTime.of(source.getHour(), source.getMinute(), source.getSecond(),
				source.getMillisecond() * 1000 * 1000);
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
		return LocalDateTime
				.from(source.toGregorianCalendar().toZonedDateTime());
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
		return LocalDate.of(source.getYear(), source.getMonth(), source.getDay());
	}

	/**
	 * From complex attributes.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<ComplexAttributeDef> fromComplexAttributes(
			List<org.unidata.mdm.meta.v1.ComplexAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<ComplexAttributeDef> target = new ArrayList<>();
		source.forEach(s -> target.add(fromComplexAttributeDef(s)));
		return target;
	}

	/**
	 * From complex attribute def.
	 *
	 * @param source the source
	 * @return the complex attribute def
	 */
	public static ComplexAttributeDef fromComplexAttributeDef(org.unidata.mdm.meta.v1.ComplexAttributeDef source) {
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
		return new ArrayList<>(source);
	}

	/**
	 * From attribute groups.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<AttributeGroupDef> fromAttributeGroups(List<org.unidata.mdm.meta.v1.AttributeGroupDef> source) {
		if (source == null) {
			return null;
		}
		List<AttributeGroupDef> target = new ArrayList<>();
		source.forEach(s -> target.add(fromAttributeGroup(s)));
		return target;
	}

	/**
	 * From attribute group.
	 *
	 * @param source the source
	 * @return the attribute group def
	 */
	public static AttributeGroupDef fromAttributeGroup(org.unidata.mdm.meta.v1.AttributeGroupDef source) {
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
	public static List<ArrayAttributeDef> fromArrayAttribute(List<org.unidata.mdm.meta.v1.ArrayAttributeDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<ArrayAttributeDef> target = new ArrayList<>();
		source.forEach(s -> target.add(fromArrayAttributeDef(s)));
		return target;
	}

	/**
	 * From array attribute def.
	 *
	 * @param source the source
	 * @return the array attribute def
	 */
	public static ArrayAttributeDef fromArrayAttributeDef(org.unidata.mdm.meta.v1.ArrayAttributeDef source) {
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
		target.setDisplayable(source.isDisplayable());
		target.setMainDisplayable(source.isMainDisplayable());

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
	public static ArrayValueType fromArrayValueType(org.unidata.mdm.meta.v1.ArrayValueType source) {
		if (source == null) {
			return null;
		}
		return ArrayValueType.valueOf(source.name());
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the lookup entity def
	 */
	public static LookupEntityDef convert(org.unidata.mdm.meta.v1.LookupEntityDef source) {
		if (source == null) {
			return null;
		}
		LookupEntityDef target = new LookupEntityDef();
		target.setCodeAttribute(fromCodeAttributeDef(source.getCodeAttribute()));
		target.setDashboardVisible(source.isDashboardVisible());
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setGroupName(source.getGroupName());
		target.setMergeSettings(fromMergeSettings(source.getMergeSettings()));
		target.setName(source.getName());
		target.setValidityPeriod(fromValidityPeriod(source.getValidityPeriod()));
		target.withAliasCodeAttributes(fromCodeAttributesDef(source.getAliasCodeAttributes()));
		target.withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()));
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
	public static List<CodeAttributeDef> fromCodeAttributesDef(List<org.unidata.mdm.meta.v1.CodeAttributeDef> source) {
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
	public static CodeAttributeDef fromCodeAttributeDef(org.unidata.mdm.meta.v1.CodeAttributeDef source) {
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
	public static SimpleDataType fromSimpleDataType(org.unidata.mdm.meta.v1.SimpleDataType source) {
		if (source == null) {
			return null;
		}

		return SimpleDataType.valueOf(source.name());
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the relation def
	 */
	public static RelationDef convert(org.unidata.mdm.meta.v1.RelationDef source) {
		if (source == null) {
			return null;
		}
		RelationDef target = new RelationDef();
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
	public static EnumerationDataType convert(org.unidata.mdm.meta.v1.EnumerationDataType source) {
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
	public static RelType fromRelType(org.unidata.mdm.meta.v1.RelType source) {
		if (source == null) {
			return null;
		}
		return RelType.valueOf(source.name());
	}

	/**
	 * From model.
	 *
	 * @param source the source
	 * @return the model
	 */
	public static Model fromModel(org.unidata.mdm.meta.v1.Model source) {
		if (source == null) {
			return null;
		}
		Model target = new Model();
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
	 * From relations.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<RelationDef> fromRelations(ListOfRelations source) {
		if (source == null) {
			return null;
		}
		List<RelationDef> target = new ArrayList<>();
		source.getRel().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * From nested entities.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<NestedEntityDef> fromNestedEntities(ListOfNestedEntities source) {
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
	public static MeasurementValues fromMeasurementValues(org.unidata.mdm.meta.v1.MeasurementValues source) {
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
			List<org.unidata.mdm.meta.v1.MeasurementValueDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<MeasurementValueDef> target = new ArrayList<>();
		source.forEach(s -> target.add(fromMeasurementValue(s)));
		return target;
	}

	/**
	 * From measurement value.
	 *
	 * @param source the source
	 * @return the measurement value def
	 */
	public static MeasurementValueDef fromMeasurementValue(org.unidata.mdm.meta.v1.MeasurementValueDef source) {
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
	public static List<MeasurementUnitDef> fromUnits(List<org.unidata.mdm.meta.v1.MeasurementUnitDef> source) {
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
	public static MeasurementUnitDef fromUnit(org.unidata.mdm.meta.v1.MeasurementUnitDef source) {
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
		source.getLookupEntity().forEach(s -> target.add(convert(s)));
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
		source.getEnumeration().forEach(s -> target.add(fromEnumertation(s)));
		return target;
	}

	/**
	 * From enumertation.
	 *
	 * @param source the source
	 * @return the enumeration data type
	 */
	public static EnumerationDataType fromEnumertation(org.unidata.mdm.meta.v1.EnumerationDataType source) {
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
	public static EntitiesGroupDef fromEntitiesGroup(org.unidata.mdm.meta.v1.EntitiesGroupDef source) {
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
	public static List<EntitiesGroupDef> fromInnerGroups(List<org.unidata.mdm.meta.v1.EntitiesGroupDef> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<EntitiesGroupDef> target = new ArrayList<>();
		source.forEach(s -> target.add(fromEntitiesGroup(s)));
		return target;
	}

	/**
	 * From entities.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<EntityDef> fromEntities(org.unidata.mdm.meta.v1.ListOfEntities source) {
		if (source == null) {
			return null;
		}
		List<EntityDef>  target = new ArrayList<>();
		source.getEntity().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * From enumeration values.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<EnumerationValue> fromEnumerationValues(
			List<org.unidata.mdm.meta.v1.EnumerationValue> source) {
		if (source == null) {
			return Collections.emptyList();
		}
		List<EnumerationValue> target = new ArrayList<>();
		source.forEach(s ->
				target.add(new EnumerationValue().withName(s.getName()).withDisplayName(s.getDisplayName())));
		return target;
	}
}
