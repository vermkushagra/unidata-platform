/**
 *
 */
package com.unidata.mdm.backend.service.data.listener;

import com.unidata.mdm.backend.common.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Data record AFTER action support.
 */
public interface DataRecordAfterExecutor<T extends CommonRequestContext>  extends DataRecordExecutor<T>{

}
