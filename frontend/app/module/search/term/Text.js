/**
 * Поиск по тексту
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.Text', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    termName: 'text',

    config: {
        value: null
    },

    applyValue: function (value) {
        if (Ext.isEmpty(value)) {
            return '';
        } else {
            return value.toString();
        }
    },

    getTermIsActive: function () {
        return !Ext.isEmpty(this.getValue());
    },

    getTermData: function () {
        return {
            qtype: Unidata.module.search.term.FormFieldStatics.searchType.FUZZY,
            text: this.getValue()
        };
    }

});
