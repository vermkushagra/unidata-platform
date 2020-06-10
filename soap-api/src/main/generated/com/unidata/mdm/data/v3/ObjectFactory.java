
package com.unidata.mdm.data.v3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import com.unidata.mdm.api.wsdl.v3.SingleValueAttributeImpl;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.unidata.mdm.data.v3 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EtalonRecord_QNAME = new QName("http://data.mdm.unidata.com/v3/", "etalonRecord");
    private final static QName _OriginRecord_QNAME = new QName("http://data.mdm.unidata.com/v3/", "originRecord");
    private final static QName _RelationTo_QNAME = new QName("http://data.mdm.unidata.com/v3/", "relationTo");
    private final static QName _OriginClassifierRecord_QNAME = new QName("http://data.mdm.unidata.com/v3/", "originClassifierRecord");
    private final static QName _EtalonClassifierRecord_QNAME = new QName("http://data.mdm.unidata.com/v3/", "etalonClassifierRecord");
    private final static QName _AbstractSingleValueAttributeBlobValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "blobValue");
    private final static QName _AbstractSingleValueAttributeClobValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "clobValue");
    private final static QName _AbstractSingleValueAttributeIntValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "intValue");
    private final static QName _AbstractSingleValueAttributeDateValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "dateValue");
    private final static QName _AbstractSingleValueAttributeTimeValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "timeValue");
    private final static QName _AbstractSingleValueAttributeTimestampValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "timestampValue");
    private final static QName _AbstractSingleValueAttributeStringValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "stringValue");
    private final static QName _AbstractSingleValueAttributeNumberValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "numberValue");
    private final static QName _AbstractSingleValueAttributeBoolValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "boolValue");
    private final static QName _AbstractSingleValueAttributeMeasuredValue_QNAME = new QName("http://data.mdm.unidata.com/v3/", "measuredValue");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.unidata.mdm.data.v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EtalonRecord }
     * 
     */
    public EtalonRecord createEtalonRecord() {
        return new EtalonRecord();
    }

    /**
     * Create an instance of {@link OriginRecord }
     * 
     */
    public OriginRecord createOriginRecord() {
        return new OriginRecord();
    }

    /**
     * Create an instance of {@link RelationTo }
     * 
     */
    public RelationTo createRelationTo() {
        return new RelationTo();
    }

    /**
     * Create an instance of {@link OriginClassifierRecord }
     * 
     */
    public OriginClassifierRecord createOriginClassifierRecord() {
        return new OriginClassifierRecord();
    }

    /**
     * Create an instance of {@link EtalonClassifierRecord }
     * 
     */
    public EtalonClassifierRecord createEtalonClassifierRecord() {
        return new EtalonClassifierRecord();
    }

    /**
     * Create an instance of {@link BaseValue }
     * 
     */
    public BaseValue createBaseValue() {
        return new BaseValue();
    }

    /**
     * Create an instance of {@link BlobValue }
     * 
     */
    public BlobValue createBlobValue() {
        return new BlobValue();
    }

    /**
     * Create an instance of {@link ClobValue }
     * 
     */
    public ClobValue createClobValue() {
        return new ClobValue();
    }

    /**
     * Create an instance of {@link MeasuredValue }
     * 
     */
    public MeasuredValue createMeasuredValue() {
        return new MeasuredValue();
    }

    /**
     * Create an instance of {@link AbstractAttribute }
     * 
     */
    public AbstractAttribute createAbstractAttribute() {
        return new AbstractAttribute();
    }

    /**
     * Create an instance of {@link AbstractSingleValueAttribute }
     * 
     */
    public AbstractSingleValueAttribute createAbstractSingleValueAttribute() {
        return new SingleValueAttributeImpl();
    }

    /**
     * Create an instance of {@link SimpleAttribute }
     * 
     */
    public SimpleAttribute createSimpleAttribute() {
        return new SimpleAttribute();
    }

    /**
     * Create an instance of {@link ComplexAttribute }
     * 
     */
    public ComplexAttribute createComplexAttribute() {
        return new ComplexAttribute();
    }

    /**
     * Create an instance of {@link EtalonKey }
     * 
     */
    public EtalonKey createEtalonKey() {
        return new EtalonKey();
    }

    /**
     * Create an instance of {@link OriginKey }
     * 
     */
    public OriginKey createOriginKey() {
        return new OriginKey();
    }

    /**
     * Create an instance of {@link NestedRecord }
     * 
     */
    public NestedRecord createNestedRecord() {
        return new NestedRecord();
    }

    /**
     * Create an instance of {@link RelationBase }
     * 
     */
    public RelationBase createRelationBase() {
        return new RelationBase();
    }

    /**
     * Create an instance of {@link IntegralRecord }
     * 
     */
    public IntegralRecord createIntegralRecord() {
        return new IntegralRecord();
    }

    /**
     * Create an instance of {@link RelationToInfoSection }
     * 
     */
    public RelationToInfoSection createRelationToInfoSection() {
        return new RelationToInfoSection();
    }

    /**
     * Create an instance of {@link EntityRelations }
     * 
     */
    public EntityRelations createEntityRelations() {
        return new EntityRelations();
    }

    /**
     * Create an instance of {@link EtalonRecordInfoSection }
     * 
     */
    public EtalonRecordInfoSection createEtalonRecordInfoSection() {
        return new EtalonRecordInfoSection();
    }

    /**
     * Create an instance of {@link OriginRecordInfoSection }
     * 
     */
    public OriginRecordInfoSection createOriginRecordInfoSection() {
        return new OriginRecordInfoSection();
    }

    /**
     * Create an instance of {@link ClassifierRecord }
     * 
     */
    public ClassifierRecord createClassifierRecord() {
        return new ClassifierRecord();
    }

    /**
     * Create an instance of {@link DataQualityError }
     * 
     */
    public DataQualityError createDataQualityError() {
        return new DataQualityError();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EtalonRecord }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "etalonRecord")
    public JAXBElement<EtalonRecord> createEtalonRecord(EtalonRecord value) {
        return new JAXBElement<EtalonRecord>(_EtalonRecord_QNAME, EtalonRecord.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OriginRecord }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "originRecord")
    public JAXBElement<OriginRecord> createOriginRecord(OriginRecord value) {
        return new JAXBElement<OriginRecord>(_OriginRecord_QNAME, OriginRecord.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelationTo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "relationTo")
    public JAXBElement<RelationTo> createRelationTo(RelationTo value) {
        return new JAXBElement<RelationTo>(_RelationTo_QNAME, RelationTo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OriginClassifierRecord }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "originClassifierRecord")
    public JAXBElement<OriginClassifierRecord> createOriginClassifierRecord(OriginClassifierRecord value) {
        return new JAXBElement<OriginClassifierRecord>(_OriginClassifierRecord_QNAME, OriginClassifierRecord.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EtalonClassifierRecord }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "etalonClassifierRecord")
    public JAXBElement<EtalonClassifierRecord> createEtalonClassifierRecord(EtalonClassifierRecord value) {
        return new JAXBElement<EtalonClassifierRecord>(_EtalonClassifierRecord_QNAME, EtalonClassifierRecord.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BlobValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "blobValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<BlobValue> createAbstractSingleValueAttributeBlobValue(BlobValue value) {
        return new JAXBElement<BlobValue>(_AbstractSingleValueAttributeBlobValue_QNAME, BlobValue.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClobValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "clobValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<ClobValue> createAbstractSingleValueAttributeClobValue(ClobValue value) {
        return new JAXBElement<ClobValue>(_AbstractSingleValueAttributeClobValue_QNAME, ClobValue.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "intValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<Long> createAbstractSingleValueAttributeIntValue(Long value) {
        return new JAXBElement<Long>(_AbstractSingleValueAttributeIntValue_QNAME, Long.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "dateValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<XMLGregorianCalendar> createAbstractSingleValueAttributeDateValue(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_AbstractSingleValueAttributeDateValue_QNAME, XMLGregorianCalendar.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "timeValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<XMLGregorianCalendar> createAbstractSingleValueAttributeTimeValue(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_AbstractSingleValueAttributeTimeValue_QNAME, XMLGregorianCalendar.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "timestampValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<XMLGregorianCalendar> createAbstractSingleValueAttributeTimestampValue(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_AbstractSingleValueAttributeTimestampValue_QNAME, XMLGregorianCalendar.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "stringValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<String> createAbstractSingleValueAttributeStringValue(String value) {
        return new JAXBElement<String>(_AbstractSingleValueAttributeStringValue_QNAME, String.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "numberValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<Double> createAbstractSingleValueAttributeNumberValue(Double value) {
        return new JAXBElement<Double>(_AbstractSingleValueAttributeNumberValue_QNAME, Double.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "boolValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<Boolean> createAbstractSingleValueAttributeBoolValue(Boolean value) {
        return new JAXBElement<Boolean>(_AbstractSingleValueAttributeBoolValue_QNAME, Boolean.class, AbstractSingleValueAttribute.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasuredValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.mdm.unidata.com/v3/", name = "measuredValue", scope = AbstractSingleValueAttribute.class)
    public JAXBElement<MeasuredValue> createAbstractSingleValueAttributeMeasuredValue(MeasuredValue value) {
        return new JAXBElement<MeasuredValue>(_AbstractSingleValueAttributeMeasuredValue_QNAME, MeasuredValue.class, AbstractSingleValueAttribute.class, value);
    }

}
