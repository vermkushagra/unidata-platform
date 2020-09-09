/**
 * Класс, реализующий редактирование аттрибута типа date
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.DateAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Date'
    },

    //maxInputWidth: 200,

    initInput: function (customCfg) {
        var input,
            cfg;

        cfg = {
            xtype: 'datefield',
            allowBlank: this.getMetaAttributeField('nullable'),
            msgTarget: this.elError.getId(),
            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,
            preventMark: this.getPreventMarkField()
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        return input;
    },

    setInputValue: function (value) {
        if (this.input) {
            this.input.setValue(Ext.Date.parse(value, 'c'));
        }
    },

    getInputValue: function () {
        var inputValue;

        if (this.input) {
            inputValue = this.input.getValue();

            if (inputValue) {
                return Ext.Date.format(inputValue, Unidata.Config.getDateTimeFormatProxy());
            }
        }

        return null;
    },

    getDataForSearch: function () {

        var result = [],
            value = this.input.getValue();

        result.push(
            Ext.Date.format(value, 'c'),
            Ext.Date.format(value, Unidata.Config.getDateFormat())
        );

        return result;
    }

});
