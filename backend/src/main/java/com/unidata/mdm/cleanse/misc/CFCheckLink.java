package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.backend.common.search.FormField.strictValue;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_DELETED;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_PUBLISHED;
import static com.unidata.mdm.backend.util.MessageUtils.getMessage;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT2;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.service.cleanse.CFAppContext;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.meta.LookupEntityDef;

public class CFCheckLink extends BasicCleanseFunctionAbstract {

    /**
     * Search service.
     */
    private SearchService searchService;
    /**
     * Model Service
     */
    private MetaModelServiceExt modelService;

    /**
     * Instantiates a new cleanse function abstract.
     */
    public CFCheckLink() {
        super(CFCheckLink.class);
        this.searchService = CFAppContext.getBean(SearchService.class);
        this.modelService = CFAppContext.getBean(MetaModelServiceExt.class);
    }

    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result)
            throws CleanseFunctionExecutionException {

        StringSimpleAttributeImpl outputMessage = new StringSimpleAttributeImpl(OUTPUT2, StringUtils.EMPTY);
        BooleanSimpleAttributeImpl cfResult = new BooleanSimpleAttributeImpl(OUTPUT1, false);

        Object refObj = getValueByPort(INPUT1, input);
        Object lookupEntityNameObj = getValueByPort(INPUT2, input);

        if (refObj == null || StringUtils.isEmpty(refObj.toString())) {
            cfResult.setValue(true);
            result.put(OUTPUT1, cfResult);
            result.put(OUTPUT2, outputMessage);
            return;
        }

        String lookupEntityName = lookupEntityNameObj.toString();
        LookupEntityDef lookupEntityDef = modelService.getLookupEntityById(lookupEntityName);
        String codeAttrName = lookupEntityDef.getCodeAttribute().getName();

        boolean isArray = refObj instanceof Object[];
        List<String> links = isArray ?
                //we can do it because links it is only int or string!
                Arrays.stream((Object[]) refObj).map(Object::toString).collect(Collectors.toList()) :
                Collections.singletonList(refObj.toString());
        Set<String> uniqueLinks = new HashSet<>(links);
        FormField notDeleted = strictValue(BOOLEAN, FIELD_DELETED.getField(), FALSE);
        FormField published = strictValue(BOOLEAN, FIELD_PUBLISHED.getField(), TRUE);
        FormFieldsGroup group = createAndGroup(notDeleted, published);
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(lookupEntityDef.getName())
                                                       .search(SearchRequestType.TERM)
                                                       .operator(SearchRequestOperator.OP_OR)
                                                       .searchFields(singletonList(codeAttrName))
                                                       .values(links)
                                                       .form(group)
                                                       .returnFields(singletonList(codeAttrName))
                                                       .page(0)
                                                       .count(Integer.MAX_VALUE)
                                                       .totalCount(true)
                                                       .countOnly(!isArray)
                                                       .onlyQuery(true)
                                                       .skipEtalonId(true)
                                                       .build();

        SearchResultDTO searchResult = searchService.search(ctx);
        boolean isPresent = searchResult.getTotalCount() != 0;
        if (isArray) {
            searchResult.getHits()
                        .stream()
                        .map(hit -> hit.getFieldValue(codeAttrName))
                        .filter(Objects::nonNull)
                        .map(SearchResultHitFieldDTO::getValues)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .sequential()
                        .forEach(uniqueLinks::remove);
            isPresent = uniqueLinks.isEmpty();
        }

        cfResult.setValue(isPresent);
        if (!isPresent) {
            String text = getMessage("app.cleanse.validation.lookupEntityRecord.notExist", uniqueLinks.toString());
            outputMessage.setValue(text);
        }

        result.put(OUTPUT1, cfResult);
        result.put(OUTPUT2, outputMessage);
    }
}
