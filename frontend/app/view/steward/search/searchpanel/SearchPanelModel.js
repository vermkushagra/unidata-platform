Ext.define('Unidata.view.steward.search.searchpanel.SearchPanelModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.search.searchpanel',

    data: {
        selectedComboboxEntity: null,
        entity: null,
        resultSetView: 'tablet' // вид отображения результата поиска: обычные или табличный
    },

    formulas: {
        userHintHtml: {
            bind: {
                bindTo: '{entity}',
                deep: true
            },
            get: function (entity) {
                var html = '';

                if (entity !== null && entity !== undefined && entity !== '') {
                    html = '<div class="">' + Unidata.i18n.t('search>query.enterSearchQuery') + '</div>';
                } else {
                    html = '<div class="">' + Unidata.i18n.t('search>query.selectEntityOrLookupEntityForSearch') + '</div>';
                }

                return html;
            }
        }
    }
});
