Ext.define('Unidata.view.steward.dataviewer.card.origin.OriginCardModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataviewer.origincard',

    data: {
        originRecords: null,
        timeIntervalStore: null,
        timeIntervalDate: null,
        metaRecord: null,
        etalonId: null
    },

    stores: {},

    formulas: {}
});
