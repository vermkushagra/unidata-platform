package com.unidata.mdm.backend.service.classifier.converters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.util.JaxbUtils;

/**
 * The Class ClsfNodeAttrDTOToPOConverter.
 */
public class ClsfNodeAttrDTOToPOConverter {
	
	/** The sdf. */
	private static SimpleDateFormat SDF = new SimpleDateFormat(JaxbUtils.XSD_DATE_TIME_FORMAT);

	/**
	 * Instantiates a new clsf node attr DTO to PO converter.
	 */
	private ClsfNodeAttrDTOToPOConverter() {
		super();
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the clsf node attr PO
	 */
	public static ClsfNodeAttrPO convert(ClsfNodeAttrDTO source) {
		if (source == null) {
			return null;
		}
		ClsfNodeAttrPO target = new ClsfNodeAttrPO();
		target.setAttrName(source.getAttrName());
		target.setCreatedAt(source.getCreatedAt());
		target.setCreatedBy(source.getCreatedBy());
		target.setDataType(source.getDataType().name());
		target.setDefaultValue(toStringValue(source));
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setHidden(source.isHidden());
		target.setReadOnly(source.isReadOnly());
		target.setSearchable(source.isSearchable());
		target.setUnique(source.isUnique());
		target.setNullable(source.isNullable());
		target.setUpdatedAt(source.getUpdatedAt());
		target.setUpdatedBy(source.getUpdatedBy());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static List<ClsfNodeAttrPO> convert(List<ClsfNodeAttrDTO> source) {
		if (source == null) {
			return null;
		}
		List<ClsfNodeAttrPO> target = new ArrayList<>();
		for (ClsfNodeAttrDTO element : source) {
			target.add(convert(element));
		}
		return target;
	}

	/**
	 * To string value.
	 *
	 * @param source the source
	 * @return the string
	 */
	private static String toStringValue(ClsfNodeAttrDTO source) {
		if (source == null || source.getDefaultValue() == null) {
			return null;
		}
		String target = null;
		switch (source.getDataType()) {
		case BLOB:
		case CLOB:
			break;
		case BOOLEAN:
			target = BooleanUtils.toString((Boolean) source.getDefaultValue(), "true", "false", null);
			break;
		case DATE:
		case TIME:
		case TIMESTAMP:
			if (source.getDefaultValue() != null) {
				target = SDF.format((Date) source.getDefaultValue());
			}
			break;
		case INTEGER:
			if (source.getDefaultValue() != null) {
				target = Long.toString(((Long) source.getDefaultValue()));
			}
			break;
		case NUMBER:
			if (source.getDefaultValue() != null) {
				target = Double.toString((Double) source.getDefaultValue());
			}
			break;
		case STRING:
			if (source.getDefaultValue() != null) {
				target = (String) source.getDefaultValue();
			}
			break;
		default:
			break;
		}
		return target;
	}
}
