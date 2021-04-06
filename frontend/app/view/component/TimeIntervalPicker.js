/**
 * Time interval picker
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.TimeIntervalPicker', {
    extend: 'Ext.container.Container',

    xtype: 'timeintervalpicker',

    defaults: {
        xtype: 'datefield',
        readOnlyCls: 'readonly-textfield'
    },

    referenceHolder: true,

    items: [
        {
            fieldLabel: Unidata.i18n.t('common:from'),
            name: 'from',
            reference: 'validFromDatePicker'
        },
        {
            fieldLabel: Unidata.i18n.t('common:to'),
            name: 'to',
            reference: 'validToDatePicker'
        }
    ]
});
