/**
 * Миксин для поисковых панелей фильтрации,
 * который реализует базовое управления вложенными поисковыми фильтрами
 *
 * @author Aleksandr Bavin
 * @date 2017-06-07
 */
Ext.define('Unidata.mixin.search.SearchFilter', {

    extend: 'Ext.Mixin',

    mixinConfig: {
        after: {
            initComponent: 'afterInitComponent',
            destroy: 'onAfterDestroy'
        },
        before: {
        }
    },

    /**
     * коллекция элементов для фильтрации
     * @type {Ext.util.Collection}
     * @private
     */
    searchFilterItemsCollection: null,

    config: {
        searchFilterItemsCount: 0
    },

    afterInitComponent: function () {
        this.on('add', this.onComponentItemAdd, this);
        this.on('remove', this.onComponentItemRemove, this);
    },

    onAfterDestroy: function () {
        if (this.searchFilterItemsCollection) {
            this.searchFilterItemsCollection.removeAll();
            this.searchFilterItemsCollection = null;
        }
    },

    onComponentItemAdd: function (searchFilter, component) {
        var collection;

        if (component.isEmptyFilter && component.getFilter) {
            collection = this.getSearchFilterItemsCollection();

            component.on('afterdestroy', function (component) {
                collection.remove(component);
            });

            collection.add(component);
        }
    },

    onComponentItemRemove: function (searchFilter, component) {
        if (component.isEmptyFilter && component.getFilter) {
            this.getSearchFilterItemsCollection().remove(component);
        }
    },

    /**
     * @returns {Ext.util.Collection}
     */
    getSearchFilterItemsCollection: function () {
        if (this.searchFilterItemsCollection === null) {
            this.searchFilterItemsCollection = new Ext.util.Collection();

            this.searchFilterItemsCollection.on('add', this.refreshSearchFilterItemsCount, this);
            this.searchFilterItemsCollection.on('remove', this.refreshSearchFilterItemsCount, this);
        }

        return this.searchFilterItemsCollection;
    },

    /**
     * Обновляет значение searchFilterItemsCount при изменениях в коллекции
     * @param collection
     */
    refreshSearchFilterItemsCount: function (collection) {
        this.setSearchFilterItemsCount(collection.getCount());
    },

    /**
     * @returns {Array}
     */
    getSearchFilterItems: function () {
        return this.getSearchFilterItemsCollection().getRange();
    },

    /**
     * Возвращает объект с описанием введенных пользователем фильтров
     *
     * @returns {Object}
     */
    getFilter: function () {
        var params = [];

        Ext.Array.each(this.getSearchFilterItems(), function (item) {
            var filter;

            if (item.isEmptyFilter && item.getFilter) {
                if (!item.isEmptyFilter()) {
                    filter = item.getFilter();

                    if (Ext.isArray(filter)) {
                        params = params.concat(filter);
                    } else {
                        params.push(filter);
                    }
                }
            }
        });

        return params;
    },

    /**
     * Валидация инпутов поисковой формы
     * @returns {Boolean}
     */
    validate: function () {
        var result;

        result = Ext.Array.reduce(this.getSearchFilterItems(), function (previous, item) {
            var isValid = true;

            if (item.validate) {
                isValid = item.validate();
            }

            return previous && isValid;
        }, true);

        return result;
    },

    /**
     * Возвращает true, если фильтры не указаны, иначе false
     *
     * @returns {boolean}
     */
    isEmptyFilter: function () {
        var result = true;

        Ext.Array.each(this.getSearchFilterItems(), function (item) {
            if (item.isEmptyFilter && !item.isEmptyFilter()) {
                result = false;

                return false; //окончание итерации
            }
        });

        return result;
    }

});
