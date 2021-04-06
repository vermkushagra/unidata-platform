package com.unidata.mdm.backend.dao;

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;

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
}
