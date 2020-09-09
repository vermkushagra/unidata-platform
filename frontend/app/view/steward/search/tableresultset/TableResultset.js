Ext.define('Unidata.view.steward.search.tableresultset.TableResultset', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.steward.search.tableresultset',

    controller: 'steward.search.tableresultset',
    viewModel: {
        type: 'steward.search.tableresultset'
    },

    requires: [
        'Unidata.view.steward.search.tableresultset.TableResultsetController',
        'Unidata.view.steward.search.tableresultset.TableResultsetModel'
    ],

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'reconfigureGridColumnsByMetaRecord'
        },
        {
            method: 'updateSourceStore'
        },
        {
            method: 'updateAllPeriodSearch'
        }
    ],

    relayers: null,

    referenceHolder: true,

    layout: 'fit',

    config: {
        metaRecord: null,
        sourceStore: null,
        allPeriodSearch: false
    },

    viewModelAccessors: ['metaRecord'],

    tableResultset: null,
    pagingResultset: null,
    stateful: true,

    initItems: function () {
        this.callParent(arguments);

        this.add({
            xtype: 'grid',
            reference: 'tableResultset',
            cls: 'un-table-grid',

            flex: 1,

            // сохранение состояния
            stateProvider: null,
            stateful: this.stateful,
            stateId: 'default',

            sortableColumns: false,

            normalGridConfig: {
                listeners: {
                    // отменяем схлопывание грида, т.к. при схлопывании какой-то баг с отображением
                    beforecollapse: function () {
                        return false;
                    }
                }
            },
            viewConfig: {
                stripeRows: false // полосатость отключена намеренно
            },
            store: Ext.StoreManager.get('ext-empty-store'),
            split: true,
            columns: [],
            listeners: {
                viewready: 'onViewReady',
                beforestatesave: 'onBeforeStateSave',
                beforestaterestore: 'onBeforeStateRestore',
                reconfigure: 'onGridReconfigure',
                columnshow: 'onColumnShow'
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    reference: 'pagingResultset',
                    dock: 'bottom',
                    displayInfo: true,
                    hideRefreshButton: true,
                    emptyMsg: '',
                    padding: '6 6 6 0',
                    bind: {
                        hidden: '{!pagingToolbarVisible}'
                    },
                    items: [
                        {
                            iconCls: 'icon-redo2 un-pagingbtn-default',
                            tooltip: Unidata.i18n.t('search>tableresultset.resetTableSettings'),
                            hidden: !this.stateful,
                            handler: 'onResetTableStateButtonClick'
                        }
                    ]
                }
            ]
        });
    },

    /**
     * Восстанавливает состояние таблицы
     *
     * @param metaRecord
     * @returns {*}
     */
    restoreGridState: function () {
        var controller = this.getController();

        return controller.restoreGridState.apply(controller, arguments);
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.relayers = this.relayEvents(this.tableResultset, [
            'itemclick',
            'itemdblclick',
            'selectionchange'
        ]);
    },

    initReferences: function () {
        this.tableResultset = this.lookupReference('tableResultset');
        this.pagingResultset = this.lookupReference('pagingResultset');
    },

    onDestroy: function () {
        Ext.destroy(this.relayers);

        this.callParent(arguments);
    },

    getTableResultSetGrid: function () {
        return this.tableResultset;
    }
});
