package com.unidata.mdm.backend.service.data.listener.classifier;

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersCommonComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;

/**
 * Responsible for resolve node id if it doesn't exist
 */
public class ClassifierUpsertValidateBeforeExecutor implements DataRecordBeforeExecutor<UpsertClassifierDataRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierUpsertValidateBeforeExecutor.class);
    /**
     * Classifier cache
     */
    @Autowired
    private ClsfService classifierService;
    /**
     * Classifiers common component.
     */
    @Autowired
    private ClassifiersCommonComponent classifiersCommonComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertClassifierDataRequestContext ctx) {

        // 1. Check general context validity
        if (!ctx.isValidClassifierKey()) {
            throw new BusinessException("Invalid classifier data supplied for upsert",
                    ExceptionId.EX_DATA_UPSERT_INCORRECT_CLASSIFIER,
                    ctx.getClassifierName(), ctx.getClassifierNodeId());
        }

        // 2. Check for existing keys. Omit next section for existing keys.
        ClassifierKeys classifierKeys;
        if (ctx.isBatchUpsert() && ctx.isInitialLoad()) {
            classifierKeys = null;
        } else {
            classifierKeys = ctx.classifierKeys() != null
                ? ctx.classifierKeys()
                : classifiersCommonComponent.identify(ctx);
        }

        // 3. Pre-resolve node id, if code or name supplied and the record is a new one (no keys)
        String resolvedNodeId = null;
        if (StringUtils.isNoneBlank(ctx.getClassifierNodeId())) {
            resolvedNodeId = ctx.getClassifierNodeId();
        } else if (StringUtils.isNoneBlank(ctx.getClassifierName()) && StringUtils.isNoneBlank(ctx.getClassifierNodeCode())) {
            ClsfNodeDTO node = classifierService.findNodeByCode(ctx.getClassifierName(), ctx.getClassifierNodeCode());
            resolvedNodeId = node == null ? null : node.getNodeId();
        } else if (StringUtils.isNoneBlank(ctx.getClassifierName()) && StringUtils.isNoneBlank(ctx.getClassifierNodeName())) {
            ClsfNodeDTO node = classifierService.findNodeByFullPath(ctx.getClassifierName(), ctx.getClassifierNodeName());
            resolvedNodeId = node == null ? null : node.getNodeId();
        }

        // 4. Save resolved node id
        if (Objects.isNull(ctx.getClassifierNodeId())) {
            ctx.putToStorage(StorageId.CLASSIFIERS_UPSERT_RESOLVED_NODE_ID, resolvedNodeId);
        }

        // 5. Load and check attributes
        ClsfNodeDTO node = classifierService.getNodeWithAttrs(resolvedNodeId, ctx.getClassifierName(), true);
        if (node == null) {
            throw new BusinessException("Record has unavailable classifier node",
                    ExceptionId.EX_DATA_UPSERT_UNAVAILABLE_CLASSIFIER_NODE,
                    ctx.getClassifierName(), resolvedNodeId);
        }

        Map<String, ClsfNodeAttrDTO> attrs = node.getNodeAttrs().stream()
                .collect(Collectors.toMap(ClsfNodeAttrDTO::getAttrName, c -> c));

        Collection<String> requiredInLevelAttrs = attrs.values().stream().filter(entry -> !entry.isNullable())
                .map(ClsfNodeAttrDTO::getAttrName).collect(Collectors.toList());

        for (SimpleAttribute<?> simpleAttribute : ctx.getClassifier().getSimpleAttributes()) {
            String attrName = simpleAttribute.getName();
            ClsfNodeAttrDTO attr = attrs.get(attrName);

            if (attr == null) {
                final String message = "Attribute '{}' supplied for upsert is missing in the model. Upsert rejected.";
                LOGGER.warn(message, attrName);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_MISSING_ATTRIBUTE, attrName);
            }

            checkSimpleAttributeValueByType(simpleAttribute, DataType.valueOf(attr.getDataType().name()), attrName);
            if (Objects.nonNull(simpleAttribute.getValue())) {
                requiredInLevelAttrs.remove(attrName);
            }
        }

        if (!requiredInLevelAttrs.isEmpty()) {
            final String message = "Some required attributes was not given. {}";
            LOGGER.warn(message, requiredInLevelAttrs);
            throw new BusinessException(message, ExceptionId.EX_DATA_UPSERT_REQUIRED_ATTRS_IS_NOT_PRESENTED,
                    requiredInLevelAttrs.stream().map(name -> attrs.get(name).getDisplayName()).collect(Collectors.toList()));
        }

        // 6. Finally set action mark
        UpsertAction action = classifierKeys == null
                ? UpsertAction.INSERT
                : UpsertAction.UPDATE;

        ctx.putToStorage(StorageId.CLASSIFIERS_UPSERT_EXACT_ACTION, action);
        ctx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, new Date(System.currentTimeMillis()));

        return true;
    }

    /**
     * Check expected value and type attribute.
     *
     * @param attr         the attribute
     * @param expectedType value type
     */
    private void checkSimpleAttributeValueByType(SimpleAttribute<?> attr, DataType expectedType, String attrPath) {

        // Some value has been set, check the schema conformity
        if (attr.getDataType() != expectedType) {
            final String message = "Attribute {} supplied for upsert has type {} "
                    + "while type attribute {} is expected from the model. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
            throw new DataProcessingException(message, EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE,
                    attrPath,
                    attr.getDataType().name(),
                    expectedType.name());
        }
    }
}
