package com.unidata.mdm.backend.service.notification;

import static com.unidata.mdm.backend.service.dump.jaxb.JaxbDataRecordUtils.to;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.unidata.mdm.api.SoftDeleteActionType;
import com.unidata.mdm.api.UnidataEventType;
import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.api.UpsertActionType;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.dump.jaxb.JaxbDataRecordUtils;
import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import com.unidata.mdm.backend.service.notification.configs.NotificationConfigDeserializer;
import com.unidata.mdm.backend.service.notification.configs.NotificationConfigSerializer;
import com.unidata.mdm.backend.service.notification.messages.NotificationCacheMessage;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.data.ExternalSourceId;
import com.unidata.mdm.data.IntegralRecord;
import com.unidata.mdm.data.RelationBase;
import com.unidata.mdm.data.RelationTo;

/**
 * Notification utils.
 *
 * @author ilya.bykov
 */
public class NotificationUtils {

    /**
     * Disabling constructor.
     */
    private NotificationUtils() {
        super();
    }

    /**
     * Creates the origin upsert notification.
     *
     * @param originRecord the origin record
     * @param actionType   the action type
     * @param operationId  the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createOriginUpsertNotification(OriginRecord originRecord, UpsertAction actionType, List<OriginKey> originKeys, String operationId) {

        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withUpsertEventDetails(
                        JaxbUtils.getApiObjectFactory().createUpsertEventDetailsDef()
                                .withOriginRecord(to(originRecord, originRecord.getInfoSection(), com.unidata.mdm.data.OriginRecord.class))
                                .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                                .withUpsertActionType(actionType == null ? null : UpsertActionType.valueOf(actionType.name())))
                .withEventType(UnidataEventType.UPSERT)
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the etalon upsert notification.
     *
     * @param etalonRecord the etalon record
     * @param originKey    Origin key
     * @param actionType   the action type
     * @param operationId  the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonUpsertNotification(
            final EtalonRecord etalonRecord,
            final OriginKey originKey,
            final UpsertAction actionType,
            final List<OriginKey> originKeys,
            final String operationId
    ) {
        return createEtalonUpsertNotification(etalonRecord, originKey, actionType, originKeys, operationId, Collections.emptyMap());
    }

    /**
     * Creates the etalon upsert notification.
     *
     * @param etalonRecord the etalon record
     * @param originKey    Origin key
     * @param actionType   the action type
     * @param operationId  the operation id
     * @param externalSourceIds external sources ids
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonUpsertNotification(
            final EtalonRecord etalonRecord,
            final OriginKey originKey,
            final UpsertAction actionType,
            final List<OriginKey> originKeys,
            final String operationId,
            final Map<String, List<ExternalSourceId>> externalSourceIds
    ) {
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withUpsertEventDetails(
                        JaxbUtils.getApiObjectFactory().createUpsertEventDetailsDef()
                                .withEtalonRecord(
                                        to(
                                                etalonRecord,
                                                etalonRecord.getInfoSection(),
                                                com.unidata.mdm.data.EtalonRecord.class,
                                                externalSourceIds
                                        )
                                )
                                .withOriginKey(to(originKey))
                                .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                                .withUpsertActionType(actionType == null ? null : UpsertActionType.valueOf(actionType.name())))
                .withEventType(UnidataEventType.UPSERT)
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the origin soft delete notification.
     *
     * @param originRecordKey the origin record key
     * @param operationId     the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createOriginSoftDeleteNotification(
            OriginKey originRecordKey, EtalonKey etalonRecordKey, List<OriginKey> originKeys, String operationId) {

        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteEventDetailsDef()
                                .withOriginKey(to(originRecordKey))
                                .withEtalonKey(to(etalonRecordKey))
                                .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ORIGIN))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the etalon softdelete notification.
     *
     * @param etalonRecordKey the etalon record key
     * @param operationId     the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonSoftDeleteNotification(
            EtalonKey etalonRecordKey, List<OriginKey> originKeys, String operationId) {

        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteEventDetailsDef()
                                .withEtalonKey(to(etalonRecordKey))
                                .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ETALON))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the etalon's period softdelete notification.
     *
     * @param etalonRecordKey the etalon record key
     * @param etalonRecord    the etalon record
     * @param operationId     the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonPeriodSoftDeleteNotification(
            EtalonKey etalonRecordKey, EtalonRecord etalonRecord, List<OriginKey> originKeys, String operationId) {
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteEventDetailsDef()
                                .withEtalonKey(to(etalonRecordKey))
                                .withEtalonRecord(to(etalonRecord, etalonRecord.getInfoSection(), com.unidata.mdm.data.EtalonRecord.class))
                                .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ETALON_PERIOD))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the origin classifier soft delete notification.
     *
     * @param classifierKeys the classifier keys
     * @param operationId    the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createOriginClassifierSoftDeleteNotification(ClassifierKeys classifierKeys, String operationId) {
        EtalonKey classifierEtalonKey = EtalonKey.builder()
                .id(classifierKeys.getEtalonId())
                .build();
        OriginKey classifierOriginKey = OriginKey.builder()
                .id(classifierKeys.getOriginId())
                .sourceSystem(classifierKeys.getOriginSourceSystem())
                .build();
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteClassifierEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteClassifierEventDetailsDef()
                                .withEtalonKey(to(classifierEtalonKey))
                                .withOriginKey(to(classifierOriginKey))
                                .withOwningEtalonKey(to(classifierKeys.getRecord().getEtalonKey()))
                                .withOwningOriginKey(to(classifierKeys.getRecord().getOriginKey()))
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ORIGIN))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the etalon classifier soft delete notification.
     *
     * @param classifierKeys the classifier keys
     * @param operationId    the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonClassifierSoftDeleteNotification(
            ClassifierKeys classifierKeys, String operationId) {
        EtalonKey classifierEtalonKey = EtalonKey.builder()
                .id(classifierKeys.getEtalonId())
                .build();
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteClassifierEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteClassifierEventDetailsDef()
                                .withEtalonKey(to(classifierEtalonKey))
                                .withOwningEtalonKey(to(classifierKeys.getRecord().getEtalonKey()))
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ETALON))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the classifier etalon's  period soft delete notification.
     *
     * @param classifierKeys the classifier keys
     * @param operationId    the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonClassifierPeriodSoftDeleteNotification(
            ClassifierKeys classifierKeys, String operationId) {
        EtalonKey classifierEtalonKey = EtalonKey.builder()
                .id(classifierKeys.getEtalonId())
                .build();
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteClassifierEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteClassifierEventDetailsDef()
                                .withEtalonKey(to(classifierEtalonKey))
                                .withOwningEtalonKey(to(classifierKeys.getRecord().getEtalonKey()))
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ETALON_PERIOD))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the origin relation soft delete notification.
     *
     * @param relationKeys the relation keys
     * @param operationId  the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createOriginRelationSoftDeleteNotification(RelationKeys relationKeys, RelationType relationType, String operationId) {
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteRelationEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteRelationEventDetailsDef()
                                .withFromEtalonKey(to(relationKeys.getFrom().getEtalonKey()))
                                .withFromOriginKey(to(relationKeys.getFrom().getOriginKey()))
                                .withToEtalonKey(to(relationKeys.getTo().getEtalonKey()))
                                .withToOriginKey(to(relationKeys.getTo().getOriginKey()))
                                .withRelationType(relationType.name())
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ORIGIN))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the etalon relation soft delete notification.
     *
     * @param relationKeys the relation keys
     * @param relationType type of relation
     * @param operationId  the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonRelationSoftDeleteNotification(
            RelationKeys relationKeys, RelationType relationType, String operationId) {

        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteRelationEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteRelationEventDetailsDef()
                                .withFromEtalonKey(to(relationKeys.getFrom().getEtalonKey()))
                                .withFromOriginKey(to(relationKeys.getFrom().getOriginKey()))
                                .withToEtalonKey(to(relationKeys.getTo().getEtalonKey()))
                                .withToOriginKey(to(relationKeys.getTo().getOriginKey()))
                                .withRelationType(relationType.name())
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ETALON))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the relation etalon's  period soft delete notification.
     *
     * @param relationKeys the relation keys
     * @param relationType type of relation
     * @param operationId  the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createEtalonRelationPeriodSoftDeleteNotification(
            RelationKeys relationKeys, RelationType relationType, String operationId) {
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.SOFT_DELETE)
                .withSoftDeleteRelationEventDetails(
                        JaxbUtils.getApiObjectFactory().createSoftDeleteRelationEventDetailsDef()
                                .withFromEtalonKey(to(relationKeys.getFrom().getEtalonKey()))
                                .withFromOriginKey(to(relationKeys.getFrom().getOriginKey()))
                                .withToEtalonKey(to(relationKeys.getTo().getEtalonKey()))
                                .withToOriginKey(to(relationKeys.getTo().getOriginKey()))
                                .withRelationType(relationType.name())
                                .withSoftDeleteActionType(SoftDeleteActionType.SOFT_DELETE_ETALON_PERIOD))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     *
     * @param originRecordKey the origin record key
     * @param etalonRecordKey the etalon record key
     * @param originKeys supplementary origin keys
     * @param operationId the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createWipeDeleteNotification(
            OriginKey originRecordKey, EtalonKey etalonRecordKey, List<OriginKey> originKeys, String operationId) {

        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withEventType(UnidataEventType.WIPE_DELETE)
                .withWipeDeleteEventDetails(
                        JaxbUtils.getApiObjectFactory().createWipeDeleteEventDetailsDef()
                                .withOriginKey(to(originRecordKey))
                                .withEtalonKey(to(etalonRecordKey))
                                .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList())))
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the etalon merge notification.
     *
     * @param duplicates  the duplicate records
     * @param master      the winner record
     * @param operationId the operation id
     * @return the unidata message def
     */
    public static final UnidataMessageDef createEtalonMergeNotification(
            List<EtalonKey> duplicates, EtalonKey master, List<OriginKey> originKeys, String operationId) {

        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withPublishDate(now())
                .withOperationId(operationId)
                .withEventType(UnidataEventType.MERGE)
                .withMergeEventDetails(
                        JaxbUtils.getApiObjectFactory().createMergeEventDetailsDef()
                                .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                                .withLooserEtalonKey(duplicates == null ? null
                                        : duplicates.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                                .withWinningEtalonKey(to(master)));
    }

    public static UnidataMessageDef createEtalonRestoreNotification(EtalonKey etalonKey, List<OriginKey> originKeys, String entityName, String operationId) {

        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withPublishDate(now())
                .withOperationId(operationId)
                .withEventType(UnidataEventType.RESTORE)
                .withRestoreEventDetails(JaxbUtils.getApiObjectFactory().createRestoreEventDetailsDef()
                        .withEtalonKey(to(etalonKey))
                        .withSupplementaryKeys(originKeys.stream().map(JaxbDataRecordUtils::to).collect(toList()))
                        .withEntityName(entityName));
    }

    /**
     * Creates the etalon upsert notification.
     *
     * @param etalonClassifier the etalon classifier record
     * @param classifierKeys   Classifier key object
     * @param actionType       the action type
     * @param operationId      the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createClassifierUpsertNotification(
            EtalonClassifier etalonClassifier, ClassifierKeys classifierKeys, UpsertAction actionType, String operationId) {

        EtalonKey classifierEtalonKey = EtalonKey.builder()
                .id(classifierKeys.getEtalonId())
                .build();
        OriginKey classifierOriginKey = OriginKey.builder()
                .id(classifierKeys.getOriginId())
                .sourceSystem(classifierKeys.getOriginSourceSystem())
                .build();
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withUpsertEventClassifierDetails(
                        JaxbUtils.getApiObjectFactory().createUpsertEventClassifierDetailsDef()
                                .withEtalonRecord(to(etalonClassifier, etalonClassifier.getInfoSection(), com.unidata.mdm.data.EtalonClassifierRecord.class))
                                .withOwningEtalonKey(to(classifierKeys.getRecord().getEtalonKey()))
                                .withOwningOriginKey(to(classifierKeys.getRecord().getOriginKey()))
                                .withEtalonKey(to(classifierEtalonKey))
                                .withOriginKey(to(classifierOriginKey))
                                .withUpsertActionType(actionType == null ? null : UpsertActionType.valueOf(actionType.name())))
                .withEventType(UnidataEventType.UPSERT)
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Creates the etalon upsert notification.
     *
     * @param etalonRelation the etalon relation record
     * @param relationKeys   Relation key object
     * @param actionType     the action type
     * @param operationId    the operation id
     * @return the unidata message def
     */
    public static UnidataMessageDef createRelationUpsertNotification(
            EtalonRelation etalonRelation, RelationKeys relationKeys, UpsertAction actionType, String operationId) {

        Class<? extends RelationBase> recordClass = (RelationType.CONTAINS.equals(etalonRelation.getInfoSection().getType()) ?
                IntegralRecord.class : RelationTo.class);
        return JaxbUtils.getApiObjectFactory().createUnidataMessageDef()
                .withEventDate(now())
                .withUpsertEventRelationDetailsDef(
                        JaxbUtils.getApiObjectFactory().createUpsertEventRelationDetailsDef()
                                .withRecord(to(etalonRelation, etalonRelation.getInfoSection(), recordClass))
                                .withFromEtalonKey(to(relationKeys.getFrom().getEtalonKey()))
                                .withFromOriginKey(to(relationKeys.getFrom().getOriginKey()))
                                .withToEtalonKey(to(relationKeys.getTo().getEtalonKey()))
                                .withToOriginKey(to(relationKeys.getTo().getOriginKey()))
                                .withRelationType(etalonRelation.getInfoSection().getType().name())
                                .withUpsertActionType(actionType == null ? null : UpsertActionType.valueOf(actionType.name())))
                .withEventType(UnidataEventType.UPSERT)
                .withPublishDate(now())
                .withOperationId(operationId);
    }

    /**
     * Serialize cache message before store in database.
     *
     * @param msg
     * @return
     * @throws JsonProcessingException
     */
    public static String marshalNotificationCacheMessage(NotificationCacheMessage msg) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addSerializer(NotificationConfig.class, new NotificationConfigSerializer());
        mapper.registerModule(module);

        return mapper.writeValueAsString(msg);


    }

