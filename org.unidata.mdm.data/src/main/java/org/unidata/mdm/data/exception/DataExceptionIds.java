package org.unidata.mdm.data.exception;

import org.unidata.mdm.system.exception.ExceptionId;

/**
 * @author Mikhail Mikhailov
 * Data module exception IDs.
 */
public final class DataExceptionIds {
    /**
     * Interval type does not support unlock.
     */
    public static final ExceptionId EX_DATA_STORAGE_NOT_CONFIGURED
            = new ExceptionId("EX_DATA_STORAGE_NOT_CONFIGURED", "app.data.storage.not.configured");
    /**
     * Cluster metadata in invalid state.
     */
    public static final ExceptionId EX_DATA_STORAGE_INVALID_STATE
            = new ExceptionId("EX_DATA_STORAGE_INVALID_STATE", "app.data.storage.invalid.state");
    /**
     * Failed to start storage cluster.
     */
    public static final ExceptionId EX_DATA_STORAGE_START_FAILED
            = new ExceptionId("EX_DATA_STORAGE_START_FAILED", "app.data.start.storage.failed");
    /**
     * Failed to initialize storage cluster.
     */
    public static final ExceptionId EX_DATA_STORAGE_INIT_FAILED
            = new ExceptionId("EX_DATA_STORAGE_INIT_FAILED", "app.data.storage.init.failed");
    /**
     * Node line supplied in wrong format.
     */
    public static final ExceptionId EX_DATA_STORAGE_INIT_NODE_FORMAT
            = new ExceptionId("EX_DATA_STORAGE_INIT_NODE_FORMAT", "app.data.storage.node.format");
    /**
     * Supplied Node line contains no credentials.
     */
    public static final ExceptionId EX_DATA_STORAGE_INIT_NODE_NO_CREDENTIALS
            = new ExceptionId("EX_DATA_STORAGE_INIT_NODE_NO_CREDENTIALS", "app.data.storage.node.no.credentials");
    /**
     * Zero nodes initialized by the system.
     */
    public static final ExceptionId EX_DATA_STORAGE_ZERO_NODES_INITIALIZED
            = new ExceptionId("EX_DATA_STORAGE_ZERO_NODES_INITIALIZED", "app.data.storage.zero.nodes.initialized");
    /**
     * Invalid node requested.
     */
    public static final ExceptionId EX_DATA_STORAGE_INVALID_NODE_REQUESTED
            = new ExceptionId("EX_DATA_STORAGE_INVALID_NODE_REQUESTED", "app.data.storage.invalid.node.requested");
    /**
     * Shard to node calculated invalid result.
     */
    public static final ExceptionId EX_DATA_STORAGE_INVALID_SHARD_REQUESTED
            = new ExceptionId("EX_DATA_STORAGE_INVALID_SHARD_REQUESTED", "app.data.storage.invalid.shard.requested");
    /**
     * Shard numbers do not match.
     */
    public static final ExceptionId EX_DATA_STORAGE_SHARD_NUMBERS_DO_NOT_MATCH
            = new ExceptionId("EX_DATA_STORAGE_SHARD_NUMBERS_DO_NOT_MATCH", "app.data.storage.shard.numbers.do.not.match");
    /**
     * Invalid record identity.
     */
    public static final ExceptionId EX_DATA_RECORD_INVALID_KEYS
            = new ExceptionId("EX_DATA_RECORD_INVALID_KEYS", "app.data.records.invalid.record.keys");
    /**
     * Invalid relto identity.
     */
    public static final ExceptionId EX_DATA_RELATION_INVALID_KEYS
            = new ExceptionId("EX_DATA_RELATION_INVALID_KEYS", "app.data.rels.invalid.relation.keys");

