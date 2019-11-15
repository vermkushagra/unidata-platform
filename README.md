# UniData Platform

UniData is a multipurpose data processing platform, especially targeting MDM. 
Its open source part builds a foundation for various platform services. 
Due to modular nature of the platform it allows wide variety of customizations (for instance, deployment sets can be variadic according to customer needs or different sets of operations on incoming data can be configured). 
It is also scalable, allowing you to run it on several machines (application nodes) in a cluster.

We use 
- PostgreSQL for persisting data (11.x and above) 
- Hazelcast (3.12) for distributed computations and caching support
- Spring (5.2.x) for managing the application
- Apache Tomcat (7.x) for running the application 
- Apache Camel (2.23) for integration with customer messaging systems
- Apache POI (3.17) for generation of OOXML output
- Elasticsearch (5.6.x) for indexing and searching
- Many other open source libraries

It is written in Java and requires at least Java 1.8 to compile.

## Current state

Currently, our open source part consists of the following modules:

1. __org.unidata.mdm.system__ - contains base types and interfaces 
    - ModuleService - the module metadata and discovery service 
    - PipelineService - the service, managing execution pipelines 
    - ExecutionService - pipeline execution and processing service 
    - RuntimePropertiesService - runtime  properties, which can be read and modyfied cluster wide
2. __org.unidata.mdm.core__ - contains security and utility services 
    - SecurityService - login, logout, token (in)validation support service 
    - EventService - a service for cluster wide event sending and subscription 
    - AuditService - audit service 
    - UPathService - a simple "UPath notation on DataRecord" interpreter
3. __org.unidata.mdm.search__ - contains search services and interfaces
    - SearchService - search services facade with access to mapping, indexing and search
4. __org.unidata.mdm.meta__ - contains meta model creation, modification, query and management services
    - MetaModelService - a service responsible for persisting and query of meta model entities
    - MetaDraftService - a service, allowing one to save a metamodel draft and to apply it to current metamodel after a number of modifications
5. __org.unidata.mdm.data__ - contains services around data and relations between data records
    - DataRecordService - data management facade service
    - DataStorageService - storage metadata management service
    - RecordValidationService - checks a DataRecord for validity
6. __org.unidata.mdm.soap.core__ - SOAP interface to core services
7. __org.unidata.mdm.soap.meta__ - SOAP interface to metamodel services
8. __org.unidata.mdm.soap.data__ - SOAP interface to data services

## Notes
### Modules 

UniData runs on a simple module system. New functionality can be added to the platform by implementing a new module. A module is a piece of code, packaged into a jar file and implementing the interface __org.unidata.mdm.system.type.module.Module__. Also, a module has to have a special entry in its __MANIFEST.MF__ file, denoting the implementing class, like the following: 

`Unidata-Module-Class: org.unidata.mdm.data.module.DataModule` 

The interface __org.unidata.mdm.system.type.module.Module__ has several mandatory methods for implementation.

```java
/**
 * Gets module ID, i. e. 'org.unidata.mdm.core'.
 * @return ID
 */ 
String getId();
/**
 * Gets module version, consisting of major.minor.rev, i. e. '5.4.3'.
 * @return version
 */
String getVersion();
/**
 * Gets module localized name, 'Unidata Core'.
 * @return name
 */
String getName();
/**
 * Gets module localized description, i. e. 'This outstanding module is for all the good things on earth...'.
 * @return description
 */
String getDescription();`
```

If the module has dependencies to other modules, it has to implement also the method

```java
/**
 * Returns the dependencies of this module.
 */
