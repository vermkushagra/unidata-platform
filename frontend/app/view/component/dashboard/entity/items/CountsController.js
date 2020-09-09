/**
 * @author Aleksandr Bavin
 * @date 2017-06-22
 */
Ext.define('Unidata.view.component.dashboard.entity.items.CountsController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard.entity.counts',

    /**
     * Загрузка данных
     */
    loadStats: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            statsStore = viewModel.getStore('stats'),
            statsStoreProxy = statsStore.getProxy(),
            entityName = view.getEntityName();

        if (!entityName) {
            return;
        }

        statsStoreProxy.setExtraParam('entityName', entityName);

        statsStore.load();

        view.fireEvent('loadingstart', view);
    },

    calcRatio: function (numerator, denominator, precision) {
        var result = 0,
            minValue;

        precision = precision || 2;

        if (denominator && numerator) {
            result = (numerator * 1.0 / denominator) * 100;
            minValue = Math.pow(10, precision * (-1));
            result = result >= minValue ? result : minValue;
        }

        return result > 0 ? result.toFixed(precision) : 0;
    },

    onStatsStoreLoad: function (store, records, success) {
        var view = this.getView();

        if (success) {
            this.updateData(records);
            this.updateChartData(records);
        }

        view.fireEvent('loadingend', view);
    },

    /**
     * Собираются данные из модели для view
     *
     * @param {Unidata.model.dashboard.Stats[]} records
     */
    updateData: function (records) {
        var view = this.getView(),
            viewData = {};

        // заполняем нулями
        Ext.Object.each(Unidata.EntityPanelConstants.TYPE, function (type) {
            viewData[type] = 0;
        });

        // заполняем данными
        Ext.Array.each(records, function (record) {
            var type = record.get('type');

            record.series().each(function (item) {
                viewData[type] = item.get('value');

                return false;
            }, this);
        }, this);

        viewData['DUPLICATES_RATIO'] = this.calcRatio(viewData['MERGED'], viewData['TOTAL']);
        viewData['ERRORS_RATIO'] = this.calcRatio(viewData['ERRORS'], viewData['TOTAL']);

        view.update(viewData);
    },

    /**
     * Обновляет график
     *
     * @param {Unidata.model.dashboard.Stats[]} records
     */
    updateChartData: function (records) {
        var view = this.getView(),
            chartDataset,
            chart,
            errors,
            errorsCount = 0,
            total,
            totalCount = 0;

        chartDataset = {
            backgroundColor: [],
            borderWidth: [],
            data: []
        };

        // удаляем старый график
        view.destroyChart();

        // ошибки на графике
        errors = Ext.Array.findBy(records, function (record) {
            return record.get('type') === Unidata.EntityPanelConstants.TYPE.ERRORS;
        });

        if (errors && errors.series) {
            errors.series().each(function (item) {
                errorsCount = item.get('value');

                return false;
            });

            chartDataset.backgroundColor.push('#EE1D23');
            chartDataset.data.push(errorsCount);
            chartDataset.borderWidth.push(1);
        }

        // всего записей
        total = Ext.Array.findBy(records, function (record) {
            return record.get('type') === Unidata.EntityPanelConstants.TYPE.TOTAL;
        });

        if (total && total.series) {
            total.series().each(function (item) {
                totalCount = item.get('value');

                return false;
            });

            chartDataset.backgroundColor.push('#9F9F9F');
            chartDataset.data.push(totalCount - errorsCount);
            chartDataset.borderWidth.push(1);
        } else {
            // если нет данных
            chartDataset.backgroundColor.push('#E6E6E6');
            chartDataset.data.push(1);
            chartDataset.borderWidth.push(0);
        }

        // устанавливаем данные для графика
        chart = view.getChart();
        chart.data.datasets = [chartDataset];
        chart.update();
    }

});
