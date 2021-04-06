/**
 * Список правил сопоставления
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.list.RuleList', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.duplicates.rulelist',

    requires: [
        'Unidata.view.admin.duplicates.list.RuleListController',
        'Unidata.view.admin.duplicates.list.RuleListModel',

        'Unidata.view.admin.duplicates.group.GroupListEditor'
    ],

    controller: 'admin.duplicates.rulelist',
    viewModel: {
        type: 'admin.duplicates.rulelist'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateMetaRecord'
        },
        {
            method: 'getRules'
        },
        {
            method: 'setSelectedRule'
        },
        {
            method: 'removeRuleFromList'
        },
        {
            method: 'addNewRule'
        }
    ],

    //cls: 'un-duplicates-rulelist',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        metaRecord: null,        // модель реестра / справочника
        currentRule: null        // выбранное и загруженное правило
    },

    referenceHolder: true,

    ruleList: null,              // список правил

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.ruleList = this.lookupReference('ruleList');
    },

    onDestroy: function () {
        var me = this;

        me.ruleList = null;

        me.callParent(arguments);
    },

    items: [
        {
            xtype: 'grid',
            reference: 'ruleList',
            sortableColumns: false,
            hideHeaders: true,
            flex: 1,
            bind: {
                store: '{ruleListStore}'
            },
            columns: [
                {
                    dataIndex: 'name',
                    flex: 1
                },
                {
                    xtype: 'un.actioncolumn',
                    width: 25,
                    hideable: false,
                    items: [
                        {
                            faIcon: 'trash-o',
                            handler: 'onDeleteRuleButtonClick',
                            isDisabled: 'isDeleteRuleButtonDisabled'
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
                            handler: 'onAddRuleButtonClick',
                            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:rule')}),
                            securedResource: 'ADMIN_MATCHING_MANAGEMENT',
                            securedEvent: 'create'
                        },
                        {
                            xtype: 'button',
                            ui: 'un-toolbar-admin',
                            scale: 'small',
                            iconCls: 'icon-folder',
                            handler: 'onRuleGroupButtonClick',
                            tooltip: Unidata.i18n.t('admin.duplicates>ruleGroups')
                        },
                        '->',
                        {
                            xtype: 'button',
                            ui: 'un-toolbar-admin',
                            scale: 'small',
                            iconCls: 'icon-download2',
                            handler: 'onRuleExportButtonClick',
                            tooltip: Unidata.i18n.t('admin.duplicates>exportRules')
                        },
                        {
                            xtype: 'button',
                            ui: 'un-toolbar-admin',
                            scale: 'small',
                            iconCls: 'icon-upload2',
                            handler: 'onRuleImportButtonClick',
                            tooltip: Unidata.i18n.t('admin.duplicates>importRules'),
                            securedResource: 'ADMIN_MATCHING_MANAGEMENT',
                            securedEvent: 'create'
                        }
                    ]
                }
            ],
            listeners: {
                select: 'onRuleSelect',
                deselect: 'onRuleDeselect',
                beforeselect: 'onRuleBeforeSelect'
            }
        }
    ]
});
