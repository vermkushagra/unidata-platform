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

package com.unidata.mdm.backend.conf.impl;

import com.unidata.mdm.conf.Delete;
import com.unidata.mdm.conf.Join;
import com.unidata.mdm.conf.Merge;
import com.unidata.mdm.conf.Model;
import com.unidata.mdm.conf.ObjectFactory;
import com.unidata.mdm.conf.Search;
import com.unidata.mdm.conf.Split;
import com.unidata.mdm.conf.Upsert;
import com.unidata.mdm.conf.WorkflowProcessDefinition;


/**
 * The Class ObjectFactoryEx.
 *
 * @author Mikhail Mikhailov Object factory for configuration objects.
 */
public class ObjectFactoryEx extends ObjectFactory {

	/**
	 * Constructor.
	 */
	public ObjectFactoryEx() {
		super();
	}

	/**
	 * Creates the delete.
	 *
	 * @return the delete
	 * @see com.unidata.mdm.conf.ObjectFactory#createDelete()
	 */
	@Override
	public Delete createDelete() {
		return new DeleteImpl();
	}

	/**
	 * Creates the merge.
	 *
	 * @return the merge
	 * @see com.unidata.mdm.conf.ObjectFactory#createMerge()
	 */
	@Override
	public Merge createMerge() {
		return new MergeImpl();
	}

	/**
	 * Creates the upsert.
	 *
	 * @return the upsert
	 * @see com.unidata.mdm.conf.ObjectFactory#createUpsert()
	 */
	@Override
	public Upsert createUpsert() {
		return new UpsertImpl();
	}

	/**
	 * Creates the search.
	 *
	 * @return the search
	 * @see com.unidata.mdm.conf.ObjectFactory#createSearch()
	 */
	@Override
	public Search createSearch() {
		return new SearchImpl();
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.conf.ObjectFactory#createJoin()
	 */
	@Override
	public Join createJoin() {
		return new JoinImpl();
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.conf.ObjectFactory#createSplit()
	 */
	@Override
	public Split createSplit() {
		return new SplitImpl();
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.conf.ObjectFactory#createModel()
	 */
	@Override
	public Model createModel() {
		return new ModelImpl();
	}

	/**
	 * Creates the workflow process definition.
	 *
	 * @return the workflow process definition
	 * @see com.unidata.mdm.conf.ObjectFactory#createWorkflowProcessDefinition()
	 */
	@Override
	public WorkflowProcessDefinition createWorkflowProcessDefinition() {
		return new WorkflowProcessDefinitionImpl();
	}
}
