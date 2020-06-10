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
package com.unidata.mdm.backend.common.exception;

import com.unidata.mdm.backend.common.service.DataQualityService;

/**
 * @author Mikhail Mikhailov
 * Exception IDs.
 */
public enum ExceptionId {

    // DQ: 0000 - 0100
    /**
     * CF not found by ID.
     */
    EX_DQ_CLEANSE_FUNCTION_NOT_FOUND("app.dq.cleanse.function.not.found"),
    /**
     * CF not found by ID (from {@link DataQualityService}).
     */
    EX_DQ_CLEANSE_FUNCTION_NOT_FOUND_DQS("app.dq.cleanse.function.not.found"),
    /**
     * Required value is missing.
     */
    EX_DQ_CLEANSE_FUNCTION_REQUIRED_VALUE_MISSING("app.dq.cleanse.function.required.value.missing"),
    /**
     * Execution context mode is not supported by function.
     */
    EX_DQ_EXECUTION_CONTEXT_MODE_NOT_SUPPORTED("app.dq.execution.context.mode.not.supported"),
    /**
     * Error, while executing cleanse function {}: .
     */
    EX_DQ_CLEANSE_FUNCTION_EXEC("app.dq.cleanse.function.execution"),
    /**
     * Unknown exception caught while executing CF.
     */
    EX_DQ_CLEANSE_FUNCTION_EXCEPTION_CAUGHT("app.dq.cleanse.function.exception.caught"),
    // Search: 0100 - 0150
    /**
     * Elasticsearch exception caught.
     */
    EX_SEARCH_ES_ESC_CAUGHT("app.search.searchElasticSearchExceptionCaught"),
    /**
     * Elasticsearch exception caught.
     */
    EX_SEARCH_ES_NO_MAPPING_FOUND("app.search.no.mapping.found"),
    /**
     * No entity name given for bulk delete.
     */
    EX_SEARCH_BULK_DELETE_NO_ENTITY_NAME("app.search.search.bulk.delete.no.entity.name"),
    /**
     * Invalid fields supplied for term query.
     */
    EX_SEARCH_INVALID_TERM_FIELDS("app.search.invalidFieldsTermQuery"),
    /**
     * IO failure from XContentFactory caught..
     */
    EX_SEARCH_MAPPING_IO_FAILURE("app.search.mappingIOFailure"),
    /**
     * Invalid mapping of unknown type supplied.
     */
    EX_SEARCH_MAPPING_TYPE_UNKNOWN("app.search.mappingUnknownType"),
    /**
     * Nested entity not found in model.
     */
    EX_SEARCH_MAPPING_NESTED_ENTITY_NOT_FOUND("app.search.mappingUnknownNestedEntity"),
    /**
     * Update document failed.
     */
    EX_SEARCH_BUILD_DQ_ERRORS_FAILED("app.search.build.dq.errors.failed"),
    /**
     * Update document failed.
     */
    EX_SEARCH_UPDATE_DOCUMENT_FAILED("app.search.updateDocumentFailed"),
    /**
     * Update record document failed.
     */
    EX_SEARCH_UPDATE_RECORD_DOCUMENT_FAILED("app.search.updateRecordDocumentFailed"),
    /**
     * Update relation document failed.
     */
    EX_SEARCH_UPDATE_RELATION_DOCUMENT_FAILED("app.search.updateRelationDocumentFailed"),
    /**
     * Update classifier document failed.
     */
    EX_SEARCH_UPDATE_CLASSIFIER_DOCUMENT_FAILED("app.search.updateClassifierDocumentFailed"),
    /**
     * Mark document failed.
     */
    EX_SEARCH_MARK_DOCUMENT_FAILED("app.search.markFailed"),
    /**
     * Case when filter has incorrect combination.
     */
    EX_SEARCH_UNAVAILABLE_FACETS_COMBINATION("app.search.facets.combination"),
    /**
     * Too many results of search.
     */
    EX_SEARCH_CLASSIFIERS_META_RESULT_TOO_MUCH("app.search.classifiers.notMuch"),
    /**
     * Complex related request is incorrect
     */
    EX_SEARCH_COMPLEX_RELATED_REQUEST_INCORRECT("app.search.complex.related.incorrect"),
    /**
     * Try to mark(update) fields which not linked with search request
     */
    EX_SEARCH_NOT_RELATED_SEARCH_TYPES_IN_MARK_OPERATION("app.search.mark.not.linked.search.types"),
    /**
     * Illegal child type i npost processing.
     */
    EX_SEARCH_ILLEGAL_CHILD_TYPE_FOR_POST_PROCESSING("app.search.illegal.child.type.for.postprocessing"),
    /**
     * Exception with run before user exit search
     */
    EX_SEARCH_BEFORE_USER_EXIT_EXCEPTION("app.search.before.user.exit.exception"),
    /**
     * Exception with run after user exit search
     */
    EX_SEARCH_AFTER_USER_EXIT_EXCEPTION("app.search.after.user.exit.exception"),
    // Meta: 0151 - 0200
    /**
     * The 'to' side containment entity '{}' of the relation '{}' not found in model.
     */
    EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_INIT("app.meta.to.containment.entity.not.found.init"),
    /**
     * The 'to' side containment entity '{}' of the relation '{}' not found in update.
     */
    EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_UPDATE("app.meta.to.containment.entity.not.found.update"),
    /**
     * Entity not found.
     */
    EX_META_ENTITY_NOT_FOUND("app.meta.entityNotFound"),
    /**
     * Source system already exists.
     */
    EX_META_SOURCE_SYSTEM_ALREADY_EXISTS("app.meta.sourceSystemAlreadyExists"),
    /**
     * Meta data not found
     */
    EX_META_NOT_FOUND("app.meta.notFound"),
    /**
     * Root group is absent
     */
    EX_META_ROOT_GROUP_IS_ABSENT("app.meta.groupIsAbsent"),
    /**
     * Name or title of group is absent
     */
    EX_META_GROUP_NAME_OR_TITLE_ABSENT("app.meta.group.name_title.absent"),
    /**
     * Lookup Entity not found.
     */
    EX_META_LOOKUP_ENTITY_NOT_FOUND("app.meta.lookupEntityNotFound"),
    /**
     * Cannot marshal model.
     */
    EX_META_CANNOT_MARSHAL_MODEL("app.meta.cannotMarshalModel"),
    /**
     * Cannot unmarshal model.
     */
    EX_META_CANNOT_UNMARSHAL_MODEL("app.meta.cannotUnmarshalModel"),
    /**
     * Cannot marshal classifier node.
     */
    EX_META_CANNOT_MARSHAL_CLASSIFIER_NODE("app.meta.marshal.classifier.node"),
    /**
     * Cannot marshal classifier.
     */
    EX_META_CANNOT_MARSHAL_CLASSIFIER("app.meta.marshal.classifier"),
    /**
     * Cannot unmarshal classifier node.
     */
    EX_META_CANNOT_UNMARSHAL_CLASSIFIER_NODE("app.meta.unmarshal.classifier.node"),
    /**
     * Cannot unmarshal full classifier
     */
    EX_META_CANNOT_UNMARSHAL_FULL_CLASSIFIER("app.meta.unmarshal.full.classifier"),
    /**
     * Metadata service failed to initialize.
     */
    EX_META_INIT_METADATA_FAILED("app.meta.metadataServiceFailedToInitilalize"),
    /**
     * Metadata insert of an object failed.
     */
    EX_META_INSERT_FAILED("app.meta.metadataInsertFailed"),
    /**
     * Metadata update of an object failed.
     */
    EX_META_UPDATE_FAILED("app.meta.metadataUpdateFailed"),
    /**
     * Metadata delete of an object failed.
     */
    EX_META_DELETE_FAILED("app.meta.metadataDeleteFailed"),
    /**
     * Cannot marshal cleanse function.
     */
    EX_META_CANNOT_MARSHAL_CLEANSE_FUNCTION("app.meta.cannotMarshallCleanseFunction"),
    /**
     * Cannot marshal composite cleanse function.
     */
    EX_META_CANNOT_MARSHAL_COMPOSITE_CLEANSE_FUNCTION("app.meta.cannotMarshallCompositeCleanseFunction"),
    /**
     * Cannot marshal cleanse function group.
     */
    EX_META_CANNOT_MARSHAL_CLEANSE_FUNCTION_GROUP("app.meta.cannotMarshallCleanseFunctionGroup"),
    /**
     * Cannot marshal source system.
     */
    EX_META_CANNOT_MARSHAL_SOURCE_SYSTEM("app.meta.cannotMarshallSourceSystem"),
    /**
     * Cannot marshal enumeration.
     */
    EX_META_CANNOT_MARSHAL_ENUMERATION("app.meta.cannotMarshallEnumeration"),
    /**
     * Cannot marshal lookup entity.
     */
    EX_META_CANNOT_MARSHAL_LOOKUP_ENTITY("app.meta.cannotMarshallLookupEntity"),
    /**
     * Cannot marshal nested entity.
     */
    EX_META_CANNOT_MARSHAL_NESTED_ENTITY("app.meta.cannotMarshallNestedEntity"),
    /**
     * Cannot marshal entity.
     */
    EX_META_CANNOT_MARSHAL_ENTITY("app.meta.cannotMarshallEntity"),
    /**
     * Cannot marshal relation.
     */
    EX_META_CANNOT_MARSHAL_RELATION("app.meta.cannotMarshallRelation"),
    /**
     * Cannot unmarshal cleanse function.
     */
    EX_META_CANNOT_UNMARSHAL_CLEANSE_FUNCTION("app.meta.cannotUnmarshallCleanseFunction"),
    /**
     * Cannot unmarshal composite cleanse function.
     */
    EX_META_CANNOT_UNMARSHAL_COMPOSITE_CLEANSE_FUNCTION("app.meta.cannotUnmarshallCompositeCleanseFunction"),
    /**
     * Cannot unmarshal cleanse function group.
     */
    EX_META_CANNOT_UNMARSHAL_CLEANSE_FUNCTION_GROUP("app.meta.cannotUnmarshallCleanseFunctionGroup"),
    /**
     * Cannot unmarshal source system.
     */
    EX_META_CANNOT_UNMARSHAL_SOURCE_SYSTEM("app.meta.cannotUnmarshallSourceSystem"),
    /**
     * Cannot unmarshal enumeration.
     */
    EX_META_CANNOT_UNMARSHAL_ENUMERATION("app.meta.cannotUnmarshallEnumeration"),
    /**
     * Cannot unmarshal lookup entity.
     */
    EX_META_CANNOT_UNMARSHAL_LOOKUP_ENTITY("app.meta.cannotUnmarshallLookupEntity"),
    /**
     * Cannot unmarshal nested entity.
     */
    EX_META_CANNOT_UNMARSHAL_NESTED_ENTITY("app.meta.cannotUnmarshallNestedEntity"),
    /**
     * Cannot unmarshal entity.
     */
    EX_META_CANNOT_UNMARSHAL_ENTITY("app.meta.cannotUnmarshallEntity"),
    /**
     * Cannot unmarshal relation.
     */
    EX_META_CANNOT_UNMARSHAL_RELATION("app.meta.cannotUnmarshallRelation"),
    /**
     * Cannot assemble model.
     */
    EX_META_CANNOT_ASSEMBLE_MODEL("app.meta.cannotAssembleModel"),
    /**
     * Incorrect meta model
     */
    EX_META_IS_INCORRECT("app.meta.incorrect"),
    /**
     * Incorrect meta model
     */
    EX_DRAFT_IS_INCORRECT("app.draft.incorrect"),
    /**
     * Import model, invalid content type.
     */
    EX_META_IMPORT_MODEL_INVALID_CONTENT_TYPE("app.meta.importModelInvalidContentType"),
    /**
     * Import model invalid file format.
     */
    EX_META_IMPORT_MODEL_INVALID_FILE_FORMAT("app.meta.importModelInvalidFileFormat"),
    /**
     * Group is absent.
     */
    EX_META_GROUP_IS_ABSENT("app.meta.entityGroup.absent"),
    /**
     * Start period of entity before global range of dates
     */
    EX_META_PERIOD_START_BEFORE_GLOBAL_PERIOD("app.meta.periodStart.invalid"),
    /**
     * Main displayable attribute absent
     */
    EX_META_MAIN_DISPLAYABLE_ATTR_ABSENT("app.meta.main.displayable.attr.absent"),
    /**
     * End period of entity after global range of dates
     */
    EX_META_PERIOD_END_AFTER_GLOBAL_PERIOD("app.meta.periodEnd.invalid"),

