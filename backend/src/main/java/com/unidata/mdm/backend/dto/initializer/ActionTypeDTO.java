package com.unidata.mdm.backend.dto.initializer;

/**
 * Action type data transfer object.
 * 
 * @author ilya.bykov
 *
 */
public enum ActionTypeDTO {
	/** The create. */
	CREATE,

	/** The overwrite. */
	OVERWRITE,

	/** The delete. */
	DELETE,

	/** The do nothing. */
	DO_NOTHING,

	/** The validate. */
	VALIDATE
}
