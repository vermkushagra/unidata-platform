package com.unidata.mdm.backend.dao;

import java.util.List;

import com.unidata.mdm.backend.po.DQErrorPO;



/**
 * The Interface DQErrorsDao.
 */
public interface DQErrorsDao {

    /**
     * Gets the errors by record id.
     *
     * @param recordID the record id
     * @return the errors by record id
     */
    DQErrorPO getErrorsByRecordID(String recordID);

	/**
	 * Save errors.
	 *
	 * @param errors the errors
	 * @return the int[]
	 */
	int[] saveErrors(List<DQErrorPO> errors);
}
