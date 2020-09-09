/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.meta.BVRMergeSettingsDefRO;
import com.unidata.mdm.backend.api.rest.dto.meta.BVTMergeSettingsDefRO;
import com.unidata.mdm.backend.api.rest.dto.meta.MergeAttributeDefRO;
import com.unidata.mdm.backend.api.rest.dto.meta.MergeSettingsRO;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.BVRMergeTypeDef;
import com.unidata.mdm.meta.BVTMergeTypeDef;
import com.unidata.mdm.meta.MergeAttributeDef;
import com.unidata.mdm.meta.MergeSettingsDef;

/**
 * @author mikhail
 * Merge settings converter.
 */
public class MergeSettingsConverter {
	
	/**
	 * Converts to RO.
	 * @param source the conversion source
	 * @return RO
	 */
	public static MergeSettingsRO to(MergeSettingsDef source) {
		
		if (source == null) {
			return null;
		}

		MergeSettingsRO target = new MergeSettingsRO();
		if (source.getBvrSettings() != null) {

			BVRMergeSettingsDefRO bvrRo = new BVRMergeSettingsDefRO();
			bvrRo.getSourceSystemsConfig().addAll(
					SourceSystemConverter.to(source.getBvrSettings().getSourceSystemsConfigs()));

			target.setBvrMergeSettings(bvrRo);
		}
		
		if (source.getBvtSettings() != null) {
			BVTMergeSettingsDefRO bvtRo = new BVTMergeSettingsDefRO();			
			List<MergeAttributeDef> attributes = source.getBvtSettings().getAttributes();
			for (int i = 0; attributes != null && i < attributes.size(); i++) {
				MergeAttributeDef attDef = attributes.get(i);
				MergeAttributeDefRO attRo = new MergeAttributeDefRO();
				attRo.setName(attDef.getName());
				attRo.setSourceSystemsConfig(SourceSystemConverter.to(attDef.getSourceSystemsConfigs()));
				bvtRo.getAttributes().add(attRo);
			}
				
			target.setBvtMergeSettings(bvtRo);
		}
		
		return target;
	}
	
	/**
	 * Converts to system format.
	 * @param source the conversion source
	 * @return system object
	 */
	public static MergeSettingsDef from(MergeSettingsRO source) {
		if (source == null) {
			return null;
		}
		
		MergeSettingsDef target = JaxbUtils.getMetaObjectFactory().createMergeSettingsDef();
		if (source.getBvrMergeSettings() != null
		 && source.getBvrMergeSettings().getSourceSystemsConfig() != null) {

			BVRMergeTypeDef bvrType = JaxbUtils.getMetaObjectFactory().createBVRMergeTypeDef();
			bvrType.getSourceSystemsConfigs().addAll(
					SourceSystemConverter.from(
							source.getBvrMergeSettings().getSourceSystemsConfig()));

			target.setBvrSettings(bvrType);
		}
		
		if (source.getBvtMergeSettings() != null) {
			BVTMergeTypeDef bvtType = JaxbUtils.getMetaObjectFactory().createBVTMergeTypeDef();			
			List<MergeAttributeDefRO> attributes = source.getBvtMergeSettings().getAttributes();
			for (int i = 0; attributes != null && i < attributes.size(); i++) {
				MergeAttributeDefRO attRo = attributes.get(i);
				MergeAttributeDef attDef = JaxbUtils.getMetaObjectFactory().createMergeAttributeDef();
				attDef.setName(attRo.getName());
				attDef.setSourceSystemsConfigs(SourceSystemConverter.from(attRo.getSourceSystemsConfig()));
				bvtType.getAttributes().add(attDef);
			}
				
			target.setBvtSettings(bvtType);
		}

		return target;
	}
}
