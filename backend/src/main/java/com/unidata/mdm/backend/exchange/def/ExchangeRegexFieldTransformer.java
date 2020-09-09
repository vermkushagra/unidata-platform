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
package com.unidata.mdm.backend.exchange.def;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Mikhail Mikhailov
 * Regex field transformer.
 */
public class ExchangeRegexFieldTransformer extends ExchangeFieldTransformer {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 6379228555373081371L;
    /**
     * Regex array to try.
     */
    private List<String> split;
    /**
     * Matching (non-empty) part to take.
     */
    private int part;
    /**
     * Skip empty or not.
     */
    private boolean skipEmpty;
    /**
     * Patterns.
     */
    private List<Pattern> paterns = new ArrayList<>();

    /**
     * Ctor.
     */
    public ExchangeRegexFieldTransformer() {
        super();
    }
    /**
     * @return the split
     */
    public List<String> getSplit() {
        return split;
    }
    /**
     * @param split the split to set
     */
    public void setSplit(List<String> split) {
        this.split = split;
        this.paterns.clear();

        if (this.split == null || this.split.size() == 0) {
            return;
        }

        for (String re : this.split) {
            this.paterns.add(Pattern.compile(re));
        }
    }
    /**
     * @return the part
     */
    public int getPart() {
        return part;
    }
    /**
     * @param part the part to set
     */
    public void setPart(int part) {
        this.part = part;
    }
    /**
     * @return the skipEmpty
     */
    public boolean isSkipEmpty() {
        return skipEmpty;
    }
    /**
     * @param skipEmpty the skipEmpty to set
     */
    public void setSkipEmpty(boolean skipEmpty) {
        this.skipEmpty = skipEmpty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transform(String input) {
        if (input != null) {
            for (Pattern p : this.paterns) {
                String[] parts = p.split(input);

                // No match, continue.
                if (parts.length == 1 && parts[0].length() == input.length()) {
                    continue;
                }

                int hits = 0;
                for (int i = 0; i < parts.length; i++) {
                    String groupValue = parts[i];
                    if (groupValue.trim().length() == 0 && this.skipEmpty) {
                        continue;
                    }

                    if (part == hits++) {
                        return groupValue.trim();
                    }
                }
            }
        }

        return null;
    }

}
