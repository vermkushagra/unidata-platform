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

package com.unidata.mdm.backend.service.audit.actions.impl.model;

import java.util.List;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.service.search.Event;


/**
 * The Class ModelDeleteAuditAction.
 */
public class ModelDeleteAuditAction extends ModelAuditAction{

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#enrichEvent(com.unidata.mdm.backend.service.search.Event, java.lang.Object[])
	 */
	@Override
	public void enrichEvent(Event event, Object... input) {
		DeleteModelRequestContext ctx = (DeleteModelRequestContext) input[0];
		StringBuilder sb = new StringBuilder();
		sb.append("Удаление элемента черновика метамодели. ");
		if (ctx.hasSourceSystemIds()) {
			sb.append("\nСистемы источники: [");
			List<String> names = ctx.getSourceSystemIds();
			sb.append(String.join(" ,", names));
			sb.append("]");
		}

		if(ctx.hasEntitiesIds()) {
			sb.append("\nРеестры: [");
			List<String> names = ctx.getEntitiesIds();
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasEnumerationIds()) {
			sb.append("\nПеречисления: [");
			List<String> names = ctx.getEnumerationIds();
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasLookupEntitiesIds()) {
			sb.append("\nСправочники: [");
			List<String> names = ctx.getLookupEntitiesIds();
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasNestedEntitiesIds()) {
			sb.append("\nВложенные сущности: [");
			List<String> names = ctx.getNestedEntitiesIds();
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasRelationIds()) {
			sb.append("\nСвязи: [");
			List<String> names = ctx.getRelationIds();
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		event.putDetails(sb.toString());
		
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#isValidInput(java.lang.Object[])
	 */
	@Override
	public boolean isValidInput(Object... input) {		
		return input.length == 1 && input[0] instanceof DeleteModelRequestContext;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#name()
	 */
	@Override
	public String name() {
		return "META_DRAFT_DELETE_ELEMENT";
	}

}
