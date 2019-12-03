package org.unidata.mdm.meta.exception;

import org.unidata.mdm.system.exception.ExceptionId;

/**
 * @author Mikhail Mikhailov on Oct 3, 2019
 */
public final class MetaExceptionIds {
    /**
     * Model element without id.
     */
    public static final ExceptionId EX_META_MODEL_ELEMENT_WITHOUT_ID
        = new ExceptionId("EX_META_MODEL_ELEMENT_WITHOUT_ID", "app.meta.element.withoutId");
    /**
     * Model element is not valid.
     */
    public static final ExceptionId EX_META_MODEL_ELEMENT_NOT_VALID
        = new ExceptionId("EX_META_MODEL_ELEMENT_NOT_VALID", "app.meta.element.notValid");
    /**
     * Simple attribute is incorrect.
     */
    public static final ExceptionId EX_META_SIMPLE_ATTRIBUTE_IS_INCORRECT
        = new ExceptionId("EX_META_SIMPLE_ATTRIBUTE_IS_INCORRECT", "app.meta.simpleAttribute.incorrect");
    /**
     * Measured settings are not present.
     */
    public static final ExceptionId EX_META_MEASUREMENT_SETTINGS_SHOULD_BE_DEFINED
        = new ExceptionId("EX_META_MEASUREMENT_SETTINGS_SHOULD_BE_DEFINED", "app.meta.measurement.settings.not.define");
    /**
     * Measured attr settings refer to a value, which is not present.
     */
    public static final ExceptionId EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINED_VALUE
        = new ExceptionId("EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINED_VALUE", "app.meta.measurement.settings.refer.undefine.value");
    /**
     * Measured attr settings refer to a unit, which is not present.
     */
    public static final ExceptionId EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINED_UNIT
        = new ExceptionId("EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINED_UNIT", "app.meta.measurement.settings.refer.undefine.unit");
    /**
     * JAXB context init failure.
     */
    public static final ExceptionId EX_META_JAXB_CONTEXT_INIT_FAILURE
        = new ExceptionId("EX_META_JAXB_CONTEXT_INIT_FAILURE", "app.meta.jaxb.context.init.failure");
    /**
     * Cannot marshal model.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_MODEL
        = new ExceptionId("EX_META_CANNOT_MARSHAL_MODEL", "app.meta.cannotMarshalModel");
    /**
     * Cannot unmarshal model.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_MODEL
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_MODEL", "app.meta.cannotUnmarshalModel");
    /**
     * Cannot marshal source system.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_SOURCE_SYSTEM
        = new ExceptionId("EX_META_CANNOT_MARSHAL_SOURCE_SYSTEM", "app.meta.cannotMarshallSourceSystem");
    /**
     * Cannot marshal enumeration.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_ENUMERATION
        = new ExceptionId("EX_META_CANNOT_MARSHAL_ENUMERATION", "app.meta.cannotMarshallEnumeration");
    /**
     * Cannot marshal lookup entity.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_LOOKUP_ENTITY
        = new ExceptionId("EX_META_CANNOT_MARSHAL_LOOKUP_ENTITY", "app.meta.cannotMarshallLookupEntity");
    /**
     * Cannot marshal nested entity.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_NESTED_ENTITY
        = new ExceptionId("EX_META_CANNOT_MARSHAL_NESTED_ENTITY", "app.meta.cannotMarshallNestedEntity");
    /**
     * Cannot marshal entity.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_ENTITY
        = new ExceptionId("EX_META_CANNOT_MARSHAL_ENTITY", "app.meta.cannotMarshallEntity");
    /**
     * Cannot marshal entities.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_ENTITIES_GROUP
        = new ExceptionId("EX_META_CANNOT_MARSHAL_ENTITIES_GROUP", "app.meta.cannotMarshallEntitiesGroup");
    /**
     * Cannot marshal relation.
     */
    public static final ExceptionId EX_META_CANNOT_MARSHAL_RELATION
        = new ExceptionId("EX_META_CANNOT_MARSHAL_RELATION", "app.meta.cannotMarshallRelation");
    /**
     * Cannot unmarshal source system.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_SOURCE_SYSTEM
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_SOURCE_SYSTEM", "app.meta.cannotUnmarshallSourceSystem");
    /**
     * Cannot unmarshal enumeration.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_ENUMERATION
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_ENUMERATION", "app.meta.cannotUnmarshallEnumeration");
    /**
     * Cannot unmarshal lookup entity.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_LOOKUP_ENTITY
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_LOOKUP_ENTITY", "app.meta.cannotUnmarshallLookupEntity");
    /**
     * Cannot unmarshal nested entity.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_NESTED_ENTITY
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_NESTED_ENTITY", "app.meta.cannotUnmarshallNestedEntity");
    /**
     * Cannot unmarshal entity.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_ENTITY
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_ENTITY", "app.meta.cannotUnmarshallEntity");
    /**
     * Cannot unmarshal relation.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_RELATION
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_RELATION", "app.meta.cannotUnmarshallRelation");
    /**
     * Cannot unmarshal group.
     */
    public static final ExceptionId EX_META_CANNOT_UNMARSHAL_GROUP
        = new ExceptionId("EX_META_CANNOT_UNMARSHAL_GROUP", "app.meta.cannotUnmarshalGroup");
    /**
     * Metadata insert of an object failed.
     */
    public static final ExceptionId EX_META_INSERT_FAILED
        = new ExceptionId("EX_META_INSERT_FAILED", "app.meta.metadataInsertFailed");
    /**
     * Metadata update of an object failed.
     */
    public static final ExceptionId EX_META_UPDATE_FAILED
        = new ExceptionId("EX_META_UPDATE_FAILED", "app.meta.metadataUpdateFailed");
    /**
     * Measured settings is not allowed
     */
    public static final ExceptionId EX_META_MEASUREMENT_SETTINGS_NOT_ALLOWED
        = new ExceptionId("EX_META_MEASUREMENT_SETTINGS_NOT_ALLOWED", "app.meta.measurement.settings.not.allow");
    /**
     * A Requared attr can be read only in the same time
     */
    public static final ExceptionId EX_META_ATTR_CAN_NOT_BE_REQUIRED_AND_READ_ONLY
        = new ExceptionId("EX_META_ATTR_CAN_NOT_BE_REQUIRED_AND_READ_ONLY", "app.meta.attr.settings.requared.readOnly");
    /**
     *  A Hidden attr must be also read only
     */
    public static final ExceptionId EX_META_ATTR_CAN_NOT_BE_HIDDEN_AND_NOT_READ_ONLY
        = new ExceptionId("EX_META_ATTR_CAN_NOT_BE_HIDDEN_AND_NOT_READ_ONLY", "app.meta.attr.settings.hidden.not.readOnly");
    /**
     * A Main displayable attr must be displayable in the same time
     */
    public static final ExceptionId EX_META_ATTR_CAN_NOT_BE_MAIN_DISPLAYABLE_AND_NOT_DISPLAYABLE
        = new ExceptionId("EX_META_ATTR_CAN_NOT_BE_MAIN_DISPLAYABLE_AND_NOT_DISPLAYABLE",
                "app.meta.attr.settings.mainDisplayable.not.displayable");
    /**
     * A displayable attr can not be hidden in the same time
     */
    public static final ExceptionId EX_META_ATTR_CAN_NOT_BE_DISPLAYABLE_AND_HIDDEN
        = new ExceptionId("EX_META_ATTR_CAN_NOT_BE_DISPLAYABLE_AND_HIDDEN", "app.meta.attr.settings.displayable.hidden");

