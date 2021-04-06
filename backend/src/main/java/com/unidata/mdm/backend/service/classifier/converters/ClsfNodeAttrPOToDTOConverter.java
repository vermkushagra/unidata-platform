package com.unidata.mdm.backend.service.classifier.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.util.JaxbUtils;


/**
 * The Class ClsfNodeAttrPOToDTOConverter.
 */
public class ClsfNodeAttrPOToDTOConverter {

	/** The Constant SDF. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat(JaxbUtils.XSD_DATE_TIME_FORMAT);

	/**
	 * Instantiates a new clsf node attr PO to DTO converter.
	 */
	private ClsfNodeAttrPOToDTOConverter() {
		super();
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the clsf node attr DTO
	 */
	public static ClsfNodeAttrDTO convert(ClsfNodeAttrPO source, int nodeId) {
		if (source == null) {
			return null;
		}
		ClsfNodeAttrDTO target = new ClsfNodeAttrDTO();
		target.setAttrName(source.getAttrName());
		target.setCreatedAt(source.getCreatedAt());
		target.setCreatedBy(source.getCreatedBy());
		target.setDataType(DataType.valueOf(source.getDataType()));
		target.setDefaultValue(fromStringValue(target.getDataType(), source.getDefaultValue()));
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setHidden(source.isHidden());
		target.setNullable(source.isNullable());
		target.setReadOnly(source.isReadOnly());
		target.setSearchable(source.isSearchable());
		target.setUnique(source.isUnique());
		target.setUpdatedAt(source.getUpdatedAt());
		target.setUpdatedBy(source.getUpdatedBy());
		target.setInherited(nodeId!=source.getNodeId());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @param nodeId 
	 * @return the list
	 */
	public static List<ClsfNodeAttrDTO> convert(List<ClsfNodeAttrPO> source, int nodeId) {
		if (source == null) {
			return null;
		}
		List<ClsfNodeAttrDTO> target = new ArrayList<>();
		for (ClsfNodeAttrPO element : source) {
			target.add(convert(element, nodeId));
		}
		return target;
	}

	/**
	 * From string value.
	 *
	 * @param dataType the data type
	 * @param value the value
	 * @return the object
	 */
	private static Object fromStringValue(DataType dataType, String value) {
		if (value == null || dataType == null) {
			return null;
		}
		Object target = null;
		switch (dataType) {
		case BLOB:
		case CLOB:
			break;
		case BOOLEAN:
			target = BooleanUtils.toBooleanObject(value);
			break;
		case DATE:
		case TIME:
		case TIMESTAMP:
			if (!StringUtils.isEmpty(value)) {
				try {
					target = SDF.parse(value);
				} catch (ParseException e) {
					throw new SystemRuntimeException(
							"Incorrect date format. Supported date format is " + JaxbUtils.XSD_DATE_TIME_FORMAT,
							ExceptionId.EX_DATA_CANNOT_PARSE_DATE);
				}
			}
			break;
		case INTEGER:
			if (!StringUtils.isEmpty(value)) {
				target = Long.parseLong(value);
			}
			break;
		case NUMBER:
			if (!StringUtils.isEmpty(value)) {
				target = Double.parseDouble(value);
			}
			break;
		case STRING:
			target = value;
			break;
		default:
			break;
		}
		return target;

	}
}