Collection<Dependency> getDependencies()
```

Module service will check and load dependencies prior to start of the module.

Special note about the module ID. Module ID must match the root package of the module, if the module uses Spring and injects beans from other modules. The reason is simple - root package of the module will be scanned by Spring to discover classes, annotated with Spring stereotypes. The class, implementing __org.unidata.mdm.system.type.module.Module__ must not be itself a bean, although __@Autowire__ or JSR330 __@Inject__ can be used inside the implementing class.

Regardless of how module interface is implemented - using Spring or not - the @ModuleRef annotation can be used to inject module instance at place of interest, like this:

```java
@ModuleRef
private DataModule dataModule;
// or
@ModuleRef("org.unidata.mdm.data")
private Module dataModule;
```

There are also several other important methods, one would probably want to implement:

```java
/**
 * Runs module's install/upgrade procedure.
 * Can be used to init / mgirate DB schema or other similar tasks. 
 */
default void install() {
    // Override
}
/**
 * Runs module's uninstall procedure.
 * Can be used to drop schema or similar tasks.
 */
default void uninstall() {
    // Override
}
/**
 * Runs module's start procedure. 
 * Happens upon each application startup.
 * Should be used for initialization.
 */
default void start() {
    // Override
}
/**
 * Runs module's stop procedure. 
 * Happens upon each application shutdown.
 * Should be used for cleanup. 
 */
default void stop() {
    // Override
}
```

Common practice for modules writing is
- to use separate DB schema per module, if the module uses database
- to have separate i18n resources
- to have and use own ExceptionIds

### Pipelines

Execution pipelines is a way to configure and run series of operations on a request context object, which inherits from __org.unidata.mdm.system.context.PipelineExecutionContext__. Such a member operation is called a "segment" and can be (at the time of writing) of type Start, Point, Connector and Finish. A properly configured Pipeline must have a Start segment, can have any number of Point and Connector segments, and must be closed with a Finish segment. 

Pipelines can be configured programmaticaly and then either used for direct calls or persisted for caching and future use. Below is an example of such a configuration and subsequent call for saving data:

```java

    Pipeline p = Pipeline.start(pipelineService.start(RecordUpsertStartExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertValidateExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertSecurityExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertPeriodCheckExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertResolveCodePointersExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertMeasuredAttributesExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertModboxExecutor.SEGMENT_ID)) // <- Modbox create
            .with(pipelineService.point(RecordUpsertLobSubmitExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertMergeTimelineExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertIndexingExecutor.SEGMENT_ID))
            .with(pipelineService.point(RecordUpsertPersistenceExecutor.SEGMENT_ID))
            .end(pipelineService.finish(RecordUpsertFinishExecutor.SEGMENT_ID));

    UpsertRecordDTO result = executionService.execute(p, ctx);
```

Pipelines can be saved using the __org.unidata.mdm.system.service.PipelineService.savePipeline(...)__ and then retrieved using the __org.unidata.mdm.system.service.PipelineService.getPipeline(...)__. Pipelines are executed via __org.unidata.mdm.system.service.ExecutionService.execute(...)__ calls, returning a subclass of __org.unidata.mdm.system.dto.PipelineExecutionResult__. Simplified, the pipeline segment types hierarchie can be shown like this:

![Segments hierarchie simplified](https://gitlab.com/unidata-community/unidata-platform/raw/5.2-SNAPSHOT/docs/images/pipeline-2.png) 

## Environment setup (development)

1. Database
    - Create DB manually from psql or PG Admin
        - DB name: unidata
        - owner: postgres
2. Install Elasticsearch (versions above 5.6.x will not work, we are in progress migrating our search subsystem to the lastest ES)
    - Install russian/english morphology plugin, if you're going to index and search, using this feature:
	$ bin/elasticsearch-plugin install http://dl.bintray.com/content/imotov/elasticsearch-plugins/org/elasticsearch/elasticsearch-analysis-morphology/5.6.7/elasticsearch-analysis-morphology-5.6.7.zip
3. Install Apache Tomcat 7.x using your favorite method
4. UniData uses gradle to build itself
    - Go to __org.unidata.mdm.war__ and build WAR (and its dependencies) with ./gradle clean war
    - Deploy the artifact to tomcat 7.x using your favorite method