Ext.define('Unidata.model.dashboard.ValidationError', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'severity',
            type: 'string'
        },
        {
            name: 'count',
            type: 'int'
        }
    ],

    statics: {
        getSeverityTypes: function () {
            var severityTypes = {
                low: Unidata.i18n.t('model>low'),
                normal: Unidata.i18n.t('model>normal'),
                high: Unidata.i18n.t('model>high'),
                critical: Unidata.i18n.t('model>critical')
            };

            return severityTypes;
        }
    }
});
