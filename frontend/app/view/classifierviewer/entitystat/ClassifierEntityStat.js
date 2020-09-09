/**
 * Панель сводной информации по классифицированным реестрам и их записям
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.entitystat.ClassifierEntityStat', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifierviewer.entitystat.ClassifierEntityStatController',
        'Unidata.view.classifierviewer.entitystat.ClassifierEntityStatModel'
    ],

    alias: 'widget.classifierviewer.entitystat',

    viewModel: {
        type: 'classifierviewer.entitystat'
    },

    controller: 'classifierviewer.entitystat',

    config: {
        classifier: null,
        classifierNode: null
    },

    //cls: 'un-panel',

    scrollable: true,

    methodMapper: [
        {
            method: 'loadAndShowClassifierEntities'
        }
    ],

    items: [],

    referenceHolder: true
});
