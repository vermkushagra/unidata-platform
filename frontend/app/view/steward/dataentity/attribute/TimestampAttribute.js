/**
 * Класс, реализующий редактирование аттрибута типа timestamp
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.TimestampAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Timestamp'
    },

    //maxInputWidth: 200,

    initInput: function (customCfg) {
        var input,
            cfg;

        cfg = {
            xtype: 'datetimefield',
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

    getDataForSearch: function () {

        var result = [],
            value = Ext.Date.parse(this.input.getValue(), 'c');

        result.push(
            Ext.Date.format(value, 'c'),
            Ext.Date.format(value, Unidata.Config.getDateFormat())
        );

        return result;

    }

});
