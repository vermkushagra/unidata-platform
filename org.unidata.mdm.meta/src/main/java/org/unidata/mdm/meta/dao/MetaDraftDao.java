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
