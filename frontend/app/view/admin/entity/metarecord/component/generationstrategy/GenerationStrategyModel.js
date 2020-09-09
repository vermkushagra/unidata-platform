Ext.define('Unidata.view.admin.entity.metarecord.component.generationstrategy.GenerationStrategyModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.component.generationstrategy',

    data: {
        readOnly: false,
        generationStrategy: null,
        generationStrategyType: null
    },

    formulas: {
        isGenerationStrategyTypeConcat: {
            bind: {
                generationStrategyType: '{generationStrategyType}'
            },
            get: function (getter) {
                var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
                    generationStrategyType = getter.generationStrategyType;

                return generationStrategyType === GenerationStrategy.generationStrategyType.CONCAT.value;
            }
        }
    }
});
