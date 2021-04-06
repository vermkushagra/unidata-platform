/**
 * Редактор маппинга правил в группы
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.group.GroupListEditor', {
    extend: 'Ext.window.Window',

    alias: 'widget.admin.duplicates.grouplisteditor',

    requires: [
        'Unidata.view.admin.duplicates.group.GroupListEditorController',
        'Unidata.view.admin.duplicates.group.GroupListEditorModel',

        'Unidata.view.admin.duplicates.group.GroupInfoWindow',
        'Unidata.view.admin.duplicates.group.GroupList'
    ],

    controller: 'admin.duplicates.grouplisteditor',
    viewModel: {
        type: 'admin.duplicates.grouplisteditor'
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

    title: Unidata.i18n.t('admin.duplicates>editRuleGroups'),

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    config: {
        rules: null,             // массив существующих правил
        metaRecord: null         // модель реестра / справочника
    },

    referenceHolder: true,

    groupTree: null,              // дерево правил
    ruleList: null,               // список существующих правил

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.groupTree = this.lookupReference('groupTree');
        me.ruleList  = this.lookupReference('ruleList');
    },

    onDestroy: function () {
        var me = this;

        me.groupTree = null;
        me.ruleList  = null;

        me.callParent(arguments);
    },

    items: [
        {
            xtype: 'grid',
            reference: 'ruleList',
            ui: 'card',
            flex: 1,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    ddGroup: 'ruleDDGroup',
                    dragText: Unidata.i18n.t('admin.duplicates>moveRule'),
                    enableDrag: true,
                    enableDrop: false
                }
            },
            sortableColumns: false,
            hideHeaders: true,
            columns: [
                {
                    dataIndex: 'name',
                    flex: 1
                }
            ],
            bind: {
                store: '{ruleListStore}'
            }
        },
        {
            xtype: 'admin.duplicates.grouplist',
            reference: 'groupTree',
            ui: 'card',
            flex: 1
        }
    ]
});
