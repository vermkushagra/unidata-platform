/**
 * Класс реализует панельку в которой будут генерироваться "плитки" (tablet) для ввода фильтров пользователем
 *
 * events:
 *        change - событие изменения фильтра
 *
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.FilterPanel', {
    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.search.SearchFilter'
    ],

    alias: 'widget.component.search.attribute.filterpanel',

    requires: [
        'Unidata.view.component.search.attribute.FilterPanelController',
        'Unidata.view.component.search.attribute.FilterPanelModel',

        'Unidata.view.component.search.attribute.tablet.Tablet'
    ],

    controller: 'component.search.attribute.filterpanel',
    viewModel: {
        type: 'component.search.attribute.filterpanel'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-search-filter',

    cls: 'un-search-attribute-filter-panel',

    title: Unidata.i18n.t('glossary:attributes'),
    baseTitle: Unidata.i18n.t('glossary:attributes'),

    referenceHolder: true,
    collapsible: true,
    titleCollapse: true,
    collapsed: true,
    // disabled: true,

    config: {
        entityRecord: null
    },

    methodMapper: [
        {
            method: 'getSearchableAttributes'
        },
        {
            method: 'getTabletGroup'
        },
        {
            method: 'findTabletGroupByAttribute'
        },
        {
            method: 'createTabletGroup'
        },
        {
            method: 'insertTabletGroupItem'
        }
    ],

    items: [],

    tools: [
        {
            xtype: 'un.fontbutton.additem',
            reference: 'addFilterTablet',
            handler: 'onAddFilterTabletClick',
            color: 'lightgray',
            disabled: false,
            bind: {
                disabled: '{addFilterTabletButtonDisabled}'
            },
            tooltip: Unidata.i18n.t('search>query.addSearchAttribute')
        }
    ],

    searchableAttributesField: null,

    initItems: function () {
        this.callParent(arguments);

        this.searchableAttributesField = this.add({
            xtype: 'tagfield',
            ui: 'un-field-default',
            cls: 'un-search-attribute-filter-tagfield',
            bind: {
                store: '{searchableAttributes}'
            },
            hidden: true,
            floating: true,
            filterPickList: true,
            forceSelection: true,
            multiSelect: true,
            grow: false,
            matchFieldWidth: false,
            queryMode: 'local',
            displayField: 'displayName',
            valueField: 'tempId',
            anyMatch: true,
            triggers: {
                picker: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.hide();
                    }
                }
            },
            listeners: {
                select: 'onSearchableAttributesFieldSelect',
                blur: function (field, event) {
                    // не прячем, если клик по пикеру
                    if (!event.within(field.getPicker().getEl(), true)) {
                        field.hide();
                    }
                },
                show: function (field) {
                    setTimeout(function () {
                        field.expand();
                    }, 0);
                }
            },
            listConfig: {
                cls: 'un-attribute-boundlist',
                minWidth: 300
            }
        });
    },

    onDestroy: function () {
        this.searchableAttributesField.destroy();
        this.searchableAttributesField = null;
        this.callParent(arguments);
    },

    getSortFields: function () {
        var sortFields = [];

        this.items.each(function (item) {
            var sortData;

            if (!item.getSortData) {
                return;
            }

            sortData = item.getSortData();

            if (sortData) {
                sortFields.push(sortData);
            }
        });

        return sortFields;
    },

    removeSearchParamsPanel: function () {
        var controller = this.getController();

        return controller.removeSearchParamsPanel();
    },

    /**
     * Обновляем заголовок - выводим count выбранных атрибутов
     * @param searchFilterItemsCount
     */
    updateSearchFilterItemsCount: function (searchFilterItemsCount) {
        if (searchFilterItemsCount) {
            this.setTitle(this.baseTitle + ' (' + searchFilterItemsCount + ')');
        } else {
            this.setTitle(this.baseTitle);
        }

        this.updateAddFilterTabletButtonVisibility();
    },

    /**
     * Обновляет состояние кнопки addFilterTablet
     */
    updateAddFilterTabletButtonVisibility: function () {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('searchableAttributes'),
            addFilterTablet = this.lookupReference('addFilterTablet'),
            disabled;

        disabled = (!this.isConfiguring && store && store.getCount() === 0);

        viewModel.set('addFilterTabletButtonDisabled', disabled);
    },

    resetFields: function () {
        var controller = this.getController(),
            entityRecord = this.getEntityRecord(),
            searchFilterItemsCollection;

        if (entityRecord && controller) {
            searchFilterItemsCollection = this.getSearchFilterItemsCollection();

            // очищаем коллекцию
            Ext.Array.each(searchFilterItemsCollection.getRange(), function (item) {
                item.destroy();
            });
            searchFilterItemsCollection.removeAll();

            // инициализируем стор с атрибутами
            controller.initSearchableAttributesStore(entityRecord);

            if (this.getSearchableAttributesCount() === 0) {
                this.add({
                    cls: 'un-search-attribute-filter-empty',
                    html: Unidata.i18n.t('search>query.searchAttributesNotExists')
                });
            }

            this.updateAddFilterTabletButtonVisibility();
        }
    },

    updateEntityRecord: function (entityRecord) {
        this.resetFields();
    },

    getSearchableAttributesCount: function () {
        var viewModel = this.getViewModel(),
            searchableAttributesStore = viewModel.getStore('searchableAttributes');

        return searchableAttributesStore.getCount();
    },

    setDisabled: function (disabled) {
        this.callParent(arguments);

        Ext.Array.each(this.items.items, function (item) {
            item.setDisabled(disabled);
        });
    },

    /**
     * Исключает поля из сортировки
     */
    excludeField: function (attributePath) {
        Ext.Array.each(this.items.items, function (item) {
            if (item.path === attributePath) {
                item.setSortButtonIncluded(false);
            }
        });
    }
});
