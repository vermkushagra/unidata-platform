/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.DashboardModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.dashboard',

    data: {
        gridEditMode: false
    },

    formulas: {
        editModeButtonIconCls: {
            bind: {
                bindTo: '{gridEditMode}',
                deep: true
            },
            get: function (gridEditMode) {
                if (gridEditMode) {
                    return 'icon-chart-bars';
                } else {
                    return 'icon-chart-settings';
                }
            }
        }
    }

});
