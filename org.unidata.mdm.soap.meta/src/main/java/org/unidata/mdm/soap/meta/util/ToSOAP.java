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

package org.unidata.mdm.soap.meta.util;

import org.unidata.mdm.meta.v1.ArrayAttributeDef;
import org.unidata.mdm.meta.v1.ArrayValueType;
import org.unidata.mdm.meta.v1.AttributeGroupDef;
import org.unidata.mdm.meta.v1.AttributeMeasurementSettingsDef;
import org.unidata.mdm.meta.v1.BVRMergeTypeDef;
import org.unidata.mdm.meta.v1.BVTMergeTypeDef;
import org.unidata.mdm.meta.v1.CodeAttributeDef;
import org.unidata.mdm.meta.v1.ComplexAttributeDef;
import org.unidata.mdm.meta.v1.CustomPropertyDef;
import org.unidata.mdm.meta.v1.EntitiesGroupDef;
import org.unidata.mdm.meta.v1.EntityDef;
import org.unidata.mdm.meta.v1.EnumerationDataType;
import org.unidata.mdm.meta.v1.GetModelResponse;
import org.unidata.mdm.meta.v1.ListOfEntities;
import org.unidata.mdm.meta.v1.ListOfEnumerations;
import org.unidata.mdm.meta.v1.ListOfLookupEntities;
import org.unidata.mdm.meta.v1.ListOfNestedEntities;
import org.unidata.mdm.meta.v1.ListOfRelations;
import org.unidata.mdm.meta.v1.ListOfSourceSystems;
import org.unidata.mdm.meta.v1.LookupEntityDef;
import org.unidata.mdm.meta.v1.MeasurementUnitDef;
import org.unidata.mdm.meta.v1.MeasurementValueDef;
import org.unidata.mdm.meta.v1.MeasurementValues;
import org.unidata.mdm.meta.v1.MergeAttributeDef;
import org.unidata.mdm.meta.v1.MergeSettingsDef;
import org.unidata.mdm.meta.v1.Model;
import org.unidata.mdm.meta.v1.NestedEntityDef;
import org.unidata.mdm.meta.v1.PeriodBoundaryDef;
import org.unidata.mdm.meta.v1.RelType;
import org.unidata.mdm.meta.v1.RelationDef;
import org.unidata.mdm.meta.v1.SimpleAttributeDef;
import org.unidata.mdm.meta.v1.SimpleDataType;
import org.unidata.mdm.meta.v1.SourceSystemDef;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    public static EntityDef convert(org.unidata.mdm.meta.EntityDef source) {
        MeasurementPoint.start();
        try {
            if (source == null) {
                return null;
            }
            return new EntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
                    .withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
                    .withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
                    .withCustomProperties(fromCustomProperties(source.getCustomProperties()))
                    .withDashboardVisible(source.isDashboardVisible())
                    .withDisplayName(source.getDisplayName()).withGroupName(source.getGroupName())
                    .withMergeSettings(fromMergeSettings(source.getMergeSettings())).withName(source.getName())
                    .withSimpleAttribute(fromSimpleAttributes(source.getSimpleAttribute()))
                    .withValidityPeriod(fromValidityPeriod(source.getValidityPeriod()))
                    .withVersion(source.getVersion());
        } finally {
            MeasurementPoint.stop();
        }
    }

	/**
     * Convert.
     *
     * @param source the source
     * @return the nested entity def
     */
    public static NestedEntityDef convert(org.unidata.mdm.meta.NestedEntityDef source) {

        MeasurementPoint.start();
        try {

            if (source == null) {
                return null;
            }

            return new NestedEntityDef().withArrayAttribute(fromArrayAttribute(source.getArrayAttribute()))
                    .withAttributeGroups(fromAttributeGroups(source.getAttributeGroups()))
                    .withComplexAttribute(fromComplexAttributes(source.getComplexAttribute()))
                    .withCustomProperties(fromCustomProperties(source.getCustomProperties()))
                    .withDisplayName(source.getDisplayName())
                    .withMergeSettings(fromMergeSettings(source.getMergeSettings())).withName(source.getName())
                    .withSimpleAttribute(fromSimpleAttributes(source.getSimpleAttribute()))
                    .withValidityPeriod(fromValidityPeriod(source.getValidityPeriod())).withVersion(source.getVersion());
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * From validity period.
     *
     * @param source the source
     * @return the period boundary def
     */
    private static PeriodBoundaryDef fromValidityPeriod(org.unidata.mdm.meta.PeriodBoundaryDef source) {
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
    private static List<SimpleAttributeDef> fromSimpleAttributes(List<org.unidata.mdm.meta.SimpleAttributeDef> source) {
        if (source == null) {
            return Collections.emptyList();
        }

        List<SimpleAttributeDef> target = new ArrayList<>();
        for (org.unidata.mdm.meta.SimpleAttributeDef s : source) {
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
    private static SimpleAttributeDef fromSimpleAttribute(org.unidata.mdm.meta.SimpleAttributeDef source) {
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
            org.unidata.mdm.meta.AttributeMeasurementSettingsDef source
    ) {
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
    private static MergeSettingsDef fromMergeSettings(org.unidata.mdm.meta.MergeSettingsDef source) {
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
    private static BVTMergeTypeDef fromBvtSettings(org.unidata.mdm.meta.BVTMergeTypeDef source) {
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
    private static List<MergeAttributeDef> fromMergeAttributes(List<org.unidata.mdm.meta.MergeAttributeDef> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        List<MergeAttributeDef> target = new ArrayList<>();
        for (org.unidata.mdm.meta.MergeAttributeDef s : source) {
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
    private static MergeAttributeDef fromMergeAttribute(org.unidata.mdm.meta.MergeAttributeDef source) {
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
    private static ListOfSourceSystems fromSourceSystemsConfig(List<org.unidata.mdm.meta.SourceSystemDef> source) {
        ListOfSourceSystems target = new ListOfSourceSystems();
        if (source == null) {
            return target;
        }

        for (org.unidata.mdm.meta.SourceSystemDef s : source) {
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
    private static SourceSystemDef fromSourceSystem(org.unidata.mdm.meta.SourceSystemDef source) {
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
    private static List<CustomPropertyDef> fromCustomProperties(List<org.unidata.mdm.meta.CustomPropertyDef> source) {
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
    private static CustomPropertyDef fromCustomProperty(org.unidata.mdm.meta.CustomPropertyDef source) {
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
    private static BVRMergeTypeDef fromBvrSettings(org.unidata.mdm.meta.BVRMergeTypeDef source) {
        if (source == null) {
            return null;
        }
        BVRMergeTypeDef target = new BVRMergeTypeDef();
        target.setSourceSystemsConfig(fromSourceSystemsConfig(source.getSourceSystemsConfigs()));

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
     * From complex attributes.
     *
     * @param source the source
     * @return the list
     */
    private static List<ComplexAttributeDef> fromComplexAttributes(
            List<org.unidata.mdm.meta.ComplexAttributeDef> source) {
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
    private static ComplexAttributeDef fromComplexAttributeDef(org.unidata.mdm.meta.ComplexAttributeDef source) {
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
        return new ArrayList<>(source);
    }

    /**
     * From attribute groups.
     *
     * @param source the source
     * @return the list
     */
    private static List<AttributeGroupDef> fromAttributeGroups(List<org.unidata.mdm.meta.AttributeGroupDef> source) {
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
    private static AttributeGroupDef fromAttributeGroup(org.unidata.mdm.meta.AttributeGroupDef source) {
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
    private static List<ArrayAttributeDef> fromArrayAttribute(List<org.unidata.mdm.meta.ArrayAttributeDef> source) {
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
    private static ArrayAttributeDef fromArrayAttributeDef(org.unidata.mdm.meta.ArrayAttributeDef source) {
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
    private static ArrayValueType fromArrayValueType(org.unidata.mdm.meta.ArrayValueType source) {
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
    public static LookupEntityDef convert(org.unidata.mdm.meta.LookupEntityDef source) {

        MeasurementPoint.start();
        try {

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

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * From code attributes def.
     *
     * @param source the source
     * @return the list
     */
    private static List<CodeAttributeDef> fromCodeAttributesDef(List<org.unidata.mdm.meta.CodeAttributeDef> source) {
        if (source == null) {
            return Collections.emptyList();
        }

        List<CodeAttributeDef> target = new ArrayList<>();
        source.forEach(s -> target.add(fromCodeAttributeDef(s)));
        return target;
    }

    /**
     * From code attribute def.
     *
     * @param source the source
     * @return the code attribute def
     */
    private static CodeAttributeDef fromCodeAttributeDef(org.unidata.mdm.meta.CodeAttributeDef source) {
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
    private static SimpleDataType fromSimpleDataType(org.unidata.mdm.meta.SimpleDataType source) {
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
    public static RelationDef convert(org.unidata.mdm.meta.RelationDef source) {
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
     * From rel type.
     *
     * @param source the source
     * @return the rel type
     */
    private static RelType fromRelType(org.unidata.mdm.meta.RelType source) {
        if (source == null) {
            return null;
        }
        return RelType.valueOf(source.name());
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the gets the model response
     */
    public static GetModelResponse convert(org.unidata.mdm.meta.Model source) {
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
    private static Model fromModel(org.unidata.mdm.meta.Model source) {
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
     * @return the list of relations
     */
    private static ListOfRelations fromRelations(List<org.unidata.mdm.meta.RelationDef> source) {
        if (source == null) {
            return null;
        }
        ListOfRelations target = new ListOfRelations();
        source.forEach(s -> target.getRel().add(convert(s)));
        return target;
    }

    /**
     * From nested entities.
     *
     * @param source the source
     * @return the list of nested entities
     */
    private static ListOfNestedEntities fromNestedEntities(List<org.unidata.mdm.meta.NestedEntityDef> source) {
        if (source == null) {
            return null;
        }
        ListOfNestedEntities target = new ListOfNestedEntities();
        source.forEach(s -> target.getNestedEntity().add(convert(s)));
        return target;
    }

    /**
     * From measurement values.
     *
     * @param source the source
     * @return the measurement values
     */
    private static MeasurementValues fromMeasurementValues(org.unidata.mdm.meta.MeasurementValues source) {
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
            List<org.unidata.mdm.meta.MeasurementValueDef> source) {
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
    private static MeasurementValueDef fromMeasurementValue(org.unidata.mdm.meta.MeasurementValueDef source) {
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
    private static List<MeasurementUnitDef> fromUnits(List<org.unidata.mdm.meta.MeasurementUnitDef> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        List<MeasurementUnitDef> target = new ArrayList<>();
        source.forEach(s -> target.add(fromUnit(s)));
        return target;
    }

    /**
     * From unit.
     *
     * @param source the source
     * @return the measurement unit def
     */
    private static MeasurementUnitDef fromUnit(org.unidata.mdm.meta.MeasurementUnitDef source) {
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
    private static ListOfLookupEntities fromLookupEntities(List<org.unidata.mdm.meta.LookupEntityDef> source) {
        if (source == null) {
            return null;
        }
        ListOfLookupEntities target = new ListOfLookupEntities();
        source.forEach(s -> target.getLookupEntity().add(convert(s)));
        return target;
    }

    /**
     * From enumertations.
     *
     * @param source the source
     * @return the list of enumerations
     */
    private static ListOfEnumerations fromEnumertations(List<org.unidata.mdm.meta.EnumerationDataType> source) {
        if (source == null) {
            return null;
        }
        ListOfEnumerations target = new ListOfEnumerations();
        source.forEach(s -> target.getEnumeration().add(fromEnumertation(s)));
        return target;
    }

    /**
     * From enumertation.
     *
     * @param source the source
     * @return the enumeration data type
     */
    private static EnumerationDataType fromEnumertation(org.unidata.mdm.meta.EnumerationDataType source) {
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
    private static EntitiesGroupDef fromEntitiesGroup(org.unidata.mdm.meta.EntitiesGroupDef source) {
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
    private static List<EntitiesGroupDef> fromInnerGroups(List<org.unidata.mdm.meta.EntitiesGroupDef> source) {
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
     * @return the list of entities
     */
    private static ListOfEntities fromEntities(List<org.unidata.mdm.meta.EntityDef> source) {
        if (source == null) {
            return null;
        }
        ListOfEntities target = new ListOfEntities();
        source.forEach(s -> target.getEntity().add(convert(s)));
        return target;
    }

}
