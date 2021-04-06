package com.unidata.mdm.backend.api.rest.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.unidata.mdm.backend.api.rest.constants.Constants;

@XmlRootElement(name="PageRequest")
public class RestPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private int page;
    private int size;
    private Sort sort;

    // ==============================================================

    public RestPageRequest() {
        this.page = 1;
        this.size = Constants.REST_DEFAULT_PAGE_SIZE;
    }

    @XmlTransient
    public PageRequest getPageRequest() {
        return new PageRequest(page-1, size, sort);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("page", page)
                .append("size", size)
                .toString();
    }

    // Getters & Setters ============================================

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
