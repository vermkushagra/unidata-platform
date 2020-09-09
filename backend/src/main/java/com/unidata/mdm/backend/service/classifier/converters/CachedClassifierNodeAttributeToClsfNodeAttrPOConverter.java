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

package com.unidata.mdm.backend.service.classifier.converters;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeArrayAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeSimpleAttribute;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeArrayAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.JaxbUtils;

/**
 * The Class ClsfNodeAttrDTOToPOConverter.
 */
public class CachedClassifierNodeAttributeToClsfNodeAttrPOConverter {

	/** The sdf. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat(JaxbUtils.XSD_DATE_TIME_FORMAT);

	/**
	 * Instantiates a new clsf node attr DTO to PO converter.
	 */
	private CachedClassifierNodeAttributeToClsfNodeAttrPOConverter() {
		super();
	}
	/**
     * Does sets common part for all types of attributes.
     * @param target the target
     * @param source the source
     * @param nodeId the node id
     */
    private static void convert(ClsfNodeAttrPO target, CachedClassifierNodeAttribute source) {
        target.setAttrName(source.getName());
        target.setCreatedAt(new Date());
        target.setCreatedBy(SecurityUtils.getCurrentUserName());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setHidden(source.isHidden());
        target.setReadOnly(source.isReadOnly());
        target.setSearchable(source.isSearchable());
        target.setUnique(source.isUnique());
        target.setNullable(source.isNullable());
        target.setUpdatedAt(null);
        target.setUpdatedBy(null);
        target.setCustomProperties(ClsfCustomPropertyPOToCachedClassifierPropertyConverter.convert(source.getCustomProperties()));
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr PO
     */
    public static ClsfNodeArrayAttrPO convert(CachedClassifierNodeArrayAttribute source) {

        if (source == null) {
            return null;
        }

        ClsfNodeArrayAttrPO target = new ClsfNodeArrayAttrPO();
        convert(target, source);

        if (source.isLookupLink()) {
            target.setLookupEntityType(source.getLookupName());
            target.setLookupEntityCodeAttributeType(source.getDataType().name());
        } else {
            target.setDataType(source.getDataType().name());
        }

        target.setValues(toStringValue(source));
        return target;
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr PO
     */
    public static ClsfNodeSimpleAttrPO convert(CachedClassifierNodeSimpleAttribute source) {

        if (source == null) {
            return null;
        }

        ClsfNodeSimpleAttrPO target = new ClsfNodeSimpleAttrPO();
        convert(target, source);

        if (source.isEnumLink()) {
            target.setEnumDataType(source.getEnumName());
        } else if (source.isLookupLink()) {
            target.setLookupEntityType(source.getLookupName());
            target.setLookupEntityCodeAttributeType(source.getDataType().name());
        } else {
            target.setDataType(source.getDataType().name());
        }

        target.setDefaultValue(toStringValue(source));
        return target;
    }
	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static List<ClsfNodeAttrPO> convert(List<CachedClassifierNodeAttribute> source) {

	    if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}

		List<ClsfNodeAttrPO> target = new ArrayList<>();
		for (CachedClassifierNodeAttribute element : source) {
		    if (element.isArray()) {
		        target.add(convert((CachedClassifierNodeArrayAttribute) element));
		    } else if (element.isSimple()) {
		        target.add(convert((CachedClassifierNodeSimpleAttribute) element));
		    }
		}

		return target;
	}

	/**
     * To string value.
     *
     * @param source the source
     * @return the string
     */
    private static Collection<String> toStringValue(CachedClassifierNodeArrayAttribute source) {

        if (source == null || source.getValues() == null || source.getValues().length == 0) {
            return null;
        }

        Collection<String> target = new ArrayList<>();
        for (Serializable value : source.getValues()) {
            switch (source.getDataType()) {
            case DATE:
            case TIME:
            case TIMESTAMP:
                if (value != null) {
                    target.add(SDF.format((Date) value));
                }
                break;
            case INTEGER:
                if (value != null) {
                    target.add(Long.toString(((Long) value)));
                }
                break;
            case NUMBER:
                if (value != null) {
                    target.add(Double.toString((Double) value));
                }
                break;
            case STRING:
                if (value != null) {
                    target.add((String) value);
                }
                break;
            default:
                break;
            }
        }

        return target;
    }
	/**
	 * To string value.
	 *
	 * @param source the source
	 * @return the string
	 */
	private static String toStringValue(CachedClassifierNodeSimpleAttribute source) {

	    if (source == null || source.getValue() == null) {
			return null;
		}

	    String target = null;
		switch (source.getDataType()) {
		case BLOB:
		case CLOB:
			break;
		case BOOLEAN:
			target = BooleanUtils.toString((Boolean) source.getValue(), "true", "false", null);
			break;
		case DATE:
		case TIME:
		case TIMESTAMP:
			if (source.getValue() != null) {
				target = SDF.format((Date) source.getValue());
			}
			break;
		case INTEGER:
			if (source.getValue() != null) {
				target = Long.toString(((Long) source.getValue()));
			}
			break;
		case NUMBER:
			if (source.getValue() != null) {
				target = Double.toString((Double) source.getValue());
			}
			break;
		case STRING:
			if (source.getValue() != null) {
				target = (String) source.getValue();
			}
			break;
		default:
			break;
		}
		return target;
	}
}
