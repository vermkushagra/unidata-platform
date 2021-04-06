/**
 *
 */
package com.unidata.mdm.backend.exchange.def;

import java.util.regex.Pattern;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ExchangeNoiseRemoveTransformer extends ExchangeFieldTransformer {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6237959543212971374L;
    /**
     * The regex.
     */
    private String regex;
    /**
     * The pattern.
     */
    private Pattern pattern;

    /**
     * Constructor.
     */
    public ExchangeNoiseRemoveTransformer() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transform(String input) {
        if (pattern != null) {
            return pattern.matcher(input).replaceAll("");
        }

        return input;
    }

    /**
     * @return the regex
     */
    public String getRegex() {
        return regex;
    }


    /**
     * @param regex the regex to set
     */
    public void setRegex(String pattern) {
        this.regex = pattern;
        if (this.regex == null) {
            this.pattern = null;
        } else {
            this.pattern = Pattern.compile(this.regex);
        }
    }

}
