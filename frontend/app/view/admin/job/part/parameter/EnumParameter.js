/**
 * Компонент для редактирования параметра типа "перечисление"
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.EnumParameter', {

    extend: 'Unidata.view.admin.job.part.parameter.AbstractParameter',

    alias: 'widget.admin.job.param.enum',

    requires: [
        'Ext.form.field.ComboBox'
    ],

    TYPE: 'ENUM',

    initInput: function (name, value) {

        /** @type {Unidata.model.job.parameter.meta.EnumMetaParameter} */
        var meta = this.getParameter().getMeta();

        return Ext.widget({
            xtype: 'combo',
            name: name,
            value: value,
            msgTarget: 'under',
            queryMode: 'local',
            editable: false,
            valueField: 'value',
            displayField: 'name',
            store: {
                fields: [
                    'value',
                    'name'
                ],
                data: meta.getValues()
            }
        });
    }

});
