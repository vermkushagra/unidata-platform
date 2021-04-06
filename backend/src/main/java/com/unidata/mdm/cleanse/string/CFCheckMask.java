package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT3;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT4;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT2;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Checks string value by mask.
 *
 * @author ilya.bykov
 */
public class CFCheckMask extends BasicCleanseFunctionAbstract {

    /** The regexp cache. */
    private LoadingCache<String, Pattern> regexpCache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Pattern>() {

                @Override
                public Pattern load(String regexp) {
                    return Pattern.compile(regexp);

                }
            });

    /**
     * Instantiates a new CF check mask.
     */
    public CFCheckMask() {
        super(CFCheckMask.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws Exception {

        String regexp = (String) super.getValueByPort(INPUT1, input);
        String mask = (String) super.getValueByPort(INPUT2, input);
        Object value = super.getValueByPort(INPUT3, input);
        String name = input.get(INPUT3) != null ? ((Attribute) input.get(INPUT3)).getName() : "";
        boolean isRequired = super.getValueByPort(INPUT4, input) == null ? false
                : (boolean) super.getValueByPort(INPUT4, input);
        regexp = StringUtils.isEmpty(regexp) ? RegexpUtils.convertMaskToRegexString(mask) : regexp;

        boolean isValid = StringUtils.isEmpty(regexp) && StringUtils.isEmpty(mask);
        boolean isArray = value instanceof Object[];
        if (!isValid && Objects.nonNull(value)) {

            if (isArray) {
                final String finalRegexp = regexp;
                isValid = Arrays.stream((Object[]) value).map(Object::toString).allMatch(v -> {
                    try {
                        return RegexpUtils.validate(regexpCache.get(finalRegexp), v);
                    } catch (ExecutionException e) {
                        return false;
                    }
                });
            } else {
                isValid = RegexpUtils.validate(regexpCache.get(regexp), value.toString());
            }
        }

        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1)
                .withValue(isRequired
                        ? isValid
                                : (
                                        (Objects.isNull(value) || StringUtils.isEmpty(value.toString())
                                                ?true
                                                        :isValid))));
        result.put(OUTPUT2, new StringSimpleAttributeImpl(OUTPUT2).withValue(isValid ? ""
                : MessageUtils.getMessage("app.cleanse.validation.mask", value == null ? null
                        : (isArray ? ArrayUtils.toString(value) : value.toString()), name, mask)));
    }

}
