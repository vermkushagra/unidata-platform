/**
 * Панель реализующая настройку представления метамодели
 *
 * @author Sergey Shishigin
 * @date 2017-08-24
 */

Ext.define('Unidata.view.admin.entity.metarecord.presentation.Presentation', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.entity.metarecord.presentation.attributegroup.Group',
        'Unidata.view.admin.entity.metarecord.presentation.PresentationController',
        'Unidata.view.admin.entity.metarecord.presentation.PresentationModel'
    ],

    alias: 'widget.admin.entity.metarecord.presentation',

    controller: 'admin.entity.metarecord.presentation',
    viewModel: {
        type: 'admin.entity.metarecord.presentation'
    },

    referenceHolder: true,

    attributeGrid: null,
    attributePresentationPanel: null,
    relationPresentationPanel: null,

    config: {
        readOnly: false
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    scrollable: 'vertical',

    layout: {
        type: 'hbox',
        align: 'top'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.attributePresentationPanel = this.lookupReference('attributePresentationPanel');
        this.relationPresentationPanel = this.lookupReference('relationPresentationPanel');
    },

    initComponentEvent: function () {
    },

    onRender: function () {
        this.callParent(arguments);

        // при скролле, обновляем кэш расположения DD элементов
        this.body.on('scroll', this.refreshDragDropManagerCache, this);
    },

    /**
     * По таймауту обновляет кэш расположения DD элементов
     */
    refreshDragDropManagerCache: function () {
        clearTimeout(this.refreshCacheTimer);

        this.refreshCacheTimer = Ext.defer(function () {
            Ext.dd.DragDropManager.refreshCache({attributeGroupsDDGroup: true});
        }, 200);
    },

    onDestroy: function () {
        this.attributeGrid = null;
        this.attributePresentationPanel = null;
        this.relationPresentationPanel = null;

        this.callParent(arguments);
    },

    initItems: function () {
        var items;

        this.callParent(arguments);

        this.attributeGrid = this.buildAttributeGrid({
            minHeight: 150
        });

        items = [
            {
                xtype: 'container',
                width: 350,
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                items: [
                    this.attributeGrid
                ]
            },
            {
                xtype: 'container',
                flex: 1,
                items: [
                    {
                        xtype: 'admin.entity.metarecord.presentation.attributegroup',
                        reference: 'attributePresentationPanel',
                        title: Unidata.i18n.t('admin.metamodel>attributeGroupSettings'),
                        collapsible: true,
                        ui: 'un-card',
                        margin: 10,
                        attributeGrid: this.attributeGrid
                    },
                    {
                        xtype: 'admin.entity.metarecord.presentation.relation',
                        reference: 'relationPresentationPanel',
                        title: Unidata.i18n.t('admin.metamodel>relationSettings'),
                        collapsible: true,
                        ui: 'un-card',
                        margin: 10,
                        bind: {
                            hidden: '{isLookupEntity}'
                        }
                    }
                ]
            }
        ];
        this.add(items);
    },

    buildAttributeGrid: function (customCfg) {
        var grid,
            cfg = {};

        cfg = Ext.apply(cfg, customCfg);
        grid = Ext.create('Unidata.view.admin.entity.metarecord.presentation.attributegroup.AttributeGrid', cfg);

        return grid;
    }
});
