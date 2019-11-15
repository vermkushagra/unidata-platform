package org.unidata.mdm.core.dao.tokenizer;

import java.util.function.BiConsumer;

import org.unidata.mdm.core.po.ObjectPO;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface RowTokenizerField<V extends ObjectPO> extends BiConsumer<String, V> {
    @SafeVarargs
    static<T extends ObjectPO> RowTokenizerField<T>[] fields(RowTokenizerField<T>... fields) {
        return fields;
    }
}
