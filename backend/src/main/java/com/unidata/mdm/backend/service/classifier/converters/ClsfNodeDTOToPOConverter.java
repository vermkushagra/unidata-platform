package com.unidata.mdm.backend.service.classifier.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;

/**
 * The Class ClsfNodeDTOToPOConverter.
 */
public class ClsfNodeDTOToPOConverter {
	
	/**
	 * Instantiates a new clsf node DTO to PO converter.
	 */
	private ClsfNodeDTOToPOConverter() {
		super();
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the clsf node PO
	 */
	public static ClsfNodePO convert(ClsfNodeDTO source) {
		if (source == null) {
			return null;
		}
		ClsfNodePO target = new ClsfNodePO();
		target.setChildren(convert(source.getChildren()));
		target.setCode(source.getCode());
		target.setCreatedAt(source.getCreatedAt());
		target.setCreatedBy(source.getCreatedBy());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setNodeAttrs(ClsfNodeAttrDTOToPOConverter.convert(source.getNodeAttrs()));
		target.setNodeId(source.getNodeId());
		target.setParentId(source.getParentId());
		target.setUpdatedAt(source.getUpdatedAt());
		target.setUpdatedBy(source.getUpdatedBy());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<ClsfNodePO> convert(List<ClsfNodeDTO> source) {
		if (source == null) {
			return null;
		}
		List<ClsfNodePO> target = new ArrayList<>();
		for (ClsfNodeDTO element : source) {
			target.add(convert(element));
		}
		return target;
	}
}
