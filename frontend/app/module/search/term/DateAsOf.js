/**
 * На дату
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.DateAsOf', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    termName: 'dateAsOf',

    config: {
        value: null // дата
    },

    getTermIsActive: function () {
        return !Ext.isEmpty(this.getValue());
    },

    getTermData: function () {
        return {
            asOf: Ext.Date.format(this.getValue(), Unidata.Config.getDateTimeFormatProxy())
        };
    }

});
