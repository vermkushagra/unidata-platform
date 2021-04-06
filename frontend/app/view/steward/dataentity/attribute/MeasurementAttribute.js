/**
 * Класс, реализующий редактирование аттрибута типа MeasurementUnit
 *
 * @author Ivan Marshalkin
 * @since 2016-11-14
 */

Ext.define('Unidata.view.steward.dataentity.attribute.MeasurementAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    //maxInputWidth: 'auto',

    config: {
        measurementValueStore: null
    },

    statics: {
        TYPE: 'Measurement'
    },

    //maxInputWidth: 300,

    initInput: function (customCfg) {
        var dataAttribute = this.getDataAttribute(),
            input,
            cfg;

        cfg = {
            xtype: 'measurementnumberfield',

            measurementValueId: dataAttribute.get('valueId'),
            measurementUnitId: dataAttribute.get('unitId'),

            msgTarget: this.elError.getId(),

            numberFieldCfg: {
                ui: 'un-dataentity-field',
                allowDecimals: true,
                decimalPrecision: Unidata.Config.getDecimalPrecision(),
                allowBlank: this.getMetaAttributeField('nullable'),
                preventMark: this.getPreventMarkField()
            },

            measurementFieldCfg: {
            },

            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,

            listeners: {
                measurementchange: this.onMeasurementChange.bind(this)
            }
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        return input;
    },

    onMeasurementChange: function (field, value) {
        var dataAttribute = this.getDataAttribute();

        dataAttribute.set('unitId', value);
    }
});
