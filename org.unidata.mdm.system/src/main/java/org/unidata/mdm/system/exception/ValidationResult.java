package org.unidata.mdm.system.exception;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author Mikhail Mikhailov
 * The validation result.
 */
public class ValidationResult {
	/**
     * The system message.
     */
    private final String systemMessage;
    /**
     * Translation code.
     */
    private final String translationCode;
    /**
     * Arguments.
     */
    private final Object[] args;
    /**
     * Nested validations.
     */
    private final Collection<ValidationResult> nestedValidations;
    /**
     * Constructor.
     * @param systemMessage the system message
     * @param translationCode the translation code
     * @param args the arguments
     */
    public ValidationResult(String systemMessage, String translationCode, Object... args) {
        super();
        this.systemMessage = systemMessage;
        this.translationCode = translationCode;
        this.args = args;
        this.nestedValidations = Collections.emptyList();
    }

    /**
     * Constructor.
     *
     * @param nestedValidations the nested validations
     * @param systemMessage     the system message
     * @param translationCode   the translation code
     * @param args              the arguments
     */
    public ValidationResult(String systemMessage, Collection<ValidationResult> nestedValidations,
            String translationCode, Object... args) {
        super();
        this.systemMessage = systemMessage;
        this.translationCode = translationCode;
        this.args = args;
        this.nestedValidations = nestedValidations;
    }

    /**
     * Constructor.
     *
     * @param nestedValidation the nested validation
     * @param systemMessage    the system message
     * @param translationCode  the translation code
     * @param args             the arguments
     */
    public ValidationResult(String systemMessage, ValidationResult nestedValidation, String translationCode,
            Object... args) {
        super();
        this.systemMessage = systemMessage;
        this.translationCode = translationCode;
        this.args = args;
        this.nestedValidations = Collections.singletonList(nestedValidation);
    }

    /**
     * @return the systemMessage
     */
    public String getSystemMessage() {

        String mainMessage = "";
        if (args != null && args.length > 0) {
            mainMessage = MessageFormatter.arrayFormat(systemMessage, args).getMessage();
        } else {
            mainMessage = systemMessage;
        }

        if(!getNestedValidations().isEmpty()){
            mainMessage = mainMessage + " Nested errors: [";
            return getNestedValidations().stream()
                                         .map(ValidationResult::getSystemMessage)
                                         .collect(Collectors.joining("\n\t", mainMessage, "]"));
        } else {
            return mainMessage;
        }
    }
    /**
     * @return the translationCode
     */
    public String getTranslationCode() {
        return translationCode;
    }

    /**
     * @return nested validations
     */
    public Collection<ValidationResult> getNestedValidations() {
        return nestedValidations;
    }

    /**
     * @return the args
     */
    public Object[] getArgs() {
        return args;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result + ((systemMessage == null) ? 0 : systemMessage.hashCode());
		result = prime * result + ((translationCode == null) ? 0 : translationCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidationResult other = (ValidationResult) obj;
		if (!Arrays.equals(args, other.args))
			return false;
		if (systemMessage == null) {
			if (other.systemMessage != null)
				return false;
		} else if (!systemMessage.equals(other.systemMessage))
			return false;
		if (translationCode == null) {
			if (other.translationCode != null)
				return false;
		} else if (!translationCode.equals(other.translationCode))
			return false;
		return true;
	}

}
