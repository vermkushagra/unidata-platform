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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.unidata.mdm.backend.common.types.Attribute.AttributeType;

/**
 * @author Mikhail Mikhailov
 * Classifier node with no superfluous information.
 */
public class CachedClassifierNode implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -4871620515338321808L;
    /**
     * Node numeric id.
     */
    private int id;
    /**
     * The node id.
     */
    private String nodeId;
    /**
     * The parent node id.
     */
    private String parentNodeId;
    /**
     * The name.
     */
    private String name;
    /**
     * The description.
     */
    private String description;
    /**
     * The code.
     */
    private String code;
    /**
     * TODO maybe replace this with sorted List<Pair<int, String>?
     * Children ids.
     */
    private final List<String> children = new ArrayList<>(8);
    /**
     * CPs.
     */
    private CachedClassifierCustomProperty[] customProperties;
    /**
     * Own attributes, either overwritten or new.
     */
    private final Map<AttributeType, List<CachedClassifierNodeAttribute>> attributes
        = new EnumMap<>(AttributeType.class);
    /**
     * Null-initializing package constructor for NULL_NODE.
     */
    public CachedClassifierNode() {
        super();
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }
    /**
     * @return the children
     */
    public List<String> getChildren() {
        return children;
    }
    /**
     * @return the attributes
     */
    public Map<AttributeType, List<CachedClassifierNodeAttribute>> getAttributes() {
        return attributes;
    }
    /**
     * Gets all attributes a s list.
     * @return list
     */
    public List<CachedClassifierNodeAttribute> getAttributesAsList() {
        return MapUtils.isEmpty(attributes)
                ? Collections.emptyList()
                : Stream.concat(
                        CollectionUtils.isEmpty(attributes.get(AttributeType.SIMPLE)) ? Stream.empty() : attributes.get(AttributeType.SIMPLE).stream(),
                        CollectionUtils.isEmpty(attributes.get(AttributeType.ARRAY)) ? Stream.empty() : attributes.get(AttributeType.ARRAY).stream())
                .collect(Collectors.toList());
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    /**
     * @return the parentNodeId
     */
    public String getParentNodeId() {
        return parentNodeId;
    }
    /**
     * @param parentNodeId the parentNodeId to set
     */
    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }
    /**
     * @return the customProperties
     */
    public CachedClassifierCustomProperty[] getCustomProperties() {
        return customProperties;
    }
    /**
     * @param customProperties the customProperties to set
     */
    public void setCustomProperties(CachedClassifierCustomProperty[] customProperties) {
        this.customProperties = customProperties;
    }
    /**
     * Tells, if this object is the same node.
     * @param other other node object
     * @return true, if so, false otherwise
     */
    public boolean sameNode(CachedClassifierNode other) {

        if (Objects.isNull(other)) {
            return false;
        }

        return this.id == other.id;
    }
}