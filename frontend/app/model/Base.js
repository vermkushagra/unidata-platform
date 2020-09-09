Ext.define('Unidata.model.Base', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.identifier.*',
        'Ext.data.validator.*'
    ],

    schema: {
        namespace: 'Unidata.model'
    },

    fields: [
        // служебное поле для фиксации признака "глубокой" модификации
        {
            name: 'deepDirty',
            type: 'boolean',
            defaultValue: false,
            persist: false
        },
        {
            name: 'modelDirty',
            type: 'boolean',
            defaultValue: false,
            persist: false
        }
    ],

    // наблюдатель для deepDirty для связанных моделей
    deepDirtyWatcher: null,

    // объект для хранения проксируемых значений
    proxyValue: null,

    constructor: function () {
        var me = this;

        // в этот объект extjs кеширует устанавливаемые значения смотри комментарий в классе Ext.data.Model к свойству _singleProp
        // данный объект один для всех моделей и лежит в прототипе класса Ext.data.Model
        // но после внедрения deepDirty происходит многократный вызов метода model.set по иерархии моделей, что приводит
        // к некорректной установке данных для моделей лежащих по иерархии выше
        this._singleProp = {};

        this.proxyValue = {
        };

        this.deepDirtyWatcher = {
            afterEdit: function () {
                me.setDeepDirtyIf();
            }
        };

        this.proxyDirtyProperty();

        this.proxyHasOneAssociations();
        this.proxyHasManyAssociations();

        this.callParent(arguments);
    },

    /**
     * Проксирует доступ к свойству dirty модели
     */
    proxyDirtyProperty: function () {
        var me = this;

        Object.defineProperty(me, 'dirty', {
            get: function () {
                return me.proxyValue.dirty;
            },
            set: function (newValue) {
                me.proxyValue.dirty = newValue;

                me.set('modelDirty', newValue);
                me.modelDirty = newValue;
            }
        });
    },

    /**
     * Проксирует доступ к ассоциациям типа hasOne
     */
    proxyHasOneAssociations: function () {
        var me = this,
            hasOne = this.getAllHasOneAssociations();

        Ext.Array.each(hasOne, function (association) {
            var propertyName = association.getInstanceName();

            if (!association.deepDirty) {
                return;
            }

            // проксируем доступ к свойствам, которые создаются внутри библиотеки ExtJS
            Object.defineProperty(me, propertyName, {
                get: function () {
                    return me.proxyValue[propertyName];
                },
                set: function (newValue) {
                    var oldValue = me.proxyValue[propertyName];

                    if (oldValue) {
                        oldValue.unjoin(me.deepDirtyWatcher);
                    }

                    me.proxyValue[propertyName] = newValue;

                    if (newValue) {
                        newValue.join(me.deepDirtyWatcher);
                    }
                }
            });
        });
    },

    /**
     * Проксирует доступ к ассоциациям типа hasMany
     */
    proxyHasManyAssociations: function () {
        var me = this,
            hasMany = this.getAllHasManyAssociations();

        Ext.Array.each(hasMany, function (association) {
            var propertyName = association.getInstanceName();

            if (!association.deepDirty) {
                return;
            }

            // проксируем доступ к свойствам, которые создаются внутри библиотеки ExtJS
            Object.defineProperty(me, propertyName, {
                get: function () {
                    return me.proxyValue[propertyName];
                },
                set: function (newValue) {
                    var oldValue = me.proxyValue[propertyName];

                    if (oldValue && oldValue.isStore) {
                        oldValue.un('datachanged', me.setDeepDirtyIf, me);
                        oldValue.un('update', me.setDeepDirtyIf, me);
                        // нужно ли добавлять подписку на oldValue.un('add', someFn); ???
                    }

                    me.proxyValue[propertyName] = newValue;

                    if (newValue && newValue.isStore) {
                        newValue.on('datachanged', me.setDeepDirtyIf, me);
                        newValue.on('update', me.setDeepDirtyIf, me);
                        // нужно ли добавлять подписку на oldValue.on('add', someFn); ???
                    }
                }
            });
        });
    },

    /**
     * Проставляет значение флага deepDirty
     */
    setDeepDirtyIf: function () {
        var dirty = false;

        // если запись находится в режиме удаления или уже удалена то здесь делать нечего. смотри UN-7610
        // флаги проставляются в методах erase и drop класса Ext.data.Model
        if (this.erasing || this.erased || this.dropped) {
            return;
        }

        if (Ext.isFunction(this.checkDeepDirty)) {
            dirty = this.checkDeepDirty();
        }

        this.set('deepDirty', Boolean(dirty));
        this.deepDirty = Boolean(dirty);
    },

    /**
     * Возвращает истину если произведено "глубокое" изменении модели
     *
     * @returns {boolean}
     */
    checkDeepDirty: function () {
        var me = this,
            dirty = false,
            hasOne = this.getAllHasOneAssociations(),
            hasMany = this.getAllHasManyAssociations(),
            associatedItem;

        Ext.Array.each(hasOne, function (association) {
            // если нет необходимости вести "глубокое" наблюдение то нам здесь делать нечего
            if (!association.deepDirty) {
                return;
            }

            associatedItem = me.getAssociatedItemFromAssociation(association);

            if (!associatedItem) {
                return;
            }

            // изменена, либо имеет глубокие изменения, либо она новая
            if (associatedItem.dirty || associatedItem.get('deepDirty') || associatedItem.phantom) {
                dirty = true;
            }
        });

        Ext.Array.each(hasMany, function (association) {
            // если нет необходимости вести "глубокое" наблюдение то нам здесь делать нечего
            if (!association.deepDirty) {
                return;
            }

            associatedItem = me.getAssociatedItemFromAssociation(association);

            if (!associatedItem || !associatedItem.isStore) {
                return;
            }

            associatedItem.each(function (record) {
                // изменена, либо имеет глубокие изменения, либо она новая
                if (record.dirty || record.get('deepDirty') || record.phantom) {
                    dirty = true;
                }
            });
        });

        return dirty;
    },

    /**
     * Возвращает истину если модель изменена или она имеет "глубокие" изменения
     *
     * @returns {boolean|*}
     */
    checkDirty: function () {
        return this.dirty || this.get('deepDirty');
    },

    /**
     * Помечает модель "глубоко" неизмененной + помечает текущую модель неизмененной
     */
    commitDeepDirty: function () {
        var me = this,
            hasOne = this.getAllHasOneAssociations(),
            hasMany = this.getAllHasManyAssociations(),
            associatedItem;

        this.commit();

        Ext.Array.each(hasOne, function (association) {
            if (!association.deepDirty) {
                return;
            }

            associatedItem = me.getAssociatedItemFromAssociation(association);

            if (!associatedItem) {
                return;
            }

            associatedItem.commitDeepDirty();
        });

        Ext.Array.each(hasMany, function (association) {
            if (!association.deepDirty) {
                return;
            }

            associatedItem = me.getAssociatedItemFromAssociation(association);

            if (!associatedItem || !associatedItem.isStore) {
                return;
            }

            associatedItem.each(function (record) {
                record.commitDeepDirty();
            });
        });
    },

    /**
     * Возвращает массив метаданных ассоциаций hasOne
     *
     * @returns {Array}
     */
    getAllHasOneAssociations: function () {
        var associations = [];

        Ext.Object.each(this.associations, function (associationName, association) {
            // hasOne
            if (association.association instanceof Ext.data.schema.OneToOne) {
                associations.push(association);
            }
        });

        return associations;
    },

    /**
     * Возвращает массив метаданных ассоциаций hasMany
     *
     * @returns {Array}
     */
    getAllHasManyAssociations: function () {
        var associations = [];

        Ext.Object.each(this.associations, function (associationName, association) {
            // hasMany
            if (association.association instanceof Ext.data.schema.ManyToOne) {
                associations.push(association);
            }
        });

        return associations;
    },

    /**
     * Возвращает связанную сущность с ассоциацией. Может быть модель для hasOne или стор для hasMany
     *
     * @param association
     * @returns {*}
     */
    getAssociatedItemFromAssociation: function (association) {
        var result = null;

        if (association && association.getterName && Ext.isFunction(this[association.getterName])) {
            result = this[association.getterName]();
        }

        return result;
    },

    /**
     * Универсальный метод для получения доступных hasMany сторов
     *
     * @param {Array} associationNames - массив с именами hasMany
     *
     * @returns {Ext.data.Store[]}
     */
    getHasManyStores: function (associationNames) {
        var me = this,
            stores = [],
            hasMany = this.getAllHasManyAssociations(),
            associatedItem;

        Ext.Array.each(hasMany, function (association) {
            if (!Ext.Array.contains(associationNames, association.name)) {
                return;
            }

            associatedItem = me.getAssociatedItemFromAssociation(association);

            if (associatedItem && associatedItem.isStore) {
                stores.push(associatedItem);
            }
        });

        return stores;
    }

});
