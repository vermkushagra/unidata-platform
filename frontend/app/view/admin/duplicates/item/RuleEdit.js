/**
 * Редактор правил
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */
Ext.define('Unidata.view.admin.duplicates.item.RuleEdit', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.duplicates.ruleedit',

    requires: [
        'Unidata.view.admin.duplicates.list.RuleListController',
        'Unidata.view.admin.duplicates.list.RuleListModel',

        'Unidata.view.admin.duplicates.item.fieldset.MatchingAlgorithm'
    ],

    controller: 'admin.duplicates.ruleedit',
    viewModel: {
        type: 'admin.duplicates.ruleedit'
    },

    config: {
        metaRecord: null,                        // модель реестра / справочника
        rule: null,                              // модель правила для редактирования
        readOnly: null
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'disableEditor'
        },
        {
            method: 'enableEditor'
        },
        {
            method: 'updateRule'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'isValidRule'
        }
    ],

    matchingAlgorithmsContainer: null,           // контейнер отображающий matchingAlgorithms
    ruleName: null,                              // поле ввода имени правила
    ruleDescription: null,                       // поле ввода описания правила

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    referenceHolder: true,

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.matchingAlgorithmsContainer = this.lookupReference('matchingAlgorithmsContainer');
        me.ruleName                    = this.lookupReference('ruleName');
        me.ruleDescription             = this.lookupReference('ruleDescription');
    },

    onDestroy: function () {
        var me = this;

        me.matchingAlgorithmsContainer = null;
        me.ruleName                    = null;
        me.ruleDescription             = null;

        me.callParent(arguments);
    },

    items: [
        {
            xtype: 'toolbar',
            height: 50,
            items: [
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-plus',
                    handler: 'onAddMatchAlgorithmButtonClick',
                    tooltip: Unidata.i18n.t('admin.duplicates>addAlgorithm'),
                    bind: {
                        hidden: '{!ruleEditable}'
                    }
                },
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-floppy-disk',
                    handler: 'onRuleSaveButtonClick',
                    tooltip: Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:rule')}),
                    bind: {
                        hidden: '{!saveButtonVisible}'
                    }
                }
            ]
        },
        {
            xtype: 'textfield',
            reference: 'ruleName',
            fieldLabel: Unidata.i18n.t('glossary:naming'),
            bind: {
                value: '{rule.name}',
                readOnly: '{!ruleEditable}'
            }
        },
        {
            xtype: 'textfield',
            reference: 'ruleDescription',
            fieldLabel: Unidata.i18n.t('glossary:description'),
            bind: {
                value: '{rule.description}',
                readOnly: '{!ruleEditable}'
            }
        },
        {
            xtype: 'container',
            reference: 'matchingAlgorithmsContainer',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            scrollable: true,
            flex: 1
        }
    ],

    isRuleEditorDirty: function () {
        var rule  = this.getRule();

        if (!rule) {
            return false;
        }

        return rule.isRuleDirty();
    }
});
