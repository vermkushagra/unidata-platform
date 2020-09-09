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

import com.unidata.mdm.backend.service.search.Event;


/**
 * The Class ModelApplyAuditAction.
 */
public class ModelApplyAuditAction extends ModelAuditAction {

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#enrichEvent(com.unidata.mdm.backend.service.search.Event, java.lang.Object[])
	 */
	@Override
	public void enrichEvent(Event event, Object... input) {
		event.putDetails("Применение черновика");

	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#isValidInput(java.lang.Object[])
	 */
	@Override
	public boolean isValidInput(Object... input) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.audit.actions.AuditAction#name()
	 */
	@Override
	public String name() {
		return "META_DRAFT_APPLY";
	}

}
