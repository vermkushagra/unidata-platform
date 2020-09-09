/**
 * Компонент плоского представления коллекции узлов классификации
 * @author Sergey Shishigin
 * @date 2018-06-09
 */

Ext.define('Unidata.view.steward.dataentity.classifier.flat.FlatClassifierNodeTablet', {
    extend: 'Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTablet',

    requires: [
        'Unidata.view.steward.dataentity.classifier.flat.FlatClassifierNodeTabletController'
        // 'Unidata.view.steward.dataentity.classifier.flat.FlatClassifierNodeTabletModel'
    ],

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer',
        nodata: 'Unidata.mixin.NoDataDisplayable'
    },

    alias: 'widget.steward.dataentity.classifier.flatclassifiernodetablet',

    controller: 'steward.dataentity.classifier.flatclassifiernodetablet',
    // viewModel: {
    //     type: 'steward.dataentity.classifier.flatclassifiernodetablet'
    // },

    /**
     * Пробрасываем методы из view в controller или model
     */
    // methodMapper: [
    //     {
    //         method: 'applyReadOnly'
    //     },
    //     {
    //         method: 'updateReadOnly'
    //     },
    //     {
    //         method: 'getSimpleAttributeContainers'
    //     },
    //     {
    //         method: 'updateHiddenAttribute'
    //     },
    //     {
    //         method: 'updatePreventMarkField'
    //     }
    // ],

    noDataText: Unidata.i18n.t('dataentity>noRecords'),

    cls: 'un-flat-attribute-tablet',

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
        this.setReadOnly(true);
    },

    applyReadOnly: function () {
        // пока плоский вид классификаторов только в режиме readOnly
        return true;
    }
});
