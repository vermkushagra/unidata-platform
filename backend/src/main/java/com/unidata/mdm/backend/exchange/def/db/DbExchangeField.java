package com.unidata.mdm.backend.exchange.def.db;

import java.sql.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unidata.mdm.backend.exchange.def.ExchangeField;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DbExchangeField extends ExchangeField {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7581999574099928870L;

    /**
     * Column name.
     */
    private String column;

    /**
     * SQL alias name.
     */
    private String alias;

    /**
     * Column type.
     */
    private String type;
    /**
     * Type class.
     */
    @JsonIgnore
    private Class<?> typeClazz;
    /**
     * Constructor.
     */
    public DbExchangeField() {
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
             && typeClazz != Double.class
             && typeClazz != Date.class
             && typeClazz != java.util.Date.class
             && typeClazz != Time.class
             && typeClazz != Timestamp.class
             && typeClazz != Array.class) {
                System.err.println("Type '" + typeClazz.getName() + "' is not supported as conversion type!");
                throw new IllegalArgumentException("Type '" + typeClazz.getName() + "' is not supported as conversion type!");
            }
        } catch (Throwable th) {
            System.err.println("Cannot initialize class for type '" + type +
                    "'. Class is either not allowed or not found. Value conversion will be not functioning!");
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
        return alias == null ? getName() : alias;
    }


    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
