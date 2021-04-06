/**
 * Список узлов классификатора с выбором классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.classifiernodelist.ClassifierNodeList', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifierviewer.classifiernodelist.ClassifierNodeListController',
        'Unidata.view.classifierviewer.classifiernodelist.ClassifierNodeListModel'
    ],

    alias: 'widget.classifierviewer.classifiernodelist',

    viewModel: {
        type: 'classifierviewer.classifiernodelist'
    },

    controller: 'classifierviewer.classifiernodelist',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-search',

    cls: 'un-classifier-nodelist',
    config: {
        classifierName: null
    },

    title: Unidata.i18n.t('classifier>classification'),

    collapsible: true,
    collapseDirection: 'left',
    collapseMode: 'header',
    animCollapse: false,
    titleCollapse: true,

    methodMapper: [
        {
            method: 'updateClassifierName'
        }
    ],

    items: [{
        xtype: 'un.classifiernodepanel',
        reference: 'classifierNodePanel',
        flex: 1,
        classifierTreeUi: 'dark',
        classifierComboBoxHidden: false
    }],

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
        this.updateClassifierName(this.getClassifierName());
    },

    initReferences: function () {
        this.classifierNodePanel = this.lookupReference('classifierNodePanel');
    }
});
