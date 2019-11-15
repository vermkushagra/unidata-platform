/**
 *
 */
package org.unidata.mdm.core.type.data;

import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * The validity range.
 */
public interface ValidityRange {

    /**
     * From date. May be null.
     * @return Date or null
     */
    public Date getValidFrom();

    /**
     * To date. May be null.
     * @return Date or null
     */
    public Date getValidTo();

}
