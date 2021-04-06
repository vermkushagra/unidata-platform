/**
 *
 */
package com.unidata.mdm.backend.exchange.def.db;

import java.util.List;

import com.unidata.mdm.backend.exchange.def.RelatesToRelation;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DbRelatesToRelation extends RelatesToRelation {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -4262060830315901999L;
    /**
     * DB URL.
     */
    private List<String> tables;
    /**
     * DB URL.
     */
    private List<String> joins;
    /**
     * Order by elements.
     */
    private String orderBy;
    /**
     * Limit predicate instead of standard (limit, offset).
     * Something like id > ${current offset value}.
     */
    private String limitPredicate;
    /**
     * Clean tables after load/import.
     */
    private boolean cleanAfter;
    /**
     * Drop tables after load/import.
     */
    private boolean dropAfter;
    /**
     * Constructor.
     */
    public DbRelatesToRelation() {
        super();
    }

    /**
     * @return the tables
     */
    public List<String> getTables() {
        return tables;
    }


    /**
     * @param tables the tables to set
     */
    public void setTables(List<String> tables) {
        this.tables = tables;
    }



    /**
     * @return the joins
     */
    public List<String> getJoins() {
        return joins;
    }



    /**
     * @param joins the joins to set
     */
    public void setJoins(List<String> joins) {
        this.joins = joins;
    }



    /**
     * @return the orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }



    /**
     * @param orderBy the orderBy to set
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }


    /**
     * @return the limitPredicate
     */
    public String getLimitPredicate() {
        return limitPredicate;
    }


    /**
     * @param limitPredicate the limitPredicate to set
     */
    public void setLimitPredicate(String limitPredicate) {
        this.limitPredicate = limitPredicate;
    }

    /**
     * @return the cleanAfter
     */
    public boolean isCleanAfter() {
        return cleanAfter;
    }

    /**
     * @param cleanAfter the cleanAfter to set
     */
    public void setCleanAfter(boolean cleanAfter) {
        this.cleanAfter = cleanAfter;
    }

    /**
     * @return the dropAfter
     */
    public boolean isDropAfter() {
        return dropAfter;
    }

    /**
     * @param dropAfter the dropAfter to set
     */
    public void setDropAfter(boolean dropAfter) {
        this.dropAfter = dropAfter;
    }
}
