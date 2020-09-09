Ext.define('Unidata.view.component.search.searchdetail.query.Query', {

    extend: 'Unidata.view.component.search.query.Query',

    alias: 'widget.component.search.searchdetail.query',

    controller: 'component.search.searchdetail.query',
    viewModel: {
        type: 'component.search.query'
    },

    title: Unidata.i18n.t('ddpickerfield>search>find'),

    config: {
        useToEntityDefaultSearchAttributes: true,
        asOf: null // дата на которую производится поиск записей
    },

    initComponent: function () {
        this.callParent(arguments);

        this.entityCombo.setHidden(true);
        this.lookupReference('queryPresetPanel').setHidden(true);
    },

    updateAsOf: function (asOf) {
        var searchQuery = this.getSearchQuery(),
            binding;

        if (searchQuery && asOf) {
            binding = searchQuery.bind('term.dateAsOf', function (dateAsOf) {
                if (dateAsOf) {
                    dateAsOf.setValue(asOf);
                    binding.destroy();
                }
            }, this);

            searchQuery.bind('term.dateAsOf.value', function (dateAsOf) {
                this.fireEvent('changedateasof', dateAsOf);
            }, this);
        }
    }

});
