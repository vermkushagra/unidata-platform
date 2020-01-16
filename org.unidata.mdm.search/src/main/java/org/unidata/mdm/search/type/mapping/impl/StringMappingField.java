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

package org.unidata.mdm.search.type.mapping.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * String.
 */
public final class StringMappingField extends AbstractValueMappingField<StringMappingField> {
    /**
     * Index case insensetive.
     */
    private boolean caseInsensitive;
    /**
     * Support morphological analysis.
     */
    private boolean morphologicalAnalysis;
    /**
     * Skip content analysis completely.
     */
    private boolean nonAnalyzable;
    /**
     * Constructor.
     */
    public StringMappingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.STRING;
    }
    /**
     * @return the caseInsensitive
     */
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }
    /**
     * @param caseInsensitive the caseInsensitive to set
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }
    /**
     * @return the morphologicalAnalysis
     */
    public boolean isMorphologicalAnalysis() {
        return morphologicalAnalysis;
    }
    /**
     * @param morphologicalAnalysis the morphologicalAnalysis to set
     */
    public void setMorphologicalAnalysis(boolean morphologicalAnalysis) {
        this.morphologicalAnalysis = morphologicalAnalysis;
    }
    /**
     * @return the nonAnalyzable
     */
    public boolean isNonAnalyzable() {
        return nonAnalyzable;
    }
    /**
     * @param nonAnalyzable the nonAnalyzable to set
     */
    public void setNonAnalyzable(boolean nonAnalyzable) {
        this.nonAnalyzable = nonAnalyzable;
    }
    /**
     * Sets field to be indexed case insensetive.
     * @param caseInsensetive the flag
     * @return self
     */
    public StringMappingField withCaseInsensitive(boolean caseInsensetive) {
        setCaseInsensitive(caseInsensetive);
        return this;
    }
    /**
     * Sets field to be indexed morphological analysis.
     * @param morphologicalAnalysis the flag
     * @return self
     */
    public StringMappingField withMorphologicalAnalysis(boolean morphologicalAnalysis) {
        setMorphologicalAnalysis(morphologicalAnalysis);
        return this;
    }
    /**
     * Sets field to be indexed without tokenization and analysis (support for TERM queries only).
     * @param nonAnalyzable the flag
     * @return self
     */
    public StringMappingField withNonAnalyzable(boolean nonAnalyzable) {
        setNonAnalyzable(nonAnalyzable);
        return this;
    }
}
