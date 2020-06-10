package com.unidata.mdm.backend.common.data;

/**
 * @author mikhail
 * Type of calculable, held by the holder.
 */
public enum CalculableType {

	/**
	 * Record.
	 */
	RECORD,
	/**
	 * Relation to.
	 */
	RELATION,
	/**
	 * Time interval info.
	 */
	INFO,
	/**
	 * Attribute.
	 */
	ATTRIBUTE,
    /**
     * Classifier
     */
    CLASSIFIER;
}
