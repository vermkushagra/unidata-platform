package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaStatusRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaStatus;


/**
 * The Class MetaStatusROToDTOConverter.
 * @author ilya.bykovF
 */
public class MetaStatusROToDTOConverter {
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the meta status
	 */
	public static MetaStatus convert(MetaStatusRO source) {
		if (source == null) {
			return null;
		}
		MetaStatus target = MetaStatus.valueOf(source.name());
		return target;
	}
}
