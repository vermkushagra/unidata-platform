/**
 * Класс реализует поля ввода для указания фильтров для типа данных Lookup
 *
 *  events:
 *         change - событие изменения фильтра
 *
 * @author Ivan Marshalkin, Sergey Shishigin
 * 2015-09-23
 */

//TODO: refactoring, create a base class for all tablets Unidata.view.component.search.attribute.tablet.Base
Ext.define('Unidata.view.component.search.attribute.tablet.Lookup', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.lookup',

    attributePath: '',
    attribute: null,

    additionalTerms: null,
    codeValues: null,

    referenceHolder: true,
    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initItems: function () {
        this.callParent(arguments);

        this.codeValues = [];
        this.additionalTerms = [];
        this.initTermEvents();

        this.add({
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'dropdownpickerfield',
                    ui: 'un-field-default',
                    reference: 'eqValue',
                    bind: {
                        codeValue: '{term.value}'
                    },
                    publishes: [
                        'codeValue'
                    ],
                    entityType: 'lookupentity',
                    displayAttributes: this.attribute.get('lookupEntityDisplayAttributes'),
                    useAttributeNameForDisplay: this.attribute.get('useAttributeNameForDisplay'),
                    findTriggerHidden: false,
                    fieldLabel: '',
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue('');
                            }
                        }
                    },
                    listeners: {
                        codevalueschange: this.onCodeValuesChange,
                        scope: this
                    }
                }
            ]
        });
    },

    initTermEvents: function () {
        var me = this,
            viewModel = this.lookupViewModel(),
            binding;

        binding = viewModel.bind('{term}', function (term) {
            if (term) {
                term.bind('searchType', me.updateAdditionalTerms, me);
                term.bind('inverted', me.updateAdditionalTerms, me);

                binding.destroy();
            }
        });
    },

    onCodeValuesChange: function (dropdown, newCodeValues) {
        this.codeValues = newCodeValues;
        this.updateAdditionalTerms();
    },

    /**
     * Создаём дополнительные термы для кодовых значений
     */
    updateAdditionalTerms: function () {
        var codeValues = this.codeValues,
            searchTypes = Unidata.module.search.term.FormFieldStatics.searchType,
            viewModel = this.lookupViewModel(),
            term = viewModel.get('term'),
            searchQuery = viewModel.get('searchQuery'),
            rawTerm,
            additionalCodeValues;

        Ext.Array.each(this.additionalTerms, function (term) {
            searchQuery.removeTerm(term);
            term.destroy();
        }, this);

        this.additionalTerms = [];

        if (term.getSearchType() === searchTypes.EXIST) {
            return;
        }

        if (codeValues.length) {
            additionalCodeValues = codeValues.slice(1);

            if (!additionalCodeValues.length) {
                return;
            }

            rawTerm = term.getTermSaveData();

            Ext.Array.each(additionalCodeValues, function (codeValue) {
                var additionalTerm = Ext.create(rawTerm);

                term.on('destroy', additionalTerm.destroy, additionalTerm);

                additionalTerm.setValue(codeValue);

                searchQuery.addTerm(additionalTerm);

                this.additionalTerms.push(additionalTerm);
            }, this);
        }
    },

    initComponent: function () {
        var me = this;

        me.callParent(arguments);

        me.lookupSelect = me.lookupReference('eqValue');

        me.lookupSelect.entityName = me.attribute.get('lookupEntityType');
        me.lookupSelect.on({
            scope: me,
            change: me.onEqValueChange,
            changecodevalue: me.onEqValueChange
        });
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
            result = [],
            type = this.getType();

        if (isNullable) {
            result.push({
                name: this.attributePath,
                value: null,
                type: type,
                inverted: isInverted
            });
        } else {
            Ext.Array.each(this.lookupSelect.getCodeValues(), function (codeValue) {
                result.push({
                    name: this.attributePath,
                    value: codeValue,
                    type: type,
                    inverted: isInverted
                });
            }, this);
        }

        return result;
    },

    getType: function () {
        return this.attribute.get('lookupEntityCodeAttributeType');
    },

    getFormField: function () {
        return this.lookupSelect;
    },

    isEmptyFilter: function () {
        var result = false;

        if (this.lookupSelect.getValue() === '') {
            result = true;
        }

        return result;
    },

    onEqValueChange: function () {
        this.fireEvent('change', this);
    },

    setDisabled: function (disabled) {
        this.lookupSelect.setDisabled(disabled);
    }
});
