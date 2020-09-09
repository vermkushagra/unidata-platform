/**
 * Picker для выбора аттрибутов
 *
 * @author Sergey Shishigin
 * @date 2016-08-09
 */
Ext.define('Unidata.view.component.AttributePicker', {

    extend: 'Ext.form.field.Picker',

    alias: 'widget.un.attributepicker',

    mixins: [
        'Unidata.mixin.Tooltipable'
    ],

    requires: [
        'Unidata.view.component.AttributeTree'
    ],

    config: {
        isArrayAttributesHidden: false,
        metaRecordKey: null,
        metaRecord: null
    },

    pickerPanel: null,

    matchFieldWidth: false,
    disabled: true,
    editable: false,

    emptyText: Unidata.i18n.t('classifier>selectAttribute'),

    initComponent: function () {
        this.callParent(arguments);

        this.getPicker();

        this.initListeners();
    },

    onCollapseIf: function (e) {
        var picker = this.getPicker();

        if (picker && picker.rendered && e.within(picker.el)) {
            if (e.type !== 'mousewheel') {
                e.preventDefault();
                e.stopPropagation();
            }
        } else {
            this.collapse();
        }
    },

    initListeners: function () {
        this.on('collapseIf', this.onCollapseIf, this);
        this.pickerPanel.on('select', this.onAttributeSelect, this);
    },

    onAttributeSelect: function (tree, attribute) {
        if (this.getValue() !== attribute.get('name')) {
            this.setValue(attribute.get('name'));
        }
        this.collapse();
    },

    /**
     * Обработчик потери фокуса компонентом
     *
     * @param e
     */
    onFocusLeave: function (e) {
        var fromComponent = e.fromComponent,
            isCollapseEnabled = true;

        if (fromComponent instanceof Ext.tree.View) {
            if (fromComponent.grid instanceof Unidata.view.component.AttributeTree) {
                isCollapseEnabled = false;
            }
        } else if (fromComponent instanceof Ext.toolbar.Toolbar) {
            isCollapseEnabled = false;
        }

        if (isCollapseEnabled) {
            this.collapse();
            this.callParent(arguments);
        }
    },

    buildAttributeTree: function (customCfg) {
        var attributeTree,
            cfg;

        cfg = {
            title: Unidata.i18n.t('glossary:attributes'),
            overflowY: 'auto',
            width: 400,
            isComplexAttributesHidden: true,
            isArrayAttributesHidden: this.getIsArrayAttributesHidden(),
            hideAttributeFilter: this.hideAttributeFilter.bind(this)
        };

        cfg = Ext.apply(cfg, customCfg);

        attributeTree = Ext.create('Unidata.view.component.AttributeTree', cfg);

        return attributeTree;
    },

    /**
     * Фильтр для сокрытия атрибутов в дереве
     *
     * @private
     * @param {Unidata.model.attribute.AbstractAttribute}
     * @returns {Boolean}
     */
    hideAttributeFilter: function (record) {
        var linkDataType   = record.get('linkDataType'),
            simpleDataType = record.get('simpleDataType');

        return linkDataType || simpleDataType == 'Blob' || simpleDataType == 'Clob';
    },

    createPicker: function () {
        var picker,
            metaRecord = this.getMetaRecord(),
            cfg;

        cfg = {
            metaRecord: metaRecord
        };

        this.pickerPanel = this.buildAttributeTree(cfg);

        picker = Ext.create('Ext.panel.Panel', {
            pickerField: this,
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

    updateMetaRecord: function (metaRecord) {
        var isMetaRecord = Boolean(metaRecord);

        this.setDisabled(!isMetaRecord);

        if (!this.pickerPanel) {
            return;
        }

        this.pickerPanel.setData(metaRecord);

        if (this.value) {
            this.setValue(this.value);
        }
    },

    updateMetaRecordKey: function (metaRecordKey) {
        var MetaRecordApi = Unidata.util.api.MetaRecord,
            isMetaRecordKey = Boolean(metaRecordKey),
            metaRecord = this.getMetaRecord(),
            promise;

        this.setDisabled(!isMetaRecordKey);

        if (!isMetaRecordKey || (metaRecord && metaRecordKey === metaRecord.getMetaRecordKey())) {
            return;
        }

        this.setLoading(true);
        this.setDisabled(true);
        promise = MetaRecordApi.getMetaRecord(metaRecordKey);
        promise
            .then(this.onLoadMetaRecordSuccess.bind(this), this.onLoadMetaRecordFailure.bind(this))
            .done();
    },

    /**
     * @private
     */
    onLoadMetaRecordSuccess: function (metaRecord) {
        this.setMetaRecord(metaRecord);
        this.setLoading(false);
        this.setDisabled(false);
    },

    /**
     * @private
     */
    onLoadMetaRecordFailure: function () {
        this.setLoading(false);
        this.setDisabled(false);
    },

    setAttributeTreeSelection: function (value) {
        var selection = null,
            currentSelection = null;

        if (value) {
            selection = this.getAttributeByValue(value);
        }
        this.pickerPanel.setSelection(this.getAttributeByValue(value));

        currentSelection = this.pickerPanel.getSelection();

        if (currentSelection !== selection) {
            this.pickerPanel.setSelection(selection);
        }
    },

    setValue: function (value) {
        if (this.pickerPanel) {
            this.setAttributeTreeSelection(value);
        }

        return this.callParent(arguments);
    },

    getAttribute: function () {
        var value = this.getValue();

        if (!value) {
            return;
        }

        return this.getAttributeByValue(value);
    },

    getAttributeByValue: function (value) {
        var treeStore,
            index;

        if (!value || !this.pickerPanel) {
            return false;
        }

        treeStore = this.pickerPanel.store;

        index = treeStore.findExact('path', value);

        if (index !== -1) {
            return treeStore.getAt(index);
        }

        return false;
    },

    transformRawValue: function (value) {
        var displayValue = value,
            attribute;

        attribute = this.getAttributeByValue(value);

        if (attribute) {
            displayValue = attribute.get('record').get('displayName');
        }

        return displayValue;
    },

    getValue: function () {
        return this.value;
    },

    /**
     * Для сабмита возвращаем
     * @returns {String}
     */
    getSubmitValue: function () {
        var attribute =  this.getAttribute();

        if (attribute) {
            return attribute.get('record').get('name');
        } else {
            return '';
        }
    }
});
