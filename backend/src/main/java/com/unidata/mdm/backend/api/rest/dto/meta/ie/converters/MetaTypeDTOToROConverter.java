package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaTypeRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;

/**
 * The Class MetaTypeDTOToROConverter.
 * 
 * @author ilya.bykov
 */
public class MetaTypeDTOToROConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta type RO
	 */
	public static MetaTypeRO convert(MetaType source) {
		if (source == null) {
			return null;
		}
		MetaTypeRO target = MetaTypeRO.valueOf(source.name());
		return target;
	}

}
