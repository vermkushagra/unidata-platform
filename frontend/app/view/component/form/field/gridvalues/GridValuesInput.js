/**
 * @author Aleksandr Bavin
 * @date 2017-09-19
 */
Ext.define('Unidata.view.component.form.field.gridvalues.GridValuesInput', {

    extend: 'Ext.form.field.Text',

    alias: 'widget.gridvalues.input',

    editable: false,
    preventLabelClick: true,

    triggers: {
        edit: {
            cls: 'icon-cog'
        }
    },

    cls: 'gridvalues-input',

    labelClsExtra: 'label-dashed',

    value: [], // массив объектов

    gridColumnsDefaults: null,
    gridColumns: [], // конфигурация колонок грида

    gridStore: null,          // стор для грида
    gridStoreFields: [],      // поля стора
    gridStoreInited: false,   // true после инициализации стора
    gridStoreListeners: null,

    gridWindow: null, // окно с гридом для редактирования

    onRender: function () {
        var editTrigger;

        this.callParent(arguments);

        editTrigger = this.getTrigger('edit');

        this.inputEl.on('click', this.onInputClick, this);

        editTrigger.getEl().on({
            click: this.onInputClick.bind(this)
        });
    },

    setReadOnly: function (readOnly) {
        var gridWindow = this.getGridWindow();

        gridWindow.setReadOnly(readOnly);

        return this.callParent(arguments);
    },

    didValueChange: function (newVal, oldVal) {
        var newValIsArray = Ext.isArray(newVal),
            oldValIsArray = Ext.isArray(oldVal),
            i,
            ln;

        // одно из значений не массив и они не равны
        if ((!newValIsArray || !oldValIsArray) && newValIsArray !== oldValIsArray) {
            return true;
        }

        // массивы разной длины
        if (newVal.length !== oldVal.length) {
            return true;
        }

        for (i = 0, ln = newVal.length; i < ln; i++) {
            if (!Ext.Object.equals(newVal[i], oldVal[i])) {
                return true;
            }
        }

        return false;
    },

    onDestroy: function () {
        this.callParent(arguments);

        if (this.gridWindow) {
            this.gridWindow.destroy();
            this.gridWindow = null;
        }
    },

    onInputClick: function () {
        this.getGridWindow().show();
    },

    getGridWindow: function () {
        if (this.gridWindow) {
            return this.gridWindow;
        }

        this.gridWindow = Ext.create({
            xtype: 'gridvalues.window',
            gridStore: this.getGridStore(),
            gridColumnsDefaults: this.gridColumnsDefaults,
            gridColumns: this.gridColumns,
            listeners: {
                scope: this,
                hide: this.onWindowHide
            }
        });

        return this.gridWindow;
    },

    onWindowHide: function () {
        this.checkChange();
        Ext.defer(this.updateInputText, 100, this);
    },

    getGrid: function () {
        return this.getGridWindow().getGrid();
    },

    setGridStore: function (gridStore) {
        if (!gridStore) {
            return;
        }

        Ext.destroy(this.gridStoreListeners);

        this.gridStoreInited = false;
        this.gridStore = this.initGridStore(gridStore);
        this.getGrid().setStore(gridStore);

        Ext.defer(this.updateInputText, 100, this);
    },

    /**
     * @returns {Ext.data.Store}
     */
    getGridStore: function () {
        if (this.gridStore) {
            return this.initGridStore(this.gridStore);
        }

        this.gridStore = this.initGridStore(this.createGridStore());

        return this.gridStore;
    },

    /**
     * @returns {Ext.data.Store}
     */
    createGridStore: function () {
        return Ext.create('Ext.data.Store', {fields: this.gridStoreFields});
    },

    /**
     * Инициализация стора
     *
     * @param {Ext.data.Store} gridStore
     * @returns {Ext.data.Store}
     * @private
     */
    initGridStore: function (gridStore) {
        if (this.gridStoreInited) {
            return gridStore;
        }

        this.gridStoreListeners = gridStore.on({
            update: this.onStoreItemUpdate,
            destroyable: true,
            scope: this
        });

        this.gridStoreInited = true;

        return gridStore;
    },

    onStoreItemUpdate: function () {
        this.updateEmptyRow();
    },

    /**
     * Добавляет пустой ряд в конец
     */
    updateEmptyRow: function () {
        this.getGridWindow().updateEmptyRow();
    },

    updateInputText: function () {
        this.setRawValue(Ext.String.format(Unidata.i18n.t('component>form.field.gridvalues.givenValues'), this.getGridStore().getCount()));
    },

    setValue: function (value) {
        var gridStore = this.getGridStore();

        gridStore.loadData(value);
        gridStore.commitChanges();

        this.updateInputText();

        return this;
    },

    getValue: function () {
        var store = this.getGridStore(),
            idProperty = store.getModel().idProperty,
            resultValue = [];

        store.each(function (record) {
            var recordData = Ext.clone(record.getData()),
                isEmptyRecord = true;

            if (!record.isValid()) {
                return;
            }

            delete recordData[idProperty]; // удаляем id, т.к. не нужен

            Ext.Object.each(recordData, function (key, value) {

                if (!Ext.isEmpty(value)) {
                    isEmptyRecord = false;

                    return false;
                }
            });

            if (!isEmptyRecord) {
                resultValue.push(recordData);
            }
        });

        return resultValue;
    },

    getSubmitValue: function () {
        return this.getValue();
    }

});
