/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.entity.items.EntityLineChart', {

    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.entity.linechart',

    viewModel: 'component.dashboard.entity.linechart',
    controller: 'component.dashboard.entity.linechart',

    requires: [
        'Unidata.view.component.dashboard.entity.items.EntityLineChartController',
        'Unidata.view.component.dashboard.entity.items.EntityLineChartModel'
    ],

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    config: {
        entityName: null
    },

    cls: [
        'un-dashboard-entity-inner',
        'un-entity-linechart'
    ],

    chart: null,

    startDateField: null,
    endDateField: null,
    granularityCombo: null,

    items: [
        {
            xtype: 'container',
            // layout: 'fit',
            minHeight: 350,
            minWidth: 300,
            flex: 1,
            html: '<canvas class="un-chart-canvas x-unselectable"></canvas>'
        },
        {
            xtype: 'container',
            minWidth: 130,
            maxWidth: 315,
            flex: 2,
            margin: '40 0 0 20',
            layout: {
                type: 'vbox'
            },
            reference: 'legend'
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        moment.locale(Unidata.Config.getLocale());
    },

    onDestroy: function () {
        this.startDateField = null;
        this.endDateField = null;
        this.granularityCombo = null;

        this.callParent(arguments);
    },

    initTools: function () {
        this.callParent(arguments);

        // Гранулярность
        this.granularityCombo = Ext.widget({
            xtype: 'combobox',
            ui: 'un-field-default',
            displayField: 'displayName',
            valueField: 'name',
            queryMode: 'local',
            editable: false,
            submitValue: false,
            width: 85,
            store: {
                fields: ['name', 'displayName'],
                data: [
                    // {name: 'MINUTE', displayName: 'Минута'},
                    // {name: 'HOUR', displayName: 'Час'},
                    {name: 'DAY', displayName: Unidata.i18n.t('dashboard>day')},
                    {name: 'WEEK', displayName: Unidata.i18n.t('dashboard>week')},
                    {name: 'MONTH', displayName: Unidata.i18n.t('dashboard>month')}
                ]
            },
            listeners: {
                change: 'onGranularityChange'
            }
        });

        // От
        this.startDateField = Ext.widget({
            xtype: 'datefield',
            ui: 'un-field-default',
            width: 110,
            reference: 'startDate',
            editable: false,
            listeners: {
                change: 'onStartDateChange'
            }
        });

        // До
        this.endDateField = Ext.widget({
            xtype: 'datefield',
            ui: 'un-field-default',
            width: 110,
            reference: 'endDate',
            editable: false,
            listeners: {
                change: 'onEndDateChange'
            }
        });

        this.addTool([
            this.granularityCombo,
            this.startDateField,
            this.endDateField
        ]);

        this.initInputValues();
    },

    /**
     * Инициализация начальных значений
     */
    initInputValues: function () {
        var dateToday = new Date(),
            dateWeekBefore;

        dateWeekBefore = Ext.Date.add(dateToday, Ext.Date.DAY, -6);

        this.startDateField.setValue(dateWeekBefore);
        this.endDateField.setValue(dateToday);

        this.granularityCombo.setValue('DAY');
    },

    updateEntityName: function () {
        var controller = this.getController();

        if (controller) {
            controller.loadStats();
        }
    },

    getChart: function () {
        var context,
            ticksColor = 'rgba(0, 0, 0, 0.6)',
            gridLinesColor = 'rgba(0, 0, 0, 0.08)';

        if (!this.chart) {
            context = this.getEl().selectNode('.un-chart-canvas').getContext('2d');

            this.chart = new Chart(context, {
                type: 'line',
                data: {
                    labels: [],
                    datasets: []
                },
                options: {
                    // responsive: false,
                    maintainAspectRatio: false,
                    title: {
                        display: false
                    },
                    legend: {
                        display: false
                    },
                    scales: {
                        xAxes: [
                            {
                                type: 'time',
                                gridLines: {
                                    color: gridLinesColor
                                },
                                ticks: {
                                    minRotation: 45,
                                    fontColor: ticksColor
                                },
                                time: {
                                    format: 'DD.MM.YYYY',
                                    minUnit: 'day',
                                    tooltipFormat: 'll',
                                    displayFormats: {
                                        day: 'D MMMM',
                                        week: 'D MMMM'
                                    }
                                }
                            }
                        ],
                        yAxes: [
                            {
                                type: 'linear',
                                gridLines: {
                                    color: gridLinesColor
                                },
                                ticks: {
                                    fontColor: ticksColor,
                                    min: 0
                                }
                            }
                        ]
                    }
                }
            });
        }

        return this.chart;
    },

    onRender: function () {
        this.callParent(arguments);
        this.getChart();
    }

});
