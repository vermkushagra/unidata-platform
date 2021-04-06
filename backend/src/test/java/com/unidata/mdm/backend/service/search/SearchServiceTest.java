/**
 *
 */
package com.unidata.mdm.backend.service.search;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonRelation;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_FROM_ETALON_ID;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.exception.SearchApplicationException;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.common.types.impl.EtalonRelationImpl;
import com.unidata.mdm.backend.conf.impl.SearchImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.search.impl.SearchServiceImpl;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class SearchServiceTest {

    private static final String INDEX_NAME_1 = "test1";
    private static final String INDEX_NAME_2 = "test2";
    private static final String CORRECT_STORAGE_ID = "default";
    private static final String INCORRECT_STORAGE_ID = "fffff";
    private static final String ENTITY_NAME_1 = "unitTest1";
    private static final String ENTITY_NAME_2 = "unitTest2";
    private static final String RELATION_NAME = "unitRelTest";
    private static final String SIMPLE_ATTR_1 = "attr1";
    private static final String SIMPLE_ATTR_2 = "attr2";
    private static final String ETALON_ID_FROM = UUID.randomUUID().toString();
    private static final String ETALON_ID_TO = UUID.randomUUID().toString();
    private static final String ETALON_REL = UUID.randomUUID().toString();
    /**
     * Client.
     */
    private static EmbeddedElasticsearchServer server = new EmbeddedElasticsearchServer();


    /** The configuration service. */
    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private SearchServiceExt searchService = new SearchServiceImpl(server.getClient());


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configurationService.getSearch()).thenReturn(new SearchImpl());
    }

    @AfterClass
    public static void classDestroy() {
        if (server != null) {
            server.shutdown();
        }
    }

    @Test
    public void A1_indexCreation() throws IOException {
        searchService.createIndex(INDEX_NAME_1, CORRECT_STORAGE_ID, null, false);
        searchService.createIndex(INDEX_NAME_2, CORRECT_STORAGE_ID, null, false);
        assertThat("The index wasn't created", searchService.indexExists(INDEX_NAME_1, CORRECT_STORAGE_ID));
    }

    @Test
    public void A2_putMapping() {
        //given
        SimpleAttributeDef simpleAttr1 = new SimpleAttributeDef().withSimpleDataType(SimpleDataType.INTEGER)
                                                                 .withName(SIMPLE_ATTR_1);
        SimpleAttributeDef simpleAttr2 = new SimpleAttributeDef().withSimpleDataType(SimpleDataType.STRING)
                                                                 .withName(SIMPLE_ATTR_2);
        EntityDef entityDef1 = new EntityDef();
        entityDef1.withName(ENTITY_NAME_1).withSimpleAttribute(simpleAttr1, simpleAttr2);
        EntityDef entityDef2 = new EntityDef();
        entityDef2.withName(ENTITY_NAME_2).withSimpleAttribute(simpleAttr1, simpleAttr2);
        //when
        boolean mapping1 = searchService.updateEntityMapping(entityDef1, Collections.emptyList(), CORRECT_STORAGE_ID);
        boolean mapping2 = searchService.updateEntityMapping(entityDef2, Collections.emptyList(), CORRECT_STORAGE_ID);
        //then
        assertThat("Mapping wasn't applied", mapping1 && mapping2);
    }

    @Test
    public void A3_getEmptyResult() {
        //given
        SearchRequestContext context1 = SearchRequestContext.forEtalonData(ENTITY_NAME_1)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .build();

        SearchRequestContext context2 = SearchRequestContext.forEtalonData(ENTITY_NAME_2)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .build();
        //when

        SearchResultDTO resultDTO1 = searchService.search(context1);
        SearchResultDTO resultDTO2 = searchService.search(context2);
        //then
        assertThat("Result set is not empty", resultDTO1.getTotalCount() == 0 && resultDTO2.getTotalCount() == 0);
    }

    @Test
    public void B1_indexRecord() {
        //given from
        EtalonRecordImpl etalonRecordFrom = new EtalonRecordImpl(new SerializableDataRecord());
        Long value1 = 5L;
        etalonRecordFrom.putAttribute(SIMPLE_ATTR_1, value1);
        etalonRecordFrom.putAttribute(SIMPLE_ATTR_2, "testFrom");
        EtalonRecordInfoSection fromInfo = new EtalonRecordInfoSection().withEntityName(ENTITY_NAME_1)
                                                                        .withEtalonKey(EtalonKey.builder()
                                                                                                .id(ETALON_ID_FROM)
                                                                                                .build());
        etalonRecordFrom.withInfoSection(fromInfo);
        //given to
        EtalonRecordImpl etalonRecordTo = new EtalonRecordImpl(new SerializableDataRecord());
        etalonRecordTo.putAttribute(SIMPLE_ATTR_1, value1);
        etalonRecordTo.putAttribute(SIMPLE_ATTR_2, "testTo");
        EtalonRecordInfoSection toInfo = new EtalonRecordInfoSection().withEntityName(ENTITY_NAME_2)
                                                                      .withEtalonKey(EtalonKey.builder()
                                                                                              .id(ETALON_ID_TO)
                                                                                              .build());
        etalonRecordTo.withInfoSection(toInfo);
        Map<EtalonRecord, Map<? extends SearchField, Object>> records = new HashMap<>();
        records.put(etalonRecordFrom, emptyMap());
        records.put(etalonRecordTo, emptyMap());
        //when
        IndexRequestContext firstIndexContext = IndexRequestContext.builder()
                                                                   .drop(false)
                                                                   .records(records)
                                                                   .classifiers(Collections.emptyList())
                                                                   .build();
        boolean indexed1 = searchService.index(firstIndexContext);
        //then
        assertThat("Record wasn't indexed", indexed1);
    }

    @Test
    public void B2_getNotEmptyResult() {
        //given
        SearchRequestContext context1 = SearchRequestContext.forEtalonData(ENTITY_NAME_1)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .onlyQuery(true)
                                                            .build();

        SearchRequestContext context2 = SearchRequestContext.forEtalonData(ENTITY_NAME_2)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .onlyQuery(true)
                                                            .build();
        //when
        SearchResultDTO resultDTO1 = searchService.search(context1);
        SearchResultDTO resultDTO2 = searchService.search(context2);
        //then
        assertThat("Result set is empty", resultDTO1.getTotalCount() > 0 && resultDTO2.getTotalCount() > 0);
    }

    @Test(expected = SearchApplicationException.class)
    public void B3_putIncorrectMapping() {
        //given
        SimpleAttributeDef simpleAttr1 = new SimpleAttributeDef().withSimpleDataType(SimpleDataType.STRING)
                                                                 .withName(SIMPLE_ATTR_1);
        SimpleAttributeDef simpleAttr2 = new SimpleAttributeDef().withSimpleDataType(SimpleDataType.INTEGER)
                                                                 .withName(SIMPLE_ATTR_2);
        EntityDef entityDef = new EntityDef();
        entityDef.withName(ENTITY_NAME_1).withSimpleAttribute(simpleAttr1, simpleAttr2);
        //when
        boolean mapping = searchService.updateEntityMapping(entityDef, Collections.emptyList(), CORRECT_STORAGE_ID);
        //then
        assertThat("Mapping wasn't applied", mapping);
    }

    @Test
    public void C1_getEmptyResultForRelation() {
        //given
        SearchRequestContext main = SearchRequestContext.forEtalonData(ENTITY_NAME_1)
                                                        .storageId(CORRECT_STORAGE_ID)
                                                        .countOnly(true)
                                                        .totalCount(true)
                                                        .fetchAll(true)
                                                        .build();

        FormField formField = FormField.strictString(RelationHeaderField.FIELD_TO_ETALON_ID.getField(), ETALON_ID_TO);

        SearchRequestContext rels = SearchRequestContext.forEtalonRelation(ENTITY_NAME_1)
                                                        .storageId(CORRECT_STORAGE_ID)
                                                        .form(FormFieldsGroup.createAndGroup(formField))
                                                        .build();
        ComplexSearchRequestContext context = ComplexSearchRequestContext.hierarchical(main, rels);

        //when
        Map<SearchRequestContext, SearchResultDTO> result = searchService.search(context);
        SearchResultDTO resultDTO = result.get(main);
        //then
        assertThat("Result set is not empty", resultDTO.getTotalCount() == 0);
    }

    @Test
    public void C2_indexRelation() {
        //given
        EtalonRelationInfoSection infoSection = new EtalonRelationInfoSection();
        infoSection.setFromEtalonKey(EtalonKey.builder().id(ETALON_ID_FROM).build());
        infoSection.setToEtalonKey(EtalonKey.builder().id(ETALON_ID_TO).build());
        infoSection.setValidTo(new Date());
        infoSection.setValidFrom(new Date());
        infoSection.setFromEntityName(ENTITY_NAME_1);
        infoSection.setToEntityName(ENTITY_NAME_2);
        infoSection.setRelationName(RELATION_NAME);
        infoSection.setRelationEtalonKey(ETALON_REL);
        infoSection.setType(RelationType.REFERENCES);
        EtalonRelationImpl relation = new EtalonRelationImpl(new SerializableDataRecord());
        relation.setInfoSection(infoSection);

        IndexRequestContext context = IndexRequestContext.builder()
                                                         .relations(Collections.singletonList(relation))
                                                         .drop(false)
                                                         .build();
        //when
        boolean indexed = searchService.index(context);
        //then
        assertThat("Relation wasn't indexed", indexed);
    }

    @Test
    public void C3_getNotEmptyResultForRels() {
        //given
        SearchRequestContext context1 = SearchRequestContext.forEtalonRelation(ENTITY_NAME_1)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .onlyQuery(true)
                                                            .build();
        SearchRequestContext context2 = SearchRequestContext.forEtalonRelation(ENTITY_NAME_2)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .onlyQuery(true)
                                                            .build();

        //when
        SearchResultDTO resultDTO1 = searchService.search(context1);
        SearchResultDTO resultDTO2 = searchService.search(context2);
        //then
        assertThat("Result set is empty", resultDTO1.getTotalCount() != 0 && resultDTO2.getTotalCount() != 0);
    }

    @Test
    public void C4_getNotEmptyResultForRelsFormRequest() {
        //given
        FormField formField = FormField.strictString(RelationHeaderField.FIELD_TO_ETALON_ID.getField(), ETALON_ID_TO);

        SearchRequestContext rels1 = SearchRequestContext.forEtalonRelation(ENTITY_NAME_1)
                                                         .storageId(CORRECT_STORAGE_ID)
                                                         .form(FormFieldsGroup.createAndGroup(formField))
                                                         .skipEtalonId(false)
                                                         .onlyQuery(true)
                                                         .totalCount(true)
                                                         .build();

        SearchRequestContext rels2 = SearchRequestContext.forEtalonRelation(ENTITY_NAME_2)
                                                         .storageId(CORRECT_STORAGE_ID)
                                                         .form(FormFieldsGroup.createAndGroup(formField))
                                                         .skipEtalonId(false)
                                                         .onlyQuery(true)
                                                         .totalCount(true)
                                                         .build();
        //when
        SearchResultDTO resultDTO1 = searchService.search(rels1);
        SearchResultDTO resultDTO2 = searchService.search(rels2);
        //then
        assertThat("Result set is empty", resultDTO1.getTotalCount() != 0 && resultDTO2.getTotalCount() != 0);
    }

    @Test
    public void C5_getResultOfComplexToRequest() {
        //given
        SearchRequestContext main = SearchRequestContext.forEtalonData(ENTITY_NAME_1)
                                                        .storageId(CORRECT_STORAGE_ID)
                                                        .countOnly(true)
                                                        .totalCount(true)
                                                        .onlyQuery(true)
                                                        .fetchAll(true)
                                                        .build();

        FormField formField = FormField.strictString(RelationHeaderField.FIELD_TO_ETALON_ID.getField(), ETALON_ID_TO);

        SearchRequestContext rels = SearchRequestContext.forEtalonRelation(ENTITY_NAME_1)
                                                        .storageId(CORRECT_STORAGE_ID)
                                                        .form(FormFieldsGroup.createAndGroup(formField))
                                                        .build();
        ComplexSearchRequestContext context = ComplexSearchRequestContext.hierarchical(main, rels);

        //when
        Map<SearchRequestContext, SearchResultDTO> result = searchService.search(context);
        SearchResultDTO resultDTO = result.get(main);
        //then
        assertThat("Result set is empty", resultDTO.getTotalCount() != 0);
    }

    @Test
    public void C6_getResultOfComplexFromRequest() {
        //given
        SearchRequestContext main = SearchRequestContext.forEtalonData(ENTITY_NAME_2)
                                                        .storageId(CORRECT_STORAGE_ID)
                                                        .countOnly(true)
                                                        .totalCount(true)
                                                        .onlyQuery(true)
                                                        .fetchAll(true)
                                                        .build();

        FormField formField = FormField.strictString(FIELD_FROM_ETALON_ID.getField(), ETALON_ID_FROM);

        SearchRequestContext rels = SearchRequestContext.forEtalonRelation(ENTITY_NAME_2)
                                                        .storageId(CORRECT_STORAGE_ID)
                                                        .form(FormFieldsGroup.createAndGroup(formField))
                                                        .build();
        ComplexSearchRequestContext context = ComplexSearchRequestContext.hierarchical(main, rels);

        //when
        Map<SearchRequestContext, SearchResultDTO> result = searchService.search(context);
        SearchResultDTO resultDTO = result.get(main);
        //then
        assertThat("Result set is empty", resultDTO.getTotalCount() != 0);
    }

    @Test
    public void D1_removeRel(){
        //given
        FormField id = FormField.strictString(FIELD_FROM_ETALON_ID.getField(), ETALON_ID_FROM);
        FormFieldsGroup fieldsGroup = createAndGroup(id);
        SearchRequestContext fromContext = forEtalonRelation(ENTITY_NAME_1).form(fieldsGroup).build();
        SearchRequestContext toContext = forEtalonRelation(ENTITY_NAME_2).form(fieldsGroup).build();
        ComplexSearchRequestContext context = ComplexSearchRequestContext.multi(fromContext, toContext);
        //when
        boolean result = searchService.deleteFoundResult(context);
        //then
        assertThat("Something went wrong during removing", result);
    }

    @Test
    public void D2_checkRelsCount(){
        //given
        SearchRequestContext context1 = SearchRequestContext.forEtalonRelation(ENTITY_NAME_1)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .onlyQuery(true)
                                                            .build();

        SearchRequestContext context2 = SearchRequestContext.forEtalonRelation(ENTITY_NAME_2)
                                                            .storageId(CORRECT_STORAGE_ID)
                                                            .countOnly(true)
                                                            .totalCount(true)
                                                            .fetchAll(true)
                                                            .onlyQuery(true)
                                                            .build();

        //when
        ComplexSearchRequestContext context = ComplexSearchRequestContext.multi(context1, context2);
        Map<SearchRequestContext, SearchResultDTO> result = searchService.search(context);
        //then
        assertThat("Result set is not empty", result.get(context1).getTotalCount() == 0 && result.get(context2).getTotalCount() == 0);
    }

    @Test
    public void W_removeExistedIndex() {
        assertThat("The index doesn't exist", searchService.indexExists(INDEX_NAME_1, CORRECT_STORAGE_ID));
        assertThat("The index doesn't exist", searchService.indexExists(INDEX_NAME_2, CORRECT_STORAGE_ID));
        assertThat("The index doesn't exist", searchService.indexExists(ENTITY_NAME_1, CORRECT_STORAGE_ID));
        assertThat("The index doesn't exist", searchService.indexExists(ENTITY_NAME_2, CORRECT_STORAGE_ID));
        assertThat("The index wasn't removed", searchService.dropIndex(INDEX_NAME_1, CORRECT_STORAGE_ID));
        assertThat("The index wasn't removed", searchService.dropIndex(INDEX_NAME_2, CORRECT_STORAGE_ID));
        assertThat("The index wasn't removed", searchService.dropIndex(ENTITY_NAME_1, CORRECT_STORAGE_ID));
        assertThat("The index wasn't removed", searchService.dropIndex(ENTITY_NAME_2, CORRECT_STORAGE_ID));
        assertThat("The index still exist", !searchService.indexExists(INDEX_NAME_1, CORRECT_STORAGE_ID));
        assertThat("The index still exist", !searchService.indexExists(INDEX_NAME_2, CORRECT_STORAGE_ID));
        assertThat("The index still exist", !searchService.indexExists(ENTITY_NAME_1, CORRECT_STORAGE_ID));
        assertThat("The index still exist", !searchService.indexExists(ENTITY_NAME_2, CORRECT_STORAGE_ID));
    }

    @Test
    public void removeNotExistedIndex() {
        assertThat("The index exist", !searchService.indexExists(INDEX_NAME_1, INCORRECT_STORAGE_ID));
        assertThat("The index exist", !searchService.indexExists(INDEX_NAME_2, INCORRECT_STORAGE_ID));
        assertThat("The index was removed", !searchService.dropIndex(INDEX_NAME_1, INCORRECT_STORAGE_ID));
        assertThat("The index was removed", !searchService.dropIndex(INDEX_NAME_2, INCORRECT_STORAGE_ID));
    }

}
