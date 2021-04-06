/**
 *
 */
package com.unidata.mdm.backend.exchange.def.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unidata.mdm.backend.exchange.def.NaturalKey;


/**
 * @author Mikhail Mikhailov
 * DB natural key.
 */
public class DbNaturalKey extends NaturalKey {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5572681031691583097L;
    /**
     * Column name.
     */
    private String column;
    /**
     * Column type.
     */
    private String type;
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
     * Type class.
     */
    @JsonIgnore
    private Class<?> typeClazz;

    /**
     * Constructor.
     */
    public DbNaturalKey() {
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
     * @return the type
     */
    public String getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
        try {
            typeClazz = Class.forName(type);
            if (typeClazz != Boolean.class
             && typeClazz != String.class
             && typeClazz != Long.class
             && typeClazz != Integer.class
             && typeClazz != Float.class
             && typeClazz != Double.class) {
                System.err.println("Type '" + typeClazz.getName() + "' is not supported as natural key type!");
                throw new IllegalArgumentException("Type '" + typeClazz.getName() + "' is not supported as natural key type!");
            }
        } catch (Throwable th) {
            System.err.println("Cannot initialize value type for type '" + "'. Natural key will be not functionaing!");
            typeClazz = null;
        }
    }


    /**
     * @return the typeClazz
     */
    @JsonIgnore
    public Class<?> getTypeClazz() {
        return typeClazz;
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
