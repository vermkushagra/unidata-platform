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
