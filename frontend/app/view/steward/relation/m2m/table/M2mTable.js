/**
 * Панель реализующая представление инстанса связи многие-ко-многим в виде табличного списка
 *
 * @author Ivan Marshalkin
 * @date 2017-05-02
 */

Ext.define('Unidata.view.steward.relation.m2m.table.M2mTable', {
    extend: 'Ext.panel.Panel',

    mixins: {
        //searchCmp: 'Unidata.view.steward.dataentity.mixin.SearchableComponent',
        //searchCont: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.m2m.table.M2mTableController',
        'Unidata.view.steward.relation.m2m.table.M2mTableModel'
    ],

    alias: 'widget.relation.m2mtable',

    controller: 'relation.m2mtable',

    viewModel: {
        type: 'relation.m2mtable'
    },

    referenceHolder: true,

    cls: 'un-relation-m2m-tableview',

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'displayRelations'
        }
    ],

    m2mlist: null,        // ссылка на грид отображающий табличный вид связи
    m2mlistPaging: null,  // ссылка на пейджер для грида

    config: {
        drafts: false,
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        metaRelation: null,
        dataRelation: null,
        readOnly: null,
        relationName: null // QA использует имя связи для поиска
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    collapsible: true,
    titleCollapse: true,
    collapsed: false,
    hideCollapseTool: true,

    tools: [
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        this.m2mlist = this.lookupReference('m2mlist');
        this.m2mlistPaging = this.lookupReference('m2mlistPaging');
    },

    /**
     * Подчищаем свои ссылки
     */
    onDestroy: function () {
        this.m2mlist = null;
        this.m2mlistPaging = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'grid',
            reference: 'm2mlist',
            cls: 'un-table-grid',
            bind: {
                store: '{filteredm2mrecords}'
            },
            plugins: [{
                ptype: 'rowexpander',
                pluginId: 'rowExpanderPlugin',
                rowBodyTpl: '<div style="padding-left: 40px; background-color: #E4E4E4; border-top: 1px solid #DCDCDC;"></div>',
                //columnWidth: 0,
                headerWidth: 20
            }],
            sortableColumns: false,
            enableColumnHide: false,
            expandOnDblClick: false, // отключаем разворачивание по двойному клику
            expandOnEnter: false, // отключаем разворачивание по энтеру
            columns: [], // колонки строятся динамически в контроллере
            viewConfig: {
                listeners: {
                    expandbody: 'onRecordRowExpand',
                    collapsebody: 'onRecordRowCollapse'
                }
            },
            listeners: {
                itemmouseenter: 'onRecordMouseEnter',
                itemmouseleave: 'onRecordMouseLeave',
                itemclick: 'onRecordRowClick'
            }
        },
        {
            xtype: 'pagingtoolbar',
            reference: 'm2mlistPaging',
            displayInfo: true,
            hideRefreshButton: true,
            hideSeparator3: true,
            bind: {
                store: '{filteredm2mrecords}'
            },
            listeners: {
                beforechange: 'onPagingToolbarPageBeforeChange'
            }
        }
    ],

    listeners: {
        afterrender: 'onAfterRenderView'
    }
});
