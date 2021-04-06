Ext.define('Unidata.view.steward.dataviewer.card.backrel.BackRelCardModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataviewer.backrelcard',

    data: {
        metaRecord: null,
        etalonId: null,
        relationsDigest: null
    },

    stores: {},

    formulas: {}
});
