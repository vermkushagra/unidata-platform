package com.unidata.mdm.backend.api.rest.dto.clsf;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.meta.AbstractAttributeDefinition;
import com.unidata.mdm.backend.api.rest.util.serializer.ClassifierAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.ClassifierAttributeSerializer;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;

import javassist.bytecode.stackmap.BasicBlock.Catch;

/**
 * The Class ClassifierAttributeRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ClassifierAttributeDeserializer.class)
@JsonSerialize(using = ClassifierAttributeSerializer.class)
public class ClsfNodeAttrRO extends AbstractAttributeDefinition {

	/**
	 * Date format without milliseconds. Frontend specific.
	 */
	public static final FastDateFormat DEFAULT_TIMESTAMP_NO_MS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

	/** Default or final value of attribute for classifier. */
	private Object value;

	/** Can be null. */
	private boolean nullable = true;

	/** Should be unique. */
	private boolean unique = false;

	/**
	 * Attribute is generally searchable.
	 */
	private boolean searchable = false;
	/**
	 * data type.
	 */
	private SimpleDataType simpleDataType;

	/**
	 * Gets the simple data type.
	 *
	 * @return the simple data type
	 */
	public SimpleDataType getSimpleDataType() {
		return simpleDataType;
	}

	/**
	 * Sets the simple data type.
	 *
	 * @param simpleDataType
	 *            the new simple data type
	 */
	public void setSimpleDataType(SimpleDataType simpleDataType) {
		this.simpleDataType = simpleDataType;
	}

	/**
	 * Parses string representation of date according to date format from
	 * {@see DEFAULT_TIMESTAMP_NO_MS}.
	 *
	 * @param dateAsString
	 *            string representation of date.
	 * @return parsed date.
	 * @throws ParseException
	 *             the parse exception
	 */
	private static Date parse(String dateAsString) throws ParseException {
		return dateAsString != null ? DEFAULT_TIMESTAMP_NO_MS.parse(dateAsString) : null;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 */
	public void setValue(String value) {
		if (value == null) {
			this.value = null;
			return;
		}
		switch (getSimpleDataType()) {
		case BOOLEAN:
			try {
				this.value = Boolean.parseBoolean(value);
			} catch (Exception ignored) {
				throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
						name, getSimpleDataType());
			}
			break;
		case DATE:
		case TIME:
		case TIMESTAMP:
			try {
				this.value = parse(value);
			} catch (ParseException ignored) {
				throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
						name, getSimpleDataType());
			}
			break;
		case INTEGER:
			try {
				this.value = Long.parseLong(value);
			} catch (Exception ignored) {
				throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
						name, getSimpleDataType());
			}

			break;
		case NUMBER:
			try {
				this.value = Double.parseDouble(value);
			} catch (Exception ignored) {
				throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
						name, getSimpleDataType());
			}
			break;
		case STRING:
			try {
				this.value = value;
			} catch (Exception ignored) {
				throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
						name, getSimpleDataType());
			}
			break;
		default:
			throw new RuntimeException("Unsupported data type: " + getSimpleDataType().name());
		}
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 */
	public void setValueObj(Object value) {
		this.value = value;
	}

	/**
	 * Checks if is nullable.
	 *
	 * @return true, if is nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Sets the nullable.
	 *
	 * @param nullable
	 *            the new nullable
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * Checks if is unique.
	 *
	 * @return true, if is unique
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * Sets the unique.
	 *
	 * @param unique
	 *            the new unique
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * Checks if is searchable.
	 *
	 * @return true, if is searchable
	 */
	public boolean isSearchable() {
		return searchable;
	}

	/**
	 * Sets the searchable.
	 *
	 * @param searchable
	 *            the new searchable
	 */
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

}
