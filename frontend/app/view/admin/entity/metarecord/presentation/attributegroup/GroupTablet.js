/**
 * Таблетка для группы атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-05-27
 */

Ext.define('Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupTablet', {
    extend: 'Ext.panel.Panel',
    //extend: 'Ext.container.Container',

    alias: 'widget.admin.entity.metarecord.presentation.attributegroup.tablet',

    referenceHolder: true,

    attributes: null,        // массив атрибутов

    config: {
        readOnly: false
    },

    removeGroupButton: null, // ссылка на кнопку удаления группы
    moveButton: null,        // ссылка на кнопку перемещения
    attributeGrid: null,     // ссылка на грид
    groupTitle: null,        // ссылка на компонент с заголовком

    cls: 'un-entity-grouptablet',
    ui: 'un-presentation-panel',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: Unidata.i18n.t('admin.metamodel>attributesGroup'),

    items: [
        {
            xtype: 'textfield',
            reference: 'groupTitle',
            fieldLabel: Unidata.i18n.t('admin.metamodel>groupNaming'),
            emptyText: Unidata.i18n.t('admin.metamodel>setGroupNaming'),
            labelAlign: 'top'
        },
        {
            xtype: 'grid',
            reference: 'attributeGrid',
            title: Unidata.i18n.t('glossary:attributes'),
            hideHeaders: true,
            sortableColumns: false,
            minHeight: 100,
            columns: [
                {
                    text: Unidata.i18n.t('glossary:naming'),
                    dataIndex: 'displayName',
                    menuDisabled: true,
                    flex: 1
                }
            ],
            store: {
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
            },
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    pluginId: 'ddplugin',
                    ddGroup: 'attributeGroupsDDGroup',
                    dragText: Unidata.i18n.t('admin.metamodel>moveAttribute')
                }
            }
        }
    ],

    tools: [
        {
            xtype: 'button',
            reference: 'moveButton',
            iconCls: 'icon-expand3',
            scale: 'small',
            ui: 'un-toolbar-block-panel',
            text: '',
            menu: {
                items: [
                    {
                        text: Unidata.i18n.t('common:up'),
                        direction: 'up'
                    },
                    {
                        text: Unidata.i18n.t('common:down'),
                        direction: 'down'
                    },
                    {
                        text: Unidata.i18n.t('common:right'),
                        direction: 'right'
                    },
                    {
                        text: Unidata.i18n.t('common:left'),
                        direction: 'left'
                    }
                ]
            }
        },
        {
            xtype: 'button',
            ui: 'un-toolbar-block-panel',
            scale: 'small',
            reference: 'removeGroupButton',
            text: '',
            tooltip: Unidata.i18n.t('admin.duplicates>removeGroup'),
            iconCls: 'icon-trash2'
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();

        this.groupTitle.setValue(this.title);
        this.setTitle(Ext.util.Format.htmlEncode(this.title));

        if (this.attributes) {
            this.attributeGrid.getStore().add(this.attributes);
            this.attributes = null;
        }
    },

    onDestroy: function () {
        this.removeGroupButton = null;
        this.moveButton = null;
        this.attributeGrid = null;
        this.groupTitle = null;

        this.callParent(arguments);
    },

    onRender: function () {
        this.callParent(arguments);

        this.removeGroupButton = this.lookupReference('removeGroupButton');
        this.moveButton        = this.lookupReference('moveButton');

        this.removeGroupButton.on('click', this.onRemoveGroupClick, this);
        this.moveButton.getMenu().on('click', this.onMoveButtonItemClick, this);
    },

    initComponentReference: function () {
        this.attributeGrid = this.lookupReference('attributeGrid');
        this.groupTitle    = this.lookupReference('groupTitle');
    },

    initComponentEvent: function () {
        var gridView;

        gridView = this.attributeGrid.getView();

        gridView.on('drop', this.onDropNode, this);

        this.groupTitle.on('change', this.onChangeTitle, this);
    },

    onRemoveGroupClick: function () {
        var title = Unidata.i18n.t('admin.metamodel>removeAttributeGroup'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmRemoveGroup');

        Unidata.util.UserDialog.showPrompt(title, msg, this.onConfirmRemoveGroup, this);
    },

    onMoveButtonItemClick: function (menu, item) {
        this.fireEvent('movegroup', this, item.direction);

        this.fireEvent('changegroup', this);
    },

    onConfirmRemoveGroup: function () {
        var store   = this.attributeGrid.getStore(),
            records = store.getRange();

        if (this.fireEvent('beforeremovegroup', this, records) !== false) {
            this.fireEvent('removegroup', this, records);
        }
    },

    getTabletInfo: function () {
        var store   = this.attributeGrid.getStore(),
            records = store.getRange(),
            info;

        info = {
            title: this.groupTitle.getValue(),
            records: records,
            attributes: []
        };

        Ext.Array.each(records, function (record) {
            info.attributes.push(record.get('name'));
        });

        return info;
    },

    onDropNode: function () {
        this.fireEvent('changegroup', this);
    },

    onChangeTitle: function () {
        this.fireEvent('changegroup', this);
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

        this.moveButton.setDisabled(readOnly);
        this.removeGroupButton.setDisabled(readOnly);

        plugin = this.attributeGrid.getView().getPlugin('ddplugin');

        if (readOnly) {
            plugin.dragZone.lock();
            plugin.dropZone.lock();
        } else {
            plugin.dragZone.unlock();
            plugin.dropZone.unlock();
        }
    }
});
