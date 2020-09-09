/**
 * @author Aleksandr Bavin
 * @date 2017-10-18
 */
Ext.define('Unidata.view.component.dashboard.entity.EmptyEntityPanelController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard.entity.empty',

    loadingCount: 0, // количество загружаемых в данный момент данных

    init: function () {
        var view = this.getView(),
            masonryGrid = view.masonryGrid;

        masonryGrid.on('widgetset', this.onGridWidgetSet, this);

        view.on('render', function () {
            view.setLoading(true);
        });

        this.getViewModel().getStore('searchHits').load();
    },

    onSearchHitsStoreLoad: function (store, records, success) {
        var view = this.getView(),
            storeData = [];

        if (success) {
            if (records !== null) {
                Ext.Array.each(records, function (record) {
                    var keyValue     = record.get('keyValue'),
                        rights       = ['create', 'read', 'update', 'delete'],
                        obj          = record.mapToObject(),
                        userHasRight = Unidata.Config.userHasAnyRights(keyValue, rights);

                    if (userHasRight) {
                        storeData.push({
                            name: obj.name,
                            displayName: obj.displayName
                        });
                    }
                });
            }

            view.setEntityStoreData(storeData);
        }

        view.setLoading(false);
    },

    onGridWidgetSet: function (grid, cell, widget) {
        var view = this.getView(),
            selectedEntityName = view.getSelectedEntityName();

        if (selectedEntityName && widget.setEntityName) {
            widget.setEntityName(selectedEntityName);
        }
    },

    onEntityNameChange: function (combo, entityName) {
        var view = this.getView(),
            masonryGrid = view.masonryGrid,
            masonryGridWidgets = masonryGrid.getWidgets();

        view.setSelectedEntityName(entityName);

        Ext.Array.each(masonryGridWidgets, function (widget) {
            if (widget.setEntityName) {
                widget.setEntityName(entityName);
            }
        }, this);
    },

    /**
     * При старте загрузки данных для одного из графиков
     */
    onLoadingStart: function () {
        this.loadingCount++;

        // не показываем лоадер, если данные загрузулись очень быстро
        clearTimeout(this.showLoadingTimer);
        this.showLoadingTimer = Ext.defer(function () {
            this.getView().setLoading(true);
        }, 100, this);
    },

    /**
     * При окончании загрузки данных для одного из графиков
     */
    onLoadingEnd: function () {
        clearTimeout(this.loadingTimer);

        this.loadingCount--;

        this.loadingTimer = Ext.defer(function () {
            var view = this.getView();

            if (view && !view.isDestroyed && this.loadingCount === 0) {
                clearTimeout(this.showLoadingTimer);
                view.setLoading(false);
            }
        }, 10, this);
    }

});