    // Custom Properties
    public static final ExceptionId EX_CUSTOM_PROPERTY_INVALID_NAMES
        = new ExceptionId("EX_CUSTOM_PROPERTY_INVALID_NAMES", "app.custom.property.invalid.names");

    public static final ExceptionId EX_CUSTOM_PROPERTY_DUPLICATED_NAMES
        = new ExceptionId("EX_CUSTOM_PROPERTY_DUPLICATED_NAMES", "app.custom.property.duplicated.names");
    /**
     * Group is absent.
     */
    public static final ExceptionId EX_META_GROUP_IS_ABSENT
        = new ExceptionId("EX_META_GROUP_IS_ABSENT", "app.meta.entityGroup.absent");
    /**
     * Name of a top level object is reserved.
     */
    public static final ExceptionId EX_META_RESERVED_TOP_LEVEL_NAME
        = new ExceptionId("EX_META_RESERVED_TOP_LEVEL_NAME", "app.meta.reserved.toplevel.name");
    /**
     * Main displayable attribute absent
     */
    public static final ExceptionId EX_META_MAIN_DISPLAYABLE_ATTR_ABSENT
        = new ExceptionId("EX_META_MAIN_DISPLAYABLE_ATTR_ABSENT", "app.meta.main.displayable.attr.absent");
    /**
     * Start period of entity before global range of dates
     */
    public static final ExceptionId EX_META_PERIOD_START_BEFORE_GLOBAL_PERIOD
        = new ExceptionId("EX_META_PERIOD_START_BEFORE_GLOBAL_PERIOD", "app.meta.periodStart.invalid");
    /**
     * End period of entity after global range of dates
     */
    public static final ExceptionId EX_META_PERIOD_END_AFTER_GLOBAL_PERIOD
        = new ExceptionId("EX_META_PERIOD_END_AFTER_GLOBAL_PERIOD", "app.meta.periodEnd.invalid");
    /**
     * Code attribute is absent.
     */
    public static final ExceptionId EX_META_CODE_ATTRIBUTE_IS_ABSENT
        = new ExceptionId("EX_META_CODE_ATTRIBUTE_IS_ABSENT", "app.meta.codeAttribute.absent");
    /**
     * Code attribute is incorrect.
     */
    public static final ExceptionId EX_META_CODE_ATTRIBUTE_IS_INCORRECT
        = new ExceptionId("EX_META_CODE_ATTRIBUTE_IS_INCORRECT", "app.meta.codeAttribute.incorrect");
    /**
     * Relation hasn't one of the side
     */
    public static final ExceptionId EX_META_RELATION_SIDE_IS_ABSENT
        = new ExceptionId("EX_META_RELATION_SIDE_IS_ABSENT", "app.meta.relation.side.absent");
    /**
     * The 'to' side containment entity '{}' of the relation '{}' not found in update.
     */
    public static final ExceptionId EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_UPDATE
        = new ExceptionId("EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_UPDATE", "app.meta.to.containment.entity.not.found.update");

