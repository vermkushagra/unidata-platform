/**
 * @author Aleksandr Bavin
 * @date 2017-10-18
 */
Ext.define('Unidata.view.component.dashboard.entity.EmptyEntityPanel', {

    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.entity.empty',

    viewModel: 'component.dashboard.entity.empty',
    controller: 'component.dashboard.entity.empty',

    requires: [
        'Unidata.view.component.dashboard.entity.EntityPanelController',
        'Unidata.view.component.dashboard.entity.EntityPanelModel',
        'Unidata.view.component.dashboard.entity.items.*'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-card',

    cls: 'un-dashboard-entity',

    config: {
        selectedEntityName: null,
        entityStoreData: []
    },

    referenceHolder: true,

    scrollable: true,

    header: {
        titlePosition: 1
    },

    entityNameCombo: null,
    masonryGrid: null,

    initComponent: function () {
        this.callParent(arguments);
    },

    onDestroy: function () {
        Ext.destroyMembers(
            this,
            'entityNameCombo',
            'masonryGrid'
        );

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

        if (!this.items.getCount()) {
            this.masonryGrid = this.add({
                xtype: 'component.grid.masonry'
            });
        } else {
            this.masonryGrid = this.items.getAt(0);
        }
    }

});
