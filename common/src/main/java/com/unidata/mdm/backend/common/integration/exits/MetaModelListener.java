package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;

/**
 * The listener interface for receiving metaModel events.
 * The class that is interested in processing a metaModel
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addMetaModelListener</code> method. When
 * the metaModel event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface MetaModelListener {
	
	/**
	 * Upsert draft.
	 *
	 * @param ctx the ctx
	 * @return the com.unidata.mdm.backend.common.context. update model request context
	 */
	default UpdateModelRequestContext upsertDraft(com.unidata.mdm.backend.common.context.UpdateModelRequestContext ctx){
		return ctx;
	}
	
	/**
	 * Apply model.
	 *
	 * @param ctx the ctx
	 * @return the update model request context
	 */
	default UpdateModelRequestContext applyModel(com.unidata.mdm.backend.common.context.UpdateModelRequestContext ctx) {
		return ctx;
	}

}
