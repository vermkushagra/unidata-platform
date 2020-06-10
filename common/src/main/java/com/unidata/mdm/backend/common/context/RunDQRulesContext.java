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

package com.unidata.mdm.backend.common.context;

import java.util.List;

public class RunDQRulesContext {
    final List<String> etalonRecordsIds;
    final String entityName;
    final boolean sandbox;
    final List<String> rules;

    public RunDQRulesContext(
            final List<String> etalonRecordsIds,
            final String entityName,
            final boolean sandbox,
            final List<String> rules
    ) {
        this.etalonRecordsIds = etalonRecordsIds;
        this.entityName = entityName;
        this.sandbox = sandbox;
        this.rules = rules;
    }

    public List<String> getEtalonRecordsIds() {
        return etalonRecordsIds;
    }

    public String getEntityName() {
        return entityName;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public List<String> getRules() {
        return rules;
    }
}
