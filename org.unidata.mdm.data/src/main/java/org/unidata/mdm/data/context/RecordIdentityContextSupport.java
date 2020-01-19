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

package org.unidata.mdm.data.context;

import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 * A utility class to be implemented by listeners.
 */
public interface RecordIdentityContextSupport {
    /**
     * Selects name either from context or from keys.
     * @param ctx context
     * @return name or null
     */
    default String selectEntityName(RecordIdentityContext ctx) {

        if (Objects.nonNull(ctx)) {

            if (ctx.getEntityName() != null) {
                return ctx.getEntityName();
            } else {
                if (ctx.keys() != null) {

                    if (ctx.keys().getEntityName() != null) {
                        return ctx.keys().getEntityName();
                    }

                    return ctx.keys().getOriginKey() != null
                            ? ctx.keys().getOriginKey().getEntityName()
                            : null;
                }
            }
        }

        return null;
    }
}
