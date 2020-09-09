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

package com.unidata.mdm.backend.service.classifier.cache;

import java.util.Properties;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;
import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;

/**
 * @author Mikhail Mikhailov
 * Factory.
 */
public class CachedClassifierNodeStoreFactory implements MapStoreFactory<String, CachedClassifierNode> {
    /**
     * Classifier name property.
     */
    public static final String CLASSIFIER_NAME_PROPERTY = "CLASSIFIER_NAME";
    /**
     * Classifier id property.
     */
    public static final String CLASSIFIER_ID_PROPERTY = "CLASSIFIER_ID";
    /**
     * Constructor.
     */
    public CachedClassifierNodeStoreFactory() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public MapLoader<String, CachedClassifierNode> newMapStore(String mapName, Properties properties) {

        final String classifierName = properties.getProperty(CLASSIFIER_NAME_PROPERTY);
//        final int classifierId = Integer.parseInt(properties.getProperty(CLASSIFIER_ID_PROPERTY));
        final ClsfDao clsfDao = ConfigurationServiceExt.APPLICATION_CONTEXT_HOLDER.get().getBean(ClsfDao.class);
//        final ClassifiersMetaModelCacheComponent cacheComponent =
//                ConfigurationService.APPLICATION_CONTEXT_HOLDER.get().getBean(ClassifiersMetaModelCacheComponent.class);

        return new CachedClassifierNodeStore(clsfDao, classifierName);
    }
}
