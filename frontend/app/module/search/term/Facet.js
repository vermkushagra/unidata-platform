/**
 * Фасеты
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.Facet', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    termName: 'facet',

    config: {
        name: null // имя фасета
    },

    /**
     * Имя вида facet.duplicates_only
     *
     * @returns {string}
     */
    getTermName: function () {
        return this.termName + '.' + this.getName();
    },

    getTermData: function () {
        return {
            facets: [this.getName()]
        };
    },

    statics: {
        facetType: {
            ERRORS_ONLY: 'errors_only',
            DUPLICATES_ONLY: 'duplicates_only',
            INACTIVE_ONLY: 'inactive_only',
            PENDING_ONLY: 'pending_only',
            INCLUDE_INACTIVE: 'include_inactive_periods',
            UN_RANGED: 'un_ranged',
            OPERATION_TYPE: 'operation_type'
        },

        /**
         * Фабрика
         * @see {Unidata.module.search.DataSearchQuery.initFacets}
         *
         * @param {Object} config
         * @param {string} config.name
         * @returns {Unidata.module.search.term.Facet}
         */
        create: function (config) {
            switch (config.name) {
                case Unidata.module.search.term.Facet.facetType.UN_RANGED:
                    return Ext.create('Unidata.module.search.term.facet.UnRanged', config);
                case Unidata.module.search.term.Facet.facetType.OPERATION_TYPE:
                    return Ext.create('Unidata.module.search.term.facet.OperationType', config);
                default:
                    return Ext.create('Unidata.module.search.term.Facet', config);
            }
        }
    }

});
