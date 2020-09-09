/**
 * formFields для поиска по dq ошибкам
 *
 * @author Aleksandr Bavin
 * @date 2018-02-13
 */
Ext.define('Unidata.module.search.term.dq.FormField', {

    extend: 'Unidata.module.search.term.FormField',

    config: {
        facetErrorsTerm: null, // facet.errors_only

        type: Unidata.module.search.term.FormFieldStatics.type.STRING,
        inverted: false,
        searchType: Unidata.module.search.term.FormFieldStatics.searchType.EXACT,
        termIsActive: true
    },

    updateFacetErrorsTerm: function (term) {
        if (term) {
            term.bind('termIsActive', this.setTermIsActive, this);
        }
    },

    getTermIsActive: function () {
        var facetErrorsTerm = this.getFacetErrorsTerm();

        // если фасет не активен, то и все форм-филды по dq отключены
        if (facetErrorsTerm && !facetErrorsTerm.getTermIsActive()) {
            return false;
        }

        return this.callParent(arguments);
    }

});
