/**
 * Частично копипаст из {@see Unidata.view.component.EntityComboBox}
 * TODO: много костылей из-за особенностей работы Ext.tree.Panel в качестве пикера - переделать компонент
 *
 * @author Aleksandr Bavin
 * @date 2018-08-27
 */
Ext.define('Unidata.view.component.EntityTagField', {

    extend: 'Ext.form.field.Tag',

    alias: 'widget.un.entitytagfield',

    requires: [
        'Unidata.proxy.entity.CatalogProxy',
        'Unidata.view.component.EntityTree'
    ],

    config: {
        dataLoading: true,
        editable: true,
        modelValidation: true,
        msgTarget: 'under',
        // если true, то комбобокс работает в режиме выбора групп
        // если false, то в режиме выбора справочников/реестров
        catalogMode: false,
        draftMode: false,
        hideEmptyGroups: true,
        showEntities: true,
        showLookupEntities: true,
        allowedEntityNames: null,
        selection: false,
        entityNames: null,
        allowedUserRights: null
    },

    displayField: 'displayName',
    valueField: 'name',
    queryMode: 'local',

    showRoot: false,

    filterByEntityNames: null,

    matchFieldWidth: false,

    initComponent: function () {
        var me = this;

        // TODO: Refactoring. Extract method createStore
        me.store = Ext.create('Ext.data.TreeStore', {
            model: 'Unidata.model.entity.Catalog',
            autoLoad: true,
            proxy: {
                type: 'un.entity.catalog',
                showRoot: me.showRoot,
                onlyCatalog: me.getCatalogMode(),
                draftMode: me.getDraftMode(),
                filterEmptyGroups: me.getHideEmptyGroups(),
                needEntities: me.getShowEntities(),
                needLookupEntities: me.getShowLookupEntities()
            },
            root: {
                id: null
            },
            listeners: {
                load: me.onStoreLoad,
                scope: me
            }
        });

        me.callParent(arguments);

        // TODO: Refactoring. Extract method buildFilterFn OR extract to method buildMethod
        this.filterByEntityNames = new Ext.util.Filter({
            filterFn: function (node) {
                var filtered = true,
                    allowed  = me.allowedEntityNames;

                if (node.isLeaf()) {
                    if (Ext.isArray(allowed) &&  allowed.length && Ext.Array.contains(allowed, node.get('name'))) {
                        filtered = false;
                    }

                } else {
                    if (me.hideEmptyGroups) {
                        Ext.Array.each(node.childNodes, function (childNode) {
                            if (childNode.get('visible')) {
                                filtered = false;
                            }
                        });
                    }
                }

                return !filtered;
            }
        });

        this.filterByUserRights = new Ext.util.Filter({
            filterFn: function (node) {
                var filtered = true,
                    rights  = me.allowedUserRights,
                    entityName = node.get('name'),
                    Config = Unidata.Config;

                if (node.isLeaf()) {
                    if (Ext.isArray(rights) && rights.length && Config.userHasAnyRights(entityName, rights)) {
                        filtered = false;
                    }
                } else {
                    if (me.hideEmptyGroups) {
                        Ext.Array.each(node.childNodes, function (childNode) {
                            if (childNode.get('visible')) {
                                filtered = false;
                            }
                        });
                    }
                }

                return !filtered;
            }
        });

        this.applyFilterByUserRights(this.allowedUserRights);
    },

    /**
     * Возвращает загруженный стор
     *
     * @return {Ext.promise.Promise}
     */
    getLoadedStore: function () {
        var deferred = new Ext.Deferred();

        if (this.store.isLoaded()) {
            deferred.resolve(this.store);
        } else {
            this.store.on('load', function () {
                deferred.resolve(this.store);
            }, this);
        }

        return deferred.promise;
    },

    applyFilterByEntityNames: function (entityNames) {
        var me      = this,
            store   = this.store,
            filters = store.getFilters();

        me.setAllowedEntityNames(entityNames);

        filters.remove(me.filterByEntityNames);

        if (Ext.isArray(entityNames) &&  entityNames.length) {
            filters.add(me.filterByEntityNames);
        }
    },

    applyFilterByUserRights: function (userRights) {
        var me      = this,
            store   = this.store,
            filters;

        if (!store) {
            return;
        }

        filters = store.getFilters();

        filters.remove(me.filterByUserRights);

        if (Ext.isArray(userRights) && userRights.length) {
            filters.add(me.filterByUserRights);
        }
    },

    createPicker: function () {
        var me = this,
            panel;

        panel = Ext.widget({
            xtype: 'un.entitytree',
            viewConfig: {
                getRowClass: Ext.bind(this.getGridRowClass, this),
                selectionModel: {
                    type: 'treemodel',
                    mode: 'SIMPLE',
                    listeners: {
                        beforeselect: this.beforeRecordSelect,
                        scope: this
                    }
                }
            },
            listeners: {
                select: this.onRecordSelect,
                deselect: this.onRecordDeselect,
                beforeselect: this.beforeRecordSelect,
                scope: this
            },
            autoRender: true,
            maxHeight: 400,
            width: 500,
            floating: true,
            hidden: true,
            store: me.store,
            ownerCt: me.ownerCt,
            catalogMode: me.catalogMode
        });

        return panel;
    },

    onRecordSelect: function (selModel, record) {
        record.cls = 'x-grid-item-selected';
        record.set('refresh', !record.get('refresh'), {commit: true});
    },

    onRecordDeselect: function (selModel, record) {
        record.cls = '';
        record.set('refresh', !record.get('refresh'), {commit: true});
    },

    onBindStore: function () {
        this.callParent(arguments);

        this.pickerSelectionModel.on('beforeselect', this.beforeRecordSelect, this);
    },

    getGridRowClass: function (record) {
        return record.cls;
    },

    beforeRecordSelect: function (selModel, record) {
        if (!record.isLeaf()) {
            return false;
        }
    },

    onStoreLoad: function () {
        this.setDataLoading(false);
        this.fireEvent('load', this);
    },

    updateAllowedUserRights: function (userRights) {
        this.applyFilterByUserRights(userRights);
    }
});
