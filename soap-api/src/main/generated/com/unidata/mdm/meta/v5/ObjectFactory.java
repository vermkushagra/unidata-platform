
package com.unidata.mdm.meta.v5;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.unidata.mdm.meta.v5 package. 
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

    private final static QName _GetModelRequestVersion_QNAME = new QName("http://meta.mdm.unidata.com/v5/", "version");
    private final static QName _GetModelRequestName_QNAME = new QName("http://meta.mdm.unidata.com/v5/", "name");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.unidata.mdm.meta.v5
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CompositeCleanseFunctionLogic }
     * 
     */
    public CompositeCleanseFunctionLogic createCompositeCleanseFunctionLogic() {
        return new CompositeCleanseFunctionLogic();
    }

    /**
     * Create an instance of {@link CompositeCleanseFunctionLogic.Links }
     * 
     */
    public CompositeCleanseFunctionLogic.Links createCompositeCleanseFunctionLogicLinks() {
        return new CompositeCleanseFunctionLogic.Links();
    }

    /**
     * Create an instance of {@link CompositeCleanseFunctionLogic.Nodes }
     * 
     */
    public CompositeCleanseFunctionLogic.Nodes createCompositeCleanseFunctionLogicNodes() {
        return new CompositeCleanseFunctionLogic.Nodes();
    }

    /**
     * Create an instance of {@link GetModelRequest }
     * 
     */
    public GetModelRequest createGetModelRequest() {
        return new GetModelRequest();
    }

    /**
     * Create an instance of {@link GetModelResponse }
     * 
     */
    public GetModelResponse createGetModelResponse() {
        return new GetModelResponse();
    }

    /**
     * Create an instance of {@link Model }
     * 
     */
    public Model createModel() {
        return new Model();
    }

    /**
     * Create an instance of {@link ListOfCleanseFunctions }
     * 
     */
    public ListOfCleanseFunctions createListOfCleanseFunctions() {
        return new ListOfCleanseFunctions();
    }

    /**
     * Create an instance of {@link ListOfSourceSystems }
     * 
     */
    public ListOfSourceSystems createListOfSourceSystems() {
        return new ListOfSourceSystems();
    }

    /**
     * Create an instance of {@link ListOfEnumerations }
     * 
     */
    public ListOfEnumerations createListOfEnumerations() {
        return new ListOfEnumerations();
    }

    /**
     * Create an instance of {@link ListOfLookupEntities }
     * 
     */
    public ListOfLookupEntities createListOfLookupEntities() {
        return new ListOfLookupEntities();
    }

    /**
     * Create an instance of {@link MeasurementValues }
     * 
     */
    public MeasurementValues createMeasurementValues() {
        return new MeasurementValues();
    }

    /**
     * Create an instance of {@link ListOfNestedEntities }
     * 
     */
    public ListOfNestedEntities createListOfNestedEntities() {
        return new ListOfNestedEntities();
    }

    /**
     * Create an instance of {@link ListOfEntities }
     * 
     */
    public ListOfEntities createListOfEntities() {
        return new ListOfEntities();
    }

    /**
     * Create an instance of {@link ListOfRelations }
     * 
     */
    public ListOfRelations createListOfRelations() {
        return new ListOfRelations();
    }

    /**
     * Create an instance of {@link EntitiesGroupDef }
     * 
     */
    public EntitiesGroupDef createEntitiesGroupDef() {
        return new EntitiesGroupDef();
    }

    /**
     * Create an instance of {@link ListOfDefaultClassifier }
     * 
     */
    public ListOfDefaultClassifier createListOfDefaultClassifier() {
        return new ListOfDefaultClassifier();
    }

    /**
     * Create an instance of {@link MetaHeader }
     * 
     */
    public MetaHeader createMetaHeader() {
        return new MetaHeader();
    }

    /**
     * Create an instance of {@link GetElementRequest }
     * 
     */
    public GetElementRequest createGetElementRequest() {
        return new GetElementRequest();
    }

    /**
     * Create an instance of {@link GetElementResponse }
     * 
     */
    public GetElementResponse createGetElementResponse() {
        return new GetElementResponse();
    }

    /**
     * Create an instance of {@link LookupEntityDef }
     * 
     */
    public LookupEntityDef createLookupEntityDef() {
        return new LookupEntityDef();
    }

    /**
     * Create an instance of {@link EntityDef }
     * 
     */
    public EntityDef createEntityDef() {
        return new EntityDef();
    }

    /**
     * Create an instance of {@link NestedEntityDef }
     * 
     */
    public NestedEntityDef createNestedEntityDef() {
        return new NestedEntityDef();
    }

    /**
     * Create an instance of {@link RelationDef }
     * 
     */
    public RelationDef createRelationDef() {
        return new RelationDef();
    }

    /**
     * Create an instance of {@link GetEntityWithDepsRequest }
     * 
     */
    public GetEntityWithDepsRequest createGetEntityWithDepsRequest() {
        return new GetEntityWithDepsRequest();
    }

    /**
     * Create an instance of {@link GetEntityWithDepsResponse }
     * 
     */
    public GetEntityWithDepsResponse createGetEntityWithDepsResponse() {
        return new GetEntityWithDepsResponse();
    }

    /**
     * Create an instance of {@link UpsertElementRequest }
     * 
     */
    public UpsertElementRequest createUpsertElementRequest() {
        return new UpsertElementRequest();
    }

    /**
     * Create an instance of {@link EnumerationDataType }
     * 
     */
    public EnumerationDataType createEnumerationDataType() {
        return new EnumerationDataType();
    }

    /**
     * Create an instance of {@link UpsertElementResponse }
     * 
     */
    public UpsertElementResponse createUpsertElementResponse() {
        return new UpsertElementResponse();
    }

    /**
     * Create an instance of {@link DeleteElementRequest }
     * 
     */
    public DeleteElementRequest createDeleteElementRequest() {
        return new DeleteElementRequest();
    }

    /**
     * Create an instance of {@link DeleteElementResponse }
     * 
     */
    public DeleteElementResponse createDeleteElementResponse() {
        return new DeleteElementResponse();
    }

    /**
     * Create an instance of {@link ApplyDraftRequest }
     * 
     */
    public ApplyDraftRequest createApplyDraftRequest() {
        return new ApplyDraftRequest();
    }

    /**
     * Create an instance of {@link ApplyDraftResponse }
     * 
     */
    public ApplyDraftResponse createApplyDraftResponse() {
        return new ApplyDraftResponse();
    }

    /**
     * Create an instance of {@link ConstantValueDef }
     * 
     */
    public ConstantValueDef createConstantValueDef() {
        return new ConstantValueDef();
    }

    /**
     * Create an instance of {@link EnumerationValue }
     * 
     */
    public EnumerationValue createEnumerationValue() {
        return new EnumerationValue();
    }

    /**
     * Create an instance of {@link MeasurementValueDef }
     * 
     */
    public MeasurementValueDef createMeasurementValueDef() {
        return new MeasurementValueDef();
    }

    /**
     * Create an instance of {@link MeasurementUnitDef }
     * 
     */
    public MeasurementUnitDef createMeasurementUnitDef() {
        return new MeasurementUnitDef();
    }

    /**
     * Create an instance of {@link AttributeMeasurementSettingsDef }
     * 
     */
    public AttributeMeasurementSettingsDef createAttributeMeasurementSettingsDef() {
        return new AttributeMeasurementSettingsDef();
    }

    /**
     * Create an instance of {@link AbstractAttributeDef }
     * 
     */
    public AbstractAttributeDef createAbstractAttributeDef() {
        return new AbstractAttributeDef();
    }

    /**
     * Create an instance of {@link ArrayAttributeDef }
     * 
     */
    public ArrayAttributeDef createArrayAttributeDef() {
        return new ArrayAttributeDef();
    }

    /**
     * Create an instance of {@link AbstractSimpleAttributeDef }
     * 
     */
    public AbstractSimpleAttributeDef createAbstractSimpleAttributeDef() {
        return new AbstractSimpleAttributeDef();
    }

    /**
     * Create an instance of {@link SimpleAttributeDef }
     * 
     */
    public SimpleAttributeDef createSimpleAttributeDef() {
        return new SimpleAttributeDef();
    }

    /**
     * Create an instance of {@link AttributeGroupDef }
     * 
     */
    public AttributeGroupDef createAttributeGroupDef() {
        return new AttributeGroupDef();
    }

    /**
     * Create an instance of {@link RelationGroupDef }
     * 
     */
    public RelationGroupDef createRelationGroupDef() {
        return new RelationGroupDef();
    }

    /**
     * Create an instance of {@link ClassifierGroupDef }
     * 
     */
    public ClassifierGroupDef createClassifierGroupDef() {
        return new ClassifierGroupDef();
    }

    /**
     * Create an instance of {@link CodeAttributeDef }
     * 
     */
    public CodeAttributeDef createCodeAttributeDef() {
        return new CodeAttributeDef();
    }

    /**
     * Create an instance of {@link ComplexAttributeDef }
     * 
     */
    public ComplexAttributeDef createComplexAttributeDef() {
        return new ComplexAttributeDef();
    }

    /**
     * Create an instance of {@link VersionedObjectDef }
     * 
     */
    public VersionedObjectDef createVersionedObjectDef() {
        return new VersionedObjectDef();
    }

    /**
     * Create an instance of {@link AbstractEntityDef }
     * 
     */
    public AbstractEntityDef createAbstractEntityDef() {
        return new AbstractEntityDef();
    }

    /**
     * Create an instance of {@link ClassifierPropertyDef }
     * 
     */
    public ClassifierPropertyDef createClassifierPropertyDef() {
        return new ClassifierPropertyDef();
    }

    /**
     * Create an instance of {@link CustomPropertyDef }
     * 
     */
    public CustomPropertyDef createCustomPropertyDef() {
        return new CustomPropertyDef();
    }

    /**
     * Create an instance of {@link SimpleAttributesHolderEntityDef }
     * 
     */
    public SimpleAttributesHolderEntityDef createSimpleAttributesHolderEntityDef() {
        return new SimpleAttributesHolderEntityDef();
    }

    /**
     * Create an instance of {@link ComplexAttributesHolderEntityDef }
     * 
     */
    public ComplexAttributesHolderEntityDef createComplexAttributesHolderEntityDef() {
        return new ComplexAttributesHolderEntityDef();
    }

    /**
     * Create an instance of {@link PeriodBoundaryDef }
     * 
     */
    public PeriodBoundaryDef createPeriodBoundaryDef() {
        return new PeriodBoundaryDef();
    }

    /**
     * Create an instance of {@link DQRMappingDef }
     * 
     */
    public DQRMappingDef createDQRMappingDef() {
        return new DQRMappingDef();
    }

    /**
     * Create an instance of {@link DQREnrichDef }
     * 
     */
    public DQREnrichDef createDQREnrichDef() {
        return new DQREnrichDef();
    }

    /**
     * Create an instance of {@link DQRRaiseDef }
     * 
     */
    public DQRRaiseDef createDQRRaiseDef() {
        return new DQRRaiseDef();
    }

    /**
     * Create an instance of {@link DQRSourceSystemRef }
     * 
     */
    public DQRSourceSystemRef createDQRSourceSystemRef() {
        return new DQRSourceSystemRef();
    }

    /**
     * Create an instance of {@link DQROriginsDef }
     * 
     */
    public DQROriginsDef createDQROriginsDef() {
        return new DQROriginsDef();
    }

    /**
     * Create an instance of {@link DQRuleDef }
     * 
     */
    public DQRuleDef createDQRuleDef() {
        return new DQRuleDef();
    }

    /**
     * Create an instance of {@link EntityDataQualityDef }
     * 
     */
    public EntityDataQualityDef createEntityDataQualityDef() {
        return new EntityDataQualityDef();
    }

    /**
     * Create an instance of {@link AbstractMergeTypeDef }
     * 
     */
    public AbstractMergeTypeDef createAbstractMergeTypeDef() {
        return new AbstractMergeTypeDef();
    }

    /**
     * Create an instance of {@link BVRMergeTypeDef }
     * 
     */
    public BVRMergeTypeDef createBVRMergeTypeDef() {
        return new BVRMergeTypeDef();
    }

    /**
     * Create an instance of {@link MergeAttributeDef }
     * 
     */
    public MergeAttributeDef createMergeAttributeDef() {
        return new MergeAttributeDef();
    }

    /**
     * Create an instance of {@link BVTMergeTypeDef }
     * 
     */
    public BVTMergeTypeDef createBVTMergeTypeDef() {
        return new BVTMergeTypeDef();
    }

    /**
     * Create an instance of {@link MergeSettingsDef }
     * 
     */
    public MergeSettingsDef createMergeSettingsDef() {
        return new MergeSettingsDef();
    }

    /**
     * Create an instance of {@link Port }
     * 
     */
    public Port createPort() {
        return new Port();
    }

    /**
     * Create an instance of {@link CleansePortList }
     * 
     */
    public CleansePortList createCleansePortList() {
        return new CleansePortList();
    }

    /**
     * Create an instance of {@link CleanseFunctionConstant }
     * 
     */
    public CleanseFunctionConstant createCleanseFunctionConstant() {
        return new CleanseFunctionConstant();
    }

    /**
     * Create an instance of {@link CleanseFunctionDef }
     * 
     */
    public CleanseFunctionDef createCleanseFunctionDef() {
        return new CleanseFunctionDef();
    }

    /**
     * Create an instance of {@link CleanseFunctionExtendedDef }
     * 
     */
    public CleanseFunctionExtendedDef createCleanseFunctionExtendedDef() {
        return new CleanseFunctionExtendedDef();
    }

    /**
     * Create an instance of {@link CompositeCleanseFunctionDef }
     * 
     */
    public CompositeCleanseFunctionDef createCompositeCleanseFunctionDef() {
        return new CompositeCleanseFunctionDef();
    }

    /**
     * Create an instance of {@link CleanseFunctionGroupDef }
     * 
     */
    public CleanseFunctionGroupDef createCleanseFunctionGroupDef() {
        return new CleanseFunctionGroupDef();
    }

    /**
     * Create an instance of {@link SourceSystemDef }
     * 
     */
    public SourceSystemDef createSourceSystemDef() {
        return new SourceSystemDef();
    }

    /**
     * Create an instance of {@link DefaultClassifier }
     * 
     */
    public DefaultClassifier createDefaultClassifier() {
        return new DefaultClassifier();
    }

    /**
     * Create an instance of {@link MatchingSettings }
     * 
     */
    public MatchingSettings createMatchingSettings() {
        return new MatchingSettings();
    }

    /**
     * Create an instance of {@link CompositeCleanseFunctionLogic.Links.NodeLink }
     * 
     */
    public CompositeCleanseFunctionLogic.Links.NodeLink createCompositeCleanseFunctionLogicLinksNodeLink() {
        return new CompositeCleanseFunctionLogic.Links.NodeLink();
    }

    /**
     * Create an instance of {@link CompositeCleanseFunctionLogic.Nodes.Node }
     * 
     */
    public CompositeCleanseFunctionLogic.Nodes.Node createCompositeCleanseFunctionLogicNodesNode() {
        return new CompositeCleanseFunctionLogic.Nodes.Node();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meta.mdm.unidata.com/v5/", name = "version", scope = GetModelRequest.class)
    public JAXBElement<String> createGetModelRequestVersion(String value) {
        return new JAXBElement<String>(_GetModelRequestVersion_QNAME, String.class, GetModelRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meta.mdm.unidata.com/v5/", name = "name", scope = GetModelRequest.class)
    public JAXBElement<String> createGetModelRequestName(String value) {
        return new JAXBElement<String>(_GetModelRequestName_QNAME, String.class, GetModelRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meta.mdm.unidata.com/v5/", name = "name", scope = MetaHeader.class)
    public JAXBElement<String> createMetaHeaderName(String value) {
        return new JAXBElement<String>(_GetModelRequestName_QNAME, String.class, MetaHeader.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meta.mdm.unidata.com/v5/", name = "version", scope = MetaHeader.class)
    public JAXBElement<String> createMetaHeaderVersion(String value) {
        return new JAXBElement<String>(_GetModelRequestVersion_QNAME, String.class, MetaHeader.class, value);
    }

}
