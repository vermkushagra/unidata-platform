/**
 * Ресстр/справочник
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.Entity', {

    extend: 'Unidata.module.search.SearchQueryTerm',

    termName: 'entity',

    config: {
        name: null, // имя реестра/справочника
        metaRecord: null,
        searchFields: null
    },

    /**
     * @protected
     */
    filterDisplayable: true,

    /**
     * @protected
     */
    termsUpdateRequired: true, // флаг, сигнализирующий о необходимости пересоздать термы для returnFields

    statics: {
        metaRecordsDeferred: null, // кэш списка мета-моделей

        metaRecordPromiseByName: {}, // кэш промисов с мета-моделями по имени

        /**
         * Возвращает промис с metaRecord
         *
         * @param {string} entityName
         * @returns {Ext.promise.Promise}
         */
        getMetaRecord: function (entityName) {
            var deferred = new Ext.Deferred();

            if (Ext.isEmpty(entityName)) {
                deferred.reject(new Error('entityName is not set'));

                return deferred.promise;
            }

            if (this.metaRecordPromiseByName[entityName]) {
                return this.metaRecordPromiseByName[entityName];
            } else {
                this.metaRecordPromiseByName[entityName] = deferred;
            }

            this.getMetaRecordsList().then(
                function (metaRecords) {
                    var metaRecord,
                        newMetaRecord;

                    metaRecord = Ext.Array.findBy(metaRecords, function (metaRecord) {
                        return metaRecord.get('name') === entityName;
                    });

                    if (!metaRecord) {
                        throw new Error('metaRecord "' + entityName + '" not found');
                    }

                    newMetaRecord  = Ext.create('Unidata.model.entity.' + metaRecord.getType());

                    newMetaRecord.setId(entityName);
                    newMetaRecord.load({
                        success: function (metaRecord) {
                            deferred.resolve(metaRecord);
                        },
                        failure: function () {
                            deferred.reject(new Error('Error while loading metaRecord'));
                        }
                    });
                },
                function (error) {
                    deferred.reject(error);
                }
            ).done();

            return deferred.promise;
        },

        /**
         * Загружаем и кэшируем все metaRecords
         * TODO: использовать общий кэш, когда он будет
         *
         * @param {boolean} [updateCache] - сбрасывает кэш
         * @returns {Ext.promise.Promise}
         */
        getMetaRecordsList: function (updateCache) {
            var deferred = this.metaRecordsDeferred,
                entitiesPromise,
                lookupEntitiesPromise;

            if (updateCache || !this.metaRecordsDeferred) {
                this.metaRecordsDeferred = deferred = new Ext.Deferred();
            }

            entitiesPromise = Unidata.util.api.MetaRecord.getMetaRecords({
                entityType: Unidata.util.MetaRecord.TYPE_ENTITY
            });

            lookupEntitiesPromise = Unidata.util.api.MetaRecord.getMetaRecords({
                entityType: Unidata.util.MetaRecord.TYPE_LOOKUP
            });

            Ext.Deferred.all([entitiesPromise, lookupEntitiesPromise]).then(
                function (data) {
                    deferred.resolve(Ext.Array.flatten(data));
                },
                function (error) {
                    deferred.reject(error);
                }
            ).done();

            return deferred.promise;
        }
    },

    updateName: function (entityName) {
        var me = this;

        this.termsUpdateRequired = true;

        this.self.getMetaRecord(entityName)
            .then(
                function (metaRecord) {
                    me.setMetaRecord(metaRecord);
                }
            );
    },

    /**
     * Возвращает промис с данными
     *
     * @returns {Ext.promise.Promise}
     */
    getTermData: function () {
        var me = this,
            promise;

        promise = this.updateFieldTerms()
            .then(function () {
                return me.getTermsData();
            })
            .then(function (termsData) {
                return me.mergeObjects(
                    {
                        entity: me.getName()
                    },
                    termsData
                );
            });

        return promise;
    },

    getTermSaveData: function () {
        return this.getSaveConfigData({
            include: ['name']
        });
    },

    /**
     * Обновляет список термов, которые привязаны к текущему справочнику/реестру
     *
     * @see {Unidata.module.search.term.SearchField}
     * @see {Unidata.module.search.term.ReturnField}
     *
     * @returns {*}
     */
    updateFieldTerms: function () {
        var promises = [];

        if (!this.termsUpdateRequired) {
            return Ext.Deferred.resolved();
        }

        promises.push(this.createReturnFieldTerms());
        promises.push(this.createSearchFieldTerms());

        return Ext.Deferred.all(promises)
            .then(this.fieldTermsUpdated.bind(this));
    },

    /**
     * Проставляем флаг, после обновления термов
     */
    fieldTermsUpdated: function () {
        this.termsUpdateRequired = false;
    },

    /**
     * Создаёт термы для returnFields
     * @see {Unidata.module.search.term.ReturnField}
     *
     * @returns {Ext.promise.Promise}
     */
    createReturnFieldTerms: function () {
        return this.createFieldTerms(
            this.getReturnFields(),
            Unidata.module.search.term.ReturnField
        );
    },

    /**
     * Создаёт термы для searchFields
     * @see {Unidata.module.search.term.SearchField}
     *
     * @returns {Ext.promise.Promise}
     */
    createSearchFieldTerms: function () {
        return this.createFieldTerms(
            this.getPromisedSearchFields(),
            Unidata.module.search.term.SearchField
        );
    },

    /**
     * Общий метод по созанию returnFields и searchFields
     *
     * @param {Ext.promise.Promise} fieldPromise - промис, возвращающий массив строк-атрибутов
     * @param {Unidata.module.search.term.ReturnField | Unidata.module.search.term.SearchField} FieldClass - конструктор
     * @returns {*|Ext.promise.Promise|Promise|PromiseLike<T>|Promise<T>}
     */
    createFieldTerms: function (fieldPromise, FieldClass) {
        var me = this;

        // удаляем текущие термы
        this.destroyTerms();

        return fieldPromise.then(
            function (fields) {
                var fieldTerms = [];

                Ext.Array.each(fields, function (field) {
                    fieldTerms.push(new FieldClass({
                        name: field
                    }));
                });

                me.addTerm(fieldTerms);
            }
        );
    },

    setFilterDisplayable: function (flag) {
        this.filterDisplayable = flag;
        this.termsUpdateRequired = true;
    },

    /**
     * Возвращает промис с массивом отображаемых атрибутов
     *
     * @returns {Ext.promise.Promise}
     */
    getReturnFields: function () {
        var entityName = this.getName(),
            filters = [],
            promise;

        if (this.filterDisplayable) {
            filters.push({
                property: 'displayable',
                value: true
            });
        }

        // если не задан entityName, по тихому резолвим пустой массив
        if (Ext.isEmpty(entityName)) {
            return Ext.Deferred.resolved([]);
        }

        promise = this.self.getMetaRecord(entityName)
            .then(
                function (metaRecord) {
                    var returnFields;

                    returnFields = Unidata.util.UPathMeta.buildAttributePaths(
                        metaRecord,
                        filters
                    );

                    return returnFields;
                }
            );

        return promise;
    },

    updateSearchFields: function () {
        this.termsUpdateRequired = true;
    },

    /**
     * Возвращает промис с массивом поисковых атрибутов
     *
     * @returns {Ext.promise.Promise}
     */
    getPromisedSearchFields: function () {
        var entityName = this.getName(),
            searchFields,
            filters,
            promise;

        // если не задан entityName, по тихому резолвим пустой массив
        if (Ext.isEmpty(entityName)) {
            return Ext.Deferred.resolved([]);
        }

        searchFields = this.getSearchFields();

        if (Ext.isArray(searchFields)) {
            return Ext.Deferred.resolved(searchFields);
        }

        filters = [
            {
                property: 'searchable',
                value: true
            },
            // поля с типом boolean не включаем см. UN-1477
            {
                filterFn: function (record) {
                    return record.get('typeValue') !== 'Boolean';
                }
            }
        ];

        promise = this.self.getMetaRecord(entityName)
            .then(
                function (metaRecord) {
                    var returnFields;

                    returnFields = Unidata.util.UPathMeta.buildAttributePaths(
                        metaRecord,
                        filters
                    );

                    return returnFields;
                }
            );

        return promise;
    }

});
