package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaActionRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaAction;


/**
 * The Class MetaActionDTOToROConverter.
 * @author ilya.bykov
 */
public class MetaActionDTOToROConverter {
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the meta action RO
	 */
	public static MetaActionRO convert(MetaAction source) {
		if (source == null) {
			return null;
		}
		MetaActionRO target = MetaActionRO.valueOf(source.name());
		return target;
	}
}
