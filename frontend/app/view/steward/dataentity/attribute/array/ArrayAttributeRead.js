/**
 * @author Aleksandr Bavin
 * @date 2017-01-20
 */
Ext.define('Unidata.view.steward.dataentity.attribute.array.ArrayAttributeRead', {

    extend: 'Ext.container.Container',

    alias: 'widget.arrayattribute.read',

    viewModel: {
        stores: {
            tagStore: {
                fields: ['tagDisplayValue', 'value'],
                proxy: {
                    type: 'memory',
                    reader: {
                        type: 'json'
                    }
                }
            }
        }
    },

    cls: 'un-array-attribute-read',

    referenceHolder: true,

    layout: 'fit',

    config: {
        tagLimit: 5,    // максимальное количество тэгов для отображения
        tipTagLimit: 20, // максимум для тултипа тэга "еще"
        value: null,

        metaRecord: null,
        dataRecord: null,
        metaAttribute: null,
        dataAttribute: null,
        attributePath: null,
        readOnly: null,
        preventMarkField: null,
        tipTpl: null
    },

    tagField: null,

    fakeTagId: 'fakeTag',
    fakeTag: null, // фейковый тэг, для отображения "...еще X..."

    moreStringFormat:  Unidata.i18n.t('dataentity>more'),

    items: [],

    onDestroy: function () {
        this.callParent(arguments);

        this.tagField = null;
        this.fakeTag = null;
    },

    initItems: function () {
        this.callParent(arguments);

        this.add(this.createSimpleAttribute(null));
        this.add(this.getTagField());
    },

    getTagField: function () {
        var tipTpl = this.getTipTpl();

        if (this.tagField) {
            return this.tagField;
        }

        tipTpl = tipTpl || this.initTipTpl();

        this.tagField = Ext.widget({
            xtype: 'arrayattribute.tagfield',
            store: this.getTagStore(),
            readOnly: this.getReadOnly(),
            value: [],
            displayField: 'tagDisplayValue',
            valueField: 'id',
            queryMode: 'local',
            tipTpl: tipTpl,
            listeners: {
                faketagclick: this.onFakeTagClick,
                valueselectionchange: this.valueSelectionChange,
                scope: this
            }
        });

        this.tagField.selectionModel.pruneRemoved = false;

        return this.tagField;
    },

    updateReadOnly: function (readOnly) {
        if (this.tagField) {
            this.tagField.setReadOnly(readOnly);
        }
    },

    initTipTpl: function () {
        return new Ext.XTemplate([
            '{tip}'
        ]);
    },

    updateTagLimit: function (limit) {
        if (this.tagField) {
            this.displayTags(limit);
        }
    },

    updateTipTagLimit: function () {
        if (this.tagField) {
            this.displayTags(this.getTagLimit());
        }
    },

    /**
     * Отображает тэги
     *
     * @param limit - количество тэгов для отображения
     */
    displayTags: function (limit) {
        var tagStore = this.getTagStore(),
            tagFieldRange = tagStore.getRange(),
            readOnly = this.getReadOnly(),
            tagFieldValues = [],
            fakeTagTip = [],
            fakeTagCount,
            fakeTagTipCount,
            tagStoreCount,
            tagField;

        Ext.Array.each(tagFieldRange, function (item, index) {
            var id = item.get('id'),
                value = item.get('value');

            if (id === this.fakeTagId) {
                return;
            }

            if (index < limit) {
                tagFieldValues.push(id);
            } else {
                if (index < limit + this.getTipTagLimit()) {
                    fakeTagTip.push(value);
                }
            }
        }, this);

        tagStoreCount = tagStore.getCount();

        // -1 фейк тэг
        fakeTagCount = tagStoreCount - tagFieldValues.length - 1;

        // добавляем для отображения фейковый тэг, если нужно
        if (fakeTagCount > 0) {
            fakeTagTipCount = tagStoreCount - fakeTagTip.length - tagFieldValues.length - 1;

            if (fakeTagTipCount > 0) {
                fakeTagTip.push(Ext.String.format(this.moreStringFormat, fakeTagTipCount));
            }

            this.fakeTag.set('tagDisplayValue', Ext.String.format(this.moreStringFormat, fakeTagCount));
            this.fakeTag.set('tip', fakeTagTip.join('<br>'));
            tagFieldValues.push(this.fakeTagId);
        }

        if (tagStoreCount === 1 && !readOnly) {
            this.fakeTag.set('tagDisplayValue', Unidata.i18n.t('dataentity>noDisplayData'));
            tagFieldValues.push(this.fakeTagId);
        }

        tagField = this.getTagField();

        tagField.setValue(null);
        tagField.setValue(tagFieldValues);

        this.fireEvent('updatelayout');
    },

    /**
     * При выборе тэга
     *
     * @param tagfield
     * @param {Array} selected
     */
    valueSelectionChange: function (tagfield, selected) {
        var tag = selected[0];

        if (tag) {
            this.onTagSelect(tag);
        } else {
            this.onTagDeselect();
        }
    },

    onTagSelect: function (tag) {
        if (this.deselectTimer) {
            clearTimeout(this.deselectTimer);
            this.deselectTimer = null;
        }

        this.showSimpleAttribute(tag);
        this.updateValueAndStore();
        this.fireEvent('tagselect', tag);
    },

    onTagDeselect: function () {
        //this.hideSimpleAttribute();
    },

    onFakeTagClick: function () {
        var limit = this.getTagLimit() + this.getTipTagLimit();

        this.setTagLimit(limit);
    },

    getTagStore: function () {
        return this.getViewModel().getStore('tagStore');
    },

    setValue: function (value) {
        value = Ext.clone(value);

        this.initStoreData(value);

        return this.callParent(arguments);
    },

    initStoreData: function (value) {
        var tagStore = this.getTagStore(),
            fakeTagCls = ['un-array-attribute-fake-tag'];

        // формируем данные для стора
        Ext.Array.each(value, function (item, index, array) {
            // удаляем пустые элементы
            if (Ext.isEmpty(item.value)) {
                array.splice(index, 1);
            }

            if (item.displayValue) {
                item.tagDisplayValue = item.displayValue;
            } else {
                item.tagDisplayValue = this.formatValue(item.value);
            }

            // поле для подсказок
            item.tip = item.tagDisplayValue;
        }, this, true);

        tagStore.setData(value);

        if (tagStore.getCount() === 0) {
            fakeTagCls.push('un-array-attribute-fake-tag-empty');
        }

        this.fakeTag = tagStore.add({
            id: this.fakeTagId,
            tagCls: fakeTagCls.join(' ')
        })[0];

        this.displayTags(this.getTagLimit());
    },

    formatValue: function (value) {
        var metaAttribute = this.getMetaAttribute(),
            typeValue = metaAttribute.get('typeValue'),
            dateTypes = ['Date', 'Timestamp', 'Time'],
            dateParsed;

        if (dateTypes.indexOf(typeValue) !== -1) {
            dateParsed = Ext.Date.parse(value, 'Y-m-dTH:i:s.u');

            if (!dateParsed) {
                dateParsed = Ext.Date.parse(value, 'Y-m-dTH:i:sP');
            }

            switch (typeValue) {
                case 'Date':
                    value = Ext.Date.format(dateParsed, 'd.m.Y');
                    break;
                case 'Time':
                    value = Ext.Date.format(dateParsed, 'H:i:s');
                    break;
                case 'Timestamp':
                default:
                    value = Ext.Date.format(dateParsed, 'd.m.Y H:i:s');
            }
        } else if (!Ext.isEmpty(value)) {
            switch (typeValue) {
                case 'Number':
                    value = value.toString().replace('.', Ext.util.Format.decimalSeparator);
                    break;
            }
        }

        return value;
    },

    createSimpleAttribute: function (value) {
        var view = this,
            metaAttribute = view.getMetaAttribute(),
            metaAttributeData = metaAttribute.getData(),
            dataAttribute = view.getDataAttribute(),
            dataAttributeData = dataAttribute.getData(),
            dataAttributeClone,
            metaAttributeClone,
            simpleAttribute;

        dataAttributeData.value = value;
        delete dataAttributeData.id;

        dataAttributeClone = dataAttribute.self.create(dataAttributeData);

        // избавляемся от лишних проверок, всё проверяется ну уровне ArrayAttribute
        metaAttributeData.nullable = true;
        delete metaAttributeData.id;

        metaAttributeClone = metaAttribute.self.create(metaAttributeData);

        simpleAttribute = Ext.create('Unidata.view.steward.dataentity.attribute.SimpleAttribute', {
            hidden: true,
            flex: 1,
            inputVisible: true,
            renderData: {
                hideIndicator: true
            },
            hideAttributeTitle: true,
            metaRecord: view.getMetaRecord(),
            dataRecord: view.getDataRecord(),
            metaAttribute: metaAttributeClone,
            dataAttribute: dataAttributeClone,
            attributePath: null, // предположительно нам он не потребуется
            readOnly: false,
            disabled: false,
            preventMarkField: view.getPreventMarkField()
        });

        simpleAttribute.on('focus', this.onSimpleAttributeFocus, this);
        simpleAttribute.on('blur', this.onSimpleAttributeBlur, this);

        this.simpleAttribute = simpleAttribute;

        return simpleAttribute;
    },

    showSimpleAttribute: function (tag) {
        var value = null,
            displayValue,
            dataAttribute,
            dataAttributeData;

        if (tag) {
            value = tag.get('value');
            displayValue = tag.get('displayValue');
            this.simpleAttribute.setDataAttribute(tag);
        } else {
            dataAttribute = this.simpleAttribute.getDataAttribute();
            dataAttributeData = dataAttribute.getData();
            dataAttributeData.value = null;
            delete dataAttributeData.id;
            this.simpleAttribute.setDataAttribute(dataAttribute.self.create(dataAttributeData));
            this.addNewSimpleAttributeListeners(this.simpleAttribute);
        }

        this.simpleAttribute.setDisplayValue(displayValue);
        this.simpleAttribute.setValue(value);
        this.simpleAttribute.show();
        this.simpleAttribute.focusInput();
    },

    addNewSimpleAttributeListeners: function (simpleAttribute) {
        var simpleAttributeInput = simpleAttribute.getInput();

        simpleAttributeInput.on(
            'specialkey',
            this.onSpecialKey,
            this,
            {
                args: [simpleAttribute],
                /**
                 * Повышаем приоритет, что бы можно было отменить событие blur
                 * @see Unidata.view.steward.dataentity.attribute.AbstractAttribute.blurOnSpecialKey
                 */
                priority: 1
            }
        );

        simpleAttribute.on('blur', this.removeSpecialKeyListener, this, {single: true, args: [simpleAttributeInput]});
    },

    removeSpecialKeyListener: function (simpleAttributeInput) {
        simpleAttributeInput.un('specialkey', this.onSpecialKey, this);
    },

    /**
     * Обработчик событий для только что созданного инпута,
     * на ENTER - создаётся новый
     */
    onSpecialKey: function (simpleAttribute, simpleAttributeInput, e) {
        if (e.getKey() === e.ENTER) {
            if (!simpleAttribute.isValid()) {
                e.stopEvent();

                return;
            }

            if (Ext.isEmpty(simpleAttribute.getValue())) {
                e.stopEvent(); // не даём сработать blur
            } else {
                simpleAttributeInput.un('specialkey', this.onSpecialKey, this);
                this.updateValueAndStore();
                this.showSimpleAttribute();
            }
        }
    },

    hideSimpleAttribute: function () {
        this.simpleAttribute.hide();
    },

    onSimpleAttributeFocus: function (simpleAttribute, event, eOpts) {
        this.fireEvent('focus', this, event, eOpts);
    },

    onSimpleAttributeBlur: function (simpleAttribute, event, eOpts) {
        this.deselectTimer = Ext.defer(function () {
            this.updateTagField();

            this.fireEvent('blur', this, event, eOpts);
        }, 200, this);
    },

    updateTagField: function () {
        this.updateValueAndStore();

        this.getTagField().selectionModel.deselectAll();
    },

    updateValueAndStore: function () {
        var tagStore = this.getTagStore(),
            value = this.getValue(),
            dataAttribute = this.simpleAttribute.getDataAttribute();

        if (!this.simpleAttribute.isValid()) {
            return;
        }

        // если элемент новый - добавляем в массив
        if (tagStore.indexOfId(dataAttribute.getId()) === -1) {
            if (value === null) {
                value = [];
            }

            value.unshift(dataAttribute.getData());
        }

        this.initStoreData(value);
        this.value = value; // тихо ставим value

        this.fireEvent('change', value);
    }

});
