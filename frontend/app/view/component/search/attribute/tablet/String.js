/**
 * Класс реализует поля воода для указания фильтров для типа данных String
 *
 *  events:
 *         change - событие изменения фильтра
 *
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.tablet.String', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.string',

    attributePath: '',

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'textfield',
                    ui: 'un-field-default',
                    reference: 'eqValue',
                    fieldLabel: '',
                    bind: {
                        value: '{term.value}'
                    },
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue('');
                            }
                        }
                    }
                }
            ]
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
            isStartWith = options.isStartWith,
            isLike = options.isLike,
            isFuzzy = options.isFuzzy,
            isMorphological = options.isMorphological,
            eqValue = this.lookupReference('eqValue');

        param = {
            name: this.attributePath,
            value: isNullable ? null : eqValue.getValue(),
            type: this.getType(),
            inverted: isInverted,
            morphological: isMorphological ? true : false
        };

        if (isStartWith) {
            param.startWith = true;
        } else if (isLike) {
            param.like = true;
        } else if (isFuzzy) {
            param.fuzzy = true;
        }

        return param;
    },

    getType: function () {
        return 'String';
    },

    isEmptyFilter: function () {
        var result = false,
            eqValue = this.lookupReference('eqValue');

        if (eqValue.getValue() === '') {
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
