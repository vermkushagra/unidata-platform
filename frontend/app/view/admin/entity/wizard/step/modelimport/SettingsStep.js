/**
 * @author Aleksandr Bavin
 * @date 2017-01-31
 *
 * @property listGrid
 * @property parentGrid
 * @property childGrid
 */
Ext.define('Unidata.view.admin.entity.wizard.step.modelimport.SettingsStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.entity.wizard.step.modelimport.ConfirmStep',
        'Unidata.model.entity.metadependency.Vertex',
        'Unidata.model.entity.modelimport.SettingsTreeNode',
        'Unidata.view.admin.entity.wizard.step.modelimport.SettingsStepModel'
    ],

    alias: 'widget.admin.entity.wizard.step.modelimport.settings',

    viewModel: {
        type: 'admin.entity.wizard.step.modelimport.settings'
    },

    includeUsers: null,
    includeRoles: null,

    title: Unidata.i18n.t('glossary:settings'),

    disabled: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        settingsData: null
    },

    cls: 'un-import-settings',

    items: [],

    statics: {
        filterFlags: {
            UNCHECKED: Unidata.i18n.t('admin.metamodel>notSelected'),
            WARNING: Unidata.i18n.t('admin.metamodel>warnings'),
            ERROR: Unidata.i18n.t('common:errors'),
            NEW: Unidata.i18n.t('glossary:new'),
            UPDATE: Unidata.i18n.t('admin.metamodel>updatable'),
            CANT_IMPORT: Unidata.i18n.t('admin.metamodel>cantImport'),
            EXIST: Unidata.i18n.t('admin.metamodel>exists')
        },
        typeMap: {
            ENTITY: Unidata.i18n.t('glossary:entities'),
            LOOKUP: Unidata.i18n.t('glossary:lookupEntities'),
            GROUPS: Unidata.i18n.t('glossary:groups'),
            NESTED_ENTITY: Unidata.i18n.t('admin.metamodel>nestedEntities'),
            ENUM: Unidata.i18n.t('glossary:enums'),
            CLASSIFIER: Unidata.i18n.t('glossary:classifiers'),
            MEASURE: Unidata.i18n.t('admin.metamodel>measure'),
            CUSTOM_CF: Unidata.i18n.t('glossary:functions'),
            COMPOSITE_CF: Unidata.i18n.t('admin.metamodel>compositeFunctions'),
            MATCH_RULE: Unidata.i18n.t('admin.metamodel>searchDuplicateRules'),
            MERGE_RULE: Unidata.i18n.t('admin.metamodel>recordMergeRules'),
            SOURCE_SYSTEM: Unidata.i18n.t('glossary:dataSources'),
            RELATION: Unidata.i18n.t('glossary:relations')
        }
    },

    initItems: function () {
        var listPanelFilterData = [];

        this.callParent(arguments);

        Ext.Object.each(this.self.filterFlags, function (key, value) {
            listPanelFilterData.push({
                value: value,
                text: value
            });
        });

        this.add([
            {
                xtype: 'container',
                reference: 'aggregatedCounts',
                padding: '8 13 8 13',
                tpl: [
                    '<div class="un-import-settings-msg-counts">',
                    '<span title="' + Unidata.i18n.t('admin.metamodel>notInSystemButInImportData') + '">',
                    '<span class="un-legend-icon icon-file-add un-grid-icon-green"></span> ',
                    Unidata.i18n.t('admin.metamodel>newCount'),
                    '</span>',
                    '<span title="' + Unidata.i18n.t('admin.metamodel>inSystemAndInImportData') + '">',
                    '<span class="un-legend-icon icon-file-check un-grid-icon-yellow"></span> ',
                    Unidata.i18n.t('admin.metamodel>updateCount'),
                    '</span>',
                    '<span title="' + Unidata.i18n.t('admin.metamodel>notInSystemAndNotInImportData') + '">',
                    '<span class="un-legend-icon icon-file-lock un-grid-icon-red"></span> ',
                    Unidata.i18n.t('admin.metamodel>notFoundCount'),
                    '</span>',
                    '<span title="' + Unidata.i18n.t('admin.metamodel>inSystemButNotInImportData') + '">',
                    '<span class="un-legend-icon icon-file-empty"></span> ',
                    Unidata.i18n.t('admin.metamodel>existsCount'),
                    '</span>',
                    '</div>'
                ]
            },
            {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'begin'
                },
                padding: '0 13',
                margin: '0 0 13 0',
                items: [
                    {
                        xtype: 'container',
                        reference: 'aggregatedTotalCounts',
                        style: {
                            'line-height': '28px'
                        },
                        tpl: [
                            '<span>' + Unidata.i18n.t('admin.metamodel>checkedAndImportCount') + '</span> ',
                            '<tpl if="warningCount">',
                            '<span class="un-import-settings-msg-warning">',
                            Unidata.i18n.t('admin.metamodel>halfRelationsWillNotChanged'),
                            '</span> ',
                            '</tpl>',
                            '<tpl if="errorCount">',
                            '<span class="un-import-settings-msg-error">',
                            Unidata.i18n.t('admin.metamodel>notSelectedNeededRelations'),
                            '</span>',
                            '</tpl>'
                        ]
                    },
                    {
                        xtype: 'component',
                        flex: 1
                    },
                    {
                        xtype: 'label',
                        text: Unidata.i18n.t('admin.metamodel>exportMetaModelWith') + ':',
                        style: {
                            'font-weight': '600',
                            'line-height': '28px'
                        },
                        securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                        securedEvent: 'create'
                    },
                    {
                        xtype: 'checkbox',
                        reference: 'includeUsers',
                        boxLabel: Unidata.i18n.t('glossary:users'),
                        margin: '0 0 0 15',
                        securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                        securedEvent: 'create'
                    },
                    {
                        xtype: 'checkbox',
                        reference: 'includeRoles',
                        boxLabel: Unidata.i18n.t('glossary:roles'),
                        margin: '0 0 0 15',
                        securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                        securedEvent: 'create'
                    }
                ]
            },
            {
                xtype: 'container',
                padding: 0,
                flex: 1,
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                defaults: {
                    layout: 'fit',
                    flex: 1
                },
                items: [
                    {
                        xtype: 'panel',
                        title: Unidata.i18n.t('admin.metamodel>usesIn'),
                        padding: '0 10 0 0',
                        hidden: true,
                        reference: 'parentPanel'
                    },
                    {
                        xtype: 'panel',
                        title: Unidata.i18n.t('admin.metamodel>importedData'),
                        cls: 'un-import-settings-listPanel',
                        reference: 'listPanel',
                        tools: [
                            {
                                xtype: 'combobox',
                                reference: 'listPanelFilter',
                                // width: 155,
                                flex: 1,
                                multiSelect: true,
                                emptyText: Unidata.i18n.t('admin.metamodel>filter'),
                                valueField: 'value',
                                queryMode: 'local',
                                editable: false,
                                store: {
                                    fields: ['value', 'text'],
                                    data: listPanelFilterData
                                },
                                hideLabel: true,
                                listeners: {
                                    change: this.onFilterChange,
                                    scope: this
                                },
                                triggers: {
                                    reset: {
                                        cls: 'x-form-clear-trigger',
                                        handler: function () {
                                            this.reset();
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                ui: 'un-toolbar-admin',
                                scale: 'small',
                                iconCls: 'icon-refresh',
                                margin: '0 0 0 5',
                                tooltip: Unidata.i18n.t('admin.metamodel>updateFilter'),
                                listeners: {
                                    click: function () {
                                        this.runListGridFilter();
                                    },
                                    scope: this
                                }
                            },
                            {
                                xtype: 'button',
                                ui: 'un-toolbar-admin',
                                scale: 'small',
                                margin: '0 0 0 5',
                                iconCls: 'icon-tab',
                                tooltip: Unidata.i18n.t('admin.metamodel>switchMode'),
                                listeners: {
                                    click: function () {
                                        var parentPanel = this.lookupReference('parentPanel'),
                                            childPanel = this.lookupReference('childPanel');

                                        parentPanel.setHidden(!parentPanel.isHidden());
                                        childPanel.setHidden(!childPanel.isHidden());
                                    },
                                    scope: this
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        title: Unidata.i18n.t('admin.metamodel>relatedFrom'),
                        padding: '0 0 0 10',
                        hidden: true,
                        reference: 'childPanel'
                    }
                ]
            }
        ]);
    },

    initComponent: function () {
        var viewModel = this.getViewModel(),
            vertexesStore = viewModel.getStore('vertexes');

        this.callParent(arguments);

        this.initComponentReference();

        vertexesStore.on('load', this.onVertexesLoad, this);
        vertexesStore.on('load', this.initGrids, this, {single: true});
        vertexesStore.on('update', this.onVertexUpdate, this);

        this.setNextStep({
            xtype: 'admin.entity.wizard.step.modelimport.confirm'
        });
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.listGrid = null;
        this.parentGrid = null;
        this.childGrid = null;

        this.includeUsers = null;
        this.includeRoles = null;
    },

    initComponentReference: function () {
        this.includeUsers = this.lookupReference('includeUsers');
        this.includeRoles = this.lookupReference('includeRoles');
    },

    onFilterChange: function () {
        // var selectionModel = this.listGrid.getSelectionModel();
        //
        // selectionModel.setLocked(false);
        // selectionModel.deselectAll();
        // selectionModel.setLocked(true);

        this.runListGridFilter();
    },

    /**
     * Запускает фильтрацию listGrid на основе выбранных элементов из listPanelFilter
     */
    runListGridFilter: function () {
        var listPanelFilter = this.lookupReference('listPanelFilter'),
            value,
            listGrid,
            listGridStore;

        if (!listPanelFilter) {
            return;
        }

        value = listPanelFilter.getValue();
        listGrid = this.listGrid;
        listGridStore = listGrid.getStore();

        if (value && value.length == 0) {
            listGridStore.clearFilter(true);

            return;
        }

        listGrid.hide();
        listGridStore.clearFilter(true);
        listGridStore.filterBy(
            Ext.bind(this.listGridStoreFilter, this, [value], true),
            this
        );
        listGrid.show();
    },

    /**
     * Фильтр для listGrid
     *
     * @param record
     * @param {Array} filterValues
     * @returns {boolean}
     */
    listGridStoreFilter: function (record, filterValues) {
        var vertex = record.get('vertex'),
            filterFlags = this.self.filterFlags,
            Vertex = Unidata.model.entity.metadependency.Vertex,
            action = Vertex.action,
            existence = Vertex.existence,
            vertexAction,
            vertexStatuses,
            vertexExistence,
            i, ln, filterValue;

        // оставляем группы
        if (!vertex) {
            return true;
        }

        vertexAction = vertex.get('action');
        vertexStatuses = vertex.get('statuses');
        vertexExistence = vertex.get('existence');

        for (i = 0, ln = filterValues.length; i < ln; i++) {
            filterValue = filterValues[i];

            switch (filterValue) {
                case filterFlags.CANT_IMPORT:
                    if (vertex.hasError() ||
                        vertexExistence === existence.EXIST ||
                        vertexExistence === existence.NOT_FOUND) {
                        return true;
                    }
                    break;

                case filterFlags.UNCHECKED:
                    if (vertexAction === action.NONE) {
                        return true;
                    }
                    break;

                case filterFlags.WARNING:
                    if (vertex.hasWarning() && vertex.uiWarning === true) {
                        return true;
                    }
                    break;

                case filterFlags.ERROR:
                    if (vertex.hasError() || vertex.uiError === true) {
                        return true;
                    }
                    break;

                case filterFlags.NEW:
                    if (vertexExistence === existence.NEW) {
                        return true;
                    }
                    break;

                case filterFlags.UPDATE:
                    if (vertexExistence === existence.UPDATE) {
                        return true;
                    }
                    break;

                case filterFlags.EXIST:
                    if (vertexExistence === existence.UPDATE || vertexExistence === existence.EXIST) {
                        return true;
                    }
                    break;
            }
        }

        return false;
    },

    /**
     * @param {Unidata.model.entity.metadependency.Vertex} vertex
     */
    updateVertexFlags: function (vertex) {
        if (vertex.updateFlags()) {
            this.updateTreeGrids(vertex);
            this.updateAggregatedCounts();
        }
    },

    /**
     * Обновляет строки дерева, соответствующие данному vertex
     */
    updateTreeGrids: function (vertex) {
        var names = ['listGrid', 'parentGrid', 'childGrid'];

        Ext.Array.each(names, function (name) {
            this.refreshTreeRow(this[name], vertex);
        }, this);

        Ext.Array.each(vertex.parent, function (parentVertex) {
            Ext.Array.each(names, function (name) {
                this.refreshTreeRow(this[name], parentVertex);
            }, this);
        }, this);

        Ext.Array.each(vertex.child, function (childVertex) {
            Ext.Array.each(names, function (name) {
                this.refreshTreeRow(this[name], childVertex);
            }, this);
        }, this);
    },

    /**
     * Отслеживает изменения в vertexesStore, и синхронизирует его с отображаемыми деревьями
     */
    onVertexUpdate: function (store, record, operation, modifiedFieldNames) {
        var vertex = record;

        if (Ext.isEmpty(modifiedFieldNames)) {
            return;
        }

        if (this.updateFlagsTimer) {
            clearTimeout(this.updateFlagsTimer);
            this.updateFlagsTimer = null;
        }

        this.updateFlagsTimer = Ext.defer(function () {
            this.updateVertexFlags(vertex);
        }, 100, this);
    },

    /**
     * Перключает флаг refresh для перерисовки элемента в дереве
     *
     * @param grid
     * @param vertex
     */
    refreshTreeRow: function (grid, vertex) {
        var store = grid.getStore(),
            id = vertex.get('id'),
            type = vertex.get('type'),
            index,
            record;

        index = store.findBy(function (vertex) {
            return ((vertex.get('id') === id) && (vertex.get('type') === type));
        });

        if (index !== -1) {
            record = store.getAt(index);
            record.set('refresh', !record.get('refresh'), {commit: true});
        }
    },

    /**
     * @typedef {Object} GridSettings
     * @property {String} gridReference
     * @property {boolean} [expandGroups = false]
     *
     * @returns {GridSettings}
     */
    getGridSettings: function () {
        return {
            reference: null,
            expandGroups: false
        };
    },

    onVertexesLoad: function () {
        var names = ['list', 'parent', 'child'];

        Ext.Array.each(names, function (name) {
            var gridName = name + 'Grid',
                panelName = name + 'Panel',
                settings = this.getGridSettings(),
                gridConfig;

            settings.gridReference = gridName;

            switch (gridName) {
                case 'parentGrid':
                case 'childGrid':
                    settings.expandGroups = true;
                    break;
            }

            gridConfig = this.getGridConfig(settings);

            if (this[gridName] === undefined) {
                this[gridName] = this.createGrid(
                    this.lookupReference(panelName),
                    gridConfig,
                    settings
                );
            } else {
                this.setStoreData(
                    this[gridName].getStore(),
                    this.getTreeData(settings)
                );
            }
        }, this);

        this.updateAggregatedCounts();
    },

    initGrids: function () {
        this.listGrid.on('selectionchange', this.onListGridSelectionChange, this);

        this.parentGrid.getStore().filterBy(this.filterGridStore);
        this.childGrid.getStore().filterBy(this.filterGridStore);

        // this.parentGrid.on('selectionchange', this.onSideGridSelectionChange, this);
        // this.childGrid.on('selectionchange', this.onSideGridSelectionChange, this);
    },

    // Alexander Bavin: недоделанная реализация при импорте метамодели, что бы в левом и в правом гриде можно было выбрать элемент,
    // и он бы открылся в центральном... своего рода бродилка по зависимостям

    // onSideGridSelectionChange: function (grid, selected) {
    //     var vertex = selected[0].get('vertex'),
    //         vertexId = vertex ? vertex.get('id') : null,
    //         selectionModel,
    //         recordToSelect;
    //
    //     if (vertexId) {
    //
    //         recordToSelect = this.listGrid.getStore().findRecord('id', vertexId, 0, false, true, true);
    //
    //         selectionModel = this.listGrid.getSelectionModel();
    //         selectionModel.setLocked(false);
    //         selectionModel.select(recordToSelect);
    //         selectionModel.setLocked(true);
    //     }
    //
    // },

    /**
     * При выборе элемента в listGrid, фильтруем parentGrid и childGrid
     *
     * @param grid
     * @param selected
     */
    onListGridSelectionChange: function (grid, selected) {
        var view = this,
            vertex = selected.length ? selected[0].get('vertex') : null,
            vertexId = vertex ? vertex.get('id') : null,
            parentIds = this.getParentIds(vertexId),
            childIds = this.getChildIds(vertexId),
            parentGridStore = this.parentGrid.getStore(),
            childGridStore = this.childGrid.getStore();

        this.parentGrid.hide();
        this.childGrid.hide();
        view.suspendLayouts();

        parentGridStore.clearFilter(true);
        parentGridStore.filterBy(
            Ext.bind(this.filterGridStore, this, [parentIds], true),
            this
        );

        childGridStore.clearFilter(true);
        childGridStore.filterBy(
            Ext.bind(this.filterGridStore, this, [childIds], true),
            this
        );

        view.resumeLayouts(true);
        this.parentGrid.show();
        this.childGrid.show();
    },

    filterGridStore: function (record, ids) {
        var vertex = record.get('vertex'),
            vertexId;

        // оставляем группы
        if (!vertex) {
            return true;
        }

        if (Ext.isEmpty(ids)) {
            return false;
        }

        vertexId = vertex.get('id');

        return (Ext.Array.indexOf(ids, vertexId) !== -1);
    },

    /**
     * Раскрашиваем ряд гриде
     *
     * @param record
     * @returns {*}
     */
    getGridRowClass: function (record) {
        var Vertex = Unidata.model.entity.metadependency.Vertex,
            action = Vertex.action,
            existence = Vertex.existence,
            vertex = record.get('vertex'),
            error = false,
            warning = false,
            vertexAction,
            vertexType,
            vertexStatuses,
            vertexExistence;

        if (!vertex) {
            return '';
        }

        vertexAction = vertex.get('action');
        vertexType = vertex.get('type');
        vertexStatuses = vertex.get('statuses');
        vertexExistence = vertex.get('existence');

        if (vertexAction === action.NONE) {
            return 'un-import-settings-unchecked';
        }

        if (vertexExistence !== existence.NOT_FOUND && vertex.hasError()) {
            return 'un-import-settings-error';
        }

        Ext.Array.each(vertex.child, function (childVertex) {
            var childVertexExistence = childVertex.get('existence'),
                childVertexAction = childVertex.get('action');

            if (this.hasNestedError(vertex, childVertex) ||
                childVertex.hasError() ||
                (childVertexExistence === existence.NEW && childVertexAction === action.NONE)) {
                error = true;

                return false;
            }

            if (childVertexExistence === existence.UPDATE && childVertexAction === action.NONE) {
                warning = true;
            }
        }, this);

        Ext.Array.each(vertex.parent, function (parentVertex) {
            if (this.hasNestedError(vertex, parentVertex)) {
                error = true;

                return false;
            }
        }, this);

        if (error) {
            return 'un-import-settings-error';
        }

        if (warning) {
            return 'un-import-settings-warning';
        }

        return '';
    },

    /**
     * UN-4467 Импорт всего: запрещать импорт реестров без выбора комплексных
     *
     * Проверка для ENTITY + NESTED_ENTITY
     *
     * @param vertex1
     * @param vertex2
     * @returns {boolean}
     */
    hasNestedError: function (vertex1, vertex2) {
        var Vertex = Unidata.model.entity.metadependency.Vertex,
            vertex1Type = vertex1.get('type'),
            vertex1Action = vertex1.get('action'),
            vertex2Type = vertex2.get('type'),
            vertex2Action = vertex2.get('action');

        if ((vertex1Type === Vertex.type.NESTED_ENTITY && vertex2Type === Vertex.type.ENTITY) ||
            (vertex1Type === Vertex.type.ENTITY && vertex2Type === Vertex.type.NESTED_ENTITY)) {

            if ((vertex1Action === Vertex.action.UPSERT && vertex2Action === Vertex.action.NONE) ||
                (vertex1Action === Vertex.action.NONE && vertex2Action === Vertex.action.UPSERT)) {
                return true;
            }

            return false;
        }

        return false;
    },

    /**
     * @param {GridSettings} settings
     * @returns {Object}
     */
    getGridConfig: function (settings) {
        var Vertex = Unidata.model.entity.metadependency.Vertex,
            action = Vertex.action,
            existence = Vertex.existence,
            gridConfig;

        gridConfig = {
            xtype: 'treepanel',
            reference: settings.gridReference,
            cls: 'un-import-settings-' + settings.gridReference,
            rootVisible: false,
            scrollable: true,
            hideHeaders: true,
            viewConfig: {
                getRowClass: Ext.bind(this.getGridRowClass, this)
            },
            columns: {
                items: [
                    {
                        xtype: 'treecolumn',
                        text: Unidata.i18n.t('glossary:name'),
                        dataIndex: 'displayName',
                        flex: 1,
                        renderer: function (value, metaData, record) {
                            var vertex = record.get('vertex'),
                                vertexExistence;

                            value = Ext.String.htmlEncode(value);

                            if (vertex) {
                                vertexExistence = vertex.get('existence');

                                switch (vertexExistence) {
                                    case existence.NEW:
                                        value = '<span class="un-grid-icon icon-file-add un-grid-icon-green" title="' + Unidata.i18n.t('admin.metamodel>new') + '"></span> ' + value; // jscs:ignore maximumLineLength
                                        break;
                                    case existence.UPDATE:
                                        value = '<span class="un-grid-icon icon-file-check un-grid-icon-yellow" title="' + Unidata.i18n.t('admin.metamodel>update') + '"></span> ' + value; // jscs:ignore maximumLineLength
                                        break;
                                    case existence.NOT_FOUND:
                                        value = '<span class="un-grid-icon icon-file-lock un-grid-icon-red" title="' + Unidata.i18n.t('admin.metamodel>notFound') + '"></span> ' + value; // jscs:ignore maximumLineLength
                                        break;
                                    case existence.EXIST:
                                        value = '<span class="un-grid-icon icon-file-empty" title="' + Unidata.i18n.t('admin.metamodel>existsInSystem') + '"></span> ' + value; // jscs:ignore maximumLineLength
                                        break;
                                }
                            }

                            return value;
                        }
                    },
                    {
                        width: 25,
                        renderer: function (value, metaData, record) {
                            var vertex = record.get('vertex'),
                                messages = [];

                            if (vertex) {

                                Ext.Array.each(vertex.get('statuses'), function (status) {
                                    messages = messages.concat(status.messages);
                                });

                                if (messages && messages.length) {
                                    metaData.tdCls = 'un-grid-icon-msg-cell';
                                    metaData.tdAttr = 'data-qtip="' + messages.join('<br>') + '"';

                                    return '<span class="un-grid-icon-msg icon-notification-circle"></span>';
                                }
                            }

                            return;
                        }
                    },
                    {
                        xtype: 'checkcolumn',
                        dataIndex: 'itemChecked',
                        width: 30,
                        renderer: function (value, metaData, record) {
                            var vertex = record.get('vertex'),
                                vertexExistence;

                            if (!record.isLeaf()) {
                                return;
                            }

                            vertexExistence = vertex.get('existence');

                            if (vertexExistence === existence.EXIST ||
                                vertexExistence === existence.NOT_FOUND) {
                                return;
                            }

                            // отображаем значение на основе vertex
                            value = (vertex.get('action') === action.UPSERT);

                            return this.defaultRenderer(value, metaData);
                        },
                        listeners: {
                            beforecheckchange: this.onBeforecheckchange,
                            scope: this
                        }
                    }
                ]
            },
            listeners: {
                beforeitemclick: this.onBeforeitemclick,
                scope: this
            }
        };

        return gridConfig;
    },

    onBeforecheckchange: function (checkcolumn, rowIndex) {
        var Vertex = Unidata.model.entity.metadependency.Vertex,
            action = Vertex.action,
            existence = Vertex.existence,
            store = checkcolumn.up('treepanel').getStore(),
            record = store.getAt(rowIndex),
            vertex = record.get('vertex'),
            vertexExistence;

        if (!vertex) {
            return false;
        }

        vertexExistence = vertex.get('existence');

        if (vertexExistence === existence.EXIST ||
            vertexExistence === existence.NOT_FOUND) {
            return false;
        }

        vertex.set(
            'action',
            vertex.get('action') === action.NONE ? action.UPSERT : action.NONE,
            {commit: true}
        );

        return false;
    },

    /**
     * Обрабатываем эвент перед кликом на результат,
     * при клике на чекбокс - не выделяем
     */
    onBeforeitemclick: function (view, record, item, index, e) {
        var el = Ext.get(e.target),
            vertex = record.get('vertex'),
            selectionModel;

        // не выделяем группы
        if (Ext.isEmpty(vertex)) {
            return true;
        }

        if (!(el.hasCls('x-grid-cell-checkcolumn') ||
              el.hasCls('x-grid-cell-inner-checkcolumn') ||
              el.hasCls('x-grid-checkcolumn'))) {
            selectionModel = view.getSelectionModel();
            selectionModel.setLocked(false);
            selectionModel.select(record);
            selectionModel.setLocked(true);
        }
    },

    /**
     * Создаёт грид-дерево
     *
     * @param addTo
     * @param gridConfig
     * @param {GridSettings} settings
     * @returns {Ext.tree.Panel}
     */
    createGrid: function (addTo, gridConfig, settings) {
        var grid;

        gridConfig.store = this.createTreeStore(settings);

        grid = Ext.create(gridConfig);

        grid.getSelectionModel().setLocked(true);

        addTo.add(grid);

        return grid;
    },

    /**
     * Возвращает массив объектов
     *
     * @typedef {Object} Edge
     * @property {Object} from
     * @property {String} from.id
     * @property {Object} to
     * @property {String} to.id
     *
     * @returns {Edge[]}
     */
    getEdges: function () {
        var settingsData = this.getSettingsData();

        return settingsData.edges;
    },

    getParentIds: function (id) {
        var edges = this.getEdges(),
            result = [];

        if (!id) {
            return result;
        }

        Ext.Array.each(edges, function (edge) {
            if (edge.to.id === id) {
                result.push(edge.from.id);
            }
        });

        return result;
    },

    getChildIds: function (id) {
        var edges = this.getEdges(),
            result = [];

        if (!id) {
            return result;
        }

        Ext.Array.each(edges, function (edge) {
            if (edge.from.id === id) {
                result.push(edge.to.id);
            }
        });

        return Ext.Array.unique(result);
    },

    /**
     * Строит массив для дерева
     *
     * @param {GridSettings} settings
     * @returns {Object[]}
     */
    getTreeData: function (settings) {
        var viewModel = this.getViewModel(),
            vertexesStore = viewModel.getStore('vertexes'),
            typeMap = this.self.typeMap,
            tree = [],
            rootsMap = {};

        vertexesStore.each(function (vertex) {
            var vertexData = vertex.getData(),
                vertexType = vertexData.type;

            if (typeMap[vertexType] !== undefined) {
                // создаём родительский узел-группу
                if (rootsMap[vertexType] === undefined) {
                    rootsMap[vertexType] = {
                        id: vertexType,
                        leaf: false,
                        expanded: settings.expandGroups,
                        displayName: typeMap[vertexType],
                        children: []
                    };
                    tree.push(rootsMap[vertexType]);
                }

                rootsMap[vertexType].children.push(vertexData);
            } else {
                tree.push(vertexData);
            }

            vertexData.itemChecked = false;
            vertexData.vertex = vertex;
            vertexData.leaf = true;
            vertexData.iconCls = 'x-hidden';
        }, this);

        return tree;
    },

    /**
     * Создание стора для дерева
     *
     * @param {GridSettings} settings
     * @returns {Ext.data.TreeStore}
     */
    createTreeStore: function (settings) {
        var store;

        store = Ext.create('Ext.data.TreeStore', {
            model: 'Unidata.model.entity.modelimport.SettingsTreeNode',
            root: {
                expanded: true
            },
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                }
            }
        });

        this.setStoreData(store, this.getTreeData(settings));

        return store;
    },

    setStoreData: function (store, data) {
        store.getProxy().setData(data);
        store.reload();
    },

    /**
     * При установке данных - инициализируем стор
     *
     * @param settingsData
     */
    updateSettingsData: function (settingsData) {
        var viewModel = this.getViewModel(),
            vertexesStore = viewModel.getStore('vertexes');

        this.setStoreData(vertexesStore, settingsData.vertexes);
        this.setVertexChildParent(vertexesStore);
    },

    setDisabled: function () {
        var result = this.callParent(arguments);

        this.updateAggregatedCounts();

        return result;
    },

    updateAggregatedCounts: function () {
        var Vertex = Unidata.model.entity.metadependency.Vertex,
            action = Vertex.action,
            existence = Vertex.existence,
            viewModel = this.getViewModel(),
            vertexesStore = viewModel.getStore('vertexes'),
            checkedCount = 0,
            errorCount = 0,
            warningCount = 0,
            importCount = 0,
            newCount = 0,
            updateCount = 0,
            notfoundCount = 0,
            existCount = 0,
            nextStep,
            nextStepViewModel,
            data;

        vertexesStore.each(function (vertex) {
            var vertexAction = vertex.get('action'),
                vertexExistence = vertex.get('existence'),
                hasError = false;

            vertex.uiError = false;
            vertex.uiWarning = false;

            switch (vertexExistence) {
                case existence.NEW:
                    importCount++;
                    newCount++;
                    break;
                case existence.UPDATE:
                    importCount++;
                    updateCount++;
                    break;
                case existence.NOT_FOUND:
                    notfoundCount++;
                    break;
                case existence.EXIST:
                    existCount++;
                    break;
            }

            if (vertexAction === action.NONE) {
                return true;
            }

            checkedCount++;

            if (vertex.hasError()) {
                errorCount++;
                vertex.uiError = true;

                return true;
            }

            Ext.Array.each(vertex.child, function (childVertex) {
                var childVertexExistence = childVertex.get('existence'),
                    childVertexAction = childVertex.get('action');

                if (this.hasNestedError(vertex, childVertex) ||
                    childVertex.hasError() ||
                    (childVertexExistence === existence.NEW && childVertexAction === action.NONE)) {
                    errorCount++;
                    vertex.uiError = true;

                    hasError = true;

                    return false;
                }
            }, this);

            if (!hasError) {
                Ext.Array.each(vertex.parent, function (parentVertex) {
                    if (this.hasNestedError(vertex, parentVertex)) {
                        errorCount++;
                        vertex.uiError = true;

                        hasError = true;

                        return false;
                    }
                }, this);
            }

            if (!hasError) {
                Ext.Array.each(vertex.child, function (childVertex) {
                    var childVertexExistence = childVertex.get('existence'),
                        childVertexAction = childVertex.get('action');

                    if (childVertexExistence === existence.UPDATE && childVertexAction === action.NONE) {
                        warningCount++;
                        vertex.uiWarning = true;

                        return false;
                    }
                });
            }

        }, this);

        viewModel.set('errorCount', errorCount);

        nextStep = this.getNextStep();

        if (nextStep) {
            if (this.isDisabled()) {
                nextStep.setStepAllowed(false);
            } else {
                if (errorCount !== 0) {
                    nextStep.setStepAllowed(false);
                } else {
                    nextStep.setStepAllowed(true);
                    nextStep.setSettingsData(this.getSettingsData());
                    nextStep.setVertexesData(vertexesStore.getData());

                    nextStepViewModel = nextStep.getViewModel();

                    nextStepViewModel.set('checkedCount', checkedCount);
                    nextStepViewModel.set('importCount', importCount);
                }
            }
        }

        data = {
            newCount: newCount,
            updateCount: updateCount,
            notfoundCount: notfoundCount,
            existCount: existCount,
            importCount: importCount,
            checkedCount: checkedCount,
            warningCount: warningCount,
            errorCount: errorCount
        };

        this.lookupReference('aggregatedCounts').update(data);
        this.lookupReference('aggregatedTotalCounts').update(data);
    },

    /**
     * Устанавливает child и parent для vertexesStore
     *
     * @param vertexesStore
     */
    setVertexChildParent: function (vertexesStore) {
        var vertexesData = vertexesStore.getData();

        vertexesData.setExtraKeys({
            byId: {
                property: 'id'
            }
        });

        vertexesStore.each(function (vertex) {
            var id = vertex.get('id'),
                parentIds = this.getParentIds(id),
                childIds = this.getChildIds(id);

            vertex.child = this.getVertexes(vertexesData, childIds);
            vertex.parent = this.getVertexes(vertexesData, parentIds);

        }, this);
    },

    /**
     * Возвращает элементы из vertexesStore по id
     *
     * @param vertexesData
     * @param {Array} ids
     * @returns {Array}
     */
    getVertexes: function (vertexesData, ids) {
        var result = [];

        Ext.Array.each(ids, function (id) {
            var record = vertexesData.byId.get(id);

            if (record) {
                result.push(record);
            }
        });

        return result;
    },

    /**
     * Функция вызывается перед активацией следующего шага
     */
    beforeNextStepActivate: function () {
        var step = this.getNextStep(),
            nextStepViewModel;

        nextStepViewModel = step.getViewModel();
        nextStepViewModel.set('includeUsers', this.includeUsers.getValue());
        nextStepViewModel.set('includeRoles', this.includeRoles.getValue());
    }

});
