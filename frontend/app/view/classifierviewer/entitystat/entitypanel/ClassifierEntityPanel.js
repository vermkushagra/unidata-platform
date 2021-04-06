/**
 * Панель отображение класифицированных записей реестра
 *
 * @author Sergey Shishigin
 * @date 2016-08-09
 */
Ext.define('Unidata.view.classifierviewer.entitystat.entitypanel.ClassifierEntityPanel', {
    extend: 'Ext.panel.Panel',
    //TODO: Добавить sorter

    requires: [
        'Unidata.util.ColumnGroupOrderConstant'
    ],

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    collapsible: true,
    collapsed: true,
    titleCollapse: true,

    referenceHolder: true,

    config: {
        store: null,
        entityStat: null,
        classifierNode: null,
        metaRecord: null,
        searchResultAttrMap: null
    },

    //cls: 'un-card',
    ui: 'un-card',

    items: [{
        xtype: 'grid',
        reference: 'classifiedRecordsListGrid',
        referenceHolder: true,
        disableSelection: true,
        minHeight: 100,
        emptyText: Unidata.i18n.t('classifier>classifiedRecordsNotExists').toLowerCase(),
        cls: 'un-table-grid',
        viewConfig: {
            deferEmptyText: false
        },
        bbar: {
            xtype: 'pagingtoolbar',
            reference: 'pagingToolbar',
            cls: 'paging-toolbar',
            hidden: true,
            displayInfo: true,
            emptyMsg: Unidata.i18n.t('classifier>selectNoRecords'),
            displayMsg: Unidata.i18n.t('glossary:displayCounter'),
            hideRefreshButton: true
        }
    }],

    grid: null,
    gridPagingToolbar: null,

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initReferences: function () {
        this.grid = this.lookupReference('classifiedRecordsListGrid');
        this.gridPagingToolbar = this.grid.lookupReference('pagingToolbar');
    },

    initListeners: function () {
        this.on('beforeexpand', this.onBeforeExpand, this);
        this.grid.on('itemclick', this.onItemClick, this);
        this.grid.on('resize', this.onGridResize, this);
        this.grid.getView().on('refresh', this.onGridViewRefresh, this);
    },

    onGridResize: function () {
        this.resizeColumns();
    },

    // перенесено из Unidata.view.steward.search.tableresultset.TableResultsetController.onViewResize
    resizeColumns: function () {
        var grid            = this.grid,
            gridView        = grid.getView(),
            columns         = gridView.getGridColumns(),
            columnWidth     = 100,
            lastColumnIndex = columns.length - 1,
            lastColumnWidth = 100,
            gridHeaderCt    = gridView.grid.headerCt,
            width           = grid.getWidth();

        if (columns.length) {
            columnWidth = width / columns.length;
            lastColumnWidth = width - columnWidth * (columns.length - 1);
        }

        columnWidth     = Ext.Number.constrain(columnWidth, 100);
        lastColumnWidth = Ext.Number.constrain(lastColumnWidth, 100);

        gridHeaderCt.suspendEvent('resize');
        gridHeaderCt.suspendEvent('columnresize');

        Ext.Array.each(columns, function (column, index) {
            var width = columnWidth;

            if (index === lastColumnIndex) {
                width = lastColumnWidth;
            }

            column.setWidth(width);
        });

        gridHeaderCt.resumeEvent('resize');
        gridHeaderCt.resumeEvent('columnresize');
    },

    updateStore: function (store) {
        var grid = this.grid,
            gridPagingToolbar = this.gridPagingToolbar;

        if (!grid) {
            return;
        }

        grid.setStore(store);
        this.gridPagingToolbar.setStore(store);
    },

    updateEntityStat: function (entityStat) {
        var title = '',
            displayName,
            count;

        if (entityStat) {
            displayName = entityStat.get('displayName');
            count      = entityStat.get('count');
            title = Ext.String.format('{0} <span class = "un-simple-title-text">(' + Unidata.i18n.t('classifier>ofRecords') + ': {1})</span>', displayName, count);
        }

        this.setTitle(title);
    },

    reconfigureGridColumns: function (searchResultAttrMap) {
        var ColumnGroupOrderConstant = Unidata.util.ColumnGroupOrderConstant,
            ColumnConfigurator = Unidata.util.ColumnConfigurator,
            columns;

        columns = ColumnConfigurator.buildColumnsByAttributeMap(searchResultAttrMap, ColumnGroupOrderConstant.CLASSIFIER_ATTRS_LAST);
        this.grid.reconfigure(columns);
        this.resizeColumns();
    },

    buildGetSearchClassifierRecordsWrapper: function (cfg) {
        var fn;

        fn = function (cfg, searchResultAttrMap) {
            this.setSearchResultAttrMap(searchResultAttrMap.all);
            cfg.searchResultAttrMap = searchResultAttrMap.all;
            cfg.metaRecordAttrMap = searchResultAttrMap.metaRecord;
            cfg.classifierAttrMap = searchResultAttrMap.classifier;
            this.reconfigureGridColumns(searchResultAttrMap.all);

            return Unidata.util.api.Classifier.searchClassifiedRecords(cfg);
        }.bind(this, cfg);

        return fn;
    },

    buildGetReturnAttributesWrapper: function () {
        var fn;

        fn = function (metaRecord) {
            var classifierNode = this.getClassifierNode(),
                classifierName = classifierNode.get('classifierName');

            this.setMetaRecord(metaRecord);

            return Unidata.util.api.Classifier.buildSearchResultAttrMap(metaRecord, classifierName, classifierNode);
        }.bind(this);

        return fn;
    },

    onBeforeExpand: function () {
        var store = this.getStore(),
            classifierNode = this.getClassifierNode(),
            entityStat = this.getEntityStat(),
            entityName,
            entityType,
            getSearchClassifiedRecordsWrapper,
            buildReturnAttributesWrapper,
            cfgMetaRecord,
            promise,
            funcs,
            cfg;

        // TODO: включить проверку на count === 0
        if (!classifierNode || !entityStat || store /*|| (entityStat && entityStat.get('count') === 0)*/) {
            return true;
        }

        entityName = entityStat.get('name');
        entityType = entityStat.get('type');

        cfgMetaRecord = {
            entityName: entityName,
            entityType: entityType
        };

        cfg = {
            classifierName: classifierNode.get('classifierName'),
            classifierNode: classifierNode,
            entityName: entityName,
            entityType: entityType,
            store: store
        };

        this.setStatus(Unidata.StatusConstant.LOADING);

        getSearchClassifiedRecordsWrapper = this.buildGetSearchClassifierRecordsWrapper(cfg);
        buildReturnAttributesWrapper = this.buildGetReturnAttributesWrapper();

        funcs = [Unidata.util.api.MetaRecord.getMetaRecord,
                buildReturnAttributesWrapper,
                getSearchClassifiedRecordsWrapper];

        promise = Ext.Deferred.pipeline(funcs, cfgMetaRecord);

        promise.then(this.onSearchClassifiedRecordsFulfilled.bind(this),
                this.onSearchClassifiedRecordsRejected.bind(this))
                .done();
    },

    onSearchClassifiedRecordsFulfilled: function (store) {
        if (!this.getStore()) {
            this.setStore(store);
        }

        this.setStatus(Unidata.StatusConstant.READY);
    },

    onSearchClassifiedRecordsRejected: function () {
        //TODO: implement me
        this.setStatus(Unidata.StatusConstant.NONE);
    },

    onItemClick: function (panel, searchHit) {
        var cfg,
            metaRecord = this.getMetaRecord();

        cfg = {
            searchHit: searchHit,
            metaRecord: metaRecord
        };

        // datarecordopen event cfg:
        //
        // dataRecordBundle {Unidata.util.DataRecordBundle} Набор структур по отображению записей
        // searchHit {Unidata.model.search.SearchHit} Результат поиска записи
        // metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель (optional)
        // saveCallback {function} Функция, вызываемая после сохранения открытой записи
        this.fireComponentEvent('datarecordopen', cfg);
    },

    onGridViewRefresh: function () {
        var grid    = this.grid,
            toolbar = this.gridPagingToolbar,
            store = this.getStore();

        if (grid.rendered && toolbar && toolbar.rendered && store) {
            if (grid.getStore().getTotalCount() <= store.getPageSize()) {
                toolbar.hide();
            } else {
                toolbar.show();
            }
        }
    }
});
