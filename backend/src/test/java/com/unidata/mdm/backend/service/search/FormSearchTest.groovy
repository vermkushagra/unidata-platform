package com.unidata.mdm.backend.service.search

import com.unidata.mdm.backend.common.context.IndexRequestContext
import com.unidata.mdm.backend.common.context.SearchRequestContext
import com.unidata.mdm.backend.common.dto.SearchResultDTO
import com.unidata.mdm.backend.common.keys.EtalonKey
import com.unidata.mdm.backend.common.record.SerializableDataRecord
import com.unidata.mdm.backend.common.search.FormField
import com.unidata.mdm.backend.common.search.FormFieldsGroup
import com.unidata.mdm.backend.common.search.SearchField
import com.unidata.mdm.backend.common.types.EtalonRecord
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl
import com.unidata.mdm.backend.service.search.impl.SearchServiceImpl
import com.unidata.mdm.meta.EntityDef
import com.unidata.mdm.meta.SimpleAttributeDef
import com.unidata.mdm.meta.SimpleDataType
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test some kind of form requests
 */
class FormSearchTest extends Specification {

    /**
     * Entity name and storage id
     */
    private static final String INDEX = "index";
    /**
     * String attribute 1
     */
    private static final String STR1 = "str1";
    /**
     * String attribute 2
     */
    private static final String STR2 = "str2";

    /**
     * Client.
     */
    @Shared
    EmbeddedElasticsearchServer server;

    /**
     * Search service
     */
    @Shared
    SearchServiceExt searchService;

    def setupSpec() {
        //subscribe
        server = new EmbeddedElasticsearchServer();
        searchService = new SearchServiceImpl(server.getClient());

        //put mapping
        SimpleAttributeDef simpleAttr1 = new SimpleAttributeDef().withSimpleDataType(SimpleDataType.STRING)
                .withName(STR1);
        SimpleAttributeDef simpleAttr2 = new SimpleAttributeDef().withSimpleDataType(SimpleDataType.STRING)
                .withName(STR2);
        EntityDef entityDef1 = new EntityDef().withName(INDEX).withSimpleAttribute(simpleAttr1, simpleAttr2);

        searchService.updateEntityMapping(entityDef1, Collections.emptyList(), INDEX);

        //index records
        def headers = [:];
        def records = [:];
        records[createRecord("tett erfix xofffft", "xdfx *gh? kli")] = headers;
        records[createRecord("tetss efrhx foffffsfd", "xdcx *gh? opp")] = headers;
        records[createRecord("tetg frerx dfsoffffsad", "xdxx *gh? kli")] = headers;
        records[createRecord("te ferfx ewtoffffdfs", "xxxx *gh? ,jk")] = headers;
        records[createRecord("tet felrx 123offfffds", "xdxd *gh| jgh")] = headers;
        records[createRecord("tsx fertx 4fsoffffsdf3", "dxxx *gh. dg")] = headers;

        IndexRequestContext context = IndexRequestContext.builder().storageId(INDEX).drop(false).records(records as Map<EtalonRecord, Map<? extends SearchField, Object>>).build();

        searchService.index(context);
    }

    def cleanupSpec() {
        searchService.dropIndex(INDEX, INDEX);
    }

    private static EtalonRecordImpl createRecord(String sttr1, String sttr2) {
        EtalonRecordImpl etalon1 = new EtalonRecordImpl(new SerializableDataRecord());
        etalon1.putAttribute(STR1, sttr1);
        etalon1.putAttribute(STR2, sttr2);
        EtalonRecordInfoSection toInfo = new EtalonRecordInfoSection().withEntityName(INDEX)
                .withEtalonKey(EtalonKey.builder()
                .id(UUID.randomUUID().toString())
                .build());
        etalon1.withInfoSection(toInfo);
        return etalon1;
    }


    @Unroll
    def "check that number of hits equals #resultSize when search over #attr and value starts with '#text'"() {
        given: "Search request which try to find all results which #attr starts with #text"
        FormField field = FormField.startWithString(attr, text);
        FormFieldsGroup fieldsGroup = FormFieldsGroup.createAndGroup(field);
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(INDEX)
                .storageId(INDEX)
                .form(fieldsGroup)
                .returnFields(Collections.singletonList(STR1))
                .page(0)
                .count(10)
                .onlyQuery(true)
                .build();
        when: "execute search request"
        SearchResultDTO result = searchService.search(ctx);
        then: "number of hits equals #resultSize"
        assert result.hits.size() == resultSize;
        where:
        attr | text          || resultSize
        STR1 | "fer"         || 2
        STR1 | "te"          || 5
        STR2 | "xd"          || 4
        STR1 | "fer te"      || 1
        STR1 | "fer sdfvgdf" || 0
    }

