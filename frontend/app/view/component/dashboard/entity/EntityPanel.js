/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.entity.EntityPanel', {

    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.entity',

    viewModel: 'component.dashboard.entity',
    controller: 'component.dashboard.entity',

    requires: [
        'Unidata.view.component.dashboard.entity.EntityPanelController',
        'Unidata.view.component.dashboard.entity.EntityPanelModel',
        'Unidata.view.component.dashboard.entity.items.*'
    ],

    layout: 'fit',

    ui: 'un-card',

    cls: 'un-dashboard-entity',

    config: {
        entityStoreData: []
    },

    referenceHolder: true,

    scrollable: true,

    header: {
        titlePosition: 1
    },

    entityNameCombo: null,
    minHeight: 38,

    initComponent: function () {
        this.callParent(arguments);
    },

    onDestroy: function () {
        this.entityNameCombo = null;

        this.callParent(arguments);
    },

    /**
     * Сортируем данные
     */
    applyEntityStoreData: function (data) {
        data = Ext.Array.sort(data, function (a, b) {
            var name1 = a.displayName.toLowerCase(),
                name2 = b.displayName.toLowerCase();

            if (name1 === name2) {
                return 0;
            }

            return (name1 > name2) ? 1 : -1;
        });

        return data;
    },

    updateEntityStoreData: function (data) {
        var entityNameComboStore;

        if (this.entityNameCombo) {
            entityNameComboStore = this.entityNameCombo.getStore();

            entityNameComboStore.loadData(data);

            if (data.length) {
                this.entityNameCombo.setValue(data[0].name);
            }
        }
    },

    initTools: function () {
        this.callParent(arguments);

        // Реестр
        this.entityNameCombo = Ext.widget({
            xtype: 'combobox',
            ui: 'un-field-default',
            cls: 'un-entity-combobox',
            grow: true,
            growToLongestValue: false,
            matchFieldWidth: false,
            displayField: 'displayName',
            valueField: 'name',
            queryMode: 'local',
            editable: false,
            submitValue: false,
            store: {
                fields: ['name', 'displayName'],
                data: this.getEntityStoreData()
            },
            listeners: {
                change: 'onEntityNameChange'
            }
        });

        this.addTool([
            this.entityNameCombo
        ]);
    },

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'container',
                reference: 'statsContainer',
                minWidth: 831,
                items: [
                    {
                        xtype: 'container',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        height: 400,
                        margin: '0 0 20 0',
                        defaults: {
                            listeners: {
                                loadingstart: 'onLoadingStart',
                                loadingend: 'onLoadingEnd'
                            }
                        },
                        items: [
                            {
                                xtype: 'component.dashboard.entity.counts',
                                reference: 'counts',
                                width: 355
                            },
                            {
                                xtype: 'component.dashboard.entity.linechart',
                                scrollable: true,
                                reference: 'linechart',
                                flex: 1
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        html: '<hr />'
                    },
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        defaults: {
                            listeners: {
                                loadingstart: 'onLoadingStart',
                                loadingend: 'onLoadingEnd'
                            }
                        },
                        items: [
                            {
                                xtype: 'component.dashboard.entity.dqerrors.chart',
                                reference: 'dqerrorsChart',
                                width: 380,
                                margin: '0 0 20 0'
                            },
                            {
                                xtype: 'component.dashboard.entity.dqerrors.aggregation',
                                reference: 'dqerrorsAggregation',
                                maxWidth: 1043,
                                flex: 1
                            }
                        ]
                    }
                ]
            }
        ]);
    }

});
