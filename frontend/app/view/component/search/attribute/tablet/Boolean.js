/**
 * Класс реализует поля воода для указания фильтров для типа данных Boolean
 *
 * events:
 *        change - событие изменения фильтра
 *
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.tablet.Boolean', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.boolean',

    attributePath: '',

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'combobox',
            ui: 'un-field-default',
            reference: 'eqValue',
            fieldLabel: '',
            editable: false,
            flex: 1,
            store: [
                ['all', Unidata.i18n.t('search>query.noMatter')],
                ['true', Unidata.i18n.t('common:yes')],
                ['false', Unidata.i18n.t('common:no')]/*,
                ['null', Unidata.i18n.t('glossary:notSet')]*/],
            value: 'all',
            bind: {
                value: '{term.value}'
            },
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue('all');
                    }
                }
            }
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.lookupReference('eqValue').on('change', this.onEqValueChange, this);

        this.initValue();
    },

    initValue: function () {
        var data = this.value;

        if (Ext.isObject(data)) {
            if (data.hasOwnProperty('value')) {
                this.lookupReference('eqValue').setValue(data.value);
            }
        }
    },

    /**
     * Сформировать объект поискового фасета (formField) для атрибута
     *
     * @param options {{isNullable: boolean, isInverted: boolean, startWith: boolean, like: boolean}}
     * @returns {{name: string, value: *|null, type: string, inverted: boolean}}
     */
    getFilter: function (options) {
        var param,
            isNullable = options.isNullable,
            isInverted = options.isInverted,
            eqValue = this.lookupReference('eqValue');

        param = {
            name: this.attributePath,
            value: isNullable ? null : eqValue.getValue(),
            type: this.getType(),
            inverted: isInverted
        };

        return param;
    },

    getType: function () {
        return 'Boolean';
    },

    isEmptyFilter: function () {
        var result = false;

        if (this.lookupReference('eqValue').getValue() === 'all') {
            result = true;
        }

        return result;
    },

    onEqValueChange: function () {
        this.fireEvent('change', this);
    },

    setDisabled: function (disabled) {
        this.lookupReference('eqValue').setDisabled(disabled);
    }
});
