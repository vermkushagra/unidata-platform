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

package org.unidata.mdm.meta.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.AbstractAttributeDef;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.AbstractSimpleAttributeDef;
import org.unidata.mdm.meta.ArrayAttributeDef;
import org.unidata.mdm.meta.CodeAttributeDef;
import org.unidata.mdm.meta.ComplexAttributeDef;
import org.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.EnumerationValue;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.MergeAttributeDef;
import org.unidata.mdm.meta.MergeSettingsDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.SimpleAttributeDef;
import org.unidata.mdm.meta.SimpleAttributesHolderEntityDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.type.info.impl.AttributeInfoHolder;
import org.unidata.mdm.system.util.TextUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ModelUtils {

    private static final String ESCAPE_STANDARD_NAMESPACE_SEPARATOR = "\\.";

    private static final int DIRECT_LEVEL = 1;
    /**
     * Name of the default source system.
     */
    public static final String DEFAULT_SOURCE_SYSTEM_NAME = "unidata";
    /**
     * Weight of the default source system.
     */
    public static final int DEFAULT_SOURCE_SYSTEM_WEIGHT = 100;
    /**
     * Name of the default entities group.
     */
    public static final String DEFAULT_GROUP_NAME = "ROOT";
    /**
     * Name of the model init lock.
     */
    public static final String MODEL_INIT_LOCK_NAME = "MODEL_INIT_LOCK_NAME";
    /**
     * Source systems comparator.
     */
    public static final Comparator<SourceSystemDef> SOURCE_SYSTEMS_COMPARATOR
        = (o1, o2) -> o1.getWeight().intValue() - o2.getWeight().intValue();

    /**
     * Source systems comparator.
     */
    public static final Comparator<SourceSystemDef> SOURCE_SYSTEMS_REVERSE_COMPARATOR
        = (o1, o2) -> o2.getWeight().intValue() - o1.getWeight().intValue();


    public static void init() {
    }

    private static Integer getOrder(AbstractAttributeDef attr)  {
        if (attr instanceof SimpleAttributeDef) {
            return ((SimpleAttributeDef) attr).getOrder().intValue();
        } else if (attr instanceof ArrayAttributeDef) {
            return ((ArrayAttributeDef) attr).getOrder().intValue();
        } else if (attr instanceof CodeAttributeDef) {
            return 0;
        }
        return 0;
    }
    /**
     * Displayable attributes comparator.
     */
    public static final Comparator<? super AbstractAttributeDef> DISPLAYABLE_ATTRIBUTES_COMPARATOR
        = (o1, o2) -> (o1.getClass().isAssignableFrom(SimpleAttributeDef.class) ? ((SimpleAttributeDef) o1).getOrder().intValue() : 0) -
                      (o2.getClass().isAssignableFrom(SimpleAttributeDef.class) ? ((SimpleAttributeDef) o2).getOrder().intValue() : 0);

    /**
     * Instantiation disabled.
     */
    private ModelUtils() {
        super();
    }

    // UN-7293
    /*
    public static void calculateDqRuleId(DQRuleDef dqRuleDefinition) {
        if (StringUtils.isEmpty(dqRuleDefinition.getId())) {
            dqRuleDefinition.setId(IdUtils.v4String());
        }
    }
    */

    /**
     * Gets attribute name respecting level.
     * @param level the level
     * @param path the path
     * @return attribute name
     */
    public static String getAttributeName(int level, String path) {
        return StringUtils.split(path, '.')[level];
    }

    /**
     * Tests if the name is a compound path.
     * @param path property name
     * @return true, if so, false otherwise
     */
    public static boolean isCompoundPath(String path) {
        return path != null && path.indexOf('.') != -1;
    }

    /**
     * Strips attribute path according to the level.
     * @param level the level
     * @param path the path
     * @return attribute path
     */
    public static String stripAttributePath(int level, String path) {
        return String.join(".", Arrays.copyOf(StringUtils.split(path, '.'), level + 1));
    }

    /**
     * Strips attribute path according to the level.
     * @param level the level
     * @param path the path
     * @return attribute path
     */
    public static String subAttributePath(int level, String path) {
        String[] parts = StringUtils.split(path, '.');
        return String.join(".", Arrays.copyOfRange(parts, level, parts.length));
    }

    /**
     *
     * @param path the path
     * @return level of attribute in hierarchy
     */
    public static int getAttributeLevel(String path) {
        String[] parts = StringUtils.split(path, '.');
        return parts.length - 1;
    }

    /**
     * Gets path for an attribute.
     * @param level current level
     * @param path current path
     * @param attr attribute
     * @return joined path
     */
    public static String getAttributePath(int level, String path, AbstractAttributeDef attr) {
        return getAttributePath(level, path, attr.getName());
    }

    /**
     * Gets path for an attribute.
     * @param level current level
     * @param path current path
     * @param attrName attribute name
     * @return joined path
     */
    public static String getAttributePath(int level, String path, String attrName) {
        return level == 0 ? attrName : String.join(".", path, attrName);
    }

    /**
     * Gets path for an attribute.
     * @param path current path
     * @param attrName attribute
     * @return joined path
     */
    public static String getAttributePath(String path, String attrName) {
        return StringUtils.isBlank(path) ? attrName : String.join(".", path, attrName);
    }

    /**
     * Creates (linked) enumeration map.
     * @param enumeration the enumeration to process
     * @return map with [name, displayName] entries
     */
    public static Map<String, String> createEnumerationMap(EnumerationDataType enumeration) {

        if (enumeration == null || enumeration.getEnumVal() == null || enumeration.getEnumVal().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> enumerationMap = new LinkedHashMap<>(enumeration.getEnumVal().size());
        for (EnumerationValue enumValue : enumeration.getEnumVal()) {
            enumerationMap.put(enumValue.getName(), enumValue.getDisplayName());
        }

        return enumerationMap;
    }

    /**
     * Creates (linked) source systems map.
     * @param sourceSystems
     * @param reversed return map in reversed order, otherwise straight
     * @return
     */
    public static Map<String, Integer> createSourceSystemsMap(List<SourceSystemDef> sourceSystems, boolean reversed) {

        if (sourceSystems == null || sourceSystems.isEmpty()) {
            return Collections.emptyMap();
        }

        List<SourceSystemDef> copy = new ArrayList<>(sourceSystems);
        Collections.sort(copy, reversed ? SOURCE_SYSTEMS_REVERSE_COMPARATOR : SOURCE_SYSTEMS_COMPARATOR);

        Map<String, Integer> sourceSystemsMap = new LinkedHashMap<>(copy.size());
        for (SourceSystemDef ssd : copy) {
            sourceSystemsMap.put(ssd.getName(), Integer.valueOf(ssd.getWeight().intValue()));
        }

        return sourceSystemsMap;
    }

    /**
     * Creates BVT attributes map.
     * @param e the entity
     * @param globalSourceSystems global source systems list
     * @param attrs attributes map
     */
    public static Map<String,Map<String,Integer>> createBvtMap(
            AbstractEntityDef e,
            List<SourceSystemDef> globalSourceSystems,
            Map<String, AttributeModelElement> attrs) {

        Map<String, Integer> globalSourceSystemsMap
            = ModelUtils.createSourceSystemsMap(globalSourceSystems, true);

        MergeSettingsDef settings = e.getMergeSettings();
        List<MergeAttributeDef> bvtAttrs
            = settings != null && settings.getBvtSettings() != null
                ? settings.getBvtSettings().getAttributes()
                : null;

        Map<String, Map<String, Integer>> mergeAttrs = new LinkedHashMap<>();
        for (int i = 0; bvtAttrs != null && i < bvtAttrs.size(); i++) {

            MergeAttributeDef attrDef = bvtAttrs.get(i);
            Map<String, Integer> overridden = ModelUtils.createSourceSystemsMap(attrDef.getSourceSystemsConfigs(), true);

            // UN-3053 Merge settings doesn't contain new source systems
            for (Entry<String, Integer> ge : globalSourceSystemsMap.entrySet()) {
                if (overridden.containsKey(ge.getKey())) {
                    continue;
                }

                overridden.put(ge.getKey(), Integer.valueOf(0));
            }

            mergeAttrs.put(attrDef.getName(), overridden);
        }

        Map<String, Integer> bvrSourceSystemsMap
            = ModelUtils.createSourceSystemsMap(settings != null && settings.getBvrSettings() != null
                ? settings.getBvrSettings().getSourceSystemsConfigs()
                : null, true);

        for (Entry<String, AttributeModelElement> entry : attrs.entrySet()) {
            if (mergeAttrs.containsKey(entry.getKey())) {
                continue;
            }

            if (!bvrSourceSystemsMap.isEmpty()) {
                mergeAttrs.put(entry.getKey(), bvrSourceSystemsMap);
                continue;
            }

            mergeAttrs.put(entry.getKey(), globalSourceSystemsMap);
        }

        return mergeAttrs;
    }

    /**
     * Returns new ordered attributes map.
     * @param e entity
     * @param refs references
     * @return map
     */
    public static Map<String, AttributeModelElement> createAttributesMap(AbstractEntityDef e, List<NestedEntityDef> refs) {

        Map<String, AttributeModelElement> attrs = new LinkedHashMap<>();
        createAttributesMap(e, StringUtils.EMPTY, 0, refs, attrs, null);
        return attrs;
    }

    /**
     * Builds an attributes map for an entity recursively.
     * @param e the entity
     * @param path current path
     * @param level current level
     * @param refs nested entities (references)
     * @param attrs attributes map
     * @param parent parent link
     */
    public static void createAttributesMap(
            AbstractEntityDef e, String path, int level,
            List<NestedEntityDef> refs,
            Map<String, AttributeModelElement> attrs, AttributeModelElement parent) {

        // 1. Check lookup entity attributes
        if (e instanceof LookupEntityDef) {

            LookupEntityDef lookupEntityDef = (LookupEntityDef) e;
            CodeAttributeDef attr = lookupEntityDef.getCodeAttribute();
            AttributeModelElement holder = new AttributeInfoHolder(attr, e, null, getAttributePath(level, path, attr), level, false);
            attrs.put(holder.getPath(), holder);

            for (CodeAttributeDef attributeDef : lookupEntityDef.getAliasCodeAttributes()) {
                AttributeModelElement alias
                    = new AttributeInfoHolder(attributeDef, e, null, getAttributePath(level, path, attributeDef), level, true);
                attrs.put(alias.getPath(), alias);
            }
        }

        // 2. Process simple attributes
        if (e instanceof SimpleAttributesHolderEntityDef) {

            SimpleAttributesHolderEntityDef sahe = (SimpleAttributesHolderEntityDef) e;

            List<AttributeModelElement> thisLevelAttrs = new ArrayList<>(
                    sahe.getArrayAttribute().size() +
                    sahe.getSimpleAttribute().size());

            for (SimpleAttributeDef attr : sahe.getSimpleAttribute()) {
                if (StringUtils.isBlank(attr.getName())) {
                    throw new IllegalArgumentException("Name of a simple attribute is invalid.");
                }

                AttributeModelElement holder = new AttributeInfoHolder(attr, e, parent, getAttributePath(level, path, attr), level);
                if (parent != null) {
                    parent.getChildren().add(holder);
                }

                thisLevelAttrs.add(holder);
            }

            for (ArrayAttributeDef attr : sahe.getArrayAttribute()) {
                if (StringUtils.isBlank(attr.getName())) {
                    throw new IllegalArgumentException("Name of an array attribute is invalid.");
                }

                AttributeModelElement holder = new AttributeInfoHolder(attr, e, parent, getAttributePath(level, path, attr), level);
                if (parent != null) {
                    parent.getChildren().add(holder);
                }

                thisLevelAttrs.add(holder);
            }

            Collections.sort(thisLevelAttrs, (o1, o2) -> o1.getOrder() - o2.getOrder());
            for (AttributeModelElement holder : thisLevelAttrs) {
                attrs.put(holder.getPath(), holder);
            }
        }

        // 3. Process complex attributes
        if (e instanceof ComplexAttributesHolderEntityDef) {

            ComplexAttributesHolderEntityDef cahe = (ComplexAttributesHolderEntityDef) e;
            for (ComplexAttributeDef attr : cahe.getComplexAttribute()) {

                if (StringUtils.isBlank(attr.getName())
                 || StringUtils.isBlank(attr.getNestedEntityName())) {
                    throw new IllegalArgumentException("Name or nested entity name of a complex attribute is invalid.");
                }

                AttributeModelElement holder = new AttributeInfoHolder(attr, e, parent, getAttributePath(level, path, attr), level);
                if (parent != null) {
                    parent.getChildren().add(holder);
                }

                attrs.put(holder.getPath(), holder);
                NestedEntityDef nested = refs.stream()
                        .filter(ne -> attr.getNestedEntityName().equals(ne.getName()))
                        .findFirst()
                        .orElse(null);

                createAttributesMap(nested, holder.getPath(), level + 1, refs, attrs, holder);
            }
        }
    }

    /**
     * Find attribute.
     *
     * @param pathToSearch the path to search
     * @param entity       the entity
     * @return the simple attribute
     */
    @Nullable
    public static AbstractAttributeDef findModelAttribute(@Nonnull String pathToSearch, @Nonnull SimpleAttributesHolderEntityDef entity, Collection<NestedEntityDef> nestedEntityDefs) {
        String[] splitPath = StringUtils.split(pathToSearch, ESCAPE_STANDARD_NAMESPACE_SEPARATOR);
        return findModelAttributeBySplitPath(splitPath, entity, nestedEntityDefs);
    }

    @Nullable
    private static AbstractAttributeDef findModelAttributeBySplitPath(@Nonnull final String[] splitPath, @Nonnull final SimpleAttributesHolderEntityDef entity, Collection<NestedEntityDef> nestedEntityDefs) {
    	if(splitPath==null||splitPath.length==0){
    		return null;
    	}
    	String attrName = splitPath[0];
        if (splitPath.length == DIRECT_LEVEL) {
            return getAttributeByName(entity, attrName);
        } else if (!(entity instanceof ComplexAttributesHolderEntityDef)) {
            return null;
        }

        AbstractAttributeDef abstractAttributeDef = getAttributeByName((ComplexAttributesHolderEntityDef) entity, attrName);

		String nestedEntityName = abstractAttributeDef == null ? StringUtils.EMPTY
				: (abstractAttributeDef instanceof ComplexAttributeDef)
						? ((ComplexAttributeDef) abstractAttributeDef).getNestedEntityName()
						: abstractAttributeDef.getName();
        Optional<? extends SimpleAttributesHolderEntityDef> foundNestedEntity = nestedEntityDefs.stream().filter(nestedEntity -> nestedEntity.getName().equals(nestedEntityName)).findAny();
        String[] nextPath = Arrays.copyOfRange(splitPath, 1, splitPath.length);
        return !foundNestedEntity.isPresent() ? null : findModelAttributeBySplitPath(nextPath, foundNestedEntity.get(), nestedEntityDefs);
    }

    //todo replace!

    /**
     * @param entity   - entity for searching
     * @param attrName - attribute name
     * @return attribute from top level of entity if it present
     */
    @Nullable
    public static AbstractAttributeDef getAttributeByName(@Nonnull final SimpleAttributesHolderEntityDef entity, @Nonnull final String attrName) {
        if (entity instanceof LookupEntityDef) {
            return getAttributeByName((LookupEntityDef) entity, attrName);
        } else if (entity instanceof ComplexAttributesHolderEntityDef) {
            return getAttributeByName((ComplexAttributesHolderEntityDef) entity, attrName);
        } else {
            return getAttributeByNameFromSimpleAttrHolder(entity, attrName);
        }
    }

    /**
     * @param entity   - entity for searching
     * @param attrName - attribute name
     * @return attribute from top level of entity if it present
     */
    @Nullable
    public static AbstractAttributeDef getAttributeByName(@Nonnull final LookupEntityDef entity, @Nonnull final String attrName) {
        Optional<CodeAttributeDef> codeAttribute = entity.getAliasCodeAttributes().stream()
                .filter(attr -> attrName.equals(attr.getName())).findAny();
        if (codeAttribute.isPresent()) {
            return codeAttribute.get();
        } else if (entity.getCodeAttribute().getName().equals(attrName)) {
            return entity.getCodeAttribute();
        } else {
            return getAttributeByNameFromSimpleAttrHolder(entity, attrName);
        }
    }

    /**
     * @param entity   - entity for searching
     * @param attrName - attribute name
     * @return attribute from top level of entity if it present
     */
    @Nullable
    public static AbstractAttributeDef getAttributeByName(@Nonnull final ComplexAttributesHolderEntityDef entity, @Nonnull final String attrName) {
        Optional<? extends AbstractAttributeDef> foundComplexAttr = entity.getComplexAttribute().stream().filter(attributeDef -> attributeDef.getName().equals(attrName)).findAny();
        if (foundComplexAttr.isPresent()) {
            return foundComplexAttr.get();
        } else {
            return getAttributeByNameFromSimpleAttrHolder(entity, attrName);
        }
    }

    private static AbstractAttributeDef getAttributeByNameFromSimpleAttrHolder(@Nonnull final SimpleAttributesHolderEntityDef entity, @Nonnull final String attrName) {

        Optional<? extends AbstractAttributeDef> foundAttr = entity.getSimpleAttribute().stream()
                .filter(attr -> attr.getName().equals(attrName))
                .findAny();

        if (foundAttr.isPresent()) {
            return foundAttr.get();
        } else {
            return entity.getArrayAttribute().stream()
                    .filter(attr -> attr.getName().equals(attrName))
                    .findAny()
                    .orElse(null);
        }
    }


    /**
     * @param lookupEntityDef - lookup entity for searching
     * @return list of name which have flag isMainDisplayable equal true
     */
    @Deprecated
    public static List<String> findMainDisplayableAttrNamesSorted(@Nonnull LookupEntityDef lookupEntityDef) {
        List<AbstractAttributeDef> displayableAttrs = new ArrayList<>();

        lookupEntityDef.getSimpleAttribute().stream()
                .filter(AbstractSimpleAttributeDef::isMainDisplayable)
                .collect(Collectors.toCollection(() -> displayableAttrs));

        lookupEntityDef.getArrayAttribute().stream()
                .filter(ArrayAttributeDef::isMainDisplayable)
                .collect(Collectors.toCollection(() -> displayableAttrs));

        lookupEntityDef.getAliasCodeAttributes().stream()
                .filter(CodeAttributeDef::isMainDisplayable)
                .collect(Collectors.toCollection(() -> displayableAttrs));

        if (lookupEntityDef.getCodeAttribute().isMainDisplayable()) {
            displayableAttrs.add(lookupEntityDef.getCodeAttribute());
        }

        Collections.sort(displayableAttrs, DISPLAYABLE_ATTRIBUTES_COMPARATOR);
        return displayableAttrs.stream()
                .sorted(Comparator.comparing(ModelUtils::getOrder))
                .map(AbstractAttributeDef::getName)
                .collect(Collectors.toList());
    }

    /**
     * @param entity   - entity for searching
     * @param attrName - attribute name
     * @return boolean value. true mean it is complex attribute name, false mean attribute not presented or it is simple attribute.
     */
    public static boolean isComplexAttribute(@Nonnull final ComplexAttributesHolderEntityDef entity, @Nonnull final String attrName) {
        AbstractAttributeDef result = getAttributeByName(entity, attrName);
        return result != null && result instanceof ComplexAttributeDef;
    }

    /**
     * Creates default (system) source - system.
     * @return source systen definition
     */
    public static SourceSystemDef createDefaultSourceSystem() {
        return MetaJaxbUtils.getMetaObjectFactory().createSourceSystemDef()
                .withName(ModelUtils.DEFAULT_SOURCE_SYSTEM_NAME)
                .withWeight(BigInteger.valueOf(ModelUtils.DEFAULT_SOURCE_SYSTEM_WEIGHT))
                .withAdmin(true)
                .withVersion(1L)
                .withDescription(
                        TextUtils.getText("app.meta.default.source.system"));
    }

    /**
     * Creates default (system) root entities group.
     * @return entities group
     */
    public static EntitiesGroupDef createDefaultEntitiesGroup() {
        return MetaJaxbUtils.getMetaObjectFactory().createEntitiesGroupDef()
                .withGroupName(DEFAULT_GROUP_NAME)
                .withTitle(
                        TextUtils.getText("app.meta.default.entities.group.root"))
                .withVersion(1L);
    }
    /**
     * Creates a new root cleanse function group and system cleanse functions.
     * @return
     */
    /*
    public static CleanseFunctionGroupDef createDefaultCleanseFunctionGroup(Locale locale) {

        Map<String, List<String>> functionsMap = new HashMap<>();

        ResourceBundle bundle = ResourceBundle.getBundle("cleanse", locale, new UTF8Control());

        for (String k : bundle.keySet()) {
            String v = bundle.getString(k);
            if (processCleanseFunctionInfo(functionsMap, "definition", k, v)
                    || processCleanseFunctionInfo(functionsMap, "className", k, v)
                    || processCleanseFunctionInfo(functionsMap, "functionName", k, v)) {
                continue; // Bogus
            }
        }

        final String packagePrefix = "com.unidata.mdm.cleanse";
        CleanseFunctionGroupDef root = DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionGroupDef()
                .withGroupName(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.root.name", "app.meta.default.cleanse.functions.group.root.name"))
                .withDescription(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.root.description", "app.meta.default.cleanse.functions.group.root.description"))
                .withVersion(1L);
        CleanseFunctionGroupDef string = DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionGroupDef()
                .withGroupName(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.string.name", "app.meta.default.cleanse.functions.group.string.name"))
                .withDescription(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.string.description", "app.meta.default.cleanse.functions.group.string.description"))
                .withVersion(1L);
        CleanseFunctionGroupDef math = DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionGroupDef()
                .withGroupName(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.math.name", "app.meta.default.cleanse.functions.group.math.name"))
                .withDescription(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.math.description", "app.meta.default.cleanse.functions.group.math.description"))
                .withVersion(1L);
        CleanseFunctionGroupDef logic = DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionGroupDef()
                .withGroupName(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.logic.name","app.meta.default.cleanse.functions.group.logic.name"))
                .withDescription(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.logic.description", "app.meta.default.cleanse.functions.group.logic.description"))
                .withVersion(1L);
        CleanseFunctionGroupDef convert = DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionGroupDef()
                .withGroupName(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.convert.name", "app.meta.default.cleanse.functions.group.convert.name"))
                .withDescription(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.convert.description", "app.meta.default.cleanse.functions.group.convert.description"))
                .withVersion(1L);
        CleanseFunctionGroupDef misc = DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionGroupDef()
                .withGroupName(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.misc.name", "app.meta.default.cleanse.functions.group.misc.name"))
                .withDescription(MessageUtils.getMessageWithLocaleAndDefault(locale, "app.meta.default.cleanse.functions.group.misc.description", "app.meta.default.cleanse.functions.group.misc.description"))
                .withVersion(1L);

        Date atDate = new Date();
        for (Entry<String, List<String>> entry : functionsMap.entrySet()) {

            if (entry.getKey().startsWith(String.join(".", packagePrefix, "string"))) {
                string.withGroupOrCleanseFunctionOrCompositeCleanseFunction(DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionExtendedDef()
                        .withFunctionName(entry.getValue().get(2))
                        .withJavaClass(entry.getValue().get(1))
                        .withDescription(entry.getValue().get(0))
                        .withCreatedAt(DqJaxbUtils.dateToXMGregorianCalendar(atDate))
                        .withCreatedBy(SecurityUtils.getCurrentUserName()));
            } else if (entry.getKey().startsWith(String.join(".", packagePrefix, "math"))) {
                math.withGroupOrCleanseFunctionOrCompositeCleanseFunction(DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionExtendedDef()
                        .withFunctionName(entry.getValue().get(2))
                        .withJavaClass(entry.getValue().get(1))
                        .withDescription(entry.getValue().get(0))
                        .withCreatedAt(DqJaxbUtils.dateToXMGregorianCalendar(atDate))
                        .withCreatedBy(SecurityUtils.getCurrentUserName()));
            } else if (entry.getKey().startsWith(String.join(".", packagePrefix, "logic"))) {
                logic.withGroupOrCleanseFunctionOrCompositeCleanseFunction(DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionExtendedDef()
                        .withFunctionName(entry.getValue().get(2))
                        .withJavaClass(entry.getValue().get(1))
                        .withDescription(entry.getValue().get(0))
                        .withCreatedAt(DqJaxbUtils.dateToXMGregorianCalendar(atDate))
                        .withCreatedBy(SecurityUtils.getCurrentUserName()));
            } else if (entry.getKey().startsWith(String.join(".", packagePrefix, "convert"))) {
                convert.withGroupOrCleanseFunctionOrCompositeCleanseFunction(DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionExtendedDef()
                        .withFunctionName(entry.getValue().get(2))
                        .withJavaClass(entry.getValue().get(1))
                        .withDescription(entry.getValue().get(0))
                        .withCreatedAt(DqJaxbUtils.dateToXMGregorianCalendar(atDate))
                        .withCreatedBy(SecurityUtils.getCurrentUserName()));
            } else if (entry.getKey().startsWith(String.join(".", packagePrefix, "misc"))) {
                misc.withGroupOrCleanseFunctionOrCompositeCleanseFunction(DqJaxbUtils.getMetaObjectFactory().createCleanseFunctionExtendedDef()
                        .withFunctionName(entry.getValue().get(2))
                        .withJavaClass(entry.getValue().get(1))
                        .withDescription(entry.getValue().get(0))
                        .withCreatedAt(DqJaxbUtils.dateToXMGregorianCalendar(atDate))
                        .withCreatedBy(SecurityUtils.getCurrentUserName()));
            }
        }

        root.withGroupOrCleanseFunctionOrCompositeCleanseFunction(string, math, logic, convert, misc);
        return root;
    }
    */
    /**
     * Process a default cleanse function definition tag.
     * @param functionsMap the map
     * @param propertyId the tag
     * @param k key
     * @param v value
     * @return true, if was a hit, false otherwise
     */
    private static boolean processCleanseFunctionInfo(Map<String, List<String>> functionsMap, String propertyId, String k, String v) {

        if (k.endsWith(propertyId)) {

            String base = stripAttributePath(getAttributeLevel(k) - 1, k);
            List<String> values = functionsMap.computeIfAbsent(base, key -> new ArrayList<>(Collections.nCopies(4, null)));
            switch (propertyId) {
            case "definition":
                values.set(0, StringUtils.trim(v));
                break;
            case "className":
                values.set(1, StringUtils.trim(v));
                break;
            case "functionName":
                values.set(2, StringUtils.trim(v));
                break;
            case "applicationMode":
                values.set(3, StringUtils.trim(v));
                break;
            default:
                return false;
            }

            return true;
        }

        return false;
    }

    public static SimpleAttribute.DataType defineDataType(SimpleAttributeDef simpleAttributeDef) {
        String type;
        if (simpleAttributeDef.getSimpleDataType() != null) {
            type = simpleAttributeDef.getSimpleDataType().name();
        } else if (StringUtils.isNotBlank(simpleAttributeDef.getEnumDataType()) ||
                   StringUtils.isNotBlank(simpleAttributeDef.getLinkDataType())) {
            type = "STRING";
        } else {
            type = simpleAttributeDef.getLookupEntityCodeAttributeType().name();
        }

        return SimpleAttribute.DataType.valueOf(type);
    }

    /**
     * Filter and collect nested entities which used in existing entities (as complex attributes).
     * It will be useful to avoid issues with model export where old non-deleted nested entities exists.
     * See UDSUE-387.
     *
     * @param nestedEntities nested entities collection.
     * @param allEntityDefs all entities from model.
     * @return nested entities referenced from entities.
     */
    public static List<NestedEntityDef> filterUsageNestedEntities(final List<NestedEntityDef> nestedEntities, List<EntityDef> allEntityDefs) {
        // Get all nestedEntity names used in entities.
        final Set<String> allNestedEntityNames = new HashSet<>();

        allEntityDefs.forEach(entity -> {
            Set<String> nestedNames = getAllNestedEntityNames(nestedEntities, entity);

            allNestedEntityNames.addAll(nestedNames);
        });

        // Collect nestedEntities which referenced from entities in complex attributes.
        return nestedEntities.stream()
                .filter(nestedEntity -> allNestedEntityNames.contains(nestedEntity.getName()))
                .collect(Collectors.toList());

    }

    /**
     * Find all deep nested entity names for declared entity.
     * All deep nested entity tree will be used for search.
     *
     * @param nestedEntities nested entities collection.
     * @param entity entity
     * @return all nested entity names collection.
     */
    private static Set<String> getAllNestedEntityNames(List<NestedEntityDef> nestedEntities, @Nonnull EntityDef entity) {
        final Set<String> allNestedEntityNames = new HashSet<>();

        entity.getComplexAttribute().forEach(complexAttributeDef -> {
            allNestedEntityNames.add(complexAttributeDef.getNestedEntityName());

            Set<String> searchNames = Collections.singleton(complexAttributeDef.getNestedEntityName());

            while (true) {
                Set<String> childNames = findChildNestedEntityNames(nestedEntities, searchNames, allNestedEntityNames);

                if (childNames.isEmpty()) {
                    break;
                }

                allNestedEntityNames.addAll(childNames);
                searchNames = childNames;
            }
        });

        return allNestedEntityNames;
    }

    /**
     * Find child nestedEntities in nestedEntities with name in searchNames collection.
     * Only one child level used for search (no deep search used).
     *
     * @param nestedEntities main collection of nested entities to find names.
     * @param searchNames nested entities to search
     * @param ignoreNames ignored names which were found before.
     * @return Set of new nested entities with usages.
     */
    private static Set<String> findChildNestedEntityNames(List<NestedEntityDef> nestedEntities, Set<String> searchNames,
                                               Set<String> ignoreNames) {
        Set<String> childNames = new HashSet<>();

        for (String searchName : searchNames) {
            Optional<NestedEntityDef> nestedEntityOptional = nestedEntities.stream()
					.filter(nestedEntity -> nestedEntity.getName().equals(searchName)).findFirst();

			if (!nestedEntityOptional.isPresent()) {
				continue;
			}

			NestedEntityDef targetNestedEntity = nestedEntityOptional.get();

			for (ComplexAttributeDef complexAttr : targetNestedEntity.getComplexAttribute()) {
				// Ignore nested name because it already added in result list.
				// Only new names will be added.
				if (ignoreNames.contains(complexAttr.getNestedEntityName())) {
                    continue;
                }

                childNames.add(complexAttr.getNestedEntityName());
            }
        }

        return childNames;
    }
}
