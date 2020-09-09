/**
 * Биндинг к config значениям любого класса
 *
 * @author Aleksandr Bavin
 * @date 2018-02-13
 */
Ext.define('Unidata.mixin.ConfigBind', {

    extend: 'Ext.Mixin',

    isConfigBindMixin: true,

    mixinConfig: {
        id: 'configBind',
        after: {
            constructor: 'initConfigBind'
        },
        before: {
            constructor: 'initComponentStub',
            destroy: 'destroyConfigBind'
        }
    },

    /**
     * Данные для прямого биндинга к свойствам компонента
     * @private
     */
    rootStub: null,
    componentStubName: 'component',

    stubsCollection: null,
    configStubsCollection: null,
    bindingsCollection: null,

    destroyConfigBind: function () {
        this.rootStub.destroy();
        this.getStubsCollection().destroy();
        this.getConfigStubsCollection().destroy();
        this.getBindingsCollection().destroy();
        this.stubsCollection = null;
        this.configStubsCollection = null;
        this.bindingsCollection = null;
    },

    initConfigBind: function () {
        this.initStubUpdater();
    },

    initComponentStub: function () {
        var componentStub;

        this.rootStub = new Unidata.bind.RootConfigStub();

        componentStub = this.rootStub.getChild(this.componentStubName);
        componentStub.set(this);
    },

    /**
     * Инициализация биндингов
     * @private
     */
    initStubUpdater: function () {
        var me = this,
            configurator = this.getConfigurator(),
            configName;

        function bindUpdate (configName) {
            var config = configurator.configs[configName],
                updateName,
                updateFn;

            if (config instanceof Ext.Config) {
                updateName = config.names.update;
                updateFn = me[updateName];

                // если еще не обёрнута - оборачиваем
                if (!updateFn || (updateFn && !updateFn['$configBind'])) {
                    me[updateName] = function (newValue, oldValue) {
                        if (updateFn) {
                            updateFn.call(me, newValue, oldValue);
                        }

                        me.afterAnyUpdateCall(configName, newValue, oldValue);
                    };

                    me[updateName].$configBind = true;
                }
            }
        }

        for (configName in configurator.configs) {
            bindUpdate(configName);
        }
    },

    /**
     * Метод вызывается после вызова любого из update методов
     *
     * @param configName
     * @param newValue
     * @private
     */
    afterAnyUpdateCall: function (configName, newValue) {
        var configStubsCollection = this.getConfigStubsCollection();

        // если новое значение - isConfigBindMixin, вызываем invalidate для обновления значений глубокого биндинга
        configStubsCollection.each(function (stub) {
            if (stub.name === configName && newValue && Ext.isObject(newValue) && newValue.isConfigBindMixin) {
                stub.invalidate(true);
            }
        }, this);

        this.notifyConfigStubs();
    },

    /**
     * Уведомление биндингов об изменениях
     * @private
     */
    notifyConfigStubs: function () {
        var configStubsCollection = this.getConfigStubsCollection(),
            configurator = this.getConfigurator(),
            removeConfigStubs = [];

        if (this.destroyed || this.isDestroyed) {
            return;
        }

        configStubsCollection.each(function (stub) {
            var config = configurator.configs[stub.name],
                getValue;

            if (stub.destroyed) {
                removeConfigStubs.push(stub);

                return;
            }

            if (config) {
                getValue = this[config.names.get]();

                if (getValue !== stub.getValue()) {
                    stub.set(getValue);
                }

                stub.schedule();
            }
        }, this);

        configStubsCollection.remove(removeConfigStubs);
    },

    /**
     * Уведомление биндингов с задержкой
     * @private
     */
    notifyConfigStubsDelayed: function () {
        clearTimeout(this.notifyConfigStubsTimer);
        this.notifyConfigStubsTimer = Ext.defer(this.notifyConfigStubs, 100, this);
    },

    /**
     * Вызывается после вызова сеттера, для обновления биндинга
     *
     * @param configName
     * @param newValue
     * @private
     */
    afterSetCall: function (configName, newValue) {
        var configurator = this.getConfigurator(),
            config = configurator.configs[configName],
            getValue = this[config.names.get]();

        this.getConfigStubsCollection().each(function (stub) {
            if (stub.name === configName) {
                // если текущее значение конфига не соответствует хранящимся данным в stub - перезаписываем
                if (getValue !== newValue) {
                    Ext.Array.each(stub.bindings, function (binding) {
                        // сбрасываем предыдущее значение биндинга, что бы корректно сработал react
                        // TODO: поискать более корректные способы
                        binding.lastValue = undefined;
                    });

                    stub.set(getValue);
                }
            }
        }, this);
    },

    /**
     * @returns {Ext.util.Collection}
     */
    getStubsCollection: function () {
        if (!this.stubsCollection) {
            this.stubsCollection = new Ext.util.Collection();
        }

        return this.stubsCollection;
    },

    /**
     * Метод вызывается, если данный компонент фигурирует в биндинге
     *
     * @param {Ext.app.bind.Stub} stub
     */
    addStub: function (stub) {
        this.getStubsCollection().add(stub);
    },

    /**
     * Метод вызывается, если данный компонент фигурирует в биндинге
     *
     * @param {Ext.app.bind.Stub} stub
     */
    removeStub: function (stub) {
        this.getStubsCollection().remove(stub);
    },

    /**
     * @returns {Ext.util.Collection}
     */
    getConfigStubsCollection: function () {
        if (!this.configStubsCollection) {
            this.configStubsCollection = new Ext.util.Collection();
        }

        return this.configStubsCollection;
    },

    /**
     * Метод вызывается, если конфиг свойство компонента фигурирует в биндинге
     *
     * @param {Ext.app.bind.Stub} stub
     */
    addConfigStub: function (stub) {
        var stubName = stub.name,
            configurator = this.getConfigurator(),
            config = configurator.configs[stubName],
            setName,
            setFn,
            configStubsCollection;

        if (config) {
            setName = config.names.set;
            setFn = this[setName];

            // если еще не обёрнута - оборачиваем сеттер
            if (!setFn || (setFn && !setFn['$configBind'])) {
                this[setName] = function (value) {
                    var result = value;

                    if (setFn) {
                        result = setFn.call(this, value);
                    }

                    this.afterSetCall(stubName, value);

                    return result;
                }.bind(this);

                this[setName].$configBind = true;
            }

            configStubsCollection = this.getConfigStubsCollection();

            if (!configStubsCollection.contains(stub)) {
                configStubsCollection.add(stub);
            }
        }
    },

    /**
     * Метод вызывается, если конфиг свойство компонента фигурирует в биндинге
     *
     * @param {Ext.app.bind.Stub} stub
     */
    removeConfigStub: function (stub) {
        this.getConfigStubsCollection().remove(stub);
    },

    /**
     * Возвращает все stub
     *
     * @returns {Ext.app.bind.Stub[]}
     */
    getAllSubs: function () {
        var stubsCollection = this.getStubsCollection().getRange(),
            configStubsCollection = this.getConfigStubsCollection().getRange();

        return stubsCollection.concat(configStubsCollection);
    },

    /**
     * Обновляет все значения stub
     */
    invalidateStub: function () {
        var stubsCollection = this.getStubsCollection(),
            removeStubs = [];

        stubsCollection.each(function (stub) {
            if (stub.destroyed) {
                removeStubs.push(stub);
            } else {
                stub.invalidate(true);
            }
        });

        if (removeStubs.length) {
            stubsCollection.remove(removeStubs);
        }
    },

    /**
     * Прямой бинд к свойству конфига
     *
     * @param {string} path
     * @param callback
     * @param scope
     * @param {Object} [options]
     * @param {boolean} [options.single]
     * @returns {*}
     */
    bind: function (path, callback, scope, options) {
        var bindingsCollection = this.getBindingsCollection(),
            stub,
            binding;

        stub = this.rootStub.getChild(this.componentStubName + '.' + path);
        stub.inspectValue(stub.getParentValue());

        binding = stub.bind(callback, scope, options);

        bindingsCollection.add(binding);

        return binding;
    },

    unbind: function (path, callback, scope) {
        var bindingsCollection = this.getBindingsCollection(),
            bindingsDestroyed = [];

        bindingsCollection.each(function (binding) {
            if (binding.destroyed) {
                bindingsDestroyed.push(binding);

                return;
            }

            if (binding.stub.name === path &&
                binding.callback === callback &&
                binding.scope === scope
            ) {
                binding.destroy();
                bindingsDestroyed.push(binding);
            }
        }, this);

        if (bindingsDestroyed.length) {
            bindingsCollection.remove(bindingsDestroyed);
        }
    },

    /**
     * @returns {Ext.util.Collection}
     * @private
     */
    getBindingsCollection: function () {
        if (!this.bindingsCollection) {
            this.bindingsCollection = new Ext.util.Collection();
        }

        return this.bindingsCollection;
    }

});
