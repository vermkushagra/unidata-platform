/**
 * Пикер со списком групп правил поиска дубликатов
 *
 * @author Ivan Marshalkin
 * @date 2016-10-27
 */

Ext.define('Unidata.view.component.DuplicateGroupComboBox', {
    extend: 'Ext.form.field.Picker',

    requires: [
        'Unidata.view.admin.duplicates.group.GroupTree'
    ],

    xtype: 'un.duplicategroupcombo',

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

        this.pickerPanel.on('select', this.onGroupItemSelect, this);
        this.pickerPanel.on('deselect', this.onGroupItemDeselect, this);

        this.pickerPanel.reloadRootNode();
    },

    updateEntityName: function (entityName) {
        this.pickerPanel.setEntityName(entityName);

        if (entityName) {
            this.setDisabled(true);
            this.setLoading({
                ui: 'loader-left'
            });
            this.pickerPanel.reloadRootNode();
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

        this.pickerPanel = Ext.create('Unidata.view.admin.duplicates.group.GroupTree', {
            listeners: {
                groupload: {
                    fn: me.onGroupStoreLoad,
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

    onGroupItemSelect: function (tree, record) {
        this.setValue(record.get('record'));

        this.collapse();
    },

    onGroupItemDeselect: function () {
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

    isGroupValue: function () {
        return this.getValue() instanceof Unidata.model.matching.Group;
    },

    isRuleValue: function () {
        return this.getValue() instanceof Unidata.model.matching.Rule;
    },

    getGroupId: function () {
        var groupId = null,
            parentNodeRecord;

        if (this.isGroupValue()) {
            groupId = this.getValue().getId();
        }

        if (this.isRuleValue()) {
            parentNodeRecord = this.pickerPanel.selection.parentNode.get('record');

            if (parentNodeRecord) {
                groupId = parentNodeRecord.getId();
            }
        }

        return groupId;
    },

    getRuleId: function () {
        return this.isRuleValue() ? this.getValue().getId() : null;
    },

    transformRawValue: function (record) {
        if (record) {
            return record.get('name');
        }

        return record;
    },

    onGroupStoreLoad: function () {
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
     * Возвращает все группы сопоставления
     *
     * @returns {Unidata.model.matching.Group[]}
     */
    getMatchingGroups: function () {
        return this.pickerPanel.getMatchingGroups();
    },

    /**
     * Возвращает все правила сопоставления
     *
     * @returns {Unidata.model.matching.Rule[]}
     */
    getMatchingRules: function () {
        return this.pickerPanel.getMatchingRules();
    }
});
