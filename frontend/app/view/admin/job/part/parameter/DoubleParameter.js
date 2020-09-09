/**
 * Компонент для редактирования параметра типа "дробное число"
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.DoubleParameter', {

    extend: 'Unidata.view.admin.job.part.parameter.AbstractParameter',

    alias: 'widget.admin.job.param.double',

    requires: [
        'Ext.form.field.Number'
    ],

    TYPE: 'DOUBLE',

    initInput: function (name, value) {
        return Ext.widget({
            xtype: 'numberfield',
            name: name,
            value: value,
            msgTarget: 'under',
            allowDecimals: true
        });
    }

});
