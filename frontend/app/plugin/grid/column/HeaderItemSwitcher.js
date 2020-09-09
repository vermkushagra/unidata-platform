/**
 * Плагин для колонок грида, добавляет к заголовку кликабельную иконку,
 * при клике на которую, скрывается заголовок, и отображаются items - кастомные фильтры для грида
 * @author Aleksandr Bavin
 * @date 2016-07-04
 */
Ext.define('Unidata.plugin.grid.column.HeaderItemSwitcher', {
    extend: 'Ext.plugin.Abstract',
    alias: 'plugin.grid.column.headeritemswitcher',

    formFields: null,
    hideItemsTimer: null,
    mode: null,

    onDestroy: function () {
        this.callParent(arguments);

        this.formFields = null;
        clearTimeout(this.hideItemsTimer);
    },

    init: function (component) {
        component.setLayout('fit');
        this.setCmp(component);
        this.hideItems();
        component.on('afterrender', this.onAfterrender, this);
    },

    onAfterrender: function () {
        var component = this.getCmp(),
            formFields,
            itemSwitcher;

        if (this.mode === 'TEXT') {
            component.textEl.on('click', this.onItemSwitcherClick, this);
        } else {
            itemSwitcher = new Ext.dom.Element(document.createElement('span'));
            itemSwitcher.addCls('icon-magnifier un-headeritemswitcher');

            itemSwitcher.on('click', this.onItemSwitcherClick, this);

            itemSwitcher.insertBefore(component.textEl);
        }

        // component.query('x-form-field');
        // component.items.each(function (item) {
        //
        //
        //     item.on('blur', function (item) {
        //         if (Ext.isEmpty(item.getValue())) {
        //             me.hideItems();
        //         }
        //     });
        // });

        //Ext.form.field.Base
        formFields = this.getAllFormFields(component);
        Ext.Array.each(formFields, function (formField) {
            formField.on('focus', function () {
                clearTimeout(this.hideItemsTimer);
            }, this);

            formField.on('blur', function () {
                var allEmpty = true;

                Ext.Array.each(formFields, function (formField) {
                    if (!Ext.isEmpty(formField.getValue())) {
                        allEmpty = false;

                        return false;
                    }
                });

                if (allEmpty) {
                    this.hideItemsDelayed();
                }
            }, this);
        }, this);

        this.formFields = formFields;

    },

    getAllFormFields: function (container) {
        var formFields = [];

        if (container instanceof Ext.form.field.Base) {
            formFields.push(container);
        }

        if (container.items) {
            container.items.each(function (item) {
                formFields = formFields.concat(formFields, this.getAllFormFields(item));
            }, this);
        }

        return formFields;
    },

    onItemSwitcherClick: function (e) {
        e.preventDefault();
        this.showItems();
    },

    hideItemsDelayed: function () {
        clearTimeout(this.hideItemsTimer);
        this.hideItemsTimer = Ext.defer(this.hideItems, 50, this);
    },

    hideItems: function () {
        var component = this.getCmp();

        component.items.each(function (item) {
            item.setHidden(true);
        });

        if (component.titleEl) {
            component.titleEl.setDisplayed(true);
        }
    },

    showItems: function () {
        var component = this.getCmp();

        if (component.titleEl) {
            component.titleEl.setDisplayed(false);
        }

        component.items.each(function (item) {
            item.setHidden(false);
        });

        if (this.formFields && this.formFields.length) {
            this.formFields[0].focus();
        }
    }

});
