/**
 * Колонка для таблеток с группами атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-05-27
 */

Ext.define('Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupColumn', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupTablet',
        'Unidata.view.admin.entity.metarecord.presentation.attributegroup.AddTablet'
    ],

    alias: 'widget.admin.entity.metarecord.presentation.attributegroup.column',

    referenceHolder: true,

    addTablet: null,        // ссылка на таблетку добавления
    tabletContainer: null,  // ссылка на контейнер для таблеток

    config: {
        readOnly: false
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    scrollable: true,

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.addTablet       = this.lookupReference('addTablet');
        this.tabletContainer = this.lookupReference('tabletContainer');
    },

    initComponentEvent: function () {
        this.addTablet.on('createattributegroup', this.onCreateAttributeGroup, this);
        this.tabletContainer.on('add', this.onTabletContainerAdd, this);
    },

    onDestroy: function () {
        this.addTablet = null;
        this.tabletContainer = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'container',
            reference: 'tabletContainer'
        },
        {
            xtype: 'admin.entity.metarecord.presentation.attributegroup.addtablet',
            reference: 'addTablet'
        }
    ],

    onCreateAttributeGroup: function () {
        var item;

        item = this.createAttributeGroup('', []);

        this.appendAttributeGroup(item);
    },

    appendAttributeGroup: function (item) {
        this.tabletContainer.add(item);
    },

    insertAttributeGroup: function (item, index) {
        this.tabletContainer.insert(index, item);
    },

    createAttributeGroup: function (title, attributes) {
        var item;

        item = Ext.create('Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupTablet', {
            title: title,
            attributes: attributes,
            readOnly: this.getReadOnly()
        });

        return item;
    },

    onTabletContainerAdd: function (addTablet, component) {
        var relayedEvents = this.relayItemEvents(component);

        component.on('removed', function () {
            Ext.destroy(relayedEvents);
        }, this, {single: true});
    },

    relayItemEvents: function (item) {
        return this.relayEvents(item, [
            'beforeremovegroup',
            'removegroup',
            'movegroup',
            'changegroup'
        ]);
    },

    getColumnTabletsInfo: function (column) {
        var items = this.tabletContainer.items,
            info  = [];

        if (items) {
            items.each(function (item, index) {
                var itemInfo;

                itemInfo        = item.getTabletInfo();
                itemInfo.row    = index;
                itemInfo.column = column;

                info.push(itemInfo);
            });
        }

        return info;
    },

    removeTablet: function (tablet) {
        this.tabletContainer.remove(tablet);
    },

    cutTablet: function (tablet) {
        this.tabletContainer.remove(tablet, false);
    },

    removeAllTablet: function () {
        this.tabletContainer.removeAll();
    },

    getTabletsCount: function () {
        return this.tabletContainer.items.getCount();
    },

    getTabletIndex: function (tablet) {
        return this.tabletContainer.items.indexOf(tablet);
    },

    updateReadOnly: function () {
        if (this.isConfiguring) {
            return;
        }

        this.syncReadOnly();
    },

    syncReadOnly: function () {
        var readOnly = this.getReadOnly();

        this.addTablet.setReadOnly(readOnly);

        this.tabletContainer.items.each(function (tablet) {
            tablet.setReadOnly(readOnly);
        });
    }
});
