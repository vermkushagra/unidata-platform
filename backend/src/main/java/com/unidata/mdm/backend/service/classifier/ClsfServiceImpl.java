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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.api.rest.converter.clsf.ClsCustomPropertyDefConverter;
import com.unidata.mdm.backend.common.configuration.BeanNameConstants;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.*;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.ClassifierDataHeaderField;
import com.unidata.mdm.backend.common.search.fields.ClassifierHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.MetaDraftServiceExt;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.dao.impl.DaoHelper;
import com.unidata.mdm.backend.notification.events.ClassifierChangeEvent.ClassifierChangesEventType;
import com.unidata.mdm.backend.notification.notifiers.ClassifierChangeNotifier;
import com.unidata.mdm.backend.service.classifier.cache.*;
import com.unidata.mdm.backend.service.classifier.converters.*;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import com.unidata.mdm.backend.service.job.JobServiceExt;
import com.unidata.mdm.backend.service.job.reindex.ReindexDataJobConstants;
import com.unidata.mdm.backend.service.job.reports.JobReportConstants;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import com.unidata.mdm.classifier.AbstractClassifierAttributeDef;
import com.unidata.mdm.classifier.ClassifierNodeDef;
import com.unidata.mdm.classifier.ClassifierValueDef;
import com.unidata.mdm.classifier.FullClassifierDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forClassifierElements;
import static com.unidata.mdm.backend.common.search.FormField.strictString;

/**
 * The Class ClsfServiceImpl.
 */
