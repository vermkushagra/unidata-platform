/**
 * Пикер со списком правил поиска дубликатов
 *
 * @author Ivan Marshalkin
 * @date 2016-10-27
 */

Ext.define('Unidata.view.component.DuplicateRuleComboBox', {
    extend: 'Ext.form.field.Picker',

    requires: [
        'Unidata.view.admin.duplicates.list.RuleGrid'
    ],

    xtype: 'un.duplicaterulecombo',

    config: {
        dataLoading: false,
        entityName: null                  // кодовое имя реестра / справочника
    },

    pickerPanel: null,                    // ссылка на компонент отображающий дерево

    disabled: true,
    editable: false,

    initComponent: function () {
        this.callParent(arguments);

        // создаем picker во время инициализации компонента, чтоб в дальнейшем использовать ссылку this.pickerPanel
        this.getPicker();

        this.pickerPanel.on('select', this.onRuleSelect, this);
        this.pickerPanel.on('deselect', this.onRuleDeselect, this);
    },

    updateEntityName: function (entityName) {
        this.pickerPanel.setEntityName(entityName);

        if (entityName) {
            this.setDisabled(true);
            this.setLoading({
                ui: 'loader-left'
            });
            this.pickerPanel.reloadRuleList();
        }
    },

    setLoading: function (flag) {
        if (flag) {
            this.setDataLoading(true);
        } else {
            this.setDataLoading(false);
        }

        return this.callParent(arguments);
    },

    /**
     * Создание выпадающего элемента
     *
     * @returns {Ext.panel.Panel|*}
     */
    createPicker: function () {
        var me = this,
            picker;

        this.pickerPanel = Ext.create('Unidata.view.admin.duplicates.list.RuleGrid', {
            listeners: {
                roleload: {
                    fn: me.onRoleStoreLoad,
                    scope: me
                }
            }
        });

        picker = Ext.create('Ext.panel.Panel', {
            pickerField: me,
            floating: true,
            hidden: true,
            height: 400,
            anchor: '100% 100',
            overflowY: 'auto',
            items: [
                this.pickerPanel
            ]
        });

        return picker;
    },

    onRuleSelect: function (grid, record) {
        this.setValue(record.get('id'));

        this.collapse();
    },

    onRuleDeselect: function () {
        this.setValue(null);
    },

    /**
     * Переопределяем функцию базового класса, т.к. в нашем случае id это число, а базовая функция возвращает строку
     *
     * @param value
     * @returns {*}
     */
    valueToRaw: function (value) {
        return value;
    },

    getRecordByValue: function (value) {
        var store,
            resultRecord;

        if (!value || !this.pickerPanel) {
            return false;
        }

        store = this.pickerPanel.getStore();

        store.each(function (record) {
            if (!resultRecord && record.get('id') === value) {
                resultRecord = record;
            }
        });

        if (resultRecord) {
            return resultRecord;
        }

        return false;
    },

    transformRawValue: function (value) {
        var record = this.getRecordByValue(value);

        if (record) {
            return record.get('name');
        }

        return value;
    },

    onRoleStoreLoad: function () {
        this.setLoading(false);
        this.setDisabled(false);
        this.setValue(this.value);
    },

    /**
     * Переопределяем метод т.к. оригинальный возвращает отображаемое имя
     *
     * @returns {*}
     */
    getValue: function () {
        return this.value;
    },

    setValue: function (value) {
        if (!value && this.pickerPanel && this.pickerPanel.getSelection()) {
            this.pickerPanel.setSelection(null);
        }

        return this.callParent(arguments);
    },

    /**
     * Возвращает все правила сопоставления
     *
     * @returns {*|Ext.data.Model[]}
     */
    getMatchingRules: function () {
        return this.pickerPanel.getMatchingRules();
    }
});
