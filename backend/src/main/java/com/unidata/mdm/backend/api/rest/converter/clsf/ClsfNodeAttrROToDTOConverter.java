package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;


/**
 * The Class ClsfNodeAttrROToDTOConverter.
 */
public class ClsfNodeAttrROToDTOConverter {

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the clsf node attr DTO
	 */
	public static ClsfNodeAttrDTO convert(ClsfNodeAttrRO source) {
		if (source == null) {
			return null;
		}
		ClsfNodeAttrDTO target = new ClsfNodeAttrDTO();
		target.setAttrName(source.getName());
		target.setDataType(DataType.valueOf(source.getSimpleDataType().name()));
		target.setDefaultValue(source.getValue());
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setHidden(source.isHidden());
		target.setNullable(source.isNullable());
		target.setReadOnly(source.isReadOnly());
		target.setSearchable(source.isSearchable());
		target.setUnique(source.isUnique());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<ClsfNodeAttrDTO> convert(List<ClsfNodeAttrRO> source) {
		if (source == null) {
			return null;
		}
		List<ClsfNodeAttrDTO> target = new ArrayList<>();
		for (ClsfNodeAttrRO element : source) {
			target.add(convert(element));
		}
		return target;
	}
}