    /**
     * The 'to' side containment entity '{}' of the relation '{}' not found in model.
     */
    public static final ExceptionId EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_INIT
        = new ExceptionId("EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_INIT", "app.meta.to.containment.entity.not.found.init");

    public static final ExceptionId EX_META_ROOT_GROUP_IS_ABSENT
        = new ExceptionId("EX_META_ROOT_GROUP_IS_ABSENT", "app.meta.groupIsAbsent");

    public static final ExceptionId EX_META_NESTED_ENTITIES_DUPLICATE2
        = new ExceptionId("EX_META_NESTED_ENTITIES_DUPLICATE2", "app.meta.nestedEntitiesIsDuplicated2");

    public static final ExceptionId EX_META_IS_INCORRECT
        = new ExceptionId("EX_META_IS_INCORRECT", "app.meta.incorrect");

    public static final ExceptionId EX_META_IMPORT_MODEL_EL_DUPLICATE
        = new ExceptionId("EX_META_IMPORT_MODEL_EL_DUPLICATE", "app.meta.import.elDuplicate");

    public static final ExceptionId EX_MEASUREMENT_UNITS_IDS_DUPLICATED
        = new ExceptionId("EX_MEASUREMENT_UNITS_IDS_DUPLICATED", "app.measurement.unit.ids.duplicated");

    public static final ExceptionId EX_MEASUREMENT_MERGE_IMPOSSIBLE_DIFFERENT_UNITS
        = new ExceptionId("EX_MEASUREMENT_MERGE_IMPOSSIBLE_DIFFERENT_UNITS", "app.measurement.merge.impossible.unit.is.different");

    public static final ExceptionId EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_REMOVED
        = new ExceptionId("EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_REMOVED", "app.measurement.merge.impossible.unit.was.removed");

    public static final ExceptionId EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_CHANGED
        = new ExceptionId("EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_CHANGED", "app.measurement.merge.impossible.unit.was.changed");

    public static final ExceptionId EX_MEASUREMENT_CONVERSION_FUNCTION_INCORRECT
        = new ExceptionId("EX_MEASUREMENT_CONVERSION_FUNCTION_INCORRECT", "app.measurement.conversion.function.incorrect");

    public static final ExceptionId EX_MEASUREMENT_REMOVING_FORBIDDEN
        = new ExceptionId("EX_MEASUREMENT_REMOVING_FORBIDDEN", "app.measurement.removing.forbidden");

    public static final ExceptionId EX_MEASUREMENT_BASE_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_BASE_IS_NOT_DEFINE", "app.measurement.base.not.define");

    public static final ExceptionId EX_MEASUREMENT_SOMEONE_ALREADY_REMOVE_VALUE
        = new ExceptionId("EX_MEASUREMENT_SOMEONE_ALREADY_REMOVE_VALUE", "app.measurement.value.removed");

    public static final ExceptionId EX_MEASUREMENT_DUPL_ID
        = new ExceptionId("EX_MEASUREMENT_DUPL_ID", "app.measurement.dupl.id");

    public static final ExceptionId EX_MEASUREMENT_VALUE_ID_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_VALUE_ID_IS_NOT_DEFINE", "app.measurement.value.id.not.define");

    public static final ExceptionId EX_MEASUREMENT_ID_INCORRECT_FOR_PATTERN
        = new ExceptionId("EX_MEASUREMENT_ID_INCORRECT_FOR_PATTERN", "app.measurement.id.incorrect.pattern");

