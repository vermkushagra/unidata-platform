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
        var meta = this.getParameter().getMeta(),
            multiSelect = meta.getMultiSelect(),
            editable,
            xtype;

        if (multiSelect) {
            xtype = 'tagfield';
            editable = true;
        } else {
            xtype = 'combo';
            editable = false;
        }

        return Ext.widget({
            xtype: xtype,
            name: name,
            value: value,
            msgTarget: 'under',
            queryMode: 'local',
            editable: editable,
            width: 500,
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
    },

    onInputChange: function () {
        var meta = this.getParameter().getMeta(),
            multiSelect = meta.getMultiSelect();

        if (multiSelect) {
            this.onMultiselectInputChange();
        } else {
            this.callParent(arguments);
        }
    },

    onMultiselectInputChange: function () {
        var value = this.input.getValue(),
            orderedValue = [],
            store = this.input.getStore();

        store.each(function (record) {
            if (Ext.Array.contains(value, record.get('value'))) {
                orderedValue.push(record.get('value'));
            }
        });

        this.value = orderedValue;

        this.parameter.setValue(this.value);
    }
});
