/**
 *
 */
package org.unidata.mdm.data.type.exchange;

import java.io.Serializable;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ComplexAttributeExpansion implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 191071160016394539L;
    /**
     * Level of expansion.
     */
    private int level;
    /**
     * Do expand or not.
     */
    private boolean expand;
    /**
     * Index of the nested data record.
     */
    private Integer index;
    /**
     * Name of the key attribute.
     */
    private String keyAttribute;
    /**
     *
     */
    public ComplexAttributeExpansion() {
        super();
    }
    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }
    /**
     * @return the expand
     */
    public boolean isExpand() {
        return expand;
    }
    /**
     * @param expand the expand to set
     */
    public void setExpand(boolean expand) {
        this.expand = expand;
    }
    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }
    /**
     * @param index the index to set
     */
    public void setIndex(Integer index) {
        this.index = index;
    }
    /**
     * @return the keyAttribute
     */
    public String getKeyAttribute() {
        return keyAttribute;
    }
    /**
     * @param keyAttribute the keyAttribute to set
     */
    public void setKeyAttribute(String keyAttribute) {
        this.keyAttribute = keyAttribute;
    }

}