    /**
     * Measured settings is not presented
     */
    EX_META_MEASUREMENT_SETTINGS_SHOULD_BE_DEFINE("app.meta.measurement.settings.not.define"),
    /**
     * Measured settings is not allowed
     */
    EX_META_MEASUREMENT_SETTINGS_NOT_ALLOW("app.meta.measurement.settings.not.allow"),
    /**
     * Measured settings refer to not presented value
     */
    EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINE_VALUE("app.meta.measurement.settings.refer.undefine.value"),
    /**
     * Measured settings refer to not presented unit
     */
    EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINE_UNIT("app.meta.measurement.settings.refer.undefine.unit"),
    /**
     *  A Hidden attr must be also read only
     */
    EX_META_ATTR_CAN_NOT_BE_HIDDEN_AND_NOT_READ_ONLY("app.meta.attr.settings.hidden.!readOnly"),
    /**
     * A Main displayable attr must be displayable in the same time
     */
    EX_META_ATTR_CAN_NOT_BE_MAIN_DISPLAYABLE_AND_NOT_DISPLAYABLE("app.meta.attr.settings.mainDisplayable.!displayable"),
    /**
     * A Requared attr can be read only in the same time
     */
    EX_META_ATTR_CAN_NOT_BE_REQUARED_AND_READ_ONLY("app.meta.attr.settings.requared.readOnly"),
    /**
     * A displayable attr can not be hidden in the same time
     */
    EX_META_ATTR_CAN_NOT_BE_DISPLAYABLE_AND_HIDDEN("app.meta.attr.settings.displayable.hidden"),
    /**
     * Complex attribute has the same name as classifier
     */
    EX_META_IDENTICAL_NAMES("app.meta.identical.names"),
    /**
     * Code attribute is absent.
     */
    EX_META_CODE_ATTRIBUTE_IS_ABSENT("app.meta.codeAttribute.absent"),
    /**
     * Code attribute is incorrect.
     */
    EX_META_CODE_ATTRIBUTE_IS_INCORRECT("app.meta.codeAttribute.incorrect"),
    /**
     * Simple attribute is incorrect
     */
    EX_META_SIMPLE_ATTRIBUTE_IS_INCORRECT("app.meta.simpleAttribute.incorrect"),
    /**
     * Cannot unmarshal group.
     */
    EX_META_CANNOT_UNMARSHAL_GROUP("app.meta.cannotUnmarshalGroup"),
    /**
     * Model contains duplicates in names of nested entities part1.
     */
    EX_META_NESTED_ENTITIES_DUPLICATE1("app.meta.nestedEntitiesIsDuplicated1"),
    /**
     * Model contains duplicates in names of nested entities part2.
     */
    EX_META_NESTED_ENTITIES_DUPLICATE2("app.meta.nestedEntitiesIsDuplicated2"),
    /**
     * Model element without id
     */
    EX_META_MODEL_ELEMENT_WITHOUT_ID("app.meta.element.withoutId"),
    /**
     * Model element is not valid
     */
    EX_META_MODEL_ELEMENT_NOT_VALID("app.meta.element.notValid"),
    /**
     * Relation hasn't one of the side
     */
    EX_META_RELATION_SIDE_IS_ABSENT("app.meta.relation.side.absent"),
    /**
     * Name of a top level object is reserved.
     */
    EX_META_RESERVED_TOP_LEVEL_NAME("app.meta.reserved.toplevel.name"),
    /**
     * Invalid input to check link sysrule generator.
     */
    EX_META_LINK_CHECK_RULE_INVALID_INPUT("app.meta.link.check.rule.invalid.input"),
    /**
     * Invalid input to check link consystence sysrule generator.
     */
    EX_META_CONSISTENCY_CHECK_RULE_INVALID_INPUT("app.meta.consistency.check.rule.invalid.input"),
    //----------------- End of Meta -----------------------------------------------------------------------------
    /**
     * Invalid input. Entity name or path blank.
     */
    EX_UPATH_INVALID_INPUT_ENTITY_OR_PATH_BLANK("app.upath.invalid.input.entity.or.path.blank"),
    /**
     * Invalid input. Path was split to zero elements.
     */
    EX_UPATH_INVALID_INPUT_SPLIT_TO_ZERO_ELEMENTS("app.upath.invalid.input.split.to.zero.elements"),
    /**
     * Invalid input. Attribute not found by path.
     */
    EX_UPATH_INVALID_INPUT_ATTRIBUTE_NOT_FOUND_BY_PATH("app.upath.invalid.input.attribute.not.found.by.path"),
    /**
     * Invalid input. Subscript expression incorrect.
     */
    EX_UPATH_INVALID_SUBSCRIPT_EXPRESSION("app.upath.invalid.subscript.expression"),
    /**
     * Invalid input. Root expression incorrect.
     */
    EX_UPATH_INVALID_ROOT_EXPRESSION("app.upath.invalid.root.expression"),
    /**
     * Invalid input. Upath for set operations must end with collecting element.
     */
    EX_UPATH_INVALID_SET_WRONG_END_ELEMENT("app.upath.invalid.set.wrong.end.element"),
    /**
     * Invalid input. Attribute and last UPath element have different names.
     */
    EX_UPATH_INVALID_SET_WRONG_ATTRIBUTE_NAME("app.upath.invalid.set.wrong.attribute.name"),
    /**
     * Invalid input. Last element of this UPath and target attribute have different value types.
     */
    EX_UPATH_INVALID_SET_WRONG_TARGET_ATTRIBUTE_TYPE("app.upath.invalid.set.wrong.target.attribute.type"),
    /**
     * Invalid state. Attribute selected for an intermediate path element is not a complex attribute.
     */
    EX_UPATH_INVALID_SET_NOT_A_COMPLEX_FOR_INTERMEDIATE("app.upath.invalid.set.not.complex.for.intermediate"),
    /**
     * Invalid input. Filtering expression denotes attribute not found in model.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_MISSING_ATTRIBUTE("app.upath.invalid.filtering.expression.missing.attribute"),
    /**
     * Invalid input. Filtering expression denotes string value in wrong format. Quoted 'value' is expected.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_STRING_FORMAT("app.upath.invalid.filtering.expression.string.format"),
    /**
     * Invalid input. Filtering expression denotes number value in wrong format. Unquoted numeric value in octal, hexadecimal, decimal possibly with type modifyer is expected.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_NUMBER_FORMAT("app.upath.invalid.filtering.expression.number.format"),
    /**
     * Invalid input. Filtering expression denotes date value in wrong format. ISO date is expected.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_DATE_FORMAT("app.upath.invalid.filtering.expression.date.format"),
    /**
     * Invalid input. Filtering expression denotes time value in wrong format. ISO time is expected.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_TIME_FORMAT("app.upath.invalid.filtering.expression.time.format"),
    /**
     * Invalid input. Filtering expression denotes timestamp value in wrong format. ISO timestamp is expected.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_TIMESTAMP_FORMAT("app.upath.invalid.filtering.expression.timestamp.format"),
    /**
     * Invalid input. Filtering expression addresses invalid attribute type. Strings, numeric types and temporal types only are supported.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_ATTRIBUTE_TYPE("app.upath.invalid.filtering.expression.attribute.type"),
    /**
     * Invalid input. Filtering expression denotes complex attribute as filter attribute. Filter attribute may be either simple or code or array.
     */
    EX_UPATH_INVALID_FILTERING_EXPRESSION_COMPLEX_ATTRIBUTE("app.upath.invalid.filtering.expression.complex.attribute"),
    /**
     * Invalid state. Attribute selected for an intermediate path element is not a complex attribute.
     */
    EX_UPATH_NOT_A_COMPLEX_ATTRIBUTE_FOR_INTERMEDIATE_PATH_ELEMENT("app.upath.invalid.state.not.complex.for.intermediate"),
    /**
     * Invalid state. Entity not found by name.
     */
    EX_UPATH_ENTITY_NOT_FOUND_BY_NAME("app.upath.invalid.state.entity.not.found.by.name"),

