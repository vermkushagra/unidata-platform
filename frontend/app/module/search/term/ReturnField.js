/**
 * Поля, для которых возвращаются значения в результатах
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.ReturnField', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    config: {
        name: null // имя поля
    },

    getTermData: function () {
        return {
            returnFields: [this.getName()]
        };
    }

});
