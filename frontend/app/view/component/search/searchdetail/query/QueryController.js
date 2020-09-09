Ext.define('Unidata.view.component.search.searchdetail.query.QueryController', {
    extend: 'Unidata.view.component.search.query.QueryController',

    alias: 'controller.component.search.searchdetail.query',

    updateSearchQuery: function (searchQuery) {
        this.callParent(arguments);

        if (searchQuery) {
            searchQuery.addTerm(new Unidata.module.search.term.ReturnField({name: '$from'}));
            searchQuery.addTerm(new Unidata.module.search.term.ReturnField({name: '$to'}));
        }
    }

});
