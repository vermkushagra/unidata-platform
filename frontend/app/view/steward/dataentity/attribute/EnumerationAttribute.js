/**
 * Класс, реализующий редактирование аттрибута типа enumeration
 *
 * @author Sergey Shishigin
 * @since 2016-10-21
 */

Ext.define('Unidata.view.steward.dataentity.attribute.EnumerationAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Enumeration'
    },

    //maxInputWidth: 'auto',

    config: {
        enumerationStore: null
    },

    initInput: function (customCfg) {
        var metaAttribute = this.getMetaAttribute(),
            enumerationName = metaAttribute.get('enumDataType'),
            EnumerationApi = Unidata.util.api.Enumeration,
            enumerationStore = EnumerationApi.getStore(),
            EnumerationUtil = Unidata.util.Enumeration,
            input,
            cfg;

        cfg = {
            msgTarget: this.elError.getId(),
            readOnly: this.readOnly,
            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,
            preventMark: this.getPreventMarkField(),
            allowBlank: this.getMetaAttributeField('nullable')
        };

        cfg = Ext.apply(cfg, customCfg);

        input = EnumerationUtil.createEnumerationComboBox(enumerationStore, enumerationName, cfg);

        return input;
    }

});
