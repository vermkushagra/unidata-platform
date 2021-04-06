/**
 * Компонент для редактирования параметра типа "чекбокс"
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.BooleanParameter', {

    extend: 'Unidata.view.admin.job.part.parameter.AbstractParameter',

    alias: 'widget.admin.job.param.boolean',

    requires: [
        'Ext.form.field.Checkbox'
    ],

    TYPE: 'BOOLEAN',

    initInput: function (name, value) {
        return Ext.widget({
            xtype: 'checkbox',
            name: name,
            value: value,
            msgTarget: 'under'
        });
    }

});
