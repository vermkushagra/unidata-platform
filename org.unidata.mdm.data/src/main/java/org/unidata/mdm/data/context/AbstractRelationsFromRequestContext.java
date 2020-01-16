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

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Abstract from side request context.
 */
public abstract class AbstractRelationsFromRequestContext<T extends AbstractRelationToRequestContext>
    extends AbstractRecordIdentityContext
    implements RecordIdentityContext {
    /**
     * Constructor.
     * @param b the builder
     */
    protected AbstractRelationsFromRequestContext(AbstractRelationsFromRequestContextBuilder<?> b) {
        super(b);
    }
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8277274116336739520L;
    /**
     * Gets the To side relations.
     * @return map of relations
     */
    public abstract Map<String, List<T>> getRelations();
    /**
     * @author Mikhail Mikhailov
     *
     * @param <X> the concrete builder class
     */
    public abstract static class AbstractRelationsFromRequestContextBuilder<X extends AbstractRelationsFromRequestContextBuilder<X>>
        extends AbstractRecordIdentityContextBuilder<X> {
        /**
         * Constructor.
         */
        public AbstractRelationsFromRequestContextBuilder() {
            super();
        }
        /**
         * Constructor.
         * @param other
         */
        public AbstractRelationsFromRequestContextBuilder(AbstractRelationsFromRequestContext<?> other) {
            super(other);
        }
    }
}
