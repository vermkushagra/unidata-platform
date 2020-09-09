/**
 * supplementarySearchQuery для связей
 *
 * @author Aleksandr Bavin
 * @date 2018-02-09
 */
Ext.define('Unidata.module.search.RelationSearchQuery', {

    extend: 'Unidata.module.search.SearchQuery',

    config: {
        relNameTerm: null,
        relationFormFieldsCount: 0
    },

    initTerms: function () {
        var dataTypes = Unidata.module.search.term.DataType.dataType;

        this.callParent(arguments);

        this.addTerm(new Unidata.module.search.term.DataType({
            value: dataTypes.ETALON_REL
        }));

        this.addTerm(new Unidata.module.search.term.relation.RelNameFormField());
    },

    /**
     * @param {Unidata.module.search.SearchQueryTerm} term
     */
    onTermAdd: function (term) {
        if (term instanceof Unidata.module.search.term.relation.EtalonIdFormField) {
            this.setRelationFormFieldsCount(this.getRelationFormFieldsCount() + 1);
        }

        if (term instanceof Unidata.module.search.term.relation.RelNameFormField) {
            this.setRelNameTerm(term);
        }
    },

    getRelName: function () {
        return this.getRelNameTerm().getValue();
    },

    /**
     * @param {Unidata.module.search.SearchQueryTerm} term
     */
    onTermRemove: function (term) {
        if (term instanceof Unidata.module.search.term.relation.EtalonIdFormField) {
            this.setRelationFormFieldsCount(this.getRelationFormFieldsCount() - 1);
        }
    }

});
