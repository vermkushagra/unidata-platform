/**
 * @author Aleksandr Bavin
 * @date 2017-06-22
 */
Ext.define('Unidata.view.component.dashboard.entity.items.EntityLineChartController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard.entity.linechart',

    buttonsByType: null,
    datasetsByType: null,

    init: function () {
        this.callParent(arguments);
        this.initButtons();
    },

    /**
     * Создание legend
     */
    initButtons: function () {
        var legend = this.lookupReference('legend');

        this.buttonsByType = {};
        this.datasetsByType = {};

        Ext.Object.each(Unidata.EntityPanelConstants.typeNameMap, function (key, value) {
            var button;

            button = legend.add({
                xtype: 'un.button.chart.legend',
                margin: '15 0 0 0',
                text: value,
                enableToggle: true,
                pressed: true,
                color: 'chart-' + key.toLowerCase()
            });

            button.on('toggle', function (button, pressed) {
                this.datasetsByType[key].hidden = !pressed;
                this.getView().getChart().update();
            }, this);

            this.buttonsByType[key] = button;
        }, this);
    },

    getChartDatasets: function () {
        return this.getView().getChart().data.datasets;
    },

    /**
     * Устанавливает данные для графика
     * @param datasets
     */
    setChartDatasets: function (datasets) {
        this.getView().getChart().data.datasets = datasets;
    },

    onGranularityChange: function () {
        this.loadStats();
    },

    onStartDateChange: function (dateField) {
        var value = dateField.getValue(),
            view = this.getView();

        view.endDateField.setMinValue(value);
        this.loadStats();
    },

    onEndDateChange: function (dateField) {
        var value = dateField.getValue(),
            view = this.getView();

        view.startDateField.setMaxValue(value);
        this.loadStats();
    },

    onStatsStoreLoad: function (store, records, success) {
        var view = this.getView();

        if (success) {
            this.updateChartData(records);
        }

        view.fireEvent('loadingend', view);
    },

    /**
     *
     * @param type
     * @returns {string}
     */
    getLineColorByType: function (type) {
        return Unidata.EntityPanelConstants.typeColorMap[type];
    },

    /**
     *
     * @param type
     * @returns {string}
     */
    getLineNameByType: function (type) {
        return Unidata.EntityPanelConstants.typeNameMap[type];
    },

    /**
     * Обновляет график, собирая данные из моделей
     *
     * @param {Unidata.model.dashboard.Stats[]} records
     */
    updateChartData: function (records) {
        var chart = this.getView().getChart(),
            datasets = this.getChartDatasets(),
            newDatasets = [],
            maxValue = 0;

        // формируем данные для графика
        Ext.Array.each(records, function (record) {
            var type = record.get('type'),
                dataset;

            dataset = Ext.Array.findBy(datasets, function (dataset) {
                return dataset.record.get('type') === type;
            });

            // создаём новый, если нужно
            if (!dataset) {
                dataset = {
                    record: record,
                    borderColor: this.getLineColorByType(type),
                    backgroundColor: 'white',
                    cubicInterpolationMode: 'monotone',
                    borderWidth: 2,
                    pointBorderWidth: 1,
                    fill: false,
                    data: []
                };
            }

            this.datasetsByType[type] = dataset;

            // скрываем, если кнопка отжата, или её нет
            if (!this.buttonsByType[type] || !this.buttonsByType[type].pressed) {
                dataset.hidden = true;
            }

            newDatasets.push(dataset);

            dataset.data = [];

            // заполняем данными
            record.series().each(function (item) {
                var date = Ext.Date.parse(item.get('time'), 'Y-m-d\\TH:i:s.uO'),
                    value = item.get('value');

                date.setMilliseconds(0);
                date.setSeconds(0);
                date.setMinutes(0);
                date.setHours(0);

                dataset.data.push({
                    x: date,
                    y: value
                });

                if (maxValue < value) {
                    maxValue = value;
                }
            }, this);
        }, this);

        this.updateChartAxes(maxValue);
        this.setChartDatasets(newDatasets);

        chart.scales['x-axis-0'].determineDataLimits();
        chart.update();
    },

    /**
     * Обновляет крайние значения для оси X и Y
     */
    updateChartAxes: function (maxY) {
        var view = this.getView(),
            chart = view.getChart(),
            maxChartValue,
            min,
            max;

        min = Ext.Date.format(view.startDateField.getValue(), Unidata.Config.getDateFormat());
        max = Ext.Date.format(view.endDateField.getValue(), Unidata.Config.getDateFormat());

        // левая и правая границы
        chart.options.scales.xAxes[0].time.min = min;
        chart.options.scales.xAxes[0].time.max = max;

        // добавляем отступ сверху
        maxChartValue = (maxY + Math.ceil(maxY * 0.1)) || 10;

        if (maxChartValue < 10) { // шкала минимум 10 единиц
            maxChartValue = 10;
        }

        chart.options.scales.yAxes[0].ticks.max = maxChartValue;
        chart.options.scales.yAxes[0].ticks.min = 0;
    },

    /**
     * Загрузка данных
     */
    loadStats: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            statsStore = viewModel.getStore('stats'),
            statsStoreProxy = statsStore.getProxy(),
            startDate = view.startDateField.getValue(),
            endDate = view.endDateField.getValue(),
            entityName = view.getEntityName(),
            granularity = view.granularityCombo.getValue();

        if (!entityName) {
            return;
        }

        // конец дня, что бы было включительно
        endDate = Ext.Date.add(Ext.Date.add(endDate, Ext.Date.DAY, 1), Ext.Date.MILLI, -1);

        statsStoreProxy.setExtraParam('startDate', startDate);
        statsStoreProxy.setExtraParam('endDate', endDate);
        statsStoreProxy.setExtraParam('entityName', entityName);
        statsStoreProxy.setExtraParam('granularity', granularity);

        statsStore.load();

        view.fireEvent('loadingstart', view);
    }

});