    @Unroll
    def "check that number of hits equals #resultSize when search over #attr and value not starts with '#text'"() {
        given: "Search request which try to find all results which #attr starts with #text"
        FormField field = FormField.notStartWithString(attr, text);
        FormFieldsGroup fieldsGroup = FormFieldsGroup.createAndGroup(field);
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(INDEX)
                .storageId(INDEX)
                .form(fieldsGroup)
                .returnFields(Collections.singletonList(STR1))
                .page(0)
                .count(10)
                .onlyQuery(true)
                .build();
        when: "execute search request"
        SearchResultDTO result = searchService.search(ctx);
        then: "number of hits equals #resultSize"
        assert result.hits.size() == resultSize;
        where:
        attr | text  || resultSize
        STR1 | "fer" || 4
        STR1 | "t"   || 0
        STR1 | "123" || 5
    }

    @Unroll
    def "check that number of hits equals #resultSize when search over #attr and value like '#text'"() {
        given: "Search request which try to find all results which #attr like #text"
        FormField field = FormField.likeString(attr, text);
        FormFieldsGroup fieldsGroup = FormFieldsGroup.createAndGroup(field);
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(INDEX)
                .storageId(INDEX)
                .form(fieldsGroup)
                .returnFields(Collections.singletonList(STR1))
                .page(0)
                .count(10)
                .onlyQuery(true)
                .build();
        when: "execute search request"
        SearchResultDTO result = searchService.search(ctx);
        then: "number of hits equals #resultSize"
        assert result.hits.size() == resultSize;
        where:
        attr | text    || resultSize
        STR1 | "offff" || 6
        STR1 | "re"    || 1
        STR2 | "*gh?"  || 4
        STR2 | "*gh"   || 6
        STR2 | " kl"   || 2
        STR2 | ",j"    || 1
    }

    @Unroll
    def "check that number of hits equals #resultSize when search over #attr and value not like '#text'"() {
        given: "Search request which try to find all results which #attr like #text"
        FormField field = FormField.notLikeString(attr, text);
        FormFieldsGroup fieldsGroup = FormFieldsGroup.createAndGroup(field);
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(INDEX)
                .storageId(INDEX)
                .form(fieldsGroup)
                .returnFields(Collections.singletonList(STR1))
                .page(0)
                .count(10)
                .onlyQuery(true)
                .build();
        when: "execute search request"
        SearchResultDTO result = searchService.search(ctx);
        then: "number of hits equals #resultSize"
        assert result.hits.size() == resultSize;
        where:
        attr | text    || resultSize
        STR1 | "offff" || 0
        STR1 | "re"    || 5
        STR2 | "*gh?"  || 2
        STR2 | "*gh"   || 0
        STR2 | " kl"   || 4
        STR2 | ",j"    || 5
    }

    def "check that empty from groups in a search request found nothing"() {
        given: "Empty groups in a search request"
        FormFieldsGroup fieldsGroup1 = FormFieldsGroup.createAndGroup();
        FormFieldsGroup fieldsGroup2 = FormFieldsGroup.createOrGroup();
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(INDEX)
                .storageId(INDEX)
                .form(fieldsGroup1, fieldsGroup2)
                .returnFields(Collections.singletonList(STR1))
                .page(0)
                .count(10)
                .onlyQuery(true)
                .build();
        when: "execute search request"
        SearchResultDTO result = searchService.search(ctx);
        then: "number of hits equals zero"
        assert result.hits.size() == 0;
    }

    def "check that empty search request found nothing"() {
        given: "Empty search request"
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(INDEX)
                .storageId(INDEX)
                .returnFields(Collections.singletonList(STR1))
                .page(0)
                .count(10)
                .onlyQuery(true)
                .build();
        when: "execute search request"
        SearchResultDTO result = searchService.search(ctx);
        then: "number of hits equals zero"
        assert result.hits.size() == 0;
    }
}
