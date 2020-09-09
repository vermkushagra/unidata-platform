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

package com.unidata.mdm.backend.service.bulk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.types.BulkOperationType;
import org.apache.commons.collections4.CollectionUtils;

public class RemoveRelationsFromConfiguration extends BulkOperationConfiguration {

    private final List<String> relationsNames = new ArrayList<>();

    public RemoveRelationsFromConfiguration(final Collection<String> relationsNames) {
        super(BulkOperationType.REMOVE_RELATIONS_FROM);
        if (CollectionUtils.isNotEmpty(relationsNames)) {
            this.relationsNames.addAll(relationsNames);
        }
    }

    public List<String> getRelationsNames() {
        return Collections.unmodifiableList(relationsNames);
    }
}
