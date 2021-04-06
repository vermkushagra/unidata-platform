package com.unidata.mdm.backend.service.registration.keys;

import java.io.Serializable;

/**
 * Unique over system key, one of linked elements of system
 */
public interface UniqueRegistryKey extends Serializable {

    /**
     * @return type of unique key
     */
    Type keyType();

    /**
     * Types of uniquer registry keys in system
     */
    //todo replace russian text to keys from message source
    enum Type {
        ENTITY("реестр"),
        LOOKUP_ENTITY("справочник"),
        RELATION("связь"),
        CLASSIFIER("класификатор"),
        ATTRIBUTE("атрибут"),
        DQ("правило качества"),
        MATCHING_RULE("правило сопоставления"),
        MATCHING_GROUP("группа правил сопоставления"),
        MEASUREMENT_VALUE("единицы измерения");

        private String description;

        Type(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
