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

package org.unidata.mdm.meta.type.info.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.ComplexModelElement;
import org.unidata.mdm.core.type.model.ContainerModelElement;
import org.unidata.mdm.core.type.model.MeasuredModelElement;
import org.unidata.mdm.meta.AbstractAttributeDef;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.AbstractSimpleAttributeDef;
import org.unidata.mdm.meta.ArrayAttributeDef;
import org.unidata.mdm.meta.AttributeType;
import org.unidata.mdm.meta.CodeAttributeDef;
import org.unidata.mdm.meta.ComplexAttributeDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.SimpleAttributeDef;
import org.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 */
public class AttributeInfoHolder implements AttributeModelElement, MeasuredModelElement, ComplexModelElement {
    /**
     * The attribute type.
     */
    private final AttributeType type;
    /**
     * Attribute definition.
     */
    private final AbstractAttributeDef attribute;
    /**
     * Entity definition.
     */
    private final ContainerModelElement container;
    /**
     * Parent link.
     */
    private final AttributeModelElement parent;
    /**
     * Children.
     */
    private final List<AttributeModelElement> children = new ArrayList<>();
    /**
     * Calculated path.
     */
    private final String path;
    /**
     * Path broken up in tokens.
     */
    private final String[] tokens;
    /**
     * The depth of this path.
     */
    private final int level;
    /**
     * Order.
     */
    private final int order;
    /**
     * Code alternative flag.
     */
    private final boolean codeAlternative;
    /**
     * Constructor.
     * @param attr the attribute
     * @param parent parent link
     * @param path calculated path
     * @param level depth of this attribute
     */
    public AttributeInfoHolder(SimpleAttributeDef attr, AbstractEntityDef entity, AttributeModelElement parent, String path, int level) {
        this.attribute = attr;
        this.container = new ContainerInfoHolder(entity);
        this.parent = parent;
        this.path = path;
        this.level = level;
        this.order = attr.getOrder().intValue();
        this.type = AttributeType.SIMPLE;
        this.tokens = StringUtils.split(path, '.');
        this.codeAlternative = false;
    }
    /**
     * Constructor.
     * @param attr the attribute
     * @param parent parent link
     * @param path calculated path
     * @param level depth of this attribute
     */
    public AttributeInfoHolder(ArrayAttributeDef attr, AbstractEntityDef entity, AttributeModelElement parent, String path, int level) {
        this.attribute = attr;
        this.container = new ContainerInfoHolder(entity);
        this.parent = parent;
        this.path = path;
        this.level = level;
        this.order = attr.getOrder().intValue();
        this.type = AttributeType.ARRAY;
        this.tokens = StringUtils.split(path, '.');
        this.codeAlternative = false;
    }
    /**
     * Constructor.
     * @param attr the attribute
     * @param parent parent link
     * @param path calculated path
     * @param level depth of this attribute
     * @param isAlternative tells whether this code attr is an alternative one
     */
    public AttributeInfoHolder(CodeAttributeDef attr, AbstractEntityDef entity, AttributeModelElement parent, String path, int level, boolean isAlternative) {
        this.attribute = attr;
        this.container = new ContainerInfoHolder(entity);
        this.parent = parent;
        this.path = path;
        this.level = level;
        this.order = -1;
        this.type = AttributeType.CODE;
        this.tokens = StringUtils.split(path, '.');
        this.codeAlternative = isAlternative;
    }
    /**
     * Constructor.
     * @param attr the attribute
     * @param parent parent link
     * @param path calculated path
     * @param level depth of this attribute
     */
    public AttributeInfoHolder(ComplexAttributeDef attr, AbstractEntityDef entity, AttributeModelElement parent, String path, int level) {
        this.attribute = attr;
        this.container = new ContainerInfoHolder(entity);
        this.parent = parent;
        this.path = path;
        this.level = level;
        this.order = attr.getOrder().intValue();
        this.type = AttributeType.COMPLEX;
        this.tokens = StringUtils.split(path, '.');
        this.codeAlternative = false;
    }
    /**
     * @return the attribute
     */
    public AbstractAttributeDef getAttribute() {
        return attribute;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public MeasuredModelElement getMeasured() {
        return isMeasured() ? this : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueId() {
        return Objects.isNull(((SimpleAttributeDef) attribute).getMeasureSettings())
                ? null
                : ((SimpleAttributeDef) attribute).getMeasureSettings().getValueId();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultUnitId() {
        return Objects.isNull(((SimpleAttributeDef) attribute).getMeasureSettings())
                ? null
                : ((SimpleAttributeDef) attribute).getMeasureSettings().getDefaultUnitId();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinCount() {
        return ((ComplexAttributeDef) attribute).getMinCount().intValue();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxCount() {
        final BigInteger maxCount = ((ComplexAttributeDef) attribute).getMaxCount();
        return maxCount != null ? maxCount.intValue() : Integer.MAX_VALUE;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getNestedEntityName() {
        return ((ComplexAttributeDef) attribute).getNestedEntityName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ComplexModelElement getComplex() {
        return isComplex() ? this : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return attribute.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        return attribute.getDisplayName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMask() {

        if (isSimple()) {
            return ((SimpleAttributeDef) attribute).getMask();
        } else if (isArray()) {
            return ((ArrayAttributeDef) attribute).getMask();
        } else if (isCode()) {
            return ((CodeAttributeDef) attribute).getMask();
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getExchangeSeparator() {

        if (isArray()) {
            return ((ArrayAttributeDef) attribute).getExchangeSeparator();
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNullable() {

        if (isSimple()) {
            return ((SimpleAttributeDef) attribute).isNullable();
        } else if (isArray()) {
            return ((ArrayAttributeDef) attribute).isNullable();
        } else if (isCode()) {
            return ((CodeAttributeDef) attribute).isNullable();
        }

        return false;
    }
    /**
     * {@inheritDoc}
     * TODO: Calc value statically -- all fields are final.
     */
    @Override
    public AttributeValueType getValueType() {

        if (isComplex()) {
            return AttributeValueType.NONE;
        }

        SimpleDataType t;
        if (isLookupLink()) {
            t = getLookupEntityCodeAttributeType();
        } else if (isEnumValue() || isLinkTemplate()) {
            t = SimpleDataType.STRING;
        } else {
            t = getSimpleDataType();
        }

        if (t == null) {
            return AttributeValueType.NONE;
        }

        switch (t) {
        case ANY:
            return AttributeValueType.ANY;
        case BLOB:
            return AttributeValueType.BLOB;
        case BOOLEAN:
            return AttributeValueType.BOOLEAN;
        case CLOB:
            return AttributeValueType.CLOB;
        case DATE:
            return AttributeValueType.DATE;
        case INTEGER:
            return AttributeValueType.INTEGER;
        case MEASURED:
            return AttributeValueType.MEASURED;
        case NUMBER:
            return AttributeValueType.NUMBER;
        case STRING:
            return AttributeValueType.STRING;
        case TIME:
            return AttributeValueType.TIME;
        case TIMESTAMP:
            return AttributeValueType.TIMESTAMP;
        default:
            break;
        }

        return null;
    }
    /**
     * Gets lookup link name.
     * @return the name
     */
    @Override
    public String getLookupLinkName() {

        if (isSimple()) {
            return ((SimpleAttributeDef) attribute).getLookupEntityType();
        } else if (isArray()) {
            return ((ArrayAttributeDef) attribute).getLookupEntityType();
        }

        return null;
    }

    /**
     * Get lookup entity display attributes.
     * @return the name
     */
    @Override
    public List<String> getLookupEntityDisplayAttributes() {

        if (isSimple()) {
            return new ArrayList<>(((SimpleAttributeDef) attribute).getLookupEntityDisplayAttributes());
        } else if (isArray()) {
            return new ArrayList<>(((ArrayAttributeDef) attribute).getLookupEntityDisplayAttributes());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getCustomProperties() {
        return attribute.getCustomProperties().stream()
                .collect(Collectors.toMap(CustomPropertyDef::getName, CustomPropertyDef::getValue));
    }
    /**
     * Show attr names for display or not.
     * @return true, if so, false otherwise
     */
    @Override
    public boolean showFieldNamesInDisplay() {

        if (isSimple()) {
            return (((SimpleAttributeDef) attribute).isUseAttributeNameForDisplay());
        } else if (isArray()) {
            return (((ArrayAttributeDef) attribute).isUseAttributeNameForDisplay());
        }

        return false;
    }
    /**
     * Gets the enum name.
     * @return enum name
     */
    @Override
    public String getEnumName() {
        return isEnumValue() ? ((SimpleAttributeDef) attribute).getEnumDataType() : null;
    }

    /**
     * Gets the template value.
     * @return template value
     */
    @Override
    public String getLinkTemplate() {
        return isLinkTemplate() ? ((SimpleAttributeDef) attribute).getLinkDataType() : null;
    }

    /**
     * @return the path
     */
    @Override
    public String getPath() {
        return path;
    }

    /**
     * @return the entity
     */
    @Override
    public ContainerModelElement getContainer() {
        return container;
    }
    /**
     * @return the parent
     */
    @Override
    public AttributeModelElement getParent() {
        return parent;
    }

    /**
     * @return the children
     */
    @Override
    public List<AttributeModelElement> getChildren() {
        return children;
    }

    /**
     * Has parent or not.
     * @return true, if has
     */
    @Override
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Has children or not.
     * @return true, if has
     */
    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    /**
     * @return the type
     */
    @Override
    public String getTypeName() {
        return type.name();
    }
    /**
     * @return the level
     */
    @Override
    public int getLevel() {
        return level;
    }
    /**
     * @return the order
     */
    @Override
    public int getOrder() {
        return order;
    }
    @Override
    public boolean isSimple() {
        return type == AttributeType.SIMPLE;
    }

    @Override
    public boolean isCode() {
        return type == AttributeType.CODE;
    }

    @Override
    public boolean isArray() {
        return type == AttributeType.ARRAY;
    }

    @Override
    public boolean isComplex() {
        return type == AttributeType.COMPLEX;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLookupLink() {
        return (isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getLookupEntityType()))
            || (isArray() && StringUtils.isNotBlank(((ArrayAttributeDef) attribute).getLookupEntityType()));
    }

    @Override
    public boolean isLinkTemplate() {
        return isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getLinkDataType());
    }

    @Override
    public boolean isEnumValue() {
        return isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getEnumDataType());
    }

    @Override
    public boolean isMeasured() {
        return isSimple() && ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.MEASURED;
    }

    @Override
    public boolean isBlob() {
        return isSimple() && ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.BLOB;
    }

    @Override
    public boolean isClob() {
        return isSimple() && ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.CLOB;
    }

    @Override
    public boolean isDate() {
        return isSimple() && (
                ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.DATE ||
                ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.TIME ||
                ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.TIMESTAMP
        );
    }

    @Override
    public boolean isUnique() {
        return isCode() || (isSimple() && ((SimpleAttributeDef) attribute).isUnique());
    }

    @Override
    public boolean hasMask() {
        return (isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getMask()))
            || (isCode() && StringUtils.isNotBlank(((CodeAttributeDef) attribute).getMask()))
            || (isArray() && StringUtils.isNotBlank(((ArrayAttributeDef) attribute).getMask()));
    }

    @Override
    public boolean isOfPath(String path) {

        if (StringUtils.length(path) == 0 && level == 0) {
            return true;
        }

        String[] parts = StringUtils.split(path, '.');
        if (level != parts.length) {
            return false;
        }

        for (int i = level - 1; i >= 0; i--) {
            if (!StringUtils.equals(tokens[i], parts[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return the codeAlternative
     */
    @Override
    public boolean isCodeAlternative() {
        return codeAlternative;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMainDisplayable() {

        if (isCode() || isSimple()) {
            return ((AbstractSimpleAttributeDef) attribute).isMainDisplayable();
        } else if (isArray()) {
            return ((ArrayAttributeDef) attribute).isMainDisplayable();
        }

        return false;
    }
    /**
     * @return the SimpleDataType of attribute or null
     */
    private SimpleDataType getSimpleDataType() {

        if (isSimple()) {
            return ((SimpleAttributeDef) attribute).getSimpleDataType();
        } else if (isArray()) {
            return ((ArrayAttributeDef) attribute).getArrayValueType().value();
        } else if (isCode()) {
            return ((CodeAttributeDef) attribute).getSimpleDataType();
        }

        return null;
    }
    /**
     * @return the SimpleDataType of attribute or null
     */
    private SimpleDataType getLookupEntityCodeAttributeType() {

        if (isLookupLink()) {

            if (isSimple()) {
                return ((SimpleAttributeDef) attribute).getLookupEntityCodeAttributeType();
            }

            if (isArray()) {
                return ((ArrayAttributeDef) attribute).getLookupEntityCodeAttributeType().value();
            }
        }

        return null;
    }
    /**
     *
     * @author Mikhail Mikhailov
     * Short top container digest.
     */
    private class ContainerInfoHolder implements ContainerModelElement {
        /**
         * Container name.
         */
        private final String name;
        /**
         * Container's display name.
         */
        private final String displayName;
        /**
         * Constructor.
         * @param name the name
         * @param displayName the display name
         */
        public ContainerInfoHolder(AbstractEntityDef entity) {
            super();
            this.name = entity.getName();
            this.displayName = entity.getDisplayName();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return name;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return displayName;
        }
    }
}
