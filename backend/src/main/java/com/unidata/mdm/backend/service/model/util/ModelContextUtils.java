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

package com.unidata.mdm.backend.service.model.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.meta.VersionedObjectDef;

/**
 * @author Mikhail Mikhailov
 * Model context manipulation routines.
 */
public class ModelContextUtils {

    /**
     * Constructor.
     */
    private ModelContextUtils() {
        super();
    }

    public static boolean hasIdsForModelType(DeleteModelRequestContext ctx, ModelType modelType) {
        switch (modelType) {
            case RELATION:
                return ctx.hasRelationIds();
            case SOURCE_SYSTEM:
                return ctx.hasSourceSystemIds();
            case ENUMERATION:
                return ctx.hasEnumerationIds();
            case LOOKUP_ENTITY:
                return ctx.hasLookupEntitiesIds();
            case ENTITY:
                return ctx.hasEntitiesIds();
            case NESTED_ENTITY:
                return ctx.hasNestedEntitiesIds();
            default:
                return false;
        }
    }

    public static List<String> getIdsForModelType(DeleteModelRequestContext ctx, ModelType modelType) {
        switch (modelType) {
            case RELATION:
                return ctx.getRelationIds();
            case SOURCE_SYSTEM:
                return ctx.getSourceSystemIds();
            case ENUMERATION:
                return ctx.getEnumerationIds();
            case LOOKUP_ENTITY:
                return ctx.getLookupEntitiesIds();
            case ENTITY:
                return ctx.getEntitiesIds();
            case NESTED_ENTITY:
                return ctx.getNestedEntitiesIds();
            default:
                return Collections.emptyList();
        }
    }

    public static <E extends VersionedObjectDef> List<String> getIdsByClass(DeleteModelRequestContext ctx, @Nonnull Class<E> clazz) {
        ModelType modelType = ModelType.getByRelatedClass(clazz);
        if (modelType == null) {
            return Collections.emptyList();
        } else {
            return getIdsForModelType(ctx, modelType);
        }
    }

    public static boolean hasUpdateForModelType(UpdateModelRequestContext ctx, @Nonnull ModelType modelType) {
        switch (modelType) {
            case CLEANSE_FUNCTION_GROUP:
                return ctx.hasCleanseFunctionsUpdate();
            case RELATION:
                return ctx.hasRelationsUpdate();
            case SOURCE_SYSTEM:
                return ctx.hasSourceSystemsUpdate();
            case ENTITY:
                return ctx.hasEntityUpdate();
            case LOOKUP_ENTITY:
                return ctx.hasLookupEntityUpdate();
            case NESTED_ENTITY:
                return ctx.hasNestedEntityUpdate();
            case ENUMERATION:
                return ctx.hasEnumerationUpdate();
            case ENTITIES_GROUP:
                return ctx.hasEntitiesGroupUpdate();
            default:
                return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends VersionedObjectDef> Collection<E> getUpdateByModelType(UpdateModelRequestContext ctx, @Nonnull Class<E> clazz) {

        ModelType modelType = ModelType.getByRelatedClass(clazz);
        if (modelType == null) {
            return Collections.emptyList();
        }

        switch (modelType) {
            case CLEANSE_FUNCTION_GROUP:
                return ctx.getCleanseFunctionsUpdate() == null ? Collections.emptyList() : (Collection<E>) Collections.singletonList(ctx.getCleanseFunctionsUpdate());
            case RELATION:
                return (Collection<E>) ctx.getRelationsUpdate();
            case SOURCE_SYSTEM:
                return (Collection<E>) ctx.getSourceSystemsUpdate();
            case ENTITY:
                return (Collection<E>) ctx.getEntityUpdate();
            case LOOKUP_ENTITY:
                return (Collection<E>) ctx.getLookupEntityUpdate();
            case NESTED_ENTITY:
                return (Collection<E>) ctx.getNestedEntityUpdate();
            case ENUMERATION:
                return (Collection<E>) ctx.getEnumerationsUpdate();
            case ENTITIES_GROUP:
                return ctx.getEntitiesGroupsUpdate() == null ? Collections.emptyList() : (Collection<E>) Collections.singletonList(ctx.getEntitiesGroupsUpdate());
            default:
                return Collections.emptyList();
        }
    }
}
