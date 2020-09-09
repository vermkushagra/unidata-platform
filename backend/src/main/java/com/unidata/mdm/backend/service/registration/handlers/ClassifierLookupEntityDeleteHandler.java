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

package com.unidata.mdm.backend.service.registration.handlers;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.registration.keys.ClassifierRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.LookupEntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * Process removing classifier from lookup entity
 */
@Component
public class ClassifierLookupEntityDeleteHandler implements DeleteHandler<ClassifierRegistryKey, LookupEntityRegistryKey> {

    @Autowired
    private MetaModelServiceExt modelService;

    @Override
    public void onDelete(ClassifierRegistryKey removedKey, LookupEntityRegistryKey linkedKey) {
        String entityName = linkedKey.getEntityName();
        LookupEntityDef lookupEntityById = modelService.getLookupEntityById(entityName);
        lookupEntityById.getClassifiers().remove(removedKey.getClassifierName());
        UpdateModelRequestContext updateModelRequestContext = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
                .lookupEntityUpdate(Collections.singletonList(lookupEntityById))
                .build();
        modelService.upsertModel(updateModelRequestContext);
    }

    @Override
    public UniqueRegistryKey.Type getRemovedEntityType() {
        return UniqueRegistryKey.Type.CLASSIFIER;
    }

    @Override
    public UniqueRegistryKey.Type getLinkedEntityType() {
        return UniqueRegistryKey.Type.LOOKUP_ENTITY;
    }
}
