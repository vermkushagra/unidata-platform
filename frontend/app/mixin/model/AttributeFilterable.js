/**
 * Миксин реализующий фильтрацию простых атрибутов по значению value = null
 *
 * На момент написания использовался в моделях записи и связи
 *
 *      Unidata.model.data.Record
 *      Unidata.model.data.RelationReference
 *
 * @author Ivan Marshalkin
 * @date 2016-05-20
 */

Ext.define('Unidata.mixin.model.AttributeFilterable', {

    /**
     * Применяет фильтрующую функцию к стору
     *
     * @param store
     * @param filterFn
     * @returns {{filter: (Ext.util.Filter|*), store: *, remoteFilter: *}|*}
     */
    applyDataRecordFilter: function (store, filterFn) {
        var filter,
            remoteFilter,
            oldFilters,
            result;

        remoteFilter = store.getRemoteFilter();

        store.setRemoteFilter(false);

        filter = new Ext.util.Filter({
            filterFn: filterFn
        });

        oldFilters = store.getFilters().items;
        store.addFilter(filter);

        // данные необходимы для восттановления в исходное состояние
        result = {
            filter: filter,             // добавленный фильтр
            store: store,               // хранилище к которому применили фильтр
            remoteFilter: remoteFilter,  // ранее установленный признак сортировки на сервере
            oldFilters: oldFilters
        };

        return result;
    },

    /**
     * Отфильтровывае все DQ
     *
     * @returns {*|{filter: (Ext.util.Filter|*), store: *, remoteFilter: *}}
     */
    applyDQFilter: function () {
        var filters = [],
            filterFn;

        filterFn = function () {
            return false; // отфильтровываем все
        };

        // фильтруем список dq
        if (typeof this.dqErrors === 'function') {
            filters = this.applyDataRecordFilter(this.dqErrors(), filterFn);
        }

        return filters;
    },

    /**
     * Отфильтровывает все ноды классификаторов которые не трогал пользователь
     *
     * @param dataRecord {Unidata.model.data.Record}
     */
    applyClassifierFilter: function (dataRecord) {
        var classifierFilters = [],
            filter,
            remoteFilter,
            result,
            filterFn,
            store;

        store = dataRecord.classifiers();

        // отправляем только узлы классификатора если изменялись атрибуты пользователем
        filterFn = function (classifierNodeInfo) {
            var filtered = true;

            // phantom - добавили
            // dirty деклассифицировали
            if (classifierNodeInfo.phantom || classifierNodeInfo.dirty) {
                filtered = false;
            }

            // деклассифицировали
            if (classifierNodeInfo && classifierNodeInfo.simpleAttributes) {
                if (classifierNodeInfo.simpleAttributes().getRemovedRecords().length) {
                    filtered = false;
                }

                classifierNodeInfo.simpleAttributes().each(function (simpleAttribute) {
                    if (simpleAttribute.dirty) {
                        filtered = false;
                    }
                });
            }

            // деклассифицировали
            if (classifierNodeInfo && classifierNodeInfo.arrayAttributes) {
                if (classifierNodeInfo.arrayAttributes().getRemovedRecords().length) {
                    filtered = false;
                }

                classifierNodeInfo.arrayAttributes().each(function (arrayAttributes) {
                    if (arrayAttributes.dirty) {
                        filtered = false;
                    }
                });
            }

            this.applySimpleAttributeFilterCascade(classifierNodeInfo, classifierFilters);
            this.applyArrayAttributeFilterCascade(classifierNodeInfo, classifierFilters);
            this.applyCodeAttributeFilterCascade(classifierNodeInfo, classifierFilters);
            this.applyComplexNestedFilterCascade(classifierNodeInfo, classifierFilters);

            return !filtered;
        };

        remoteFilter = store.getRemoteFilter();

        store.setRemoteFilter(false);

        filter = new Ext.util.Filter({
            filterFn: filterFn.bind(this)
        });

        store.addFilter(filter);

        // данные необходимы для восттановления в исходное состояние
        result = {
            filter: filter,             // добавленный фильтр
            store: store,               // хранилище к которому применили фильтр
            remoteFilter: remoteFilter  // ранее установленный признак сортировки на сервере
        };

        classifierFilters = Ext.Array.merge(classifierFilters, result);

        return classifierFilters;
    },

    /**
     * Применяет фильтр для простых атрибутов
     *
     * @param store
     * @returns {{filter: (Ext.util.Filter|*), store: *, remoteFilter: *}|*}
     */
    applySimpleAttributeFilter: function (store) {
        var filterFn;

        filterFn = function (item) {
            var filtered = false,
                modified = item.isModified('value'),
                value    = item.get('value');

            // не отправляем на бекенд немодифицированные атрибуты со значением null
            // во всех остальных случаях атрибуты отправляются
            if (modified) {
                if (value === null) {
                    filtered = false;
                }
            } else {
                if (value === null) {
                    filtered = true;
                }
            }

            return !filtered;
        };

        return this.applyDataRecordFilter(store, filterFn);
    },

    /**
     * Применяет фильтр для комплексных атрибутов
     *
     * @param store
     * @returns {*|{filter: (Ext.util.Filter|*), store: *, remoteFilter: *}}
     */
    applyComplexAttributeFilter: function (store) {
        var filterFn;

        filterFn = function (item) {
            var nestedRecords = item.nestedRecords();

            return (nestedRecords.getCount() > 0) || (nestedRecords.getRemovedRecords().length > 0);
        };

        return this.applyDataRecordFilter(store, filterFn);
    },

    /**
     * Применяет фильтрацияю перед сохранением для всех атрибутов любой вложенности
     *
     * @param dataRecord {Unidata.model.data.Record}
     * @returns {Object} Ассоциативный массив фильтров
     */
    applyAttributesFilterCascade: function (dataRecord) {
        var filters        = {},
            DataRecordUtil = Unidata.util.DataRecord;

        filters['simple']        = this.applySimpleAttributeFilterCascade(dataRecord);
        filters['array']         = this.applyArrayAttributeFilterCascade(dataRecord);
        filters['code']          = this.applyCodeAttributeFilterCascade(dataRecord);
        filters['complexNested'] = this.applyComplexNestedFilterCascade(dataRecord);

        // узлы классификаторы могут быть в эталонах/ориджинах сущностях (но не на записях связей)
        if (DataRecordUtil.isDataRecord(dataRecord) || DataRecordUtil.isOriginDataRecord(dataRecord)) {
            filters['classifiers'] = this.applyClassifierFilter(dataRecord);
        }

        return filters;
    },

    /**
     * Применяет фильтрацияю перед сохранением для всех атрибутов любой вложенности
     *
     * @private
     * @param dataNested
     * @param filters
     * @param attrGetterFnName Имя getter'a для store аттрибутов
     * @param attrFilterFn Функция фильтрации
     * @returns {Array}
     */
    applyAttributeFilterCascade: function (dataNested, filters, attrGetterFnName, attrFilterFn) {
        var me = this,
            localFilters,
            store;

        if (!attrGetterFnName || !attrFilterFn) {
            return filters;
        }

        dataNested = dataNested || {};
        filters    = filters    || [];

        // для простых атрибутов
        if (typeof dataNested[attrGetterFnName] === 'function') {
            store = dataNested[attrGetterFnName].apply(dataNested);

            localFilters = attrFilterFn.apply(me, [store]);
            filters = Ext.Array.merge(filters, localFilters);
        }

        // для комлексных атрибутов
        if (typeof dataNested.complexAttributes === 'function') {
            dataNested.complexAttributes().each(function (complexAttribute) {
                complexAttribute.nestedRecords().each(function (nestedRecord) {
                    localFilters = me.applyAttributeFilterCascade(nestedRecord, filters, attrGetterFnName, attrFilterFn);
                    filters = Ext.Array.merge(filters, localFilters);
                });
            });
        }

        return filters;
    },

    /**
     * Применяет фильтрацияю перед сохранением для всех простых атрибутов любой вложенности
     *
     * @private
     * @param dataNested
     * @param filters
     * @returns {*|Array}
     */
    applySimpleAttributeFilterCascade: function (dataNested, filters) {
        var attrFilterFn = this.applySimpleAttributeFilter;

        filters = this.applyAttributeFilterCascade(dataNested, filters, 'simpleAttributes', attrFilterFn);

        return filters;
    },

    /**
     * Применяет фильтрацияю перед сохранением для всех атрибутов типа массив любой вложенности
     *
     * @private
     * @param dataNested
     * @param filters
     * @returns {*|Array}
     */
    applyArrayAttributeFilterCascade: function (dataNested, filters) {
        var attrFilterFn = this.applySimpleAttributeFilter;

        filters = this.applyAttributeFilterCascade(dataNested, filters, 'arrayAttributes', attrFilterFn);

        return filters;
    },

    /**
     * Применяет фильтрацияю перед сохранением для всех альтернативных атрибутов любой вложенности
     *
     * @private
     * @param dataNested
     * @param filters
     * @returns {*|Array}
     */
    applyCodeAttributeFilterCascade: function (dataNested, filters) {
        var attrFilterFn = this.applySimpleAttributeFilter;

        filters = this.applyAttributeFilterCascade(dataNested, filters, 'codeAttributes', attrFilterFn);

        return filters;
    },

    /**
     * Применяет фильтр для nested записей
     *
     * @param store
     * @returns {*|{filter: (Ext.util.Filter|*), store: *, remoteFilter: *}}
     */
    applyNestedRecordFilter: function (store) {
        var filterFn;

        filterFn = function (item) {
            var hasSimple  = false,
                hasArray   = false,
                hasComplex = false;

            if (typeof item.simpleAttributes === 'function') {
                if (item.simpleAttributes().getCount()) {
                    hasSimple = true;
                }
            }

            if (typeof item.arrayAttributes === 'function') {
                if (item.arrayAttributes().getCount()) {
                    hasArray = true;
                }
            }

            if (typeof item.complexAttributes === 'function') {
                if (item.complexAttributes().getCount()) {
                    hasComplex = true;
                }
            }

            return hasSimple || hasComplex || hasArray;
        };

        return this.applyDataRecordFilter(store, filterFn);
    },

    /**
     * Удаляет примененные фильтры
     *
     * @param filters
     */
    revertFilter: function (filters) {
        filters =  filters || [];

        Ext.Array.each(filters, function (filterItem) {
            var store        = filterItem.store,
                oldFilters   = filterItem.oldFilters,
                remoteFilter = filterItem.remoteFilter;

            store.setRemoteFilter(false);
            store.getFilters().removeAll();
            store.setFilters(oldFilters);
            store.setRemoteFilter(remoteFilter);
        });
    },

    /**
     * Применяет фильтры каскадно для записи. Простые атрибуты не фильтруются
     *
     * @private
     * @param dataNested
     * @param filters
     * @returns {*|Array}
     */
    applyComplexNestedFilterCascade: function (dataNested, filters) {
        var me = this,
            localFilters,
            nestedRecords,
            complexAttributes;

        dataNested = dataNested || {};
        filters    = filters    || [];

        // для комлексных атрибутов
        if (typeof dataNested.complexAttributes === 'function') {
            complexAttributes = dataNested.complexAttributes();

            complexAttributes.each(function (complexAttribute) {
                nestedRecords = complexAttribute.nestedRecords();

                // сначала обрабатываем вложенные сущности, затем фильтруем текущую
                // но ни как не на оборот
                nestedRecords.each(function (nestedRecord) {
                    localFilters = me.applyComplexNestedFilterCascade(nestedRecord, filters);
                    filters = Ext.Array.merge(filters, localFilters);
                });

                // текущая фильтрация
                localFilters = me.applyNestedRecordFilter(nestedRecords);
                filters = Ext.Array.merge(filters, localFilters);
            });

            localFilters = me.applyComplexAttributeFilter(complexAttributes);
            filters = Ext.Array.merge(filters, localFilters);
        }

        return filters;
    },

    /**
     * Отменить фильтры
     *
     * @param filters Ассоциативный массив фильтров {Object}
     */
    revertFilters: function (filters) {
        Ext.Object.eachValue(filters, function (filter) {
            this.revertFilter(filter);
        }, this);
    }
});
