/**
 * Layout экрана "Классификаторы. Просмотр"
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.ClassifierViewerLayout', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.classifierviewer.ClassifierViewerLayoutController',
        'Unidata.view.classifierviewer.ClassifierViewerLayoutModel',
        'Unidata.view.classifierviewer.classifiernodelist.ClassifierNodeList',
        'Unidata.view.classifierviewer.tabpanel.TabPanel'
    ],

    alias: 'widget.classifierviewer',

    viewModel: {
        type: 'classifierviewer'
    },

    controller: 'classifierviewer',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    config: {
        classifierName: null
    },

    cls: 'un-section-classifier',

    eventBusHolder: true,

    methodMapper: [
        {
            method: 'updateClassifierName'
        }
    ],

    items: [
        {
            xtype: 'classifierviewer.classifiernodelist',
            reference: 'classifierNodeList',
            width: 300
        },
        {
            xtype: 'classifierviewer.tabpanel',
            flex: 1
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
        this.updateClassifierName(this.getClassifierName());
    },

    initReferences: function () {
        this.classifierNodeList = this.lookupReference('classifierNodeList');
    }
});
