package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaTypeRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;

/**
 * The Class MetaTypeROToDTOConverter.
 * 
 * @author ilya.bykov
 */
public class MetaTypeROToDTOConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static Set<MetaType> convert(List<MetaTypeRO> source) {
		if (source == null) {
			return null;
		}
		Set<MetaType> target = new HashSet<>();
		for (MetaTypeRO metaTypeRo : source) {
			target.add(convert(metaTypeRo));
		}
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta type
	 */
	public static MetaType convert(MetaTypeRO source) {
		if (source == null) {
			return null;
		}
		MetaType target = MetaType.valueOf(source.name());
		return target;
	}
}
