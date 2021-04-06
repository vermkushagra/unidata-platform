package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;


/**
 * The Class ClsfNodeAttrDTOToROConverter.
 */
public class ClsfNodeAttrDTOToROConverter {
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the clsf node attr RO
	 */
	public static ClsfNodeAttrRO convert(ClsfNodeAttrDTO source) {
		if (source == null) {
			return null;
		}
		ClsfNodeAttrRO target = new ClsfNodeAttrRO();
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setHidden(source.isHidden());
		target.setName(source.getAttrName());
		target.setNullable(source.isNullable());
		target.setReadOnly(source.isReadOnly());
		target.setSearchable(source.isSearchable());
		target.setSimpleDataType(SimpleDataType.valueOf(source.getDataType().name()));
		target.setUnique(source.isUnique());
		target.setValueObj(source.getDefaultValue());
		
		return target;
	}
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<ClsfNodeAttrRO> convert(List<ClsfNodeAttrDTO> source) {
		if (source == null) {
			return null;
		}
		List<ClsfNodeAttrRO> target = new ArrayList<>();
		for (ClsfNodeAttrDTO element : source) {
			target.add(convert(element));
		}
		return target;
	}
}
