/**
 * Поле ввода константы DQ порта целочисленного типа
 *
 * @author Sergey Shishigin
 * @date 2018-03-12
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.IntegerField', {
    extend: 'Ext.form.field.Number',

    alias: 'widget.admin.entity.metarecord.dq.dqrule.port.constant.integerfield',

    submitLocaleSeparator: false,

    config: {
        changeFn: null
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
    },

    clearValue: function () {
        this.setValue(null);
    }
});
