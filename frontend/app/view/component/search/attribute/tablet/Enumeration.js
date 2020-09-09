/**
 * Класс реализует поля ввода для указания фильтров для типа данных Enumeration
 *
 *  events:
 *         change - событие изменения фильтра
 *
 * @author Sergey Shishigin
 * 2015-09-23
 */

//TODO: refactoring, create a base class for all tablets Unidata.view.component.search.attribute.tablet.Base
Ext.define('Unidata.view.component.search.attribute.tablet.Enumeration', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.enumeration',

    attributePath: '',
    attribute: null,

    referenceHolder: true,
    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    enumerationSelect: null,

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            items: []
        }
    ],

    initComponent: function () {
        var me = this,
            enumerationStore,
            enumerationName,
            customCfg,
            enumerationSelect;

        me.callParent(arguments);

        enumerationStore = Unidata.util.api.Enumeration.getStore();
        enumerationName = me.attribute.get('enumDataType');
        customCfg = {
            ui: 'un-field-default',
            fieldLabel: '',
            editable: false,
            reference: 'eqValue',
            width: '100%',
            bind: {
                value: '{term.value}'
            },
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue(null);
                    }
                }
            }
        };

        enumerationSelect = Unidata.util.Enumeration.createEnumerationComboBox(
            enumerationStore,
            enumerationName,
            customCfg
        );

        enumerationSelect.on({
            scope: me,
            change: me.onEqValueChange,
            changecodevalue: me.onEqValueChange
        });

        this.add(enumerationSelect);
        this.enumerationSelect = enumerationSelect;
    },

    /**
     * Сформировать объект поискового фасета (formField) для атрибута
     *
     * @param options {{isNullable: boolean, isInverted: boolean, startWith: boolean, like: boolean}}
     * @returns {{name: string, value: *|null, type: string, inverted: boolean}}
     */
    getFilter: function (options) {
        var isNullable = options.isNullable,
            isInverted = options.isInverted,
            result;

        result = {
            name: this.attributePath,
            value: isNullable ? null : this.enumerationSelect.getValue(),
            type: this.getType(),
            inverted: isInverted
        };

        return result;
    },

    getType: function () {
        return 'String';
    },

    getFormField: function () {
        return this.enumerationSelect;
    },

    isEmptyFilter: function () {
        var result = false;

        if (!this.enumerationSelect.getValue()) {
            result = true;
        }

        return result;
    },

    onEqValueChange: function () {
        this.fireEvent('change', this);
    },

    setDisabled: function (disabled) {
        this.enumerationSelect.setDisabled(disabled);
    }
});
