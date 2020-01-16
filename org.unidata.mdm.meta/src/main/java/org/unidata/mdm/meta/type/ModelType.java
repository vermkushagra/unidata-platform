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
package org.unidata.mdm.meta.type;

import javax.annotation.Nullable;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.type.info.impl.EntitiesGroupWrapper;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.type.info.impl.EnumerationInfoHolder;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;
import org.unidata.mdm.meta.type.info.impl.NestedInfoHolder;
import org.unidata.mdm.meta.type.info.impl.RelationInfoHolder;
import org.unidata.mdm.meta.type.info.impl.SourceSystemInfoHolder;

/**
 * @author Mikhail Mikhailov
 *         Types of the model objects.
 */
public enum ModelType {
    /**
     * Entities group.
     * Group tree is stored separately from entities and lookup entities.
     */
    ENTITIES_GROUP("entitiesGroup") {
        @Override
        public Class<? extends IdentityModelElement> getWrapperClass() {
            return EntitiesGroupWrapper.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return EntitiesGroupDef.class;
        }
    },
    /**
     * Source system.
     */
    SOURCE_SYSTEM("sourceSystem") {
        @Override
        public Class<? extends IdentityModelElement> getWrapperClass() {
            return SourceSystemInfoHolder.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return SourceSystemDef.class;
        }
    },
    /**
     * Enumeration.
     */
    ENUMERATION("enumeration") {
        @Override
        public Class<? extends IdentityModelElement> getWrapperClass() {
            return EnumerationInfoHolder.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return EnumerationDataType.class;
        }
    },
    /**
     * Lookup entity.
     */
    LOOKUP_ENTITY("lookupEntity") {
        @Override
        public Class<? extends IdentityModelElement> getWrapperClass() {
            return LookupInfoHolder.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return LookupEntityDef.class;
        }
    },
    /**
     * Nested entity.
     */
    NESTED_ENTITY("nestedEntity") {
        @Override
        public Class<? extends IdentityModelElement> getWrapperClass() {
            return NestedInfoHolder.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return NestedEntityDef.class;
        }
    },
    /**
     * Top level entity.
     */
    ENTITY("entity") {
        @Override
        public Class<? extends IdentityModelElement> getWrapperClass() {
            return EntityInfoHolder.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return EntityDef.class;
        }
    },
    /**
     * Relation.
     */
    RELATION("relation") {
        @Override
        public Class<? extends IdentityModelElement> getWrapperClass() {
            return RelationInfoHolder.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return RelationDef.class;
        }
    };
    /**
     * Tag and also an element name.
     */
    private final String tag;

    /**
     * Constructor.
     *
     * @param tag name of the tag
     */
    private ModelType(String tag) {
        this.tag = tag;
    }

    @Nullable
    public static ModelType getByElementClass(Class<? extends VersionedObjectDef> modelElementClass) {
        for (ModelType modelType : ModelType.values()) {
            if (modelType.getModelElementClass().equals(modelElementClass)) {
                return modelType;
            }
        }
        return null;
    }

    public static boolean isOf(Class<? extends VersionedObjectDef> modelElementClass, Class<? extends IdentityModelElement> wrapperClass) {
        ModelType relatedModelType = getByElementClass(modelElementClass);
        return relatedModelType != null && relatedModelType.getWrapperClass().equals(wrapperClass);
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    public abstract Class<? extends IdentityModelElement> getWrapperClass();

    public abstract Class<? extends VersionedObjectDef> getModelElementClass();

}


