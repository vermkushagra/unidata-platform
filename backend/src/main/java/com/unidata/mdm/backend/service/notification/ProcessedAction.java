package com.unidata.mdm.backend.service.notification;
/**
 * @author amagdenko
 * The action, being processed by the notification engine.
 */
public enum ProcessedAction {
    DELETE,
    RESTORE,
    MERGE,
    UPSERT_ORIGIN,
    UPSERT_ETALON,
    RESEND_ETALON,
    UPSERT_CLASSIFIER,
    DELETE_CLASSIFIER,
    UPSERT_RELATION,
    DELETE_RELATION
}
