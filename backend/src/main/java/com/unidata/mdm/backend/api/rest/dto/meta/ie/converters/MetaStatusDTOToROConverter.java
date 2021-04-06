package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaStatusRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaStatus;

/**
 * The Class MetaStatusDTOToROConverter.
 * 
 * @author ilya.bykov
 */
public class MetaStatusDTOToROConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta status RO
	 */
	public static MetaStatusRO convert(MetaStatus source) {
		if (source == null) {
			return null;
		}
		MetaStatusRO target = MetaStatusRO.valueOf(source.name());
		return target;
	}
}
