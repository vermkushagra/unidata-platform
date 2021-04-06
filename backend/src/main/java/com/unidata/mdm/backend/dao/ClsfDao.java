package com.unidata.mdm.backend.dao;

import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;


/**
 * The Interface ClsfDao.
 */
public interface ClsfDao {

    /**
     * Creates the.
     *
     * @param toSave
     *            the to save
     */
    void create(ClsfPO toSave);

    /**
     * Checks if is clsf exists.
     *
     * @param clsfname
     *            the clsfname
     * @return true, if is clsf exists
     */
    boolean isClsfExists(String clsfname);

    /**
     * Creates the.
     *
     * @param clsfName
     *            the clsf name
     * @param node
     *            the node
     */
    void create(String clsfName, ClsfNodePO node);

    /**
     * Checks if is clsf node exists.
     *
     * @param clsfName
     *            the clsf name
     * @param nodeId
     *            the node id
     * @return true, if is clsf node exists
     */
    boolean isClsfNodeExists(String clsfName, String nodeId);
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
     * @param node
     *            the node
     * @param clsfName
     *            classifier name
     */
    void update(ClsfNodePO node, String clsfName);

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
     * Gets the nodes by parent id.
     *
     * @param clsfName
     *            the clsf name
     * @param parentId
     *            the parent id
     * @return the nodes by parent id
     */
    List<ClsfNodePO> getNodesByParentId(String clsfName, String parentId);

    /**
     * Gets the nodes with child count and has attrs by parent id.
     *
     * @param clsfName the clsf name
     * @param parentId the parent id
     * @return list of triples of node, child count, has attrs
     */
    List<Triple<ClsfNodePO, Integer, Boolean>> findNodesByParentIdWithChildCountAndHasAttrs(String clsfName, String parentId);

    /**
     * Gets the node by id.
     *
     * @param clsfName
     *            the clsf name
     * @param id
     *            the id
     * @return the node by id
     */
    ClsfNodePO getNodeById(String clsfName, String id);

    /**
     * Gets the node by id.
     *
     * @param clsfName
     *            the clsf name
     * @return the node by id
     */
    ClsfNodePO getRootNode(String clsfName);

    /**
     * Gets the all node ids.
     *
     * @param classifierName
     *            the classifier name
     * @return the all node ids
     */
    List<String> getAllNodeIds(String classifierName);

    /**
     * Gets the nodes to root.
     *
     * @param ownNodeId
     *            the own node id
     * @param classifierName
     *            the classifier name
     * @return the nodes to root
     */
    List<String> getNodesToRoot(String ownNodeId, String classifierName);

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
     * Create nodes.
     *
     * @param clsfId clsf name
     * @param nodes nodes
     */
    void create(final int clsfId, final List<ClsfNodePO> nodes);

    /**
     * Gets the node attrs.
     *
     * @param classifierName the classifier name
     * @param ownNodeId the own node id
     * @return the node attrs
     */
    List<ClsfNodeAttrPO> getNodeAttrs(String classifierName, String ownNodeId);

    /**
     * Gets only the node attrs.
     *
     * @param classifierName the classifier name
     * @param ownNodeId the own node id
     * @return the node attrs
     */
    List<ClsfNodeAttrPO> getOnlyNodeAttrs(String classifierName, String ownNodeId);

    /**
     * Insert node attrs.
     *
     * @param nodeAttrs the node attrs
     * @param nodeId the node id
     */
    void insertNodeAttrs(List<ClsfNodeAttrPO> nodeAttrs, String nodeId, String clsfName);

    /**
     * Batch insert attrs.
     * @param attrs pairs of node and attr
     * @param clsfId classifier id
     */
    void insertNodeAttrs(List<Pair<ClsfNodeDTO,ClsfNodeAttrPO>> attrs, int clsfId);

    /**
     * Count childs.
     *
     * @param nodeId the node id
     * @return the int
     */
    int countChilds(String nodeId, String clsfName);

    /**
     * Checks if is own attrs.
     *
     * @param nodeId the node id
     * @return true, if is own attrs
     */
    boolean isOwnAttrs(String nodeId, String clsfName);

    /**
     * Gets the node by path.
     *
     * @param clsfName the clsf name
     * @param path the path
     * @return the node by path
     */
    ClsfNodePO getNodeByPath(String clsfName, String path);

    /**
     * Remove all nodes by classifier id
     * @param classifierId classifier id
     */
    void removeAllNodesByClassifierId(final int classifierId);

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

    ClsfNodePO findNodeByCodeAndNameAndParentId(String clsfName, String code, String name, String parentId);

    List<ClsfPO> findAllClassifiers();

    Map<String, List<String>> findNodesWithPresentAttributesInClassifier(String clsfName, String nodeId, List<String> names);
}
