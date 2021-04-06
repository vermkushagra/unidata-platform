Ext.define('Unidata.view.steward.dashboard.layout.Layout', {
    extend: 'Ext.panel.Panel',

    viewModel: {
        type: 'steward.dashboard.layout'
    },
    controller: 'steward.dashboard.layout',

    requires: [
        'Unidata.view.steward.dashboard.layout.LayoutController',
        'Unidata.view.steward.dashboard.layout.LayoutModel',

        'Unidata.view.component.dashboard.Dashboard'
    ],

    alias: 'widget.steward.dashboard.layout',

    referenceHolder: true,

    layout: 'fit',

    items: [],

    initItems: function () {
        this.callParent(arguments);
        this.initDashboard();
    },

    getDefaultDashboard: function () {
        var defaultDashboard;

        defaultDashboard = {'xclass': 'Unidata.view.component.dashboard.Dashboard', 'grid': {'xclass': 'Unidata.view.component.grid.masonry.MasonryGrid', 'items': [{'xclass': 'Unidata.view.component.grid.masonry.MasonryGridRow', 'items': [{'xclass': 'Unidata.view.component.grid.masonry.MasonryGridCell', 'items': [{'xclass': 'Unidata.view.component.dashboard.task.TaskAndExport'}], 'columnIndex': 0, 'columnsCount': 10}], 'columnsCount': 10}, {'xclass': 'Unidata.view.component.grid.masonry.MasonryGridRow', 'items': [{'xclass': 'Unidata.view.component.grid.masonry.MasonryGridCell', 'items': [{'xclass': 'Unidata.view.component.dashboard.entity.EntityPanel'}], 'columnIndex': 0, 'columnsCount': 10}], 'columnsCount': 10}]}};

        return defaultDashboard;
    },

    initDashboard: function () {
        var dashboardConfig = Ext.JSON.decode(Unidata.BackendStorage.getCurrentUserValue(Unidata.BackendStorageKeys.DASHBOARD));

        if (!dashboardConfig) {
            dashboardConfig = this.getDefaultDashboard();
        }

        this.add(dashboardConfig);
    }

});
