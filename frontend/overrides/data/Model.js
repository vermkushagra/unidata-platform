Ext.define('Ext.overrides.data.Model', {
    override: 'Ext.data.Model',

    //compatibility : '5.1.0.107',

    // Перенесено из 5.1.1, для передачи options, при получении getAssociatedData
    //
    // from Ivan Marshalkin:
    // функция содержит код для удаления timpId в моделях, который необходимо оставить
    //
    getData: function (options) {
        var me = this,
            ret = {},
            opts = (options === true) ? me._getAssociatedOptions : (options || ret), //cheat
            data = me.data,
            associated = opts.associated,
            changes = opts.changes,
            critical = changes && opts.critical,
            content = changes ? me.modified : data,
            fieldsMap = me.fieldsMap,
            persist = opts.persist,
            serialize = opts.serialize,
            criticalFields, field, n, name, value,
            idProperty = this.getIdProperty();

        // DON'T use "opts" from here on...

        // Keep in mind the two legacy use cases:
        //  - getData() ==> Ext.apply({}, me.data)
        //  - getData(true) ==> Ext.apply(Ext.apply({}, me.data), me.getAssociatedData())

        if (content) { // when processing only changes, me.modified could be null
            for (name in content) {
                value = data[name];

                field = fieldsMap[name];

                if (field) {
                    if (persist && !field.persist) {
                        continue;
                    }

                    if (serialize && field.serialize) {
                        value = field.serialize(value, me);
                    }
                }

                ret[name] = value;
            }
        }

        if (critical) {
            criticalFields = me.self.criticalFields || me.getCriticalFields();

            for (n = criticalFields.length; n-- > 0;) {
                name = (field = criticalFields[n]).name;

                if (!(name in ret)) {
                    value = data[name];

                    if (serialize && field.serialize) {
                        value = field.serialize(value, me);
                    }
                    ret[name] = value;
                }
            }
        }

        if (associated) {
            me.getAssociatedData(ret, opts); // pass ret so new data is added to our object
        }

        // ------------------------------------
        // начала кода удаления не нужных полей
        // ------------------------------------
        // удаляем tempId
        if (idProperty === 'tempId') {
            delete ret[idProperty];
        }

        // удаляем служебные поля для глубокого отслеживания изменений
        Ext.Array.each(['deepDirty', 'modelDirty'], function (item) {
            delete ret[item];
        }, this);
        // ------------------------------------
        // конец кода удаления не нужных полей
        // ------------------------------------

        return ret;
    },

    // Перенесено из версии 5.1.1 с целью использования options
    getAssociatedData: function (result, options) {
        var me = this,
            associations = me.associations,
            deep, i, item, items, itemData, length,
            record, role, roleName, opts, clear, associated;

        result = result || {};

        me.$gathering = 1;

        if (options) {
            options = Ext.Object.chain(options);
        }

        for (roleName in associations) {
            role = associations[roleName];
            item = role.getAssociatedItem(me);

            if (!item || item.$gathering) {
                continue;
            }

            if (item.isStore) {
                item.$gathering = 1;

                items = item.getData().items; // get the records for the store
                length = items.length;
                itemData = [];

                for (i = 0; i < length; ++i) {
                    // NOTE - we don't check whether the record is gathering here because
                    // we cannot remove it from the store (it would invalidate the index
                    // values and misrepresent the content). Instead we tell getData to
                    // only get the fields vs descend further.
                    record = items[i];
                    deep = !record.$gathering;
                    record.$gathering = 1;

                    if (options) {
                        associated = options.associated;

                        if (associated === undefined) {
                            options.associated = deep;
                            clear = true;
                        } else if (!deep) {
                            options.associated = false;
                            clear = true;
                        }
                        opts = options;
                    } else {
                        opts = deep ? me._getAssociatedOptions : me._getNotAssociatedOptions;
                    }
                    itemData.push(record.getData(opts));

                    if (clear) {
                        options.associated = associated;
                        clear = false;
                    }
                    delete record.$gathering;
                }

                delete item.$gathering;
            } else {
                opts = options || me._getAssociatedOptions;

                if (options && options.associated === undefined) {
                    opts.associated = true;
                }
                itemData = item.getData(opts);
            }

            result[roleName] = itemData;
        }

        delete me.$gathering;

        return result;
    },

    getErrorMessages: function () {
        return Ext.Object.getValues(this.getValidation().getData()).filter(
            function (item) {
                return item !== true;
            }
        );
    },

    /**
     * Метод безопасного удаления моделей
     * В ходе работы над багом UN-6906 выяснилось, что в случае провала удаления модели на сервере
     * ее associations остаются удаленными.
     * Данный метод реализует удаление associations только после успешного удаления модели на сервере.
     *
     * Метод является экспериментальным.
     * Впоследствии рекомендуется рассмотреть возможность использования данного метода для всех случаев удаления вместо erase
     * @param options
     */
    safeErase: function (options) {
        var me = this;

        me.erasing = true;

        me.drop(false);
        delete me.erasing;
        options.success = this.wrapSuccessCallback(options.success, options.scope);

        return me.save(options);
    },

    wrapSuccessCallback: function (callback, scope) {
        var me = this,
            fn;

        scope = scope || me;

        fn = function () {
            me.dropAssociations();

            if (callback) {
                callback.apply(scope, arguments);
            }
        };

        return fn.bind(scope);
    },

    /**
     * Удалить associations модели
     */
    dropAssociations: function () {
        var me = this,
            associations = me.associations,
            session = me.session,
            roleName;

        if (associations) {
            for (roleName in associations) {
                associations[roleName].onDrop(me, session);
            }
        }
    }
});
