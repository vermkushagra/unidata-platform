/**
 * Экран редактирования правил дубликатов для выбранного реестра / справочника
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.editor.EntityDuplicateEditor', {
    extend: 'Ext.Container',

    alias: 'widget.admin.duplicates.entityduplicateeditor',

    requires: [
        'Unidata.view.admin.duplicates.editor.EntityDuplicateEditorController',
        'Unidata.view.admin.duplicates.editor.EntityDuplicateEditorModel',

        'Unidata.view.admin.duplicates.item.RuleEdit',
        'Unidata.view.admin.duplicates.list.RuleList'
    ],

    controller: 'admin.duplicates.entityduplicateeditor',
    viewModel: {
        type: 'admin.duplicates.entityduplicateeditor'
    },

    referenceHolder: true,

    ruleList: null,              // компонент список правил
    ruleEditor: null,            // компонент редактор правила
    attributeTree: null,         // компонент дерево атрибутов

    config: {
        metaRecord: null         // модель реестра / справочника
    },

    cls: 'un-content-inner un-content-container',

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateMetaRecord'
        },
        {
            method: 'isEntityDuplicateEditorDirty'
        },
        {
            method: 'resetEntityDuplicateEditor'
        }
    ],

    componentCls: 'un-duplicates-editor',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.ruleList      = me.lookupReference('ruleList');
        me.ruleEditor    = me.lookupReference('ruleEditor');
        me.attributeTree = me.lookupReference('attributeTree');
    },

    onDestroy: function () {
        var me = this;

        me.ruleList      = null;
        me.ruleEditor    = null;
        me.attributeTree = null;

        me.callParent(arguments);
    },

    items: [
        {
            xtype: 'admin.duplicates.rulelist',
            reference: 'ruleList',
            ui: 'un-card',
            bodyPadding: '5',
            title: Unidata.i18n.t('admin.duplicates>rules'),
            width: 400,
            margin: 0,
            listeners: {
                ruleload: 'onRuleLoad',
                beforeruleselect: 'onBeforeRuleSelect',
                beforeruleadd: 'onBeforeRuleAdd',
                ruledeselect: 'onRuleDeselect',
                ruledelete: 'onRuleDelete'
            }
        },
        {
            xtype: 'component.attributeTree',
            reference: 'attributeTree',
            ui: 'un-card',
            title: Unidata.i18n.t('glossary:attributes'),
            overflowY: 'auto',
            width: 400,
            margin: '0 16',
            isComplexAttributesHidden: true,
            isArrayAttributesHidden: true,
            hideAttributeFilter: function (record) {
                if (record.get('linkDataType')) {
                    return true;
                }

                if (record.get('simpleDataType') == 'Blob' || record.get('simpleDataType') == 'Clob') {
                    return true;
                }

                return false;
            },
            viewConfig: {
                copy: true,
                plugins: {
                    ptype: 'treeviewdragdrop',
                    ddGroup: 'attributeDDGroup',
                    appendOnly: true,
                    sortOnDrop: false,
                    containerScroll: true
                }
            }
        },
        {
            xtype: 'admin.duplicates.ruleedit',
            reference: 'ruleEditor',
            title: Unidata.i18n.t('admin.duplicates>ruleSettings'),
            ui: 'un-card',
            bodyPadding: '0 5',
            margin: 0,
            hidden: true,
            bind: {
                readOnly: '{ruleEditorReadOnly}'
            },
            flex: 1,
            listeners: {
                beforesaverule: 'onBeforeSaveRule'
            }
        }
    ]
});
