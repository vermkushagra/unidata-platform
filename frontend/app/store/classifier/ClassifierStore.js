/**
 * Store классификаторов
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.store.classifier.ClassifierStore', {

    extend: 'Ext.data.Store',

    alias: 'store.un.classifier',

    model: 'Unidata.model.classifier.Classifier',
    proxy: 'un.classifier'
});
