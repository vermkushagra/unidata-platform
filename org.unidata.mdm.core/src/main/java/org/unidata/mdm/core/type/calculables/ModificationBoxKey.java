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

package org.unidata.mdm.core.type.calculables;

/**
 * @author Mikhail Mikhailov
 * Marks objects, capable to generate box keys.
 * TODO: Move calc methods to particular types!
 */
public interface ModificationBoxKey {
    /**
     * Returns the box key for this object.
     * @return box key
     */
    String toBoxKey();

    /**
     * Creates box key for {@link RecordIdentityContext}.
     * @param ctx the context
     * @return key
     */
//    static String toBoxKey(RelationIdentityContext ctx) {
//        RelationKeys keys = ctx.relationKeys();
//        if (Objects.isNull(keys)) {
//            return null;
//        }
//
//        return toBoxKey(keys.getOriginKey());
//    }

    /**
     * Creates box key for {@link RecordIdentityContext}.
     * @param ctx the context
     * @return key
     */
//    static String toBoxKey(ClassifierIdentityContext ctx) {
//        RecordKeys keys = ctx.keys();
//        ClassifierKeys clsfKeys = ctx.classifierKeys();
//        String sourceSystem = keys == null ? ctx.getSourceSystem() : keys.getOriginKey().getSourceSystem();
//        String externalId = clsfKeys == null ? ctx.getClassifierNodeId() : clsfKeys.getOriginKey().getNodeId();
//        return String.join("|", sourceSystem, externalId);
//    }
    /**
     * Creates box key for {@link RecordOriginKey}.
     * @param key the origin key
     * @return key
     */
//    static String toBoxKey(RecordOriginKey key) {
//        return String.join("|", key.getSourceSystem(), key.getExternalId());
//    }
    /**
     * Creates box key for {@link RelationOriginKey}.
     * @param key the relation origin key
     * @return key
     */
//    static String toBoxKey(RelationOriginKey key) {
//        return StringUtils.join(key.getFrom() != null ? key.getFrom().getExternalId() : "", "|", key.getSourceSystem(), "|", key.getTo().getExternalId());
//    }
    /**
     * Creates box key for {@link ClassifierOriginKey}.
     * @param key the relation origin key
     * @return key
     */
//    static String toBoxKey(ClassifierOriginKey key) {
//        return StringUtils.join(key.getNodeId(), "|", key.getSourceSystem());
//    }
    /**
     * Creates box key.
     * @param sourceSystem the source system
     * @param externalId the external id
     * @return key
     */
    static String toBoxKey(String sourceSystem, String externalId) {
        return String.join("|", sourceSystem, externalId);
    }
}