    //----------------- End of UPath -----------------------------------------------------------------------------
    /**
     * Put string simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_STRING_NOT_SIMPLE("app.data.attribute.put.string.not.simple"),
    /**
     * Put string simple attribute: Attribute exists and is not string.
     */
    EX_DATA_ATTRIBUTE_PUT_STRING_NOT_STRING("app.data.attribute.put.string.not.string"),
    /**
     * Put integer simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_INT_NOT_SIMPLE("app.data.attribute.put.int.not.simple"),
    /**
     * Put integer simple attribute: Attribute exists and is not integer.
     */
    EX_DATA_ATTRIBUTE_PUT_INT_NOT_INT("app.data.attribute.put.int.not.int"),
    /**
     * Put numeric simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_NUM_NOT_SIMPLE("app.data.attribute.put.num.not.simple"),
    /**
     * Put numeric simple attribute: Attribute exists and is not numeric.
     */
    EX_DATA_ATTRIBUTE_PUT_NUM_NOT_NUM("app.data.attribute.put.num.not.num"),
    /**
     * Put boolean simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_BOOL_NOT_SIMPLE("app.data.attribute.put.bool.not.simple"),
    /**
     * Put boolean simple attribute: Attribute exists and is not boolean.
     */
    EX_DATA_ATTRIBUTE_PUT_BOOL_NOT_BOOL("app.data.attribute.put.bool.not.bool"),
    /**
     * Put date simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_DATE_NOT_SIMPLE("app.data.attribute.put.date.not.simple"),
    /**
     * Put date simple attribute: Attribute exists and is not date.
     */
    EX_DATA_ATTRIBUTE_PUT_DATE_NOT_DATE("app.data.attribute.put.date.not.date"),
    /**
     * Put time simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_TIME_NOT_SIMPLE("app.data.attribute.put.time.not.simple"),
    /**
     * Put time simple attribute: Attribute exists and is not time.
     */
    EX_DATA_ATTRIBUTE_PUT_TIME_NOT_TIME("app.data.attribute.put.time.not.time"),
    /**
     * Put timestamp simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_TIMESTAMP_NOT_SIMPLE("app.data.attribute.put.timestamp.not.simple"),
    /**
     * Put timestamp simple attribute: Attribute exists and is not timestamp.
     */
    EX_DATA_ATTRIBUTE_PUT_TIMESTAMP_NOT_TIMESTAMP("app.data.attribute.put.timestamp.not.timestamp"),
    /**
     * Put BLOB simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_BLOB_NOT_SIMPLE("app.data.attribute.put.blob.not.simple"),
    /**
     * Put BLOB simple attribute: Attribute exists and is not BLOB.
     */
    EX_DATA_ATTRIBUTE_PUT_BLOB_NOT_BLOB("app.data.attribute.put.blob.not.blob"),
    /**
     * Put CLOB simple attribute: Attribute exists and is not simple.
     */
    EX_DATA_ATTRIBUTE_PUT_CLOB_NOT_SIMPLE("app.data.attribute.put.clob.not.simple"),
    /**
     * Put CLOB simple attribute: Attribute exists and is not CLOB.
     */
    EX_DATA_ATTRIBUTE_PUT_CLOB_NOT_CLOB("app.data.attribute.put.clob.not.clob"),
    /**
     * Put complex attribute: Attribute exists and is not complex.
     */
    EX_DATA_ATTRIBUTE_PUT_NOT_COMPLEX("app.data.attribute.put.not.complex"),
    /**
     * Cannot marshal Golden record.
     */
    EX_DATA_CANNOT_MARSHAL_ETALON("app.data.cannotMarshallGolden"),
    /**
     * Cannot marshal Original record.
     */
    EX_DATA_CANNOT_MARSHAL_ORIGIN("app.data.cannotMarshallOrigin"),
    /**
     * Cannot unmarshal Original record.
     */
    EX_DATA_CANNOT_UNMARSHAL_ORIGIN("app.data.cannotUnmarshallOrigin"),
    /**
     * Value in measured attribute is not present
     */
    EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT("app.data.attribute.measured.value.notPresent"),
    /**
     * Unit in measured attribute is not present
     */
    EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT("app.data.attribute.measured.unit.notPresent"),
    //----------------------- Get section
    /**
     * Invalid get request context.
     */
    EX_DATA_GET_INVALID_INPUT("app.data.invalidGetInput"),
    /**
     * Record not found by supplied keys.
     */
    EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS("app.data.notFoundByKeys"),
    /**
     * Record can not be read due to unsufficient rights.
     */
    EX_DATA_GET_NO_RIGHTS("app.data.get.noReadRights"),
    //----------------------- Restore section
    /**
     * Invalid restore request context.
     */
    EX_DATA_RESTORE_INVALID_INPUT("app.data.invalidGetInput"),
    /**
     * Record not found by supplied keys.
     */
    EX_DATA_RESTORE_NOT_FOUND_BY_SUPPLIED_KEYS("app.data.notFoundByKeys"),
    //----------------------- Upsert section
    /**
     * Invalid upsert request context. Neither etalon record nor origin record has been supplied. Upsert rejected.
     */
    EX_DATA_UPSERT_NO_INPUT("app.data.upsertNoInput"),
    /**
     * Source system must be defined. Upsert rejected.
     */
    EX_DATA_UPSERT_NO_SOURCE_SYSTEM("app.data.upsertNoSourceSystem"),
    /**
     * Invalid upsert request context. No entity name was supplied. Upsert rejected.
     */
    EX_DATA_UPSERT_NO_ID("app.data.upsertNoEntityName"),
    /**
     * Invalid upsert request context. Entity was not found by name. Upsert rejected.
     */
    EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME("app.data.upsertEntityNotFoundByName"),
    /**
     * Required rels is not presented.
     */
    EX_DATA_UPSERT_REQUIRED_RELS_IS_NOT_PRESENTED("app.data.upsert.required.rels.notPresented"),
    /**
     * Required rels is not presented.
     */
    EX_DATA_UPSERT_RELS_INCORRECT_TO_SIDE_PERIOD("app.data.upsert.rels.incorrect.toside.period"),
    /**
     * Required rels is not presented.
     */
    EX_DATA_UPSERT_REQUIRED_RELS_INCORRECT_TO_SIDE_PERIOD("app.data.upsert.required.rels.incorrect.toside.period"),
    /**
     * Enum attribute has a value which not present in system
     */
    EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT("app.data.upsert.attribute.enum.value.incorrect"),
    /**
     * Attribute supplied for upsert is missing in the model. Upsert rejected.
     */
    EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE("app.data.upsert.incorrect.quantity.complex.attrs.range"),
    /**
     * Attribute supplied for upsert is missing in the model. Upsert rejected.
     */
    EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_LOWER_BOUND("app.data.upsert.incorrect.quantity.complex.attrs.lower.bound"),
    /**
     * Attribute supplied for upsert is missing in the model. Upsert rejected.
     */
    EX_DATA_UPSERT_MISSING_ATTRIBUTE("app.data.upsertMissingAttribute"),
    /**
     * Attribute supplied for upsert is of the wrong type compared to the model. Upsert rejected.
     */
    EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE("app.data.upsertWrongSpecialAttributeType"),
    /**
     * Attribute and attribute definition have different measurement values.
     */
    EX_DATA_UPSERT_WRONG_MEASUREMENT_VALUES("app.data.upsert.wrong.measurement.values"),
    /**
     * Measurement attr has incorrect value after enrich in dq
     */
    EX_DATA_UPSERT_ENRICH_MEASUREMENT_VALUE_IS_INCORRECT("app.data.upsert.incorrect.enrich.measurement.attr"),
    /**
     * Wrong code attribute link value. Upsert rejected.
     */
    EX_DATA_UPSERT_WRONG_SIMPLE_CODE_ATTRIBUTE_REFERENCE_VALUE("app.data.upsertWrongSpecialAttributeType"),
    /**
     * Wrong code attribute link value. Upsert rejected.
     */
    EX_DATA_UPSERT_WRONG_ARRAY_CODE_ATTRIBUTE_REFERENCE_VALUE("app.data.upsertWrongArrayCodeAttributeReferenceType"),
    /**
     * Attribute and attribute definition have link to unavailable measurement value
     */
    EX_DATA_UPSERT_MEASUREMENT_VALUE_UNAVAILABLE("app.data.upsert.unavailable.measurement.value"),
    /**
     * Attribute has link to unavailable measurement unit
     */
    EX_DATA_UPSERT_MEASUREMENT_UNIT_UNAVAILABLE("app.data.upsert.unavailable.measurement.unit"),
    /**
     * Large object attribute has incorrect link to attach file
     */
    EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE("app.data.upsert.unavailable.large.object.value"),
    /**
     * Attribute supplied for upsert is of the wrong value type compared to the model. Upsert rejected.
     */
    EX_DATA_UPSERT_WRONG_CODE_ATTRIBUTE_VALUE_TYPE("app.data.upsertWrongCodeAttributeValueType"),
    /**
     * Attribute supplied for upsert is of the wrong value type compared to the model. Upsert rejected.
     */
    EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE("app.data.upsertWrongSimpleAttributeValueType"),
    /**
     * Attribute supplied for upsert is of the wrong value type compared to the model. Upsert rejected.
     */
    EX_DATA_UPSERT_WRONG_ARRAY_ATTRIBUTE_VALUE_TYPE("app.data.upsertWrongArrayAttributeValueType"),
    /**
     * Attribute supplied for upsert is of the wrong type compared to the model. Upsert rejected.
     */
    EX_DATA_UPSERT_WRONG_ATTRIBUTE_TYPE("app.data.upsertWrongAttributeType"),
    /**
     * Invalid upsert request context. Neither external id nor origin record key has been supplied. Upsert rejected.
     */
    EX_DATA_UPSERT_INVALID_ORIGIN_INPUT("app.data.upsertInvalidOriginInput"),
    /**
     * Upsert failed, origin is in inactive state.
     */
    EX_DATA_UPSERT_ORIGIN_INACTIVE("app.data.upsertOriginInactive"),
    /**
     * Upsert failed, etalon is in inactive state.
     */
    EX_DATA_UPSERT_ETALON_INACTIVE("app.data.upsertEtalonInactive"),
    /**
     * Delete records before user exits errors
     */
    EX_DATA_DELETE_RECORD_BEFORE_USER_EXIT_ERROR_OLD("app.data.delete.record.before.user.exit.error.old"),
    /**
     * Delete records before user exits errors
     */
    EX_DATA_DELETE_RECORD_BEFORE_USER_EXIT_ERROR("app.data.delete.record.before.user.exit.error"),
    /**
     * Delete records after user exits errors
     */
    EX_DATA_DELETE_RECORD_AFTER_USER_EXIT_ERROR("app.data.delete.record.after.user.exit.error"),
    /**
     * Record can not be identified by supplied keys. Upsert rejected.
     */
    EX_DATA_UPSERT_INVALID_KEYS("app.data.upsertInvalidKeys"),
    /**
     * Alias code attribute is invalid
     */
    EX_DATA_UPSERT_INVALID_ALIAS_CODE_ATTRIBUTE("app.data.upsert.invalidAliasCodeAttribute"),
    /**
     * Unavailable classifier
     */
    EX_DATA_UPSERT_UNAVAILABLE_CLASSIFIER("app.data.upsert.unavailable.classifier"),
    /**
     * Incorrect classifier
     */
    EX_DATA_UPSERT_INCORRECT_CLASSIFIER("app.data.upsert.incorrect.classifier"),
    /**
     * Incorrect classifier
     */
    EX_DATA_UPSERT_CLASSIFIER_ENTITY_NAME_MISSING("app.data.upsert.classifier.entity.name.missing"),
    /**
     * Incorrect classifier node
     */
    EX_DATA_UPSERT_UNAVAILABLE_CLASSIFIER_NODE("app.data.upsert.incorrect.classifier.node"),
    /**
     * Required attributes is not presented.
     */
    EX_DATA_UPSERT_REQUIRED_ATTRS_IS_NOT_PRESENTED("app.data.upsert.required.attrs.notPresented"),
    /**
     * Upsert relation user exits errors
     */
    EX_DATA_UPSERT_RELATION_BEFORE_USER_EXIT_ERROR("app.data.upsert.relation.before.user.exit.error"),
    /**
     * Upsert relation user exits errors
     */
    EX_DATA_UPSERT_RELATION_AFTER_USER_EXIT_ERROR("app.data.upsert.relation.after.user.exit.error"),
    /**
     * Upsert origin record user exits errors
     */
    EX_DATA_UPSERT_ORIGIN_RECORD_BEFORE_USER_EXIT_ERROR("app.data.upsert.origin.record.before.user.exit.error"),
    /**
     * Upsert origin record user exits errors
     */
    EX_DATA_UPSERT_ORIGIN_RECORD_AFTER_USER_EXIT_ERROR("app.data.upsert.origin.record.after.user.exit.error"),
    /**
     * Upsert etalon record user exits errors
     */
    EX_DATA_UPSERT_ETALON_RECORD_AFTER_USER_EXIT_ERROR("app.data.upsert.etalon.record.after.user.exit.error"),
    /**
     * Unavailable classifier
     */
    EX_DATA_GET_CLASSIFIER_RECORD_NOT_FOUND("app.data.get.classifier.record.not.found"),
    /**
     * Unavailable node.
     */
    EX_DATA_GET_CLASSIFIER_RECORD_INVALID_NODE("app.data.get.classifier.record.invalid.node"),
    /**
     * Unavailable classifier
     */
    EX_DATA_DELETE_CLASSIFIER_RECORD_NOT_FOUND("app.data.delete.classifier.record.not.found"),
    /**
     * Delete relation user exits errors
     */
    EX_DATA_DELETE_RELATION_BEFORE_USER_EXIT_ERROR("app.data.delete.relation.before.user.exit.error"),
    /**
     * Delete relation user exits errors
     */
    EX_DATA_DELETE_RELATION_AFTER_USER_EXIT_ERROR("app.data.delete.relation.after.user.exit.error"),

