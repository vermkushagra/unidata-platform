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
    filterEmptyMessage: null,
    // disabled: true,

    config: {
        searchQuery: null,
        toEntityDefaultSearchAttributes: null,
        useToEntityDefaultSearchAttributes: false,
        entityRecord: null
    },

    methodMapper: [
        {
            method: 'updateToEntityDefaultSearchAttributes'
        },
        {
            method: 'updateUseToEntityDefaultSearchAttributes'
        },
        {
            method: 'updateSearchQuery'
        },
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

        this.filterEmptyMessage = Ext.create('Ext.Component', {
            cls: 'un-search-attribute-filter-empty',
            html: Unidata.i18n.t('search>query.searchAttributesNotExists'),
            hidden: true
        });

        this.add(this.filterEmptyMessage);

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
                    if (field.suspendBlur) {
                        event.stopEvent();

                        return;
                    }

                    // не прячем, если клик по пикеру
                    if (!event.within(field.getPicker().getEl(), true)) {
                        field.hide();
                    }
                },
                beforeshow: function (field) {
                    // магический флаг для борьбы с багами ExtJS
                    field.suspendBlur = true;
                },
                show: function (field) {
                    Ext.defer(function () {
                        field.expand();

                        // сбрасываем флаг по таймауту после показа компонента
                        Ext.defer(function () {
                            field.suspendBlur = false;
                        }, 300);
                    }, 10);
                }
            },
            listConfig: {
                cls: 'un-attribute-boundlist',
                minWidth: 300
            },
            onBlur: function () {
                // если флаг не сброшен это значит что мы наткнулись на очередной баг ExtJS
                if (this.suspendBlur) {
                    Ext.defer(function () {
                        this.focus(false, 50, function () {
                            this.expand();
                        }, this);
                    }.bind(this), 0);

                    return;
                }

                this.mixins.focusable.onBlur.apply(this, arguments);
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
                this.filterEmptyMessage.setHidden(false);
            } else {
                this.filterEmptyMessage.setHidden(true);
            }

            this.updateAddFilterTabletButtonVisibility();
        }
    },

    updateEntityRecord: function () {
        this.resetFields();
    },

    getSearchableAttributesCount: function () {
        var viewModel = this.getViewModel(),
            searchableAttributesStore = viewModel.getStore('searchableAttributes'),
            count = searchableAttributesStore.getCount();

        this.publishState('searchableAttributesCount', count);

        return count;
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
