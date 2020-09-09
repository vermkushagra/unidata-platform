/**
 * Для фасета
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.facet.UnRanged', {

    extend: 'Unidata.module.search.term.Facet',

    config: {
        name: Unidata.module.search.term.Facet.facetType.UN_RANGED
    },

    constructor: function () {
        this.callParent(arguments);

        this.addReturnField('$from');
        this.addReturnField('$to');
    },

    addReturnField: function (name) {
        this.addTerm(new Unidata.module.search.term.ReturnField({
            name: name
        }));
    },

    getTermData: function () {
        var me = this,
            result = this.callParent(arguments),
            promise;

        promise = this.getTermsData()
            .then(
                function (termsData) {
                    return me.mergeObjects(result, termsData);
                }
            );

        return promise;
    }

});
