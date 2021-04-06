/**
 *
 */
package com.unidata.mdm.backend.service.data.listener;

import com.unidata.mdm.backend.common.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Data record BEFORE action support.
 */
public interface DataRecordBeforeExecutor<T extends CommonRequestContext> extends DataRecordExecutor<T>{


}
