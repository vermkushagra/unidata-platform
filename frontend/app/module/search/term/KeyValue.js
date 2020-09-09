/**
 * Терм из пары ключ-значение
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.KetValue', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    config: {
        key: null,
        value: null
    },

    getTermData: function () {
        var data = {};

        data[this.getKey()] = this.getValue();

        return data;
    }

});
