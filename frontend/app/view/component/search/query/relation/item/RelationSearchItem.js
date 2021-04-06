/**
 * Панель поиска по связи
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.item.RelationSearchItem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.relation.item.RelationSearchItemController',
        'Unidata.view.component.search.query.relation.item.RelationSearchItemModel',

        'Unidata.view.component.search.query.relation.component.RelationPicker',
        'Unidata.view.component.search.query.relation.component.RecordGrid',
        'Unidata.view.component.search.query.relation.component.EtalonClusterCombobox'
    ],

    alias: 'widget.component.search.query.relation.item.relationsearchitem',

    controller: 'component.search.query.relation.item.relationsearchitem',
    viewModel: 'component.search.query.relation.item.relationsearchitem',

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    referenceHolder: true,

    config: {
        metaRecord: null,
        relation: null,
        entityTo: null,
        etalonCluster: null,
        selectionMode: null
    },

    relationName: null,// для нужд QA отдела
    relationType: null,// для нужд QA отдела
    relationFromEntity: null,// для нужд QA отдела
    relationToEntity: null,// для нужд QA отдела

    methodMapper: [
        {
            method: 'updatePanelTitle'
        },
        {
            method: 'updateRelation'
        },
        {
            method: 'onRelationChange'
        },
        {
            method: 'onEtalonClusterComboBoxChange'
        },
        {
            method: 'getRelationName'
        },
        {
            method: 'getEtalonIds'
        },
        {
            method: 'updateEtalonCluster'
        },
        {
            method: 'updateSelectionMode'
        }
    ],

    viewModelAccessors: ['metaRecord', 'entityTo', 'etalonCluster', 'selectionMode'],

    relationPicker: null,
    etalonClusterCombobox: null,
    recordGrid: null,

    relMetaRecordLoadErrorText: Unidata.i18n.t('search>query.metaModelLoadError'),

    currentEditModeType: false, // текущий режим редактирования

    editModeType: {
        NONE: false,            // без режимов
        SELECTION: 'selection', // выделение отдельных записей
        QUERY: 'query',         // выделение всех(поисковой запрос)
        DISABLED: 'disabled'    // выделение отключено
    },

    ui: 'un-search-attribute-group',

    hideCollapseTool: true,
    frameHeader: false,
    collapsible: true,
    titleCollapse: true,
    collapsed: false,
    animCollapse: false,

    header: {
        titlePosition: 1
    },

    items: [],

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initItems: function () {
        var relation,
            toEntityDefaultDisplayAttributes = [];

        this.callParent(arguments);

        relation = this.getRelation();

        if (relation) {
            toEntityDefaultDisplayAttributes = relation.get('toEntityDefaultDisplayAttributes');
        }

        this.add({
            xtype: 'component.search.query.relation.component.recordgrid',
            reference: 'recordGrid',
            firstRecordsCount: Unidata.Config.getCustomerCfg()['FIRST_RELATION_SEARCH_RECORDS_COUNT'],
            toEntityDefaultDisplayAttributes: toEntityDefaultDisplayAttributes,
            bind: {
                disabled: '{!recordGridEnabled}'
            },
            listeners: {
                selectionchange: 'onRecordGridSelectionChange'
            }
        });
    },

    initTools: function () {
        this.callParent(arguments);

        this.addTool([
            {
                xtype: 'tool',
                cls: 'x-tool-collapse-el',
                handler: 'toggleCollapse',
                scope: this
            },
            {
                xtype: 'un.fontbutton',
                scale: 'small',
                color: 'lightgray',
                iconCls: 'icon-cross2',
                reference: 'deleteButton',
                handler: 'onDeleteButtonClick',
                tooltip: Unidata.i18n.t('search>query.removeRelationSearchPanel')
            }
        ]);
    },

    initReferences: function () {
        this.relationPicker = this.lookupReference('relationPicker');
        this.recordGrid = this.lookupReference('recordGrid');
    },

    initListeners: function () {
        this.recordGrid.on('countchange', this.updatePanelTitle, this);
    },

    setEditMode: function (type) {
        if (this.currentEditModeType) {
            this.removeCls('un-editmode-' + this.currentEditModeType);
        }

        this.currentEditModeType = type;

        if (type) {
            this.addCls('un-editmode-' + type);
        }
    },

    isEditModeDisabled: function () {
        return this.currentEditModeType == this.editModeType.DISABLED;
    }
});
