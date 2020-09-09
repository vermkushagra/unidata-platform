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

package com.unidata.mdm.backend.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeArrayAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import com.unidata.mdm.backend.util.collections.Maps;

/**
 * The Interface ClsfDao.
 */
public interface ClsfDao {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    Map<Class<? extends ClsfNodeAttrPO>, String> ATTR_TYPE_MAPPING = Maps.of(
            ClsfNodeSimpleAttrPO.class, "SIMPLE",
            ClsfNodeArrayAttrPO.class, "ARRAY"
    );

    Map<String, Supplier<? extends ClsfNodeAttrPO>> ATTR_OBJECT_SUPPLIER_MAPPING = Maps.of(
            "SIMPLE", ClsfNodeSimpleAttrPO::new,
            "ARRAY", ClsfNodeArrayAttrPO::new
    );

    Map<String, Function<String, BiFunction<ResultSet, ClsfNodeAttrPO, ClsfNodeAttrPO>>> ATTR_OBJECT_ENRICHER_MAPPING = Maps.of(
            "SIMPLE", (columnPrefix) -> (rs, attr) -> {
                ClsfNodeSimpleAttrPO result = (ClsfNodeSimpleAttrPO) attr;
                try {
                    result.setEnumDataType(
                            rs.getString(columnPrefix + ClsfNodeSimpleAttrPO.FieldColumns.ENUM_DATA_TYPE.name())
                    );
                    final String value = rs.getString(
                            columnPrefix + ClsfNodeSimpleAttrPO.FieldColumns.DEFAULT_VALUE.name()
                    );
                    if (value != null) {
                        result.setDefaultValue(
                                OBJECT_MAPPER.readValue(value, String.class)
                        );
                    }
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
                return result;
            },
            "ARRAY", (columnPrefix) -> (rs, attr) -> {
                ClsfNodeArrayAttrPO result = (ClsfNodeArrayAttrPO) attr;
                try {
                    final String value = rs.getString(
                            columnPrefix + ClsfNodeSimpleAttrPO.FieldColumns.DEFAULT_VALUE.name()
                    );
                    if (value != null) {
                        result.setValues(OBJECT_MAPPER.readValue(value, Collection.class));
                    }
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
                return result;
            }
    );

    Map<String, BiFunction<Map<String, Object>, ClsfNodeAttrPO, Map<String, Object>>> ATTR_OBJECT_PARAMS_MAPPING = Maps.of(
            "SIMPLE", (params, attr) -> {
                ClsfNodeSimpleAttrPO toSave = (ClsfNodeSimpleAttrPO) attr;
                params.put(ClsfNodeSimpleAttrPO.FieldColumns.ENUM_DATA_TYPE.name().toLowerCase(), toSave.getEnumDataType());
                try {
                    params.put(
                            ClsfNodeSimpleAttrPO.FieldColumns.DEFAULT_VALUE.name().toLowerCase(),
                            StringUtils.isNotBlank(toSave.getDefaultValue()) ?
                                    OBJECT_MAPPER.writeValueAsString(toSave.getDefaultValue()) : null
                    );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return params;
            },
            "ARRAY", (params, attr) -> {
                ClsfNodeArrayAttrPO toSave = (ClsfNodeArrayAttrPO) attr;
                try {
                    params.put(
                            ClsfNodeSimpleAttrPO.FieldColumns.DEFAULT_VALUE.name().toLowerCase(),
                            CollectionUtils.isNotEmpty(toSave.getValues()) ? OBJECT_MAPPER.writeValueAsString(toSave.getValues()) : null
                    );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return params;
            }
    );

    Map<Class<? extends ClsfNodeAttrPO>, Function<ClsfNodeAttrPO, List<String>>> ATTR_OBJECT_VALUES_MAPPING = Maps.of(
            ClsfNodeSimpleAttrPO.class, (ClsfNodeAttrPO attr) -> {
                final String value = ((ClsfNodeSimpleAttrPO) attr).getDefaultValue();
                return value != null ? Collections.singletonList(value) : Collections.emptyList();
            },
            ClsfNodeArrayAttrPO.class, (ClsfNodeAttrPO attr) -> ((ClsfNodeArrayAttrPO) attr).getValues()
    );

    Map<Class<? extends ClsfNodeAttrPO>, BiConsumer<ClsfNodeAttrPO, String>> ATTR_OBJECT_VALUES_SETTER_MAPPING = Maps.of(
            ClsfNodeSimpleAttrPO.class, (ClsfNodeAttrPO attr, String value) ->
                    ((ClsfNodeSimpleAttrPO) attr).setDefaultValue(value),
            ClsfNodeArrayAttrPO.class, (ClsfNodeAttrPO attr, String value) ->
                    ((ClsfNodeArrayAttrPO) attr).addValue(value)
    );

    /**
     * Creates the.
     *
     * @param toSave
     *            the to save
     */
    void create(ClsfPO toSave);

    /**
     * Creates the.
     *
     * @param clsfName
     *            the clsf name
     * @param node
     *            the node
     * @return generated key
     */
    int create(String clsfName, ClsfNodePO node);

    /**
     * Checks if is clsf node code exists.
     *
     * @param clsfName
     *            the clsf name
     * @param code
     *            the code
     * @return true, if is clsf node exists
     */
    boolean isClsfNodeCodeExists(String clsfName, String code);
    /**
     * Gets the all classifiers.
     *
     * @return the all classifiers
     */
    List<ClsfPO> getAllClassifiers();

    /**
     * Gets the classifier by name.
     *
     * @param clsfName
     *            classifier name
     * @return the classifier by name
     */
    ClsfPO getClassifierByName(String clsfName);

    /**
     * Gets the all nodes.
     *
     * @param classifierName
     *            the classifier name
     * @return the all nodes
     */
    List<ClsfNodePO> getAllNodes(String classifierName);

    /**
     * Update.
     *
     * @param toUpdate
     *            the to update
     */
    void update(ClsfPO toUpdate);

    /**
     * Update.
     *
     * @param clsfName
     *            classifier name
     * @param node
     *            the node
     *
     */
    void update(String clsfName, ClsfNodePO node);

    /**
     * Removes the.
     *
     * @param clsfName
     *            the clsf name
     */
    void remove(String clsfName);

    /**
     * Removes the.
     *
     * @param clsfName
     *            the clsf name
     * @param nodeId
     *            the node id
     */
    void remove(String clsfName, String nodeId);

    /**
     * Gets the node by id.
     *
     * @param clsfName
     *            the clsf name
     * @param id
     *            the id
     * @return the node by id
     */
    Pair<ClsfNodePO, List<ClsfNodePO>> getNodeAndChildrenById(String clsfName, String id);

    /**
     * Gets the node by id.
     *
     * @param clsfName
     *            the clsf name
     * @return the node by id
     */
    ClsfNodePO getRootNode(String clsfName);

    /**
     * Gets the all classifier attrs.
     *
     * @param classifierName
     *            the classifier name
     * @return the all classifier attrs
     */
    List<ClsfNodeAttrPO> getAllClassifierAttrs(String classifierName);

    /**
     * Gets the node by code.
     *
     * @param clsfName
     *            the clsf name
     * @param classifierPointer
     *            the classifier pointer
     * @return the node by code
     */
    ClsfNodePO getNodeByCode(String clsfName, String classifierPointer);

    /**
     * Creates nodes.
     *
     * @param clsfName the clsf name
     * @param nodes the nodes
     */
    void create(String clsfName, List<ClsfNodePO> nodes);

    /**
     * Gets only the node attrs.
     *
     * @param clsfName the classifier name
     * @param nodeNumericId the node numeric id
     * @return the node attrs
     */
    List<ClsfNodeAttrPO> findOnlyNodeAttrs(String clsfName, int nodeNumericId);

    /**
     * Batch insert attrs.
     * @param attrs pairs of node and attr
     * @param clsfName classifier name
     */
    void insertNodeAttrs(List<Pair<ClsfNodeDTO, ClsfNodeAttrPO>> attrs, String clsfName);

    /**
     * Gets the node by path.
     *
     * @param clsfName the clsf name
     * @param path the path
     * @return the node by path
     */
    ClsfNodePO getNodeByPath(String clsfName, String path);

    /**
     * Remove all nodes by classifier name
     * @param clsfName classifier name
     */
    void removeAllNodesByClassifierName(String clsfName);

    /**
     * Remove origins links to classifier not exists nodes
     * @param classifier Classifier PO
     */
    void removeOriginsLinksToClassifierNotExistsNodes(ClsfPO classifier);

    /**
     * Remove etalons links to classifier not exists nodes
     * @param classifier Classifier PO
     */
    void removeEtalonLinksToClassifierNotExistsNodes(ClsfPO classifier);

    ClsfNodePO findNodeByCodeAndNameAndParentId(String clsfName, String code, String name, String parentId, String nodeId);

    List<ClsfPO> findAllClassifiers();

    void removeCodeAttrsValues(String lookupEntityName, String value);

    List<ClsfNodePO> findNodesWithLookupAttributes(String lookupEntityName);

    boolean containsCodeAttrsValue(String lookupEntityName, String value);

    void removeCodeAttrsWithLookupsLinks(Collection<String> lookupEntitiesIds);

    List<ClsfNodeAttrPO> fetchAttrsForCheck(String nodeId, List<String> attrsForCheck);
}