    //----------------------- End of Upsert pre- check
    /**
     * Soap user don't able to upsert record if record in pending state.
     */
    EX_DATA_UPSERT_NOT_ACCEPTED_HAS_PENDING_RECORD("app.data.upsert.hasPendingVersions"),
    /**
     * The user has no or unsufficient insert rights.
     */
    EX_DATA_UPSERT_INSERT_NO_RIGHTS("app.data.upsert.noRightsForInsert"),
    /**
     * The user has no or unsufficient update rights.
     */
    EX_DATA_UPSERT_UPDATE_NO_RIGHTS("app.data.upsert.noRightsForUpdate"),
    /**
     * Unable to generate externalId or code attribute, using autogeneration strategy for entity {}.
     * Either no data was given or content for configured fields is missing or incomplete. Processing {}.
     */
    EX_DATA_UPSERT_UNABLE_TO_APPLY_ID_GENERATION_STRATEGY("app.data.upsert.unable.to.apply.id.generation.strategy"),
    /**
     * Unable to generate externalId, using autogeneration strategy for entity {}.
     * Generated key length exceeds the limit of 512 characters.
     */
    EX_DATA_UPSERT_ID_GENERATION_STRATEGY_KEY_LENGTH("app.data.upsert.id.generation.strategy.key.length"),
    /**
     * Soap user don't able to upsert record if record in pending state.
     */
    EX_DATA_DELETE_PERIOD_NOT_ACCEPTED_HAS_PENDING_RECORD("app.data.deletePeriod.hasPendingVersions"),
    /**
     * Record can not be deleted due to unsufficient rights.
     */
    EX_DATA_DELETE_NO_RIGHTS("app.data.delete.noReadRights"),
    /**
     * Time line not exist
     */
    EX_DATA_TIMELINE_NOT_EXIST("app.data.timeline.notExist"),
    /**
     * Time line not exist
     */
    EX_DATA_TIMELINE_NO_IDENTITY("app.data.timeline.noIdentity"),
    /**
     * Time line not exist
     */
    EX_DATA_RELATIONS_TIMELINE_NO_IDENTITY("app.data.timeline.noIdentity"),
    /**
     * Time line not exist
     */
    EX_DATA_RELATION_TIMELINE_NO_IDENTITY("app.data.timeline.noIdentity"),
    /**
     * Time line not exist
     */
    EX_DATA_USER_DONT_HAVE_RIGHTS_TO_TIMELINE("app.data.timeline.rightsNotEnough"),
    /**
     * Origin update failed.
     */
    EX_DATA_ORIGIN_UPDATE_FAILED("app.data.originUpdateFailed"),
    /**
     * Origin insert failed.
     */
    EX_DATA_ORIGIN_INSERT_FAILED("app.data.originInsertFailed"),
    /**
     * Init bulk set failed.
     */
    EX_DATA_INIT_BATCH_SET_FAILED("app.data.init.batch.set.failed"),
    /**
     * Append to bulk set failed.
     */
    EX_DATA_APPEND_BATCH_SET_FAILED("app.data.append.batch.set.failed"),
    /**
     * Finish bulk set failed.
     */
    EX_DATA_FINISH_BATCH_SET_FAILED("app.data.finish.batch.set.failed"),
    /**
     * Origin batch insert failed.
     */
    EX_DATA_ORIGIN_BATCH_INSERT_FAILED("app.data.origin.batch.insert.failed"),
    /**
     * Origin batch update failed.
     */
    EX_DATA_ORIGIN_BATCH_UPDATE_FAILED("app.data.origin.batch.update.failed"),
    /**
     * Copy stream close failed.
     */
    EX_DATA_COPY_STREAM_CLOSE_FAILED("app.data.copy.stream.close.failed"),
    /**
     * Origin transition insert failed.
     */
    EX_DATA_ORIGIN_TRANSITION_ATTACH_FAILED("app.data.originTransitionAttachFailed"),
    /**
     * Origin insert on merge failed.
     */
    EX_DATA_ORIGIN_TRANSITION_MERGE_FAILED("app.data.originTransitionMergeFailed"),
    /**
     * Golden insert failed.
     */
    EX_DATA_ETALON_UPDATE_FAILED("app.data.goldenUpdateFailed"),
    /**
     * Etalon approval state change failed.
     */
    EX_DATA_ETALON_APPROVAL_STATE_UPDATE_FAILED("app.data.etalon.approval.state.change.failed"),
    /**
     * Etalon insert failed.
     */
    EX_DATA_ETALON_INSERT_FAILED("app.data.goldenInsertFailed"),
    /**
     * Etalon initial transition insert failed.
     */
    EX_DATA_INITIAL_ETALON_TRANSITION_INSERT_FAILED("app.data.etalonInitialTransitionInsertFailed"),
    /**
     * Origin insert etalon transition insert failed.
     */
    EX_DATA_ORIGIN_INSERT_ETALON_TRANSITION_INSERT_FAILED("app.data.etalonOriginInsertTransitionInsertFailed"),
    /**
     * Merge etalon transition insert failed.
     */
    EX_DATA_ETALON_MERGE_ETALON_TRANSITION_INSERT_FAILED("app.data.etalonEtalonMergeTransitionInsertFailed"),
    /**
     * Merge etalon transition insert failed.
     */
    EX_DATA_DUPLICATES_STATE_ETALON_TRANSITION_INSERT_FAILED("app.data.etalonDuplicatesStateTransitionInsertFailed"),
    /**
     * Golden record not found.
     */
    EX_DATA_ETALON_NOT_FOUND("app.data.goldenRecordNotFound"),
    /**
     * Cannot delete record because active refs still exist.
     */
    EX_DATA_CANNOT_DELETE_REF_EXIST("app.data.cannotDeleteRefExist"),
    /**
     * Golden delete failed (no key received).
     */
    EX_DATA_ETALON_DELETE_FAILED("app.data.goldenDeleteFailed"),
    /**
     * Etalon wipe failed (no key received).
     */
    EX_DATA_ETALON_WIPE_FAILED("app.data.goldenDeleteFailed"),
    /**
     * Origin delete failed (no key received).
     */
    EX_DATA_ORIGIN_DELETE_FAILED("app.data.originDeleteFailed"),
    /**
     * Invalid delete request context.
     */
    EX_DATA_INVALID_DELETE_INPUT("app.data.invalidDeleteInput"),
    /**
     * Record not found by supplied keys.
     */
    EX_DATA_MERGE_NOT_FOUND_BY_SUPPLIED_KEYS("app.data.merge.notFoundByKeys"),
    /**
     * Merge failed (duplicates not found).
     */
    EX_DATA_MERGE_DUPLICATES_NOT_FOUND("app.data.mergeFailedDuplicatesNotFound"),
    /**
     * Record key for merge has incorrect state.
     */
    EX_DATA_MERGE_VALIDATE_INCORRECT_RECORD_STATE("app.data.merge.validate.incorrectRecordState"),
    /**
     * Merge failed (storage update unsuccessful).
     */
    EX_DATA_MERGE_FAILED_UPDATE("app.data.mergeFailedUpdate"),
    /**
     * Record can not be merged due to unsufficient rights.
     */
    EX_DATA_MERGE_NO_RIGHTS("app.data.merge.noMergeRights"),
    /**
     * External ID can not be joined. Etalon ID not found.
     */
    EX_DATA_JOIN_ETALON_ID_NOT_FOUND("app.data.join.etalon.id.not.found"),
    /**
     * External ID can not be joined. Invalid input.
     */
    EX_DATA_JOIN_INVALID_INPUT("app.data.join.invalid.input"),
    /**
     * External ID can not be joined. Target register and the supplied one do not match.
     */
    EX_DATA_JOIN_TARGET_REGISTER_DONT_MATCH("app.data.join.target.register.dont.match"),
    /**
     * External ID can not be joined. The key is already defined for the target.
     */
    EX_DATA_JOIN_KEY_IS_ALREADY_DEFINED_IN_TARGET("app.data.join.key.already.in"),
    /**
     * External ID can not be joined. The key is already used by another record.
     */
    EX_DATA_JOIN_KEY_IS_ALREADY_USED_BY_ANOTHER("app.data.join.key.already.used"),
    /**
     * Invalid get golden list request context.
     */
    EX_DATA_INVALID_GET_LIST_INPUT("app.data.invalidGetGoldenListInput"),
    /**
     * Invalid LOB object received from front end.
     */
    EX_DATA_INVALID_LOB_OBJECT("app.data.invalidLobObject"),
    /**
     * Invalid LOB object received from front end.
     */
    EX_DATA_INVALID_CLOB_OBJECT("app.data.invalidClobObject"),
    /**
     * Invalid LOB object update received. Trying to update already persisted and versioned object.
     */
    EX_DATA_INVALID_LOB_UPDATE("app.data.invalidLobUpdate"),
    /**
     * Batch insert to vistory failed.
     */
    EX_DATA_INSERT_VISTORY_BATCH_FAILED("app.data.insertVistoryBatchFailed"),
    /**
     * DQ failed from before executor (new record).
     */
    EX_DATA_ORIGIN_UPSERT_NEW_DQ_FAILED_BEFORE("app.data.originUpsertBeforeExecutorFailed"),
    /**
     * An etalon record contains relations to yourself
     */
    EX_DATA_ETALON_CONTAINS_RELATIONS_TO_YOURSELF("app.data.hasRelationsToYourself"),
    /**
     * An etalon record contains links to yourself
     */
    EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF("app.data.hasLinksToYourself"),
    /**
     * An etalon record contains links to yourself with periods
     */
    EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF_WITH_PERIODS("app.data.hasLinksToYourselfWithPeriods"),
    /**
     * Some executor failed before merge
     */
    EX_DATA_ETALON_MERGE_BEFORE("app.data.etalonMergeBeforeExecutorFailed"),
    /**
     * Deactivation failed (no key received).
     */
    EX_DATA_ORIGIN_DEACTIVATION_FAILED("app.data.deactivationFailed"),
    /**
     * Cannot unmarshal Relation.
     */
    EX_DATA_CANNOT_UNMARSHAL_RELATION("app.data.cannotUnmarshallRelation"),
    /**
     * Cannot marshal Relation.
     */
    EX_DATA_CANNOT_MARSHAL_RELATION("app.data.cannotMarshallRelation"),
    /**
     * Cannot unmarshal Relation.
     */
    EX_DATA_CANNOT_UNMARSHAL_CLASSIFIER("app.data.cannotUnmarshallClassifiers"),
    /**
     * Cannot marshal Relation.
     */
    EX_DATA_CANNOT_MARSHAL_CLASSIFIER("app.data.cannotMarshallClassifiers"),
    /**
     * Attribute supplied for upsert is of the wrong type in the model.
     */
    EX_DATA_UPSERT_WRONG_ATTRIBUTE("app.data.upsertWrongAttribute"),
    /**
     * Cannot parse date string. Incorrect date format.
     */
    EX_DATA_CANNOT_PARSE_DATE("app.data.cannotParseDate"),
    /**
     * Cannot parse date string. Incorrect date format.
     */
    EX_DATA_CANNOT_PARSE_LOCAL_DATE("app.data.cannotParseLocalDate"),
    /**
     * Cannot parse date string. Incorrect date format.
     */
    EX_DATA_CANNOT_PARSE_LOCAL_TIME("app.data.cannotParseLocalTime"),
    /**
     * Cannot parse date string. Incorrect date format.
     */
    EX_DATA_CANNOT_PARSE_LOCAL_DATE_TIME("app.data.cannotParseLocalDateTime"),
    /**
     * Cannot parse timestamp string. Incorrect date format.
     */
    EX_DATA_CANNOT_PARSE_TIMESTAMP("app.data.cannotParseTimestamp"),
    /**
     * Cannot load LOB data.
     */
    EX_DATA_CANNOT_LOAD_LOB("app.data.cannotLoadLOB"),
    /**
     * Cannot save LOB data.
     */
    EX_DATA_CANNOT_SAVE_LOB("app.data.cannotSaveLOB"),
    /**
     * File name not valid(encoding).
     */
    EX_DATA_INCORRECT_ENCODING("app.data.incorrectEncoding"),
    /**
     * Unable to save xlsx file.
     */
    EX_DATA_XLSX_IMPORT_SAVE_FILE("app.data.import.unableToSaveFile"),
    /**
     * Unable to parse xlsx file.
     */
    EX_DATA_XLSX_IMPORT_PARSE_FILE("app.data.import.unableToParseFile"),
    /**
     * Unable to parse xlsx file.
     */
    EX_DATA_XLSX_IMPORT_PARSE_FILE_INVALID_CELL_FORMAT("app.data.import.parse.invalid.cell.format"),
    /**
     * Duplicated keys.
     */
    EX_DATA_XLSX_IMPORT_DUPLICATED_KEYS("app.data.import.duplicatedKeys"),
    /**
     * Duplicate ids detected (etalon, or external).
     */
    EX_DATA_XLSX_IMPORT_DUPLICATED_IDS("app.data.import.duplicate.ids"),
    /**
     * Non-unique values detected.
     */
    EX_DATA_XLSX_IMPORT_NON_UNIQUE_VALUES("app.data.import.non.unique.values"),
    /**
     * Unable to save data into temporary table.
     */
    EX_DATA_XLSX_IMPORT_TEMPORARY_TABLE("app.data.import.unableToSaveData"),
    /**
     * Record not found by supplied keys.
     */