@Component
public class ClsfServiceImpl implements ClsfService, ConfigurationUpdatesConsumer {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClsfServiceImpl.class);
    /**
     * The clsf dao.
     */
    @Autowired
    private ClsfDao clsfDao;
    /**
     * The search service.
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * The security service.
     */
    @Autowired
    private SecurityServiceExt securityService;
    /**
     * The metamodel service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Meta model draft service.
     */
    @Autowired
    private MetaDraftServiceExt metaDraftService;
    /**
     * The job service.
     */
    @Autowired
    private JobServiceExt jobServiceExt;
    /**
     * Nodes cache.
     */
    @Autowired
    private ClassifiersMetaModelCacheComponent cacheComponent;
    /**
     * HZ change notifier.
     */
    @Autowired
    private ClassifierChangeNotifier changeNotifier;

    /** The conversion service. */
    @Autowired
    private ConversionService conversionService;

    /**
     * Default object mapper.
     */
    @Autowired
    @Qualifier(BeanNameConstants.DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    protected ObjectMapper objectMapper;

    /**
     * The Constant ROOT_NODE_POSTFIX.
     */
    private static final String ROOT_NODE_POSTFIX = "root";
    /**
     * The Constant DOT.
     */
    private static final String DOT = ".";

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_-]*$", Pattern.CASE_INSENSITIVE);

    private AtomicInteger batchSize = new AtomicInteger(
            (Integer) UnidataConfigurationProperty.CLASSIFIER_IMPORT_BATCH_SIZE.getDefaultValue().get()
    );

    /**
     * Max number of nodes returned from search.
     */
    private static final int MAX_SEARCH_RESULTS = 100;
    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#createClassifier(com
     * .unidata.mdm.backend.service.classifier.dto.ClsfDTO)
     */
    @Override
    @Transactional
    public void createClassifier(ClsfDTO toSave, boolean createRoot) {

        MeasurementPoint.start();
        try {

            if (cacheComponent.classifierExists(toSave.getName())) {
                throw new BusinessException("Classifier already exist!", ExceptionId.EX_CLASSIFIER_ALREADY_CREATED,
                        toSave.getName(), toSave.getDisplayName());
            }

            validateClassifierCodePattern(toSave.getCodePattern());
            toSave.setCreatedAt(new Date());
            toSave.setCreatedBy(SecurityUtils.getCurrentUserName());
            ClsfPO clsfPO = ClsfDTOToPOConverter.convert(toSave);

            clsfDao.create(clsfPO);

            if (!searchService.classifierIndexExist(SecurityUtils.getCurrentUserStorageId())) {
                searchService.createClassifierIndex(SecurityUtils.getCurrentUserStorageId());
            }

            if (createRoot) {

                DaoHelper.executeAfterCommitAction(() -> {

                    ClsfNodeDTO rootNode = new ClsfNodeDTO();
                    rootNode.setName(toSave.getDisplayName());
                    rootNode.setClsfName(toSave.getName());
                    rootNode.setNodeId(String.join(DOT, toSave.getName(), ROOT_NODE_POSTFIX));
                    rootNode.setDescription(toSave.getDescription());
                    rootNode.setCode("");

                    addNewNodeToClassifier(toSave.getName(), rootNode, false);
                });
            }

            securityService.createResourceForClassifier(toSave.getName(), toSave.getDisplayName());

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void validateClassifierCodePattern(final String codePattern) {
        if (StringUtils.isBlank(codePattern)) {
            return;
        }
        final Collection<Pair<Integer, InvalidSymbolReason>> errors = CodeParser.validateCodePatternContent(codePattern);
        if (!errors.isEmpty()) {
            throw new CodePatternValidationException(
                    "Code pattern is incorrect: " + codePattern,
                    ExceptionId.EX_CLASSIFIER_CODE_PATTERN_INCORRECT,
                    errors.stream().map(r -> {
                        switch (r.getRight()) {
                            case UNKNOWN_SYMBOL:
                                return new ValidationResult(
                                        "Unknown symbol in position " + r.getLeft(),
                                        "app.classifier.code.pattern.unknown.symbol",
                                        r.getLeft()
                                );
                            case MUST_BE_PLACEHOLDER:
                                return new ValidationResult(
                                        "Placeholder must be in position " + r.getLeft(),
                                        "app.classifier.code.pattern.must.be.placeholder",
                                        r.getLeft()
                                );
                            case WRONG_ZERO_FILLER_POSITION:
                                return new ValidationResult(
                                        "Wrong position of zeros filler " + r.getLeft(),
                                        "app.classifier.code.pattern.wrong.position.of.zeros.filler",
                                        r.getLeft()
                                );
                        }
                        throw new RuntimeException("Unhandled validation symbol reason");
                    }).collect(Collectors.toList()),
                    codePattern,
                    highlightErrors(codePattern, errors)
            );
        }
    }

    private String highlightErrors(final String codePattern, Collection<Pair<Integer, InvalidSymbolReason>> errors) {
        final StringBuilder codePatternWith = new StringBuilder();
        final int[] currentSymbol = {0};
        errors.stream().map(p -> p.getLeft() - 1).forEach(errorPos -> {
            if (errorPos != 0) {
                codePatternWith.append(codePattern.substring(currentSymbol[0], errorPos));
            }
            codePatternWith.append('[')
                    .append(errorPos + 1)
                    .append(':')
                    .append(codePattern.charAt(errorPos))
                    .append(']');
            currentSymbol[0] = errorPos + 1;
        });
        return codePatternWith.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * getAllClassifiersWithoutDescendants()
     */
    @Override
    public List<ClsfDTO> getAllClassifiersWithoutDescendants() {
        MeasurementPoint.start();
        try {
            List<ClsfPO> clsfPOs = clsfDao.getAllClassifiers();
            return ClsfPOToDTOConverter.convert(clsfPOs);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * getClassifierByName(java.lang.String)
     */
    @Override
    public ClsfDTO getClassifierByName(String classifierName) {
        MeasurementPoint.start();
        try {
            return CachedClassifierToClsfDTOConverter.convert(cacheComponent.getClassifier(classifierName));
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#findNodes(java.
     * lang.String, java.lang.String)
     */
    @Override
    public ClsfDTO findNodes(String classifierName, String text) {

        MeasurementPoint.start();
        try {

            ClsfDTO clsf = getClassifierByName(classifierName);
            FormField classifierField = strictString(ClassifierHeaderField.CLASSIFIER_NAME.getField(), classifierName);
            FormField textField = FormField.startWithString(ClassifierHeaderField.NODE_SEARCH_ELEMENT.getField(), text);
            SearchRequestContext scb = SearchRequestContext.forClassifierElements().text(text)
                    .form(FormFieldsGroup.createAndGroup(classifierField, textField))
                    .returnFields(Collections.singletonList(ClassifierHeaderField.NODE_UNIQUE_ID.getField())).count(MAX_SEARCH_RESULTS)
                    .page(0).totalCount(true).source(false).build();
            SearchResultDTO result = searchService.search(scb);
            if (result.getTotalCount() > MAX_SEARCH_RESULTS) {
                throw new BusinessException("Too many results found",
                        ExceptionId.EX_SEARCH_CLASSIFIERS_META_RESULT_TOO_MUCH, MAX_SEARCH_RESULTS);
            }
            if (result.getHits().isEmpty()) {
                return clsf;
            }

            List<String> nodes = result.getHits().stream()
                    .map(hit -> hit.getFieldValue(ClassifierHeaderField.NODE_UNIQUE_ID.getField())).filter(Objects::nonNull)
                    .filter(SearchResultHitFieldDTO::isNonNullField).map(SearchResultHitFieldDTO::getValues)
                    .flatMap(Collection::stream).filter(Objects::nonNull).map(Object::toString).distinct()
                    .collect(Collectors.toList());

            ClsfNodeDTO node = buildBranchToRoot(nodes, classifierName);

            clsf.setRootNode(node);
            return clsf;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#updateClassifier(
     * com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO)
     */
    @Override
    @Transactional
    public void updateClassifier(ClsfDTO clsfDTO) {

        if (!cacheComponent.classifierExists(clsfDTO.getName())) {
            throw new BusinessException("Classifier for update does not exist!",
                    ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE, clsfDTO.getName());
        }

        // 1. Classifier
        clsfDTO.setUpdatedAt(new Date());
        clsfDTO.setUpdatedBy(SecurityUtils.getCurrentUserName());

        ClsfPO toUpdate = ClsfDTOToPOConverter.convert(clsfDTO);
        clsfDao.update(toUpdate);

        cacheComponent.refreshClassifier(clsfDTO.getName());
        changeNotifier.notifyClassifierChanged(ClassifierChangesEventType.REFRESH, Collections.singletonList(clsfDTO.getName()));

        // 2. Root node
        CachedClassifier cc = cacheComponent.getClassifier(clsfDTO.getName());
        CachedClassifierNode rootNode = cacheComponent.getNode(clsfDTO.getName(), cc.getRootNodeId());
        if (Objects.nonNull(rootNode) && !StringUtils.equals(rootNode.getName(), clsfDTO.getDisplayName())) {
            rootNode.setName(clsfDTO.getDisplayName());
            cacheComponent.setNode(clsfDTO.getName(), rootNode);
        }
    }

    private void fillLookupEntityCodeAttributeType(final ClsfNodeAttrPO attr) {
        if (StringUtils.isNotBlank(attr.getLookupEntityType())
                && StringUtils.isBlank(attr.getLookupEntityCodeAttributeType())) {
            attr.setLookupEntityCodeAttributeType(
                    metaModelService.getLookupEntityById(attr.getLookupEntityType())
                            .getCodeAttribute()
                            .getSimpleDataType()
                            .name()
            );
        }
    }

    private void fillLookupEntityCodeAttributeType(final ClsfNodeAttrDTO attr) {
        if (StringUtils.isNotBlank(attr.getLookupEntityType())
                && attr.getLookupEntityCodeAttributeType() == null) {
            attr.setLookupEntityCodeAttributeType(
                    CodeAttribute.CodeDataType.valueOf(
                            metaModelService.getLookupEntityById(attr.getLookupEntityType())
                                    .getCodeAttribute()
                                    .getSimpleDataType()
                                    .name()
                    )
            );
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#removeClassifier(
     * java.lang.String)
     */
    @Override
    @Transactional
    public void removeClassifier(String classifierName, boolean dropRefs) {

        if (!cacheComponent.classifierExists(classifierName)) {
            throw new BusinessException("Classifier does not exist!", ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE, classifierName);
        }

        clsfDao.remove(classifierName);

        cacheComponent.destroyClassifier(classifierName);
        changeNotifier.notifyClassifierChanged(ClassifierChangesEventType.REMOVE, Collections.singletonList(classifierName));

        afterRemoveClassifierParts(classifierName, dropRefs);
    }

    private void afterRemoveClassifierParts(
            final String classifierName,
            final boolean dropRefs
    ) {
        FormField clsfNameField = strictString(ClassifierHeaderField.CLASSIFIER_NAME.getField(), classifierName);
        SearchRequestContext searchContext = forClassifierElements()
                .form(FormFieldsGroup.createAndGroup(clsfNameField))
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build();
        searchService.deleteFoundResult(searchContext);
        securityService.deleteResources(Collections.singletonList(classifierName));
        if (dropRefs) {
            removeRefsToClsf(classifierName);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * addNewNodeToClassifier(java.lang.String,
     * com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO)
     */
    @Override
    @Transactional
    public ClsfNodeDTO addNewNodeToClassifier(
            final String classifierName,
            final ClsfNodeDTO classifierNode,
            final boolean updateRefs
    ) {

        final ClsfDTO classifier = getClassifierByName(classifierName);
        if (Objects.isNull(classifier)) {
            throw new BusinessException("Classifier does not exist!", ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE, classifierName);
        }

        parentNodeCheckAndUpdate(classifierName, classifierNode, classifier);

        if (classifierNode.getNodeId() == null) {
            if (StringUtils.isNotBlank(classifierNode.getCode())
                    && StringUtils.isNotBlank(classifier.getCodePattern())
                    && classifier.isValidateCodeByLevel()) {
                classifierNode.setNodeId(CodeParser.toNodeId(classifierNode.getCode()));
            } else {
                classifierNode.setNodeId(IdUtils.v1String());
            }
        }

        validate(classifier, classifierNode, classifier.isValidateCodeByLevel());
        validateExistNodeNameAndCode(classifier, classifierNode);
        validateAttributes(classifier, classifierNode);

        updateIds(classifierName, classifierNode);

        classifierNode.setCreatedAt(new Date());
        classifierNode.setCreatedBy(SecurityUtils.getCurrentUserName());

        classifierNode.getAllNodeAttrs().forEach(this::fillLookupEntityCodeAttributeType);
        cacheComponent.setNode(classifierName, ClsfNodeDTOToCachedClassifierNodeConverter.convert(classifierNode));

        classifierNode.setClsfName(classifierName);
        searchService.indexClassifierNode(SecurityUtils.getCurrentUserStorageId(), classifierNode);
//        if (updateRefs) {
//            removeRefsToClsf(classifierName, false);
//        }
        return getNodeWithAttrs(classifierNode.getNodeId(), classifierName, false);
    }

    private void validateAttributes(final ClsfDTO classifier, final ClsfNodeDTO classifierNode) {
        final List<ClsfNodeAttrDTO> attrsForCheck = classifierNode.getAllNodeAttrs();
        if (CollectionUtils.isEmpty(attrsForCheck)) {
            return;
        }

        final List<String> attrsNamesForCheck = attrsForCheck.stream()
                .map(ClsfNodeAttrDTO::getAttrName)
                .collect(Collectors.toList());

        final List<ClsfNodeAttrDTO> nodeAttrs = classifierNode.getNodeId() != null ?
                findAttrsForCheckByNodeId(classifierNode, attrsNamesForCheck) :
                findByAttrsForCheckByParentId(classifier, classifierNode.getParentId());

        if (nodeAttrs.isEmpty()) {
            return;
        }

        final Map<String, List<ClsfNodeAttrDTO>> attrsForCheckMap = nodeAttrs.stream()
                .collect(Collectors.groupingBy(ClsfNodeAttrDTO::getAttrName));

        final List<ClsfNodeAttrDTO> invalidAttrs = attrsForCheck.stream()
                .filter(a -> attrsForCheckMap.containsKey(a.getAttrName()) && !sameType(a, attrsForCheckMap.get(a.getAttrName())))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(invalidAttrs)) {
            throw new BusinessException(
                    "Attributes with names have different type then exists in same branch",
                    ExceptionId.EX_CLASSIFIER_ATTRIBUTES_WITH_SAME_NAME_EXISTS,
                    invalidAttrs.stream()
                            .map(ClsfNodeAttrDTO::getAttrName)
                            .collect(Collectors.joining(","))
            );
        }
    }

    private void validateAttrs(final ClsfNodeDTO node) {
        if (CollectionUtils.isEmpty(node.getAllNodeAttrs())) {
            return;
        }
        final List<String> reqReadOnlyAttrsAttrs = node.getAllNodeAttrs().stream()
                .filter(a -> a.isReadOnly() && !a.isNullable())
                .map(ClsfNodeAttrDTO::getDisplayName)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(reqReadOnlyAttrsAttrs)) {
            throw new BusinessException(
                    "Attrs are read only and non nullabe in same time.",
                    ExceptionId.EX_CLASSIFIER_REQ_READ_ONLY_ATTRIBUTES,
                    String.join(", ", reqReadOnlyAttrsAttrs),
                    node.getName()
            );
        }
    }

    private List<ClsfNodeAttrDTO> findAttrsForCheckByNodeId(ClsfNodeDTO classifierNode, List<String> attrsForCheck) {
        return ClsfNodeAttrPOToDTOConverter.convertNodeAttrs(clsfDao.fetchAttrsForCheck(classifierNode.getNodeId(), attrsForCheck));
    }

    private List<ClsfNodeAttrDTO> findByAttrsForCheckByParentId(ClsfDTO classifier, String parentId) {
        final ClsfNodeDTO nodeWithAttrs = getNodeWithAttrs(parentId, classifier.getName(), false);
        if (nodeWithAttrs == null) {
            return Collections.emptyList();
        }
        return nodeWithAttrs.getAllNodeAttrs();
    }

    private boolean sameType(final ClsfNodeAttrDTO current, final List<ClsfNodeAttrDTO> exists) {
        for (ClsfNodeAttrDTO exist : exists) {
            if (current.getClass() != exist.getClass()) {
                return false;
            }
            if (current instanceof ClsfNodeSimpleAttrDTO) {
                String currentEnumDataType = ((ClsfNodeSimpleAttrDTO) current).getEnumDataType();
                String existEnumDataType = ((ClsfNodeSimpleAttrDTO) exist).getEnumDataType();
                if (StringUtils.isNotBlank(currentEnumDataType) || StringUtils.isNotBlank(existEnumDataType)) {
                    return Objects.equals(currentEnumDataType, existEnumDataType);
                }
            }
            if ((current.getDataType() != null
                    || exist.getDataType() != null)
                    && !Objects.equals(current.getDataType(), exist.getDataType())) {
                return false;
            }
            else if ((StringUtils.isNotBlank(current.getLookupEntityType())
                    || StringUtils.isNotBlank(exist.getLookupEntityType()))
                    && !Objects.equals(current.getLookupEntityType(), exist.getLookupEntityType())) {
                return false;
            }
        }
        return true;
    }

    private boolean sameType(final ClsfNodeAttrDTO current, CachedClassifierNodeAttribute exists) {

        if ((current.isArray() && !exists.isArray())
                || (current.isSimple() && !exists.isSimple())) {
            return false;
        }

        // Enum
        if (current.isSimple() && StringUtils.isNotBlank(((ClsfNodeSimpleAttrDTO) current).getEnumDataType())) {

            if (!(exists instanceof CachedClassifierNodeLinkableAttribute)) {
                return false;
            }

            CachedClassifierNodeLinkableAttribute linkable = (CachedClassifierNodeLinkableAttribute) exists;
            if (!linkable.isEnumLink()) {
                return false;
            }

            String currentEnumDataType = ((ClsfNodeSimpleAttrDTO) current).getEnumDataType();
            String existEnumDataType = ((CachedClassifierNodeSimpleAttribute) exists).getEnumName();
            if (StringUtils.isNotBlank(currentEnumDataType) || StringUtils.isNotBlank(existEnumDataType)) {
                return Objects.equals(currentEnumDataType, existEnumDataType);
            }
        }

        // Lookup
        if (!Objects.nonNull(current.getLookupEntityType())) {

            if (!(exists instanceof CachedClassifierNodeLinkableAttribute)) {
                return false;
            }

            CachedClassifierNodeLinkableAttribute linkable = (CachedClassifierNodeLinkableAttribute) exists;
            if (!linkable.isLookupLink()) {
                return false;
            }

            return StringUtils.equals(current.getLookupEntityType(), linkable.getLookupName());
        }

        // General type
        if (current.getDataType() != null) {

            String typeName = current.getDataType().name();
            String existingName = null;
            if (exists.isSimple() && Objects.nonNull(((CachedClassifierNodeSimpleAttribute) exists).getDataType())) {
                existingName = ((CachedClassifierNodeSimpleAttribute) exists).getDataType().name();
            }  else if (exists.isArray() && Objects.nonNull(((CachedClassifierNodeArrayAttribute) exists).getDataType())) {
                existingName = ((CachedClassifierNodeArrayAttribute) exists).getDataType().name();
            }

            return StringUtils.equals(typeName, existingName);
        }

        return true;
    }

    private void validateExistNodeNameAndCode(ClsfDTO clsf, ClsfNodeDTO classifierNode) {
        final ClsfNodePO node = clsfDao.findNodeByCodeAndNameAndParentId(
                clsf.getName(),
                classifierNode.getCode(),
                classifierNode.getName(),
                classifierNode.getParentId(),
                classifierNode.getNodeId()
        );
        if (node != null) {
            throw new BusinessException(
                    "Node with same code and name on this level exists.",
                    ExceptionId.EX_CLASSIFIER_SAME_CODE_AND_NAME_ON_ONE_LEVEL,
                    clsf.getName(),
                    classifierNode.getCode(),
                    classifierNode.getName()
            );
        }
    }

    private void validate(ClsfDTO clsf, ClsfNodeDTO classifierNode, boolean validateCodeByLevel) {

        final String classifierName = clsf.getName();
        if (StringUtils.isEmpty(classifierNode.getName())) {
            throw new BusinessException(
                    "Conversion to classifier node failed. No value for field: name.",
                    ExceptionId.EX_CLASSIFIER_NODE_FAILED_MISSING_FIELD,
                    "name"
            );
        }

        if (CollectionUtils.isNotEmpty(classifierNode.getNodeSimpleAttrs())) {

            Set<String> attrNames = new HashSet<>();
            classifierNode.getNodeSimpleAttrs().forEach(attr -> {

                if (attrNames.contains(attr.getAttrName())) {
                    throw new BusinessException("Classifier does not exist!", ExceptionId.EX_CLASSIFIER_NODE_ATTR_DUPL,
                            attr.getAttrName(), classifierName, classifierNode.getName());
                }

                if (!NAME_PATTERN.matcher(attr.getAttrName()).matches()) {
                    throw new BusinessException(
                            "Classifier attribute name incorrect!",
                            ExceptionId.EX_CLASSIFIER_NODE_ATTR_NAME_INCORRECT,
                            attr.getAttrName(),
                            classifierName,
                            classifierNode.getName(),
                            attr.getDataType().name()
                    );
                }

                attrNames.add(attr.getAttrName());
            });
        }

        validateAttrs(classifierNode);

        if (StringUtils.isNotEmpty(clsf.getCodePattern()) && StringUtils.isNotEmpty(classifierNode.getParentId())) {
            if (!CodeParser.isValidCodeForPattern(classifierNode.getCode(), clsf.getCodePattern())) {
                throw new BusinessException(
                        "Pattern are not matching",
                        ExceptionId.EX_CLASSIFIER_NODE_CODE_INCORRECT,
                        classifierNode.getCode(),
                        clsf.getCodePattern(),
                        clsf.getName()
                );
            }

            final String[] codes = CodeParser.extractGroups(classifierNode.getCode(), clsf.getCodePattern());
            final List<String> toRoot = getIdsToRoot(classifierNode.getParentId(), classifierName);
            int maxNodesCount = CodeParser.groupsCount(clsf.getCodePattern());
            if (toRoot.size() > maxNodesCount) {
                throw new BusinessException("Maximum number of groups exceeded!",
                        ExceptionId.EX_CLASSIFIER_NODE_ADD_MAXIMUM_EXCEEDED,
                        maxNodesCount);
            }
            if (validateCodeByLevel && codes.length != toRoot.size()) {
                throw new BusinessException("Code is incorrect!",
                        ExceptionId.EX_CLASSIFIER_NODE_CODE_DOESNT_MATCH_PARENT,
                        classifierNode.getCode());
            } else if (clsfDao.isClsfNodeCodeExists(classifierName, classifierNode.getCode())
                    && !cacheComponent.nodeExists(classifierName, classifierNode.getNodeId())) {
                throw new BusinessException("Code is not unique!", ExceptionId.EX_CLASSIFIER_NODE_CODE_NOT_UNIQUE,
                        classifierName, classifierNode.getName(), classifierNode.getCode());
            } else if (toRoot.size() > 1) {
                ClsfNodeDTO parent = getNodeByNodeId(classifierNode.getParentId(), classifierName);
                final String[] parentCodes = CodeParser.extractGroups(parent.getCode(), clsf.getCodePattern());
                for (int i = 0; i < codes.length - 1; i++) {
                    if (i<parentCodes.length && !parentCodes[i].equals(codes[i])) {
                        throw new BusinessException("Code is incorrect!",
                                ExceptionId.EX_CLASSIFIER_NODE_CODE_DOESNT_MATCH_PARENT_CODE,
                                classifierNode.getCode(), parent.getCode());
                    }

                }
            }
        }
    }

    private void updateIds(String classifierName, ClsfNodeDTO classifierNode) {
        if (classifierNode.getNodeId() == null) {
            classifierNode.setNodeId(IdUtils.v1String());
        }
        if (!StringUtils.equals(classifierNode.getNodeId(), String.join(DOT, classifierName, ROOT_NODE_POSTFIX))
                && classifierNode.getParentId() == null) {
            classifierNode.setParentId(String.join(DOT, classifierName, ROOT_NODE_POSTFIX));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * updateClassifierNode(java.lang.String,
     * com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO)
     */
    @Override
    @Transactional
    public void updateClassifierNode(
            final String classifierName,
            final ClsfNodeDTO classifierNode,
            final boolean updateRefs,
            final boolean hasData
    ) {

        final ClsfDTO clsf = getClassifierByName(classifierName);
        if (Objects.isNull(clsf)) {
            throw new BusinessException(
                    "Classifier does not exist!",
                    ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE,
                    classifierName
            );
        }

        parentNodeCheckAndUpdate(classifierName, classifierNode, clsf);

        validate(clsf, classifierNode, clsf.isValidateCodeByLevel());
        validateExistNodeNameAndCode(clsf, classifierNode);
        validateAttributes(clsf, classifierNode);
        if (hasData) {
            validateAttributesModification(clsf, classifierNode);
        }

        if (classifierNode.getNodeId() == null) {
            if (classifierNode.getCode() != null && clsf.getCodePattern() != null && clsf.isValidateCodeByLevel()) {
                classifierNode.setNodeId(CodeParser.toNodeId(classifierNode.getCode()));
            }
            else {
                classifierNode.setNodeId(IdUtils.v1String());
            }
        }

        FormField nodeIdField = strictString(ClassifierHeaderField.NODE_UNIQUE_ID.getField(),
                classifierNode.getNodeId());
        SearchRequestContext searchContext = forClassifierElements().form(FormFieldsGroup.createAndGroup(nodeIdField))
                .storageId(SecurityUtils.getCurrentUserStorageId()).build();
        searchService.deleteFoundResult(searchContext);
        classifierNode.setClsfName(classifierName);
        searchService.indexClassifierNode(SecurityUtils.getCurrentUserStorageId(), classifierNode);

        classifierNode.getAllNodeAttrs().forEach(this::fillLookupEntityCodeAttributeType);

        final CachedClassifierNode cachedClassifierNode = ClsfNodeDTOToCachedClassifierNodeConverter.convert(classifierNode);

        final CachedClassifierNode node = cacheComponent.getNode(classifierName, classifierNode.getNodeId());
        if (node != null && CollectionUtils.isNotEmpty(node.getChildren())) {
            cachedClassifierNode.getChildren().addAll(node.getChildren());
        }

        cacheComponent.setNode(classifierName, cachedClassifierNode);

//        if (hasData) {
//            cleanIndexedDataByNodeId(classifierName, classifierNode.getNodeId());
//        }
//        if (updateRefs) {
//            removeRefsToClsf(classifierName, false);
//        }
    }

    private void parentNodeCheckAndUpdate(String classifierName, ClsfNodeDTO classifierNode, ClsfDTO clsf) {
        if (classifierNode.getParentId() == null) {
            classifierNode.setName(clsf.getDisplayName());
        } else {
            final CachedClassifierNode parentNode = cacheComponent.getNode(clsf.getName(), classifierNode.getParentId());
            if (parentNode == null) {
                throw new BusinessException(
                        "Couldn't find parent node!",
                        ExceptionId.EX_CLASSIFIER_NO_PARENT_NODE,
                        classifierName,
                        classifierNode.getName(),
                        classifierNode.getParentId()
                );
            }
        }
    }

    private void validateAttributesModification(ClsfDTO clsf, ClsfNodeDTO classifierNode) {

        final Map<String, CachedClassifierNodeAttribute> currentAttrs;
        CachedClassifierNode current = cacheComponent.getNode(clsf.getName(), classifierNode.getNodeId());
        if (Objects.nonNull(current)) {
            currentAttrs = current.getAttributesAsList().stream()
                    .collect(Collectors.toMap(CachedClassifierNodeAttribute::getName, Function.identity()));
        } else {
            currentAttrs = Collections.emptyMap();
        }

        classifierNode.getAllNodeAttrs().forEach(attr -> {
            final String attrName = attr.getAttrName();
            CachedClassifierNodeAttribute cached = currentAttrs.get(attrName);
            if (Objects.nonNull(cached) && !sameType(attr, cached)) {

                throw new BusinessException(
                        "Modification attribute with data",
                        ExceptionId.EX_CLASSIFIER_MODIFICATION_ATTRIBUTE_WITH_DATA,
                        classifierNode.getName(),
                        attrName
                );
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#removeNode(java.
     * lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void removeNode(String classifierName, String ownNodeId, boolean updateRefs) {

        // 1. Remove from metamodel
        cacheComponent.removeNode(classifierName, ownNodeId);

        // 2. Remove from metaindex
        FormField nodeIdField = strictString(ClassifierHeaderField.NODE_UNIQUE_ID.getField(), ownNodeId);
        SearchRequestContext searchContext = forClassifierElements()
                .form(FormFieldsGroup.createAndGroup(nodeIdField))
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build();

        searchService.deleteAll(searchContext);

        // 3. Remove from data index.
        // Data will be removed by separate job, as potentially big piece of work.
        cleanIndexedDataByNodeId(classifierName, ownNodeId);

        // 4. Update model
//        if (updateRefs) {
//            removeRefsToClsf(classifierName, false);
//        }

    }

    private void cleanIndexedDataByNodeId(final String classifierName, final String ownNodeId) {
        List<AbstractEntityDef> classifiedEntities = metaModelService.getClassifiedEntities(classifierName);
        if (CollectionUtils.isNotEmpty(classifiedEntities)) {

            String classifierNodeField = String.join(".", classifierName,
                    ClassifierDataHeaderField.FIELD_NODES.getField(),
                    ClassifierDataHeaderField.FIELD_NODE_ID.getField());

            FormField classifierField = strictString(ClassifierDataHeaderField.FIELD_NAME.getField(), classifierName);
            FormField textField = strictString(classifierNodeField, ownNodeId);
            for (AbstractEntityDef entityDef : classifiedEntities) {

                SearchRequestContext searchContext = SearchRequestContext.builder(EntitySearchType.CLASSIFIER, entityDef.getName())
                        .form(FormFieldsGroup.createAndGroup(classifierField, textField))
                        .storageId(SecurityUtils.getCurrentUserStorageId())
                        .build();

                searchService.deleteAll(searchContext);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getNodeByNodeId(
     * java.lang.String, java.lang.String)
     */
    @Override
    public boolean isNodeExist(String ownNodeId, String classifierName) {

        MeasurementPoint.start();
        try {
            return cacheComponent.nodeExists(classifierName, ownNodeId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getNodeByNodeId(
     * java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodeDTO getNodeByNodeId(String ownNodeId, String classifierName) {

        MeasurementPoint.start();
        try {
            return fetchNodeWithParams(classifierName, ownNodeId, true, false, false);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getNodeWithAttrs(
     * java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodeDTO getNodeWithAttrs(final String ownNodeId, final String classifierName, final boolean reduce) {
        MeasurementPoint.start();
        try {
            return fetchNodeWithParams(classifierName, ownNodeId, true, true, reduce);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Internal node fetch method.
     * @param classifierName classifier name
     * @param ownNodeId node id
     * @param fetchDirectChildren whether to fetch next level children
     * @param fetchAttrs whether to fetch attributes
     * @param reduceAttrs whether to reduce fetched attributes
     * @return node DTO
     */
    private ClsfNodeDTO fetchNodeWithParams(String classifierName, String ownNodeId,
                                            boolean fetchDirectChildren,
                                            boolean fetchAttrs,
                                            boolean reduceAttrs) {

        // 1. Node
        CachedClassifierNode cachedNode = cacheComponent.getNode(classifierName, ownNodeId);
        if (Objects.isNull(cachedNode)) {
            return null;
        }

        // 2. Convert
        ClsfNodeDTO result = CachedClassiferNodeToClsfNodeDTOConverter.convert(cachedNode);

        // 3. Children
        if (fetchDirectChildren && !cachedNode.getChildren().isEmpty()) {
            cachedNode.getChildren().stream()
                    .map(id -> cacheComponent.getNode(classifierName, id))
                    .filter(Objects::nonNull)
                    .map(CachedClassiferNodeToClsfNodeDTOConverter::convert)
                    .collect(Collectors.toCollection(result::getChildren));
        }

        // 4. Attributes
        if (fetchAttrs) {

            List<CachedClassifierNode> subtree = Objects.isNull(cachedNode.getParentNodeId())
                    ? Collections.emptyList()
                    : cacheComponent.getBranch(classifierName, cachedNode.getParentNodeId());

            if (reduceAttrs) {

                Map<String, ClsfNodeAttrDTO> attributes = new HashMap<>();
                for (CachedClassifierNode node : subtree) {
                    attributes.putAll(node.getAttributesAsList().stream()
                            .map(CachedClassifierNodeAttributeToClsfNodeAttrDTOConverter::convert)
                            //.peek(attr -> attr.setInherited(true))
                            .collect(Collectors.toMap(ClsfNodeAttrDTO::getAttrName, v -> v)));
                }

                attributes.putAll(cachedNode.getAttributesAsList().stream()
                        .map(CachedClassifierNodeAttributeToClsfNodeAttrDTOConverter::convert)
                        //.peek(attr -> attr.setInherited(false))
                        .collect(Collectors.toMap(ClsfNodeAttrDTO::getAttrName, v -> v)));

                result.addAttrs(attributes.values());

            } else {

                result.addAttrs(
                        subtree.stream()
                                .map(CachedClassifierNode::getAttributesAsList)
                                .flatMap(Collection::stream)
                                .map(CachedClassifierNodeAttributeToClsfNodeAttrDTOConverter::convert)
                                .peek(attr -> attr.setInherited(true))
                                .collect(Collectors.toList()));

                result.addAttrs(
                        cachedNode.getAttributesAsList().stream()
                                .map(CachedClassifierNodeAttributeToClsfNodeAttrDTOConverter::convert)
                                .peek(attr -> attr.setInherited(false))
                                .collect(Collectors.toList()));
            }
        }

        return result;
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#buildBranchToRoot(
     * java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodeDTO buildBranchToRoot(List<String> nodeIds, String classifierName) {

        MeasurementPoint.start();
        try {
            Map<String, ClsfNodeDTO> subtree = new HashMap<>();
            for (String nodeId : nodeIds) {

                String nextId = nodeId;
                do {

                    if (subtree.containsKey(nextId)) {
                        nextId = null;
                    } else {

                        CachedClassifierNode cachedNode = cacheComponent.getNode(classifierName, nextId);

                        // Invalid subtree
                        if (Objects.isNull(cachedNode)) {
                            LOGGER.warn("Fetched null from cache for classifier name [{}] and node id [{}]!", classifierName, nextId);
                            break;
                        }

                        ClsfNodeDTO current = CachedClassiferNodeToClsfNodeDTOConverter.convert(cachedNode);

                        // Add self to parent for wide sub-tree
                        ClsfNodeDTO parent = subtree.get(cachedNode.getParentNodeId());
                        if (Objects.nonNull(parent)) {
                            parent.getChildren().add(current);
                        }

                        // Add first child to self. The others will add themselves.
                        for (String childId : cachedNode.getChildren()) {

                            ClsfNodeDTO child = subtree.get(childId);
                            if (Objects.nonNull(child)) {
                                current.getChildren().add(child);
                            }
                        }

                        nextId = current.getParentId();
                        subtree.put(current.getNodeId(), current);
                    }

                } while (Objects.nonNull(nextId));
            }

            return subtree.get(String.join(DOT, classifierName, ROOT_NODE_POSTFIX));

        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getAllClsfAttr(
     * java.lang.String)
     */
    @Override
    public List<ClsfNodeAttrDTO> getAllClsfAttr(String classifierName) {
        MeasurementPoint.start();
        try {
            List<ClsfNodeAttrPO> attrPOs = clsfDao.getAllClassifierAttrs(classifierName);
            return ClsfNodeAttrPOToDTOConverter.convertNodeAttrs(attrPOs, 0);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getIdsToRoot(java.
     * lang.String, java.lang.String)
     */
    @Override
    public List<String> getIdsToRoot(String classifierNodeId, String classifierName) {

        MeasurementPoint.start();
        try {
            List<String> ids = new ArrayList<>();
            String nextId = classifierNodeId;
            do {

                CachedClassifierNode cachedNode = cacheComponent.getNode(classifierName, nextId);
                if (Objects.isNull(cachedNode)) {
                    return Collections.emptyList();
                }

                ids.add(0, cachedNode.getNodeId());
                nextId = cachedNode.getParentNodeId();

            } while (Objects.nonNull(nextId));

            return ids;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * addFullFilledClassifierByCode(com.unidata.mdm.backend.service.classifier.
     * dto.ClsfDTO)
     */
    @Override
    @Transactional
    public boolean addFullFilledClassifierByCode(FullClassifierDef toSave, String importFromUser) {
        List<ClassifierNodeDef> nodes = toSave.getClassifierNodes();
        final String codePattern = toSave.getClassifier().getCodePattern();
        for (ClassifierNodeDef node : nodes) {
            if (node.getParentId() != null && node.getCode() != null && !CodeParser.isValidCodeForPattern(node.getCode(), codePattern)) {
                throw new BusinessException(
                        "Pattern are not matching",
                        ExceptionId.EX_CLASSIFIER_NODE_CODE_INCORRECT,
                        node.getCode(),
                        codePattern,
                        toSave.getClassifier().getName()
                );
            }
            if (!StringUtils.isEmpty(node.getCode())) {
                final String[] codes = CodeParser.extractGroups(node.getCode(), codePattern);
                if (codes.length == 1) {
                    node.setParentId(rootNodeId(toSave.getClassifier().getName()));
                    node.setId(CodeParser.toNodeId(node.getCode()));
                } else {
                    node.setParentId(CodeParser.extractParentId(node.getCode(), codePattern));
                    node.setId(CodeParser.toNodeId(node.getCode()));
                }
            } else {
                node.setId(rootNodeId(toSave.getClassifier().getName()));
                node.setParentId(null);
            }
        }
        return addFullFilledClassifierByIds(toSave, importFromUser);
    }

    private String rootNodeId(final String classifierName) {
        return classifierName + ".root";
    }

    @Override
    @Transactional
    public boolean addFullFilledClassifierByIds(final FullClassifierDef toSave) {
        return addFullFilledClassifierByIds(toSave, SecurityUtils.getCurrentUserName());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * addFullFilledClassifierByIds(com.unidata.mdm.backend.service.classifier.
     * dto.ClsfDTO)
     */
    @Override
    @Transactional
    public boolean addFullFilledClassifierByIds(final FullClassifierDef toSave, final String importFromUser) {
        validate(toSave);
        final String clsfName = toSave.getClassifier().getName();
        final CachedClassifier classifier = cacheComponent.getClassifier(clsfName);
        final boolean isUpdate = classifier != null;
        final ClsfDTO clsf = new ClsfDTO();
        clsf.setCodePattern(toSave.getClassifier().getCodePattern());
        clsf.setName(clsfName);
        clsf.setDisplayName(toSave.getClassifier().getDisplayName());
        clsf.setDescription(toSave.getClassifier().getDescription());
        clsf.setValidateCodeByLevel(toSave.getClassifier().isValidateCodeByLevel());

        final Collection<ClassifierNodeDef> classifierNodeDefs = orderTree(clsf, toSave.getClassifierNodes());

        //cacheComponent.destroyClassifier(clsfName);
        if (isUpdate) {
            clsf.setUpdatedAt(new Date());
            clsf.setUpdatedBy(SecurityUtils.getCurrentUserName());
            clsfDao.update(ClsfDTOToPOConverter.convert(clsf));
            clsfDao.removeAllNodesByClassifierName(classifier.getName());
        } else {
            createClassifier(clsf, false);
        }

        try {
            final List<List<ClsfNodeDTO>> nodes = saveClassifierNodes(clsf, classifierNodeDefs);
            nodes.forEach(batch ->
                    searchService.indexClassifierNodes(SecurityUtils.getCurrentUserStorageId(), batch)
            );
            if (isUpdate) {
                removeAllNodesForClassifierFromIndex(classifier.getName());
            }
        } finally {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    cacheComponent.destroyClassifier(clsfName);
                    changeNotifier.notifyClassifierChanged(ClassifierChangesEventType.REFRESH, Collections.singletonList(clsfName));
                    if (isUpdate) {
                        runClassifierReindexJob(clsf, importFromUser);
                    }
                }
            });
        }
        return isUpdate;
    }

    private void runClassifierReindexJob(final ClsfDTO clsf, String importFromUser) {
        final String typesForReindex = metaModelService.getClassifiedEntities(clsf.getName()).stream()
                .map(AbstractEntityDef::getName)
                .collect(Collectors.joining(SearchUtils.COMMA_SEPARATOR));
        final JobDTO reindexJob = new JobDTO();
        reindexJob.setDescription("Reindex classification");
        reindexJob.setName("Reindex classification Job");
        reindexJob.setEnabled(true);
        reindexJob.setJobNameReference(ReindexDataJobConstants.JOB_NAME);

        final Map<String, String> userReport = new HashMap<>();
        userReport.put(
                ReindexDataJobConstants.USER_REPORT_MESSAGE_PARAM,
                MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_IMPORT_UPDATE_STEP2_SUCCESS)
        );
        userReport.put(
                ReindexDataJobConstants.USER_REPORT_FAIL_MESSAGE_PARAM,
                MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_IMPORT_UPDATE_STEP2_FAIL)
        );

        String value = "";
        try {
            value = objectMapper.writeValueAsString(userReport);
        } catch (JsonProcessingException e) {
            LOGGER.error("Can't serialize user report data.", e);
        }
        reindexJob.setParameters(
                Arrays.asList(
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_TYPES, typesForReindex),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_UPDATE_MAPPINGS, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_CLEAN_INDEXES, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_RECORDS, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_MATCHING, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_RELATIONS, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_SKIP_DQ, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_SUPPRESS_DEFAULT_REPORT, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_SUPPRESS_CONSISTENCY_CHECK, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_BLOCK_SIZE, batchSize.longValue()),
                        new JobParameterDTO(JobReportConstants.USER_NAME_PARAM, importFromUser),
                        new JobParameterDTO(ReindexDataJobConstants.USER_REPORT_PARAM, value),
                        new JobParameterDTO(JobReportConstants.JOB_REPORT_TYPE, ClsfService.CLASSIFIERS_IMPORT)
                )
        );
        jobServiceExt.startSystemJob(reindexJob);
    }

    private void validate(FullClassifierDef classifierDef) {
        final String classifierName = classifierDef.getClassifier().getName();
        if (StringUtils.isBlank(classifierName)) {
            throw new DataProcessingException(
                    "Missing name of classifier.",
                    ExceptionId.EX_CLASSIFIER_FAILED_MISSING_FIELD,
                    "name"
            );
        }
        if (!NAME_PATTERN.matcher(classifierName).matches()) {
            throw new BusinessException(
                    "Incorrect name of classifier " + classifierName + ".",
                    ExceptionId.EX_CLASSIFIER_NAME_INCORRECT,
                    classifierName
            );
        }
        if (StringUtils.isBlank(classifierDef.getClassifier().getDisplayName())) {
            throw new DataProcessingException(
                    "Missing displayName of classifier " + classifierName + ".",
                    ExceptionId.EX_CLASSIFIER_FAILED_MISSING_FIELD,
                    "displayName"
            );
        }
        if (CollectionUtils.isNotEmpty(classifierDef.getClassifierNodes())) {
            classifierDef.getClassifierNodes().forEach(node -> {
                if (StringUtils.isBlank(node.getName())) {
                    throw new DataProcessingException(
                            "Missing name of node of classifier " + classifierName + ".",
                            ExceptionId.EX_CLASSIFIER_NODE_FAILED_MISSING_FIELD,
                            "name", classifierName
                    );
                }
                if (StringUtils.isBlank(node.getId())) {
                    throw new DataProcessingException(
                            "Conversion to classifier failed. Missing nodeId of node " + node.getName() + " of classifier " + classifierName + ".",
                            ExceptionId.EX_CLASSIFIER_NODE_FAILED_MISSING_FIELD,
                            "nodeId", classifierName
                    );
                }
                final List<AbstractClassifierAttributeDef> attrs = new ArrayList<>(node.getAttributes());
                attrs.addAll(node.getArrayAttributes());
                if (CollectionUtils.isNotEmpty(attrs)) {
                    for (int i = 0; i < attrs.size(); i++) {
                        final AbstractClassifierAttributeDef attribute = attrs.get(i);
                        if (StringUtils.isBlank(attribute.getName())) {
                            throw new DataProcessingException(
                                    "Conversion to classifier failed. Missing name of attr of node " + node.getName(),
                                    ExceptionId.EX_CLASSIFIER_NODE_ATTR_FAILED_MISSING_FIELD,
                                    "name", i, node.getName(), classifierName
                            );
                        }
                        if (StringUtils.isBlank(attribute.getDisplayName())) {
                            throw new DataProcessingException(
                                    "Conversion to classifier failed. Missing displayName of attr of node " + node.getName(),
                                    ExceptionId.EX_CLASSIFIER_NODE_ATTR_FAILED_MISSING_FIELD,
                                    "displayName", i, node.getName(), classifierName
                            );
                        }
                        if (attribute.getValueType() == null && attribute.getLookupEntityCodeAttributeType() == null && attribute.getEnumDataType() == null) {
                            throw new DataProcessingException(
                                    "Conversion to classifier failed. Missing valueType of attr of node " + node.getName(),
                                    ExceptionId.EX_CLASSIFIER_NODE_ATTR_FAILED_MISSING_FIELD,
                                    "type", i, node.getName(), classifierName
                            );
                        }
                    }
                }
            });
        }
    }

    private List<List<ClsfNodeDTO>> saveClassifierNodes(
            final ClsfDTO clsf,
            final Collection<ClassifierNodeDef> nodeDefs
    ) {
        final String clsfName = clsf.getName();

        final List<List<ClsfNodeDTO>> nodes = nodeDefs.stream()
                .map(converterFromClassifierNodeDefToClsfNodeDTO(clsf, clsfName))
                .collect(new BatchCollector<>(batchSize.get()));

        nodes.forEach(batch -> {
            batchSaveClsfNodes(clsfName, batch);
            batchSaveAttrs(clsfName, batch);
        });
        return nodes;
    }

    private Collection<ClassifierNodeDef> orderTree(
            final ClsfDTO classifier,
            final List<ClassifierNodeDef> nodeDefs
    ) {
        final String classifierName = classifier.getName();
        final List<ClassifierNodeDef> orderedTree = new ArrayList<>();
        final Set<String> nodesIdsInTree = new HashSet<>();
        final Set<Triple<String, String, String>> nodesKeys = new HashSet<>();
        final Set<String> nodesIds = new HashSet<>();
        final Map<String, List<ClassifierNodeDef>> nodesNotInTree = new TreeMap<>();
        final List<String> rootNodesNames = new ArrayList<>();
        final List<String> duplicatedNodes = new ArrayList<>();
        final List<String> duplicatedIds = new ArrayList<>();
        nodeDefs.forEach(
                classifierNodeDef -> {
                    final Triple<String, String, String> nodeKey = Triple.of(
                            classifierNodeDef.getParentId(), classifierNodeDef.getCode(), classifierNodeDef.getName()
                    );
                    if (!nodesKeys.add(nodeKey)) {
                        duplicatedNodes.add(classifierNodeDef.getName());
                    }
                    if (!nodesIds.add(classifierNodeDef.getId())) {
                        duplicatedIds.add(classifierNodeDef.getId());
                    }
                    if (StringUtils.isEmpty(classifierNodeDef.getParentId())) {
                        rootNodesNames.add(classifierNodeDef.getName());
                        classifierNodeDef.setName(classifier.getDisplayName());
                    }
                    if (StringUtils.isEmpty(classifierNodeDef.getParentId()) || nodesIdsInTree.contains(classifierNodeDef.getParentId())) {
                        nodesIdsInTree.add(classifierNodeDef.getId());
                        orderedTree.add(classifierNodeDef);
                        if (nodesNotInTree.containsKey(classifierNodeDef.getId())) {
                            orderedTree.addAll(nodesNotInTree.remove(classifierNodeDef.getId()));
                        }
                    } else {
                        if (!nodesNotInTree.containsKey(classifierNodeDef.getParentId())) {
                            nodesNotInTree.put(classifierNodeDef.getParentId(), new ArrayList<>());
                        }
                        nodesNotInTree.get(classifierNodeDef.getParentId()).add(classifierNodeDef);
                    }
                }
        );
        if (StringUtils.isNotEmpty(classifier.getCodePattern()) && !classifier.isValidateCodeByLevel() && !nodesNotInTree.isEmpty()) {
            orderedTree.addAll(resolveToClosestParent(classifier, nodesNotInTree, nodesIdsInTree));
            nodesNotInTree.clear();
        }
        validateClsfOrderedTree(classifierName, nodesNotInTree, rootNodesNames, duplicatedNodes, duplicatedIds);
        return orderedTree;
    }

    private List<ClassifierNodeDef> resolveToClosestParent(
            final ClsfDTO classifier,
            final Map<String, List<ClassifierNodeDef>> nodesNotInTree,
            final Set<String> nodesIdsInTree
    ) {
        return nodesNotInTree.entrySet().stream().flatMap(entry -> {
            final List<ClassifierNodeDef> nodes = entry.getValue();
            if (nodesIdsInTree.contains(entry.getKey())) {
                return nodes.stream();
            }
            final ClassifierNodeDef classifierNodeDef = nodes.get(0);
            final String parentId = findExsitParentIdForNode(classifier, classifierNodeDef, nodesIdsInTree);
            return nodes.stream().peek(node -> node.setParentId(parentId));
        }).collect(Collectors.toList());
    }

    private String findExsitParentIdForNode(
            final ClsfDTO classifier,
            final ClassifierNodeDef classifierNodeDef,
            final Set<String> nodesIdsInTree
    ) {
        final List<String> ids = CodeParser.extractParentIds(classifierNodeDef.getCode(), classifier.getCodePattern());
        return ids.stream()
                .filter(nodesIdsInTree::contains)
                .findFirst()
                .orElseGet(() -> rootNodeId(classifier.getName()));
    }

    private void validateClsfOrderedTree(
            final String classifierName,
            final Map<String, List<ClassifierNodeDef>> nodesNotInTree,
            final List<String> rootNodesNames,
            final List<String> duplicatedNodes,
            final List<String> duplicatedIds
    ) {
        if (duplicatedNodes.size() > 0) {
            throw new BusinessException(
                    "Found duplicate nodes " + duplicatedNodes + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_DUPLICATE_NODES,
                    classifierName,
                    duplicatedNodes
            );
        }
        if (duplicatedIds.size() > 0) {
            throw new BusinessException(
                    "Found duplicate ids " + duplicatedIds + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_DUPLICATE_IDS,
                    classifierName,
                    duplicatedIds
            );
        }
        if (rootNodesNames.size() > 1) {
            throw new BusinessException(
                    "More then one root nodes " + rootNodesNames + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_MORE_THEN_ONE_ROOT_NODES,
                    classifierName,
                    rootNodesNames
            );
        }
        if (!nodesNotInTree.isEmpty()) {
            final List<String> detachedNodes =
                    nodesNotInTree.values().stream()
                            .flatMap(Collection::stream)
                            .map(ClassifierNodeDef::getId)
                            .collect(Collectors.toList());
            throw new BusinessException(
                    "Nodes didn't attached to root " + detachedNodes + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_NOT_ATTACHED_NODES_TO_ROOT,
                    classifierName,
                    detachedNodes
            );
        }
    }

    private Function<ClassifierNodeDef, ClsfNodeDTO> converterFromClassifierNodeDefToClsfNodeDTO(
            final ClsfDTO clsf,
            final String clsfName
    ) {
        return classifierNodeDef -> {
            final ClsfNodeDTO clsfNodeDTO = convertToDTO(classifierNodeDef);
            validate(clsf, clsfNodeDTO, false);
            clsfNodeDTO.setClsfName(clsfName);
            return clsfNodeDTO;
        };
    }

    private ClsfNodeDTO convertToDTO(final ClassifierNodeDef classifierNodeDef) {
        ClsfNodeDTO clsNode = new ClsfNodeDTO(
                classifierNodeDef.getName(),
                classifierNodeDef.getId(),
                classifierNodeDef.getParentId(),
                classifierNodeDef.getCode(),
                classifierNodeDef.getDescription(),
                SecurityUtils.getCurrentUserName(),
                new Date(),
                classifierNodeDef.getAttributes().stream()
                        .map(simpleAttributeDef -> conversionService.convert(simpleAttributeDef, ClsfNodeSimpleAttrDTO.class))
                        .collect(Collectors.toList()),
                classifierNodeDef.getArrayAttributes().stream()
                        .map(arrayAttributeDef -> conversionService.convert(arrayAttributeDef, ClsfNodeArrayAttrDTO.class))
                        .collect(Collectors.toList())
        );
        clsNode.setCustomProperties(ClsCustomPropertyDefConverter.convertTo(classifierNodeDef.getCustomProperties()));
        return clsNode;
    }

    private void batchSaveClsfNodes(final String classifierName, final List<ClsfNodeDTO> nodes) {
        final List<ClsfNodePO> convert = ClsfNodeDTOToPOConverter.convert(nodes);
        convert.forEach(node -> {
            node.getNodeArrayAttrs().forEach(this::fillLookupEntityCodeAttributeType);
            node.getNodeSimpleAttrs().forEach(this::fillLookupEntityCodeAttributeType);
        });
        clsfDao.create(
                classifierName,
                convert
        );
    }

    private void batchSaveAttrs(final String clsfName, final List<ClsfNodeDTO> batch) {
        batch.stream()
                .flatMap(node ->
                        {
                            final List<ClsfNodeAttrPO> attrs = new ArrayList<>(
                                    ClsfNodeAttrDTOToPOConverter.convertSimpleAttrs(node.getNodeSimpleAttrs())
                            );
                            attrs.addAll(ClsfNodeAttrDTOToPOConverter.convertArrayAttrs(node.getNodeArrayAttrs()));
                            return attrs.stream()
                                    .map(attr -> Pair.of(node, attr));
                        }
                )
                .collect(new BatchCollector<>(batchSize.get()))
                .forEach(attrsBatch -> clsfDao.insertNodeAttrs(attrsBatch, clsfName));
    }

    private void removeAllNodesForClassifierFromIndex(String classifierName) {
        final FormField clsfNameField = strictString(ClassifierHeaderField.CLASSIFIER_NAME.getField(), classifierName);
        final SearchRequestContext searchContext = forClassifierElements()
                .form(FormFieldsGroup.createAndGroup(clsfNameField))
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build();
        searchService.deleteAll(searchContext);

        final List<AbstractEntityDef> classifiedEntities = metaModelService.getClassifiedEntities(classifierName);
        for (AbstractEntityDef classifiedEntity : classifiedEntities) {
            final SearchRequestContext sc = SearchRequestContext.builder(EntitySearchType.CLASSIFIER, classifiedEntity.getName())
                    .form(FormFieldsGroup.createAndGroup(clsfNameField))
                    .storageId(SecurityUtils.getCurrentUserStorageId())
                    .build();

            searchService.deleteAll(sc);
        }
    }

    /**
     * Convert to obj.
     *
     * @param value the value
     * @return the object
     */
    private Object convertToObj(ClassifierValueDef value) {
        if (value == null || value.getType() == null) {
            return null;
        }
        Object result = null;
        switch (value.getType()) {
            case BLOB:
            case CLOB:
                break;
            case BOOLEAN:
                result = value.isBoolValue();
                break;
            case DATE:
                result = value.getDateValue() != null
                        ? Date.from(value.getDateValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
                break;
            case TIME:
                result = value.getTimeValue() != null ? Date.from(
                        value.getTimeValue().atDate(LocalDate.of(1970, 1, 1)).atZone(ZoneId.systemDefault()).toInstant())
                        : null;
                break;
            case TIMESTAMP:
                result = value.getTimestampValue() != null
                        ? Date.from(value.getTimestampValue().atZone(ZoneId.systemDefault()).toInstant()) : null;
                break;
            case INTEGER:
                result = value.getIntValue();
                break;
            case NUMBER:
                result = value.getNumberValue();
                break;
            case STRING:
                result = value.getStringValue();
                break;
            default:
                break;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * getClassifierByNameWithAllNodes(java.lang.String)
     */
    @Override
    public ClsfDTO getClassifierByNameWithAllNodes(String classifierName) {
        MeasurementPoint.start();
        try {
            ClsfDTO result = getClassifierByName(classifierName);
            List<ClsfNodeDTO> nodes = ClsfNodePOToDTOConverter.convert(clsfDao.getAllNodes(classifierName));
            if (CollectionUtils.isEmpty(nodes)) {
                return result;
            }
            Map<String, ClsfNodeDTO> interim = new HashMap<>();
            for (ClsfNodeDTO node : nodes) {
                interim.put(node.getNodeId(), node);
            }
            interim.forEach((k, v) -> {
                if (StringUtils.isEmpty(v.getParentId())) {
                    result.setRootNode(v);
                } else {
                    interim.get(v.getParentId()).getChildren().add(v);
                }
            });
            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#findNodeByFullPath
     * (java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodeDTO findNodeByFullPath(String clsfName, String classifierPointer) {
        MeasurementPoint.start();
        try {
            ClsfNodePO nodePO = clsfDao.getNodeByPath(clsfName, classifierPointer);
            return ClsfNodePOToDTOConverter.convert(nodePO);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#findNodeByCode(
     * java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodeDTO findNodeByCode(String clsfName, String classifierPointer) {
        MeasurementPoint.start();
        try {
            ClsfNodePO nodePO = clsfDao.getNodeByCode(clsfName, classifierPointer);
            return ClsfNodePOToDTOConverter.convert(nodePO);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Removes the refs to clsf.
     *
     * @param clsfName the clsf name
     */
    private void removeRefsToClsf(String clsfName) {
        List<EntityDef> entityDefs = metaModelService.getEntitiesList();
        if (entityDefs.size() != 0) {
            for (EntityDef entityDef : entityDefs) {
                removeRefsToClsf(clsfName, entityDef);
            }
        }
        List<LookupEntityDef> lookupEntityDefs = metaModelService.getLookupEntitiesList();
        if (lookupEntityDefs.size() != 0) {
            for (LookupEntityDef lookupEntityDef : lookupEntityDefs) {
                removeRefsToClsf(clsfName, lookupEntityDef);
            }
        }
        metaDraftService.removeRefsToClsf(clsfName);
    }

    /**
     * Removes the refs to clsf.
     *
     * @param clsfName the clsf name
     * @param eDef the e def
     */
    private void removeRefsToClsf(String clsfName, LookupEntityDef eDef) {
        if (eDef == null || eDef.getClassifiers() == null || eDef.getClassifiers().size() == 0
                || !eDef.getClassifiers().contains(clsfName)) {
            return;
        }
        eDef.getClassifiers().remove(clsfName);
        UpdateModelRequestContext ctx = new UpdateModelRequestContextBuilder()
                .lookupEntityUpdate(Collections.singletonList(eDef))
                .skipRemoveElements(true)
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build();
        metaModelService.upsertModel(ctx);
    }

    /**
     * Removes the refs to clsf.
     *
     * @param clsfName the clsf name
     * @param eDef the e def
     */
    private void removeRefsToClsf(String clsfName, EntityDef eDef) {
        if (eDef == null || eDef.getClassifiers() == null || eDef.getClassifiers().size() == 0
                || !eDef.getClassifiers().contains(clsfName)) {
            return;
        }
        eDef.getClassifiers().remove(clsfName);
        List<NestedEntityDef> nestedEntityUpdate = metaModelService.getNestedEntitiesByTopLevelId(eDef.getName());
        UpdateModelRequestContext ctx = new UpdateModelRequestContextBuilder()
                .entityUpdate(Collections.singletonList(eDef))
                .nestedEntityUpdate(nestedEntityUpdate)
                .skipRemoveElements(true)
                .storageId(SecurityUtils.getCurrentUserStorageId()).build();
        metaModelService.upsertModel(ctx);
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String batchSizePropertyKey = UnidataConfigurationProperty.CLASSIFIER_IMPORT_BATCH_SIZE.getKey();
        updates
                .filter(values ->
                        values.containsKey(batchSizePropertyKey) && values.get(batchSizePropertyKey).isPresent()
                )
                .map(values -> (Integer) values.get(batchSizePropertyKey).get())
                .subscribe(batchSize::set);
    }

    @Override
    public int getClassifierBatchSize(){
        return batchSize.get();
    }
    /**
     * Collect stream into fixed size batches.
     * !Non-thread safe.! Don't use in parallel streams.
     *
     * @param <T> Type of elements in batches.
     */
    public static class BatchCollector<T> implements Collector<T, List<List<T>>, List<List<T>>> {

        private final int batchSize;

        private final int[] counterHolder = {0};

        public BatchCollector(int batchSize) {
            this.batchSize = batchSize;
        }

        @Override
        public Supplier<List<List<T>>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<List<T>>, T> accumulator() {
            return (lists, value) -> {
                final int currentPosition = counterHolder[0]++;
                final int currentBatchIndex = currentPosition / batchSize;
                if (currentBatchIndex == lists.size()) {
                    lists.add(new ArrayList<>(batchSize));
                }
                lists.get(currentBatchIndex).add(value);
            };
        }

        @Override
        public BinaryOperator<List<List<T>>> combiner() {
            return (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            };
        }

        @Override
        public Function<List<List<T>>, List<List<T>>> finisher() {
            return (e) -> e;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Collector.Characteristics.IDENTITY_FINISH);
        }
    }

    @Override
    public void removeCodeAttrsValues(final LookupEntityDef lookupEntity, final String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        clsfDao.removeCodeAttrsValues(lookupEntity.getName(), value);
    }

    @Override
    public List<ClsfNodeDTO> findNodesWithLookupAttributes(LookupEntityDef lookupEntity) {
        final List<ClsfNodePO> nodesWithLookupAttributes = clsfDao.findNodesWithLookupAttributes(lookupEntity.getName());
        if (CollectionUtils.isNotEmpty(nodesWithLookupAttributes)) {
            return ClsfNodePOToDTOConverter.convert(nodesWithLookupAttributes);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean containsCodeAttrsValue(LookupEntityDef lookupEntity, String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return clsfDao.containsCodeAttrsValue(lookupEntity.getName(), value);
    }

    @Override
    public void removeCodeAttrsWithLookupsLinks(Collection<String> lookupEntitiesIds) {
        if (CollectionUtils.isEmpty(lookupEntitiesIds) || StringUtils.isAllBlank(lookupEntitiesIds.toArray(new String[0]))) {
            return;
        }
        clsfDao.removeCodeAttrsWithLookupsLinks(lookupEntitiesIds);
    }
}
