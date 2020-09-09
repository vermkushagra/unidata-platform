/**
 * Различные true/false флаги
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.Flags', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    termName: 'flags',

    config: {
        countOnly: false,     // если true, то только считает, без результатов
        fetchAll: false       // если true, то вытягивает все записи, игнорируя text и formFields
    },

    getTermData: function () {
        return this.getConfigData({
            include: ['countOnly', 'fetchAll']
        });
    }

});
