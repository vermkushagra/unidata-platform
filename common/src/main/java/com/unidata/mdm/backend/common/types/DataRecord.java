package com.unidata.mdm.backend.common.types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Data record, which may contain other data records as part of complex attributes.
 */
public interface DataRecord {

    /**
     * Gets all attribute names on this level.
     * E. g. it doesn't include attribute names from nested record of complex attributes.
     * @return unmodifiable view of all attribute names
     */
    Collection<String> getAttributeNames();
    /**
     * Gets all attributes on the first level regardless of their type.
     * @return unmodifiable view of all attributes.
     */
    Collection<Attribute> getAllAttributes();
    /**
     * Gets all attributes regardless of their type recursively.
     * @return unmodifiable view of all attributes.
     */
    Collection<Attribute> getAllAttributesRecursive();
    /**
     * Gets attribute by name.
     * @param name the name of the attribute
     * @return attribute or null, if not found
     */
    Attribute getAttribute(String name);
    /**
     * Gets attributes by name recursive.
     * @param path the name of the attribute (path, may be with '.')
     * @return attribute or null, if not found
     */
    Collection<Attribute> getAttributeRecursive(String path);
    /**
     * Gets RW attribute iterator.
     * @return iterator
     */
    AttributeIterator attributeIterator();
    /**
     * Gets all simple attributes.
     * @return read-only list of simple attributes
     */
    Collection<SimpleAttribute<?>> getSimpleAttributes();
    /**
     * Gets all simple attributes ordered as defined by meta model.
     * @return read-only list of simple attributes
     */
    List<SimpleAttribute<?>> getSimpleAttributesOrdered();
    /**
     * Gets simple attribute by name.
     * @param name the name of the attribute
     * @return simple attribute or null
     */
    SimpleAttribute<?> getSimpleAttribute(String name);
    /**
     * Gets simple attribute recursive by name.
     * @param name the name of the attribute
     * @return simple attributes or empty collection
     */
    Collection<SimpleAttribute<?>> getSimpleAttributeRecursive(String name);
    /**
     * Gets all code attributes.
     * @return read-only list of code attributes
     */
    Collection<CodeAttribute<?>> getCodeAttributes();
    /**
     * Gets all code attributes ordered as defined by meta model.
     * @return read-only list of code attributes
     */
    List<CodeAttribute<?>> getCodeAttributesOrdered();
    /**
     * Gets code attribute by name.
     * @param name the name of the attribute
     * @return code attribute or null
     */
    CodeAttribute<?> getCodeAttribute(String name);
    /**
     * Gets all array attributes.
     * @return read-only list of array attributes
     */
    Collection<ArrayAttribute<?>> getArrayAttributes();
    /**
     * Gets all array attributes ordered as defined by meta model.
     * @return read-only list of array attributes
     */
    List<ArrayAttribute<?>> getArrayAttributesOrdered();
    /**
     * Gets array attribute by name.
     * @param name the name of the attribute
     * @return array attribute or null
     */
    ArrayAttribute<?> getArrayAttribute(String name);
    /**
     * Gets array attribute recursive by name.
     * @param name the name of the attribute
     * @return simple attributes or empty collection
     */
    Collection<ArrayAttribute<?>> getArrayAttributeRecursive(String name);
    /**
     * Gets all complex attributes.
     * @return read-only list of complex attributes
     */
    Collection<ComplexAttribute> getComplexAttributes();
    /**
     * Gets all complex attributes ordered as defined by meta model.
     * @return read-only list of complex attributes
     */
    List<ComplexAttribute> getComplexAttributesOrdered();
    /**
     * Gets a complex attribute by name.
     * @param name the name
     * @return complex attribute or null
     */
    ComplexAttribute getComplexAttribute(String name);
    /**
     * Gets a complex attribute recursive by name.
     * @param name the name
     * @return complex attributes or empty collection
     */
    Collection<ComplexAttribute> getComplexAttributeRecursive(String name);
    /**
     * Adds an attribute.
     * @param attribute the attribute to add
     */
    void addAttribute(Attribute attribute);
    /**
     * Adds several attributes at once.
     * @param attributes attributes to add
     */
    void addAll(Collection<? extends Attribute> attributes);
    /**
     * Adds attribute
     * @param path the path
     * @param attribute the attribute
     */
    void addAttributeRecursive(String path, Attribute attribute);
    /**
     * Adds a map of attributes.
     * @param attributes the attributes to add
     */
    public void addAllRecursive(Map<String, Attribute> attributes);
    /**
     * Puts a {@link String} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    String putAttribute(String name, String value);
    /**
     * Puts a {@link Long} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    Long putAttribute(String name, Long value);
    /**
     * Puts a {@link Double} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    Double putAttribute(String name, Double value);
    /**
     * Puts a {@link Boolean} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    Boolean putAttribute(String name, Boolean value);
    /**
     * Puts a {@link LocalDate} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    LocalDate putAttribute(String name, LocalDate value);
    /**
     * Puts a {@link LocalTime} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    LocalTime putAttribute(String name, LocalTime value);
    /**
     * Puts a {@link LocalDateTime} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    LocalDateTime putAttribute(String name, LocalDateTime value);
    /**
     * Puts a {@link BinaryLargeValue} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    BinaryLargeValue putAttribute(String name, BinaryLargeValue value);
    /**
     * Puts a {@link CharacterLargeValue} attribute.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return previous value if the key was already mapped
     */
    CharacterLargeValue putAttribute(String name, CharacterLargeValue value);
    /**
     * Checks presence of an attribute.
     * @param name the attribute's name.
     * @return true, if mapped, false otherwise
     */
    boolean containsAttribute(String name);
    /**
     * Removes attribute, if it can be found.
     * @param attribute the attribute.
     * @return the value, if it was mapped, null otherwise
     */
    Attribute removeAttribute(String name);
    /**
     * Removes attributes recursive by given path.
     * @param path the path
     * @return removed attributes
     */
    Collection<Attribute> removeAttributeRecursive(String path);
    /**
     * Gets the number of all attributes in a data record.
     * @return size
     */
    int getSize();
}
