/**
 *
 */
package com.unidata.mdm.backend.exchange.def.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unidata.mdm.backend.exchange.def.SystemKey;


/**
 * @author Mikhail Mikhailov
 * DB natural key.
 */
public class DbSystemKey extends SystemKey {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1818610311206266144L;
    /**
     * Column name.
     */
    private String column;
    /**
     * Select alias.
     */
    private String alias;
    /**
     * Hack.
     */
    private String sqlAdditionLeft;
    /**
     * Hack.
     */
    private String sqlAdditionRight;
    /**
     * Constructor.
     */
    public DbSystemKey() {
        super();
    }

    /**
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * @return the typeClazz
     */
    @JsonIgnore
    public Class<?> getTypeClazz() {
        return String.class;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the sqlAdditionLeft
     */
    public String getSqlAdditionLeft() {
        return sqlAdditionLeft;
    }

    /**
     * @param sqlAdditionLeft the sqlAdditionLeft to set
     */
    public void setSqlAdditionLeft(String sqlAdditionLeft) {
        this.sqlAdditionLeft = sqlAdditionLeft;
    }

    /**
     * @return the sqlAdditionRight
     */
    public String getSqlAdditionRight() {
        return sqlAdditionRight;
    }

    /**
     * @param sqlAdditionRight the sqlAdditionRight to set
     */
    public void setSqlAdditionRight(String sqlAdditionRight) {
        this.sqlAdditionRight = sqlAdditionRight;
    }
}
