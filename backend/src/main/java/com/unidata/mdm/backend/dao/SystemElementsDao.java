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

package com.unidata.mdm.backend.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;

// TODO: Auto-generated Javadoc
/**
 * Data access object for the system_elements table.
 * @author ilya.bykov
 */
public interface SystemElementsDao {
	
	/**
	 * Creates the.
	 *
	 * @param element
	 *            the element
	 */
	public void create(SystemElementPO element);

	/**
	 * Gets the by id.
	 *
	 * @param id
	 *            the id
	 * @return the by id
	 */
	public SystemElementPO getById(int id);

	/**
	 * Gets the by name and path.
	 *
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 * @return the by name and path
	 */
	public SystemElementPO getByNameAndPath(String name, String path);

	/**
	 * Gets the by path and types.
	 *
	 * @param path
	 *            the path
	 * @param types
	 *            the types
	 * @return the by path and types
	 */
	public List<SystemElementPO> getByPathAndTypes(String path, ElementTypePO... types);

	/**
	 * Gets the by type.
	 *
	 * @param type
	 *            the type
	 * @return the by type
	 */
	public List<SystemElementPO> getByType(ElementTypePO type);

	/**
	 * Delete by id.
	 *
	 * @param id
	 *            the id
	 */
	public void deleteById(int id);

	/**
	 * Delete by name and path.
	 *
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 */
	public void deleteByNameAndPath(String name, String path);
	/**
	 * Delete by name and types.
	 *
	 * @param name
	 *            the name
	 * @param types
	 *            the types
	 */
	public void deleteByNameAndTypes(String name, ElementTypePO... types);
	/**
	 * Delete by path and types.
	 *
	 * @param path
	 *            the path
	 * @param types
	 *            the types
	 */
	public void deleteByPathAndTypes(String path, ElementTypePO... types);

	/**
	 * Clear old.
	 *
	 * @param deleteAfter
	 *            the delete after
	 * @param types
	 *            the types
	 */
	public void clearOld(Date deleteAfter, ElementTypePO... types);

	/**
	 * Update.
	 *
	 * @param element
	 *            the element
	 */
	void update(SystemElementPO element);
	/**
	 * Update class names and remove old cleanse functions.
	 * @param toUpdate what need to be updated.
	 */

	public void removeOldFunctions(Map<Integer, String> toUpdate);

	/**
	 * Delete by class name.
	 *
	 * @param className the class name
	 */
	void deleteByClassName(String className);
}
