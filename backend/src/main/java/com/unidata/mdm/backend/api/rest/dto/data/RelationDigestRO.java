/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.types.RelationSide;

/**
 * @author Mikhail Mikhailov
 *
 */
public class RelationDigestRO {

    /**
     * Viewport etalon id.
     */
    private String etalonId;
    /**
     * Relation name.
     */
    private String relName;
    /**
     * Direction.
     */
    private RelationSide direction;
    /**
     * Page.
     */
    private int page;
    /**
     * Count on page.
     */
    private int count;
    /**
     * Return total count or not.
     */
    private boolean totalCount;
    /**
     * From date to filter relations versions for.
     */
    private Date from;
    /**
     * To date to filter relations versions for.
     */
    private Date to;
    /**
     * Return fields.
     */
    private List<String> fields;
    /**
     * Constructor.
     */
    public RelationDigestRO() {
        super();
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the relName
     */
    public String getRelName() {
        return relName;
    }

    /**
     * @param relName the relName to set
     */
    public void setRelName(String relName) {
        this.relName = relName;
    }

    /**
     * @return the direction
     */
    public RelationSide getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(RelationSide direction) {
        this.direction = direction;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the totalCount
     */
    public boolean isTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(boolean totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date date) {
        this.from = date;
    }


    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }


    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * @return the fields
     */
    public List<String> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

}
