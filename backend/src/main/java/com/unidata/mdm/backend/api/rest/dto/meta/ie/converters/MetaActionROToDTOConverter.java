package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaActionRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaAction;


/**
 * The Class MetaActionROToDTOConverter.
 * @author ilya.bykov
 */
public class MetaActionROToDTOConverter {

/**
 * Convert.
 *
 * @param source the source
 * @return the meta action
 */
public static MetaAction convert(MetaActionRO source){
	if(source==null){
		return null;
	}
	MetaAction target = MetaAction.valueOf(source.name());
	return target;
}
}
