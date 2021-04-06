/**
 * Класс, реализующий просмотр аттрибута, если по каким-то причинам его тип не распознан
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */
Ext.define('Unidata.view.steward.dataentity.attribute.UnknownAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Unknown'
    },

    initInput: function (customCfg) {
        var input,
            cfg;

        cfg = {
            xtype: 'textfield',
            editable: false,
            width: this.inputWidth,
            preventMark: this.getPreventMarkField(),
            maxWidth: this.maxInputWidth
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        return input;
    }

});
