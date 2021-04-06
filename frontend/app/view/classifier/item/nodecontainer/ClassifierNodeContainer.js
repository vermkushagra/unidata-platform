/**
 * Контейнер узла классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-08-15
 */
Ext.define('Unidata.view.classifier.item.nodecontainer.ClassifierNodeContainer', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifier.item.nodecontainer.ClassifierNodeContainerController',
        'Unidata.view.classifier.item.nodecontainer.ClassifierNodeContainerModel'
    ],

    alias: 'widget.classifier.item.nodecontainer',

    viewModel: {
        type: 'classifier.item.nodecontainer'
    },

    controller: 'classifier.item.nodecontainer',

    layout: {
        type: 'fit',
        align: 'stretch'
    },

    cls: 'un-classifier-node-container',

    methodMapper: [
        {
            method: 'setClassifierNode'
        },
        {
            method: 'loadAndSetClassifierNode'
        },
        {
            method: 'highlightErrors'
        },
        {
            method: 'resetErrors'
        },
        {
            method: 'loadClassifierNode'
        }
    ],

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    config: {
        classifier: null
    },

    items: [],

    loadClassifierNodeFailureText: Unidata.i18n.t('glossary:loadClassifierNodeFailure')
});