    public static final ExceptionId EX_MEASUREMENT_VALUE_NAME_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_VALUE_NAME_IS_NOT_DEFINE", "app.measurement.value.name.not.define");

    public static final ExceptionId EX_MEASUREMENT_VALUE_SHORT_NAME_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_VALUE_SHORT_NAME_IS_NOT_DEFINE", "app.measurement.value.shortName.not.define");

    public static final ExceptionId EX_MEASUREMENT_BASE_UNIT_DUPL
        = new ExceptionId("EX_MEASUREMENT_BASE_UNIT_DUPL", "app.measurement.base.unit.dupl");

    public static final ExceptionId EX_MEASUREMENT_FUNCTION_SHOULD_BE_STANDARD
        = new ExceptionId("EX_MEASUREMENT_FUNCTION_SHOULD_BE_STANDARD", "app.measurement.base.conversion.function.incorrect");

    public static final ExceptionId EX_MEASUREMENT_UNIT_ID_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_UNIT_ID_IS_NOT_DEFINE", "app.measurement.unit.id.not.define");

    public static final ExceptionId EX_MEASUREMENT_UNIT_NAME_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_UNIT_NAME_IS_NOT_DEFINE", "app.measurement.unit.name.not.define");

    public static final ExceptionId EX_MEASUREMENT_UNIT_SHORT_NAME_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_UNIT_SHORT_NAME_IS_NOT_DEFINE", "app.measurement.unit.shortName.not.define");

    public static final ExceptionId EX_MEASUREMENT_UNIT_FUNCTION_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_UNIT_FUNCTION_IS_NOT_DEFINE", "app.measurement.unit.function.not.define");

    public static final ExceptionId EX_MEASUREMENT_UNIT_VALUE_ID_IS_NOT_DEFINE
        = new ExceptionId("EX_MEASUREMENT_UNIT_VALUE_ID_IS_NOT_DEFINE", "app.measurement.unit.valueId.not.define");

    public static final ExceptionId EX_MEASUREMENT_CONVERSION_FAILED
        = new ExceptionId("EX_MEASUREMENT_CONVERSION_FAILED", "app.measurement.conversion.failed");

    public static final ExceptionId EX_DRAFT_IS_INCORRECT
        = new ExceptionId("EX_DRAFT_IS_INCORRECT", "app.draft.incorrect");

    public static final ExceptionId EX_DRAFT_UNABLE_TO_CHANGE
        = new ExceptionId("EX_DRAFT_UNABLE_TO_CHANGE", "app.draft.unable.to.change");

    public static final ExceptionId EX_META_INIT_METADATA_FAILED
        = new ExceptionId("EX_META_INIT_METADATA_FAILED", "app.meta.metadataServiceFailedToInitilalize");

    public static final ExceptionId EX_META_RELOAD_METADATA_FAILED
        = new ExceptionId("EX_META_RELOAD_METADATA_FAILED", "app.meta.metadataServiceFailedToReInitilalize");

    public static final ExceptionId EX_META_ENTITY_NOT_FOUND
        = new ExceptionId("EX_META_ENTITY_NOT_FOUND", "app.meta.entityNotFound");
    /**
     * Cannot aquire index lock.
     */
    public static final ExceptionId EX_META_INDEX_LOCK_TIME_OUT
        = new ExceptionId("EX_META_INDEX_LOCK_TIME_OUT", "app.error.while.creating.indexes.lock.time.out");
    /**
     * Interrupted caught.
     */
    public static final ExceptionId EX_MODEL_CREATE_INDEX_INTERRUPTED
        = new ExceptionId("EX_MODEL_CREATE_INDEX_INTERRUPTED", "app.error.while.creating.indexes.interrupted");
    /**
     * Nested entity not found in model.
     */
    public static final ExceptionId EX_META_MAPPING_NESTED_ENTITY_NOT_FOUND
        = new ExceptionId("EX_META_MAPPING_NESTED_ENTITY_NOT_FOUND", "app.search.mappingUnknownNestedEntity");
    /**
     * More than one root group found.
     */
    public static final ExceptionId EX_META_MORE_THEN_ONE_ROOT_GROUP
        = new ExceptionId("EX_META_MORE_THEN_ONE_ROOT_GROUP", "app.meta.more.then.one.root.group");
    /**
     * Not a sys init and no root group.
     */
    public static final ExceptionId EX_META_NOT_SYS_NO_ROOT_GROUP
        = new ExceptionId("EX_META_NOT_SYS_NO_ROOT_GROUP", "app.meta.not.sys.no.root.group");
    /**
     * Constructor.
     */
    private MetaExceptionIds() {
        super();
    }

}
