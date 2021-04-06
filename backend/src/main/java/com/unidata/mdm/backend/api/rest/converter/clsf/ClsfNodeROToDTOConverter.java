package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;


/**
 * The Class ClsfNodeROToDTOConverter.
 */
public class ClsfNodeROToDTOConverter {
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the clsf node DTO
	 */
	public static ClsfNodeDTO convert(ClsfNodeRO source) {
		if (source == null) {
			return null;
		}
		ClsfNodeDTO target = new ClsfNodeDTO();
		target.setChildren(convert(source.getChildren()));
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setNodeAttrs(ClsfNodeAttrROToDTOConverter.convert(source.getNodeAttrs()));
		target.setNodeId(source.getId());
		target.setParentId(source.getParentId());
		return target;
	}
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<ClsfNodeDTO> convert(List<ClsfNodeRO> source) {
		if (source == null) {
			return null;
		}
		List<ClsfNodeDTO> target = new ArrayList<>();
		for (ClsfNodeRO element : source) {
			target.add(convert(element));
		}
		return target;
	}
}
