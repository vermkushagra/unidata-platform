package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.backend.common.types.SimpleAttribute.DataType.BOOLEAN;
import static com.unidata.mdm.backend.common.types.SimpleAttribute.DataType.INTEGER;
import static com.unidata.mdm.backend.common.types.SimpleAttribute.DataType.NUMBER;
import static com.unidata.mdm.backend.common.types.SimpleAttribute.DataType.STRING;
import static com.unidata.mdm.backend.common.types.SimpleAttribute.DataType.TIMESTAMP;
import static com.unidata.mdm.cleanse.misc.CFInnerFetch.ENTITY_NAME_PORT;
import static com.unidata.mdm.cleanse.misc.CFInnerFetch.FETCH_MODE_PORT;
import static com.unidata.mdm.cleanse.misc.CFInnerFetch.ORDER_NAME_PORT;
import static com.unidata.mdm.cleanse.misc.CFInnerFetch.RETURN_NAME_PORT;
import static com.unidata.mdm.cleanse.misc.CFInnerFetch.SEARCH_NAME_PORT;
import static com.unidata.mdm.cleanse.misc.CFInnerFetch.SEARCH_VALUE_PORT;
import static com.unidata.mdm.meta.SimpleDataType.valueOf;
import static java.lang.Long.parseLong;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.google.common.collect.Iterables;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.backend.service.cleanse.CFAppContext;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(CFAppContext.class)
/**
 * 
 * @author ynikolaev
 *
 */
public class CFInnerFetchTest {
    /**
     * Fetch mode get first result
     * 
     */
    private static final IntegerSimpleAttributeImpl GET_FIRST = new IntegerSimpleAttributeImpl(FETCH_MODE_PORT, 0L);
    /**
     * Fetch mode get last result
     */
    private static final IntegerSimpleAttributeImpl GET_LAST = new IntegerSimpleAttributeImpl(FETCH_MODE_PORT, 1L);

    /**
     * Set of Different simple attributes
     */
    private static final IntegerSimpleAttributeImpl INT_ATTR = new IntegerSimpleAttributeImpl("port", 1L);
    private static final BooleanSimpleAttributeImpl BOOL_ATTR = new BooleanSimpleAttributeImpl("port", false);
    private static final StringSimpleAttributeImpl STR_ATTR = new StringSimpleAttributeImpl("port", "port");
    private static final NumberSimpleAttributeImpl DBL_ATR = new NumberSimpleAttributeImpl("port", 0.5d);
    private static final TimestampSimpleAttributeImpl TS_ATTR = new TimestampSimpleAttributeImpl("port", now());

    /**
     * Set of different data sets
     */
    private static final List<Object> INT_SET = Arrays.asList(5L, 4L, 7L, 8L, 10L, 435L, -2345L);
    private static final List<Object> BOOL_SET = Arrays.asList(true, false, true, false);
    private static final List<Object> STR_SET = Arrays.asList("tak", "siak", "koe", "kak");
    private static final List<Object> NBR_SET = Arrays.asList(5D, 4D, 7D, 8D, 10D, 435D, -2345D);
    private static final List<Object> TS_SET = Arrays.asList(new Date(), new Date(), new Date(), new Date(), new Date());

    /**
     * Search value #1
     */
    @Parameterized.Parameter(value = 0)
    public SimpleAttribute searchValue;
    /**
     * Return Type
     */
    @Parameterized.Parameter(value = 1)
    public SimpleAttribute.DataType returnType;
    /**
     * Fetch mode
     */
    @Parameterized.Parameter(value = 2)
    public IntegerSimpleAttributeImpl fetchMode;
    /**
     * Fetch mode
     */
    @Parameterized.Parameter(value = 3)
    public Long totalCount;
    /**
     * Fetch mode
     */
    @Parameterized.Parameter(value = 4)
    public List<Object> set;
    /**
     * Search result
     */
    private SearchResultDTO searchResult;
    /**
     * Search service mock
     */
    private SearchService searchService;
    /**
     * Meta model service mock
     */
    private MetaModelService metaModelService;
    /**
     * Input parameterize map
     */
    private Map<String, Object> inputMap;

