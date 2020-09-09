/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.entity.EntityPanelController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard.entity',

    loadingCount: 0, // количество загружаемых в данный момент данных

    init: function () {
        var view = this.getView();

        view.on('render', function () {
            view.setLoading(true);
        });

        this.getViewModel().getStore('searchHits').load();
    },

    onSearchHitsStoreLoad: function (store, records, success) {
        var view = this.getView(),
            storeData = [];

        if (success && Ext.isArray(records) && records.length) {
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

            view.setEntityStoreData(storeData);
        } else {
            view.lookupReference('statsContainer').hide();
            view.getHeader().hide();
            view.setHtml('Не настроены реестры/справочники для отображения статистики.');
        }

        view.setLoading(false);
    },

    onEntityNameChange: function (combo, entityName) {
        this.lookupReference('linechart').setEntityName(entityName);
        this.lookupReference('counts').setEntityName(entityName);
        this.lookupReference('dqerrorsAggregation').setEntityName(entityName);
        this.lookupReference('dqerrorsChart').setEntityName(entityName);
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
