/**
 * Класс, реализующий редактирование аттрибута типа lookup
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.LookupAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Lookup'
    },

    //maxInputWidth: 'auto',

    initInput: function (customCfg) {
        var metaAttribute             = this.metaAttribute,
            entityName       = metaAttribute.get('typeValue'),
            userHasReadRight = Unidata.Config.userHasRight(entityName, 'read'),
            metaRecord = this.getMetaRecord(),
            input,
            cfg;

        cfg = {
            xtype: 'dropdownpickerfield',
            allowBlank: this.getMetaAttributeField('nullable'),
            msgTarget: this.elError.getId(),
            displayAttributes: metaAttribute.get('lookupEntityDisplayAttributes'),
            useAttributeNameForDisplay: metaAttribute.get('useAttributeNameForDisplay'),
            entityName: entityName,
            entityType: 'lookupentity',
            codeValue: this.value,
            value: this.value,
            matchFieldWidth: false,
            displayValue: this.getDataAttributeDisplayValue(),
            etalonId: this.getDataAttributeTargetEtalonId(),
            openLookupRecordHidden: !userHasReadRight,
            addLookupRecordHidden: this.readOnly,
            readOnly: this.readOnly,
            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,
            preventMark: this.getPreventMarkField(),
            listeners: {
                datarecordopen: this.onDataRecordOpen,
                wantaddetalonid: this.onWantAddEtalonId,
                scope: this
            },
            showValueTooltip: false
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        this.enableBubble([
            'datarecordopen',
            'wantaddetalonid'
        ]);

        return input;
    },

    /**
     * Возвращает displayValue из dataAttribute
     * @returns {*}
     */
    getDataAttributeDisplayValue: function () {
        var dataAttribute = this.getDataAttribute();

        return dataAttribute.get('displayValue');
    },

    /**
     * Возвращает etalonId из dataAttribute
     * @returns {*}
     */
    getDataAttributeTargetEtalonId: function () {
        var dataAttribute = this.getDataAttribute();

        return dataAttribute.get('targetEtalonId');
    },

    setupInputEventsListening: function () {
        var listenerRemovers = this.callParent(arguments),
            remover;

        remover = this.input.on({
            destroyable: true,
            scope: this,
            select: this.onChange,
            changecodevalue: this.onChange
        });

        listenerRemovers.push(remover);

        return listenerRemovers;
    },

    setInputValue: function (value) {
        if (this.input) {
            this.input.etalonId = null;
            this.input.displayValue = null;
            this.input.setCodeValue(value);
        }
    },

    setDisplayValue: function (displayValue) {
        if (this.input) {
            this.input.setValueSilent(displayValue);
        }
    },

    getInputValue: function () {

        if (this.input) {
            return this.input.getCodeValue();
        }

        return false;

    },

    getDataForSearch: function () {
        return [
            this.input.getValue()
        ];
    },

    hasChanges: function () {
        var codeValues;

        if (this.input) {
            codeValues = this.input.getCodeValues();

            // если текущее значение value входит в массив codeValues, то ничего не меняем
            if (codeValues.indexOf(this.value) !== -1) {
                return false;
            }
        }

        return this.callParent(arguments);
    },

    onDataRecordOpen: function (cfg) {
        this.fireEvent('datarecordopen', cfg);
    },

    onWantAddEtalonId: function (metaRecord, saveCallBack) {
        this.fireEvent('wantaddetalonid', metaRecord, saveCallBack);
    }

});
