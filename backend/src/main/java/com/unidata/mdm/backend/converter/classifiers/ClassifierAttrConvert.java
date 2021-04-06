package com.unidata.mdm.backend.converter.classifiers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.classifier.ClassifierValueDef;

/**
 * The Class ClassifierAttrConvert.
 */
@ConverterQualifier
@Component
public class ClassifierAttrConvert implements Converter<ClsfNodeAttrDTO, ClassifierValueDef> {
	private static final String SDF_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String SDF_TIME = "HH:mm:ss";
	private static final String SDF_DATE = "yyyy-MM-dd";
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.core.convert.converter.Converter#convert(java.lang.
	 * Object)
	 */
	@Override
	public ClassifierValueDef convert(ClsfNodeAttrDTO source) {
		if (source.getDataType() == null) {
			throw new SystemRuntimeException("data type of classifier attribute should be defined",
					ExceptionId.EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT);
		}

		ClassifierValueDef target = JaxbUtils.getClassifierObjectFactory().createClassifierValueDef();
		target.setPath(source.getAttrName());
		switch (source.getDataType()) {
		case BOOLEAN:
			target.withBoolValue(toBoolean(source.getDefaultValue()));
			break;
		case DATE:
			target.withDateValue(toDate(source.getDefaultValue()));
			break;
		case TIME:
			target.withTimeValue(toTime(source.getDefaultValue()));
			break;
		case TIMESTAMP:
			target.withTimestampValue(toTimestamp(source.getDefaultValue()));
			break;
		case INTEGER:
			target.withIntValue(toLong(source.getDefaultValue()));
			break;
		case NUMBER:
			target.withNumberValue(toDouble(source.getDefaultValue()));
			break;
		case STRING:
			target.withStringValue((String) source.getDefaultValue());
			break;
		case BLOB:
			throw new SystemRuntimeException("BLOB data type is not supported!",
					ExceptionId.EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT);
		case CLOB:
			throw new SystemRuntimeException("CLOB data type is not supported!",
					ExceptionId.EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT);
		default:
			break;
		}
		return target;
	}


	/**
	 * Object to boolean, if required.
	 * @param o object
	 * @return
	 */
	protected Boolean toBoolean(Object o) {
		return o == null
				? null
				: Boolean.class.isAssignableFrom(o.getClass())
				? (Boolean) o
				: Boolean.valueOf(o.toString());
	}

	/**
	 * Object to long, if required.
	 * @param o object
	 * @return
	 */
	protected Long toLong(Object o) {
		return o == null
				? null
				: Number.class.isAssignableFrom(o.getClass())
				? ((Number) o).longValue()
				: Long.valueOf(o.toString());
	}

	/**
	 * Object to boolean, if required.
	 * @param o object
	 * @return
	 */
	protected Double toDouble(Object o) {
		return o == null
				? null
				: Number.class.isAssignableFrom(o.getClass())
				? ((Number) o).doubleValue()
				: Double.valueOf(o.toString());
	}

	/**
	 * Object to boolean, if required.
	 * @param o object
	 * @return
	 */
	protected String toString(Object o) {
		return o == null
				? null
				: String.class.isAssignableFrom(o.getClass())
				? (String) o
				: o.toString();
	}

	/**
	 * Object to boolean, if required.
	 *
	 * @param o
	 *            object
	 * @return the XML gregorian calendar
	 */
	protected LocalDate toDate(Object o) {
		if (o == null) {
			return null;
		} else if (Calendar.class.isAssignableFrom(o.getClass())) {
			return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} else if (Date.class.isAssignableFrom(o.getClass())) {
			return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} else if (String.class.isAssignableFrom(o.getClass())) {
			return LocalDate.parse(o.toString(), DateTimeFormatter.ofPattern(SDF_DATE));
		}

		return null;
	}

	/**
	 * Object to boolean, if required.
	 *
	 * @param o
	 *            object
	 * @return the XML gregorian calendar
	 */
	protected LocalTime toTime(Object o) {
		if (o == null) {
			return null;
		} else if (Calendar.class.isAssignableFrom(o.getClass())) {
			return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
		} else if (Date.class.isAssignableFrom(o.getClass())) {
			return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
		} else if (String.class.isAssignableFrom(o.getClass())) {
			return LocalTime.parse(o.toString(), DateTimeFormatter.ofPattern(SDF_TIME));
		}

		return null;
	}

	/**
	 * Object to boolean, if required.
	 *
	 * @param o
	 *            object
	 * @return the XML gregorian calendar
	 */
	protected LocalDateTime toTimestamp(Object o) {
		if (o == null) {
			return null;
		} else if (Calendar.class.isAssignableFrom(o.getClass())) {
			return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} else if (Date.class.isAssignableFrom(o.getClass())) {
			return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} else if (String.class.isAssignableFrom(o.getClass())) {
			return LocalDateTime.parse(o.toString(), DateTimeFormatter.ofPattern(SDF_DATE_TIME));
		}

		return null;
	}

}
