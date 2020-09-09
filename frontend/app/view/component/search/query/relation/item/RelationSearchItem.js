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
        /**
         * @type {Unidata.module.search.DataSearchQuery}
         */
        searchQuery: null,
        /**
         * @type {Unidata.module.search.term.relation.SupplementaryRequest}
         */
        supplementaryRequestTerm: null,
        /**
         * @type {Unidata.module.search.RelationSearchQuery}
         */
        relationSearchQuery: null,
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

    viewModelAccessors: [
        'searchQuery',
        'entityTo',
        'etalonCluster',
        'selectionMode'
    ],

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
        this.initTerm();
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

    initTerm: function () {
        var viewModel = this.getViewModel(),
            supplementaryRequestTerm = this.getSupplementaryRequestTerm(),
            relationSearchQuery,
            binding;

        if (!supplementaryRequestTerm) {
            supplementaryRequestTerm = new Unidata.module.search.term.relation.SupplementaryRequest();

            // добавляем терм в запрос
            binding = viewModel.bind('{searchQuery}', function (searchQuery) {
                if (searchQuery) {
                    searchQuery.addTerm(supplementaryRequestTerm);
                    binding.destroy();
                }
            }, this);
        }

        relationSearchQuery = supplementaryRequestTerm.getSupplementarySearchQuery();

        this.setRelationSearchQuery(relationSearchQuery);

        this.initRelationSearchQueryTerms();

        // при изменении entityName или searchQuery, удаляем
        viewModel.bind('{searchQuery}', this.destroyOnChanges, this);
        viewModel.bind('{searchQuery.term.entity.name}', this.destroyOnChanges, this);

        this.on('destroy', supplementaryRequestTerm.destroy, supplementaryRequestTerm);
    },

    destroyOnChanges: function (newValue, oldValue) {
        if (oldValue !== undefined) {
            this.destroy();
        }
    },

    initRelationSearchQueryTerms: function () {
        var relationSearchQuery = this.getRelationSearchQuery(),
            recordGrid = this.recordGrid,
            recordGridStore = this.recordGrid.getStore(),
            /**
             * @type {Unidata.module.search.term.relation.RelNameFormField}
             */
            relNameTerm = relationSearchQuery.getRelNameTerm(),
            relation = this.getRelation(),
            searchHits = [];

        relNameTerm.setValue(relation.get('name'));

        // создаём данные для грида, если есть нужные термы
        relationSearchQuery.getTermsCollection().each(function (term) {
            var searchHit;

            if (term instanceof Unidata.module.search.term.relation.EtalonIdFormField) {
                this.doNotShowWindow = true;

                searchHit = new Unidata.model.search.SearchHit({
                    fakeSearchHit: true,
                    calculatedDisplayName: term.getCalculatedDisplayName(),
                    preview: [
                        {
                            field: '$etalon_id',
                            value: term.getValue()
                        }
                    ]
                });

                searchHits.push(searchHit);
            }
        }, this);

        recordGrid.addSearchHits(searchHits);

        recordGridStore.on('add', this.onRecordGridStoreAdd, this);
        recordGridStore.on('remove', this.onRecordGridStoreRemove, this);
    },

    onRecordGridStoreAdd: function (store, records) {
        var relationSearchQuery = this.getRelationSearchQuery();

        Ext.Array.each(records, function (record) {
            var term;

            term = new Unidata.module.search.term.relation.EtalonIdFormField({
                value: record.get('etalonId'),
                calculatedDisplayName: record.get('calculatedDisplayName')
            });

            relationSearchQuery.addTerm(term);
        });
    },

    onRecordGridStoreRemove: function (store, records) {
        var relationSearchQuery = this.getRelationSearchQuery(),
            removedEtalonIds;

        removedEtalonIds = Ext.Array.map(records, function (record) {
            return record.get('etalonId');
        });

        Ext.Array.each(relationSearchQuery.getTerms(), function (term) {
            if (term instanceof Unidata.module.search.term.relation.EtalonIdFormField) {
                if (removedEtalonIds.indexOf(term.getValue()) !== -1) {
                    relationSearchQuery.removeTerm(term);
                }
            }
        });
    },

    getMetaRecord: function () {
        return this.getSearchQuery().getMetaRecord();
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
