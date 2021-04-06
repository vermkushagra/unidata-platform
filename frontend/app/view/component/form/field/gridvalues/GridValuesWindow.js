/**
 * @author Aleksandr Bavin
 * @date 2017-09-19
 */
Ext.define('Unidata.view.component.form.field.gridvalues.GridValuesWindow', {

    extend: 'Ext.window.Window',

    alias: 'widget.gridvalues.window',

    layout: 'fit',

    cls: 'un-gridvalues-window',

    dockedItems: {
        xtype: 'toolbar',
        reference: 'toolbar',
        ui: 'footer',
        dock: 'bottom',
        items: []
    },

    referenceHolder: true,

    header: false,
    resizable: false,
    modal: true,
    autoDestroy: false,
    closeAction: 'hide',

    width: 700,
    height: 600,

    grid: null,
    gridStore: null,

    gridColumnsDefaults: null,
    gridColumns: [],

    initComponent: function () {
        this.callParent(arguments);

        this.on('beforeshow', this.onBeforeShow, this);
        this.on('beforehide', this.onBeforeHide, this);

        this.initToolbar();
    },

    initToolbar: function () {
        var toolbar = this.lookupReference('toolbar');

        toolbar.add(
            {
                xtype: 'container',
                flex: 1,
                layout: {
                    type: 'hbox',
                    pack: 'center'
                },
                defaults: {
                    margin: '0 5 0 5',
                    minWidth: 75
                },
                items: [
                    {
                        xtype: 'button',
                        reference: 'saveButton',
                        text: Unidata.i18n.t('component>form.field.gridvalues.apply'),
                        scope: this,
                        handler: this.onSaveClick
                    },
                    {
                        xtype: 'button',
                        text: Unidata.i18n.t('component>form.field.gridvalues.cancel'),
                        scope: this,
                        handler: this.onCancelClick
                    }
                ]
            },
            {
                xtype: 'button',
                reference: 'deleteButton',
                ui: 'un-fontbutton',
                iconCls: 'icon-trash2',
                scale: 'large',
                tooltip: Unidata.i18n.t('component>form.field.gridvalues.delete'),
                scope: this,
                handler: function () {
                    var grid = this.getGrid(),
                        gridStore = this.getGridStore(),
                        selection = grid.getSelection();

                    grid.getSelectionModel().deselectAll();

                    gridStore.remove(selection);

                    this.updateEmptyRow();
                }
            }
        );
    },

    onBeforeShow: function () {
        this.isEditing = true;
        this.updateEmptyRow();
        this.getGrid().getSelectionModel().deselectAll();
    },

    onBeforeHide: function () {
        this.isEditing = false;
        this.updateEmptyRow(true);
        this.getGridStore().rejectChanges();
    },

    setReadOnly: function (readOnly) {
        var grid = this.getGrid(),
            cellediting = grid.findPlugin('cellediting'),
            deleteButton = this.lookupReference('deleteButton'),
            saveButton = this.lookupReference('saveButton');

        this.readOnly = readOnly;

        if (readOnly) {
            deleteButton.hide();
            saveButton.hide();
            cellediting.disable();
            this.addCls('un-readonly-true');
            this.removeCls('un-readonly-false');
        } else {
            cellediting.enable();
            deleteButton.show();
            saveButton.show();
            this.addCls('un-readonly-false');
            this.removeCls('un-readonly-true');
        }

        grid.getSelectionModel().deselectAll();
        this.updateEmptyRow();
    },

    /**
     * Добавляет пустой ряд в конец
     */
    updateEmptyRow: function (skipEditCheck) {
        var gridWindow = this,
            gridStore = this.getGridStore(),
            gridStoreCount = gridStore.getCount(),
            lastRowIsEmpty = true,
            lastRow,
            lastRowIdProperty;

        if (skipEditCheck !== true) {
            if (!this.isEditing) {
                return;
            }
        }

        if (gridStoreCount) {
            lastRow = gridStore.getAt(gridStoreCount - 1);

            lastRowIdProperty = lastRow.getIdProperty();

            Ext.Object.each(lastRow.getData(), function (key, value) {
                if (key === lastRowIdProperty) {
                    return;
                }

                if (!Ext.isEmpty(value)) {
                    lastRowIsEmpty = false;

                    return false;
                }
            });
        } else {
            lastRowIsEmpty = false;
        }

        if (this.readOnly) {
            if (lastRowIsEmpty) {
                gridStore.removeAt(gridStoreCount - 1);
            }
        } else {
            if (!this.isEditing) {
                if (lastRowIsEmpty) {
                    gridStore.removeAt(gridStoreCount - 1);
                }
            } else {
                if (!lastRowIsEmpty) {
                    gridStore.add({});
                }
            }
        }
    },

    onSaveClick: function () {
        var gridStore = this.getGridStore(),
            gridStoreCount = gridStore.getCount();

        if (gridStoreCount) {
            this.isEditing = false;
            this.updateEmptyRow(true);

            gridStore.commitChanges();

            this.hide();
        }

        this.hide();
    },

    onCancelClick: function () {
        this.getGridStore().rejectChanges();
        this.hide();
    },

    initItems: function () {
        this.callParent(arguments);
        this.initGrid();
    },

    getGrid: function () {
        return this.grid;
    },

    getGridStore: function () {
        return this.getGrid().getStore();
    },

    initGrid: function () {
        var gridColumns = Ext.clone(this.gridColumns),
            gridColumnsDefaults = Ext.clone(this.gridColumnsDefaults) || {},
            grid;

        if (!gridColumnsDefaults.renderer) {
            gridColumnsDefaults.renderer = function (value, metaData, record) {
                if (record.phantom && !value && (metaData.rowIndex + 1) === this.getStore().getCount()) {
                    return Unidata.i18n.t('component>form.field.gridvalues.enterValue');
                }

                return value;//this.defaultRenderer(value, metaData, record);
            };
        }

        Ext.Array.each(gridColumns, function (column, index, arr) {
            arr[index] = Ext.Object.merge({}, gridColumnsDefaults, column);
        });

        this.initStoreEvents(this.gridStore, null);

        this.grid = grid = Ext.create({
            xtype: 'grid',

            scrollable: true,
            // selType: 'rowmodel',

            selModel: {
                selType: 'checkboxmodel',
                checkOnly: true,
                pruneRemoved: false,
                injectCheckbox: 'last'
            },

            modelValidation: true,

            plugins: {
                ptype: 'cellediting',
                clicksToEdit: 1
            },

            viewConfig: {
                getRowClass: function (record, rowIndex) {
                    var isLastRow = ((rowIndex + 1) === this.getStore().getCount());

                    if (isLastRow) {
                        return '';
                    }

                    return (record.isValid()) ? '' : 'un-gridvalues-invalid';
                }
            },

            store: this.gridStore,

            columns: {
                defaults: {
                    draggable: false,
                    sortable: false,
                    hideable: false
                    // flex: 1
                    // editor: {
                    //     xtype: 'textfield'
                    // }
                    // renderer: function (value, metaData, record) {
                    //     if (record.phantom && !value && (metaData.rowIndex + 1) === this.getStore().getCount()) {
                    //         return Unidata.i18n.t('component>form.field.gridvalues.enterValue');
                    //     }
                    //
                    //     return value;//this.defaultRenderer(value, metaData, record);
                    // }
                },
                items: gridColumns
            },

            listeners: {
                scope: this,
                reconfigure: this.onGridReconfigure
            }
        });

        this.add(grid);
    },

    onGridReconfigure: function (grid, store, columns, oldStore, oldColumns, eOpts) {
        this.initStoreEvents(store, oldStore);
    },

    initStoreEvents: function (newStore, oldStore) {
        if (oldStore) {
            oldStore.un('update', this.onStoreItemUpdate, this);
            oldStore.un('remove', this.onStoreItemUpdate, this);
        }

        newStore.on('update', this.onStoreItemUpdate, this);
        newStore.on('remove', this.onStoreItemUpdate, this);
    },

    onStoreItemUpdate: function (store) {
        var storeCount = store.getCount(),
            isValid = true,
            saveButton = this.lookupReference('saveButton');

        store.each(function (item, index) {
            var isLast = ((index + 1) === storeCount);

            if (isLast) {
                return;
            }

            if (!item.isValid()) {
                isValid = false;

                return false;
            }
        }, this);

        saveButton.setDisabled(!isValid);
    }

});
