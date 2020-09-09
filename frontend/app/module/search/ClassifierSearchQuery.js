/**
 * supplementarySearchQuery для классификатора
 *
 * @author Aleksandr Bavin
 * @date 2018-02-09
 */
Ext.define('Unidata.module.search.ClassifierSearchQuery', {

    extend: 'Unidata.module.search.SearchQuery',

    initTerms: function () {
        var dataTypes = Unidata.module.search.term.DataType.dataType;

        this.callParent(arguments);

        this.addTerm(new Unidata.module.search.term.DataType({
            value: dataTypes.CLASSIFIER
        }));

        this.addTerm(new Unidata.module.search.term.classifier.NodeFormField());
    },

    /**
     * Терм для поиска по узлу классификатора
     *
     * @returns {Unidata.module.search.term.classifier.NodeFormField}
     */
    getNodeTerm: function () {
        return this.findTerm('classifierNode');
    }

});
