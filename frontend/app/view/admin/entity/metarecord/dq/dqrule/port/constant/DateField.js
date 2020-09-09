/**
 * Поле ввода константы DQ порта типа "Дата"
 *
 * @author Sergey Shishigin
 * @date 2018-03-12
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DateField', {
    extend: 'Ext.form.field.Date',

    alias: 'widget.admin.entity.metarecord.dq.dqrule.port.constant.datefield',

    config: {
        changeFn: null
    },

    submitFormat: 'Y-m-d\\TH:i:s.u',
    altFormats: 'm/d/Y|n/j/Y|n/j/y|m/j/y|n/d/y|m/j/Y|n/d/Y|m-d-y|m-d-Y|m/d|m-d|md|mdy|mdY|d|Y-m-d|n-j|n/j|Y-m-d\\TH:i:s.u',

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
