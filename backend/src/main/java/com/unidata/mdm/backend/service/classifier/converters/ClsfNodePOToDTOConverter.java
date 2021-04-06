package com.unidata.mdm.backend.service.classifier.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;


/**
 * The Class ClsfNodePOToDTOConverter.
 */
public class ClsfNodePOToDTOConverter {
	
	/**
	 * Instantiates a new clsf node PO to DTO converter.
	 */
	private ClsfNodePOToDTOConverter() {
		super();
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the clsf node DTO
	 */
	public static ClsfNodeDTO convert(ClsfNodePO source) {
		if (source == null) {
			return null;
		}
		ClsfNodeDTO target = new ClsfNodeDTO();
		target.setChildren(convert(source.getChildren()));
		target.setCode(source.getCode());
		target.setCreatedAt(source.getCreatedAt());
		target.setCreatedBy(source.getCreatedBy());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setNodeAttrs(ClsfNodeAttrPOToDTOConverter.convert(source.getNodeAttrs(), source.getId()));
		target.setNodeId(source.getNodeId());
		target.setParentId(source.getParentId());
		target.setUpdatedAt(source.getUpdatedAt());
		target.setUpdatedBy(source.getUpdatedBy());
		target.setChildCount(source.getChildCount());
		target.setHasOwnAttrs(source.isHasOwnAttrs());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<ClsfNodeDTO> convert(List<ClsfNodePO> source) {
		if (source == null) {
			return null;
		}
		List<ClsfNodeDTO> target = new ArrayList<>();
		for (ClsfNodePO element : source) {
			target.add(convert(element));
		}
		return target;
	}
}
