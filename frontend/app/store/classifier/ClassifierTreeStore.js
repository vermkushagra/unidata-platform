/**
 * TreeStore классификаторов
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.store.classifier.ClassifierTreeStore', {

    extend: 'Ext.data.TreeStore',

    alias: 'store.un.classifiertree',

    proxy: {
        type: 'un.classifiernode'
    },
    model: 'Unidata.model.classifier.ClassifierNode'
});
