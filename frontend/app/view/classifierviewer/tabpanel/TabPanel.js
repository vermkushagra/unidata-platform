/**
 * Tab panel
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.tabpanel.TabPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'Unidata.view.classifierviewer.tabpanel.TabPanelController',
        'Unidata.view.classifierviewer.tabpanel.TabPanelModel'
    ],

    alias: 'widget.classifierviewer.tabpanel',

    viewModel: {
        type: 'classifierviewer.tabpanel'
    },

    controller: 'classifierviewer.tabpanel',

    mixins: [
        'Unidata.mixin.ExtendedTabPanel'
    ],

    ui: 'un-content',

    listeners: {
        add: 'onTabAdd'
    },

    items: []
});
