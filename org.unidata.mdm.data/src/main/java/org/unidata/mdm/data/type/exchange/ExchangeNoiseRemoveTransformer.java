/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package org.unidata.mdm.data.type.exchange;

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
