/**
 * @author Aleksandr Bavin
 * @date 2017-01-13
 */
Ext.define('Unidata.view.steward.dataentity.attribute.ArrayAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    requires: [
        'Unidata.view.steward.dataentity.attribute.array.ArrayAttributeInput',
        'Unidata.view.steward.dataentity.attribute.array.ArrayAttributeRead',
        'Unidata.view.steward.dataentity.attribute.array.ArrayAttributeCompare',
        'Unidata.view.steward.dataentity.attribute.SimpleAttribute'
    ],

    cls: 'un-array-attribute',

    statics: {
        TYPE: 'Array'
    },

    config: {
        viewMode: Unidata.AttributeViewMode.READ
    },

    initialViewMode: null, // оригинальный viewMode
    inputByViewMode: null, // кэш с инпутами по viewMode

    constructor: function () {
        this.inputByViewMode = {};

        this.callParent(arguments);
    },

    destroy: function () {

        if (this.inputByViewMode) {
            Ext.Object.each(this.inputByViewMode, function (key, viewItem) {
                Ext.destroy(viewItem);
            });
        }

        this.inputByViewMode = null;

        this.callParent(arguments);
    },

    updateViewMode: function (viewMode) {
        if (this.isConfiguring) {
            this.initialViewMode = viewMode;
        }

        this.callParent(arguments);
    },

    showInput: function () {
        if (this.elSwitcher) {
            this.elSwitcher.hide();
        }

        if (this.input instanceof Unidata.view.steward.dataentity.attribute.array.ArrayAttributeRead) {
            if (!this.getReadOnly()) {
                this.input.showSimpleAttribute(null);
            }
        }

        this.updateLayoutDelayed();
    },

    hideInput: function () {
        if (this.elSwitcher && !this.getReadOnly()) {
            this.elSwitcher.show();
        }

        if (this.input instanceof Unidata.view.steward.dataentity.attribute.array.ArrayAttributeRead) {
            this.input.hideSimpleAttribute();
        }

        this.updateLayoutDelayed();
    },

    updateInputVisibility: function () {
        if (!this.elSwitcher) {
            return;
        }

        if (this.getReadOnly()) {
            this.elSwitcher.hide();
        } else if (!(this.input instanceof Unidata.view.steward.dataentity.attribute.array.ArrayAttributeCompare)) {
            this.elSwitcher.show();
        }
    },

    onInputBlur: function () {
        this.hideInput();
        this.callParent(arguments);
    },

    onSwitcherFocus: function () {
        if (this.input instanceof Unidata.view.steward.dataentity.attribute.array.ArrayAttributeInput) {
            this.input.addArrayItem();
            this.elSwitcher.hide();
        } else {
            this.showInput();
        }
    },

    /**
     * Инициализация показа подсказки по наведению на поле ввода
     */
    initToolTip: function () {
    },

    onViewModeChange: function (viewMode) {
        var input = this.getInput();

        if (!this.rendered) {
            return;
        }

        if (input instanceof Unidata.view.steward.dataentity.attribute.array.ArrayAttributeRead) {
            this.hideInput();
        }

        this.setInput(
            this.getViewModeInput(viewMode)
        );
    },

    getViewModeInput: function (viewMode) {
        var input = this.inputByViewMode[viewMode];

        if (input === undefined) {
            input = this.createInput(viewMode);
        }

        return input;
    },

    /**
     * Создаёт инпут на основе viewMode
     *
     * @param viewMode
     */
    createInput: function (viewMode) {
        var viewModes = Unidata.AttributeViewMode;

        switch (viewMode) {
            case viewModes.EDIT:
                this.inputByViewMode[viewMode] = this.initEditInput();
                break;

            case viewModes.READ:
                this.inputByViewMode[viewMode] = this.initReadInput();
                break;

            case viewModes.COMPARE:
                this.inputByViewMode[viewMode] = this.initCompareInput();
                break;

            default:
                throw new Error('Unknown viewMode: ' + viewMode);
        }

        return this.inputByViewMode[viewMode];
    },

    setupInputEventsListening: function () {
        var input = this.getInput(),
            listenerRemovers = this.callParent(arguments);

        listenerRemovers.push(
            input.on({
                datarecordopen: this.onDataRecordOpen,
                destroyable: true,
                scope: this
            })
        );

        return listenerRemovers;
    },

    initInput: function () {
        this.enableBubble([
            'datarecordopen',
            'wantaddetalonid'
        ]);

        return this.getViewModeInput(this.getViewMode());
    },

    initEditInput: function () {
        var metaAttribute = this.getMetaAttribute(),
            input;

        input = Ext.widget({
            xtype: 'arrayattributeinput',
            metaRecord: this.getMetaRecord(),
            dataRecord: this.getDataRecord(),
            metaAttribute: metaAttribute,
            dataAttribute: this.getDataAttribute(),
            // attributePath: null, // предположительно нам он не потребуется
            readOnly: this.getReadOnly(),
            preventMarkField: this.getPreventMarkField()
        });

        return input;
    },

    initReadInput: function () {
        var input,
            baseTooltip,
            tipTpl,
            tplHtml;

        baseTooltip = this.buildBaseToolTip();
        tplHtml = Ext.String.format('<div class="{0}">{1}</div>',
            this.baseCls + '-tip-value',
            '{tip}'
        );
        tipTpl = Ext.String.format(baseTooltip, tplHtml);
        input = Ext.widget({
            xtype: 'arrayattribute.read',
            metaRecord: this.getMetaRecord(),
            dataRecord: this.getDataRecord(),
            metaAttribute: this.getMetaAttribute(),
            dataAttribute: this.getDataAttribute(),
            readOnly: this.getReadOnly(),
            preventMarkField: this.getPreventMarkField(),
            tipTpl: tipTpl
        });

        input.on('tagselect', this.onTagSelect, this);

        return input;
    },

    onTagSelect: function () {
        if (this.elSwitcher) {
            this.elSwitcher.hide();
        }
    },

    initCompareInput: function () {
        var input;

        input = Ext.widget({
            xtype: 'arrayattribute.compare',
            metaRecord: this.getMetaRecord(),
            dataRecord: this.getDataRecord(),
            metaAttribute: this.getMetaAttribute(),
            dataAttribute: this.getDataAttribute(),
            readOnly: this.getReadOnly(),
            preventMarkField: this.getPreventMarkField()
        });

        return input;
    },

    onRender: function () {
        this.callParent(arguments);

        if (this.input instanceof Unidata.view.steward.dataentity.attribute.array.ArrayAttributeRead) {
            if (this.elSwitcher && this.getReadOnly()) {
                this.elSwitcher.hide();
            }
        }

        Ext.defer(this.updateLayout, 1, this);
    },

    onTitleClick: function () {
        var viewModes = Unidata.AttributeViewMode,
            viewMode = this.getViewMode();

        if (this.initialViewMode === viewMode) {
            this.setViewMode(viewModes.EDIT);
        } else {
            this.setViewMode(this.initialViewMode);
        }

        this.callParent(arguments);
    },

    getValue: function () {
        return this.value;
    },

    getSubmitValue: function () {
        var inputValue = this.getInputValue().slice();

        if (inputValue.length === 0) {
            return null;
        }

        Ext.Array.each(inputValue, function (value, index, arr) {
            arr[index] = {
                value: value['value']
            };
        });

        return inputValue;
    },

    hasChanges: function () {
        var hasChanges = false,
            inputValue = this.getInputValue();

        if (this.value == inputValue) {
            return false;
        }

        if (this.value == null || inputValue == null) {
            return true;
        }

        if (this.value.length !== inputValue.length) {
            return true;
        }

        Ext.Array.each(this.value, function (item, index) {
            if (item.value !== inputValue[index].value) {
                hasChanges = true;

                return false;
            }
        });

        return hasChanges;
    },

    onChange: function () {
        this.callParent(arguments);

        this.validate();
    },

    isValid: function () {
        return this.validate();
    },

    validate: function () {
        var metaAttribute = this.getMetaAttribute(),
            nullable = metaAttribute.get('nullable'),
            value = this.getValue(),
            valid = true;

        if (!nullable && (!value || value.length === 0)) {
            valid = false;

            this.showErrorMsg(Unidata.i18n.t('validation:somethingCantBeEmpty', {name: Unidata.i18n.t('glossary:array')}));
        }

        if (!nullable && valid) {
            Ext.Array.each(value, function (valueItem) {
                if (!nullable && valueItem.value === null) {
                    valid = false;

                    this.showErrorMsg(Unidata.i18n.t('dataentity>arrayItemCantBeEmpty'));

                    return false;
                }
            }, this);
        }

        this.onValidate(valid);

        return valid;
    },

    onValidate: function (valid) {
        if (valid) {
            this.hideErrorMsg();
        }
    },

    onDataRecordOpen: function (cfg) {
        var listener,
            ded;

        // необходимо остановить лейаутинг инпута, перед открытием другой записи,
        // иначе возникают визуальные баги при лейаутинге скрытого компонента
        this.input.suspendLayouts();

        ded = this.findParentBy(function (cmp) {
            return (cmp instanceof Unidata.view.steward.dataentity.DataEntity);
        });

        // если атрибут не в DataEntity, значит что-то не то
        if (!ded) {
            return;
        }

        // далее происходит магия, которая запускает лейаутинг, когда инпут становится виден
        listener = ded.on('afterlayout', function (cmp) {
            if (!this.isVisible(true)) {
                listener.destroy();
                // атрибут скрылся, ожидаем, когда он вновь станет видим
                listener = cmp.on('afterlayout', function () {
                    if (this.isVisible(true)) {
                        listener.destroy();
                        // атрибут вновь виден, разрешаем лейаутинг
                        this.input.resumeLayouts(true);
                    }
                }, this, {destroyable: true});
            }
        }, this, {destroyable: true});

        this.fireEvent('datarecordopen', cfg);
    },

    onWantAddEtalonId: function (metaRecord, saveCallBack) {
        this.fireEvent('wantaddetalonid', metaRecord, saveCallBack);
    }

});
