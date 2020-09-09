/**
 * Поля, по которым осуществляется поиск
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.SearchField', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    config: {
        name: null // имя поля
    },

    getTermData: function () {
        return {
            searchFields: [this.getName()]
        };
    }

});
