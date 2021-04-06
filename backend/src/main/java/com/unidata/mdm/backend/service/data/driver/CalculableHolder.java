/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

import java.util.Date;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Holder
 */
public interface CalculableHolder<T> {
    /**
     * @return the relation
     */
    public T getValue();
    /**
     * @return the name
     */
    public String getTypeName();
    /**
     * @return the sourceSystem
     */
    public String getSourceSystem();
    /**
     * @return the external id (if present)
     */
    public String getExternalId();
    /**
     * @return the status
     */
    public RecordStatus getStatus();
    /**
     * @return the calculable type
     */
    public CalculableType getCalculableType();

    /**
     * @return the last update date
     */
    public Date getLastUpdate();
}
