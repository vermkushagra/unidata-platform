/**
 * Класс, реализующий редактирование аттрибута типа boolean
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.BooleanAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Boolean'
    },

    //maxInputWidth: 200,

    blurOnSpecialKey: function (field, e) {
        if (e.isStopped) {
            return;
        }

        if (e.getKey() === e.ESC) {
            this.onEscClick(field, e);
        }

        // предотвращаем скролирование вверх вниз экрана
        if (Ext.Array.contains([e.DOWN, e.UP, e.SPACE], e.getKey())) {
            e.stopEvent();
        }

        if (e.getKey() === e.ENTER) {
            Ext.defer(function () {
                field.blur();
            }, 10);
        }
    },

    initInput: function (customCfg) {
        var storeData = [
                {
                    name: Unidata.i18n.t('common:yes'),
                    value: true
                },
                {
                    name: Unidata.i18n.t('common:no'),
                    value: false
                }
            ],
            input,
            cfg;

        // элемент для выбора доступен только если поле не обязательно к заполнению
        if (this.getMetaAttributeField('nullable')) {
            storeData.push({
                name: Unidata.i18n.t('dataentity>notSet'),
                value: null
            });
        }

        cfg = {
            xtype: 'combobox',
            valueField: 'value',
            displayField: 'name',
            editable: false,
            allowBlank: this.getMetaAttributeField('nullable'),
            msgTarget: this.elError.getId(),
            width: this.inputWidth,
            //maxWidth: this.maxInputWidth,
            preventMark: this.getPreventMarkField(),
            store: {
                fields: [
                    'name',
                    'value'
                ],
                data: storeData
            }
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        return input;
    },

    setupInputEventsListening: function () {
        var listenerRemovers = this.callParent(arguments);

        listenerRemovers.push(this.input.on('select', this.onChange, this, {destroyable: true}));

        return listenerRemovers;
    },

    getDataForSearch: function () {

        var result = [],
            value = this.input.getValue();

        switch (value) {
            case true:
                result.push(
                    'true',
                    'yes',
                    Unidata.i18n.t('common:yes').toLowerCase(),
                    '1'
                );
                break;
            case false:
                result.push(
                    'false',
                    'no',
                    Unidata.i18n.t('common:no').toLowerCase(),
                    '0'
                );
                break;
            case null:
                result.push(
                    'null',
                    'empty',
                    Unidata.i18n.t('dataentity>notSet').toLowerCase(),
                    Unidata.i18n.t('dataentity>notSpecified')
                );
                break;
        }

        return result;

    }

});
