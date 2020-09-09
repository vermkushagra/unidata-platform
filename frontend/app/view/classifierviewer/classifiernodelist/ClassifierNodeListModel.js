/**
 * Список узлов классификатора с выбором классификатора (model)
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.classifiernodelist.ClassifierNodeListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.classifierviewer.classifiernodelist',

    stores: {
        classifierStore: {
            type: 'un.classifier',
            autoLoad: true
        }
    }
});
