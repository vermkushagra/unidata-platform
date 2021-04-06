/**
 * @author Aleksandr Bavin
 * @date 2017-06-27
 */
Ext.define('Unidata.view.component.dashboard.entity.items.ErrorsChart', {

    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.entity.dqerrors.chart',

    viewModel: 'component.dashboard.entity.dqerrors.chart',
    controller: 'component.dashboard.entity.dqerrors.chart',

    requires: [
        'Unidata.view.component.dashboard.entity.items.ErrorsChartController',
        'Unidata.view.component.dashboard.entity.items.ErrorsChartModel'
    ],

    layout: 'hbox',

    referenceHolder: true,

    config: {
        entityName: null
    },

    title: Unidata.i18n.t('dashboard>totalErrors'),

    cls: [
        'un-dashboard-entity-inner',
        'un-dqerrors-chart'
    ],

    items: [
        {
            xtype: 'container',
            margin: '10 30 0 20',
            height: 150,
            width: 150,
            html: '<div class="un-chart-total-count"></div><canvas class="un-chart-canvas"></canvas>'
        },
        {
            xtype: 'container',
            height: 150,
            layout: {
                type: 'vbox',
                pack: 'center'
            },
            reference: 'legend'
        }
    ],

    severityVisibility: null,
    severityCount: null,

    initComponent: function () {
        this.severityVisibility = {};
        this.severityCount = {};

        Ext.Object.each(Unidata.EntityPanelConstants.ERROR_SEVERITY, function (severity) {
            this.severityVisibility[severity] = true;
            this.severityCount[severity] = 0;
        }, this);

        this.callParent(arguments);
    },

    initItems: function () {
        var legend;

        this.callParent(arguments);

        legend = this.lookupReference('legend');

        Ext.Object.each(Unidata.EntityPanelConstants.errorSeverityNameMap, function (key, value) {
            var type = key,
                button;

            button = legend.add({
                xtype: 'un.button.chart.legend',
                cls: 'un-errors-legend-' + type.toLowerCase(),
                margin: '10 0 0 0',
                text: value,
                enableToggle: true,
                pressed: true,
                color: 'chart-severity-' + key.toLowerCase()
            });

            button.on('toggle', function (button, pressed) {
                // не позволяем выключить последний
                if (!pressed && this.countVisibleSeverity() <= 1) {
                    button.setPressed(true);

                    return false;
                }

                this.setSeverityVisibility(type, pressed);
            }, this);

        }, this);
    },

    setSeverityCount: function (severity, count) {
        if (this.severityCount[severity] !== count) {
            this.severityCount[severity] = count;
            this.updateChartDelayed();
        }
    },

    getSeverityCount: function (severity) {
        return this.severityCount[severity] || 0;
    },

    /**
     * количество видимых элементов
     */
    countVisibleSeverity: function () {
        var count = 0;

        Ext.Object.each(this.severityVisibility, function (key, value) {
            if (value) {
                count++;
            }
        });

        return count;
    },

    setSeverityVisibility: function (severity, visibility) {
        if (this.severityVisibility[severity] !== visibility) {
            this.severityVisibility[severity] = visibility;
            this.updateChartDelayed();
        }
    },

    getSeverityVisibility: function (severity) {
        return this.severityVisibility[severity] ? true : false;
    },

    updateEntityName: function () {
        var controller = this.getController();

        if (controller) {
            controller.loadStats();
        }
    },

    updateChart: function () {
        var chart = this.getChart(),
            dataset = chart.data.datasets[0],
            datasetData = dataset.data,
            errorsCount = 0;

        Ext.Object.each(Unidata.EntityPanelConstants.ERROR_SEVERITY, function (severity) {
            var dataIndex = Unidata.EntityPanelConstants.severityChartIndexMap[severity],
                count = this.getSeverityCount(severity),
                visible = this.getSeverityVisibility(severity);

            if (visible) {
                errorsCount += count;
                datasetData[dataIndex] = count;
            } else {
                datasetData[dataIndex] = 0;
            }
        }, this);

        if (errorsCount) {
            datasetData[datasetData.length - 1] = 0;
        } else {
            datasetData[datasetData.length - 1] = 1;
        }

        this.getEl().selectNode('.un-chart-total-count').innerHTML = errorsCount;

        chart.update();
    },

    updateChartDelayed: function () {
        clearTimeout(this.updateChartTimer);
        this.updateChartTimer = Ext.defer(this.updateChart, 100, this);
    },

    onRender: function () {
        this.callParent(arguments);
        this.updateChartDelayed();
    },

    getChart: function () {
        var datasetsBackgroundColor = [],
            context;

        if (!this.chart) {
            context = this.getEl().selectNode('.un-chart-canvas').getContext('2d');

            Ext.Object.each(Unidata.EntityPanelConstants.ERROR_SEVERITY, function (key) {
                datasetsBackgroundColor.push(Unidata.EntityPanelConstants.severityColorMap[key]);
            });

            datasetsBackgroundColor.push('#E6E6E6'); // цвет фона

            this.chart = new Chart(context, {
                type: 'doughnut',
                data: {
                    datasets: [
                        {
                            borderWidth: [0, 0, 0, 0, 0],
                            data: [0, 0, 0, 0, 1],
                            backgroundColor: datasetsBackgroundColor
                        }
                    ]
                },
                options: {
                    title: {
                        display: false
                    },
                    tooltips: {
                        enabled: false
                    },
                    legend: {
                        display: false
                    },
                    // responsive: true,
                    cutoutPercentage: 93,
                    rotation: 0.5 * Math.PI,
                    animation: {
                        // animateScale: false,
                        animateRotate: true
                    }
                }
            });
        }

        return this.chart;
    }

});
