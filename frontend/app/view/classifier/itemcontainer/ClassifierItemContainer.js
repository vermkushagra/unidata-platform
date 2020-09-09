/**
 * Контейнер классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.itemcontainer.ClassifierItemContainer', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.classifier.itemcontainer.ClassifierItemContainerController',
        'Unidata.view.classifier.itemcontainer.ClassifierItemContainerModel',
        'Unidata.view.classifier.item.ClassifierItem'
    ],

    alias: 'widget.classifier.itemcontainer',

    viewModel: {
        type: 'classifier.itemcontainer'
    },

    controller: 'classifier.itemcontainer',

    layout: {
        type: 'fit',
        align: 'stretch'
    },

    cls: 'un-container un-classifier-item-container',

    items: [],

    eventBusHolder: true,

    methodMapper: [
        {
            method: 'updateStatus'
        }
    ],

    loadClassifierFailureText: Unidata.i18n.t('glossary:loadClassifierNodeFailure'),
    loadingText: Unidata.i18n.t('common:loading'),

    config: {
        status: Unidata.StatusConstant.NONE
    }
});
