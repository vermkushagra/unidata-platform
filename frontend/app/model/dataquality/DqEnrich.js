Ext.define('Unidata.model.dataquality.DqEnrich', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'sourceSystem',
            type: 'string'
        },
        {
            name: 'action',
            type: 'string',
            defaultValue: 'UPDATE_CURRENT'
        },
        {
            name: 'phase',
            type: 'string',
            defaultValue: 'BEFORE_UPSERT'
        }
    ],

    statics: {
        getPhasesList: function () {
            return [
                {text: Unidata.i18n.t('model>beforeSaveData'), value: 'BEFORE_UPSERT'}
            ];
        }
    },

    validators: {
        action: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>actionShouldSelect')
            }
        ],
        phase: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>dataHandlePhaseShouldSelect')
            }
        ]
    }

});
