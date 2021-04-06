/**
 * Утилитный класс генератора стратегий
 *
 * @author Sergey Shishigin
 * @date 2017-11-03
 */

Ext.define('Unidata.util.GenerationStrategy', {
    singleton: true,

    /**
     * Выбрана ли у атрибута какая-нибудь стратегия генерации
     * @returns {*}
     */
    isAttributeHasGenerationStrategy: function (metaAttribute) {
        var GenerationStrategyType = Unidata.model.entity.GenerationStrategy.generationStrategyType,
            MetaAttributeUtil      = Unidata.util.MetaAttribute,
            idGenerationStrategyType,
            hasStrategy = false;

        if (MetaAttributeUtil.isCodeAttribute(metaAttribute)) {
            idGenerationStrategyType = metaAttribute.getIdGenerationStrategyType();

            if (idGenerationStrategyType && idGenerationStrategyType !== GenerationStrategyType.NONE.value) {
                hasStrategy = true;
            }
        }

        return hasStrategy;
    },

    /**
     * Используеься ли атрибут в стратегии генерации кодового атрибута
     * @returns {boolean}
     */
    isAttributeUsedInExternalIdGenerationStrategy: function (attributePath, metaRecord) {
        return this.isAttributeUsedInIdGenerationStrategy(attributePath, metaRecord);
    },

    /**
     * Используется ли атрибут в стратегии генерации внешнего ключа
     * @returns {boolean}
     */
    isAttributeUsedInCodeAttributeGenerationStrategy: function (attributePath, lookupEntity) {
        var MetaRecordUtil = Unidata.util.MetaRecord,
            codeAttribute;

        if (!MetaRecordUtil.isLookup(lookupEntity)) {
            return false;
        }
        codeAttribute = lookupEntity.getCodeAttribute();

        return this.isAttributeUsedInIdGenerationStrategy(attributePath, codeAttribute);
    },

    /**
     * Используется ли атрибут в стратегии генерации ключа
     * @private
     * @returns {boolean}
     */
    isAttributeUsedInIdGenerationStrategy: function (attributePath, strategyOwner) {
        var GenerationStrategyType = Unidata.model.entity.GenerationStrategy.generationStrategyType,
            used = false,
            idGenerationStrategyType,
            idGenerationStrategy,
            attributes;

        if (strategyOwner && Ext.isFunction(strategyOwner.getIdGenerationStrategyType)) {
            idGenerationStrategyType = strategyOwner.getIdGenerationStrategyType();
        }

        if (idGenerationStrategyType === GenerationStrategyType.CONCAT.value) {
            idGenerationStrategy = strategyOwner.getExternalIdGenerationStrategy();
            attributes = idGenerationStrategy.get('attributes');
            used = Ext.Array.contains(attributes, attributePath);
        }

        return used;
    }
});
