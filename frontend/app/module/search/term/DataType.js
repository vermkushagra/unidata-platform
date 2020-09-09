/**
 * Различные true/false флаги
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.DataType', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    termName: 'dataType',

    config: {
        value: null
    },

    getTermData: function () {
        return {
            dataType: this.getValue()
        };
    },

    statics: {
        dataType: {
            CLASSIFIER: 'CLASSIFIER', // классификаторы
            ETALON_REL: 'ETALON_REL'  // связи
        }
    }

});
