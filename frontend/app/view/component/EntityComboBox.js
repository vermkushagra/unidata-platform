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
        editable: true,
        modelValidation: true,
        msgTarget: 'under',
        enableKeyEvents: true,
        lastQuery: '',
        typeAhead: true,
        mode: 'local',
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

    listeners: {

        keyup: function (self, e) {
            var me = this;

            if (e.getKey() === e.ESC) {
                this.store.clearFilter(true);
                this.setValue('');

                return;
            }

            if (e.getKey() === e.ENTER) {
                return;
            }

            if (e.getKey() === e.DOWN || e.getKey() === e.UP) {
                this.getPicker().focus();

                return;
            }

            // если input пустой, показываем все справочники
            if (me.getRawValue().length == 0) {
                this.store.clearFilter(true);

                return;
            }

            this.store.clearFilter(true);
            this.store.filterBy(
                function (node) {
                    var filtered = true;
                    // хоть кто-то из родителей подошёл по имени, рисуем всю дерево, вместе с текущим элементом
                    if (me.isAnyAncestorMatches(node)) {
                        filtered = false;
                    } else if (node.isLeaf()) {
                        // если это справочник, то просто проверяем соответствие по имени
                        if (me.containsNameIgnoreCase(node.get('displayName'), me.getRawValue())) {
                            filtered = false;
                        }
                    } else {
                        // если это группа, проверяем подходит ли она по имени
                        // если не подошла, смотрим подошёл ли какой-нибудь чайлд
                        if (me.showRoot && node.get('groupName') === 'ROOT') {
                            filtered = false;
                        } else if (me.containsNameIgnoreCase(node.get('displayName'), me.getRawValue())) {
                            filtered = false;
                        } else {
                            filtered = !me.isAnyChildMatches(node);
                        }

                    }

                    return !filtered;
                }
            );

            if (this.expand && !this.isExpanded) {
                this.expand();
                this.focus(); //после expand возвращаем focus на input
            }

        }

    },

    isAnyAncestorMatches: function (node) {
        var me = this,
            parentNode = node.parentNode;

        while (parentNode) {
            if (me.containsNameIgnoreCase(parentNode.get('displayName'), me.getRawValue())) {
                return true;
            } else {
                parentNode = parentNode.parentNode;
            }
        }

        return false;
    },

    isAnyChildMatches: function (node) {
        var me = this,
            matches;

        matches = me.isAnyChildMatchesRecoursively(node);

        return matches;
    },

    isAnyChildMatchesRecoursively: function (currentNode) {
        var me = this,
            i,
            childNode;

        for (i = 0; i < currentNode.childNodes.length; i++) {
            childNode = currentNode.childNodes[i];

            if (childNode.get('visible')) {
                if (me.containsNameIgnoreCase(childNode.get('displayName'), me.getRawValue())) {
                    return true;
                } else {
                    return me.isAnyChildMatchesRecoursively(childNode);
                }
            }
        }

        return false;
    },

    containsNameIgnoreCase: function (str1, str2) {
        return str1.toUpperCase().indexOf(str2.toUpperCase()) > -1;
    },

    onBlur: function (e) {
        var me = this,
            record,
            value;

        // если нажали на какой-то элемент внутри input, то уходим
        if (e.within(this.getPicker().getEl(), true)) {
            return;
        }

        this.callParent(arguments);

        // если инпут пустой, сбрасываем фильтры и уходим
        if (!this.getRawValue()) {
            me.setValue('');
            this.store.clearFilter(true);

            return;
        }

        record = this.findExactRecord();

        if (record) {
            this.collapse();
            this.fireEvent('select', this, record);

            return;
        }
        // если точный поиск не дал результатов ищем
        record = this.findApproximately();

        if (!record) {
            me.setValue('');
            this.store.clearFilter(true);
        } else {
            value = record.get(this.getValueField());
            this.setValue(value);
        }
        this.collapse();
        this.fireEvent('select', this, record);

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

    findExactRecord: function () {
        var me = this,
            resultRecord;

        me.store.each(function (record) {
            if (record.get('displayName') === me.getRawValue()) {
                resultRecord = record;
            }
        });

        return resultRecord;
    },

    findApproximately: function () {
        var me = this,
            resultRecord;

        me.store.each(function (record) {
            if (me.containsNameIgnoreCase(record.get('displayName'), me.getRawValue())) {
                resultRecord = record;

                return false;
            }
        });

        return resultRecord;
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
            focusable: true,
            maxHeight: 400,
            width: 500,
            floating: true,
            hidden: true,
            ownerCt: me.ownerCt,
            store: me.store,
            catalogMode: me.catalogMode
        });

        panel.on('itemclick', me.onItemSelect, me);

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
