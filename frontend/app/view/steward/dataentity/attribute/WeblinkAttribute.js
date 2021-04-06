/**
 * Класс, реализующий редактирование аттрибута типа weblink
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */
Ext.define('Unidata.view.steward.dataentity.attribute.WeblinkAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    config: {
        alwaysShowInput: true
    },

    statics: {
        TYPE: 'Weblink'
    },

    initInput: function (customCfg) {
        var typeValue = this.getMetaAttributeField('typeValue'),
            input,
            emptyText,
            cfg;

        // исправить при выполнении UN-5816
        // emptyText = Ext.String.htmlEncodeMulti(
        //     Unidata.util.MetaAttributeFormatter.getSimpleDataDisplayName(typeValue),
        //     1
        // );

        emptyText = Unidata.i18n.t('dataentity>valueCalcAfterRecordSaved');

        cfg = {
            xtype: 'textfield',
            emptyText: emptyText,
            readOnly: true,
            focusable: false,
            tabIndex: -1,
            allowBlank: this.getMetaAttributeField('nullable'),
            msgTarget: this.elError.getId(),
            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,
            preventMark: this.getPreventMarkField()
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        input.on('render', function () {
            input.getEl().on('click', this.openLink, this);
        }, this);

        return input;
    },

    openLink: function () {
        var value = this.getValue();

        if (!value) {
            return;
        }

        // если протокол не указан то использовать протокол хоста на котором работает платформаы
        // расшифровка регулярки: строка начинается с символов (от 1 до 4) и следующий символ ":"
        value = value.match(/^.{1,4}:/) ? value : '//' + value;

        window.open(value, '_blank').focus();
    },

    /**
     * Переопределяем метод базового класса т.к. атрибут данного типа должен быть всегда read only
     * см UN-2182
     *
     * @returns {Unidata.view.steward.dataentity.attribute.WeblinkAttribute}
     */
    setReadOnly: function () {
        this.readOnly = true;

        return this;
    }
});