    /**
     * Deserialize cache message after read from database.
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static NotificationCacheMessage unmarshalNotificationCacheMessage(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(NotificationConfig.class, new NotificationConfigDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(data, NotificationCacheMessage.class);
    }

    /**
     * Serialize message to XML string.
     *
     * @param message unidata notification message.
     * @return XML representation of the unidata notification message(as a String)
     * @throws JAXBException If marshalling wasn't successfully finished.
     */
    public static String marshalUnidataMessageDef(UnidataMessageDef message) throws JAXBException {
        final Marshaller marshaller = JaxbUtils.getAPIContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final StringWriter stringWriter = new StringWriter();
        marshaller.marshal(
                new JAXBElement<>(new QName("", "unidataMessage"), UnidataMessageDef.class, message),
                stringWriter);
        return stringWriter.toString();
    }

    /**
     * Deserialize message from XML string.
     *
     * @param data XML representation of the unidata notification message(as a String)
     * @return unidata notification message
     * @throws JAXBException If marshalling wasn't successfully finished.
     */
    public static UnidataMessageDef unmarshalUnidataMessageDef(String data) throws JAXBException {
        return JaxbUtils.getAPIContext()
                .createUnmarshaller()
                .unmarshal(new StreamSource(new StringReader(data)), UnidataMessageDef.class)
                .getValue();
    }

    /**
     * Now.
     *
     * @return the XML gregorian calendar
     */
    private static XMLGregorianCalendar now() {
        return JaxbUtils.dateToXMGregorianCalendar(new Date());
    }

}