    @Parameterized.Parameters
    public static Collection<Object[]> inputs() {
        return Arrays.asList(new Object[][] {
                //empty results
                { BOOL_ATTR, INTEGER, GET_FIRST, 0L, BOOL_SET },
                { STR_ATTR, STRING, GET_FIRST, parseLong(String.valueOf(INT_SET.size())), emptyList() },
                { INT_ATTR, BOOLEAN, GET_LAST, 0L, INT_SET },
                { DBL_ATR, NUMBER, GET_LAST, parseLong(String.valueOf(BOOL_SET.size())), emptyList() },
                //common results
                //integer result value
                { INT_ATTR, INTEGER, GET_FIRST, parseLong(String.valueOf(INT_SET.size())), INT_SET },
                { INT_ATTR, INTEGER, GET_FIRST, parseLong(String.valueOf(INT_SET.size())) - 2, INT_SET },
                { INT_ATTR, INTEGER, GET_LAST, parseLong(String.valueOf(INT_SET.size())), INT_SET },
                { INT_ATTR, INTEGER, GET_LAST, parseLong(String.valueOf(INT_SET.size())) - 2, INT_SET },
                //bool result value
                { BOOL_ATTR, BOOLEAN, GET_FIRST, parseLong(String.valueOf(BOOL_SET.size())), BOOL_SET },
                { BOOL_ATTR, BOOLEAN, GET_FIRST, parseLong(String.valueOf(BOOL_SET.size())) - 1, BOOL_SET },
                { BOOL_ATTR, BOOLEAN, GET_LAST, parseLong(String.valueOf(BOOL_SET.size())), BOOL_SET },
                { BOOL_ATTR, BOOLEAN, GET_LAST, parseLong(String.valueOf(BOOL_SET.size())) - 1, BOOL_SET },
                //string
                { STR_ATTR, STRING, GET_FIRST, parseLong(String.valueOf(STR_SET.size())), STR_SET },
                { STR_ATTR, STRING, GET_FIRST, parseLong(String.valueOf(STR_SET.size())) - 1, STR_SET },
                { STR_ATTR, STRING, GET_LAST, parseLong(String.valueOf(STR_SET.size())), STR_SET },
                { STR_ATTR, STRING, GET_LAST, parseLong(String.valueOf(STR_SET.size())) - 1, STR_SET },
                //number
                { DBL_ATR, NUMBER, GET_FIRST, parseLong(String.valueOf(NBR_SET.size())), NBR_SET },
                { DBL_ATR, NUMBER, GET_FIRST, parseLong(String.valueOf(NBR_SET.size())) - 1, NBR_SET },
                { DBL_ATR, NUMBER, GET_LAST, parseLong(String.valueOf(NBR_SET.size())), NBR_SET },
                { DBL_ATR, NUMBER, GET_LAST, parseLong(String.valueOf(NBR_SET.size())) - 1, NBR_SET },
                //timestamp
                { TS_ATTR, TIMESTAMP, GET_FIRST, parseLong(String.valueOf(TS_SET.size())), TS_SET },
                { TS_ATTR, TIMESTAMP, GET_FIRST, parseLong(String.valueOf(TS_SET.size())) - 1, TS_SET },
                { TS_ATTR, TIMESTAMP, GET_LAST, parseLong(String.valueOf(TS_SET.size())), TS_SET },
                { TS_ATTR, TIMESTAMP, GET_LAST, parseLong(String.valueOf(TS_SET.size())) - 1, TS_SET },
        });
    }

