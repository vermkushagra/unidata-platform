/**
 * @author Aleksandr Bavin
 * @date 2017-09-25
 */
Ext.define('Unidata.view.admin.entity.form.field.KeyValueInput', {

    extend: 'Unidata.view.component.form.field.gridvalues.GridValuesInput',

    alias: 'widget.keyvalue.input',

    gridColumnsDefaults: {
        flex: 1
    },

    gridColumns: [
        {
            text: Ext.String.capitalize(Unidata.i18n.t('admin.metamodel>key')),
            dataIndex: 'name',
            editor: {
                xtype: 'textfield',
                emptyText: Ext.String.capitalize(Unidata.i18n.t('admin.metamodel>key')),
                allowBlank: false
            }
        },
        {
            text: Ext.String.capitalize(Unidata.i18n.t('admin.metamodel>value')),
            dataIndex: 'value',
            editor: {
                xtype: 'textfield',
                emptyText: Ext.String.capitalize(Unidata.i18n.t('admin.metamodel>value'))
            }
        }
    ],

    onRender: function () {
        this.callParent(arguments);

        Ext.QuickTips.register({
            target: this.labelEl,
            showDelay: 1000,
            text: Unidata.i18n.t('component>customPropertiesTooltip')
        });
    }

});
