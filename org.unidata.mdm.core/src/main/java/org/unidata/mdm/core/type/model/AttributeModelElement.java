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

package org.unidata.mdm.core.type.model;

import java.util.List;
import java.util.Map;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov
 * Holds attributes information.
 */
public interface AttributeModelElement {
    /**
     * @author Mikhail Mikhailov
     * Type of data, which an attribute - simple, code or array so far - can hold.
     * TODO: Data converters.
     */
    public enum AttributeValueType {
        /**
         * Local date.
         */
        DATE("Date"),
        /**
         * Local time.
         */
        TIME("Time"),
        /**
         * Local TS.
         */
        TIMESTAMP("Timestamp"),
        /**
         * String.
         */
        STRING("String"),
        /**
         * Integer.
         */
        INTEGER("Integer"),
        /**
         * FP number.
         */
        NUMBER("Number"),
        /**
         * Boolean.
         */
        BOOLEAN("Boolean"),
        /**
         * Blob.
         */
        BLOB("Blob"),
        /**
         * Clob.
         */
        CLOB("Clob"),
        /**
         * Measured attribute init value.
         */
        MEASURED("Measured"),
        /**
         * Any of the above (used by CF).
         */
        ANY("Any"),
        /**
         * Special value, meaning "this attribute holds no data", as it is the case by complex attribute.
         */
        NONE("None");
        /**
         * Constructor.
         * @param value the JAXB friendly value
         */
        private AttributeValueType(String value) {
            this.value = value;
        }
        /**
         * JAXB friendly value.
         */
        private final String value;
        /**
         * Gets the JAXB friendly value.
         * @return
         */
        public String value() {
            return value;
        }
        /**
         * Creates instance from JAXB friendly value
         * @param v the value
         * @return enum instamce
         */
        public static AttributeValueType fromValue(String v) {

            for (AttributeValueType c: AttributeValueType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }

            throw new IllegalArgumentException(v);
        }

        public FieldType toSearchType() {

            switch (this) {
            case BLOB:
            case CLOB:
            case STRING:
                return FieldType.STRING;
            case BOOLEAN:
                return FieldType.BOOLEAN;
            case DATE:
                return FieldType.DATE;
            case TIME:
                return FieldType.TIME;
            case TIMESTAMP:
                return FieldType.TIMESTAMP;
            case INTEGER:
                return FieldType.INTEGER;
            case MEASURED:
            case NUMBER:
                return FieldType.NUMBER;
            case ANY:
                return FieldType.ANY;
            default:
                break;
            }

            return null;
        }
    }
    /**
     * Gets the attribute's name.
     * @return name
     */
    String getName();
    /**
     * Gets the attribute's display name.
     * @return display name
     */
    String getDisplayName();
    /**
     * Gets the value type, hold by this attribute.
     * @return value type
     */
    AttributeValueType getValueType();
    /**
     * Gets measure settings view of the attribute data.
     * @return settings or null, if the attribute is not the measured one
     */
    MeasuredModelElement getMeasured();
    /**
     * Gets the complex view of the attribute data.
     * @return complex or null, if the attribute is not the complex one
     */
    ComplexModelElement getComplex();
    /**
     * Gets lookup link name.
     * @return the name
     */
    String getLookupLinkName();
    /**
     * Get lookup entity display attributes.
     * @return the name
     */
    List<String> getLookupEntityDisplayAttributes();
    /**
     * Gets custom properties defined on the attribute, if any.
     * @return map
     */
    Map<String, String> getCustomProperties();
    /**
     * Show attr names for display or not.
     * @return true, if so, false otherwise
     */
    boolean showFieldNamesInDisplay();
    /**
     * Gets the enum name.
     * @return enum name
     */
    String getEnumName();
    /**
     * Gets the template value.
     * @return template value
     */
    String getLinkTemplate();
    /**
     * Gets the mask for simple | code | array attributes
     * @return mask
     */
    String getMask();
    /**
     * Gets exchange separator.
     * @return separator
     */
    String getExchangeSeparator();
    /**
     * Gets the full path of this attribute.
     * @return the path
     */
    String getPath();
    /**
     * @return the entity
     */
    ContainerModelElement getContainer();
    /**
     * Gets the parent complex attribute if it exists.
     * @return the parent
     */
    AttributeModelElement getParent();
    /**
     * Gets its children attributes, if this attribute is a complex one.
     * @return the children
     */
    List<AttributeModelElement> getChildren();
    /**
     * Has parent or not.
     * @return true, if has
     */
    boolean hasParent();
    /**
     * Has children or not.
     * @return true, if has
     */
    boolean hasChildren();
    /**
     * Returns the type name
     * @return the type name
     */
    String getTypeName();
    /**
     * Gets the hierarchie level this attribute is on.
     * @return the level
     */
    int getLevel();
    /**
     * Gets the order number in the record.
     * @return the order
     */
    int getOrder();
    /**
     * Checks for being a simple attribute.
     * @return true for simple, false otherwise
     */
    boolean isSimple();
    /**
     * Checks for being a code attribute.
     * @return true for code, false otherwise
     */
    boolean isCode();
    /**
     * Checks for being an array attribute.
     * @return true for array, false otherwise
     */
    boolean isArray();
    /**
     * Checks for being a complex attribute.
     * @return true for complex, false otherwise
     */
    boolean isComplex();
    /**
     * Checks for being a lookup link attribute.
     * @return true for lookup link, false otherwise
     */
    boolean isLookupLink();
    /**
     * Checks for being a template attribute.
     * @return true for template, false otherwise
     */
    boolean isLinkTemplate();
    /**
     * Checks for being a enum attribute.
     * @return true for enum, false otherwise
     */
    boolean isEnumValue();
    /**
     * Checks for being a measured attribute.
     * @return true for measured, false otherwise
     */
    boolean isMeasured();
    /**
     * Checks for being a BLOB attribute.
     * @return true for BLOB, false otherwise
     */
    boolean isBlob();
    /**
     * Checks for being a CLOB attribute.
     * @return true for CLOB, false otherwise
     */
    boolean isClob();
    /**
     * Checks for being a temporal attribute.
     * @return true for temporal, false otherwise
     */
    boolean isDate();
    /**
     * Checks for being a unique attribute.
     * @return true for unique, false otherwise
     */
    boolean isUnique();
    /**
     * Checks for being a nullable attribute.
     * @return true for nullable, false otherwise
     */
    boolean isNullable();
    /**
     * Checks for having a value mask.
     * @return true for having value mask, false otherwise
     */
    boolean hasMask();
    /**
     * Checks for participation in a path.
     * @return true if participates, false otherwise
     */
    boolean isOfPath(String path);
    /**
     * Checks for being a code alternative attribute.
     * @return true for codeAlternative, false otherwise
     */
    boolean isCodeAlternative();
    /**
     * Checks for being a main displayable attribute.
     * @return true if main displayable, false otherwise
     */
    boolean isMainDisplayable();
}