/**
 * Layout экрана "Классификаторы"
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.ClassifierLayout', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.classifier.ClassifierLayoutController',
        'Unidata.view.classifier.ClassifierLayoutModel',
        'Unidata.view.classifier.list.ClassifierList',
        'Unidata.view.classifier.itemcontainer.ClassifierItemContainer'
    ],

    alias: 'widget.classifier',

    viewModel: {
        type: 'classifier'
    },

    controller: 'classifier',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    eventBusHolder: true,

    items: [
        {
            xtype: 'classifier.list',
            width: 300
        },
        {
            xtype: 'classifier.itemcontainer',
            flex: 1
        }
    ]
});
