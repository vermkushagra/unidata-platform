/**
 * Класс реализует поля воода для указания фильтров для типа данных Integer и Number
 *
 * events:
 *        change - событие изменения фильтра
 *
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.tablet.Numeric', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.numeric',

    attributePath: '',

    allowDecimals: false,
    decimalPrecision: 2,

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-search-attribute-tablet-numeric',

    items:  [
        {
            xtype: 'container',
            layout: {
                type: 'hbox'
            },
            reference: 'eqValueContainer',
            cls: 'un-search-attribute-tablet-numeric-eq-value',
            items: [
                {
                    xtype: 'numberfield',
                    ui: 'un-field-default',
                    reference: 'eqValue',
                    fieldLabel: '',
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue('');
                            }
                        }
                    },
                    flex: 1
                }
            ],
            flex: 1
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox'
            },
            items: [
                {
                    xtype: 'numberfield',
                    ui: 'un-field-default',
                    reference: 'leftRange',
                    flex: 1,
                    emptyText: Unidata.i18n.t('common:from'),
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue('');
                            }
                        }
                    }
                }, {
                    xtype: 'numberfield',
                    ui: 'un-field-default',
                    reference: 'rightRange',
                    flex: 1,
                    emptyText: Unidata.i18n.t('common:to'),
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue('');
                            }
                        }
                    }
                }
            ],
            flex: 1
        }
    ],

    initComponent: function () {
        var me = this,
            field,
            measurementUnitComboBox,
            eqValueContainer;

        this.callParent(arguments);

        Ext.Array.each(['eqValue', 'leftRange', 'rightRange'], function (item) {
            field               = me.lookupReference(item);
            field.allowDecimals = me.allowDecimals;
            field.decimalPrecision = me.decimalPrecision;
        });

        this.initListeners();

        if (this.attribute.isSimpleMeasurementDataType()) {
            measurementUnitComboBox = this.buildMeasurementUnitComboBox();

            if (measurementUnitComboBox) {
                eqValueContainer = this.lookupReference('eqValueContainer');
                eqValueContainer.add(measurementUnitComboBox);
            }
        }

        this.initValue();
    },

    initValue: function () {
        var data = this.value;

        if (Ext.isObject(data)) {
            if (data.hasOwnProperty('value')) {
                this.lookupReference('eqValue').setValue(data.value);
            } else if (data.hasOwnProperty('range') && Ext.isArray(data.range)) {
                this.lookupReference('leftRange').setValue(data.range[0]);
                this.lookupReference('rightRange').setValue(data.range[1]);
            }
        }
    },

    initListeners: function () {
        this.lookupReference('eqValue').on('change', this.onEqValueChange, this);
        this.lookupReference('leftRange').on('change', this.onRangeChange, this);
        this.lookupReference('rightRange').on('change', this.onRangeChange, this);
    },

    buildMeasurementUnitComboBox: function (customCfg) {
        var me                    = this,
            MeasurementValuesApi  = Unidata.util.api.MeasurementValues,
            measurementValueStore = MeasurementValuesApi.getStore(),
            measurementValueId,
            cfg,
            measurementUnitComboBox,
            MeasurementValueUtil  = Unidata.util.MeasurementUnit,
            measurementValue,
            baseMeasurementUnitId;

        measurementValueId = me.attribute.get('valueId');
        measurementValue = MeasurementValuesApi.getMeasurementValueById(measurementValueId);

        if (!measurementValue) {
            return null;
        }

        baseMeasurementUnitId = measurementValue.getBaseMeasurementUnitId();

        cfg = {
            ui: 'un-field-default',
            fieldLabel: '',
            editable: false,
            showTooltip: true,
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue(null);
                    }
                }
            },
            readOnly: true,
            width: 50,
            value: baseMeasurementUnitId
        };

        cfg = Ext.apply(cfg, customCfg);

        measurementUnitComboBox = MeasurementValueUtil.createMeasurementUnitComboBox(
            measurementValueStore,
            measurementValueId,
            cfg
        );

        return measurementUnitComboBox;
    },

    /**
     * Сформировать объект поискового фасета (formField) для атрибута
     *
     * @param options {{isNullable: boolean, isInverted: boolean, startWith: boolean, like: boolean}}
     * @returns {{name: string, value: *|null, type: string, inverted: boolean}}
     */
    getFilter: function (options) {
        var param,
            paramPath,
            isNullable = options.isNullable,
            isInverted = options.isInverted,
            eqValue    = this.lookupReference('eqValue'),
            leftRange  = this.lookupReference('leftRange'),
            rightRange = this.lookupReference('rightRange');

        paramPath = {
            name: this.attributePath,
            type: this.getType(),
            inverted: isInverted
        };

        if (!eqValue.isDisabled() || isNullable) {
            param = {
                value: isNullable ? null : eqValue.getValue()
            };
        } else {
            param = {
                range: [leftRange.getValue(), rightRange.getValue()]
            };
        }

        param = Ext.apply(param, paramPath);

        return param;
    },

    getType: function () {
        return this.allowDecimals ? 'Number' : 'Integer';
    },

    isEmptyFilter: function () {
        var result     = false,
            eqValue    = this.lookupReference('eqValue'),
            leftRange  = this.lookupReference('leftRange'),
            rightRange = this.lookupReference('rightRange');

        if (eqValue.getValue() === null && leftRange.getValue() === null && rightRange.getValue() === null) {
            result = true;
        }

        return result;
    },

    onEqValueChange: function (field, newValue) {
        var disable = true;

        if (newValue === null) {
            disable = false;
        }

        this.lookupReference('leftRange').setDisabled(disable);
        this.lookupReference('rightRange').setDisabled(disable);

        this.fireEvent('change', this);
    },

    onRangeChange: function () {
        var disable = true,
            leftRangeValue,
            rightRangeValue;

        leftRangeValue  = this.lookupReference('leftRange').getValue();
        rightRangeValue = this.lookupReference('rightRange').getValue();

        if (leftRangeValue === null && rightRangeValue === null) {
            disable = false;
        }

        this.lookupReference('eqValue').setDisabled(disable);

        this.fireEvent('change', this);
    },

    setDisabled: function (disabled) {
        this.lookupReference('leftRange').setDisabled(disabled);
        this.lookupReference('rightRange').setDisabled(disabled);
        this.lookupReference('eqValue').setDisabled(disabled);
    }
});
