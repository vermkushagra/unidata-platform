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
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;


/**
 * The Class ModelUpsertAuditAction.
 */
public class ModelUpsertAuditAction extends ModelAuditAction{

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#enrichEvent(com.unidata.mdm.backend.service.search.Event, java.lang.Object[])
	 */
	@Override
	public void enrichEvent(Event event, Object... input) {
		UpdateModelRequestContext ctx = (UpdateModelRequestContext) input[0];
		StringBuilder sb = new StringBuilder();
		sb.append("Обновление черновика метамодели. ");
		if (ctx.hasSourceSystemsUpdate()) {
			sb.append("\nСистемы источники: [");
			List<String> names = ctx.getSourceSystemsUpdate().stream().map(SourceSystemDef::getName)
					.collect(Collectors.toList());
			sb.append(String.join(" ,", names));
			sb.append("]");
		}

		if(ctx.hasEntityUpdate()) {
			sb.append("\nРеестры: [");
			List<String> names = ctx.getEntityUpdate().stream().map(EntityDef::getName)
					.collect(Collectors.toList());
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasEnumerationUpdate()) {
			sb.append("\nПеречисления: [");
			List<String> names = ctx.getEnumerationsUpdate().stream().map(EnumerationDataType::getName)
					.collect(Collectors.toList());
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasLookupEntityUpdate()) {
			sb.append("\nСправочники: [");
			List<String> names = ctx.getLookupEntityUpdate().stream().map(LookupEntityDef::getName)
					.collect(Collectors.toList());
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasNestedEntityUpdate()) {
			sb.append("\nВложенные сущности: [");
			List<String> names = ctx.getNestedEntityUpdate().stream().map(NestedEntityDef::getName)
					.collect(Collectors.toList());
			sb.append(String.join(" ,", names));
			sb.append("]");
		}
		if(ctx.hasRelationsUpdate()) {
			sb.append("\nСвязи: [");
			List<String> names = ctx.getRelationsUpdate().stream().map(RelationDef::getName)
					.collect(Collectors.toList());
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
		return input.length == 1 && input[0] instanceof UpdateModelRequestContext;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#name()
	 */
	@Override
	public String name() {
		return "MODEL_UPSERT";
	}

}
