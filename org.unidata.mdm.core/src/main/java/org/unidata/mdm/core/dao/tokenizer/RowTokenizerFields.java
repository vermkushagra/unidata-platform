package org.unidata.mdm.core.dao.tokenizer;

import java.util.function.BiConsumer;

import org.unidata.mdm.core.po.ObjectPO;

/**
 * @author Mikhail Mikhailov
 * Field parse interface.
 */
public interface RowTokenizerFields<T extends ObjectPO> {
    /**
     * The field consumer.
     * @return consumer
     */
    BiConsumer<String, T> consumer();
}
