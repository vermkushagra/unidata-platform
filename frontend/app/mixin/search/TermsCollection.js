/**
 * Миксин, добавляющий в компонент коллекцию термов и методы работы с ней
 *
 * @author Aleksandr Bavin
 * @date 2018-02-15
 */
Ext.define('Unidata.mixin.search.TermsCollection', {

    extend: 'Ext.Mixin',

    mixinConfig: {},

    isTermsCollectionMixin: true,

    /**
     * Карта термов по имени, используетя для биндинга term.{termName}
     *
     * @type {Object}
     */
    term: null,

    /**
     * @param {Unidata.module.search.SearchQueryTerm | Unidata.module.search.SearchQueryTerm[]} term
     * @returns {Unidata.module.search.SearchQueryTerm | Unidata.module.search.SearchQueryTerm[]} term
     */
    addTerm: function (term) {
        var termsCollection = this.getTermsCollection(),
            terms = Ext.isArray(term) ? term : [term],
            newTerms;

        newTerms = Ext.Array.filter(terms, function (term) {
            if (Ext.isObject(term) && !(term instanceof Unidata.module.search.SearchQueryTerm)) {
                term = Ext.create(term);
            }

            return !termsCollection.contains(term);
        });

        // проверяем новые термы перед вставкой
        Ext.Array.each(newTerms, function (newTerm) {
            var newTermName = newTerm.getTermName();

            if (newTermName && termsCollection.findIndex('termName', newTermName) !== -1) {
                // в коллекции не должно быть термов с одинаковым именем,
                // иначе не понятно, как строить карту для биндинга
                throw new Error('Term duplicate for "' + newTermName + '"');
            }
        }, this);

        termsCollection.add(newTerms);

        return term;
    },

    /**
     * @param {Unidata.module.search.SearchQueryTerm | Unidata.module.search.SearchQueryTerm[]} term
     */
    removeTerm: function (term) {
        this.getTermsCollection().remove(term);
    },

    destroyTerms: function () {
        Ext.Array.each(this.getTerms(), function (term) {
            term.destroy();
        });
    },

    /**
     * Возвращает все термы, если нужно, собирает и дочерние
     *
     * @param {boolean} [deep]
     * @returns {Unidata.module.search.SearchQueryTerm[]}
     */
    getTerms: function (deep) {
        var termsCollection = this.getTermsCollection(),
            result = termsCollection.getRange();

        if (deep) {
            termsCollection.each(function (item) {
                if (item.isTermsCollectionMixin) {
                    result = result.concat(item.getTerms(deep));
                }
            });
        }

        return result;
    },

    /**
     * Находит и возвращает терм по имени
     *
     * @param {string} termName
     * @returns {null | Unidata.module.search.SearchQueryTerm}
     */
    findTerm: function (termName) {
        return this.getTermsCollection().findBy(function (term) {
            return term.getTermName() === termName;
        });
    },

    /**
     * Сбор данных из термов
     *
     * @returns {Ext.promise.Promise} - промис с данными
     */
    getTermsData: function () {
        var me = this,
            promisedData = [],
            termsByName = {};

        this.syncTerms();

        this.getTermsCollection().each(
            /**
             * @param {Unidata.module.search.SearchQueryTerm} term
             */
            function (term) {
                var termIsActive = term.getTermIsActive(),
                    termIsSearchable = term.isSearchable(),
                    termName,
                    termData;

                if (!termIsActive || !termIsSearchable) {
                    return;
                }

                termName = term.getTermName();
                termData = term.getTermData();

                if (termName) {
                    termsByName[termName] = term;
                }
                promisedData.push(termData);
            },
            this
        );

        return Ext.Deferred.all(promisedData).then(
            function (data) {
                var result = {};

                Ext.Array.each(data, function (termData) {
                    me.mergeObjects(result, termData);
                });

                return result;
            }
        );
    },

    /**
     * Сбор данных для сохранения и последующего восстановления
     *
     * @returns {Ext.promise.Promise} - промис с данными
     */
    getSaveData: function () {
        var terms = [],
            result;

        this.syncTerms();

        result = {
            xclass: this.$className,
            loadTerms: terms
        };

        this.getTermsCollection().each(
            /**
             * @param {Unidata.module.search.SearchQueryTerm} term
             */
            function (term) {
                if (term.isSavable()) {
                    terms.push(term.getTermSaveData());
                }
            },
            this
        );

        return result;
    },

    /**
     * Возвращает всё, что работает асинхронно
     *
     * @returns {Array}
     */
    gatAllSchedules: function () {
        var terms = this.getTerms(true),
            schedules = [];

        Ext.Array.each(terms, function (term) {
            if (term.isConfigBindMixin) {
                Ext.Array.each(term.getAllSubs(), function (stub) {
                    if (!stub.destroyed) {
                        schedules.push(stub);

                        if (stub.bindings && stub.bindings.length) {
                            Ext.Array.each(stub.bindings, function (binding) {
                                schedules.push(binding);
                            });
                        }
                    }
                });
            }
        });

        return schedules;
    },

    /**
     * Актуализирует все значения термов
     */
    syncTerms: function () {
        var schedules = this.gatAllSchedules(),
            hasScheduled = false,
            i, ln, item;

        ln = schedules.length;

        do {
            for (i = 0; i < ln; i++) {
                item = schedules[i];

                if (item.scheduled) {
                    hasScheduled = true;
                    item.scheduled = false;

                    item.react();

                    break;
                }
            }

            if (i === ln) {
                hasScheduled = false;
            }

        } while (hasScheduled);
    },

    /**
     * Мерж двух объектов
     * Массивы объединяются
     *
     * @param {Object} targetObj
     * @param {Object} sourceObj
     */
    mergeObjects: function (targetObj, sourceObj) {
        Ext.Object.each(sourceObj, function (key, sourceValue) {
            var targetValue;

            if (!targetObj.hasOwnProperty(key)) {
                targetObj[key] =  Ext.clone(sourceValue);
            } else {
                targetValue = targetObj[key];

                if (typeof targetValue === typeof sourceValue) {
                    if (Ext.isObject(sourceValue)) {
                        this.mergeObjects(targetValue, sourceValue);

                        return;
                    }

                    if (Ext.isArray(sourceValue)) {
                        targetObj[key] = Ext.Array.unique(targetValue.concat(sourceValue));

                        return;
                    }
                }

                targetObj[key] = sourceValue;
            }

        }, this);

        return targetObj;
    },

    /**
     * Точка доступа к добавляемым термам
     *
     * @param {Unidata.module.search.SearchQueryTerm} term
     */
    onTermAdd: Ext.emptyFn,

    /**
     * Точка доступа к удаляемым термам
     *
     * @param {Unidata.module.search.SearchQueryTerm} term
     */
    onTermRemove: Ext.emptyFn,

    /**
     * Вызывается после обновления карты термов
     */
    onTermsMapUpdate: Ext.emptyFn,

    privates: {
        /**
         * @type {Ext.util.Collection}
         */
        termsCollection: null,

        /**
         * @returns {Ext.util.Collection}
         */
        getTermsCollection: function () {
            if (this.termsCollection) {
                return this.termsCollection;
            }

            this.termsCollection = new Ext.util.Collection({
                grouper: {
                    property: '$className'
                },
                decoder: function (item) {
                    return item;
                },
                listeners: {
                    add: this.termsCollectionAdd,
                    remove: this.termsCollectionRemove,
                    endupdate: this.termsCollectionEndupdate,
                    scope: this
                }
            });

            return this.termsCollection;
        },

        termsCollectionAdd: function (collection, details) {
            Ext.Array.each(details.items, function (term) {
                term.on('destroy', this.onTermDestroy, this);
                this.onTermAdd(term);
            }, this);
        },

        termsCollectionRemove: function (collection, details) {
            Ext.Array.each(details.items, function (term) {
                term.un('destroy', this.onTermDestroy, this);
                this.onTermRemove(term);
            }, this);
        },

        onTermDestroy: function (term) {
            this.removeTerm(term);
        },

        termsCollectionEndupdate: function () {
            clearTimeout(this.termsUpdateTimer);
            this.termsUpdateTimer = Ext.defer(function () {
                var termsMap;

                if (!this.term) {
                    this.term = {};
                }

                this.getTermsCollection().each(
                    /**
                     * @param {Unidata.module.search.SearchQueryTerm} term
                     */
                    function (term) {
                        var termsMap = this.term,
                            termName = term.getTermName(),
                            path, i, ln;

                        if (Ext.isEmpty(termName)) {
                            return;
                        }

                        path = termName.split('.');

                        for (i = 0, ln = path.length; i < ln; i++) {
                            if (i === ln - 1) {
                                termsMap[path[i]] = term;
                            } else {
                                if (termsMap[path[i]] === undefined) {
                                    termsMap[path[i]] = {};
                                }

                                termsMap = termsMap[path[i]];
                            }
                        }
                    },
                    this
                );

                this.onTermsMapUpdate();
            }, 100, this);
        }
    }

});
