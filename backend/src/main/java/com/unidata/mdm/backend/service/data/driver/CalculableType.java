/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

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
	RELATION_TO,
	/**
	 * Time interval.
	 */
	TIME_INTERVAL,
	/**
	 * Attribute.
	 */
	ATTRIBUTE,
    /**
     * Classifier
     */
    CLASSIFIER;
}