    @Before
    public void prepare() {
        //init map
        Map<String, Object> input = new HashMap<>();
        input.put(ENTITY_NAME_PORT, new StringSimpleAttributeImpl(ENTITY_NAME_PORT, ENTITY_NAME_PORT));
        input.put(RETURN_NAME_PORT, new StringSimpleAttributeImpl(RETURN_NAME_PORT, RETURN_NAME_PORT));
        input.put(ORDER_NAME_PORT, new StringSimpleAttributeImpl(ORDER_NAME_PORT, ORDER_NAME_PORT));
        input.put(SEARCH_NAME_PORT, new StringSimpleAttributeImpl(SEARCH_NAME_PORT, SEARCH_NAME_PORT));
        input.put(FETCH_MODE_PORT, fetchMode);
        input.put(SEARCH_VALUE_PORT, searchValue);
        inputMap = input;

        //init response
        searchResult = new SearchResultDTO();
        searchResult.setTotalCount(totalCount);
        List<SearchResultHitDTO> resultHitDTOs = set.stream()
                                                    .limit(totalCount)
                                                    .map(r -> new SearchResultHitDTO("", "", null, singletonMap(RETURN_NAME_PORT,
                                                            new SearchResultHitFieldDTO(RETURN_NAME_PORT,
                                                                    singletonList(r))), null))
                                                    .collect(Collectors.toList());
        searchResult.setHits(resultHitDTOs);

        //init mocks
        PowerMockito.mockStatic(CFAppContext.class);
        searchService = Mockito.mock(SearchService.class);
        metaModelService = Mockito.mock(MetaModelService.class);
        Mockito.when(metaModelService.getAttributeByPath(anyString(), anyString()))
               .thenReturn(new AbstractSimpleAttributeDef().withSimpleDataType(valueOf(returnType.name())));
        Mockito.when(CFAppContext.getBean(SearchService.class)).thenReturn(searchService);
        Mockito.when(CFAppContext.getBean(MetaModelService.class)).thenReturn(metaModelService);
        Mockito.when(searchService.search(Mockito.any(SearchRequestContext.class))).thenReturn(searchResult);
    }

    @Test
    public void testAttrExtracting() throws Exception {
        //given
        CFInnerFetch cf = new CFInnerFetch();
        //when
        SimpleAttribute result = (SimpleAttribute)cf.retrieveAttr(searchResult, inputMap);
        //then
        assertThat("Null result", result, is(not(equalTo(null))));
        assertThat("Data type", result.getDataType(), is(equalTo(returnType)));
        boolean empty = totalCount == 0 || set.isEmpty();
        assertThat("Value is not null", result.getValue(), is(not(equalTo(empty ? new Object() : null))));
        if (empty) {
            return;
        }
        //check value
        Object resultValue = fetchMode.getValue().equals(0L) ?
                Iterables.getFirst(set.subList(0, Math.toIntExact(totalCount)), null) :
                Iterables.getLast(set.subList(0, Math.toIntExact(totalCount)), null);
        assertThat("Result value", result.narrow(SimpleAttribute.NarrowType.ES), is(equalTo(resultValue)));
    }

    @Test
    public void testSearchRequestCreation() throws Exception {
        //given
        CFInnerFetch cf = new CFInnerFetch();
        //when
        SearchRequestContext searchRequest = cf.createSearchRequest(inputMap);
        //then
        List<FormFieldsGroup> groups = searchRequest.getForm();
        assertThat("Entity name", searchRequest.getEntity(), is(equalTo(ENTITY_NAME_PORT)));
        assertThat("Return field", searchRequest.getReturnFields(), is(contains(RETURN_NAME_PORT)));
        assertThat("Total count", searchRequest.isTotalCount(), is(equalTo(true)));
        assertThat("Group Form Field count", 1, is(equalTo(groups.size())));
        assertThat("Form Fields in group [count]", groups.get(0).getFormFields().size(), is(equalTo(1)));
        assertThat("Form Field name", groups.get(0).getFormFields().iterator().next().getPath(),
                is(equalTo(SEARCH_NAME_PORT)));
        assertThat("Form Field value", groups.get(0).getFormFields().iterator().next().getInitialSingleValue(),
                is(equalTo(searchValue.narrow(SimpleAttribute.NarrowType.ES))));
        assertThat("Sort field", searchRequest.getSortFields().size(), is(equalTo(1)));
        assertThat("Sort field name", searchRequest.getSortFields().iterator().next().getFieldName(),
                is(equalTo(ORDER_NAME_PORT)));
    }
}
