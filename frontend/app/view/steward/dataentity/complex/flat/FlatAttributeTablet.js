/**
 * Класс реализует плоское представление комплексного атрибута (группа инстансов комплексного атрибута)
 *
 * @author Ivan Marshalkin
 * @date 2016-02-21
 */

Ext.define('Unidata.view.steward.dataentity.complex.flat.FlatAttributeTablet', {
    extend: 'Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTablet',

    requires: [
        'Unidata.view.steward.dataentity.complex.flat.FlatAttributeTabletController',
        'Unidata.view.steward.dataentity.complex.flat.FlatAttributeTabletModel'
    ],

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer',
        nodata: 'Unidata.mixin.NoDataDisplayable'
    },

    alias: 'widget.steward.dataentity.complex.flatattributetablet',

    controller: 'steward.dataentity.complex.flatattributetablet',
    viewModel: {
        type: 'steward.dataentity.complex.flatattributetablet'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'applyReadOnly'
        },
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

    listeners: {
        add: 'onAdd',
        remove: 'onRemove'
    },

    noDataText: Unidata.i18n.t('dataentity>noRecords'),

    //cls: 'un-flat-attribute-tablet un-card-inner',
    cls: 'un-flat-attribute-tablet',
    depthCls: '',

    title: 'ComplexAttributeTablet',
    collapsible: true,
    collapsed: true,
    animCollapse: false,

    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
    collapseFirst: true,
    header: {
        titlePosition: 1
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function () {
        this.callParent(arguments);
    },

    setDepth: function (depth) {
        this.depth = depth;
    }
});