    EX_DATA_MERGE_DUPLICATES_NOT_FOUND_BY_SUPPLIED_KEYS("app.data.merge.duplicatesNotFoundByKeys"),
    /**
     * Unable to parse xlsx file.
     */
    EX_DATA_XLSX_IMPORT_UNKNOWN_FILE_FORMAT("app.data.import.unknown.file.format"),
    /**
     * Entity name is not known to the current metamodel.
     */
    EX_DATA_XLSX_IMPORT_UNKNOWN_ENTITY("app.data.import.unknownEntityName"),
    /**
     * Unable to create XLS template file.
     */
    EX_DATA_XLSX_IMPORT_UNABLE_TO_CREATE_TEMPLATE("app.data.import.unableToCreateTemplate"),
    /**
     * Unable to export data to XLS file.
     */
    EX_DATA_EXPORT_UNABLE_TO_EXPORT_XLS("app.data.exportUnableToExportXLS"),
    /**
     * Unable to create XLS template file.
     */
    EX_DATA_IMPORT_UNABLE_TO_PARSE_EXCHANGE_DEFINITION("app.data.import.unable.to.parse.exchange.definition"),
    /**
     * Unable to create XLS template file.
     */
    EX_DATA_IMPORT_ENTITIES_NO_TABLES_TO_PROCESS("app.data.import.entities.no.tables.to.process"),
    /**
     * Unable to create XLS template file.
     */
    EX_DATA_IMPORT_RELATIONS_NO_TABLES_TO_PROCESS("app.data.import.relations.no.tables.to.process"),
    /**
     * Unable to create XLS template file.
     */
    EX_DATA_IMPORT_COUNT_PARITION_SIZE_FAILED("app.data.import.count.partition.size.failed"),
    /**
     * Unable to create XLS template file.
     */
    EX_DATA_EXPORT_UNABLE_TO_PARSE_EXCHANGE_DEFINITION("app.data.import.unable.to.parse.exchange.definition"),
    /**
     * Time conversation support just calendar,string,date
     */
    EX_DATA_IMPORT_UNSUPPORTED_TIME_FORMAT("app.data.import.unsupported.time.type"),
    /**
     * String has incorrect format
     */
    EX_DATA_IMPORT_INCORRECT_STRING_TIME_FORMAT("app.data.import.incorrect.string.time.format"),
    /**
     * Impossible convert to requared type
     */
    EX_DATA_IMPORT_IMPOSSIBLE_CONVERT_TO_TYPE("app.data.import.impossible.convert.type"),
    /**
     * Neither entity name nor relation name given. Step target undefined.
     */
    EX_DATA_IMPORT_STEP_TARGET_UNDEFINED("app.data.import.step.target.undefined"),
    /**
     * Containment record upsert failed.
     */
    EX_DATA_RELATIONS_UPSERT_CONTAINS_FAILED("app.data.containsUpsertFailed"),
    /**
     * Etalon record upsert failed.
     */
    EX_DATA_RELATIONS_UPSERT_ETALON_FAILED("app.data.etalonFailed"),
    /**
     * Origin record upsert failed.
     */
    EX_DATA_RELATIONS_UPSERT_ORIGIN_FAILED("app.data.originFailed"),
    /**
     * Version record upsert failed.
     */
    EX_DATA_RELATIONS_UPSERT_VERSION_FAILED("app.data.versionFailed"),
    /**
     * Etalon record upsert failed.
     */
    EX_DATA_RELATIONS_BATCH_UPSERT_ETALON_FAILED("app.data.etalonFailed"),
    /**
     * Origin record upsert failed.
     */
    EX_DATA_RELATIONS_BATCH_UPSERT_ORIGIN_FAILED("app.data.originFailed"),
    /**
     * From or to side is in inactive state.
     */
    EX_DATA_RELATIONS_UPSERT_SIDES_INACTIVE("app.data.sidesInactive"),
    /**
     * From key not found.
     */
    EX_DATA_RELATIONS_UPSERT_FROM_NOT_FOUND("app.data.fromKeyNotFound"),
    /**
     * To key not found.
     */
    EX_DATA_RELATIONS_UPSERT_TO_NOT_FOUND("app.data.toKeyNotFound"),
    /**
     * Relation not found by name.
     */
    EX_DATA_RELATIONS_UPSERT_RELATION_NOT_FOUND("app.data.relationNotFoundByName"),
    /**
     * Relation upsert before action listener failed.
     */
    EX_DATA_RELATIONS_UPSERT_MORE_THEN_ONE_REFERENCE("app.data.relationUpsertMoreThenOneReference"),
    /**
     * No insert rights.
     */
    EX_DATA_RELATIONS_UPSERT_NO_INSERT_RIGHTS("app.data.relations.noInsertRights"),
    /**
     * No update rights.
     */
    EX_DATA_RELATIONS_UPSERT_NO_UPDATE_RIGHTS("app.data.relations.noUpdateRights"),
    /**
     * Iinvalid input.
     */
    EX_DATA_RELATIONS_UPSERT_INVALID_INPUT("app.data.relations.upsert.invalidInput"),
    /**
     * Relation not found by name.
     */
    EX_DATA_RELATIONS_GET_RELATION_NOT_FOUND("app.data.get.relationNotFoundByName"),
    /**
     * Invalid input.
     */
    EX_DATA_RELATIONS_GET_INVALID_INPUT("app.data.get.invalidInput"),
    /**
     * No read rights.
     */
    EX_DATA_RELATIONS_GET_NO_RIGHTS("app.data.relations.noReadRights"),
    /**
     * Relation not found by supplied keys.
     */
    EX_DATA_RELATIONS_GET_NOT_FOUND_BY_SUPPLIED_KEYS("app.data.relations.not.found.by.keys"),
    /**
     * Relation not found by name.
     */
    EX_DATA_RELATIONS_DELETE_RELATION_NOT_FOUND("app.data.delete.relationNotFoundByName"),
    /**
     * Relation not found.
     */
    EX_DATA_RELATIONS_DELETE_NOT_FOUND("app.data.keysNotFound"),
    /**
     * No delete rights.
     */
    EX_DATA_RELATIONS_DELETE_NO_RIGHTS("app.data.relations.noDeleteRights"),
    /**
     * Iinvalid input.
     */
    EX_DATA_RELATIONS_DELETE_INVALID_INPUT("app.data.relations.delete.invalidInput"),
    /**
     * Invalid relto identity.
     */
    EX_DATA_RELATION_MISSING_RELTO_KEY("app.data.rels.missingRelToKey"),
    /**
     * Invalid relto identity.
     */
    EX_DATA_RELATION_NOT_FOUND_BY_KEY("app.data.rels.relToNotFoundByKey"),
    /**
     * Upsert of a user event failed. No user.
     */
    EX_DATA_USER_EVENT_NO_USER("app.data.userEventUpsertNoUser"),
    /**
     * Meta model type wrapper for id not found for BVT calculation.
     */
    EX_DATA_NO_TYPE_WRAPPER_FOR_BVT_CALCULATION("app.data.noTypeWrapperForBVTCalculation"),
    /**
     * Etalon id UUID invalid.
     */
    EX_DATA_V3_ETALON_ID_UUID_INVALID("app.data.upsert.etalon.key.uuid.invalid"),
    /**
     * Etalon id UUID invalid.
     */
    EX_DATA_V3_ORIGIN_ID_UUID_INVALID("app.data.upsert.origin.key.uuid.invalid"),
    /**
     * Etalon id UUID invalid.
     */
    EX_DATA_V4_ETALON_ID_UUID_INVALID("app.data.upsert.etalon.key.uuid.invalid"),
    /**
     * Etalon id UUID invalid.
     */
    EX_DATA_V4_ORIGIN_ID_UUID_INVALID("app.data.upsert.origin.key.uuid.invalid"),
    /**
     * Classifier attribute is incorrect
     */
    EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT("app.data.classifier.attr.incorrect"),
    /**
     * From key not found.
     */
    EX_DATA_CLASSIFIER_UPSERT_RECORD_NOT_FOUND("app.data.classifier.record.not.found"),
    /**
     * Record inactive.
     */
    EX_DATA_CLASSIFIER_UPSERT_RECORD_INACTIVE("app.data.classifier.record.inactive"),
    /**
     * Upsert etalon record failed.
     */
    EX_DATA_CLASSIFIER_UPSERT_ETALON_FAILED("app.data.classifier.upsert.failed"),
    /**
     * Upsert classifier version failed.
     */
    EX_DATA_CLASSIFIER_UPSERT_VERSION_FAILED("app.data.classifier.upsert.version.failed"),
    /**
     * Classifier upsert wrong data type.
     */
    EX_DATA_CLASSIFIER_UPSERT_WRONG_ATTRIBUTE_TYPE("app.data.classifier.upsert.wrong.attr.type"),
    /**
     * No insert rights.
     */
    EX_DATA_CLASSIFIER_UPSERT_NO_INSERT_RIGHTS("app.data.classifier.noInsertRights"),
    /**
     * No update rights.
     */
    EX_DATA_CLASSIFIER_UPSERT_NO_UPDATE_RIGHTS("app.data.classifier.noUpdateRights"),
    /**
     * No read rights.
     */
    EX_DATA_CLASSIFIER_GET_NO_RIGHTS("app.data.classifier.noReadRights"),
    /**
     * No delete rights.
     */
    EX_DATA_CLASSIFIER_DELETE_NO_RIGHTS("app.data.classifier.noDeleteRights"),
    /**
     * Classifier upsert wrong data type.
     */
    EX_DATA_VALIDITY_PERIOD_INCORRECT("app.data.validity.period.incorrect"),
    //-------------------------- System
    /**
     * Undefined error occurs.
     */
    EX_SYSTEM_CONNECTION_GET("app.system.connection.get"),
    /**
     * Undefined error occurs.
     */
    EX_SYSTEM_CONNECTION_UNWRAP("app.system.connection.unwrap"),
    /**
     * JAXB context init failure.
     */
    EX_SYSTEM_JAXB_CONTEXT_INIT_FAILURE("app.data.jaxbContextInitFailure"),
    /**
     * JAXB data type factory init failure.
     */
    EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE("app.data.jaxbTypeFactoryInitFailure"),
    /**
     *
     */
    EX_SYSTEM_CONFIGURATION_NOT_IN_SYNC("app.config.notInSync"),
    /**
     *
     */
    EX_SYSTEM_CONFIGURATION_UNABLE_TO_VERIFY("app.config.unableToVerify"),
    /**
     * Configuration cannot be unmarshaled.
     */
    EX_SYSTEM_JAXB_CANNOT_UNMARSHAL_CONFIGURATION("app.data.jaxbCannotUnmarshalConfiguration"),
    /**
     * Configuration cannot be unmarshaled.
     */
    EX_SYSTEM_JAXB_CANNOT_SET_FIELD_PERMISSION("app.data.jaxbCannotSetFieldPermission"),
    /**
     * Unable to init output ports.
     */
    EX_SYSTEM_CLEANSE_INIT_OUTPUT("app.cleanse.unableToInitOutputPorts"),
    /**
     * Unable to init input ports.
     */
    EX_SYSTEM_CLEANSE_INIT_INPUT("app.cleanse.unableToInitInputPorts"),
    /**
     * Unable to read property file.
     */
    EX_SYSTEM_CLEANSE_READ_PROPERTIES("app.cleanse.unableToReadProperty"),
    /**
     * Unable to read property file (from default init).
     */
    EX_SYSTEM_CLEANSE_READ_PROPERTIES_DEFAULT("app.cleanse.unableToReadProperty"),
    /**
     * Unable to initialize cleanse function with reflection.
     */
    EX_SYSTEM_CLEANSE_BASIC_INIT_FAILED("app.cleanse.basicInitFailed"),
    /**
     * Unable to execute cleanse function.
     */
    EX_SYSTEM_CLEANSE_EXEC_FAILED("app.cleanse.execFailed"),
    /**
     * Unable to delete cleanse function.
     */
    EX_SYSTEM_CLEANSE_DELETE_FAILED("app.cleanse.deleteFailed"),
    /**
     * Unable to send notification.
     */
    EX_SYSTEM_NOTIFICATION_FAILED("app.notification.failed"),
    /**
     *
     */
    EX_SYSTEM_REMOVING_FORBID_HAS_LINKS("app.remove.forbid"),
    EX_SYSTEM_DATABASE_CANNOT_CONNECT("app.system.database.cannotConnect"),
    EX_SYSTEM_REQUEST_TOO_LARGE("app.system.request.tooLarge"),
    /**
     * Platform version undefined.
     */
    EX_SYSTEM_PLATFORM_VERSION_UNDEFINED("app.system.platform.version.undefined"),
    /**
     * Platform version invalid.
     */
    EX_SYSTEM_PLATFORM_VERSION_INVALID("app.system.platform.version.invalid"),
    /**
     * Node id undefined.
     */
    EX_SYSTEM_NODE_ID_UNDEFINED("app.system.node.id.undefined"),

