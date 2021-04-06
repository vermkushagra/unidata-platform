/**
 * Layout экрана "Классификаторы" (model)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.ClassifierLayoutModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.classifier',

    stores: {
        classifierStore: {
            type: 'un.classifier',
            autoLoad: true
        }
    }
});
