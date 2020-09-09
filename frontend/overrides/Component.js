Ext.define('Ext.overrides.Component', {
    override: 'Ext.Component',

    mixins: [
        'Unidata.mixin.ComponentEventBus'
    ],

    unidataLayoutManagerEnabled: false,        // включена / выключен менеджмент многократно вызываемого updateLayout компонентами ExtJS
    unidataLayoutManagerDelayedTask: null,     // откложенная задача для принудительного запуска updateLayout
    unidataLayoutManagerText: null,            // текст для вывода в консоль для отладки
    unidataLayoutCount: 0,                     // счетчик отображающий сколько раз вызывался updateLayout
    unidataMaxLayoutCount: 10,                 // макасимальное значение счетчика после которого принудительно вызывается updateLayout
    unidataLayoutManagerDelay: 50,             // таймаут для вызова updateLayout в миллисекундах

    /**
     * Метод вызывается при добавлении дочерних компонентов на любом уровне иерархии
     *
     * @param {Ext.Component[]} components - компонеты, которые добавились
     */
    componentsAdded: function (components) {
    },

    /**
     * Метод вызывается при добавлении данного компонента в иерархию компонентов,
     * даже если это произошло где-то высоко в иерархии
     *
     * @param rootOwnerCt - рутовый компонент в иерархии
     */
    componentAddedToHierarchy: function (rootOwnerCt) {
    },

    /**
     * Оповещает компонент и все родительские компонены, о том, что добавлены новые дочерние компоненты
     *
     * @param {Ext.Component[]} components
     */
    notifyComponentsAdded: function (components) {
        this.componentsAdded(components);

        if (this.ownerCt) {
            this.ownerCt.notifyComponentsAdded(components);
        } else {
            Ext.Array.each(components, function (component) {
                component.componentAddedToHierarchy(this);
            }, this);
        }
    },

    /**
     * Возвращает массив дочерних компонентов (рукурсивно)
     *
     * @param withSelf - включая себя
     * @returns {Ext.Component[]}
     */
    getChildComponents: function (withSelf) {
        var components = [];

        if (withSelf) {
            components.push(this);
        }

        if (this.items && this.items.each) {
            this.items.each(function (item) {
                if (item instanceof Ext.Component) {
                    components = components.concat(item.getChildComponents(true));
                }
            });
        }

        return components;
    },

    /**
     * При добавлении компоента, оповещаем все его родительские компоненты о том, что добавились новые
     *
     * @param parentComponent
     */
    onAdded: function (parentComponent) {
        this.callParent(arguments);

        parentComponent.notifyComponentsAdded(this.getChildComponents(true));
    },

    initComponent: function () {
        this.callParent(arguments);

        this.unidataLayoutManagerDelayedTask = Ext.create('Ext.util.DelayedTask');

        /**
         * BUG EXTJS-16180
         *
         * Исправляем косячное поведение элементов. После setDisabled(true)/setDisabled(false) для некоторых элементов
         * остается класс x-mask поэтому они выглядят как неактивные, но доступны для взаимодействия с пользователем
         *
         * Смотри подробности:
         * https://www.sencha.com/forum/showthread.php?295910-5.1.0.107-setDisabled%28true%29-on-formpanel-doesn-t-enable-buttons
         */

        this.on('enable', function (component) {
            if (component.isMasked()) {
                component.unmask();
            }
        });
        // end fix EXTJS-16180

        this.on('beforerender', this.onBeforeRenderSecuredResource, this, {single: true});
    },

    constructor: function () {
        // производим проброс методов
        this.processMethodMapper();

        // сгенерировать getters/setters для переменных config'а,
        // значения которых хранятся во viewModel (для последующего биндинга)
        this.generateViewModelAccessors();

        this.callParent(arguments);
    },

    /**
     * Переопределяем родной метод updateLayout для реализации дополнительной логики лейатута.
     * Нет необходимости подряд выполнять многократный вызов updateLayout т.к. сенча начинает неимоверно тупить
     */
    updateLayout: function () {
        if (this.rendered && this.unidataLayoutManagerEnabled && this.unidataLayoutManagerDelayedTask) {
            this.unidataLayoutCount++;

            if (this.unidataLayoutCount < this.unidataMaxLayoutCount) {
                this.unidataLayoutManagerDelayedTask.cancel();
                this.unidataLayoutManagerDelayedTask.delay(this.unidataLayoutManagerDelay, this.forceUpdateLayoutManager, this);

                return;
            }
        }

        this.callParent(arguments);

        this.unidataLayoutCount = 0;

        if (this.unidataLayoutManagerText) {
            console.log(this.unidataLayoutManagerText);
        }
    },

    /**
     * Принудительно запускает updateLayout минуя unidataLayoutManagerEnabled
     */
    forceUpdateLayoutManager: function () {
        this.unidataLayoutCount = this.unidataMaxLayoutCount + 1;
        this.updateLayout();
    },

    /**
     * Производит определение пробрасываемых методов
     *
     * Маппер определяется:
     *
     * methodMapper: [
     *     {
     *         method: 'updateReadOnly',    <-- имя метода который будет проброшен
     *         map: 'updateReadOnly',       <-- имя метода который будет вызываться
     *         scope: 'controller'          <-- область в которую пробрасывается controller или model
     *     }
     * ]
     *
     * Проброс не осуществляется если метод определен явно в классе
     * Если не указан метод к которому пробрасывать считаем что он совпадает с именем пробрасываемого метода
     * Если не указана область для проброса, считается что проброс осуществляется в контроллер
     */

    processMethodMapper: function () {
        var methodMapper = this.methodMapper,
            prototype;

        // супер осторожная проверка
        if (!methodMapper || !this.self || !this.self.prototype) {
            return;
        }

        prototype = this.self.prototype;

        Ext.Array.each(methodMapper, function (mapper) {
            var method = mapper.method,
                map    = method,
                scope  = mapper.scope,
                functionBody;

            if (mapper.map) {
                map = mapper.map;
            }

            // если определен метод то не переопределяем его
            if (prototype.hasOwnProperty(method)) {
                return;
            }

            functionBody = 'var scope = this.getController(); return scope.' + map + '.apply(scope, arguments);';

            if (scope === 'model') {
                functionBody = 'var scope = this.getViewModel(); return scope.' + map + '.apply(scope, arguments);';
            }

            prototype[method] = new Function(functionBody);
        });
    },

    /**
     * Генерация getters/setters для переменных config'а,
     * значения которых хранятся во viewModel (для последующего биндинга)
     *
     * Пример:
     * Переменные должны быть определены во view config, а также в секции data или секции formulas viewModel.
     *
     * view (panel, container, etc)
     * config: {
     *    status: null,
     *    mergeDataRecordCount: null
     * }
     *
     * viewModel {
     *    data: {
     *        status: null,
     *        mergeDataRecordCount: null
     *    }
     * }
     *
     * Имена переменных в секции viewModelAccessors задаются одним из двух способов:
     *
     * 1. Массив строк:
     * viewModelAccessors: ['status', 'mergeDataRecordCount'],
     *
     * 2. Массив объектов:
     * viewModelAccessors: [
     *     {
     *         name: 'status'
     *     },
     *     {
     *         name: 'mergeDataRecordCount'
     *     }
     * ]
     *
     * В соответствии с настройками будут сгенерированы методы:
     * getStatus, setStatus, getMergeDataRecordCount, setMergeDataRecordCount
     *
     * Внутри set* методов вызываются template-методы update*, apply*
     *
     * Подробнее:
     * http://docs.sencha.com/extjs/5.1.0/guides/core_concepts/classes.html#core_concepts-_-classes_-_configuration
     * http://moduscreate.com/a-dive-into-the-sencha-class-config-system/
     *
     * Теперь значения переменных хранятся во viewModel и доступны для binding
     */
    generateViewModelAccessors: function () {
        var viewModelAccessors = this.viewModelAccessors,
            funcs = {},
            prototype;

        // супер осторожная проверка
        if (!viewModelAccessors ||
            !Ext.isArray(viewModelAccessors) ||
            !this.self ||
            !this.self.prototype) {
            return;
        }

        prototype = this.self.prototype;

        // сгенерировать getters/setters
        funcs = Ext.Array.map(viewModelAccessors, this.generateViewModelAccessor, this);

        // вставляем getters/setters, как свойства данного объекта
        Ext.Array.forEach(funcs, function (funcObj) {
            Ext.Object.each(funcObj, function (fnName, fn) {
                prototype[fnName] = fn;
            });
        }, this);
    },

    /**
     * Генерация getters/setters для переменных config'а для конкретной переменной
     *
     * @private
     * @param viewModelAccessor {Object|string}
     *
     * {string} viewModelAccessor - имя переменной во view config и во viewModel
     *
     * {Object} viewModelAccessor:
     * name - имя переменной во view config и во viewModel
     *
     * @returns {Object}
     *
     * {Object} funcs:
     * get* - getter
     * set* - setter
     */
    generateViewModelAccessor: function (viewModelAccessor) {
        var viewModel = this.getViewModel(),
            name,
            funcs = {},
            getterFnName,
            setterFnName,
            getterFn,
            setterFn;

        if (Ext.isString(viewModelAccessor) && viewModelAccessor) {
            name = viewModelAccessor;
        } else if (Ext.isObject(viewModelAccessor) &&
            viewModelAccessor.hasOwnProperty('name') &&
            viewModelAccessor.name) {
            name = viewModelAccessor.name;
        } else {
            Ext.Error.raise('One of viewModel accessors config is incorrect');
        }

        setterFnName = this.buildViewModelAccessorName(name, 'set');
        setterFn = this.buildViewModelSetterFn(name);
        getterFnName = this.buildViewModelAccessorName(name, 'get');
        getterFn = this.buildViewModelGetterFn(name);

        funcs[setterFnName] = setterFn;
        funcs[getterFnName] = getterFn;

        return funcs;
    },

    /**
     * Генерирует имя для accessor-метода (get, set, update, apply)
     *
     * @private
     * @param name - имя переменной во view config и во viewModel
     * @param type - тип метода (get, set, update, apply)
     * @returns {string} имя функции
     */
    buildViewModelAccessorName: function (name, type) {
        var fnName;

        if (type !== 'get' && type !== 'set' && type !== 'update' && type !== 'apply') {
            Ext.Error.raise('Incorrect accessor type');
        }

        fnName = type + Ext.String.capitalize(name);

        return fnName;
    },

    /**
     * Генерация сеттер-метода
     *
     * @private
     */
    buildViewModelSetterFn: function (name) {
        var setterFn,
            applyFnName,
            updateFnName,
            prototype;

        applyFnName = this.buildViewModelAccessorName(name, 'apply');
        updateFnName = this.buildViewModelAccessorName(name, 'update');

        prototype = this.self.prototype;

        setterFn = function (value) {
            var viewModel = this.getViewModel(),
                oldValue;

            oldValue = viewModel.get(name);

            if (Ext.isFunction(prototype[applyFnName])) {
                value = prototype[applyFnName].apply(this, [value, oldValue]);
            }
            viewModel.set(name, value);

            if (Ext.isFunction(prototype[updateFnName])) {
                prototype[updateFnName].apply(this, [value, oldValue]);
            }
        };

        return setterFn;
    },

    /**
     * Генерация геттер-метода
     *
     * @private
     */
    buildViewModelGetterFn: function (name) {
        var getterFn;

        getterFn = function () {
            var viewModel = this.getViewModel();

            return viewModel ? viewModel.get(name) : null;
        };

        return getterFn;
    },

    onBeforeRenderSecuredResource: function (component) {
        var bindCfg;

        if (!Unidata.Config.getRole()) {
            return;
        }

        function getRole (resource) {
            var findData;

            Unidata.Config.getRole().forEach(function (right) {
                if (right.securedResource.name === resource) {
                    findData = right;
                }
            });

            return findData;
        }

        if (component.securedResource) {
            component.setSecuredResource = function (resource) {
                //если пользователь администратор - то разрешено все
                if (Unidata.Config.isUserAdmin()) {
                    return;
                }

                var role = getRole(resource);

                if (!role) {
                    component.setHidden(true);

                    return;
                }

                if (component.securedEvent === 'create') {
                    if (role.create !== true) {
                        component.setHidden(true);
                    } else {
                        component.setHidden(false);
                    }
                } else if (component.securedEvent === 'read') {
                    if (role.read !== true) {
                        component.setHidden(true);
                    } else {
                        component.setHidden(false);
                    }
                } else if (component.securedEvent === 'update') {
                    if (role.update !== true) {
                        component.setHidden(true);
                    } else {
                        component.setHidden(false);
                    }
                } else if (component.securedEvent === 'write') {
                    if (role.create !== true && role.update !== true) {
                        component.setDisabled(true);
                    } else {
                        component.setDisabled(false);
                    }
                } else if (component.securedEvent === 'delete') {
                    if (role['delete'] !== true) {
                        component.setHidden(true);
                    } else {
                        component.setHidden(false);
                    }
                } else if ((component.securedEvent === 'full')) {
                    if (role['create'] !== true ||
                        role['read'] !== true ||
                        role['update'] !== true ||
                        role['delete'] !== true) {

                        component.setHidden(true);
                    } else {
                        component.setHidden(false);
                    }
                }
            };
            bindCfg = {
                securedResource: component.securedResource
            };
            Ext.apply(bindCfg, component.config.bind);
            component.setBind(bindCfg);
        }
    },

    map: {},
    getTemplate: function (url, callback) {
        var me = this;

        if (this.map[url] === undefined) {
            Ext.Ajax.request({
                url: 'app/templates/' + url,
                success: function (xhr) {
                    var template = new Ext.XTemplate(xhr.responseText);

                    me.map[url] = template;
                    callback(template);
                }
            });
        } else {
            callback(this.map[url]);
        }
    }
});
