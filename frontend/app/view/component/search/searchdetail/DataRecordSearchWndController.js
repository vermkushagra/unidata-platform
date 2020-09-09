/**
 *
 * @author Ivan Marshalkin
 * @date 2018-06-27
 */

Ext.define('Unidata.view.component.search.searchdetail.DataRecordSearchWndController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.search.datarecordsearch',

    tableResultSetGetRowClassOld: null,
    allowToSelectEtalonIds: null,

    initTableResultset: function () {
        var view = this.getView(),
            grid;

        grid = view.tableResult.getTableResultSetGrid();

        this.tableResultSetGetRowClassOld = grid.getView().getRowClass;
        grid.getView().getRowClass = this.tableResultSetGetRowClass.bind(this);
    },

    tableResultSetGetRowClass: function (record) {
        var cls = [];

        if (Ext.isFunction(this.tableResultSetGetRowClassOld)) {
            cls = this.tableResultSetGetRowClassOld.apply(this, arguments);
        }

        if (Ext.isString(cls)) {
            cls = [cls];
        }

        if (!this.isAllowedToSelectRecord(record)) {
            cls.push('disallow-record-select');
        }

        return cls;
    },

    isAllowedToSelectRecord: function (record) {
        var allowed = false,
            etalonId;

        if (record) {
            etalonId = record.get('etalonId');
        }

        if (etalonId && Ext.isArray(this.allowToSelectEtalonIds) && Ext.Array.contains(this.allowToSelectEtalonIds, etalonId)) {
            allowed = true;
        }

        return allowed;
    },

    onSelectButtonClick: function () {
        var view = this.getView(),
            selectedSearchHit = this.getSelectedSearchHit();

        if (selectedSearchHit) {
            view.fireEvent('recordselect', selectedSearchHit);

            view.close();
        }
    },

    onCancelButtonClick: function () {
        var view = this.getView();

        view.fireEvent('cancel', view);

        view.close();
    },

    onTableResultSetItemDblClick: function () {
        var view = this.getView(),
            selectedSearchHit = this.getSelectedSearchHit();

        if (this.isAllowedToSelectRecord(selectedSearchHit)) {
            view.fireEvent('recordselect', selectedSearchHit);

            view.close();
        }
    },

    onResultSelectionChange: function (grid, selected) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            selectedSearchHit = null,
            allowSelectedSearchHitToSelect = false;

        if (selected.length) {
            selectedSearchHit = selected[0];

            view.dataRecordDetail.displayDataRecordDetail({
                metaRecord: view.getMetaRecord(),
                referencedDisplayAttributes: view.getToEntityDisplayAttributes(),
                validFrom: view.getValidFrom(),
                validTo: view.getValidTo(),
                etalonId: selectedSearchHit.get('etalonId'),
                status: selectedSearchHit.get('status')
            });

            allowSelectedSearchHitToSelect = this.isAllowedToSelectRecord(selectedSearchHit);
            viewModel.set('allowSelectedSearchHitToSelect', allowSelectedSearchHitToSelect);
        } else {
            view.dataRecordDetail.displayEmptyDataRecordDetail();
        }

        viewModel.set('selectedSearchHit', selectedSearchHit);
    },

    getSelectedSearchHit: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('selectedSearchHit');
    },

    onTableResultSetStoreLoad: function (store) {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            searchHits = store.getRange(),
            etalonIds = [],
            validFrom = view.getValidFrom(),
            validTo = view.getValidTo(),
            timeIntervalIntersectType = view.getTimeIntervalIntersectType(),
            promise;

        Ext.Array.each(searchHits, function (searchHit) {
            etalonIds.push(searchHit.get('etalonId'));
        });

        this.allowToSelectEtalonIds = null;
        viewModel.set('status', 'LOADING');
        view.tableResult.setLoading(true);

        promise = this.checkTimeIntervalIntersectType(etalonIds, validFrom, validTo, timeIntervalIntersectType);
        promise.then(
            function (etalonIds) {
                var grid;

                me.allowToSelectEtalonIds = etalonIds;

                grid = view.tableResult.getTableResultSetGrid();
                grid.getView().refresh();

                viewModel.set('status', 'READY');
                view.tableResult.setLoading(false);
            },
            function () {
                viewModel.set('status', 'READY');
                view.tableResult.setLoading(false);
            }
        ).done();
    },

    checkTimeIntervalIntersectType: function (etalonIds, validFrom, validTo, timeIntervalIntersectType) {
        var Util = Unidata.util.api.DataRecord;

        return Util.allowedDataRecordByTimeIntervalIntersectType(etalonIds, validFrom, validTo, timeIntervalIntersectType);
    },

    onQueryChangeDateAsOf: function (dateasOf) {
        var view = this.getView(),
            tpl = new Ext.XTemplate('{i18nTitle} {date}'),
            title = '';

        if (Ext.isDate(dateasOf)) {
            title = tpl.apply({
                i18nTitle: Unidata.i18n.t('ddpickerfield>search>dataRecordSearchWndDateAsOfTitle'),
                date: Ext.Date.format(dateasOf, Unidata.Config.getDateFormat())
            });
        }

        view.windowTitle.setText(title);
    }
});
