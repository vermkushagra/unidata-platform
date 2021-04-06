/**
 * Раздел редактирования backend properties
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.BackendProperties', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.beproperties',

    viewModel: {
        type: 'admin.beproperties'
    },
    controller: 'admin.beproperties',

    requires: [
        'Unidata.view.admin.beproperties.BackendPropertiesController',
        'Unidata.view.admin.beproperties.BackendPropertiesModel',
        'Unidata.view.admin.beproperties.editor.BackendPropertiesEditor'
    ],

    referenceHolder: true,

    backendPropertiesEditor: null,          // ссылка на компонент редактор backend properties

    layout: {
        type: 'fit',
        align: 'stretch'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
    },

    initReferences: function () {
        this.backendPropertiesEditor = this.lookupReference('backendPropertiesEditor');
    },

    onDestroy: function () {
        this.backendPropertiesEditor = null;

        this.callParent(arguments);
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            cls: 'right-toolbar',
            dock: 'right',
            width: 45,
            defaults: {
                xtype: 'button',
                ui: 'un-toolbar-admin',
                scale: 'medium'
            },
            items: [
                {
                    handler: 'onSaveBackendPropertiesButtonClick',
                    tooltip: Unidata.i18n.t('common:save'),
                    iconCls: 'icon-floppy-disk'
                }
            ]
        }
    ],

    items: [
        {
            xtype: 'admin.beproperties.editor',
            reference: 'backendPropertiesEditor'
        }
    ]
});
