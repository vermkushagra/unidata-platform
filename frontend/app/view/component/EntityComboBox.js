/**
 * Комбобокс для выбора реестра/справочника.
 * Так же используется для выбора папки, в которой лежит реестр/справочник
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-19
 */
Ext.define('Unidata.view.component.EntityComboBox', {

    extend: 'Ext.form.field.Picker',

    alias: 'widget.un.entitycombo',

    requires: [
        'Unidata.proxy.entity.CatalogProxy',
        'Unidata.view.component.EntityTree'
    ],

    config: {
        dataLoading: true,
        editable: false,
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

    showRoot: false,

    filterByEntityNames: null,

    publishes: ['selection'],
    twoWayBindable: ['selection'],

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
                needLookupEntities: me.getShowLookupEntities(),
                listeners: {
                    endprocessresponse: me.onEndProcessResponse,
                    scope: me
                }
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

        this.on('expand', this.onPickerExpand, this);
    },

    onPickerExpand: function () {
        if (!this.store.getCount()) {
            this.collapse();
        }
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

    getAllLeafNode: function () {
        var resultNodes = [],
            storeNodes  = this.store.getRange();

        Ext.Array.each(storeNodes, function (node) {
            if (node.isLeaf()) {
                resultNodes.push(node);
            }
        });

        return resultNodes;
    },

    onEndProcessResponse: function () {
        this.fireEventArgs('endprocessresponse', arguments);
    },

    createPicker: function () {

        var me = this,
            panel,
            selection = me.getRecordByValue(me.value);

        panel = Ext.widget({
            xtype: 'un.entitytree',
            autoRender: true,
            reference: 'catalogTree',
            maxHeight: 400,
            width: 500,
            floating: true,
            hidden: true,
            ownerCt: me.ownerCt,
            store: me.store,
            catalogMode: me.catalogMode,
            listeners: {
                select: me.onItemSelect,
                scope: me
            }
        });

        if (selection) {
            panel.setSelection(selection);
        }

        return panel;

    },

    /**
     * Для сабмита возвращаем entityName|groupName
     * @returns {String}
     */
    getSubmitValue: function () {
        if (this.selection) {
            return this.selection.get(this.getValueField());
        } else {
            return '';
        }
    },

    getValueField: function () {
        return this.catalogMode ? 'groupName' : 'entityName';
    },

    getRecordByValue: function (value) {
        var store,
            index;

        if (!value) {
            return false;
        }

        store = this.store;
        index = store.findExact(this.getValueField(), value);

        if (index !== -1) {
            return store.getAt(index);
        }

        return false;

    },

    getRecord: function () {
        return this.getRecordByValue(this.getValue());
    },

    transformRawValue: function (value) {
        var record = this.getRecordByValue(value);

        if (record) {
            return record.get('displayName');
        }

        return value;
    },

    updateSelection: function (selection) {

        var picker = this.picker;

        if (picker) {
            picker.setSelection(selection);
        }

    },

    setValue: function (value) {
        this.setSelection(this.getRecordByValue(value));

        return this.callParent(arguments);
    },

    getValue: function () {
        return this.value;
    },

    onItemSelect: function (treepanel, record) {
        var value = record.get(this.getValueField());

        this.setValue(value);
        this.collapse();

        this.fireEvent('select', this, record);
    },

    onStoreLoad: function () {
        this.setDataLoading(false);
        this.setValue(this.value);
        this.fireEvent('load', this);
    },

    updateAllowedUserRights: function (userRights) {
        this.applyFilterByUserRights(userRights);
    }
});
