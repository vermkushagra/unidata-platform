/**
 * Класс, реализующий редактирование аттрибута типа string
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.StringAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    requires: [
        'Unidata.mixin.form.InputTextMask'
    ],

    statics: {
        TYPE: 'String'
    },

    maskPlugin: null,

    onDestroy: function () {
        this.callParent(arguments);

        Ext.destroyMembers(this, 'maskPlugin');
    },

    initInput: function (customCfg) {
        var maskAttribute = this.getMetaAttributeField('mask'),
            maskPlugin,
            input,
            cfg;

        cfg = {
            xtype: 'textfield',
            allowBlank: this.getMetaAttributeField('nullable'),
            msgTarget: this.elError.getId(),
            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,
            preventMark: this.getPreventMarkField()
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        if (maskAttribute) {
            // TODO: Ivan Marshalkin данный плагин необходимо оформить в нормльном виде. Чтоб не приходилось вызывать
            // руками maskPlugin.init(this.input);
            this.maskPlugin = maskPlugin = Ext.create('Unidata.mixin.form.InputTextMask', {
                mask: maskAttribute
            });

            maskPlugin.init(input);
        }

        return input;
    },

    getInputValue: function () {
        var value = this.callParent(arguments);

        if (Ext.isEmpty(value)) {
            return null;
        }

        // Если значение целиком из плейсхолдеров, возвращаем null
        if (this.maskPlugin && this.maskPlugin.isEmpty()) {
            return null;
        }

        return value;
    }

});