    /**
     * Record can not be identified by supplied keys. Upsert rejected.
     */
    EX_DATA_REPUBLICATION_INCORRECT_KEYS("app.data.republiation.incorrect.keys"),
    EX_SECURITY_USER_ALREADY_EXIST("app.security.userExist"),
    EX_SECURITY_ROLE_ALREADY_EXISTS("app.security.roleExists"),
    EX_SECURITY_CANNOT_DEACTIVATE_USER("app.security.cannotDeactivate"),
    EX_SECURITY_CANNOT_CREATE_USER("app.security.cannotCreate"),
    EX_SECURITY_CANNOT_LOGIN("app.security.cannotLogin"),
    EX_SECURITY_USER_HAVE_NO_RIGHTS_FOR_ENDPOINT("app.security.noRightsForEndpoint"),
    EX_SECURITY_LICENSE_INVALID("app.security.license.invalid"),
    EX_SECURITY_HW_FOR_LICENSE_INVALID("app.security.hw.for.license.invalid"),
    EX_SECURITY_HW_FOR_LICENSE_INVALID_WHEN_LOGIN("app.security.hw.for.license.invalid"),

    EX_JOB_NOT_FOUND("app.job.notFound"),
    EX_JOB_DELETE_FAILED("app.job.deleteFailed"),
    EX_JOB_DISABLED("app.job.disabled"),
    EX_JOB_SAME_NAME("app.job.sameName"),
    EX_JOB_CRON_EXPRESSION("app.job.cronExpression"),
    EX_JOB_CRON_SUSPICIOUS_SECOND("app.job.cronExpression.suspicious.second"),
    EX_JOB_CRON_SUSPICIOUS_MINUTE("app.job.cronExpression.suspicious.minute"),
    EX_JOB_CRON_SUSPICIOUS_SHORT_CYCLES_DOM("app.job.cronExpression.suspicious.cycles_dom"),
    EX_JOB_UPDATE_ERROR("app.job.updateError"),
    EX_JOB_PARAMETER_INVALID_TYPE("app.job.parameter.invalidType"),
    EX_JOB_PARAMETER_PREPARE_UPSERT_ERROR("app.job.parameter.prepareUpsertError"),
    EX_JOB_PARAMETER_PREPARE_VALIDATION_ERROR("app.job.parameter.prepareValidationError"),
    EX_JOB_PARAMETER_EXTRACT_ERROR("app.job.parameter.extractError"),
    EX_JOB_PARAMETER_VALIDATION_ERROR("app.job.parameter.validationError"),
    EX_JOB_BATCH_EXECUTION_FAILED("app.job.batch.execution.failed"),
    EX_JOB_BATCH_RESTART_FAILED("app.job.batch.restart.failed"),
    EX_JOB_BATCH_RESTART_FAILED_ALREADY_RUNNING("app.job.batch.restart.failed.already.running"),
    EX_JOB_BATCH_STOP_FAILED("app.job.batch.stop.failed"),
    EX_JOB_SAME_PARAMETERS("app.job.sameParameters"),
    EX_JOB_ALREADY_RUNNING("app.job.alreadyRunning"),
    EX_JOB_TRIGGER_UPDATE_ERROR("app.job.trigger.updateError"),
    EX_JOB_TRIGGER_PARAMETER_VALIDATION_ERROR("app.job.trigger.parameter.validationError"),
    EX_JOB_TRIGGER_NOT_FOUND("app.job.trigger.notFound"),
    EX_JOB_TRIGGER_DELETE_FAILED("app.job.trigger.deleteFailed"),
    EX_JOB_TRIGGER_START_JOB_NOT_FOUND("app.job.trigger.startJob.notFound"),
    EX_JOB_TRIGGER_SAME_NAME("app.job.trigger.sameName"),
    EX_JOB_TRIGGER_RECURSIVE_CALL("app.job.trigger.recursiveCall"),
    EX_JOB_MAPPING_INCORRECT("app.job.mapping.incorrect"),
    EX_JOB_EXECUTION_NOT_FOUND("app.job.execution.notFound"),
    EX_JOB_PARAMETERS_SOAP_VALIDATION_ERRORS("app.soap.job.parameters.validation.errors"),
    EX_JOB_UNKNOWN_PARAMETERS("app.job.unknown.parameters"),
    EX_JOB_PARAMETERS_NOT_SET("app.job.parameters.not.set"),
    EX_JOB_STEP_SUBMIT_FAILED("app.job.general.step.submit.failed"),
    EX_JOB_PARAMETERS_VALIDATION_ERRORS("app.job.parameters.validation.errors"),
    //--------------------------------------Bulk operations
    /**
     * Business analog for class cast exception in bulk operation service.
     */
    EX_BULK_OPERATION_INCORRECT_CLASS("app.bulk.operations.incorrect.class"),
    /**
     * partially filled record is incorrect
     */
    EX_BULK_OPERATION_MODIFY_RECORD_INCORRECT("app.bulk.operations.modify.record.incorrect"),
    /**
     * partially filled classifier is incorrect
     */
    EX_BULK_OPERATION_MODIFY_CLASSIFIER_INCORRECT("app.bulk.operations.modify.record.incorrect"),
    /**
     * Data is outdated, approve state already changed
     */
    EX_WF_DECLINE_RECORD_FAILED_VERSIONS_UPDATE_ERROR("app.wf.versions.update.error"),
    /**
     * Data is outdated, approve state already changed.
     */
    EX_WF_APPROVE_RECORD_FAILED_VERSIONS_UPDATE_ERROR("app.wf.versions.approve.error"),
    /**
     * Data is outdated, approve state already changed.
     */
    EX_WF_APPROVE_RECORD_FAILED_RELATIONS_VERSIONS_UPDATE_ERROR("app.wf.versions.approve.relations.error"),
    /**
     * Cannot start workflow process.
     */
    EX_WF_START_PROCESS_FAILED("app.wf.startProcessFailed"),
    /**
     * Cannot decline record. Parameter(s) missing.
     */
    EX_WF_COMPLETE_RECORD_FAILED_PARAMS_MISSING("app.wf.completeProcessFailedParamsMissing"),
    /**
     * Cannot decline record. Parameter(s) missing.
     */
    EX_WF_DECLINE_RECORD_FAILED_PARAMS_MISSING("app.wf.declineRecordChangeFailedParamsMissing"),
    /**
     * Cannot complete task. Task not found.
     */
    EX_WF_CANNOT_COMPLETE_TASK_NOT_FOUND("app.wf.cannot.complete.task.not.found"),
    /**
     * More then one task selected for complete.
     */
    EX_WF_CANNOT_COMPLETE_TASK_MORE_THEN_ONE("app.wf.cannot.complete.task.more.then.one"),
    /**
     * Cannot start process. Activiti returned inappropriate result.
     */
    EX_WF_CANNOT_START_PROCESS_WRONG_RESULT("app.wf.cannot.start.process"),
    /**
     * Cannot start process. User support handler denied process start.
     */
    EX_WF_CANNOT_START_PROCESS_NOT_ALLOWED("app.wf.cannot.start.process.handler.denial"),
    /**
     * Cannot add comment for process. Task not found.
     */
    EX_WF_CANNOT_ADD_PROCESS_COMMENT_TASK_NOT_FOUND("app.wf.cannot.add.process.comment.task.not.found"),
    /**
     * Cannot add comment for task. Task not found.
     */
    EX_WF_CANNOT_ADD_TASK_COMMENT_TASK_NOT_FOUND("app.wf.cannot.add.task.comment.task.not.found"),
    /**
     * Cannot get comments. No taskId or pocessInstanceId.
     */
    EX_WF_CANNOT_ADD_COMMENTS_NO_TASK_ID_OR_PROCESS_ID("app.wf.cannot.add.comments.no.params"),
    /**
     * Cannot get comments. No taskId or pocessInstanceId.
     */
    EX_WF_CANNOT_GET_PROCESS_COMMENTS_NO_TASK_ID_OR_PROCESS_ID("app.wf.cannot.get.comments.no.params"),
    /**
     * Cannot get attachments. No taskId or pocessInstanceId.
     */
    EX_WF_CANNOT_GET_ATTACHMENTS_NO_TASK_ID_OR_PROCESS_ID("app.wf.cannot.get.attachments.no.params"),
    /**
     * Cannot add attachment. No taskId or pocessInstanceId.
     */
    EX_WF_CANNOT_ADD_ATTACHMENT_NO_TASK_ID_OR_PROCESS_ID("app.wf.cannot.add.attachment.no.params"),
    /**
     * Cannot add attachment for process. Task not found.
     */
    EX_WF_CANNOT_ADD_PROCESS_ATTACHMENT_TASK_NOT_FOUND("app.wf.cannot.add.process.attachment.task.not.found"),
    /**
     * Cannot add attachment for task. Task not found.
     */
    EX_WF_CANNOT_ADD_TASK_ATTACHMENT_TASK_NOT_FOUND("app.wf.cannot.add.task.attachment.task.not.found"),
    /**
     * Cannot get attachment content. Attachment not found.
     */
    EX_WF_CANNOT_GET_CONTENT_ATTACHMENT_NOT_FOUND("app.wf.cannot.get.content.attachment.not.found"),
    /**
     * Cannot assign task. Task not found.
     */
    EX_WF_CANNOT_ASSIGN_TASK_NOT_FOUND("app.wf.cannot.assign.task.not.found"),
    /**
     * Cannot unassign task. Task not found.
     */
    EX_WF_CANNOT_UNASSIGN_TASK_NOT_FOUND("app.wf.cannot.unassign.task.not.found"),
    /**
     * Cannot assign task. Activiti engine error.
     */
    EX_WF_CANNOT_ASSIGN_ENGINE_ERROR("app.wf.cannot.assign.task.engine.error"),
    /**
     * Cannot unassign task. Activiti engine error.
     */
    EX_WF_CANNOT_UNASSIGN_ENGINE_ERROR("app.wf.cannot.unassign.task.engine.error"),
    /**
     * Cannot create process diagram. Process definition not found.
     */
    EX_WF_CANNOT_GENERATE_DIAGRAM_PROCESS_NOT_FOUND("app.wf.cannot.generate.diagram.process.not.found"),
    EX_WF_CANNOT_GENERATE_DIAGRAM_HISTORICAL_PROCESS_NOT_FOUND("app.wf.cannot.generate.diagram.historical.process.not.found"),
    //-------------------------- End of Workflow and process support

