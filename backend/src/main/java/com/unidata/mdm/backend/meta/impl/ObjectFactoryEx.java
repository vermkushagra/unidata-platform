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

/**
 *
 */
package com.unidata.mdm.backend.meta.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.namespace.QName;

import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.ObjectFactory;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ObjectFactoryEx extends ObjectFactory {
    /**
     * Meta prefix.
     */
    public static final String META_PREFIX = "meta";
    /**
     * Cleanse function {@link QName}.
     */
    /*
    public static final QName CLEANSE_FUNCTION_QNAME
        = new QName("http://meta.mdm.taskdata.com/", ModelType.CLEANSE_FUNCTION.getTag());
    */
    /**
     * Composite cleanse function {@link QName}.
     */
    /*
    public static final QName COMPOSITE_CLEANSE_FUNCTION_QNAME
        = new QName("http://meta.mdm.taskdata.com/", ModelType.COMPOSITE_CLEANSE_FUNCTION.getTag());
    */
    /**
     * Cleanse function group {@link QName}.
     */
    public static final QName CLEANSE_FUNCTION_GROUP_QNAME
        = new QName(JaxbUtils.META_URI, ModelType.CLEANSE_FUNCTION_GROUP.getTag(), META_PREFIX);
    /**
     * Source system {@link QName}.
     */
    public static final QName SOURCE_SYSTEM_QNAME
        = new QName(JaxbUtils.META_URI, ModelType.SOURCE_SYSTEM.getTag(), META_PREFIX);
    /**
     * Enumeration {@link QName}.
     */
    public static final QName ENUMERATION_QNAME
        = new QName(JaxbUtils.META_URI, ModelType.ENUMERATION.getTag(), META_PREFIX);
    /**
     * Lookup entity {@link QName}.
     */
    public static final QName LOOKUP_ENTITY_QNAME
        = new QName(JaxbUtils.META_URI, ModelType.LOOKUP_ENTITY.getTag(), META_PREFIX);
    /**
     * Nested entity {@link QName}.
     */
    public static final QName NESTED_ENTITY_QNAME
        = new QName(JaxbUtils.META_URI, ModelType.NESTED_ENTITY.getTag(), META_PREFIX);
    /**
     * Top level entity {@link QName}.
     */
    public static final QName ENTITY_QNAME
        = new QName(JaxbUtils.META_URI, ModelType.ENTITY.getTag(), META_PREFIX);
    /**
     * Top level entities group {@link QName}.
     */
    public static final QName ENTITIES_GROUP_QNAME
            = new QName(JaxbUtils.META_URI, ModelType.ENTITIES_GROUP.getTag(), META_PREFIX);
    /**
     * Relation {@link QName}.
     */
    public static final QName RELATION_QNAME
        = new QName(JaxbUtils.META_URI, ModelType.RELATION.getTag(), META_PREFIX);
    /**
     * Constructor.
     */
    public ObjectFactoryEx() {
        super();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CleanseFunctionDef }{@code >}}
     *
     */
    /*
    @XmlElementDecl(namespace = "http://meta.mdm.taskdata.com/", name = "cleanseFunction")
    public JAXBElement<CleanseFunctionDef> createCleanseFunctionDef(CleanseFunctionDef value) {
        return new JAXBElement<CleanseFunctionDef>(CLEANSE_FUNCTION_QNAME, CleanseFunctionDef.class, null, value);
    }
    */
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositeCleanseFunctionDef }{@code >}}
     *
     */
    /*
    @XmlElementDecl(namespace = "http://meta.mdm.taskdata.com/", name = "compositeCleanseFunction")
    public JAXBElement<CompositeCleanseFunctionDef> createCompositeCleanseFunctionDef(CompositeCleanseFunctionDef value) {
        return new JAXBElement<CompositeCleanseFunctionDef>(COMPOSITE_CLEANSE_FUNCTION_QNAME, CompositeCleanseFunctionDef.class, null, value);
    }
    */
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CleanseFunctionGroupDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "cleanseFunctionGroup")
    public JAXBElement<CleanseFunctionGroupDef> createCleanseFunctionGroupDef(CleanseFunctionGroupDef value) {
        return new JAXBElement<CleanseFunctionGroupDef>(CLEANSE_FUNCTION_GROUP_QNAME, CleanseFunctionGroupDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SourceSystemDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "sourceSystem")
    public JAXBElement<SourceSystemDef> createSourceSystemDef(SourceSystemDef value) {
        return new JAXBElement<SourceSystemDef>(SOURCE_SYSTEM_QNAME, SourceSystemDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnumerationDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "enumeration")
    public JAXBElement<EnumerationDataType> createEnumerationDataType(EnumerationDataType value) {
        return new JAXBElement<EnumerationDataType>(ENUMERATION_QNAME, EnumerationDataType.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupEntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "lookupEntity")
    public JAXBElement<LookupEntityDef> createLookupEntityDef(LookupEntityDef value) {
        return new JAXBElement<LookupEntityDef>(LOOKUP_ENTITY_QNAME, LookupEntityDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NestedEntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "nestedEntity")
    public JAXBElement<NestedEntityDef> createNestedEntityDef(NestedEntityDef value) {
        return new JAXBElement<NestedEntityDef>(NESTED_ENTITY_QNAME, NestedEntityDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "entity")
    public JAXBElement<EntityDef> createEntityDef(EntityDef value) {
        return new JAXBElement<EntityDef>(ENTITY_QNAME, EntityDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "entitiesGroup")
    public JAXBElement<EntitiesGroupDef> createEntitiesGroupDef(EntitiesGroupDef value) {
        return new JAXBElement<EntitiesGroupDef>(ENTITIES_GROUP_QNAME, EntitiesGroupDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelationDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = JaxbUtils.META_URI, name = "relation")
    public JAXBElement<RelationDef> createRelationDef(RelationDef value) {
        return new JAXBElement<RelationDef>(RELATION_QNAME, RelationDef.class, null, value);
    }
}
