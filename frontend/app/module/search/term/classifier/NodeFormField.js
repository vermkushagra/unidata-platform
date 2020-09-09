/**
 * formFields для поиска по узлу классификатора
 *
 * @author Aleksandr Bavin
 * @date 2018-02-13
 */
Ext.define('Unidata.module.search.term.classifier.NodeFormField', {

    extend: 'Unidata.module.search.term.FormField',

    termName: 'classifierNode',

    config: {
        type: Unidata.module.search.term.FormFieldStatics.type.STRING,
        searchType: Unidata.module.search.term.FormFieldStatics.searchType.EXACT
    },

    getClassifierName: function () {
        return this.getName().split('.')[0];
    }

});
