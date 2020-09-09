/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.data.xlsximport;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.meta.SimpleDataType;

// TODO: Auto-generated Javadoc
/**
 * The Class XLSXHeader.
 */
public class XLSXHeader {

	/** The system header. */
	private String systemHeader;

	/** The hr header. */
	private String hrHeader;

	/** The type header. */
	private SimpleDataType typeHeader;

	/** The order. */
	private int order;

	/** Is this header mandatory?. */
	private boolean isMandatory;

	/** The type. */
	private TYPE type;
	
	/** The rel. */
	private boolean rel;
	/**
	 * Attribute holder.
	 */
	private AttributeInfoHolder attributeHolder;
	/**
	 * Gets the system header.
	 *
	 * @return the system header
	 */
	public String getSystemHeader() {
		return systemHeader;
	}

	/**
	 * Sets the system header.
	 *
	 * @param systemHeader
	 *            the new system header
	 */
	public void setSystemHeader(String systemHeader) {
		this.systemHeader = systemHeader;
	}

	/**
	 * Gets the hr header.
	 *
	 * @return the hr header
	 */
	public String getHrHeader() {
		return hrHeader;
	}

	/**
	 * Sets the hr header.
	 *
	 * @param hrHeader
	 *            the new hr header
	 */
	public void setHrHeader(String hrHeader) {
		this.hrHeader = hrHeader;
	}

	/**
	 * Gets the type header.
	 *
	 * @return the type header
	 */
	public SimpleDataType getTypeHeader() {
		return typeHeader;
	}

	/**
	 * Sets the type header.
	 *
	 * @param typeHeader
	 *            the new type header
	 */
	public void setTypeHeader(SimpleDataType typeHeader) {
		this.typeHeader = typeHeader;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order.
	 *
	 * @param order
	 *            the new order
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Gets the attribute holder.
	 *
	 * @return the attributeHolder
	 */
    public AttributeInfoHolder getAttributeHolder() {
        return attributeHolder;
    }

    /**
     * Sets the attribute holder.
     *
     * @param attributeHolder the attributeHolder to set
     */
    public void setAttributeHolder(AttributeInfoHolder attributeHolder) {
        this.attributeHolder = attributeHolder;
    }

    /**
	 * With order.
	 *
	 * @param order
	 *            the order
	 * @return the XLSX header
	 */
	public XLSXHeader withOrder(int order) {
		this.order = order;
		return this;
	}

	/**
	 * With hr header.
	 *
	 * @param hrHeader
	 *            the hr header
	 * @return the XLSX header
	 */
	public XLSXHeader withHrHeader(String hrHeader) {
		this.hrHeader = hrHeader;
		return this;
	}

	/**
	 * With system header.
	 *
	 * @param systemHeader
	 *            the system header
	 * @return the XLSX header
	 */
	public XLSXHeader withSystemHeader(String systemHeader) {
		this.systemHeader = systemHeader;
		return this;
	}

	/**
	 * With type header.
	 *
	 * @param typeHeader
	 *            the type header
	 * @return the XLSX header
	 */
	public XLSXHeader withTypeHeader(SimpleDataType typeHeader) {
		this.typeHeader = typeHeader;
		return this;
	}

	/**
     * With attribute holder.
     *
     * @param attributeHolder
     *            the attribute holder
     * @return the XLSX header
     */
    public XLSXHeader withAttributeHolder(AttributeInfoHolder attributeHolder) {
        this.attributeHolder = attributeHolder;
        return this;
    }

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(TYPE type) {
		this.type = type;
	}

	/**
	 * With type.
	 *
	 * @param type
	 *            the type
	 * @return the XLSX header
	 */
	public XLSXHeader withType(TYPE type) {
		this.type = type;
		return this;
	}

	/**
	 * Checks if is mandatory.
	 *
	 * @return true, if is mandatory
	 */
	public boolean isMandatory() {
		return isMandatory;
	}

	/**
	 * Sets the mandatory.
	 *
	 * @param isMandatory
	 *            the new mandatory
	 */
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	/**
	 * With is mandatory.
	 *
	 * @param isMandatory
	 *            the is mandatory
	 * @return the XLSX header
	 */
	public XLSXHeader withIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
		return this;
	}

	
    /**
     * Checks if is rel.
     *
     * @return true, if is rel
     */
    public boolean isRel() {
        return rel;
    }

    
    /**
     * Sets the rel.
     *
     * @param rel the new rel
     */
    public XLSXHeader withIsRel(boolean rel) {
        this.rel = rel;
        return this;
    }

    /**
	 * The Enum TYPE.
	 */
	public enum TYPE {

		/** The classifier node. */
		CLASSIFIER_NODE,
		/**
		 * Relation.
		 */
		RELATION,
		/** The classfier attribute. */
		CLASSIFIER_ATTRIBUTE,
		/** The data attribute. */
		DATA_ATTRIBUTE,
		/** The system. */
		SYSTEM
	}
}
