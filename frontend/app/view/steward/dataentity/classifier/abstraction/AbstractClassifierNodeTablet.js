/**
 * Абстрактный компонент представления коллекции узлов классификации
 * @author Sergey Shishigin
 * @date 2018-05-031
 */
Ext.define('Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTablet', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTabletController',
        'Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTabletModel'
    ],

    alias: 'widget.steward.dataentity.classifier.abstractclassifiernodetablet',

    controller: 'steward.dataentity.classifiernode.abstractclassifiernodetablet',
    viewModel: {
        type: 'steward.dataentity.classifiernode.abstractclassifiernodetablet'
    },

    mixins: {
        highlight: 'Unidata.mixin.DataHighlightable',
        stateable: 'Unidata.mixin.PanelStateable'
    },

    referenceHolder: true,

    ui: 'un-card',
    collapsible: true,
    collapsed: false,
    titleCollapse: true,

    config: {
        metaRecord: null,
        dataRecord: null,
        classifier: null,
        metaClassifierNodes: null,
        dataClassifierNodes: null,
        readOnly: null,
        hiddenAttribute: null,
        preventMarkField: null,
        attributeDiff: null
    },

    viewModelAccessors: ['readOnly'],

    tools: [
        {
            xtype: 'un.fontbutton.additem',
            reference: 'addClassifierNodeAttributeButton',
            handler: 'onAddClassifierNodeButtonClick',
            buttonSize: 'extrasmall',
            shadow: false,
            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:classificationNode')}),
            bind: {
                hidden: '{readOnly}'
            }
        }
    ]

    // TODO: compute attribute diff onRender
    // TODO: implement readOnly mode
});
