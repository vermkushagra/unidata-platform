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

package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;

/**
 * The listener interface for receiving metaModel events.
 * The class that is interested in processing a metaModel
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addMetaModelListener</code> method. When
 * the metaModel event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface MetaModelListener {
	
	/**
	 * Upsert draft.
	 *
	 * @param ctx the ctx
	 * @return the com.unidata.mdm.backend.common.context. update model request context
	 */
	default UpdateModelRequestContext upsertDraft(com.unidata.mdm.backend.common.context.UpdateModelRequestContext ctx){
		return ctx;
	}
	
	/**
	 * Apply model.
	 *
	 * @param ctx the ctx
	 * @return the update model request context
	 */
	default UpdateModelRequestContext applyModel(com.unidata.mdm.backend.common.context.UpdateModelRequestContext ctx) {
		return ctx;
	}

}
