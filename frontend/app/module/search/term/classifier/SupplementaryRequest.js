/**
 * Доп. запрос для поиска по классификаторам
 *
 * @author Aleksandr Bavin
 * @date 2018-02-15
 */
Ext.define('Unidata.module.search.term.classifier.SupplementaryRequest', {

    extend: 'Unidata.module.search.term.SupplementaryRequest',

    applySupplementarySearchQuery: function () {
        var supplementarySearchQuery = this.callParent(arguments);

        if (!supplementarySearchQuery) {
            supplementarySearchQuery = new Unidata.module.search.ClassifierSearchQuery();
        }

        return supplementarySearchQuery;
    }

});
