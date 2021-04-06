/**
 * Модель стратегии генерации ключей (external id, code attribute)
 */
Ext.define('Unidata.model.entity.GenerationStrategy', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: '@type',
            type: 'string'
        },
        {
            name: 'attributes',
            type: 'auto'
        },
        {
            name: 'separator',
            type: 'string'
        }
    ],

    validators: {
        separator: [
            {
                type: 'generatestrategyseparator',
                message: Unidata.i18n.t('admin.metamodel>generationStrategy.separatorRequired')
            }
        ],
        attributes: [
            {
                type: 'generatestrategyattributes',
                message: Unidata.i18n.t('admin.metamodel>generationStrategy.attributesRequired')
            }
        ]
    },

    statics: {
        generationStrategyType: {
            NONE: {
                displayName: Unidata.i18n.t('admin.metamodel>generationStrategy.typeNone'),
                value: 'NONE'
            },
            RANDOM: {
                displayName: Unidata.i18n.t('admin.metamodel>generationStrategy.typeRandom'),
                value: 'RANDOM'
            },
            SEQUENCE: {
                displayName: Unidata.i18n.t('admin.metamodel>generationStrategy.typeSequence'),
                value: 'SEQUENCE'
            },
            CONCAT: {
                displayName: Unidata.i18n.t('admin.metamodel>generationStrategy.typeConcat'),
                value: 'CONCAT'
            }
        }
    }
});
