/**
 * sortFields
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.SortField', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    config: {
        field: null,
        order: null,
        type: 'ASC'
    },

    getTermData: function () {
        var sortField,
            result;

        sortField = {
            field: this.getField(),
            order: this.getOrder(),
            type: this.getType()
        };

        result = {
            sortFields: [sortField]
        };

        return result;
    },

    statics: {
        sortType: {
            ASC: 'ASC',
            DESC: 'DESC'
        }
    }

});
