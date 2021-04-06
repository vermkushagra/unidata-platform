package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaExistenceRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaExistence;

/**
 * The Class MetaStatusDTOToROConverter.
 * @author ilya.bykov
 */
public class MetaExistenceDTOToROConverter {
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the meta status RO
	 */
	public static MetaExistenceRO convert(MetaExistence source) {
		if (source == null) {
			return null;
		}
		MetaExistenceRO target = MetaExistenceRO.valueOf(source.name());
		return target;
	}
}
