package com.unidata.mdm.backend.service.data.merge;


/**
 * @author Mikhail Mikhailov
 * Transition types.
 */
public enum TransitionType {
    /**
     * Object create.
     */
    CREATE,
    /**
     * Etalon merge (one or more etalon ids are merged to a master id).
     */
    ETALON_MERGE,
    /**
     * Etalon unmerge (one of the duplicates becomes independent again).
     */
    ETALON_UNMERGE,
    /**
     * A single origin is either attached as new one
     * or detached from one etalon and becomes part of the state of another etalon.
     */
    ORIGIN_ATTACH,
    /**
     * An origin is either deactivated
     * or detached from an etalon and becomes part of the state of a new etalon.
     */
    ORIGIN_DETACH
}
