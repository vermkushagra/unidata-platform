/**
 *
 */
package com.unidata.mdm.backend.service.model;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.service.model.util.wrappers.CleanseFunctionRootGroupWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntitiesGroupWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.ModelWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.NestedEntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.SourceSystemWrapper;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;
import com.unidata.mdm.meta.VersionedObjectDef;

/**
 * @author Mikhail Mikhailov
 *         Types of the model objects.
 */
public enum ModelType {
    /**
     * Cleanse function group.
     * Group tree is stored separately from cleanse functions.
     */
    CLEANSE_FUNCTION_GROUP("cleanseFunctionGroup") {
        @Override
        public Class<? extends ModelWrapper> getWrapperClass() {
            return CleanseFunctionRootGroupWrapper.class;
        }

        @Override
        public Class<? extends VersionedObjectDef> getModelElementClass() {
            return CleanseFunctionGroupDef.class;
        }
    },
    /**
     * Entities group.
     * Group tree is stored separately from entities and lookup entities.
     */
    ENTITIES_GROUP("entitiesGroup") {
        @Override
        public Class<? extends ModelWrapper> getWrapperClass() {
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
        public Class<? extends ModelWrapper> getWrapperClass() {
            return SourceSystemWrapper.class;
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
        public Class<? extends ModelWrapper> getWrapperClass() {
            return EnumerationWrapper.class;
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
        public Class<? extends ModelWrapper> getWrapperClass() {
            return LookupEntityWrapper.class;
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
        public Class<? extends ModelWrapper> getWrapperClass() {
            return NestedEntityWrapper.class;
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
        public Class<? extends ModelWrapper> getWrapperClass() {
            return EntityWrapper.class;
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
        public Class<? extends ModelWrapper> getWrapperClass() {
            return RelationWrapper.class;
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
    public static ModelType getByRelatedClass(Class<? extends VersionedObjectDef> modelElementClass) {
        for (ModelType modelType : ModelType.values()) {
            if (modelType.getModelElementClass().equals(modelElementClass)) {
                return modelType;
            }
        }
        return null;
    }

    public static boolean isRelatedClasses(Class<? extends VersionedObjectDef> modelElementClass, Class<? extends ModelWrapper> wrapperClass) {
        ModelType relatedModelType = getByRelatedClass(modelElementClass);
        return relatedModelType != null && relatedModelType.getWrapperClass().equals(wrapperClass);
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    public abstract Class<? extends ModelWrapper> getWrapperClass();

    public abstract Class<? extends VersionedObjectDef> getModelElementClass();

}


