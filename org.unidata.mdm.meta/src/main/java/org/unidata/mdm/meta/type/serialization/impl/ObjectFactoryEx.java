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
package org.unidata.mdm.meta.type.serialization.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.namespace.QName;

import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.ObjectFactory;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.util.MetaJaxbUtils;

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
     * Source system {@link QName}.
     */
    public static final QName SOURCE_SYSTEM_QNAME
        = new QName(MetaJaxbUtils.META_URI, ModelType.SOURCE_SYSTEM.getTag(), META_PREFIX);
    /**
     * Enumeration {@link QName}.
     */
    public static final QName ENUMERATION_QNAME
        = new QName(MetaJaxbUtils.META_URI, ModelType.ENUMERATION.getTag(), META_PREFIX);
    /**
     * Lookup entity {@link QName}.
     */
    public static final QName LOOKUP_ENTITY_QNAME
        = new QName(MetaJaxbUtils.META_URI, ModelType.LOOKUP_ENTITY.getTag(), META_PREFIX);
    /**
     * Nested entity {@link QName}.
     */
    public static final QName NESTED_ENTITY_QNAME
        = new QName(MetaJaxbUtils.META_URI, ModelType.NESTED_ENTITY.getTag(), META_PREFIX);
    /**
     * Top level entity {@link QName}.
     */
    public static final QName ENTITY_QNAME
        = new QName(MetaJaxbUtils.META_URI, ModelType.ENTITY.getTag(), META_PREFIX);
    /**
     * Top level entities group {@link QName}.
     */
    public static final QName ENTITIES_GROUP_QNAME
            = new QName(MetaJaxbUtils.META_URI, ModelType.ENTITIES_GROUP.getTag(), META_PREFIX);
    /**
     * Relation {@link QName}.
     */
    public static final QName RELATION_QNAME
        = new QName(MetaJaxbUtils.META_URI, ModelType.RELATION.getTag(), META_PREFIX);
    /**
     * Constructor.
     */
    public ObjectFactoryEx() {
        super();
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SourceSystemDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = MetaJaxbUtils.META_URI, name = "sourceSystem")
    public JAXBElement<SourceSystemDef> createSourceSystemDef(SourceSystemDef value) {
        return new JAXBElement<SourceSystemDef>(SOURCE_SYSTEM_QNAME, SourceSystemDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnumerationDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = MetaJaxbUtils.META_URI, name = "enumeration")
    public JAXBElement<EnumerationDataType> createEnumerationDataType(EnumerationDataType value) {
        return new JAXBElement<EnumerationDataType>(ENUMERATION_QNAME, EnumerationDataType.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupEntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = MetaJaxbUtils.META_URI, name = "lookupEntity")
    public JAXBElement<LookupEntityDef> createLookupEntityDef(LookupEntityDef value) {
        return new JAXBElement<LookupEntityDef>(LOOKUP_ENTITY_QNAME, LookupEntityDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NestedEntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = MetaJaxbUtils.META_URI, name = "nestedEntity")
    public JAXBElement<NestedEntityDef> createNestedEntityDef(NestedEntityDef value) {
        return new JAXBElement<NestedEntityDef>(NESTED_ENTITY_QNAME, NestedEntityDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = MetaJaxbUtils.META_URI, name = "entity")
    public JAXBElement<EntityDef> createEntityDef(EntityDef value) {
        return new JAXBElement<EntityDef>(ENTITY_QNAME, EntityDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = MetaJaxbUtils.META_URI, name = "entitiesGroup")
    public JAXBElement<EntitiesGroupDef> createEntitiesGroupDef(EntitiesGroupDef value) {
        return new JAXBElement<EntitiesGroupDef>(ENTITIES_GROUP_QNAME, EntitiesGroupDef.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelationDef }{@code >}}
     *
     */
    @XmlElementDecl(namespace = MetaJaxbUtils.META_URI, name = "relation")
    public JAXBElement<RelationDef> createRelationDef(RelationDef value) {
        return new JAXBElement<RelationDef>(RELATION_QNAME, RelationDef.class, null, value);
    }
}
