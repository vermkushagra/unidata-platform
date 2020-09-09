Ext.define('Unidata.model.dataquality.DqRaise', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'functionRaiseErrorPort',
            type: 'string'
        },
        {
            name: 'phase',
            type: 'string',
            defaultValue: 'BEFORE_UPSERT'
        },
        {
            name: 'messagePort',
            type: 'string',
            defaultValue: null,
            allowNull: true
        },
        {
            name: 'messageText',
            type: 'string',
            defaultValue: null,
            allowNull: true,
            serialize: function (value) {
                if (value === '') {
                    value = null;
                }

                return value;
            }
        },
        {
            name: 'severityPort',
            type: 'string',
            defaultValue: null,
            allowNull: true
        },
        {
            name: 'severityValue',
            type: 'string',
            defaultValue: null,
            allowNull: true,
            serialize: function (value) {
                if (value === '') {
                    value = null;
                }

                return value;
            }
        },
        {
            name: 'categoryPort',
            type: 'string',
            defaultValue: null,
            allowNull: true
        },
        {
            name: 'categoryText',
            type: 'string',
            defaultValue: null,
            allowNull: true,
            serialize: function (value) {
                if (value === '') {
                    value = null;
                }

                return value;
            }
        }
    ],

    statics: {
        getPhasesList: function () {
            return [
                {text: Unidata.i18n.t('model>beforeSaveData'), value: 'BEFORE_UPSERT'},
                {text: Unidata.i18n.t('model>afterSaveData'), value: 'AFTER_UPSERT'}
            ];
        },
        getSeverityList: function () {
            return [
                {text: Unidata.i18n.t('model>criticalFemale'), value: 'CRITICAL'},
                {text: Unidata.i18n.t('model>highFemale'), value: 'HIGH'},
                {text: Unidata.i18n.t('model>normalFemale'), value: 'NORMAL'},
                {text: Unidata.i18n.t('model>lowFemale'), value: 'LOW'}
            ];
        }
    },
    validators: {
        functionRaiseErrorPort: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>portShouldSelect')
            }
        ],
        phase: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>phaseShouldSelect')
            }
        ],
        severityValue: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>fieldcriticalnessRequired')
            }
        ]
    }
});
