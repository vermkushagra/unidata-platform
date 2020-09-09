/**
 * Панель оборачивающая компонент для выбора узла классификатора + панель фильтрации атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-12-01
 */

Ext.define('Unidata.view.component.search.query.classifier.ClassifierFilterItem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.classifier.ClassifierFilterItemController',
        'Unidata.view.component.search.query.classifier.ClassifierFilterItemModel',

        'Unidata.view.component.ClassifierNodePicker'
    ],

    alias: 'widget.component.search.query.classifieritem',

    controller: 'component.search.query.classifieritem',
    viewModel: {
        type: 'component.search.query.classifieritem'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-classifier-filter-item',

    referenceHolder: true,
    eventBusHolder: true,

    methodMapper: [
        {
            method: 'getFilters'
        },
        {
            method: 'isEmptyFilter'
        },
        {
            method: 'setAllowedClassifiers'
        },
        {
            method: 'getClassifierNode'
        },
        {
            method: 'excludeField'
        },
        {
            method: 'setDisabled'
        }
    ],

    classifierNodePicker: null,
    classifierFilterPanel: null,

    /**
     * @type Unidata.module.search.term.SupplementaryRequest
     */
    supplementaryRequestTerm: null,

    config: {
        currentClassifierName: null,
        searchQuery: null,
        allowedEntities: null
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initClassifierNode();
    },

    updateCurrentClassifierName: function (currentClassifierName) {
        this.fireEvent('currentclassifierchange', currentClassifierName);
    },

    initClassifierNode: function () {
        var classifierNodePicker = this.classifierNodePicker,
            /**
             * @type {Unidata.module.search.term.classifier.NodeFormField}
             */
            nodeTerm,
            classifierName,
            nodeId;

        if (this.supplementaryRequestTerm) {
            nodeTerm = this.getNodeTerm();

            classifierName = nodeTerm.getClassifierName();
            nodeId = nodeTerm.getValue();

            // загружаем нужный узел классификатора
            classifierNodePicker.loadByNodeId(classifierName, nodeId).done();
        }
    },

    updateSearchQuery: function (searchQuery) {
        this.getViewModel().set('searchQuery', searchQuery);
    },

    /**
     * Создаётся терм, если его нет
     *
     * @returns {Unidata.module.search.term.SupplementaryRequest}
     */
    getSupplementaryRequestTerm: function () {
        var supplementaryRequestTerm = this.supplementaryRequestTerm;

        if (!supplementaryRequestTerm) {
            supplementaryRequestTerm = new Unidata.module.search.term.classifier.SupplementaryRequest();
        }

        this.initSupplementaryRequestTerm(supplementaryRequestTerm);

        return supplementaryRequestTerm;
    },

    initSupplementaryRequestTerm: function (supplementaryRequestTerm) {
        var viewModel = this.getViewModel(),
            supplementarySearchQuery,
            binding;

        if (this.supplementaryRequestTermInited) {
            return;
        }

        supplementarySearchQuery = supplementaryRequestTerm.getSupplementarySearchQuery();

        this.supplementaryRequestTerm = supplementaryRequestTerm;

        this.on('beforedestroy', function () {
            this.fireEvent('classifieritemnodereset', this);
            this.supplementaryRequestTerm.destroy();
        }, this);

        /**
         * @see Unidata.view.component.search.query.classifier.ClassifierFilterItemModel.data.searchQuery
         */
        binding = viewModel.bind('{searchQuery}', function (searchQuery) {
            if (searchQuery) {
                searchQuery.addTerm(supplementaryRequestTerm);

                binding.destroy();
            }
        });

        viewModel.set('supplementarySearchQuery', supplementarySearchQuery);

        this.supplementaryRequestTermInited = true;
    },

    /**
     * @returns {Unidata.module.search.ClassifierSearchQuery}
     */
    getSupplementarySearchQuery: function () {
        return this.getSupplementaryRequestTerm().getSupplementarySearchQuery();
    },

    getNodeTerm: function () {
        return this.getSupplementarySearchQuery().getNodeTerm();
    },

    onDestroy: function () {
        var me = this;

        me.classifierNodePicker  = null;
        me.classifierFilterPanel = null;

        me.callParent(arguments);
    },

    initComponentReference: function () {
        var me = this;

        me.classifierNodePicker  = me.lookupReference('classifierNodePicker');
        me.classifierFilterPanel = me.lookupReference('classifierFilterPanel');
    },

    items: [],

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'un.classifiernodepicker',
                ui: 'un-field-default',
                publishes: ['value'],
                cls: 'un-classifier-nodepicker',
                reference: 'classifierNodePicker'
            },
            {
                xtype: 'component.search.classifierattribute.classifierfilterpanel',
                hidden: true,
                bind: {
                    searchQuery: '{supplementarySearchQuery}',
                    hidden: '{!classifierNodePicker.value}'
                },
                listeners: {
                    beforecollapse: function () {
                        this.lookupReference('classifierFilterMargin').hide();
                    },
                    beforeexpand: function () {
                        this.lookupReference('classifierFilterMargin').show();
                    },
                    scope: this
                },
                reference: 'classifierFilterPanel'
            },
            {
                xtype: 'container',
                cls: 'un-classifier-margin',
                reference: 'classifierFilterMargin',
                hidden: true
            }
        ]);
    }

});
