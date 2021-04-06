package com.unidata.mdm.backend.api.rest.dto.security;

/**
 * @author Mikhail Mikhailov
 * RO security category peer.
 */
public enum SecuredResourceCategoryRO {
    /**
     * System.
     */
    SYSTEM,
    /**
     * Resource is of the meta model category (i. e. Entity, Lookup entity, etc.).
     */
    META_MODEL,
    /**
     * Resource is of the classifier category.
     */
    CLASSIFIER
}
