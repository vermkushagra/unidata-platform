/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.entity.items.Counts', {

    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.entity.counts',

    viewModel: 'component.dashboard.entity.counts',
    controller: 'component.dashboard.entity.counts',

    requires: [
        'Unidata.view.component.dashboard.entity.items.CountsController',
        'Unidata.view.component.dashboard.entity.items.CountsModel'
    ],

    referenceHolder: true,

    config: {
        entityName: null
    },

    title: Unidata.i18n.t('dashboard>recordsStats'),

    cls: [
        'un-dashboard-entity-inner',
        'un-entity-counts'
    ],

    tpl: [
        '<ul>',
        '<li class="un-entity-counts-total un-border-bottom"><span>' + Unidata.i18n.t('dashboard>totalRecords') + '<b>{TOTAL}</b></span><div class="un-chart-canvas-wrap x-unselectable"><canvas class="un-chart-canvas"></canvas></div></li>',
        '<li class="un-entity-counts-errors un-border-bottom"><span>' + Unidata.i18n.t('dashboard>withErrors').toLowerCase() + '<b>{ERRORS}</b></span></li>',
        '<li class="un-entity-counts-duplicates"><span>' + Unidata.i18n.t('dashboard>ofDuplicates') + '<b>{DUPLICATES}</b></span></li>',
        '<li class="un-entity-counts-clusters"><span>' + Unidata.i18n.t('dashboard>ofDuplicateGroups') + '<b>{CLUSTERS}</b></span></li>',
        '<li class="un-entity-counts-updated"><span>' + Unidata.i18n.t('dashboard>ofUpdated') + '<b>{UPDATED}</b></span></li>',
        '<li class="un-entity-counts-merged"><span>' + Unidata.i18n.t('dashboard>ofMerged') + '<b>{MERGED}</b></span></li>',
        '<li class="un-entity-counts-new"><span>' + Unidata.i18n.t('dashboard>ofNew') + '<b>{NEW}</b></span></li>',
        '<li class="un-clear un-border-bottom"></li>',
        '<li class="un-entity-counts-ratio"><span><div class="un-entity-counts-ratio-numerator un-entity-counts-ratio-numerator-duplicates">дубликатов</div><div>всего записей</div><b>{DUPLICATES_RATIO} %</b></span></li>',
        '<li class="un-entity-counts-ratio"><span><div class="un-entity-counts-ratio-numerator un-entity-counts-ratio-numerator-errors">ошибок</div><div>всего записей</div><b>{ERRORS_RATIO} %</b></span></li>',
        '</ul>'
    ],

    initComponent: function () {
        this.callParent(arguments);
    },

    updateEntityName: function () {
        var controller = this.getController();

        if (controller) {
            controller.loadStats();
        }
    },

    getChart: function () {
        var context;

        if (!this.chart) {
            context = this.getEl().selectNode('.un-chart-canvas').getContext('2d');

            this.chart = new Chart(context, {
                type: 'doughnut',
                data: {
                    datasets: [
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
                    cutoutPercentage: 40,
                    rotation: 0.5 * Math.PI,
                    animation: {
                        // animateScale: false,
                        animateRotate: true
                    }
                }
            });
        }

        return this.chart;
    },

    destroyChart: function () {
        if (this.chart) {
            this.chart.destroy();
            this.chart = null;
        }
    }

});
