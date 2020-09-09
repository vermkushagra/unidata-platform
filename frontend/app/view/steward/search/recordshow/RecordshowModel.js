Ext.define('Unidata.view.steward.search.recordshow.RecordshowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.steward.search.recordshow',

    data: {},

    formulas: {
        isTabsExist: {
            bind: {
                bindTo: '{recordshowTabPanel.activeTab}',
                deep: true
            },
            get: function (items) {
                return items.getCount() > 0;
            }
        }
    }
});
