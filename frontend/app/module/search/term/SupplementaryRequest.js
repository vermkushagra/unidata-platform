/**
 * Дополнительный запрос
 *
 * @author Aleksandr Bavin
 * @date 2018-02-15
 */
Ext.define('Unidata.module.search.term.SupplementaryRequest', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    config: {
        /**
         * простой key-value терм, который связан с {@see Unidata.module.search.term.Entity} основного запроса
         */
        entityTerm: null,
        supplementarySearchQuery: null
    },

    constructor: function () {
        this.callParent(arguments);
        this.setSupplementarySearchQuery(this.getSupplementarySearchQuery()); // инициализируем
    },

    applySupplementarySearchQuery: function (data) {
        if (Ext.isObject(data) && !(data instanceof Unidata.module.search.SearchQuery)) {
            return Ext.create(data);
        }

        return data;
    },

    getTerms: function (deep) {
        var terms = this.callParent(arguments),
            supplementarySearchQuery = this.getSupplementarySearchQuery();

        // собираем термы из подзапроса
        if (deep && supplementarySearchQuery) {
            terms = terms.concat(supplementarySearchQuery.getTerms(deep));
        }

        return terms;
    },

    updateEntityTerm: function (newTerm, oldTerm) {
        if (oldTerm) {
            oldTerm.destroy();
        }

        if (newTerm) {
            this.getSupplementarySearchQuery().addTerm(newTerm);
        }
    },

    getTermData: function () {
        return this.getSupplementarySearchQuery().getTermsData()
            .then(
                function (termsData) {
                    return {
                        supplementaryRequests: [termsData]
                    };
                }
            );
    },

    getTermSaveData: function () {
        var result = {xclass: this.$className};

        result.supplementarySearchQuery = this.getSupplementarySearchQuery().getSaveData();

        return result;
    }

});
