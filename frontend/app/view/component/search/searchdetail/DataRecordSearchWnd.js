/**
 * Окно поиска связанной записи
 *
 * @author Ivan Marshalkin
 * @date 2018-06-27
 */

Ext.define('Unidata.view.component.search.searchdetail.DataRecordSearchWnd', {
    extend: 'Ext.window.Window',

    mixins: [
        'Unidata.mixin.PromisedComponent'
    ],

    requires: [
        'Unidata.view.component.search.searchdetail.DataRecordSearchWndController',
        'Unidata.view.component.search.searchdetail.DataRecordSearchWndModel',

        'Unidata.view.steward.search.tableresultset.TableResultset',
        'Unidata.view.component.dropdown.Detail'
    ],

    alias: 'widget.search.datarecordsearch',

    viewModel: {
        type: 'search.datarecordsearch'
    },

    controller: 'search.datarecordsearch',

    componentCls: 'un-lookup-attribute-search-wnd',

    referenceHolder: true,

    queryPanel: null,
    tableResult: null,
    dataRecordDetail: null,
    windowTitle: null,

    config: {
        toEntityDefaultSearchAttributes: null,
        toEntityDisplayAttributes: null,
        metaRecord: null,
        validFrom: null,
        validTo: null,
        timeIntervalIntersectType: null,
        asOf: null // дата на которую производится поиск записей
    },

    methodMapper: [
        {
            method: 'initTableResultset'
        }
    ],

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    header: false, // скрывать заголовок окна

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();

        this.initTableResultset();

        this.queryPanel.setAsOf(this.getAsOf());

        this.initChildComponentState();
    },

    initComponentReference: function () {
        var me = this;

        me.queryPanel = me.lookupReference('queryPanel');
        me.tableResult = me.lookupReference('tableResult');
        me.dataRecordDetail = me.lookupReference('dataRecordDetail');
        me.windowTitle = me.lookupReference('windowTitle');
    },

    initComponentEvent: function () {
    },

    initChildComponentState: function () {
        var metaRecord = this.getMetaRecord();

        this.tableResult.reconfigureGridColumnsByMetaRecord(metaRecord);
        this.queryPanel.linkResultsetPanel(this.tableResult);
    },

    onDestroy: function () {
        var me = this;

        me.queryPanel = null;
        me.tableResult = null;
        me.dataRecordDetail = null;
        me.windowTitle = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'component.search.searchdetail.query',
            reference: 'queryPanel',
            hideRelationsSearch: true,
            entityReadOnly: true,
            width: 300,
            collapsible: true,
            collapseDirection: 'left',
            animCollapse: false,
            title: Unidata.i18n.t('ddpickerfield>search>dataRecordSearchWndTitle'),
            listeners: {
                changedateasof: 'onQueryChangeDateAsOf'
            }
        },
        {
            xtype: 'panel',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            cls: 'un-lookup-attribute-search-results',
            width: 600,
            flex: 1,
            multiselect: false,
            items: [
                {
                    xtype: 'steward.search.tableresultset',
                    reference: 'tableResult',
                    cls: 'un-lookup-attribute-search-results-table',
                    allPeriodSearch: true,
                    stateful: false,
                    flex: 1,
                    listeners: {
                        selectionchange: 'onResultSelectionChange',
                        storeload: 'onTableResultSetStoreLoad',
                        itemdblclick: 'onTableResultSetItemDblClick'
                    }
                },
                {
                    xtype: 'dropdownpickerfield.detail',
                    reference: 'dataRecordDetail',
                    maxHeight: 182,
                    hidden: true,
                    bind: {
                        hidden: '{!dataRecordDetailVisible}'
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'un.toolbar',
                    padding: '15 5',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'container',
                            layout: {
                                type: 'hbox',
                                align: 'stretch'
                            },
                            flex: 1,
                            items: [
                                {
                                    xtype: 'label',
                                    reference: 'windowTitle',
                                    cls: 'un-title',
                                    text: ''
                                }
                            ]
                        },
                        {
                            xtype: 'tool',
                            type: 'close',
                            handler: function () {
                                this.up('window').close();
                            }
                        }
                    ]
                },
                {
                    xtype: 'un.toolbar',
                    reference: 'buttonItems',
                    dock: 'bottom',
                    autoHide: true,
                    items: [
                        {
                            xtype: 'container',
                            flex: 1
                        },
                        {
                            xtype: 'button',
                            reference: 'prevButton',
                            margin: '0 10 0 0',
                            text: Unidata.i18n.t('ddpickerfield>search>selectBtnText'),
                            listeners: {
                                click: 'onSelectButtonClick'
                            },
                            bind: {
                                disabled: '{!selectButtonEnabled}'
                            }
                        },
                        {
                            xtype: 'button',
                            reference: 'nextButton',
                            margin: '0 0 0 10',
                            text: Unidata.i18n.t('ddpickerfield>search>cancelBtnText'),
                            color: 'transparent',
                            listeners: {
                                click: 'onCancelButtonClick'
                            }
                        },
                        {
                            xtype: 'container',
                            flex: 1
                        }
                    ]
                }
            ]
        }
    ],

    updateMetaRecord: function (metaRecord) {
        this.componentReady().then(function () {
            this.queryPanel.setSelectedEntityName(metaRecord.get('name'));
            this.tableResult.reconfigureGridColumnsByMetaRecord(metaRecord);
        }.bind(this));
    },

    updateToEntityDefaultSearchAttributes: function (toEntityDefaultSearchAttributes) {
        this.componentReady().then(function () {
            this.queryPanel.setToEntityDefaultSearchAttributes(toEntityDefaultSearchAttributes);
        }.bind(this));
    }
});