    /**
     * SOAP
     */
    EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_ENTITY_NAME("app.soap.request.info.empty.entityName"),
    EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_FROM_OR_TO("app.soap.request.info.empty.from.to"),
    EX_SOAP_INCORRECT_REQUEST_INFO_GET_FROM_AFTER_TO("app.soap.request.info.from.after.to"),
    EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_MASTER_KEYS("app.soap.request.merge.empty.master.keys"),
    EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_DUPLICATE_KEYS("app.soap.request.merge.empty.duplicate.keys"),

    EX_FOUND_SORT_CONDITION_WITHOUT_ATTR_NAME("app.soap.request.search.sort.condition.without.attr.name"),
    EX_FOUND_SORT_CONDITION_WITH_UNKNOWN_ATTRS("app.soap.request.search.sort.condition.with.unknown.attrs"),

    EX_SOAP_SEARCH_OPERATOR_NOT_SET("app.soap.request.search.atom.operator.is.not.set"),

    /**
     * Users.
     */
    EX_USER_PROPERTY_VALIDATION_ERROR("app.user.property.validationError"),
    EX_USER_DATA_VALIDATION_ERROR("app.user.data.validationError"),

    /**
     * Roles.
     */
    EX_ROLE_PROPERTY_VALIDATION_ERROR("app.role.property.validationError"),
    EX_ROLE_DATA_VALIDATION_ERROR("app.role.data.validationError"),
    EX_MATCHING_FIELD_INCORRECT_ID_ABSENT("app.matching.field.incorrect.id.absent"),
    EX_MATCHING_FIELD_INCORRECT_ATTR_NAME_ABSENT("app.matching.field.incorrect.name.absent"),
    EX_MATCHING_ALGO_INCORRECT_FIELDS_EMPTY("app.matching.algo.incorrect.fields.empty"),
    EX_MATCHING_ALGO_INCORRECT_NAME_ABSENT("app.matching.algo.incorrect.name.blank"),
    EX_MATCHING_ALGO_INCORRECT_ID_EMPTY("app.matching.algo.incorrect.id.empty"),
    EX_MATCHING_ALGO_INCORRECT_INNER_FIELDS("app.matching.algo.incorrect.inner.fields"),
    EX_MATCHING_ALGO_DOESNT_PRESENT("app.matching.algo.doesnot.present"),
    EX_MATCHING_RULE_INCORRECT_BLANK_NAME("app.matching.rule.incorrect.blank.name"),
    EX_MATCHING_RULE_INCORRECT_LONG_NAME("app.matching.rule.incorrect.long.name"),
    EX_MATCHING_RULE_INCORRECT_BLANK_ENTITY("app.matching.rule.incorrect.blank.entity"),
    EX_MATCHING_RULE_INCORRECT_INNER_ALGOS("app.matching.rule.incorrect.inner.algos"),
    EX_MATCHING_RULE_INCORRECT("app.matching.rule.incorrect"),
    EX_MATCHING_NEED_ALGORITHMS("app.matching.rule.need.algorithms"),
    EX_MATCHING_IMPORT_TYPE_UNSUPPORTED("app.matching.import.unsupported"),
    EX_MATCHING_GROUP_OR_RULE_NOT_FOUND("app.matching.group.rule.notFound"),
    EX_MATCHING_INCORRECT_PREPROCESSING_RULE("app.matching.rule.incorrectPreprocessing"),
    EX_MATCHING_CLUSTER_NOT_FOUND("app.matching.cluster.notFound"),
    EX_MATCHING_CLUSTER_DOES_NOT_CONTAINS_RECORD("app.matching.cluster.doesNot.contains.record"),
    EX_MATCHING_CLUSTER_ALREADY_MODIFIED("app.matching.cluster.modified.concurrent"),
    EX_MATCHING_USER_SETTINGS_INCORRECT("app.matching.settings.incorrect"),
    EX_MATCHING_RULE_CONTAIN_UNAVAILABLE_ATTRIBUTE("app.matching.rule.contain.unavailable.attribute"),
    EX_MATCHING_RULE_NAME_WAS_DUPLICATE("app.matching.rule.name.duplicate"),
    EX_CLASSIFIER_ALREADY_CREATED("app.classifier.exist"),
    EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE("app.classifier.not.exist"),
    EX_CLASSIFIER_NODE_ATTR_DUPL("app.classifier.node.attr.dupl"),
    EX_CLASSIFIER_NODE_ATTR_NAME_INCORRECT("app.classifier.node.attr.name.incorrect"),
    EX_CLASSIFIER_IMPORT_TYPE_UNSUPPORTED("app.classifier.content.type.unsupported"),
    EX_CLASSIFIER_IMPORT_DATA_EMPTY("app.classifier.data.empty"),
    EX_CLASSIFIER_IMPORT_BY_CODE_WITHOUT_PATTERN("app.classifier.import.by.code.without.pattern"),
    EX_CLASSIFIER_NODE_CODE_INCORRECT("app.classifier.node.code.incorrect"),
    EX_CLASSIFIER_NODE_CODE_NOT_UNIQUE("app.classifier.node.code.notUnique"),
    EX_CLASSIFIER_CODE_PATTERN_INCORRECT("app.classifier.code.pattern.incorrect"),
    EX_CLASSIFIER_NODE_CODE_DOESNT_MATCH_PARENT("app.classifier.node.code.doesntMatchParent"),
    EX_CLASSIFIER_NODE_CODE_DOESNT_MATCH_PARENT_CODE("app.classifier.node.code.doesntMatchParentCode"),
    EX_CLASSIFIER_NODE_ADD_MAXIMUM_EXCEEDED("app.classifier.node.add.maximum.exceeded"),
    EX_CLASSIFIER_SAME_CODE_AND_NAME_ON_ONE_LEVEL("app.classifier.node.code.name.notUnique"),
    EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE("app.classifier.invalidAttributeValue"),
    EX_CLASSIFIER_NOT_ATTACHED_NODES_TO_ROOT("app.classifier.not.attached.nodes.to.root"),
    EX_CLASSIFIER_MORE_THEN_ONE_ROOT_NODES("app.classifier.more.then.one.root.nodes"),
    EX_CLASSIFIER_DUPLICATE_NODES("app.classifier.duplicate.nodes"),
    EX_CLASSIFIER_DUPLICATE_IDS("app.classifier.duplicate.ids"),
    EX_CLASSIFIER_ATTRIBUTES_WITH_SAME_NAME_EXISTS("app.classifier.attributes.with.same.name.exists"),
    EX_CLASSIFIER_REQ_READ_ONLY_ATTRIBUTES("app.classifier.attributes.req.read.only"),
    EX_CLASSIFIER_MODIFICATION_ATTRIBUTE_WITH_DATA("app.classifier.attributes.modification.with.data"),
    EX_CLASSIFIER_WRONG_ATTRIBUTE_VALUE_OF_TYPE("app.classifier.wrong.attribute.value.of.type"),
    EX_CLASSIFIER_NO_PARENT_NODE("app.classifier.no.parent.node"),


