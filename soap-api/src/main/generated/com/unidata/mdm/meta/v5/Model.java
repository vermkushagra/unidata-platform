
package com.unidata.mdm.meta.v5;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cleanseFunctions" type="{http://meta.mdm.unidata.com/v5/}ListOfCleanseFunctions"/&gt;
 *         &lt;element name="sourceSystems" type="{http://meta.mdm.unidata.com/v5/}ListOfSourceSystems"/&gt;
 *         &lt;element name="enumerations" type="{http://meta.mdm.unidata.com/v5/}ListOfEnumerations"/&gt;
 *         &lt;element name="lookupEntities" type="{http://meta.mdm.unidata.com/v5/}ListOfLookupEntities"/&gt;
 *         &lt;element name="measurementValues" type="{http://meta.mdm.unidata.com/v5/}MeasurementValues"/&gt;
 *         &lt;element name="nestedEntities" type="{http://meta.mdm.unidata.com/v5/}ListOfNestedEntities"/&gt;
 *         &lt;element name="entities" type="{http://meta.mdm.unidata.com/v5/}ListOfEntities"/&gt;
 *         &lt;element name="relations" type="{http://meta.mdm.unidata.com/v5/}ListOfRelations"/&gt;
 *         &lt;element name="entitiesGroup" type="{http://meta.mdm.unidata.com/v5/}EntitiesGroupDef"/&gt;
 *         &lt;element name="defaultClassifiers" type="{http://meta.mdm.unidata.com/v5/}ListOfDefaultClassifier" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="storageId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cleanseFunctions",
    "sourceSystems",
    "enumerations",
    "lookupEntities",
    "measurementValues",
    "nestedEntities",
    "entities",
    "relations",
    "entitiesGroup",
    "defaultClassifiers"
})
@XmlRootElement(name = "model")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class Model
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfCleanseFunctions cleanseFunctions;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfSourceSystems sourceSystems;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfEnumerations enumerations;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfLookupEntities lookupEntities;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected MeasurementValues measurementValues;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfNestedEntities nestedEntities;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfEntities entities;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfRelations relations;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected EntitiesGroupDef entitiesGroup;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfDefaultClassifier defaultClassifiers;
    @XmlAttribute(name = "storageId", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String storageId;

    /**
     * Gets the value of the cleanseFunctions property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfCleanseFunctions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfCleanseFunctions getCleanseFunctions() {
        return cleanseFunctions;
    }

    /**
     * Sets the value of the cleanseFunctions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfCleanseFunctions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setCleanseFunctions(ListOfCleanseFunctions value) {
        this.cleanseFunctions = value;
    }

    /**
     * Gets the value of the sourceSystems property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfSourceSystems }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfSourceSystems getSourceSystems() {
        return sourceSystems;
    }

    /**
     * Sets the value of the sourceSystems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfSourceSystems }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setSourceSystems(ListOfSourceSystems value) {
        this.sourceSystems = value;
    }

    /**
     * Gets the value of the enumerations property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfEnumerations }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfEnumerations getEnumerations() {
        return enumerations;
    }

    /**
     * Sets the value of the enumerations property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfEnumerations }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setEnumerations(ListOfEnumerations value) {
        this.enumerations = value;
    }

    /**
     * Gets the value of the lookupEntities property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfLookupEntities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfLookupEntities getLookupEntities() {
        return lookupEntities;
    }

    /**
     * Sets the value of the lookupEntities property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfLookupEntities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setLookupEntities(ListOfLookupEntities value) {
        this.lookupEntities = value;
    }

    /**
     * Gets the value of the measurementValues property.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public MeasurementValues getMeasurementValues() {
        return measurementValues;
    }

    /**
     * Sets the value of the measurementValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setMeasurementValues(MeasurementValues value) {
        this.measurementValues = value;
    }

    /**
     * Gets the value of the nestedEntities property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfNestedEntities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfNestedEntities getNestedEntities() {
        return nestedEntities;
    }

    /**
     * Sets the value of the nestedEntities property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfNestedEntities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setNestedEntities(ListOfNestedEntities value) {
        this.nestedEntities = value;
    }

    /**
     * Gets the value of the entities property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfEntities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfEntities getEntities() {
        return entities;
    }

    /**
     * Sets the value of the entities property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfEntities }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setEntities(ListOfEntities value) {
        this.entities = value;
    }

    /**
     * Gets the value of the relations property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfRelations }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfRelations getRelations() {
        return relations;
    }

    /**
     * Sets the value of the relations property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfRelations }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setRelations(ListOfRelations value) {
        this.relations = value;
    }

    /**
     * Gets the value of the entitiesGroup property.
     * 
     * @return
     *     possible object is
     *     {@link EntitiesGroupDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public EntitiesGroupDef getEntitiesGroup() {
        return entitiesGroup;
    }

    /**
     * Sets the value of the entitiesGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntitiesGroupDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setEntitiesGroup(EntitiesGroupDef value) {
        this.entitiesGroup = value;
    }

    /**
     * Gets the value of the defaultClassifiers property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfDefaultClassifier }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfDefaultClassifier getDefaultClassifiers() {
        return defaultClassifiers;
    }

    /**
     * Sets the value of the defaultClassifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfDefaultClassifier }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setDefaultClassifiers(ListOfDefaultClassifier value) {
        this.defaultClassifiers = value;
    }

    /**
     * Gets the value of the storageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String getStorageId() {
        return storageId;
    }

    /**
     * Sets the value of the storageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setStorageId(String value) {
        this.storageId = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withCleanseFunctions(ListOfCleanseFunctions value) {
        setCleanseFunctions(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withSourceSystems(ListOfSourceSystems value) {
        setSourceSystems(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withEnumerations(ListOfEnumerations value) {
        setEnumerations(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withLookupEntities(ListOfLookupEntities value) {
        setLookupEntities(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withMeasurementValues(MeasurementValues value) {
        setMeasurementValues(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withNestedEntities(ListOfNestedEntities value) {
        setNestedEntities(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withEntities(ListOfEntities value) {
        setEntities(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withRelations(ListOfRelations value) {
        setRelations(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withEntitiesGroup(EntitiesGroupDef value) {
        setEntitiesGroup(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withDefaultClassifiers(ListOfDefaultClassifier value) {
        setDefaultClassifiers(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public Model withStorageId(String value) {
        setStorageId(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