    public static final ExceptionId EX_SYSTEM_JAXB_CANNOT_SET_FIELD_PERMISSION =
            new ExceptionId("EX_SYSTEM_JAXB_CANNOT_SET_FIELD_PERMISSION", "app.data.jaxbCannotSetFieldPermission");
    /**
     * Failed to migrate storage cluster metadata schema.
     */
    public static final ExceptionId EX_DATA_STORAGE_MIGRATE_META_FAILED =
            new ExceptionId("EX_DATA_STORAGE_MIGRATE_META_FAILED", "app.data.migrate.storage.meta.failed");
    /**
     * Migrate data schema failed.
     */
    public static final ExceptionId EX_DATA_STORAGE_MIGRATE_DATA_FAILED =
            new ExceptionId("EX_DATA_STORAGE_MIGRATE_DATA_FAILED", "app.data.migrate.schema.failed");
    /**
     * JAXB context init failure.
     */
    public static final ExceptionId EX_DATA_JAXB_CONTEXT_INIT_FAILURE =
            new ExceptionId("EX_DATA_JAXB_CONTEXT_INIT_FAILURE", "app.data.jaxbContextInitFailure");
    /**
     * Cannot unmarshal Original record.
     */
    public static final ExceptionId EX_DATA_CANNOT_UNMARSHAL_ORIGIN =
            new ExceptionId("EX_DATA_CANNOT_UNMARSHAL_ORIGIN", "app.data.cannotUnmarshallOrigin");
    /**
     * Cannot unmarshal Relation.
     */
    public static final ExceptionId EX_DATA_CANNOT_UNMARSHAL_RELATION =
            new ExceptionId("EX_DATA_CANNOT_UNMARSHAL_RELATION", "app.data.cannotUnmarshallRelation");
    /**
     * Cannot marshal Golden record.
     */
    public static final ExceptionId EX_DATA_CANNOT_MARSHAL_ETALON =
            new ExceptionId("EX_DATA_CANNOT_MARSHAL_ETALON", "app.data.cannotMarshallGolden");
    /**
     * Cannot marshal Original record.
     */
    public static final ExceptionId EX_DATA_CANNOT_MARSHAL_ORIGIN =
            new ExceptionId("EX_DATA_CANNOT_MARSHAL_ORIGIN", "app.data.cannotMarshallOrigin");
    /**
     * Cannot marshal Relation.
     */
    public static final ExceptionId EX_DATA_CANNOT_MARSHAL_RELATION =
            new ExceptionId("EX_DATA_CANNOT_MARSHAL_RELATION", "app.data.cannotMarshallRelation");

    // TODO: 22.10.2019 localization
    public static final ExceptionId EX_DATA_INVALID_LOB_OBJECT =
            new ExceptionId("EX_DATA_INVALID_LOB_OBJECT", "app.data.invalidLobObject");

    // TODO: 22.10.2019 localization
    public static final ExceptionId EX_DATA_CANNOT_PARSE_DATE =
            new ExceptionId("EX_DATA_CANNOT_PARSE_DATE", "app.data.cannotParseDate");

    // TODO: 22.10.2019 localization
    public static final ExceptionId EX_DATA_UNSUPPORTED_DATA_TYPE =
            new ExceptionId("EX_DATA_UNSUPPORTED_DATA_TYPE", "app.data.unsupportedDataType");

