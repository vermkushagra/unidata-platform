/**
 * Грид для отображения списка атрибутов
 *
 * @author Sergey Shishigin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.entity.metarecord.presentation.attributegroup.AttributeGrid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.admin.entity.metarecord.presentation.attributegroup.attributegrid',

    config: {
        readOnly: false
    },

    reference: 'attributeGrid',
    title: Unidata.i18n.t('glossary:attributes'),
    sortableColumns: false,
    cls: 'un-attribute-grid',
    maxWidth: 350,
    flex: 1,
    hideHeaders: true,
    emptyText: Unidata.i18n.t('common:noData'),
    deferEmptyText: false,

    viewConfig: {
        plugins: {
            ptype: 'gridviewdragdrop',
            pluginId: 'ddplugin',
            ddGroup: 'attributeGroupsDDGroup',
            dragText: Unidata.i18n.t('admin.metamodel>moveAttribute')
        }
    },

    columns: [
        {
            text: Unidata.i18n.t('glossary:naming'),
            dataIndex: 'displayName',
            menuDisabled: true,
            flex: 1
        }
    ],

    viewModel: {
        stores: {
            attributes: {
                fields: [
                    {
                        name: 'name',
                        type: 'string'
                    },
                    {
                        name: 'displayName',
                        type: 'string'
                    }
                ]
            }
        }
    },

    initComponent: function () {
        var view,
            store;

        this.callParent(arguments);

        view = this.getView();
        this.relayEvents(view, 'drop');
        store = this.createStore();
        this.setStore(store);

        view.on('boxready', this.onViewBoxReady, this);
    },

    onViewBoxReady: function () {
        this.syncReadOnly();
    },

    createStore: function () {
        var store;

        store = Ext.create('Ext.data.Store', {
            fields: [
                {
                    name: 'name',
                    type: 'string'
                },
                {
                    name: 'displayName',
                    type: 'string'
                }
            ]
        });

        return store;
    },

    updateReadOnly: function () {
        if (this.isConfiguring) {
            return;
        }

        this.syncReadOnly();
    },

    syncReadOnly: function () {
        var readOnly = this.getReadOnly(),
            plugin;

        plugin = this.getView().getPlugin('ddplugin');

        if (plugin.dragZone && plugin.dropZone) {
            if (readOnly) {
                plugin.dragZone.lock();
                plugin.dropZone.lock();
            } else {
                plugin.dragZone.unlock();
                plugin.dropZone.unlock();
            }
        }
    }
});
