package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaExistenceRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaExistence;

/**
 * The Class MetaStatusROToDTOConverter.
 * @author ilya.bykov
 */
public class MetaExistenceROToDTOConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta type
	 */
	public static MetaExistence convert(MetaExistenceRO source) {
		if (source == null) {
			return null;
		}
		MetaExistence target = MetaExistence.valueOf(source.name());
		return target;
	}
}
