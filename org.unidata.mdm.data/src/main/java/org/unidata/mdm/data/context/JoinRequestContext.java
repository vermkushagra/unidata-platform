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

/**
 * Join origin to an existing etalon id.
 * @author Mikhail Mikhailov
 */
public class JoinRequestContext
    extends AbstractRecordIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -9109988572739215092L;
    /**
     * Constructor.
     */
    protected JoinRequestContext(JoinRequestContextBuilder b) {
        super(b);
    }
    /**
     * Builder shorthand.
     * @return builder
     */
    public static JoinRequestContextBuilder builder() {
        return new JoinRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class JoinRequestContextBuilder extends AbstractRecordIdentityContextBuilder<JoinRequestContextBuilder> {
        /**
         * Constructor.
         */
        protected JoinRequestContextBuilder() {
            super();
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public JoinRequestContext build() {
            return new JoinRequestContext(this);
        }
    }
}
