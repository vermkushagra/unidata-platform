/**
 * Фасет "Тип изменения"
 *
 * @author Sergey Shishigin
 * @date 2018-07-05
 */
Ext.define('Unidata.module.search.term.facet.OperationType', {

    extend: 'Unidata.module.search.term.Facet',

    config: {
        value: null     // DIRECT | CASCADED
    },

    getTermIsActive: function () {
        return !Ext.isEmpty(this.getValue());
    },

    getTermData: function () {
        var facet = Unidata.module.search.term.Facet.facetType.OPERATION_TYPE + '_' + this.getValue().toLowerCase();

        return {
            facets: [facet]
        };
    },

    getTermName: function () {
        return this.termName + '.' + Unidata.module.search.term.Facet.facetType.OPERATION_TYPE;
    }

});
