/**
 * Класс, реализующий редактирование аттрибута типа time
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.TimeAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Time'
    },

    //maxInputWidth: 200,

    initInput: function (customCfg) {
        var input,
            cfg;

        cfg = {
            xtype: 'timetextfield',
            allowBlank: this.getMetaAttributeField('nullable'),
            msgTarget: this.elError.getId(),
            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,
            preventMark: this.getPreventMarkField()
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        return input;
    }
});
