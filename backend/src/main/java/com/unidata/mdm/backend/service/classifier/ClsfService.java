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

package com.unidata.mdm.backend.service.classifier;

import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.classifier.FullClassifierDef;
import com.unidata.mdm.meta.LookupEntityDef;


/**
 * The Interface ClsfService.
 */
public interface ClsfService {

    String CLASSIFIERS_IMPORT = "CLASSIFIERS_IMPORT";

    /**
     * Save classifier.
     *
     * @param toSave the to save
     * @param createRoot create root node?
     */
    void createClassifier(ClsfDTO toSave, boolean createRoot);

    /**
     * Gets the all classifiers without descendants.
     *
     * @return the all classifiers without descendants
     */
    List<ClsfDTO> getAllClassifiersWithoutDescendants();

    /**
     * Gets the classifier by name.
     *
     * @param classifierName the classifier name
     * @return the classifier by name
     */
    ClsfDTO getClassifierByName(String classifierName);

    /**
     * Find nodes.
     *
     * @param classifierName the classifier name
     * @param text the text
     * @return the clsf DTO
     */
    ClsfDTO findNodes(String classifierName, String text);

    /**
     * Update classifier.
     *
     * @param convert the convert
     */
    void updateClassifier(ClsfDTO convert);

    /**
     * Update classifier node.
     *
     * @param classifierName the classifier name
     * @param convert the convert
     * @param updateRefs
     * @param hasData Node has data
     */
    void updateClassifierNode(String classifierName, ClsfNodeDTO convert, boolean updateRefs, boolean hasData);

    /**
     * Adds the new node to classifier.
     *
     * @param classifierName the classifier name
     * @param classifierNode the classifier node
     */
    ClsfNodeDTO addNewNodeToClassifier(String classifierName, ClsfNodeDTO classifierNode, boolean updateRefs);

    /**
     * Removes the classifier.
     *
     * @param classifierName the classifier name
     */
    void removeClassifier(String classifierName, boolean dropRefs);

    /**
     * Removes the node.
     *
     * @param classifierName the classifier name
     * @param ownNodeId the own node id
     */
    void removeNode(String classifierName, String ownNodeId, boolean updateRefs);

    /**
     * Existence check for node
     *
     * @param ownNodeId the own node id
     * @param classifierName the classifier name
     * @return true if node exist, else false
     */
    boolean isNodeExist(String ownNodeId, String classifierName);

    /**
     * Gets the node by node id.
     *
     * @param ownNodeId the own node id
     * @param classifierName the classifier name
     * @return the node by node id
     */
    ClsfNodeDTO getNodeByNodeId(String ownNodeId, String classifierName);

    /**
     * Gets the node with attrs.
     *
     * @param ownNodeId the own node id
     * @param classifierName the classifier name
     * @param reduce
     * @return the node with attrs
     */
    ClsfNodeDTO getNodeWithAttrs(String ownNodeId, String classifierName, boolean reduce);

    /**
     * Builds the branch to root.
     *
     * @param nodeIds the node ids
     * @param classifierName the classifier name
     * @return the clsf node DTO
     */
    ClsfNodeDTO buildBranchToRoot(List<String> nodeIds, String classifierName);

    /**
     * Adds the full filled classifier by code.
     *
     * @param fullClassifierDef the new classifier
     * @param importFromUser the user name who start import
     */
    boolean addFullFilledClassifierByCode(FullClassifierDef fullClassifierDef, String importFromUser);

    /**
     * Adds the full filled classifier by ids.
     *
     * @param fullClassifierDef the new classifier
     */
    boolean addFullFilledClassifierByIds(FullClassifierDef fullClassifierDef);

    /**
     * Adds the full filled classifier by ids.
     *
     * @param fullClassifierDef the new classifier
     * @param importFromUser the user name who start import
     */
    boolean addFullFilledClassifierByIds(FullClassifierDef fullClassifierDef, String importFromUser);

    /**
     * Find node by full path.
     *
     * @param clsfName the clsf name
     * @param classifierPointer the classifier pointer
     * @return the clsf node DTO
     */
    ClsfNodeDTO findNodeByFullPath(String clsfName, String classifierPointer);


    /**
     * Find node by code.
     *
     * @param clsfName the clsf name
     * @param classifierPointer the classifier pointer
     * @return the clsf node DTO
     */
    ClsfNodeDTO findNodeByCode(String clsfName, String classifierPointer);

    /**
     * Gets the all clsf attr.
     *
     * @param classifierName the classifier name
     * @return the all clsf attr
     */
    List<ClsfNodeAttrDTO> getAllClsfAttr(String classifierName);

    /**
     * Gets the ids to root.
     *
     * @param classifierNodeId the classifier node id
     * @param classifierName the classifier name
     * @return the ids to root
     */
    Collection<String> getIdsToRoot(String classifierNodeId, String classifierName);

    /**
     * Gets the classifier by name with all nodes.
     *
     * @param classifierName the classifier name
     * @return the classifier by name with all nodes
     */
    ClsfDTO getClassifierByNameWithAllNodes(String classifierName);

    /**
     * Get classifier batch size for processing
     * @return batch size
     */
    int getClassifierBatchSize();

    void removeCodeAttrsValues(LookupEntityDef lookupEntity, String value);

    List<ClsfNodeDTO> findNodesWithLookupAttributes(LookupEntityDef lookupEntity);

    boolean containsCodeAttrsValue(LookupEntityDef lookupEntity, String value);

    void removeCodeAttrsWithLookupsLinks(Collection<String> lookupEntitiesIds);
}
