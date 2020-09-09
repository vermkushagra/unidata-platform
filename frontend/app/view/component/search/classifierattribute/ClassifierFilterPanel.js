/**
 * Панель фильтрации записей по классификатору
 *
 * @author Ivan Marshalkin
 * 2016-08-23
 */

Ext.define('Unidata.view.component.search.classifierattribute.ClassifierFilterPanel', {
    extend: 'Unidata.view.component.search.attribute.FilterPanel',

    alias: 'widget.component.search.classifierattribute.classifierfilterpanel',

    requires: [
        'Unidata.view.component.search.classifierattribute.ClassifierFilterPanelController',
        'Unidata.view.component.search.classifierattribute.ClassifierFilterPanelModel'
    ],

    controller: 'component.search.classifierattribute.classifierfilterpanel',
    viewModel: {
        type: 'component.search.classifierattribute.classifierfilterpanel'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        classifierNode: null
    },

    animCollapse: false,

    title: Unidata.i18n.t('search>query.classifierAttributes'),
    baseTitle: Unidata.i18n.t('search>query.classifierAttributes'),

    /**
     * @param classifierNode
     */
    updateClassifierNode: function (classifierNode) {
        this.setEntityRecord(classifierNode);
    },

    items: []
});
