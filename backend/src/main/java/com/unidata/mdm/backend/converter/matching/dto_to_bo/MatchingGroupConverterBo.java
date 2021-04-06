package com.unidata.mdm.backend.converter.matching.dto_to_bo;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.matching.MatchingGroupRO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MatchingValidationException;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;

@ConverterQualifier
@Component
public class MatchingGroupConverterBo implements Converter<MatchingGroupRO, MatchingGroup> {

    @Override
    public MatchingGroup convert(MatchingGroupRO source) {
        MatchingGroup matchingGroup = new MatchingGroup();
        matchingGroup.setId(source.getId());
        matchingGroup.setName(source.getName());
        matchingGroup.setDescription(source.getDescription());
        matchingGroup.setAutoMerge(source.isAutoMerge());
        matchingGroup.setEntityName(source.getEntityName());
        matchingGroup.setRulesIds(source.getRuleIds());
        Collection<ValidationResult> validationResults = matchingGroup.checkCompleteness();
        if (!validationResults.isEmpty()) {
            throw new MatchingValidationException("Matching group incorrect", ExceptionId.EX_MATCHING_GROUP_INCORRECT,
                    validationResults);
        }
        return matchingGroup;
    }
}