    /**
     * Etalon insert failed.
     */
    public static final ExceptionId EX_DATA_ETALON_INSERT_FAILED =
            new ExceptionId("EX_DATA_ETALON_INSERT_FAILED", "app.data.goldenInsertFailed");
    /**
     * Etalon approval state change failed.
     */
    public static final ExceptionId EX_DATA_ETALON_APPROVAL_STATE_UPDATE_FAILED =
            new ExceptionId("EX_DATA_ETALON_APPROVAL_STATE_UPDATE_FAILED", "app.data.etalon.approval.state.change.failed");
    /**
     * Golden insert failed.
     */
    public static final ExceptionId EX_DATA_ETALON_UPDATE_FAILED =
            new ExceptionId("EX_DATA_ETALON_UPDATE_FAILED", "app.data.goldenUpdateFailed");
    /**
     * Batch insert to vistory failed.
     */
    public static final ExceptionId EX_DATA_INSERT_VISTORY_BATCH_FAILED =
            new ExceptionId("EX_DATA_INSERT_VISTORY_BATCH_FAILED", "app.data.insertVistoryBatchFailed");
    /**
     * Origin record upsert failed.
     */
    public static final ExceptionId EX_DATA_RELATIONS_BATCH_UPSERT_ORIGIN_FAILED =
            new ExceptionId("EX_DATA_RELATIONS_BATCH_UPSERT_ORIGIN_FAILED", "app.data.originFailed");
    /**
     * Version record upsert failed.
     */
    public static final ExceptionId EX_DATA_RELATIONS_UPSERT_VERSION_FAILED =
            new ExceptionId("EX_DATA_RELATIONS_UPSERT_VERSION_FAILED", "app.data.versionFailed");
    /**
     * Ensure keys failed.
     */
    public static final ExceptionId EX_DATA_IDENTIFY_RECORD_FAILED =
            new ExceptionId("EX_DATA_IDENTIFY_RECORD_FAILED", "app.data.identify.record.failed");
    /**
     * Mass key resolution failed.
     */
    public static final ExceptionId EX_DATA_TIMELINE_MASS_KEYS_NO_IDENTITY =
            new ExceptionId("EX_DATA_TIMELINE_MASS_KEYS_NO_IDENTITY", "app.data.timeline.noIdentity");
    /**
     * External ID can not be joined. Etalon ID not found.
     */
    public static final ExceptionId EX_DATA_JOIN_ETALON_ID_NOT_FOUND =
            new ExceptionId("EX_DATA_JOIN_ETALON_ID_NOT_FOUND", "app.data.join.etalon.id.not.found");
    /**
     * External ID can not be joined. Invalid input.
     */
    public static final ExceptionId EX_DATA_JOIN_INVALID_INPUT =
            new ExceptionId("EX_DATA_JOIN_INVALID_INPUT", "app.data.join.invalid.input");
    /**
     * External ID can not be joined. Target register and the supplied one do not match.
     */
    public static final ExceptionId EX_DATA_JOIN_TARGET_REGISTER_DONT_MATCH =
            new ExceptionId("EX_DATA_JOIN_TARGET_REGISTER_DONT_MATCH", "app.data.join.target.register.dont.match");
    /**
     * External ID can not be joined. The key is already defined for the target.
     */
    public static final ExceptionId EX_DATA_JOIN_KEY_IS_ALREADY_DEFINED_IN_TARGET =
            new ExceptionId("EX_DATA_JOIN_KEY_IS_ALREADY_DEFINED_IN_TARGET", "app.data.join.key.already.in");
    /**
     * External ID can not be joined. The key is already used by another record.
     */
    public static final ExceptionId EX_DATA_JOIN_KEY_IS_ALREADY_USED_BY_ANOTHER =
            new ExceptionId("EX_DATA_JOIN_KEY_IS_ALREADY_USED_BY_ANOTHER", "app.data.join.key.already.used");
    /**
     * Meta model type wrapper for id not found for BVT calculation.
     */
    public static final ExceptionId EX_DATA_NO_ENTITY_ELEMENT_FOR_BVT_CALCULATION =
            new ExceptionId("EX_DATA_NO_ENTITY_ELEMENT_FOR_BVT_CALCULATION", "app.data.noTypeWrapperForBVTCalculation");
    /**
     * Meta model type wrapper for id is not BVT capable.
     */
    public static final ExceptionId EX_DATA_ENTITY_ELEMENT_NOT_BVT_CAPABLE =
            new ExceptionId("EX_DATA_ENTITY_ELEMENT_NOT_BVT_CAPABLE", "app.data.noTypeWrapperForBVTCalculation");
    /**
     * Unable to generate externalId, using autogeneration strategy for entity {}.
     * Generated key length exceeds the limit of 512 characters.
     */
    public static final ExceptionId EX_DATA_UPSERT_ID_GENERATION_STRATEGY_KEY_LENGTH =
            new ExceptionId("EX_DATA_UPSERT_ID_GENERATION_STRATEGY_KEY_LENGTH", "app.data.upsert.id.generation.strategy.key.length");
    /**
     * Unable to generate externalId or code attribute, using autogeneration strategy for entity {}.
     * Either no data was given or content for configured fields is missing or incomplete. Processing {}.
     */
    public static final ExceptionId EX_DATA_UPSERT_UNABLE_TO_APPLY_ID_GENERATION_STRATEGY =
            new ExceptionId("EX_DATA_UPSERT_UNABLE_TO_APPLY_ID_GENERATION_STRATEGY", "app.data.upsert.unable.to.apply.id.generation.strategy");
    /**
     * Invalid upsert request context. Neither etalon record nor origin record has been supplied. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_NO_INPUT =
            new ExceptionId("EX_DATA_UPSERT_NO_INPUT", "app.data.upsertNoInput");
    /**
     * Record can not be identified by supplied keys. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_INVALID_KEYS =
            new ExceptionId("EX_DATA_UPSERT_INVALID_KEYS", "app.data.upsertInvalidKeys");
    /**
     * Upsert failed, origin is in inactive state.
     */
    public static final ExceptionId EX_DATA_UPSERT_ORIGIN_INACTIVE =
            new ExceptionId("EX_DATA_UPSERT_ORIGIN_INACTIVE", "app.data.upsertOriginInactive");
    /**
     * Upsert failed, etalon is in inactive state.
     */
    public static final ExceptionId EX_DATA_UPSERT_ETALON_INACTIVE =
            new ExceptionId("EX_DATA_UPSERT_ETALON_INACTIVE", "app.data.upsertEtalonInactive");
    /**
     * Invalid upsert request context. Neither external id nor origin record key has been supplied. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_INVALID_ORIGIN_INPUT =
            new ExceptionId("EX_DATA_UPSERT_INVALID_ORIGIN_INPUT", "app.data.upsertInvalidOriginInput");
    /**
     * Source system must be defined. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_NO_SOURCE_SYSTEM =
            new ExceptionId("EX_DATA_UPSERT_NO_SOURCE_SYSTEM", "app.data.upsertNoSourceSystem");
    /**
     * Time line not exist
     */
    public static final ExceptionId EX_DATA_RELATION_CONTEXT_NO_IDENTITY =
            new ExceptionId("EX_DATA_RELATION_CONTEXT_NO_IDENTITY", "app.data.timeline.noIdentity");
    /**
     * Invalid upsert request context. No entity name was supplied. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_NO_ID =
            new ExceptionId("EX_DATA_UPSERT_NO_ID", "app.data.upsertNoEntityName");
    /**
     * Invalid upsert request context. Entity was not found by name. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME =
            new ExceptionId("EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME", "app.data.upsertEntityNotFoundByName");
    /**
     * Required attributes is not presented.
     */
    public static final ExceptionId EX_DATA_UPSERT_REQUIRED_ATTRS_NOT_PRESENT =
            new ExceptionId("EX_DATA_UPSERT_REQUIRED_ATTRS_NOT_PRESENT", "app.data.upsert.required.attrs.notPresented");
    /**
     * Attribute supplied for upsert is missing in the model. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE =
            new ExceptionId("EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE", "app.data.upsert.incorrect.quantity.complex.attrs.range");
    /**
     * Attribute supplied for upsert is missing in the model. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_MISSING_ATTRIBUTE =
            new ExceptionId("EX_DATA_UPSERT_MISSING_ATTRIBUTE", "app.data.upsertMissingAttribute");
    /**
     * Attribute supplied for upsert is of the wrong type compared to the model. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_ATTRIBUTE_TYPE =
            new ExceptionId("EX_DATA_UPSERT_WRONG_ATTRIBUTE_TYPE", "app.data.upsertWrongAttributeType");
    /**
     * Wrong code attribute link value. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_SIMPLE_CODE_ATTRIBUTE_REFERENCE_VALUE =
            new ExceptionId("EX_DATA_UPSERT_WRONG_SIMPLE_CODE_ATTRIBUTE_REFERENCE_VALUE", "app.data.upsertWrongSpecialAttributeType");
    /**
     * Attribute supplied for upsert is of the wrong type compared to the model. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE =
            new ExceptionId("EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE", "app.data.upsertWrongSpecialAttributeType");
    /**
     * Enum attribute has a value which not present in system
     */
    public static final ExceptionId EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT =
            new ExceptionId("EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT", "app.data.upsert.attribute.enum.value.incorrect");
    /**
     * Value in measured attribute is not present
     */
    public static final ExceptionId EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT =
            new ExceptionId("EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT", "app.data.attribute.measured.value.notPresent");
    /**
     * Unit in measured attribute is not present
     */
    public static final ExceptionId EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT =
            new ExceptionId("EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT", "app.data.attribute.measured.unit.notPresent");
    /**
     * Attribute supplied for upsert is of the wrong value type compared to the model. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_CODE_ATTRIBUTE_VALUE_TYPE =
            new ExceptionId("EX_DATA_UPSERT_WRONG_CODE_ATTRIBUTE_VALUE_TYPE", "app.data.upsertWrongCodeAttributeValueType");
    /**
     * Attribute supplied for upsert is of the wrong value type compared to the model. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE =
            new ExceptionId("EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE", "app.data.upsertWrongSimpleAttributeValueType");
    /**
     * Wrong code attribute link value. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_ARRAY_CODE_ATTRIBUTE_REFERENCE_VALUE =
            new ExceptionId("EX_DATA_UPSERT_WRONG_ARRAY_CODE_ATTRIBUTE_REFERENCE_VALUE", "app.data.upsertWrongArrayCodeAttributeReferenceType");
    /**
     * Attribute supplied for upsert is of the wrong value type compared to the model. Upsert rejected.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_ARRAY_ATTRIBUTE_VALUE_TYPE =
            new ExceptionId("EX_DATA_UPSERT_WRONG_ARRAY_ATTRIBUTE_VALUE_TYPE", "app.data.upsertWrongArrayAttributeValueType");
    /**
     * Large object attribute has incorrect link to attach file
     */
    public static final ExceptionId EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE =
            new ExceptionId("EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE", "app.data.upsert.unavailable.large.object.value");
    /**
     * The user has no or unsufficient insert rights.
     */
    public static final ExceptionId EX_DATA_UPSERT_INSERT_NO_RIGHTS =
            new ExceptionId("EX_DATA_UPSERT_INSERT_NO_RIGHTS", "app.data.upsert.noRightsForInsert");
    /**
     * The user has no or unsufficient update rights.
     */
    public static final ExceptionId EX_DATA_UPSERT_UPDATE_NO_RIGHTS =
            new ExceptionId("EX_DATA_UPSERT_UPDATE_NO_RIGHTS", "app.data.upsert.noRightsForUpdate");
    /**
     * Soap user don't able to upsert record if record in pending state.
     */
    public static final ExceptionId EX_DATA_UPSERT_NOT_ACCEPTED_HAS_PENDING_RECORD =
            new ExceptionId("EX_DATA_UPSERT_NOT_ACCEPTED_HAS_PENDING_RECORD", "app.data.upsert.hasPendingVersions");
    /**
     * No configured pipeline for entity name.
     */
    public static final ExceptionId EX_DATA_UPSERT_RECORD_NO_SELECTABLE_PIPELINE =
            new ExceptionId("EX_DATA_UPSERT_RECORD_NO_SELECTABLE_PIPELINE", "app.data.upsert.record.no.selectable.pipeline");
    /**
     * No configured pipeline for entity name.
     */
    public static final ExceptionId EX_DATA_GET_RECORD_NO_SELECTABLE_PIPELINE =
            new ExceptionId("EX_DATA_GET_RECORD_NO_SELECTABLE_PIPELINE", "app.data.get.record.no.selectable.pipeline");
    /**
     * Incorrect validity range input.
     */
    public static final ExceptionId EX_DATA_VALIDITY_PERIOD_INCORRECT =
            new ExceptionId("EX_DATA_VALIDITY_PERIOD_INCORRECT", "app.data.validity.period.incorrect");
    /**
     * Alias code attribute is invalid
     */
    public static final ExceptionId EX_DATA_UPSERT_INVALID_ALIAS_CODE_ATTRIBUTE =
            new ExceptionId("EX_DATA_UPSERT_INVALID_ALIAS_CODE_ATTRIBUTE", "app.data.upsert.invalidAliasCodeAttribute");
    /**
     * Attribute and attribute definition have link to unavailable measurement value
     */
    public static final ExceptionId EX_DATA_UPSERT_MEASUREMENT_VALUE_UNAVAILABLE =
            new ExceptionId("EX_DATA_UPSERT_MEASUREMENT_VALUE_UNAVAILABLE", "app.data.upsert.unavailable.measurement.value");
    /**
     * Attribute and attribute definition have link to unavailable measurement value
     */
    public static final ExceptionId EX_MEASUREMENT_VALUE_DOESNT_EXIST =
            new ExceptionId("EX_MEASUREMENT_VALUE_DOESNT_EXIST", "app.measurement.value.doesnt.exist");
    /**
     * Attribute and attribute definition have different measurement values.
     */
    public static final ExceptionId EX_DATA_UPSERT_WRONG_MEASUREMENT_VALUES =
            new ExceptionId("EX_DATA_UPSERT_WRONG_MEASUREMENT_VALUES", "app.data.upsert.wrong.measurement.values");
    /**
     * Attribute has link to unavailable measurement unit
     */
    public static final ExceptionId EX_DATA_UPSERT_MEASUREMENT_UNIT_UNAVAILABLE =
            new ExceptionId("EX_DATA_UPSERT_MEASUREMENT_UNIT_UNAVAILABLE", "app.data.upsert.unavailable.measurement.unit");
    /**
     * Measurement attr has incorrect value after enrich in dq
     */
    public static final ExceptionId EX_DATA_UPSERT_ENRICH_MEASUREMENT_VALUE_IS_INCORRECT =
            new ExceptionId("EX_DATA_UPSERT_ENRICH_MEASUREMENT_VALUE_IS_INCORRECT", "app.data.upsert.incorrect.enrich.measurement.attr");
    /**
     * Invalid get request context.
     */
    public static final ExceptionId EX_DATA_GET_INVALID_INPUT =
            new ExceptionId("EX_DATA_GET_INVALID_INPUT", "app.data.invalidGetInput");
    /**
     * Record not found by supplied keys.
     */
    public static final ExceptionId EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS =
            new ExceptionId("EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS", "app.data.notFoundByKeys");
    /**
     * Etalon found, but register names do not match.
     */
    public static final ExceptionId EX_ENTITY_NAME_AND_ETALON_ID_MISMATCH =
            new ExceptionId("EX_ENTITY_NAME_AND_ETALON_ID_MISMATCH", "entity_name_and_etalon_id_mismatch");
    /**
     * Record can not be read due to unsufficient rights.
     */
    public static final ExceptionId EX_DATA_GET_NO_RIGHTS =
            new ExceptionId("EX_DATA_GET_NO_RIGHTS", "app.data.get.noReadRights");
    /**
     * Invalid delete request context.
     */
    public static final ExceptionId EX_DATA_INVALID_DELETE_INPUT =
            new ExceptionId("EX_DATA_INVALID_DELETE_INPUT", "app.data.invalidDeleteInput");
    /**
     * Soap user don't able to upsert record if record in pending state.
     */
    public static final ExceptionId EX_DATA_DELETE_PERIOD_NOT_ACCEPTED_HAS_PENDING_RECORD =
            new ExceptionId("EX_DATA_DELETE_PERIOD_NOT_ACCEPTED_HAS_PENDING_RECORD", "app.data.deletePeriod.hasPendingVersions");
    /**
     * Record can not be deleted due to unsufficient rights.
     */
    public static final ExceptionId EX_DATA_DELETE_NO_RIGHTS =
            new ExceptionId("EX_DATA_DELETE_NO_RIGHTS", "app.data.delete.noReadRights");
    /**
     * Constructor.
     */
    private DataExceptionIds() {
        super();
    }
}