    EX_CONVERSION_ATTACHMENT_TO_STEAM_FAILED("app.conversion.attachment.failed"),
    EX_CONVERSION_ATTACHMENT_TO_FILE_FAILED("app.conversion.attachment.failed"),
    EX_CONVERSION_STEAM_TO_STRING_FAILED("app.conversion.stream.string.failed"),
    EX_CONVERSION_EXCEL_TO_CLASSIFIER_FAILED("app.conversion.excel.classifier.failed"),
    EX_CONVERSION_EXCEL_TO_CLASSIFIER_NODE_FAILED("app.conversion.excel.classifier.node.failed"),
    EX_CLASSIFIER_FAILED_MISSING_FIELD("app.conversion.classifier.failed.missing.field"),
    EX_CLASSIFIER_NAME_INCORRECT("app.conversion.classifier.name.incorrect"),
    EX_CLASSIFIER_NODE_FAILED_MISSING_FIELD("app.conversion.classifier.node.failed.missing.field"),
    EX_CLASSIFIER_NODE_ATTR_FAILED_MISSING_FIELD("app.conversion.classifier.node.attr.failed.missing.field"),
    EX_CONVERSION_EXCEL_TO_FULL_CLASSIFIER_FAILED("app.conversion.excel.classifier.failed"),
    EX_CONVERSION_CLASSIFIER_TO_XML_FAILED("app.conversion.classifier.xml.failed"),
    EX_CONVERSION_CLASSIFIER_TO_XLSX_FAILED("app.conversion.classifier.xlsx.failed"),

    EX_MEASUREMENT_IMPORT_TYPE_UNSUPPORTED("app.measurement.content.type.unsupported"),
    EX_MEASUREMENT_IMPORT_EMPTY("app.measurement.empty.import.data"),
    EX_MEASUREMENT_MARSHAL_FAILED("app.measurement.marshal.failed"),
    EX_MEASUREMENT_BASE_IS_NOT_DEFINE("app.measurement.base.not.define"),
    EX_MEASUREMENT_BASE_UNIT_DUPL("app.measurement.base.unit.dupl"),
    EX_MEASUREMENT_DUPL_ID("app.measurement.dupl.id"),
    EX_MEASUREMENT_SOMEONE_ALREADY_REMOVE_VALUE("app.measurement.value.removed"),
    EX_MEASUREMENT_VALUE_ID_IS_NOT_DEFINE("app.measurement.value.id.not.define"),
    EX_MEASUREMENT_ID_INCORRECT_FOR_PATTERN("app.measurement.id.incorrect.pattern"),
    EX_MEASUREMENT_VALUE_NAME_IS_NOT_DEFINE("app.measurement.value.name.not.define"),
    EX_MEASUREMENT_VALUE_SHORT_NAME_IS_NOT_DEFINE("app.measurement.value.shortName.not.define"),
    EX_MEASUREMENT_FUNCTION_SHOULD_BE_STANDARD("app.measurement.base.conversion.function.incorrect"),
    EX_MEASUREMENT_UNITS_IDS_DUPLICATED("app.measurement.unit.ids.duplicated"),
    EX_MEASUREMENT_UNIT_ID_IS_NOT_DEFINE("app.measurement.unit.id.not.define"),
    EX_MEASUREMENT_UNIT_NAME_IS_NOT_DEFINE("app.measurement.unit.name.not.define"),
    EX_MEASUREMENT_UNIT_SHORT_NAME_IS_NOT_DEFINE("app.measurement.unit.shortName.not.define"),
    EX_MEASUREMENT_UNIT_FUNCTION_IS_NOT_DEFINE("app.measurement.unit.function.not.define"),
    EX_MEASUREMENT_UNIT_VALUE_ID_IS_NOT_DEFINE("app.measurement.unit.valueId.not.define"),
    EX_MEASUREMENT_CONVERSION_FAILED("app.measurement.conversion.failed"),
    EX_MEASUREMENT_CONVERSION_FUNCTION_INCORRECT("app.measurement.conversion.function.incorrect"),
    EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_REMOVED("app.measurement.merge.impossible.unit.was.removed"),
    EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_CHANGED("app.measurement.merge.impossible.unit.was.changed"),
    EX_MEASUREMENT_MERGE_IMPOSSIBLE_DIFFERENT_UNITS("app.measurement.merge.impossible.unit.is.different"),
    EX_MEASUREMENT_VALUE_DOESNT_EXIST("app.measurement.value.doesnt.exist"),
    EX_MEASUREMENT_REMOVING_FORBIDDEN("app.measurement.removing.forbidden"),

    EX_SYSTEM_LOG_FILE_APPENDER_NOT_FOUND_OR_MISCONFIGURED("app.sys.log.file.appender.not.found"),

    EX_UNMARSHALLING("app.unmarshalling"),
    EX_MARSHALLING("app.marshalling"),
    EX_MAINTENANCE("app.maintenance.cluster"),
    EX_MAINTENANCE_IMPORT_MODEL("app.maintenance.cluster.metaImport"),
    EX_META_IMPORT_MODEL_EL_NOT_FOUND("app.meta.import.elNotFound"),
    EX_META_IMPORT_MODEL_FILE_UNKNOWN("app.meta.import.fileUnknown"),
    EX_META_IMPORT_MODEL_FILE_UNKNOWN_ONLY_DIRS("app.meta.import.fileUnknownOnlyDirs"),
    EX_META_IMPORT_MODEL_FILE_DUPL_NOT_ALLOWED("app.meta.import.fileDuplNotAllowed"),
    EX_META_IMPORT_MODEL_FILE_STRUCTURE_INVALID("app.meta.import.fileStructureInvalid"),
    EX_META_IMPORT_MODEL_EL_DUPLICATE("app.meta.import.elDuplicate"),
    EX_META_IMPORT_MODEL_UNABLE_TO_PARSE("app.meta.import.unableToParse"),

    // Custom Properties
    EX_CUSTOM_PROPERTY_INVALID_NAMES("app.custom.property.invalid.names"),
    EX_CUSTOM_PROPERTY_DUPLICATED_NAMES("app.custom.property.duplicated.names"),

    // Runtime Properties
    EX_CONFIGURATION_PROPERTIES_INVALID("app.configuration.properties.invalid"),

    EX_DETACH_LAST_ORIGIN("app.data.detach.last.origin"),
    EX_ORIGIN_NOT_FOUND("app.data.origin.not.found"),

    // Merge User Exit
    EX_MERGE_BEFORE_USER_EXIT_ERROR("app.data.merge.record.before.user.exit.error"),
    EX_MERGE_AFTER_USER_EXIT_ERROR("app.data.merge.record.after.user.exit.error"),

    // Join User Exit
    EX_JOIN_USER_EXIT_BEFORE_ERROR("app.join.user.exit.before.error"),
    EX_JOIN_USER_EXIT_AFTER_ERROR("app.join.user.exit.after.error"),

    // Split User Exit
    EX_SPLIT_USER_EXIT_BEFORE_ERROR("app.split.user.exit.before.error"),
    EX_SPLIT_USER_EXIT_AFTER_ERROR("app.split.user.exit.after.error"),

    /**
     * Global exception for user exits
     */
    EX_RUN_EXIT("app.data.global.user.exit.exception"),

    EX_ERROR_WHILE_CREATING_INDEXES_LOCK_TIME_OUT("app.error.while.creating.indexes.lock.time.out"),
    EX_ERROR_WHILE_CREATING_INDEXES_INTERRUPTED("app.error.while.creating.indexes.interrupted"),

    EX_LOOKUP_ENTITY_HAS_CLASSIFIER_DATA("app.error.lookup.entity.has.classifier.data");

    /**
     * Exception code.
     */
    private final String code;

    /**
     * Constructor.
     * @param localizationCode message code
     */
    ExceptionId(final String localizationCode) {
        this.code = localizationCode;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
}
