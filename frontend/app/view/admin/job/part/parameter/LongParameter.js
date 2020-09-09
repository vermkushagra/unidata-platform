/**
 * Компонент для редактирования параметра типа "число"
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.LongParameter', {

    extend: 'Unidata.view.admin.job.part.parameter.AbstractParameter',

    alias: 'widget.admin.job.param.long',

    requires: [
        'Ext.form.field.Number'
    ],

    TYPE: 'LONG',

    initInput: function (name, value) {
        return Ext.widget({
            xtype: 'numberfield',
            name: name,
            value: value,
            msgTarget: 'under',
            allowDecimals: false
        });
    }

});
