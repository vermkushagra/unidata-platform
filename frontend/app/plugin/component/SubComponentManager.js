/**
 *
 * Компонент для DataEntity
 *
 * @author Ivan Marshalkin
 * @date 2016-02-04
 */

/**
 * Пример использования № 1
 *
 * plugin.addComponent(someComponent, 'someFormulaName');
 *
 * Формула someFormulaName возвращает объект
 *
 * {
 *     hidden: true,
 *     disabled: true
 * }
 *
 * Т.е. компонент скрывается и дизаблится
 *
 *
 *
 * Пример использования № 2
 *
 * options = {
 *     setter: function (value, component) {...},
 *     scope: someScope
 * };
 *
 * Параметры setter
 *      value     - значение возвращаемое формулой
 *      component - компонет которым управляют
 *
 * plugin.addComponent(someComponent, 'someFormulaName', options);
 *
 * Формула someFormulaName возвращает объект
 * {
 *     hidden: true,
 *     disabled: true,
 *     shadow: true
 * }
 *
 * Т.е. обработчик options.setter берет на себя обработку управления состоянием компонента someComponent
 *
 */

Ext.define('Unidata.plugin.component.SubComponentManager', {
    extend: 'Ext.AbstractPlugin',

    alias: 'plugin.subcomponentmanager',

    valuePropPrefix: 'value-',
    setterPrefix: 'setValue-',
    getterPrefix: 'getValue-',

    componentMap: null,
    holderComponent: null,

    init: function () {
        if (!this.componentMap) {
            this.componentMap = Ext.create('Ext.util.HashMap');
        }

        this.holderComponent = Ext.create('Ext.Component', {
            hidden: true
        });
        this.holderComponent.values = {};

        this.getCmp().add(this.holderComponent);
    },

    destroy: function () {
        var component       = this.getCmp(),
            holderComponent = this.holderComponent;

        if (component && holderComponent) {
            component.remove(holderComponent);
        }
    },

    /**
     * Добавляет в компонент-держатель свойств забиндиное свойсво на формулу для управлением состоянием component
     *
     * options = {
     *     setter: function (value) {}, // функция реализует обработку возвращаемого формулой значения
     *     scope: scope                 // контекст с которым вызывается setter (если не задан вызывается
     *                                  // с контекстом компонента к которому прикручен плагин)
     * }
     *
     * @param component   - компонент состоянием которого мы будем управлять
     * @param formulaName - имя формулы на которую будем делать bind
     * @param options     - дополнительные опции
     */
    addComponent: function (component, formulaName, options) {
        var componentId = component.getId(),
            mapObject;

        options = options || {};

        if (options.setter && !options.scope) {
            options.scope = this.getCmp();
        }

        mapObject = {
            component: component,
            formula: formulaName,
            options: options
        };

        if (this.componentMap.containsKey(componentId)) {
            this.removeComponent(component);
            this.componentMap.removeAtKey(componentId);
        }

        this.componentMap.add(componentId, mapObject);

        this.generateGetter(component);
        this.generateSetter(component);
        this.generateBind(component, formulaName);
    },

    /**
     * Удаляет в компонент-держатель свойств забиндиное свойсво на формулу для управлением состоянием component
     *
     * @param component
     */
    removeComponent: function (component) {
        this.removeGetter(component);
        this.removeSetter(component);
        this.removeBind(component);
    },

    /**
     * Возращает имя свойства к которому мы будет делать bind для компонента-держателя свойств
     *
     * @param component
     * @returns {string}
     */
    getValuePropName: function (component) {
        return this.valuePropPrefix + component.getId();
    },

    /**
     * Возвращает имя для функции сеттера
     *
     * @param component
     * @returns {string}
     */
    getSetterName: function (component) {
        return this.setterPrefix + component.getId();
    },

    /**
     * Возвращает имя для функции геттера
     *
     * @param component
     * @returns {string}
     */
    getGetterName: function (component) {
        return this.getterPrefix + component.getId();
    },

    /**
     * Создает в компоненте-держателе свойств сеттер
     *
     * @param component
     */
    generateSetter: function (component) {
        var plugin          = this,
            holderComponent = this.holderComponent,
            componentId     = component.getId(),
            setterFnName    = this.getSetterName(component),
            valuePropName   = this.getValuePropName(component);

        /**
         * В качестве параметра принимает объект
         * {
         *     hidden: true | false,      // скрыт / отображен компонент
         *     disabled: true | false     // активен / неактивен компонент
         * }
         *
         * Если при добавлении указывали кастомный обработчик то value возвращаемое формулой может быть в том формате,
         * который ожидает реализованный обработчик
         *
         * @param value - Object
         */
        holderComponent[setterFnName] = function (value) {
            var component = Ext.getCmp(componentId),
                mapObject = plugin.componentMap.get(componentId),
                options   = mapObject.options;

            // при добавлении указали объект options - вызываем кастомный обработчик
            if (options && options.setter) {
                options.setter.call(options.scope, value, component);
            // иначе берем обработку на себя
            } else {
                if (component) {
                    component.setHidden(value.hidden);
                    component.setDisabled(value.disabled);
                }
            }

            holderComponent.values[valuePropName] = value;
        };
    },

    /**
     * Удаляет в компоненте-держателе свойств сеттер, по факту заменяет пустой функцией
     *
     * @param component
     */
    removeSetter: function (component) {
        var holderComponent = this.holderComponent,
            setterFnName    = this.getSetterName(component);

        if (holderComponent.hasOwnProperty(setterFnName)) {
            holderComponent[setterFnName] = Ext.emptyFn;
        }
    },

    /**
     * Создает в компоненте-держателе свойств геттер
     *
     * @param component
     */
    generateGetter: function (component) {
        var holderComponent = this.holderComponent,
            getterFnName    = this.getGetterName(component),
            valuePropName   = this.getValuePropName(component);

        holderComponent[getterFnName] = function () {
            return holderComponent.values[valuePropName];
        };
    },

    /**
     * Удаляет в компоненте-держателе свойств геттер, по факту заменяет пустой функцией
     *
     * @param component
     */
    removeGetter: function (component) {
        var holderComponent = this.holderComponent,
            getterFnName    = this.getGetterName(component);

        if (holderComponent.hasOwnProperty(getterFnName)) {
            holderComponent[getterFnName] = Ext.emptyFn;
        }
    },

    /**
     * Создает в компоненте-держателе свойств забиндиное свойство
     *
     * @param component
     */
    generateBind: function (component, formulaName) {
        var holderComponent = this.holderComponent,
            valuePropName   = this.getValuePropName(component),
            bindCfg         = {};

        bindCfg[valuePropName] = '{' + formulaName + '}';

        holderComponent.setBind(bindCfg);
    },

    /**
     * Удаляет в компоненте-держателе свойств забиндиное свойство
     *
     * @param component
     */
    removeBind: function (component) {
        var holderComponent = this.holderComponent,
            valuePropName   = this.getValuePropName(component),
            bindCfg         = {};

        bindCfg[valuePropName] = null;

        holderComponent.setBind(bindCfg);
    }
});
