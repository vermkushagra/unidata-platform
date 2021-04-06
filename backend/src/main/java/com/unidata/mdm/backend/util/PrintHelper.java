package com.unidata.mdm.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Collection;
import java.util.Map;

/**
 * @author denis.vinnichek
 */
public class PrintHelper {
    private static final Logger log = LoggerFactory.getLogger(PrintHelper.class);

    public static final String ITEMS_SEPARATOR = ",";
    
    public static String printObjectArray(Object... args) {
        StringBuilder sb = new StringBuilder().append("[");

        if (args != null && args.length > 0) {
            if (args.length == 1 && args[0] instanceof MapSqlParameterSource) {
                Map<String, Object> values = ((MapSqlParameterSource)args[0]).getValues();
                int itemsCounter = 0;
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    if (itemsCounter++ > 0) {
                        sb.append(ITEMS_SEPARATOR);
                    }
                    sb.append(entry.getKey()).append(':');
                    String itemStr;
                    if (entry.getValue() != null) {
                        Object value = entry.getValue();
                        if (value instanceof SqlParameterValue) {
                            SqlParameterValue itemParam = (SqlParameterValue) value;
                            Object itemValue = itemParam.getValue();
                            itemStr = itemValue != null ? itemValue.toString() : "null";
                        } else {
                            itemStr = value.toString();
                        }
                    } else {
                        itemStr = "null";
                    }
                    sb.append(itemStr);
                }
            } else {

                int itemsCounter = 0;
                for (Object item : args) {
                    if (itemsCounter++ > 0) {
                        sb.append(ITEMS_SEPARATOR);
                    }
                    if (item != null) {
                        String itemStr;
                        if (item instanceof SqlParameterValue) {
                            SqlParameterValue itemParam = (SqlParameterValue) item;
                            Object itemValue = itemParam.getValue();
                            itemStr = itemValue != null ? itemValue.toString() : "null";
                        } else {
                            itemStr = item.toString();
                        }
                        sb.append(itemStr);
                    } else {
                        sb.append("null");
                    }
                }
            }
        }
        
        sb.append("]");
        return sb.toString();
    }

    public static String printCollection(Collection collection) {
        StringBuilder sb = new StringBuilder("[");

        if (collection != null) {
            int itemsCounter = 0;
            for (Object obj : collection) {
                if (itemsCounter++ > 0) {
                    sb.append(ITEMS_SEPARATOR);
                }
                sb.append(objectToString(obj));
            }
        } else {
            sb.append("null");
        }
        sb.append("]");

        return sb.toString();
    }

    public static String objectToString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ex) {
            log.error("Failed to serialize object: " + ex.getMessage(), ex);
            return "[Serialization error]";
        }
    }

    public static String multiplyString(String strToMultiply, int multiplyCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < multiplyCount; i++) {
            if (i > 0) {
                sb.append(ITEMS_SEPARATOR);
            }
            sb.append(strToMultiply);
        }
        return sb.toString();
    }
}
