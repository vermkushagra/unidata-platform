/**
 * Компонент редактирование алгоритмов споставления
 *
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */
Ext.define('Unidata.view.admin.duplicates.item.fieldset.MatchingAlgorithm', {
    extend: 'Ext.Container',

    alias: 'widget.admin.duplicates.ruleedit.matchingalgorithm',

    requires: [
        'Unidata.view.admin.duplicates.item.fieldset.MatchingAlgorithmController',
        'Unidata.view.admin.duplicates.item.fieldset.MatchingAlgorithmModel'
    ],

    controller: 'admin.duplicates.ruleedit.matchingalgorithm',
    viewModel: {
        type: 'admin.duplicates.ruleedit.matchingalgorithm'
    },

    config: {
        metaRecord: null,                               // модель реестра / справочника
        matchingAlgorithm: null,                        // алгоритм сопоставления
        matchingAlgorithmStore: null,                   // хранилище списка допустимых алгоритмов
        readOnly: null
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'getMatchingAlgorithm4Update'
        },
        {
            method: 'updateMatchingAlgorithm'
        },
        {
            method: 'updateMatchingAlgorithmStore'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'isValidMatchingAlgorithm'
        }
    ],

    matchingAlgorithmField: null,                // выбор типа матчинга
    fieldsContainer: null,                       // ссылка на контейнер с атрибутами

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.matchingAlgorithmField = this.lookupReference('matchingAlgorithmField');
        me.fieldsContainer        = this.lookupReference('fieldsContainer');
    },

    onDestroy: function () {
        var me = this;

        me.matchingAlgorithmField = null;
        me.fieldsContainer = null;

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
                    iconCls: 'icon-trash2',
                    handler: 'onDeleteMatchingAlgorithmButtonClick',
                    tooltip: Unidata.i18n.t('admin.duplicates>removeFields'),
                    bind: {
                        hidden: '{readOnly}'
                    }
                }
            ]
        },
        {
            xtype: 'combobox',
            reference: 'matchingAlgorithmField',
            editable: false,
            displayField: 'name',
            valueField: 'name',
            queryMode: 'local',
            store: {
                model: 'Unidata.model.matching.MatchingAlgorithm',
                autoLoad: false
            },
            bind: {
                value: '{matchingAlgorithm.name}',
                readOnly: '{readOnly}'
            },
            listeners: {
                select: 'onMatchingAlgorithmSelect'
            }
        },
        {
            xtype: 'container',
            reference: 'fieldsContainer',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: []
        }
    ]
});
