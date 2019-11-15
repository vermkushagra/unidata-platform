package org.unidata.mdm.soap.data.exception;

import org.unidata.mdm.system.exception.ExceptionId;

/**
 * @author Alexander Malyshev
 */
public final class DataSoapExceptionIds {
    private DataSoapExceptionIds() { }

    public static final ExceptionId EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE =
            new ExceptionId("EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE", "app.data.jaxbTypeFactoryInitFailure");

    public static final ExceptionId EX_SYSTEM_JAXB_CONTEXT_INIT_FAILURE =
            new ExceptionId("EX_SYSTEM_JAXB_CONTEXT_INIT_FAILURE", "app.data.jaxbContextInitFailure");

    public static final ExceptionId EX_DATA_CANNOT_UNMARSHAL_ORIGIN =
            new ExceptionId("EX_DATA_CANNOT_UNMARSHAL_ORIGIN", "app.data.cannotUnmarshallOrigin");

    public static final ExceptionId EX_DATA_CANNOT_UNMARSHAL_RELATION =
            new ExceptionId("EX_DATA_CANNOT_UNMARSHAL_RELATION", "app.data.cannotUnmarshallRelation");

    public static final ExceptionId EX_DATA_CANNOT_MARSHAL_ETALON =
            new ExceptionId("EX_DATA_CANNOT_UNMARSHAL_RELATION", "app.data.cannotMarshallGolden");

    public static final ExceptionId EX_DATA_CANNOT_MARSHAL_ORIGIN =
            new ExceptionId("EX_DATA_CANNOT_MARSHAL_ORIGIN", "app.data.cannotMarshallOrigin");

    public static final ExceptionId EX_DATA_CANNOT_MARSHAL_RELATION =
            new ExceptionId("EX_DATA_CANNOT_MARSHAL_RELATION", "app.data.cannotMarshallRelation");

    public static final ExceptionId EX_DATA_V4_ETALON_ID_UUID_INVALID =
            new ExceptionId("EX_DATA_V4_ETALON_ID_UUID_INVALID", "app.data.upsert.etalon.key.uuid.invalid");

    public static final ExceptionId EX_DATA_V4_ORIGIN_ID_UUID_INVALID =
            new ExceptionId("EX_DATA_V4_ORIGIN_ID_UUID_INVALID", "app.data.upsert.origin.key.uuid.invalid");

    public static final ExceptionId EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_ENTITY_NAME =
            new ExceptionId("EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_ENTITY_NAME", "app.soap.request.info.empty.entityName");

    public static final ExceptionId EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_FROM_OR_TO =
            new ExceptionId("EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_FROM_OR_TO", "app.soap.request.info.empty.from.to");

    public static final ExceptionId EX_SOAP_INCORRECT_REQUEST_INFO_GET_FROM_AFTER_TO =
            new ExceptionId("EX_SOAP_INCORRECT_REQUEST_INFO_GET_FROM_AFTER_TO", "app.soap.request.info.from.after.to");

    public static final ExceptionId EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_DUPLICATE_KEYS =
            new ExceptionId("EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_DUPLICATE_KEYS", "app.soap.request.merge.empty.duplicate.keys");

    public static final ExceptionId EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_MASTER_KEYS =
            new ExceptionId("EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_MASTER_KEYS", "app.soap.request.merge.empty.master.keys");

    public static final ExceptionId EX_SECURITY_CANNOT_LOGIN =
            new ExceptionId("EX_SECURITY_CANNOT_LOGIN", "app.security.cannotLogin");

    public static final ExceptionId EX_SOAP_SEARCH_OPERATOR_NOT_SET =
            new ExceptionId("EX_SOAP_SEARCH_OPERATOR_NOT_SET", "app.soap.request.search.atom.operator.is.not.set");
}
