Ext.define('Unidata.view.component.dropdown.DetailController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.dropdownpickerfield.detail',

    abortLastRequest: function () {
        var view = this.getView(),
            proxy,
            store;

        store = view.detailsStore,
            proxy = store.getProxy();

        if (store.isLoading()) {
            proxy.abort();
        }
    },

    setEmptyStore: function () {
        var view = this.getViewModel(),
            store = view.detailsStore;

        this.setStoreInstance(store);
    },

    /**
     * Устанавливает рабочую копию стора
     */
    setWorkStore: function () {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('details');

        this.setStoreInstance(store);
    },

    loadStore: function (value, onLoad, onFailure) {
        var view = this.getView(),
        store = view.detailsStore,
        proxy = store.getProxy(),
        extraParams = proxy.getExtraParams(),
        loadCfg;

        view.fireEvent('loading', true);

        onLoad = onLoad || Ext.emptyFn;
        onFailure = onFailure || Ext.emptyFn;

        proxy.setExtraParams(extraParams);

        loadCfg = {
            callback: function (records, operation, success) {
                success ? onLoad(records, operation, success) : onFailure(records, operation, success);
                view.fireEvent('loading', false);
            }
        };

        this.abortLastRequest();

        store.clearStoreData();
        store.loadPage(1, loadCfg);

        store.clearFilter();

        if (!view.isShowRemovedIntervals) {
            store.filter([view.filterActiveOnly]);

        }

        store.getSorters().add([{
            sorterFn: function (res1, res2) {
                var from1, from2;

                from1 = res1.mapToObject()['$from'];
                from2 = res2.mapToObject()['$from'];

                return (from1 > from2) ? 1 : -1;
            }
        }]);
    },

    initExtraParams: function (etalonId) {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            store = view.detailsStore,
            proxy = store.getProxy(),
            formFields = [],
            returnFields = [],
            facets = [];

        facets.push('un_ranged');
        facets.push('include_inactive_periods');

        if (view.getRecordStatus() === 'INACTIVE') {
            facets.push('inactive_only');
        }

        formFields.push({
            inverted: false,
            name: '$etalon_id',
            type: 'String',
            value: etalonId
        });

        if (view.referencedDisplayAttributes) {
            returnFields = Ext.Array.merge(returnFields, view.referencedDisplayAttributes);
        }

        if (view.displayAttributes) {
            returnFields = Ext.Array.merge(returnFields, view.displayAttributes);
        }

        returnFields = Ext.Array.merge(returnFields, ['$from', '$to', '$inactive']);

        proxy.setExtraParam('facets', facets);
        proxy.setExtraParam('returnFields', returnFields);
        proxy.setExtraParam('formFields', formFields);
        proxy.setExtraParam('entity', metaRecord.get('name'));
    },

    /**
     * Отобразить детализированную информацию по датарекорду
     */
    displayDataRecordDetail: function (params) {
        var view = this.getView();

        view.setMetaRecord(params.metaRecord);
        view.setReferencedDisplayAttributes(params.referencedDisplayAttributes);
        view.setValidFrom(params.validFrom);
        view.setValidTo(params.validTo);
        view.setEtalonId(params.etalonId);
        view.setRecordStatus(params.status);

        view.buildDetailData();
    },

    toggleToReferencedDisplayAttributes: function (button, flag) {
        var view = this.getView();

        view.setShowReferencedAttributes(flag);
        view.lookupReference('detailsGrid').getView().refresh();
    },
    /**
     * Отобразить "пустую" информацию по датарекорду
     */
    displayEmptyDataRecordDetail: function () {
    }
});
