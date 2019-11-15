/**
 * 
 */
package org.unidata.mdm.soap.data.exception;

/**
 * @author mikhail
 * Exit state used to indicate the reason for the exception.
 */
@Deprecated
public enum ExitState {
	/**
	 * Data validation failed.
	 */
	ES_VALIDATION_ERROR,
	/**
	 * Upsert denied. No changes needed for some reason.
	 */
	ES_UPSERT_DENIED,
	/**
	 * Foreign system failed.
	 */
	ES_FOREIGN_SYSTEM_FAILURE,
	/**
	 * General failure.
	 */
	ES_GENERAL_FAILURE;
}
