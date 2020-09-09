/**
 * Поле ввода константы DQ порта логического типа
 *
 * @author Sergey Shishigin
 * @date 2018-03-12
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.BooleanField', {
    extend: 'Ext.form.field.ComboBox',

    alias: 'widget.admin.entity.metarecord.dq.dqrule.port.constant.booleanfield',

    config: {
        changeFn: null
    },

    valueField: 'value',
    displayField: 'name',
    editable: false,

    store: {
        fields: [
            'name',
            'value'
        ],
        data: [
            {
                name: Unidata.i18n.t('common:yes'),
                value: true
            },
            {
                name: Unidata.i18n.t('common:no'),
                value: false
            }
        ]
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initListeners();
    },

    initListeners: function () {
        var changeFn = this.getChangeFn();

        // используем changeFn в качестве обработчика события change
        if (changeFn) {
            this.on('change', changeFn);
        }
    }
});
