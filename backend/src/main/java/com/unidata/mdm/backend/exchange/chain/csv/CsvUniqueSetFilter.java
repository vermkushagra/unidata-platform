package com.unidata.mdm.backend.exchange.chain.csv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.csv.CsvExchangeField;

/**
 * @author Mikhail Mikhailov
 * Support for importing of lookup entities.
 */
public class CsvUniqueSetFilter implements Predicate<List<String>> {

    /**
     * Values, already put.
     */
    private Set<String> values = new HashSet<String>();

    /**
     * Code attribute
     */
    private List<CsvExchangeField> codeAttrs = new ArrayList<>();

    /**
     * Unique record filter. Not thread safe.
     * @param e entity import description
     * @param isLookup entity is a lookup entity
     */
    public CsvUniqueSetFilter(ExchangeEntity e, boolean isLookup) {
        for (ExchangeField f : e.getFields()) {
            if (isLookup) {
                if (f.isCodeAttribute()) {
                    codeAttrs.add((CsvExchangeField) f);
                    break;
                }
            } else {
                codeAttrs.add((CsvExchangeField) f);
            }
        }

        if (codeAttrs.isEmpty()) {
            throw new IllegalArgumentException("Entity ["
                    + e.getName() + "] doesn't contain code attribute!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(List<String> t) {
        StringBuilder idBuilder = new StringBuilder();
        for (CsvExchangeField f : codeAttrs) {
            idBuilder.append(t.get(f.getIndex()).trim());
        }

        return values.add(idBuilder.toString());
    }

}