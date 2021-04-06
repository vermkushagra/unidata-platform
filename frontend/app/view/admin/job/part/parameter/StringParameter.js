/**
 * Компонент для редактирования параметра типа "строка"
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.StringParameter', {

    extend: 'Unidata.view.admin.job.part.parameter.AbstractParameter',

    alias: 'widget.admin.job.param.string',

    requires: [
        'Ext.form.field.Text'
    ],

    TYPE: 'STRING',

    initInput: function (name, value) {
        return Ext.widget({
            xtype: 'textfield',
            name: name,
            value: value,
            flex: 1,
            msgTarget: 'under'
        });
    }

});
