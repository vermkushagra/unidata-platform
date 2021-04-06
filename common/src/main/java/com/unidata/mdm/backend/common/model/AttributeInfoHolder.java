package com.unidata.mdm.backend.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.AttributeType;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 * Holds attributes information.
 */
public class AttributeInfoHolder {
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
    private final AbstractEntityDef entity;
    /**
     * Entity definition.
     */
    private final RelationDef relation;
    /**
     * Parent link.
     */
    private final AttributeInfoHolder parent;
    /**
     * Children.
     */
    private final List<AttributeInfoHolder> children = new ArrayList<>();
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
     * @param relation the relation
     */
    public AttributeInfoHolder(SimpleAttributeDef attr, AbstractEntityDef entity, AttributeInfoHolder parent, String path, int level) {
        this.attribute = attr;
        this.entity = entity;
        this.relation = null;
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
     * @param relation the relation
     */
    public AttributeInfoHolder(ArrayAttributeDef attr, AbstractEntityDef entity, AttributeInfoHolder parent, String path, int level) {
        this.attribute = attr;
        this.entity = entity;
        this.relation = null;
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
     * @param relation the relation
     */
    public AttributeInfoHolder(CodeAttributeDef attr, AbstractEntityDef entity, AttributeInfoHolder parent, String path, int level, boolean isAlternative) {
        this.attribute = attr;
        this.entity = entity;
        this.relation = null;
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
     * @param relation the relation
     */
    public AttributeInfoHolder(ComplexAttributeDef attr, AbstractEntityDef entity, AttributeInfoHolder parent, String path, int level) {
        this.attribute = attr;
        this.entity = entity;
        this.relation = null;
        this.parent = parent;
        this.path = path;
        this.level = level;
        this.order = -attr.getOrder().intValue();
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
     * Gets lookup link name.
     * @return the name
     */
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
    public List<String> getLookupEntityDisplayAttributes() {
        if (isSimple()) {
            return new ArrayList<>(((SimpleAttributeDef) attribute).getLookupEntityDisplayAttributes());
        } else if (isArray()) {
            return new ArrayList<>(((ArrayAttributeDef) attribute).getLookupEntityDisplayAttributes());
        }

        return Collections.emptyList();
    }

    /**
     * Show attr names for display or not.
     * @return true, if so, false otherwise
     */
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
    public String getEnumName() {
        return isEnumValue() ? ((SimpleAttributeDef) attribute).getEnumDataType() : null;
    }

    /**
     * Gets the template value.
     * @return template value
     */
    public String getLinkTemplate() {
        return isLinkTemplate() ? ((SimpleAttributeDef) attribute).getLinkDataType() : null;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the entity
     */
    public AbstractEntityDef getEntity() {
        return entity;
    }

    /**
     * @return the entity
     */
    public RelationDef getRelation() {
        return relation;
    }


    /**
     * @return the parent
     */
    public AttributeInfoHolder getParent() {
        return parent;
    }


    /**
     * @return the children
     */
    public List<AttributeInfoHolder> getChildren() {
        return children;
    }

    /**
     * Has parent or not.
     * @return true, if has
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Has children or not.
     * @return true, if has
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    /**
     * @return the type
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }
    public boolean isSimple() {
        return type == AttributeType.SIMPLE;
    }

    public boolean isCode() {
        return type == AttributeType.CODE;
    }

    public boolean isArray() {
        return type == AttributeType.ARRAY;
    }

    public boolean isComplex() {
        return type == AttributeType.COMPLEX;
    }

    public boolean isLookupLink() {
        return (isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getLookupEntityType()))
            || (isArray() && StringUtils.isNotBlank(((ArrayAttributeDef) attribute).getLookupEntityType()));
    }

    public boolean isLinkTemplate() {
        return isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getLinkDataType());
    }

    public boolean isEnumValue() {
        return isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getEnumDataType());
    }

    public boolean isMeasured() {
        return isSimple() && ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.MEASURED;
    }

    public boolean isBlob() {
        return isSimple() && ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.BLOB;
    }

    public boolean isClob() {
        return isSimple() && ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.CLOB;
    }

    public boolean isDate() {
        return isSimple() && (
                ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.DATE ||
                ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.TIME ||
                ((SimpleAttributeDef) attribute).getSimpleDataType() == SimpleDataType.TIMESTAMP
        );
    }

    public boolean isUnique() {
        return isCode() || (isSimple() && ((SimpleAttributeDef) attribute).isUnique());
    }

    public boolean hasMask() {
        return (isSimple() && StringUtils.isNotBlank(((SimpleAttributeDef) attribute).getMask()))
            || (isCode() && StringUtils.isNotBlank(((CodeAttributeDef) attribute).getMask()))
            || (isArray() && StringUtils.isNotBlank(((ArrayAttributeDef) attribute).getMask()));
    }

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
    public boolean isCodeAlternative() {
        return codeAlternative;
    }
    /**
     * Naroow a type hold
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractAttributeDef> T narrow() {
        return (T) attribute;
    }
}