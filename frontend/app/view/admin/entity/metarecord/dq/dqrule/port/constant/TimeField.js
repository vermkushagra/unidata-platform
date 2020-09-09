/**
 * Поле ввода константы DQ порта типа "Время"
 *
 * @author Sergey Shishigin
 * @date 2018-04-20
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.TimeField', {
    extend: 'Unidata.view.component.TimeTextField',

    alias: 'widget.admin.entity.metarecord.dq.dqrule.port.constant.timefield',

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
