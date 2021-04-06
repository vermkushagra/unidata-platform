/**
 *
 */
package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.backend.common.search.FormField.FormType.POSITIVE;
import static com.unidata.mdm.backend.common.search.FormField.exceptStrictValue;
import static com.unidata.mdm.backend.common.search.FormField.strictValue;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_DELETED;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_PUBLISHED;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_TO;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static com.unidata.mdm.meta.SimpleDataType.STRING;
import static com.unidata.mdm.meta.SimpleDataType.TIMESTAMP;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.service.cleanse.CFAppContext;
import com.unidata.mdm.backend.service.cleanse.DataQualityServiceExt;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Ruslan Trachuk
 */
public class CFAvoidDuplicates extends BasicCleanseFunctionAbstract {
    /**
     * Data service.
     */
    private DataRecordsService dataService;
    /**
     * Search service.
     */
    private SearchService searchService;

    /**
     * Constructor.
     *
     * @throws Exception
     */
    public CFAvoidDuplicates() throws Exception {
        super(CFAvoidDuplicates.class);
        this.dataService = CFAppContext.getBean(DataRecordsService.class);
        this.searchService = CFAppContext.getBean(SearchService.class);
    }

    @Override
    protected void validateInput(Map<String, Object> input) throws CleanseFunctionExecutionException {
        super.validateInput(input);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {

        String entityName = (String) input.get(DataQualityServiceExt.$ENTITY);
        if (searchService == null || dataService == null || entityName == null) {
            throw new RuntimeException("Unsufficient input. Input [search service: " + searchService
                    + ", data service: " + dataService + ", entity: " + entityName + "]");
        }

        // Collect values.
        List<FormField> searchFields = new ArrayList<>();
        List<String> portsToCheck = super.getInputPorts().keySet().stream()
                .filter(k -> !StringUtils.equalsIgnoreCase(k, "IS_CODE_ATTR")
                        && !StringUtils.equalsIgnoreCase(k, "IS_SEARCH_BY_ALL_PERIODS"))
                .collect(Collectors.toList());
        for (String inputPortName : portsToCheck) {

            Attribute attr = (Attribute) input.get(inputPortName);
            SimpleDataType type = extractDataType(attr);
            Object val = extractDataValue(attr);
            if (attr == null || type == null || val ==null || (type == STRING && isEmpty((String) val))) {
                continue;
            }

            searchFields.add(strictValue(type, attr.getName(), val));
        }

        boolean isDuplicate = false;
        if (!searchFields.isEmpty()) {
            FormFieldsGroup searchFieldsGroup = FormFieldsGroup.createAndGroup(searchFields);
            FormFieldsGroup specifiedAndGroup = specifiedAndGroupFields(input);
            SearchRequestContext ctx = SearchRequestContext.forEtalonData(entityName)
                    .form(searchFieldsGroup, specifiedAndGroup)
                    .totalCount(true)
                    .countOnly(true)
                    .onlyQuery(true)
                    .search(SearchRequestType.TERM)
                    .operator(SearchRequestOperator.OP_AND)
                    .build();

            SearchResultDTO searchResult = searchService.search(ctx);
            isDuplicate = searchResult.getTotalCount() != 0;
        }

        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1).withValue(!isDuplicate));
    }

    private SimpleDataType extractDataType(Attribute attr) {

        if (Objects.nonNull(attr)) {
            if (attr.getAttributeType() == AttributeType.CODE) {
                return SimpleDataType.valueOf(((CodeAttribute<?>) attr).getDataType().name());
            } else if (attr.getAttributeType() == AttributeType.SIMPLE) {
                return SimpleDataType.valueOf(((SimpleAttribute<?>) attr).getDataType().name());
            }
        }

        return null;
    }

    private Object extractDataValue(Attribute attr) {

        if (Objects.nonNull(attr)) {
            if (attr.getAttributeType() == AttributeType.CODE) {
                CodeAttribute<?> cast = (CodeAttribute<?>) attr;
                return cast.getDataType() == CodeDataType.STRING && cast.getValue() == null
                        ? StringUtils.EMPTY
                        : cast.getValue();
            } else if (attr.getAttributeType() == AttributeType.SIMPLE) {
                SimpleAttribute<?> cast = (SimpleAttribute<?>) attr;
                return cast.getDataType() == DataType.STRING && cast.getValue() == null
                        ? StringUtils.EMPTY
                        : cast.narrow(SimpleAttribute.NarrowType.ES);
            }
        }

        return null;
    }

    private FormFieldsGroup specifiedAndGroupFields(Map<String, Object> input) {
        String id = (String) input.get(DataQualityServiceExt.$ETALON_RECORD_ID);
        boolean isOldRecord = !StringUtils.isBlank(id);
        Collection<FormField> specifiedFormFields = new ArrayList<>();
        specifiedFormFields.add(strictValue(BOOLEAN, FIELD_DELETED.getField(), Boolean.FALSE));
        specifiedFormFields.add(strictValue(BOOLEAN, FIELD_PUBLISHED.getField(), Boolean.TRUE));
        if (isOldRecord) {
            //skip yourself
            specifiedFormFields.add(exceptStrictValue(STRING, FIELD_ETALON_ID.getField(), id));
        }

        Object searchByAllPeriodsObject = input.get("IS_SEARCH_BY_ALL_PERIODS");
        Boolean searchByAllPeriods = searchByAllPeriodsObject instanceof BooleanSimpleAttributeImpl  ?
                ((BooleanSimpleAttributeImpl) searchByAllPeriodsObject).getValue() : false;

        if(BooleanUtils.isFalse(searchByAllPeriods)){
            Date from = (Date) input.get(DataQualityServiceExt.$FROM);
            Date to = (Date) input.get(DataQualityServiceExt.$TO);
            if (Objects.nonNull(to)) {
                specifiedFormFields.add(FormField.range(TIMESTAMP, FIELD_FROM.getField(), POSITIVE, null, to));
            }
            if (Objects.nonNull(from)) {
                specifiedFormFields.add(FormField.range(TIMESTAMP, FIELD_TO.getField(), POSITIVE, from, null));
            }
        }
        return FormFieldsGroup.createAndGroup(specifiedFormFields);
    }

    /**
     * Return value of {@SimpleAttribute} by.
     *
     * @param name  the name
     * @param input the input
     * @return the value by name
     * @throws Exception the exception
     */
    @Override
    protected Object getValueByPort(String name, Map<String, Object> input) throws CleanseFunctionExecutionException {
        List<Port> inputPorts = definition.getInputPorts();
        for (Port port : inputPorts) {
            if (port.getName().equals(name)) {
                Attribute value = (Attribute) input.get(port.getName());

                if (value == null) {
                    break;
                }

                return extractDataValue(value);
            }
        }
        return null;
    }
}
