/**
 updateRules *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.group.GroupList', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.duplicates.grouplist',

    requires: [
        'Unidata.view.admin.duplicates.group.GroupListController',
        'Unidata.view.admin.duplicates.group.GroupListModel',

        'Unidata.view.admin.duplicates.group.GroupInfoWindow',
        'Unidata.view.admin.duplicates.group.GroupTree'
    ],

    controller: 'admin.duplicates.grouplist',
    viewModel: {
        type: 'admin.duplicates.grouplist'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateMetaRecord'
        },
        {
            method: 'updateRules'
        }
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        metaRecord: null,                // модель реестра / справочника
        rules: null                      // список существующих правил
    },

    referenceHolder: true,

    groupTree: null,              // дерево правил
    treeGroupNameColumn: null,    // колонка с наименованием группы / правила

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.groupTree           = this.lookupReference('groupTree');
        me.treeGroupNameColumn = this.lookupReference('treeGroupNameColumn');
    },

    onDestroy: function () {
        var me = this;

        me.groupTree           = null;
        me.treeGroupNameColumn = null;

        me.callParent(arguments);
    },

    items: [
        {
            xtype: 'un.duplicategrouptree',
            reference: 'groupTree',
            flex: 1,
            sortableColumns: false,
            hideHeaders: true,
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    pluginId: 'ruleddplugin',
                    ddGroup: 'ruleDDGroup',
                    enableDrag: true,
                    enableDrop: true
                },
                listeners: {
                    nodedragover: 'onNodeDragOver',
                    beforedrop: 'onBeforeDropRule'
                }
            },
            columns: [
                {
                    xtype: 'treecolumn'
                },
                {
                    flex: 1,
                    reference: 'treeGroupNameColumn'
                },
                {
                    xtype: 'un.actioncolumn',
                    width: 25,
                    hideable: false,
                    items: [
                        {
                            faIcon: 'trash-o',
                            handler: 'onDeleteGroupActionClick',
                            isDisabled: 'isDeleteGroupButtonDisabled',
                            getTip: function (value, metadata, record) {
                                return record.isLeaf() ?
                                  Unidata.i18n.t('admin.duplicates>excludeRuleFromGroup') :
                                  Unidata.i18n.t('admin.duplicates>removeGroup');
                            }
                        }
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    height: 50,
                    items: [
                        {
                            xtype: 'button',
                            ui: 'un-toolbar-admin',
                            scale: 'small',
                            iconCls: 'icon-plus',
                            handler: 'onAddGroupButtonClick',
                            tooltip: Unidata.i18n.t('admin.duplicates>addGroup'),
                            securedResource: 'ADMIN_MATCHING_MANAGEMENT',
                            securedEvent: 'create'
                        }
                    ]
                }
            ],
            listeners: {
                itemdblclick: 'onGroupDoubleClick'
            }
        }
    ]
});
