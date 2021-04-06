package com.unidata.mdm.cleanse.convert;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.PATTERN;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function parse string to date.
 * @author ilya.bykov
 */
public class CFParseDate extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF parse date.
     */
    public CFParseDate(){
        super(CFParseDate.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        String value = (String) super.getValueByPort(INPUT1, input);
        boolean isBlank = StringUtils.isBlank(value);
        Date dt = null;
		try {
            if(!isBlank){
                dt = DateUtils.parseDateStrictly(value, (String) super.getValueByPort(PATTERN, input));
            }
		} catch (ParseException e) {
			throw new CleanseFunctionExecutionException(getDefinition().getFunctionName(), "Unable to parse string to date");
		}

		// TODO split this to support DATE, TIME and TIMESTAMP.
        LocalDateTime ldt = dt == null ? null : LocalDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault());
        result.put(OUTPUT1, new TimestampSimpleAttributeImpl(OUTPUT1, ldt));
    }

}
