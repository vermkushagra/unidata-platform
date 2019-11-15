/**
 *
 */
package org.unidata.mdm.core.po;

/**
 * @author Mikhail Mikhailov
 * Binary data.
 */
public class BinaryLargeObjectPO extends LargeObjectPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "binary_data";
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBinary() {
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCharacter() {
        return false;
    }
}
