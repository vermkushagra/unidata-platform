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

package org.unidata.mdm.meta.dao;

import java.util.List;

import org.unidata.mdm.meta.po.MetaDraftPO;

/**
 * The Interface MetaDraftDao.
 */
public interface MetaDraftDao {
	
	/**
	 * Creates the.
	 *
	 * @param source the source
	 * @return true, if successful
	 */
	boolean create(MetaDraftPO source);

	/**
	 * Update.
	 *
	 * @param source the source
	 * @return true, if successful
	 */
	boolean update(MetaDraftPO source);

	/**
	 * Delete all active drafts
	 *
	 * @param storageId the storage id - UNUSABLE now
	 * @return true, if successful
	 */
	boolean deleteActiveDraft(String storageId);

	/**
	 * Read.
	 *
	 * @param source the source
	 * @return the list
	 */
	List<MetaDraftPO> read(MetaDraftPO source);

	/**
	 * Current draft.
	 *
	 * @param storageId the storage id is unused now
	 * @return the list
	 */
	List<MetaDraftPO> currentDraft(String storageId);

	/**
	 * Checks if is draft exist.
	 *
	 * @param storageId the storage id
	 * @return true, if is draft exist
	 */
	boolean isDraftExist(String storageId);
	
	/**
	 * Gets the last version.
	 *
	 * @param storageId the storage id - UNUSABLE now
	 * @return the last version
	 */
	long getLastVersion(String storageId);

}
