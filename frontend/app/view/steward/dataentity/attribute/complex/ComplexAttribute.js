/**
 * Класс реализует представление комплексного атрибута (конкретный инстанс комплексного атрибута)
 *
 * @author Ivan Marshalkin
 * @date 2016-02-21
 */

Ext.define('Unidata.view.steward.dataentity.attribute.complex.ComplexAttribute', {

    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.dataentity.util.ComplexAttribute'
    ],

    alias: 'widget.steward.dataentity.attribute.complex.complexattribute',

    controller: 'steward.dataentity.attribute.complex.complexattribute',
    viewModel: {
        type: 'steward.dataentity.attribute.complex.complexattribute'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        },
        {
            method: 'getSimpleAttributeContainers'
        },
        {
            method: 'updateHiddenAttribute'
        },
        {
            method: 'updatePreventMarkField'
        }
    ],

    config: {
        metaRecord: null,
        dataRecord: null,
        metaNested: null,
        dataNested: null,
        depth: 0,
        deletable: false,
        deletableHidden: true,
        readOnly: null,
        hiddenAttribute: null,
        preventMarkField: null
    },

    simpleContainers: null,       // массив контейнеров с простыми атрибутами
    complexContainers: null,      // массив контейнеров с комплексными атрибутами

    ui: 'un-card',
    cls: 'un-dataentity-attribute-complex',
    depthCls: '',

    tools: [
        {
            xtype: 'un.fontbutton.delete',
            reference: 'onRemoveComplexAttributeClick',
            handler: 'onRemoveButtonClick',
            //shadow: false,
            //buttonSize: 'extrasmall',
            tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:complexAttribute')}),

            disabled: true,
            hidden: true,
            bind: {
                disabled: '{!deleteButtonEnabled}',
                hidden: '{!deleteButtonVisible}'
            }
        }
    ],

    collapsible: true,
    collapsed: true,
    titleCollapse: true,
    animCollapse: false,
    // hideCollapseTool: true,

    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
    collapseFirst: true,
    header: {
        titlePosition: 1
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: Unidata.i18n.t('glossary:complexAttribute'),

    items: [],

    initComponent: function () {
        this.callParent(arguments);

        this.clearAttributeContainerLink();
    },

    destroy: function () {
        this.clearAttributeContainerLink();

        this.callParent(arguments);
    },

    clearAttributeContainerLink: function () {
        this.simpleContainers = [];
        this.complexContainers = [];
    },

    setDeletable: function (deletable) {
        var viewModel = this.getViewModel();

        this.deletable = deletable;

        viewModel.set('deletable', deletable);
    },

    setDeletableHidden: function (deletableHidden) {
        var viewModel = this.getViewModel();

        this.deletableHidden = deletableHidden;

        viewModel.set('deletableHidden', deletableHidden);
    }
});
